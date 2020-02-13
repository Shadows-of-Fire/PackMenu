package shadows.menu.buttons;

import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

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

	public JsonButton(int xPos, int yPos, int width, int height, int fontColor, int hoverFontColor, String langKey, ActionInstance handler) {
		super(xPos, yPos, width, height, langKey, handler);
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

	public JsonButton usesWidgets(boolean widgets) {
		this.usesWidgets = widgets;
		return this;
	}

	public JsonButton setup(MainMenuScreen screen) {
		this.x = this.xOff + screen.width / 2;
		this.y = this.yOff + screen.height / 4 + 48;
		this.setMessage(I18n.format(langKey));
		return this;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void renderButton(int mouseX, int mouseY, float partial) {
		if (this.visible) {
			if (usesWidgets) renderWidgetButton(mouseX, mouseY, partial);
			else renderImageButton(mouseX, mouseY, partial);
		}
	}

	public static void drawCenteredStringNoShadow(FontRenderer font, String string, int x, int y, int color) {
		font.drawString(string, x - font.getStringWidth(string) / 2, y, color);
	}

	/**
	 * Renders this button as if it was a default button based on the widgets texture (automatic scaling)
	 */
	private void renderWidgetButton(int mouseX, int mouseY, float partial) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(texture);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
		this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		this.renderBg(minecraft, mouseX, mouseY);
		int j = getFGColor();
		this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}

	/**
	 * Renders this button as if it was an image button.
	 */
	private void renderImageButton(int mouseX, int mouseY, float partial) {
		Minecraft mc = Minecraft.getInstance();
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		mc.getTextureManager().bindTexture(this.texture);
		RenderSystem.disableDepthTest();
		int x = u, y = v;
		if (this.isHovered()) {
			x = hoverU;
			y = hoverV;
		}
		blit(this.x, this.y, x, y, this.width, this.height, texWidth, texHeight);
		RenderSystem.enableDepthTest();
		int color = getFGColor();

		String buttonText = this.getMessage();
		int strWidth = mc.fontRenderer.getStringWidth(buttonText);
		int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

		if (strWidth > width - 6 && strWidth > ellipsisWidth) buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

		drawCenteredStringNoShadow(mc.fontRenderer, buttonText, this.x + this.width / 2 + 5, this.y + (this.height - 12) / 2, color);
	}

	@Override
	public int getFGColor() {
		return this.active ? fontColor : hoverFontColor;
	}

	public static JsonButton deserialize(JsonObject obj) {
		JsonElement tex = obj.get("texture");
		JsonElement u = obj.get("u");
		JsonElement v = obj.get("v");
		JsonElement hoverU = obj.get("hoverU");
		JsonElement hoverV = obj.get("hoverV");
		JsonElement widgets = obj.get("widgets");
		JsonElement x = obj.get("x");
		JsonElement y = obj.get("y");
		JsonElement width = obj.get("width");
		JsonElement height = obj.get("height");
		JsonElement langKey = obj.get("langKey");
		JsonElement action = obj.get("action");
		JsonElement texWidth = obj.get("texWidth");
		JsonElement texHeight = obj.get("texHeight");
		JsonElement fontColor = obj.get("fontColor");
		JsonElement hoverFontColor = obj.get("hoverFontColor");

		ResourceLocation _tex = tex == null ? WIDGETS_LOCATION : new ResourceLocation(tex.getAsString());
		int _u = get(u, 0), _v = get(v, 0), _hoverU = get(hoverU, 0), _hoverV = get(hoverV, 0);
		int _x = get(x, 0), _y = get(y, 0), _width = get(width, 0), _height = get(height, 0);
		int _texWidth = get(texWidth, 256), _texHeight = get(texHeight, 256);
		boolean _widgets = widgets == null ? _tex.toString().contains("widgets") : widgets.getAsBoolean();
		int _fontColor = get(fontColor, 16777215), _hoverFontColor = get(hoverFontColor, 10526880);
		String display = langKey == null ? "" : I18n.format(langKey.getAsString());
		ButtonAction act = ButtonAction.valueOf(action.getAsString().toUpperCase(Locale.ROOT));
		Object data = act.readData(obj);
		return new JsonButton(_x, _y, _width, _height, _fontColor, _hoverFontColor, display, new ActionInstance(act, data)).texture(_tex, _u, _v, _hoverU, _hoverV, _texWidth, _texHeight).usesWidgets(_widgets);
	}

	private static int get(JsonElement e, int def) {
		return e == null ? def : e.getAsInt();
	}
}