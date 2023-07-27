package dev.shadowsoffire.packmenu.logo;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.Strings;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.shadowsoffire.packmenu.ExtendedMenuScreen;
import dev.shadowsoffire.packmenu.buttons.AnchorPoint;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

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

    public void draw(ExtendedMenuScreen screen, GuiGraphics gfx) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        gfx.pose().pushPose();
        gfx.pose().translate(this.anchor.getX(screen), this.anchor.getY(screen), 0);
        gfx.pose().scale((float) this.width / this.texWidth, (float) this.height / this.texHeight, 1);
        gfx.blit(this.texture, this.xOff, this.yOff, 0, 0, this.texWidth, this.texHeight, this.texWidth, this.texHeight);
        gfx.pose().popPose();
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
        if (!cfg.getBoolean("Enable Logo", "logo", true, "If the logo is enabled or not.")) return null;
        if (Strings.isBlank(tex)) return null;
        return new Logo(xOff, yOff, width, height, texWidth, texHeight, new ResourceLocation(tex), anchor);
    }

}
