/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.network;

import com.google.common.base.Preconditions;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Constants;
import forestry.core.utils.Log;

//TODO - can we get away with simple channel?
public class PacketHandler {

	public static final Logger LOGGER = LogManager.getLogger();
	//TODO - may need to wrap this in constructor, we'll see what happens with classloading
	public static final String channelId = "FOR";	//TODO - change to 1 or similar...
	public static final EventNetworkChannel channel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(Constants.MOD_ID, "channel"))
			.clientAcceptedVersions(s -> s.equals("1"))
			.serverAcceptedVersions(s -> s.equals("1"))
			.networkProtocolVersion(() -> "1")
			.eventNetworkChannel();
	static {
		channel.addListener(e -> onServerCustomPayload(e));
		channel.addListener(e -> onClientCustomPayload(e));
	}

	public PacketHandler() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelId);
		channel.register(this);
	}

	public static void onServerCustomPayload(NetworkEvent.ServerCustomPayloadEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPayload());
		ServerPlayerEntity player = event.getSource().get().getSender();
		if(player == null) {
			LOGGER.warn("player was null", new Exception());	//TODO - debug
			return;
		}

		byte packetIdOrdinal = data.readByte();
		PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
		IForestryPacketHandlerServer packetHandler = packetId.getPacketHandler();
		checkThreadAndEnqueue(packetHandler, data, player, player.getServerWorld());
	}

	public void onClientCustomPayload(NetworkEvent.ClientCustomPayloadEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPayload());

		byte packetIdOrdinal = data.readByte();
		PacketIdClient packetId = PacketIdClient.VALUES[packetIdOrdinal];
		IForestryPacketHandlerClient packetHandler = packetId.getPacketHandler();
		checkThreadAndEnqueue(packetHandler, data, Minecraft.getInstance());
	}

	public void sendPacket(FMLProxyPacket packet, ServerPlayerEntity player) {
		channel.sendTo(packet, player);
	}

	@OnlyIn(Dist.CLIENT)
	private static void checkThreadAndEnqueue(final IForestryPacketHandlerClient packet, final PacketBufferForestry data, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			data.retain();
			threadListener.addScheduledTask(() -> {
				try {
					PlayerEntity player = Minecraft.getInstance().player;
					Preconditions.checkNotNull(player, "Tried to send data to client before the player exists.");
					packet.onPacketData(data, player);
					data.release();
				} catch (IOException e) {
					Log.error("Network Error", e);
				}
			});
		}
	}

	private static void checkThreadAndEnqueue(final IForestryPacketHandlerServer packet, final PacketBufferForestry data, final ServerPlayerEntity player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			data.retain();
			threadListener.addScheduledTask(() -> {
				try {
					packet.onPacketData(data, player);
					data.release();
				} catch (IOException e) {
					Log.error("Network Error", e);
				}
			});
		}
	}
}
