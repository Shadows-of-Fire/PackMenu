package shadows.menu.logo;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.Strings;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import shadows.menu.ExtendedMenuScreen;
import shadows.placebo.config.Configuration;

public class Logo {

	/**
	 * Texture path, used for all buttons.
	 */
	protected ResourceLocation texture;

	/**
	 * X and Y offsets.
	 */
	protected final int xOff, yOff, width, height;

	/**
	 * Texture size and coordinates.
	 */
	protected final int texWidth, texHeight;

	public Logo(int xOff, int yOff, int width, int height, int texWidth, int texHeight, ResourceLocation texture) {
		this.xOff = xOff;
		this.yOff = yOff;
		this.width = width;
		this.height = height;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
		this.texture = texture;
	}

	public void draw(ExtendedMenuScreen screen) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(this.texture);
		RenderSystem.disableDepthTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1);
		RenderSystem.pushMatrix();
		RenderSystem.translatef(screen.width / 2, screen.height / 4, 0);
		RenderSystem.scaled((double) width / texWidth, (double) height / texHeight, 1);
		Screen.blit(xOff, yOff, 0, 0, texWidth, texHeight, texWidth, texHeight);
		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
	}

	@Nullable
	public static Logo read(Configuration cfg) {
		String tex = cfg.getString("Texture Path", "logo", "", "The location of the logo texture.  Must be a png file.  Should contain the extension.");
		int xOff = cfg.getInt("X Offset", "logo", 0, -500000, 500000, "The X offset of the logo.");
		int yOff = cfg.getInt("Y Offset", "logo", 0, -500000, 500000, "The Y offset of the logo.");
		int width = cfg.getInt("Width", "logo", 0, 0, 500000, "The width of the logo.");
		int height = cfg.getInt("Height", "logo", 0, 0, 500000, "The height of the logo.");
		int texWidth = cfg.getInt("Texture Width", "logo", 0, 0, 500000, "The width of the logo's texture.");
		int texHeight = cfg.getInt("Texture Height", "logo", 0, 0, 500000, "The height of the logo's texture.");
		if (Strings.isBlank(tex)) return null;
		return new Logo(xOff, yOff, width, height, texWidth, texHeight, new ResourceLocation(tex));
	}

}
