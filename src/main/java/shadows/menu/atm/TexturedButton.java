package shadows.menu.atm;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class TexturedButton extends Button {

	protected ResourceLocation texture;
	protected int texX, texY;

	public TexturedButton(int xPos, int yPos, int width, int height, String displayString, IPressable handler) {
		super(xPos, yPos, width, height, displayString, handler);
		setTexture(WIDGETS_LOCATION);
	}

	public TexturedButton setTexture(ResourceLocation texture, int x, int y) {
		this.texture = texture;
		this.texX = x;
		this.texY = y;
		return this;
	}

	public TexturedButton setTexture(ResourceLocation texture) {
		return setTexture(texture, 0, 0);
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void renderButton(int mouseX, int mouseY, float partial) {
		if (this.visible) {
			Minecraft mc = Minecraft.getInstance();
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.getTextureManager().bindTexture(this.texture);
			RenderSystem.disableDepthTest();
			int i = 0;
			if (this.isHovered()) {
				i += 30;
			}
			blit(this.x, this.y, 0, i, this.width, this.height, 120, 60);
			RenderSystem.enableDepthTest();
			int color = 0;

			if (packedFGColor == 43534) {
				color = packedFGColor;
			} else if (!this.active) {
				color = 0x00AAFF;
			} else if (this.isHovered) {
				color = 0x116666;
			}

			String buttonText = this.getMessage();
			int strWidth = mc.fontRenderer.getStringWidth(buttonText);
			int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

			if (strWidth > width - 6 && strWidth > ellipsisWidth) buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

			this.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2 + 5, this.y + (this.height - 12) / 2, color);
		}
	}

	@Override
	public void drawCenteredString(FontRenderer font, String string, int x, int y, int color) {
		font.drawString(string, x - font.getStringWidth(string) / 2, y, color);
	}
}