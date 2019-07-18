package forestry.core.multiblock;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent;

@OnlyIn(Dist.CLIENT)
public class MultiblockClientTickHandler {

	//TODO - register event handler
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			MultiblockRegistry.tickStart(Minecraft.getInstance().world);
		}
	}
}
