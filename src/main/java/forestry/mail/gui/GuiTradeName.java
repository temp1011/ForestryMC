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
package forestry.mail.gui;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;
import forestry.mail.network.packets.PacketTraderAddressRequest;
import forestry.mail.tiles.TileTrader;

import org.lwjgl.glfw.GLFW;

public class GuiTradeName extends GuiForestry<ContainerTradeName> {
	private final TileTrader tile;
	private TextFieldWidget addressNameField;

	public GuiTradeName(ContainerTradeName container, PlayerInventory inv, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/tradername.png", container, inv, title);
		this.tile = container.getTile();
		this.xSize = 176;
		this.ySize = 90;

		addressNameField = new TextFieldWidget(this.minecraft.fontRenderer, guiLeft + 44, guiTop + 39, 90, 14, "");
	}

	@Override
	public void init() {
		super.init();

		addressNameField = new TextFieldWidget(this.minecraft.fontRenderer, guiLeft + 44, guiTop + 39, 90, 14, "");
		addressNameField.setText(container.getAddress().getName());
		addressNameField.focused = true;
	}

	@Override
	public boolean keyPressed(int eventCharacter, int eventKey, int int3) {

		// Set focus or enter text into address
		if (addressNameField.isFocused()) {
			if (eventKey == GLFW.GLFW_KEY_ENTER) {
				setAddress();
			} else {
				addressNameField.keyPressed(eventCharacter, eventKey, int3);
			}
			return true;
		}

		return super.keyPressed(eventCharacter, eventKey, int3);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if(super.mouseClicked(mouseX, mouseY, mouseButton)) {
			return false;	//TODO this return value
		}
		addressNameField.mouseClicked(mouseX, mouseY, mouseButton);
		return true;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);

		String prompt = Translator.translateToLocal("for.gui.mail.nametrader");
		textLayout.startPage();
		textLayout.newLine();
		textLayout.drawCenteredLine(prompt, 0, ColourProperties.INSTANCE.get("gui.mail.text"));
		textLayout.endPage();
		addressNameField.render(var2, var3, var1);	//TODO correct?
	}

	@Override
	public void onClose() {
		super.onClose();
		setAddress();
	}

	private void setAddress() {
		String address = addressNameField.getText();
		if (StringUtils.isNotBlank(address)) {
			PacketTraderAddressRequest packet = new PacketTraderAddressRequest(tile, address);
			NetworkUtil.sendToServer(packet);
		}
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
	}
}
