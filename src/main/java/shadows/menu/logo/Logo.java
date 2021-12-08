package shadows.menu.logo;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.Strings;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import shadows.menu.ExtendedMenuScreen;
import shadows.menu.buttons.AnchorPoint;

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

	@SuppressWarnings("deprecation")
	public void draw(ExtendedMenuScreen screen, PoseStack stack) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
		stack.pushPose();
		stack.translate(anchor.getX(screen), anchor.getY(screen), 0);
		stack.scale((float) width / texWidth, (float) height / texHeight, 1);
		Screen.blit(stack, xOff, yOff, 0, 0, texWidth, texHeight, texWidth, texHeight);
		stack.popPose();
		RenderSystem.enableDepthTest();
	}

	@Nullable
	public static Logo read(Configuration cfg) {
		String tex = cfg.getString("Texture Path", "logo", "packmenu:textures/gui/logo.png", "The location of the logo texture.  Must be a png file.  Should contain the extension.");
		int xOff = cfg.getInt("X Offset", "logo", -650, -500000, 500000, "The X offset of the logo.");
		int yOff = cfg.getInt("Y Offset", "logo", 0, -500000, 500000, "The Y offset of the logo.");
		int width = cfg.getInt("Width", "logo", 100, 0, 500000, "The width of the logo.");
		int height = cfg.getInt("Height", "logo", 100, 0, 500000, "The height of the logo.");
		int texWidth = cfg.getInt("Texture Width", "logo", 300, 0, 500000, "The width of the logo's texture.");
		int texHeight = cfg.getInt("Texture Height", "logo", 300, 0, 500000, "The height of the logo's texture.");
		AnchorPoint anchor = AnchorPoint.valueOf(cfg.getString("Anchor Point", "logo", "DEFAULT_LOGO", "The anchor point of the logo.  The types of anchor points are available on the wiki."));
		if (Strings.isBlank(tex)) return null;
		return new Logo(xOff, yOff, width, height, texWidth, texHeight, new ResourceLocation(tex), anchor);
	}

}
