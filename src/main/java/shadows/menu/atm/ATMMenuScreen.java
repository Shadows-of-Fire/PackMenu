package shadows.menu.atm;

import java.net.URI;

import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import shadows.menu.PackMenu;

public class ATMMenuScreen extends MainMenuScreen {

	public static final ResourceLocation BACKGROUND = tex("background");
	public static final ResourceLocation AKLIZ_R = tex("akliz");
	public static final ResourceLocation DISCORD_R = tex("discord");
	public static final ResourceLocation GITHUB_R = tex("github");
	public static final ResourceLocation LANG = tex("lang");
	public static final ResourceLocation MODS = tex("mods");
	public static final ResourceLocation SMP = tex("multiplayer");
	public static final ResourceLocation QUIT = tex("quit");
	public static final ResourceLocation REDDIT_R = tex("reddit");
	public static final ResourceLocation OPTIONS = tex("settings");
	public static final ResourceLocation SSP = tex("singleplayer");
	public static final URI DISCORD, GITHUB, AKLIZ, REDDIT;
	static {
		try {
			DISCORD = new URI("https://discord.gg/K5FBPYa");
			AKLIZ = new URI("https://akliz.net/allthemods");
			REDDIT = new URI("https://reddit.com/r/allthemods");
			GITHUB = new URI("https://github.com/allthemods");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void init() {
		if (this.splashText == null) {
			this.splashText = this.minecraft.getSplashes().getSplashText();
		}

		this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.widthCopyrightRest = this.width - this.widthCopyright - 2;
		int maxHeight = this.height / 4 - 30;
		int buttonWidth = 120, buttonHeight = 30;
		int xSpacing = buttonWidth + 5, ySpacing = buttonHeight + 5;

		//Singleplayer Button
		this.addButton(new TexturedButton(this.width / 2 - 50, maxHeight, buttonWidth, buttonHeight, I18n.format("menu.singleplayer"), button -> {
			this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
		})).setTexture(SSP);

		//Multiplayer Button
		this.addButton(new TexturedButton(this.width / 2 - 50 + xSpacing, maxHeight, buttonWidth, buttonHeight, I18n.format("menu.multiplayer"), button -> {
			this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
		})).setTexture(SMP);

		//Discord Button
		this.addButton(new TexturedButton(this.width / 2 - 50, maxHeight + ySpacing, buttonWidth, buttonHeight, I18n.format("packmenu.atm.discord"), button -> {
			Util.getOSType().openURI(DISCORD);
		})).setTexture(DISCORD_R);

		//Akliz Button
		this.addButton(new TexturedButton(this.width / 2 - 50 + xSpacing, maxHeight + ySpacing, buttonWidth, buttonHeight, I18n.format("packmenu.atm.akliz"), button -> {
			Util.getOSType().openURI(AKLIZ);
		})).setTexture(AKLIZ_R);

		//Reddit Button
		this.addButton(new TexturedButton(this.width / 2 - 50, maxHeight + ySpacing * 2, buttonWidth, buttonHeight, I18n.format("packmenu.atm.reddit"), button -> {
			Util.getOSType().openURI(REDDIT);
		})).setTexture(REDDIT_R);

		//Github Button
		this.addButton(new TexturedButton(this.width / 2 - 50 + xSpacing, maxHeight + ySpacing * 2, buttonWidth, buttonHeight, I18n.format("packmenu.atm.github"), button -> {
			Util.getOSType().openURI(GITHUB);
		})).setTexture(GITHUB_R);

		//Mods Button
		this.addButton(new TexturedButton(this.width / 2 - 50, maxHeight + ySpacing * 3, buttonWidth, buttonHeight, I18n.format("fml.menu.mods"), button -> {
			this.minecraft.displayGuiScreen(new ModListScreen(this));
		})).setTexture(MODS);

		//Language Button
		this.addButton(new TexturedButton(this.width / 2 - 50 + xSpacing, maxHeight + ySpacing * 3, buttonWidth, buttonHeight, I18n.format("narrator.button.language"), button -> {
			this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
		})).setTexture(LANG);

		//Options Button
		this.addButton(new TexturedButton(this.width / 2 - 50, maxHeight + ySpacing * 4, buttonWidth, buttonHeight, I18n.format("menu.options"), button -> {
			this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
		})).setTexture(OPTIONS);

		//Quit Button
		this.addButton(new TexturedButton(this.width / 2 - 50 + xSpacing, maxHeight + ySpacing * 4, buttonWidth, buttonHeight, I18n.format("menu.quit"), button -> {
			this.minecraft.shutdown();
		})).setTexture(QUIT);

		this.minecraft.setConnectedToRealms(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
			this.firstRenderTime = Util.milliTime();
		}

		this.minecraft.getTextureManager().bindTexture(BACKGROUND);

		blit(0, 0, this.width, this.height, 0.0F, 0.0F, 1080, 1920, 1080, 1920);
		float f1 = 1.0F;
		int l = MathHelper.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0) {

			this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);

			String s = "Minecraft " + SharedConstants.getVersion().getName();
			s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());

			BrandingControl.forEachLine(true, true, (brdline, brd) -> this.drawString(this.font, brd, 2, this.height - (10 + brdline * (this.font.FONT_HEIGHT + 1)), 16777215 | l));

			BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> this.drawString(this.font, brd, this.width - font.getStringWidth(brd), this.height - (10 + (brdline + 1) * (this.font.FONT_HEIGHT + 1)), 16777215 | l));
			this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 16777215 | l);
			if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
				fill(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
			}

			for (Widget widget : this.buttons) {
				widget.setAlpha(f1);
			}

			for (int i = 0; i < this.buttons.size(); ++i) {
				this.buttons.get(i).render(mouseX, mouseY, partialTicks);
			}
		}
	}

	private static ResourceLocation tex(String tex) {
		return new ResourceLocation(PackMenu.MODID, "textures/gui/atm/" + tex + ".png");
	}

}
