package forestry.core.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.api.gui.IGuiElement;
import forestry.api.gui.events.GuiEvent;
import forestry.api.gui.events.GuiEventDestination;
import forestry.core.gui.elements.Window;

/**
 * GuiScreen implementation of a gui that contains {@link forestry.api.gui.IGuiElement}s.
 */
public class GuiWindow extends Screen implements IGuiSizable {
	protected final Window window;
	protected final int xSize;
	protected final int ySize;
	protected int guiLeft;
	protected int guiTop;

	public GuiWindow(int xSize, int ySize, ITextComponent title) {
		super(title);
		this.xSize = xSize;
		this.ySize = ySize;
		this.window = new Window<>(xSize, ySize, this);
		addElements();
	}

	protected void addElements() {
	}

	//TODO right method?
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		window.updateClient();
	}

	//TODO - right method?
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		window.setMousePosition(mouseX, mouseY);
		super.render(mouseX, mouseY, partialTicks);
		window.draw(mouseX, mouseY);
	}

	protected void drawTooltips(int mouseX, int mouseY) {
		PlayerInventory playerInv = minecraft.player.inventory;

		if (playerInv.getItemStack().isEmpty()) {
			GuiUtil.drawToolTips(this, buttons, mouseX, mouseY);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(guiLeft, guiTop, 0.0F);
			window.drawTooltip(mouseX, mouseY);
			GlStateManager.popMatrix();
		}
	}

	//TODO check right method
	@Override
	public void init() {
		super.init();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		window.init(guiLeft, guiTop);
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		window.setSize(width, height);
		super.setWorldAndResolution(mc, width, height);
	}

	@Override
//	protected void keyTyped(char typedChar, int keyCode) {
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {	//TODO resolve this method
		if (keyCode == 1) {
			this.minecraft.displayGuiScreen(null);

			if (this.minecraft.currentScreen == null) {
				this.minecraft.setIngameFocus();
			}
		}
		IGuiElement origin = (window.getFocusedElement() == null) ? this.window : this.window.getFocusedElement();
		window.postEvent(new GuiEvent.KeyEvent(origin, typedChar, keyCode), GuiEventDestination.ALL);
	}

	//TODO onMouseClicked
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
		window.postEvent(new GuiEvent.DownEvent(origin, mouseX, mouseY, mouseButton), GuiEventDestination.ALL);
	}

	//TODO onMouseRelease
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
		window.postEvent(new GuiEvent.UpEvent(origin, mouseX, mouseY, state), GuiEventDestination.ALL);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int dWheel = Mouse.getDWheel();
		if (dWheel != 0) {
			window.postEvent(new GuiEvent.WheelEvent(window, dWheel), GuiEventDestination.ALL);
		}
	}

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public int getSizeX() {
		return xSize;
	}

	@Override
	public int getSizeY() {
		return ySize;
	}

	@Override
	public Minecraft getMC() {
		return minecraft;
	}

}
