package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

public interface IBreedingTrackerManager {

	void registerTracker(String rootUID, IBreedingTrackerHandler handler);

	<T extends IBreedingTracker> T getTracker(String rootUID, @Nullable World world, @Nullable GameProfile profile);
}
