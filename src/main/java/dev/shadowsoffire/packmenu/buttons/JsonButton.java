package dev.shadowsoffire.packmenu.buttons;

import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.shadowsoffire.packmenu.ExtendedMenuScreen;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class JsonButton extends Button {

    /**
     * Texture path, used for all buttons.
     */
    protected ResourceLocation texture = WIDGETS_LOCATION;

    /**
     * Texture U and V coordinates, used only when using a non-widgets texture.
     */
    protected int u, v, hoverU, hoverV, texWidth, texHeight;

    /**
     * X and Y offsets. Must be retained here since the main offsets are mutable.
     */
    protected final int xOff, yOff;

    /**
     * If the button uses a texture resembling of the widgets texture, and may be drawn with any width/height.
     */
    protected boolean usesWidgets = false;

    /**
     * The untranslated key of this button's text. Cannot be translated at construction time as language hasn't loaded yet.
     */
    protected String langKey, hoverKey;

    /**
     * The color of the text drawn on this button.
     */
    protected final int fontColor, hoverFontColor;

    /**
     * The anchor point of this button.
     */
    protected AnchorPoint anchor = AnchorPoint.DEFAULT;

    /**
     * The offsets for the drawn text.
     */
    protected int textXOff, textYOff;

    /**
     * If the text on this button is drawn with a drop shadow.
     */
    protected boolean dropShadow;

    /**
     * Button scaling factor. Should be something that's reciprocal is an integer.
     */
    protected float scaleX = 1F, scaleY = 1F;

    /**
     * Ticked variable that allows for larger-than-width strings to rotate.
     */
    protected int scrollCounter = 0;

    /**
     * The translated message instace for hover text.
     */
    protected Component hoverMessage;

    public JsonButton(int xPos, int yPos, int width, int height, int fontColor, int hoverFontColor, String langKey, ActionInstance handler) {
        super(xPos, yPos, width, height, Component.translatable(langKey), handler, Button.DEFAULT_NARRATION);
        handler.setSource(this);
        this.xOff = xPos;
        this.yOff = yPos;
        this.langKey = langKey;
        this.fontColor = fontColor;
        this.hoverFontColor = hoverFontColor;
    }

    public JsonButton texture(ResourceLocation texture, int u, int v, int hoverU, int hoverV, int texWidth, int texHeight) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.hoverU = hoverU;
        this.hoverV = hoverV;
        this.texHeight = texHeight;
        this.texWidth = texWidth;
        return this;
    }

    public JsonButton anchor(AnchorPoint anchor) {
        this.anchor = anchor;
        return this;
    }

    public JsonButton usesWidgets(boolean widgets) {
        this.usesWidgets = widgets;
        return this;
    }

    public JsonButton textOffsets(int x, int y) {
        this.textXOff = x;
        this.textYOff = y;
        return this;
    }

    public JsonButton setup(ExtendedMenuScreen screen) {
        this.setX(this.xOff + this.anchor.getX(screen));
        this.setY(this.yOff + this.anchor.getY(screen));
        this.setMessage(Component.translatable(this.langKey));
        this.hoverMessage = Component.translatable(this.hoverKey);
        return this;
    }

    public JsonButton dropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    public JsonButton scale(float x, float y) {
        this.scaleX = x;
        this.scaleY = y;
        return this;
    }

    public JsonButton hoverText(String hoverKey) {
        if (Strings.isNullOrEmpty(hoverKey)) {
            this.hoverKey = this.langKey;
        }
        else this.hoverKey = hoverKey;
        return this;
    }

    @Override
    public Component getMessage() {
        if (this.isHovered) return this.hoverMessage;
        return super.getMessage();
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderWidget(GuiGraphics stack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            if (this.usesWidgets) this.renderWidgetButton(stack, mouseX, mouseY, partial);
            else this.renderImageButton(stack, mouseX, mouseY, partial);
        }
    }

    public static void drawCenteredStringNoShadow(GuiGraphics stack, Font font, String string, int x, int y, int color) {
        stack.drawString(font, string, x - font.width(string) / 2, y, color, false);
    }

    protected void drawCenteredString(GuiGraphics stack, Font font, String string, int x, int y, int color) {
        if (this.dropShadow) stack.drawCenteredString(font, string, x, y, color);
        else drawCenteredStringNoShadow(stack, font, string, x, y, color);
    }

    protected int getVIndex(boolean pIsHovered) {
        int i = 1;
        if (!this.active) {
            i = 0;
        }
        else if (pIsHovered) {
            i = 2;
        }

        return i;
    }

    /**
     * Renders this button as if it was a default button based on the widgets texture (automatic scaling)
     */
    private void renderWidgetButton(GuiGraphics stack, int mouseX, int mouseY, float partial) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getVIndex(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        stack.blit(this.texture, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
        stack.blit(this.texture, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderText(stack);
    }

    /**
     * Renders this button as if it was an image button.
     */
    private void renderImageButton(GuiGraphics stack, int mouseX, int mouseY, float partial) {
        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.disableDepthTest();
        int x = this.u, y = this.v;
        if (this.isHoveredOrFocused()) {
            x = this.hoverU;
            y = this.hoverV;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        stack.pose().pushPose();
        stack.pose().scale(this.scaleX, this.scaleY, 1);
        stack.blit(this.texture, Math.round(this.getX() / this.scaleX), Math.round(this.getY() / this.scaleY), x, y, Math.round(this.width / this.scaleX), Math.round(this.height / this.scaleY), this.texWidth, this.texHeight);
        stack.pose().popPose();
        RenderSystem.enableDepthTest();
        this.renderText(stack);
    }

    protected void renderText(GuiGraphics stack) {
        Minecraft mc = Minecraft.getInstance();
        int color = this.getFGColor();
        String buttonText = this.getMessage().getString();
        int strWidth = mc.font.width(buttonText);
        if (strWidth <= this.width - 6) {
            this.drawCenteredString(stack, mc.font, buttonText, this.getX() + this.width / 2 + this.textXOff, this.getY() + this.height / 2 + this.textYOff, color);
        }
        else if (!this.isHovered) {
            this.scrollCounter = 0;
            int ellipsisWidth = mc.font.width("...");
            if (strWidth > ellipsisWidth) buttonText = this.trimStringToWidth(this.getMessage(), this.width - 6 - ellipsisWidth).getString().trim() + "...";
            this.drawCenteredString(stack, mc.font, buttonText, this.getX() + this.width / 2 + this.textXOff, this.getY() + this.height / 2 + this.textYOff, color);
        }
        else {
            int halfLen = mc.font.width(buttonText + "      ");
            buttonText += "      " + buttonText;
            stack.pose().pushPose();
            double d0 = mc.getWindow().getGuiScale();
            float y = Minecraft.getInstance().screen.height - this.getY() - this.height;
            RenderSystem.enableScissor((int) (this.getX() * d0), (int) (y * d0), (int) (d0 * this.width), (int) (d0 * this.height));
            stack.pose().translate((-this.scrollCounter - mc.getDeltaFrameTime()) % halfLen, 0, 0);
            stack.drawString(mc.font, buttonText, this.getX() + this.width / 8 + this.textXOff, this.getY() + this.height / 2 + this.textYOff, color, this.dropShadow);
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
        return !this.isHovered ? this.fontColor : this.hoverFontColor;
    }

    public static JsonButton deserialize(JsonObject obj) {
        JsonElement x = obj.get("x");
        JsonElement y = obj.get("y");
        JsonElement width = obj.get("width");
        JsonElement height = obj.get("height");
        JsonElement tex = obj.get("texture");
        JsonElement u = obj.get("u");
        JsonElement v = obj.get("v");
        JsonElement hoverU = obj.get("hoverU");
        JsonElement hoverV = obj.get("hoverV");
        JsonElement texWidth = obj.get("texWidth");
        JsonElement texHeight = obj.get("texHeight");
        JsonElement widgets = obj.get("widgets");
        JsonElement langKey = obj.get("langKey");
        JsonElement hoverLangKey = obj.get("hoverLangKey");
        JsonElement action = obj.get("action");
        JsonElement fontColor = obj.get("fontColor");
        JsonElement hoverFontColor = obj.get("hoverFontColor");
        JsonElement anchor = obj.get("anchor");
        JsonElement textX = obj.get("textXOffset");
        JsonElement textY = obj.get("textYOffset");
        JsonElement dropShadow = obj.get("dropShadow");
        JsonElement active = obj.get("active");
        JsonElement scaleX = obj.get("scaleX");
        JsonElement scaleY = obj.get("scaleY");

        ResourceLocation _tex = tex == null ? WIDGETS_LOCATION : new ResourceLocation(tex.getAsString());
        int _u = get(u, 0), _v = get(v, 0), _hoverU = get(hoverU, 0), _hoverV = get(hoverV, 0);
        int _x = get(x, 0), _y = get(y, 0), _width = get(width, 0), _height = get(height, 0);
        int _texWidth = get(texWidth, 256), _texHeight = get(texHeight, 256);
        boolean _widgets = widgets == null ? _tex.toString().contains("widgets") : widgets.getAsBoolean();
        int _fontColor = get(fontColor, 16777215), _hoverFontColor = get(hoverFontColor, 16777215);
        String displayKey = langKey == null ? "" : langKey.getAsString();
        String hoverKey = hoverLangKey == null ? "" : hoverLangKey.getAsString();
        ButtonAction act = ButtonAction.valueOf(action.getAsString().toUpperCase(Locale.ROOT));
        AnchorPoint _anchor = anchor == null ? AnchorPoint.DEFAULT : AnchorPoint.valueOf(anchor.getAsString());
        Object data = act.readData(obj);
        int _textX = get(textX, 0), _textY = get(textY, -4);
        float _scaleX = get(scaleX, 1F), _scaleY = get(scaleY, 1F);
        boolean _dropShadow = dropShadow == null ? true : dropShadow.getAsBoolean();
        JsonButton button = new JsonButton(_x, _y, _width, _height, _fontColor, _hoverFontColor, displayKey, new ActionInstance(act, data));
        button.texture(_tex, _u, _v, _hoverU, _hoverV, _texWidth, _texHeight).usesWidgets(_widgets).anchor(_anchor);
        button.textOffsets(_textX, _textY).dropShadow(_dropShadow).scale(_scaleX, _scaleY).hoverText(hoverKey);
        button.active = active == null ? true : active.getAsBoolean();
        return button;
    }

    private static int get(JsonElement e, int def) {
        return e == null ? def : e.getAsInt();
    }

    private static float get(JsonElement e, float def) {
        return e == null ? def : e.getAsFloat();
    }
}
