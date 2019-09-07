package forestry.core.fluids;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class DrainOnlyFluidHandlerWrapper implements IFluidHandler {
	private final IFluidHandler internalFluidHandler;

	public DrainOnlyFluidHandlerWrapper(IFluidHandler internalFluidHandler) {
		this.internalFluidHandler = internalFluidHandler;
	}

	@Override
	public int getTanks() {
		return internalFluidHandler.getTanks();
	}

	@Nonnull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return internalFluidHandler.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return internalFluidHandler.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		return internalFluidHandler.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		return internalFluidHandler.drain(maxDrain, doDrain);
	}
}
