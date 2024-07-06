package dev.shadowsoffire.packmenu.buttons;

import java.util.Optional;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.packmenu.ExtendedMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class JsonButton extends Button {

    public static final Codec<JsonButton> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.INT.fieldOf("x").forGetter(JsonButton::getXPos),
        Codec.INT.fieldOf("y").forGetter(JsonButton::getYPos),
        Codec.intRange(0, Short.MAX_VALUE).fieldOf("width").forGetter(JsonButton::getWidth),
        Codec.intRange(0, Short.MAX_VALUE).fieldOf("height").forGetter(JsonButton::getHeight),
        AnchorPoint.CODEC.optionalFieldOf("anchor", AnchorPoint.DEFAULT).forGetter(JsonButton::getAnchor),
        WidgetSpritesCodec.INSTANCE.optionalFieldOf("sprites", SPRITES).forGetter(JsonButton::getSprites),
        ButtonIcon.CODEC.optionalFieldOf("icon").forGetter(JsonButton::getIcon),
        ButtonAction.CODEC.fieldOf("action").forGetter(JsonButton::getAction),
        ButtonText.CODEC.optionalFieldOf("text", ButtonText.EMPTY).forGetter(JsonButton::getText),
        Codec.BOOL.optionalFieldOf("active", true).forGetter(JsonButton::isActive),
        Codec.FLOAT.optionalFieldOf("scale_x", 1F).forGetter(JsonButton::getScaleX),
        Codec.FLOAT.optionalFieldOf("scale_y", 1F).forGetter(JsonButton::getScaleY))
        .apply(inst, JsonButton::new));

    protected final int xOff, yOff;
    protected final AnchorPoint anchor;
    protected final WidgetSprites sprites;
    protected final Optional<ButtonIcon> icon;
    protected final ButtonAction action;
    protected final ButtonText text;
    protected final float scaleX, scaleY;

    protected int scrollCounter = 0;
    protected Component hoverMessage;

    public JsonButton(int xPos, int yPos, int width, int height, AnchorPoint anchor, WidgetSprites sprites, Optional<ButtonIcon> icon, ButtonAction action, ButtonText text, boolean active, float scaleX, float scaleY) {
        super(xPos, yPos, width, height, Component.translatable(text.key()), action, Button.DEFAULT_NARRATION);
        this.xOff = xPos;
        this.yOff = yPos;
        this.anchor = anchor;
        this.sprites = sprites;
        this.icon = icon;
        this.action = action;
        this.text = text;
        this.active = active;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.hoverMessage = Component.translatable(text.hoverKey());
    }

    public int getXPos() {
        return this.xOff;
    }

    public int getYPos() {
        return this.yOff;
    }

    public AnchorPoint getAnchor() {
        return anchor;
    }

    public WidgetSprites getSprites() {
        return sprites;
    }

    public Optional<ButtonIcon> getIcon() {
        return icon;
    }

    public ButtonAction getAction() {
        return action;
    }

    public ButtonText getText() {
        return text;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public JsonButton setup(ExtendedMenuScreen screen) {
        this.setX(this.xOff + this.anchor.getX(screen));
        this.setY(this.yOff + this.anchor.getY(screen));
        this.setMessage(Component.translatable(this.text.key()));
        this.hoverMessage = Component.translatable(this.text.hoverKey());
        return this;
    }

    @Override
    public Component getMessage() {
        if (this.isHovered) {
            return this.hoverMessage;
        }
        return super.getMessage();
    }

    @Override
    public void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            gfx.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            gfx.pose().pushPose();
            gfx.pose().scale(this.scaleX, this.scaleY, 1);
            gfx.blitSprite(this.sprites.get(this.active, this.isHoveredOrFocused()), Math.round(this.getX() / this.scaleX), Math.round(this.getY() / this.scaleY), Math.round(this.width / this.scaleX),
                Math.round(this.height / this.scaleY));
            if (this.icon.isPresent()) {
                ButtonIcon icon = this.icon.get();
                int iconX = this.getX() + this.getWidth() / 2 - icon.width() / 2;
                int iconY = this.getY() + this.getHeight() / 2 - icon.height() / 2;
                gfx.blitSprite(icon.texture(), Math.round(iconX / this.scaleX), Math.round(iconY / this.scaleY), Math.round(icon.width() / this.scaleX), Math.round(icon.height() / this.scaleY));
            }
            gfx.pose().popPose();
            gfx.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.renderText(gfx, partial);
        }
    }

    protected void drawCenteredString(GuiGraphics stack, Font font, String string, int x, int y, int color) {
        if (this.text.dropShadow()) {
            stack.drawCenteredString(font, string, x, y, color);
        }
        else {
            drawCenteredStringNoShadow(stack, font, string, x, y, color);
        }
    }

    protected void renderText(GuiGraphics stack, float partial) {
        Minecraft mc = Minecraft.getInstance();
        int color = this.getFGColor();
        String buttonText = this.getMessage().getString();
        int strWidth = mc.font.width(buttonText);
        if (strWidth <= this.width - 6) {
            this.drawCenteredString(stack, mc.font, buttonText, this.getX() + this.width / 2 + this.text.xOff(), this.getY() + this.height / 2 + this.text.yOff(), color);
        }
        else if (!this.isHovered) {
            this.scrollCounter = 0;
            int ellipsisWidth = mc.font.width("...");
            if (strWidth > ellipsisWidth) buttonText = this.trimStringToWidth(this.getMessage(), this.width - 6 - ellipsisWidth).getString().trim() + "...";
            this.drawCenteredString(stack, mc.font, buttonText, this.getX() + this.width / 2 + this.text.xOff(), this.getY() + this.height / 2 + this.text.yOff(), color);
        }
        else {
            int halfLen = mc.font.width(buttonText + "      ");
            buttonText += "      " + buttonText;
            stack.pose().pushPose();
            double d0 = mc.getWindow().getGuiScale();
            float y = Minecraft.getInstance().screen.height - this.getY() - this.height;
            RenderSystem.enableScissor((int) (this.getX() * d0), (int) (y * d0), (int) (d0 * this.width), (int) (d0 * this.height));
            stack.pose().translate((-this.scrollCounter - partial) % halfLen, 0, 0);
            stack.drawString(mc.font, buttonText, this.getX() + this.width / 8 + this.text.xOff(), this.getY() + this.height / 2 + this.text.yOff(), color, this.text.dropShadow());
            RenderSystem.disableScissor();
            stack.pose().popPose();
        }
    }

    public void tickScrollCounter() {
        this.scrollCounter++;
    }

    public FormattedText trimStringToWidth(FormattedText str, int width) {
        return Minecraft.getInstance().font.getSplitter().splitLines(str, width, Style.EMPTY).get(0);
    }

    @Override
    public int getFGColor() {
        return !this.isHovered ? this.text.color() : this.text.hoverColor();
    }

    public static void drawCenteredStringNoShadow(GuiGraphics stack, Font font, String string, int x, int y, int color) {
        stack.drawString(font, string, x - font.width(string) / 2, y, color, false);
    }
}
