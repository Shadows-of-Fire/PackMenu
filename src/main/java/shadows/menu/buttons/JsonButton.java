package shadows.menu.buttons;

import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import shadows.menu.ExtendedMenuScreen;

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
	 * X and Y offsets.  Must be retained here since the main offsets are mutable.
	 */
	protected final int xOff, yOff;

	/**
	 * If the button uses a texture resembling of the widgets texture, and may be drawn with any width/height.
	 */
	protected boolean usesWidgets = false;

	/**
	 * The untranslated key of this button's text.  Cannot be translated at construction time as language hasn't loaded yet.
	 */
	protected final String langKey;

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

	public JsonButton(int xPos, int yPos, int width, int height, int fontColor, int hoverFontColor, String langKey, ActionInstance handler) {
		super(xPos, yPos, width, height, new TranslationTextComponent(langKey), handler, Button.DEFAULT_ON_TOOLTIP);
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
		this.x = this.xOff + anchor.getX(screen);
		this.y = this.yOff + anchor.getY(screen);
		this.setMessage(new TranslationTextComponent(langKey));
		return this;
	}

	public JsonButton dropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
		return this;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partial) {
		if (this.visible) {
			if (usesWidgets) renderWidgetButton(stack, mouseX, mouseY, partial);
			else renderImageButton(stack, mouseX, mouseY, partial);
		}
	}

	public static void drawCenteredStringNoShadow(MatrixStack stack, FontRenderer font, String string, int x, int y, int color) {
		font.drawString(stack, string, x - font.getStringWidth(string) / 2, y, color);
	}

	@Override
	public void drawCenteredString(MatrixStack stack, FontRenderer font, String string, int x, int y, int color) {
		if (dropShadow) super.drawCenteredString(stack, font, string, x, y, color);
		else drawCenteredStringNoShadow(stack, font, string, x, y, color);
	}

	/**
	 * Renders this button as if it was a default button based on the widgets texture (automatic scaling)
	 */
	@SuppressWarnings("deprecation")
	private void renderWidgetButton(MatrixStack stack, int mouseX, int mouseY, float partial) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(texture);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.blit(stack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
		this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		this.renderBg(stack, minecraft, mouseX, mouseY);
		int j = getFGColor();
		this.drawCenteredString(stack, fontrenderer, this.getMessage(), this.x + this.width / 2 + textXOff, this.y + this.height / 2 + textYOff, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}

	/**
	 * Renders this button as if it was an image button.
	 */
	private void renderImageButton(MatrixStack stack, int mouseX, int mouseY, float partial) {
		Minecraft mc = Minecraft.getInstance();
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		mc.getTextureManager().bindTexture(this.texture);
		RenderSystem.disableDepthTest();
		int x = u, y = v;
		if (this.isHovered()) {
			x = hoverU;
			y = hoverV;
		}
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		blit(stack, this.x, this.y, x, y, this.width, this.height, texWidth, texHeight);
		RenderSystem.enableDepthTest();
		int color = getFGColor();

		String buttonText = this.getMessage().getString();
		//int strWidth = mc.fontRenderer.getStringWidth(buttonText);
		//int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

		//if (strWidth > width - 6 && strWidth > ellipsisWidth) buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

		drawCenteredString(stack, mc.fontRenderer, buttonText, this.x + this.width / 2 + textXOff, this.y + this.height / 2 + textYOff, color);
	}

	@Override
	public int getFGColor() {
		return !this.isHovered ? fontColor : hoverFontColor;
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
		JsonElement action = obj.get("action");
		JsonElement fontColor = obj.get("fontColor");
		JsonElement hoverFontColor = obj.get("hoverFontColor");
		JsonElement anchor = obj.get("anchor");
		JsonElement textX = obj.get("textXOffset");
		JsonElement textY = obj.get("textYOffset");
		JsonElement dropShadow = obj.get("dropShadow");
		JsonElement active = obj.get("active");

		ResourceLocation _tex = tex == null ? WIDGETS_LOCATION : new ResourceLocation(tex.getAsString());
		int _u = get(u, 0), _v = get(v, 0), _hoverU = get(hoverU, 0), _hoverV = get(hoverV, 0);
		int _x = get(x, 0), _y = get(y, 0), _width = get(width, 0), _height = get(height, 0);
		int _texWidth = get(texWidth, 256), _texHeight = get(texHeight, 256);
		boolean _widgets = widgets == null ? _tex.toString().contains("widgets") : widgets.getAsBoolean();
		int _fontColor = get(fontColor, 16777215), _hoverFontColor = get(hoverFontColor, 16777215);
		String display = langKey == null ? "" : I18n.format(langKey.getAsString());
		ButtonAction act = ButtonAction.valueOf(action.getAsString().toUpperCase(Locale.ROOT));
		AnchorPoint _anchor = anchor == null ? AnchorPoint.DEFAULT : AnchorPoint.valueOf(anchor.getAsString());
		Object data = act.readData(obj);
		int _textX = get(textX, 0), _textY = get(textY, -4);
		boolean _dropShadow = dropShadow == null ? true : dropShadow.getAsBoolean();
		JsonButton button = new JsonButton(_x, _y, _width, _height, _fontColor, _hoverFontColor, display, new ActionInstance(act, data)).texture(_tex, _u, _v, _hoverU, _hoverV, _texWidth, _texHeight).usesWidgets(_widgets).anchor(_anchor).textOffsets(_textX, _textY).dropShadow(_dropShadow);
		button.active = active == null ? true : active.getAsBoolean();
		return button;
	}

	private static int get(JsonElement e, int def) {
		return e == null ? def : e.getAsInt();
	}
}