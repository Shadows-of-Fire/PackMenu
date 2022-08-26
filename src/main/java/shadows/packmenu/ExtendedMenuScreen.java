package shadows.packmenu;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mojang.realmsclient.RealmsMainScreen;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.internal.BrandingControl;
import shadows.packmenu.buttons.JsonButton;
import shadows.packmenu.slideshow.Slideshow;

public class ExtendedMenuScreen extends TitleScreen {

	public static final ResourceLocation BACKGROUND = new ResourceLocation(PackMenu.MODID, "textures/gui/background.png");
	public final PanoramaRenderer panorama = new PanoramaRenderer(CUBE_MAP);

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

	@SuppressWarnings("deprecation")
	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
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
			blit(stack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		} else if (PackMenuClient.slideshow) {
			Slideshow.render(this, stack, partialTicks);
		} else {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, BACKGROUND);
			blit(stack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		}

		float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = Mth.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0) {
			if (PackMenuClient.drawTitle) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, MINECRAFT_LOGO);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);
				this.blitOutlineBlack(PackMenuClient.title.getX(this), PackMenuClient.title.getY(this), (x, y) -> {
					this.blit(stack, x, y, 0, 0, 155, 44);
					this.blit(stack, x + 155, y, 0, 45, 155, 44);
				});
			}

			if (PackMenuClient.logo != null) PackMenuClient.logo.draw(this, stack);

			RenderSystem.setShaderTexture(0, MINECRAFT_EDITION);

			if (PackMenuClient.drawJavaEd) blit(stack, PackMenuClient.javaEd.getX(this), PackMenuClient.javaEd.getY(this), 0.0F, 0.0F, 98, 14, 128, 16);

			if (PackMenuClient.drawForgeInfo) {
				int alpha = (this.fading ? Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1) << 24;
				int x = PackMenuClient.forgeWarn.getX(this);
				int y = PackMenuClient.forgeWarn.getY(this);
				if (x != 0 || y != 0) {
					stack.pushPose();
					stack.translate(x, y, 0);
					ForgeHooksClient.renderMainMenu(this, stack, this.getFont(), this.width, this.height, alpha);
					stack.popPose();
				} else ForgeHooksClient.renderMainMenu(this, stack, this.getFont(), this.width, this.height, alpha);
			}

			if (this.splash != null && PackMenuClient.drawSplash) {
				stack.pushPose();
				stack.translate(PackMenuClient.splash.getX(this), PackMenuClient.splash.getY(this), 0);
				stack.mulPose(Vector3f.ZP.rotationDegrees(PackMenuClient.splashRotation));
				float f2 = 1.8F - Mth.abs(Mth.sin(Util.getMillis() % 1000L / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
				f2 = f2 * 100.0F / (this.getFont().width(this.splash) + 32);
				stack.scale(f2, f2, f2);
				drawCenteredString(stack, this.getFont(), this.splash, 0, -8, PackMenuClient.splashColor);
				stack.popPose();
			}

			String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
			s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
			if (Minecraft.checkModStatus().shouldReportAsModified()) {
				s = s + I18n.get("menu.modded");
			}

			for (Widget widget : this.renderables) {
				if (widget instanceof AbstractWidget) ((AbstractWidget) widget).setAlpha(f1);
			}

			for (int i = 0; i < this.renderables.size(); ++i) {
				this.renderables.get(i).render(stack, mouseX, mouseY, partialTicks);
			}

			BrandingControl.forEachLine(true, true, (brdline, brd) -> drawString(stack, this.getFont(), brd, 2, this.height - (10 + brdline * (this.getFont().lineHeight + 1)), 16777215 | l));

			BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> drawString(stack, this.getFont(), brd, this.width - this.getFont().width(brd), this.height - (10 + (brdline + 1) * (this.getFont().lineHeight + 1)), 16777215 | l));
			// modUpdateNotification.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		}
	}

	@Override
	public void tick() {
		super.tick();
		for (Widget b : this.renderables) {
			if (b instanceof JsonButton && ((JsonButton) b).isHoveredOrFocused()) {
				((JsonButton) b).tickScrollCounter();
			}
		}
	}

	private void addDefaultButtons() {
		int buttonHeight = this.height / 4 + 48;
		int buttonWidth = this.width / 2;

		//Singleplayer Button
		this.addRenderableWidget(new Button(buttonWidth - 100, buttonHeight, 200, 20, Component.translatable("menu.singleplayer"), p_213089_1_ -> {
			this.minecraft.setScreen(new SelectWorldScreen(this));
		}));

		//Multiplayer Button
		this.addRenderableWidget(new Button(buttonWidth - 100, buttonHeight + 24 * 1, 200, 20, Component.translatable("menu.multiplayer"), p_213086_1_ -> {
			this.minecraft.setScreen(new JoinMultiplayerScreen(this));
		}));

		//Realms Button
		this.addRenderableWidget(new Button(buttonWidth + 2, buttonHeight + 24 * 2, 98, 20, Component.translatable("menu.online"), p_213095_1_ -> {
			Minecraft.getInstance().setScreen(new RealmsMainScreen(Minecraft.getInstance().screen));
		}));

		//Mods Button
		this.addRenderableWidget(new Button(buttonWidth - 100, buttonHeight + 24 * 2, 98, 20, Component.translatable("fml.menu.mods"), button -> {
			this.minecraft.setScreen(new ModListScreen(this));
		}));

		//Language Button
		this.addRenderableWidget(new ImageButton(buttonWidth - 124, buttonHeight + 72 + 12, 20, 20, 0, 106, 20, AbstractWidget.WIDGETS_LOCATION, 256, 256, p_213090_1_ -> {
			this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
		}, Component.translatable("narrator.button.language")));

		//Options Button
		this.addRenderableWidget(new Button(buttonWidth - 100, buttonHeight + 72 + 12, 98, 20, Component.translatable("menu.options"), p_213096_1_ -> {
			this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
		}));

		//Quit Button
		this.addRenderableWidget(new Button(buttonWidth + 2, buttonHeight + 72 + 12, 98, 20, Component.translatable("menu.quit"), p_213094_1_ -> {
			this.minecraft.stop();
		}));

		//Accessibility Options Button
		this.addRenderableWidget(new ImageButton(buttonWidth + 104, buttonHeight + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURE, 32, 64, p_213088_1_ -> {
			this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
		}, Component.translatable("narrator.button.accessibility")));
	}

	public Font getFont() {
		return this.font;
	}

}
