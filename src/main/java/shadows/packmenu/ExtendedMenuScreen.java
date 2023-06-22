package shadows.packmenu;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.internal.BrandingControl;
import shadows.packmenu.buttons.JsonButton;
import shadows.packmenu.panorama.VariedCubeMap;
import shadows.packmenu.slideshow.Slideshow;

public class ExtendedMenuScreen extends TitleScreen {

	public static final ResourceLocation BACKGROUND = new ResourceLocation(PackMenu.MODID, "textures/gui/background.png");
	public static VariedCubeMap VARIED_CUBE_MAP = new VariedCubeMap(new ResourceLocation("textures/gui/title/background/panorama"));

	public final PanoramaRenderer panorama = new PanoramaRenderer(VARIED_CUBE_MAP);

	public ExtendedMenuScreen(boolean fade) {
		super(fade);
	}

	@Override
	protected void init() {
		super.init();
		this.renderables.clear();
		this.children().clear();
		//this.narratables.clear();
		if (PackMenuClient.BUTTON_MANAGER.getButtons().isEmpty()) {
			this.addDefaultButtons();
		} else PackMenuClient.BUTTON_MANAGER.getButtons().forEach(b -> {
			this.addRenderableWidget(b).setup(this);
		});

		this.minecraft.setConnectedToRealms(false);

		int txtWidth = this.font.width(COPYRIGHT_TEXT);
		int leftPos = this.width - txtWidth - 2;
		this.addRenderableWidget(new PlainTextButton(leftPos, this.height - 10, txtWidth, 10, COPYRIGHT_TEXT, (p_211790_) -> {
			this.minecraft.setScreen(new WinScreen(false, Runnables.doNothing()));
		}, this.font));
	}

	@Override
	public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
		if (this.fadeInStart == 0L && this.fading) {
			this.fadeInStart = Util.getMillis();
		}

		float f = this.fading ? (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
		if (PackMenuClient.drawPanorama) {
			this.panorama.render(partialTicks * PackMenuClient.panoramaSpeed, Mth.clamp(f, 0.0F, 1.0F));
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.fading ? (float) Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1.0F);
			gfx.blit(PANORAMA_OVERLAY, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		} else if (PackMenuClient.slideshow) {
			Slideshow.render(this, gfx, partialTicks);
		} else {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, BACKGROUND);
			gfx.blit(BACKGROUND, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		}

		float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = Mth.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0) {
			if (PackMenuClient.drawTitle) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				this.logoRenderer.renderLogo(gfx, PackMenuClient.title.getX(this), f1, PackMenuClient.title.getY(this));
			}

			if (PackMenuClient.logo != null) PackMenuClient.logo.draw(this, gfx);

			//RenderSystem.setShaderTexture(0, MINECRAFT_EDITION);

			//if (PackMenuClient.drawJavaEd) blit(gfx, PackMenuClient.javaEd.getX(this), PackMenuClient.javaEd.getY(this), 0.0F, 0.0F, 98, 14, 128, 16);

			if (PackMenuClient.drawForgeInfo) {
				int alpha = (this.fading ? Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1) << 24;
				int x = PackMenuClient.forgeWarn.getX(this);
				int y = PackMenuClient.forgeWarn.getY(this);
				if (x != 0 || y != 0) {
					gfx.pose().pushPose();
					gfx.pose().translate(x, y, 0);
					ForgeHooksClient.renderMainMenu(this, gfx, this.getFont(), this.width, this.height, alpha);
					gfx.pose().popPose();
				} else ForgeHooksClient.renderMainMenu(this, gfx, this.getFont(), this.width, this.height, alpha);
			}

			if (this.splash != null && PackMenuClient.drawSplash) {
				gfx.pose().pushPose();
				gfx.pose().translate(PackMenuClient.splash.getX(this), PackMenuClient.splash.getY(this), 0);
				gfx.pose().mulPose(Axis.ZP.rotationDegrees(PackMenuClient.splashRotation));
				this.splash.render(gfx, this.width, font, PackMenuClient.splashColor);
				gfx.pose().popPose();
			}

			String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
			s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
			if (Minecraft.checkModStatus().shouldReportAsModified()) {
				s = s + I18n.get("menu.modded");
			}

			for (var widget : this.renderables) {
				if (widget instanceof AbstractWidget) ((AbstractWidget) widget).setAlpha(f1);
			}

			for (int i = 0; i < this.renderables.size(); ++i) {
				this.renderables.get(i).render(gfx, mouseX, mouseY, partialTicks);
			}

			BrandingControl.forEachLine(true, true, (brdline, brd) -> gfx.drawString(this.getFont(), brd, 2, this.height - (10 + brdline * (this.getFont().lineHeight + 1)), 16777215 | l));

			BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> gfx.drawString(this.getFont(), brd, this.width - this.getFont().width(brd), this.height - (10 + (brdline + 1) * (this.getFont().lineHeight + 1)), 16777215 | l));
			// modUpdateNotification.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		}
	}

	@Override
	public void tick() {
		super.tick();
		for (var b : this.renderables) {
			if (b instanceof JsonButton && ((JsonButton) b).isHoveredOrFocused()) {
				((JsonButton) b).tickScrollCounter();
			}
		}
	}

	private void addDefaultButtons() {
		int buttonHeight = this.height / 4 + 48;
		int buttonWidth = this.width / 2;

		//Singleplayer Button
		this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), btn -> {
			this.minecraft.setScreen(new SelectWorldScreen(this));
		}).bounds(buttonWidth - 100, buttonHeight, 200, 20).build());

		//Multiplayer Button
		this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), btn -> {
			Screen screen = (Screen) (this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this));
			this.minecraft.setScreen(screen);
		}).bounds(buttonWidth - 100, buttonHeight + 24, 200, 20).build());

		//Realms Button
		this.addRenderableWidget(Button.builder(Component.translatable("menu.online"), btn -> {
			this.realmsButtonClicked();
		}).bounds(buttonWidth + 2, buttonHeight + 48, 98, 20).build());

		//Mods Button
		this.addRenderableWidget(Button.builder(Component.translatable("fml.menu.mods"), btn -> {
			this.minecraft.setScreen(new net.minecraftforge.client.gui.ModListScreen(this));
		}).pos(buttonWidth - 100, buttonHeight + 48).size(98, 20).build());

		//Language Button
		this.addRenderableWidget(new ImageButton(buttonWidth - 124, buttonHeight + 72 + 12, 20, 20, 0, 106, 20, AbstractWidget.WIDGETS_LOCATION, 256, 256, btn -> {
			this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
		}, Component.translatable("narrator.button.language")));

		//Options Button
		this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), btn -> {
			this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
		}).bounds(buttonWidth - 100, buttonHeight + 72 + 12, 98, 20).build());

		//Quit Button
		this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), btn -> {
			this.minecraft.stop();
		}).bounds(buttonWidth + 2, buttonHeight + 72 + 12, 98, 20).build());

		//Accessibility Options Button
		this.addRenderableWidget(new ImageButton(buttonWidth + 104, buttonHeight + 72 + 12, 20, 20, 0, 0, 20, Button.ACCESSIBILITY_TEXTURE, 32, 64, btn -> {
			this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
		}, Component.translatable("narrator.button.accessibility")));
	}

	public Font getFont() {
		return this.font;
	}

}
