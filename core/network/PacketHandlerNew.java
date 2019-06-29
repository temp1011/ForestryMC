package forestry.core.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketActiveUpdate;

public class PacketHandlerNew {

	public static final Logger LOGGER = LogManager.getLogger();
	//TODO - may need to wrap this in constructor, we'll see what happens with classloading
	public static final String channelId = "FOR";	//TODO - change to 1 or similar...
	public static final SimpleChannel channel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(Constants.MOD_ID, "channel"))
			.clientAcceptedVersions(s -> s.equals(channelId))
			.serverAcceptedVersions(s -> s.equals(channelId))
			.networkProtocolVersion(() -> channelId)
			.simpleChannel();

	public static void register() {
		int index = 0;
		channel.registerMessage(index++, PacketActiveUpdate.class, )
	}
}
