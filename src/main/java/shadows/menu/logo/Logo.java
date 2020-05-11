package shadows.menu.logo;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.Strings;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import shadows.menu.ExtendedMenuScreen;
import shadows.menu.buttons.AnchorPoint;
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

	/**
	 * The source anchor for the logo.
	 */
	protected final AnchorPoint anchor;

	public Logo(int xOff, int yOff, int width, int height, int texWidth, int texHeight, ResourceLocation texture, AnchorPoint anchor) {
		this.xOff = xOff;
		this.yOff = yOff;
		this.width = width;
		this.height = height;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
		this.texture = texture;
		this.anchor = anchor;
	}

	public void draw(ExtendedMenuScreen screen) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(this.texture);
		GlStateManager.disableDepthTest();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(anchor.getX(screen), anchor.getY(screen), 0);
		GlStateManager.scaled((double) width / texWidth, (double) height / texHeight, 1);
		Screen.blit(xOff, yOff, 0, 0, texWidth, texHeight, texWidth, texHeight);
		GlStateManager.popMatrix();
		GlStateManager.enableDepthTest();
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
		AnchorPoint anchor = AnchorPoint.valueOf(cfg.getString("Anchor Point", "logo", "DEFAULT_LOGO", "The anchor point of the logo.  The types of anchor points are available on the wiki."));
		if (Strings.isBlank(tex)) return null;
		return new Logo(xOff, yOff, width, height, texWidth, texHeight, new ResourceLocation(tex), anchor);
	}

}
