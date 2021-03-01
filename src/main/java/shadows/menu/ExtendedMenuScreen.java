package shadows.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import shadows.menu.buttons.JsonButton;
import shadows.menu.slideshow.Slideshow;

public class ExtendedMenuScreen extends MainMenuScreen {

	public static final ResourceLocation BACKGROUND = new ResourceLocation(PackMenu.MODID, "textures/gui/background.png");
	public final RenderSkybox panorama = new RenderSkybox(PANORAMA_RESOURCES);

	public ExtendedMenuScreen(boolean fade) {
		super(fade);
	}

	@Override
	protected void init() {
		if (this.splashText == null) {
			this.splashText = this.minecraft.getSplashes().getSplashText();
		}

		this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.widthCopyrightRest = this.width - this.widthCopyright - 2;

		if (PackMenuClient.BUTTON_MANAGER.getButtons().isEmpty()) {
			addDefaultButtons();
		} else PackMenuClient.BUTTON_MANAGER.getButtons().forEach(b -> {
			this.addButton(b).setup(this);
		});

		this.minecraft.setConnectedToRealms(false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
			this.firstRenderTime = Util.milliTime();
		}

		if (PackMenuClient.drawPanorama) {
			float f = this.showFadeInAnimation ? (Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
			fill(stack, 0, 0, this.width, this.height, -1);
			this.panorama.render(partialTicks * PackMenuClient.panoramaSpeed, MathHelper.clamp(f, 0.0F, 1.0F));
			this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
			blit(stack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		} else if (PackMenuClient.slideshow) {
			Slideshow.render(this, stack, partialTicks);
		} else {
			this.minecraft.getTextureManager().bindTexture(BACKGROUND);
			blit(stack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		}

		float f1 = 1.0F;
		int l = MathHelper.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0) {
			if (PackMenuClient.drawTitle) {
				this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
				this.blit(stack, PackMenuClient.title.getX(this), PackMenuClient.title.getY(this), 0, 0, 155, 44);
				this.blit(stack, PackMenuClient.title.getX(this) + 155, PackMenuClient.title.getY(this), 0, 45, 155, 44);
			}

			if (PackMenuClient.logo != null) PackMenuClient.logo.draw(this, stack);

			this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);

			if (PackMenuClient.drawJavaEd) blit(stack, PackMenuClient.javaEd.getX(this), PackMenuClient.javaEd.getY(this), 0.0F, 0.0F, 98, 14, 128, 16);

			if (PackMenuClient.drawForgeInfo) {
				int x = PackMenuClient.forgeWarn.getX(this);
				int y = PackMenuClient.forgeWarn.getY(this);
				if (x != 0 || y != 0) {
					stack.push();
					stack.translate(x, y, 0);
					ForgeHooksClient.renderMainMenu(this, stack, getFont(), this.width, this.height);
					stack.pop();
				} else ForgeHooksClient.renderMainMenu(this, stack, getFont(), this.width, this.height);
			}

			if (this.splashText != null && PackMenuClient.drawSplash) {
				RenderSystem.pushMatrix();
				RenderSystem.translatef(PackMenuClient.splash.getX(this), PackMenuClient.splash.getY(this), 0);
				RenderSystem.rotatef(PackMenuClient.splashRotation, 0.0F, 0.0F, 1.0F);
				float f2 = 1.8F - MathHelper.abs(MathHelper.sin(Util.milliTime() % 1000L / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
				f2 = f2 * 100.0F / (getFont().getStringWidth(this.splashText) + 32);
				RenderSystem.scalef(f2, f2, f2);
				drawCenteredString(stack, getFont(), this.splashText, 0, -8, PackMenuClient.splashColor);
				RenderSystem.popMatrix();
			}

			String s = "Minecraft " + SharedConstants.getVersion().getName();
			s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());

			for (Widget widget : this.buttons) {
				widget.setAlpha(f1);
			}

			for (int i = 0; i < this.buttons.size(); ++i) {
				this.buttons.get(i).render(stack, mouseX, mouseY, partialTicks);
			}

			BrandingControl.forEachLine(true, true, (brdline, brd) -> drawString(stack, getFont(), brd, 2, this.height - (10 + brdline * (getFont().FONT_HEIGHT + 1)), 16777215 | l));

			BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> drawString(stack, getFont(), brd, this.width - getFont().getStringWidth(brd), this.height - (10 + (brdline + 1) * (getFont().FONT_HEIGHT + 1)), 16777215 | l));
			drawString(stack, getFont(), "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 16777215 | l);
			if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
				fill(stack, this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		for (Widget b : this.buttons) {
			if (b.isHovered() && b instanceof JsonButton) {
				((JsonButton) b).tickScrollCounter();
			}
		}
	}

	private void addDefaultButtons() {
		int buttonHeight = this.height / 4 + 48;
		int buttonWidth = this.width / 2;

		//Singleplayer Button
		this.addButton(new Button(buttonWidth - 100, buttonHeight, 200, 20, new TranslationTextComponent("menu.singleplayer"), (p_213089_1_) -> {
			this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
		}));

		//Multiplayer Button
		this.addButton(new Button(buttonWidth - 100, buttonHeight + 24 * 1, 200, 20, new TranslationTextComponent("menu.multiplayer"), (p_213086_1_) -> {
			this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
		}));

		//Realms Button
		this.addButton(new Button(buttonWidth + 2, buttonHeight + 24 * 2, 98, 20, new TranslationTextComponent("menu.online"), (p_213095_1_) -> {
			RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
			realmsbridgescreen.func_231394_a_(this);
		}));

		//Mods Button
		this.addButton(new Button(buttonWidth - 100, buttonHeight + 24 * 2, 98, 20, new TranslationTextComponent("fml.menu.mods"), button -> {
			this.minecraft.displayGuiScreen(new ModListScreen(this));
		}));

		//Language Button
		this.addButton(new ImageButton(buttonWidth - 124, buttonHeight + 72 + 12, 20, 20, 0, 106, 20, Widget.WIDGETS_LOCATION, 256, 256, (p_213090_1_) -> {
			this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
		}, new TranslationTextComponent("narrator.button.language")));

		//Options Button
		this.addButton(new Button(buttonWidth - 100, buttonHeight + 72 + 12, 98, 20, new TranslationTextComponent("menu.options"), (p_213096_1_) -> {
			this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
		}));

		//Quit Button
		this.addButton(new Button(buttonWidth + 2, buttonHeight + 72 + 12, 98, 20, new TranslationTextComponent("menu.quit"), (p_213094_1_) -> {
			this.minecraft.shutdown();
		}));

		//Accessibility Options Button
		this.addButton(new ImageButton(buttonWidth + 104, buttonHeight + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, (p_213088_1_) -> {
			this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings));
		}, new TranslationTextComponent("narrator.button.accessibility")));
	}

	public FontRenderer getFont() {
		return font;
	}

}
