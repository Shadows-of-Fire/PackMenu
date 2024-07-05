package dev.shadowsoffire.packmenu;

import java.util.Comparator;

import com.google.common.util.concurrent.Runnables;
import com.mojang.math.Axis;
import com.mojang.realmsclient.RealmsMainScreen;

import dev.shadowsoffire.packmenu.buttons.JsonButton;
import dev.shadowsoffire.packmenu.panorama.VariedCubeMap;
import dev.shadowsoffire.packmenu.slideshow.Slideshow;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.neoforged.neoforge.internal.BrandingControl;

public class ExtendedMenuScreen extends TitleScreen {

    public static final ResourceLocation BACKGROUND = PackMenu.loc("textures/gui/background.png");
    public static VariedCubeMap VARIED_CUBE_MAP = new VariedCubeMap(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama"));

    public final PanoramaRenderer panorama = new PanoramaRenderer(VARIED_CUBE_MAP);

    public ExtendedMenuScreen(boolean fade) {
        super(fade);
        Slideshow.reset();
    }

    @Override
    protected void init() {
        super.init();
        this.renderables.clear();
        this.children().clear();
        if (PackMenu.BUTTON_MANAGER.getButtons().isEmpty()) {
            this.addDefaultButtons();
        }
        else {
            PackMenu.BUTTON_MANAGER.getButtons().stream().sorted(Comparator.comparing(JsonButton::getYPos).thenComparing(Comparator.comparing(JsonButton::getXPos))).forEach(b -> {
                this.addRenderableWidget(b).setup(this);
            });
        }

        int txtWidth = this.font.width(COPYRIGHT_TEXT);
        int leftPos = this.width - txtWidth - 2;
        this.addRenderableWidget(new PlainTextButton(leftPos, this.height - 10, txtWidth, 10, COPYRIGHT_TEXT, p_211790_ -> {
            this.minecraft.setScreen(new WinScreen(false, Runnables.doNothing()));
        }, this.font));
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        if (this.fadeInStart == 0L && this.fading) {
            this.fadeInStart = Util.getMillis();
        }

        float fade = 1.0F;
        if (this.fading) {
            float panoramaFade = (float) (Util.getMillis() - this.fadeInStart) / 2000.0F;
            if (panoramaFade > 1.0F) {
                this.fading = false;
                this.panoramaFade = 1.0F;
            }
            else {
                panoramaFade = Mth.clamp(panoramaFade, 0.0F, 1.0F);
                fade = Mth.clampedMap(panoramaFade, 0.5F, 1.0F, 0.0F, 1.0F);
                this.panoramaFade = Mth.clampedMap(panoramaFade, 0.0F, 0.5F, 0.0F, 1.0F);
            }

            this.fadeWidgets(fade);
        }

        if (PackMenu.drawPanorama) {
            this.renderPanorama(gfx, partialTicks);
        }
        else if (PackMenu.slideshow) {
            Slideshow.render(this, gfx, partialTicks);
        }
        else {
            gfx.blit(BACKGROUND, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        }

        float f1 = this.fading ? Mth.clamp(fade - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = Mth.ceil(f1 * 255.0F) << 24;
        if ((l & -67108864) != 0) {
            if (PackMenu.drawTitle) {
                this.logoRenderer.renderLogo(gfx, PackMenu.title.getX(this), f1, PackMenu.title.getY(this));
            }

            if (PackMenu.logo != null) {
                PackMenu.logo.draw(this, gfx);
            }

            if (PackMenu.drawForgeInfo) {
                int alpha = (this.fading ? Mth.ceil(Mth.clamp(fade, 0.0F, 1.0F)) : 1) << 24;
                int x = PackMenu.forgeWarn.getX(this);
                int y = PackMenu.forgeWarn.getY(this);
                if (x != 0 || y != 0) {
                    gfx.pose().pushPose();
                    gfx.pose().translate(x, y, 0);
                    ClientHooks.renderMainMenu(this, gfx, this.getFont(), this.width, this.height, alpha);
                    gfx.pose().popPose();
                }
                else ClientHooks.renderMainMenu(this, gfx, this.getFont(), this.width, this.height, alpha);
            }

            if (this.splash != null && PackMenu.drawSplash && !this.minecraft.options.hideSplashTexts().get()) {
                gfx.pose().pushPose();
                gfx.pose().translate(PackMenu.splash.getX(this), PackMenu.splash.getY(this), 0);
                gfx.pose().mulPose(Axis.ZP.rotationDegrees(PackMenu.splashRotation));
                this.splash.render(gfx, this.width, this.font, PackMenu.splashColor);
                gfx.pose().popPose();
            }

            String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
            s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
            if (Minecraft.checkModStatus().shouldReportAsModified()) {
                s = s + I18n.get("menu.modded");
            }

            for (int i = 0; i < this.renderables.size(); ++i) {
                this.renderables.get(i).render(gfx, mouseX, mouseY, partialTicks);
            }

            BrandingControl.forEachLine(true, true, (brdline, brd) -> gfx.drawString(this.getFont(), brd, 2, this.height - (10 + brdline * (this.getFont().lineHeight + 1)), 16777215 | l));

            BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> gfx.drawString(this.getFont(), brd, this.width - this.getFont().width(brd), this.height - (10 + (brdline + 1) * (this.getFont().lineHeight + 1)), 16777215 | l));
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
        int buttonHeight = this.height / 4 + 32;
        int buttonWidth = this.width / 2;

        // Singleplayer Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), btn -> {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }).bounds(buttonWidth - 100, buttonHeight, 200, 20).build());

        // Multiplayer Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), btn -> {
            Screen screen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
            this.minecraft.setScreen(screen);
        }).bounds(buttonWidth - 100, buttonHeight + 24, 200, 20).build());

        // Realms Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.online"), btn -> {
            this.minecraft.setScreen(new RealmsMainScreen(this));
        }).bounds(buttonWidth + 2, buttonHeight + 48, 98, 20).build());

        // Mods Button
        this.addRenderableWidget(Button.builder(Component.translatable("fml.menu.mods"), btn -> {
            this.minecraft.setScreen(new ModListScreen(this));
        }).pos(buttonWidth - 100, buttonHeight + 48).size(98, 20).build());

        // Language Button
        SpriteIconButton langBtn = this.addRenderableWidget(CommonButtons.language(20, btn -> {
            this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
        }, true));
        langBtn.setPosition(buttonWidth - 124, buttonHeight + 72 + 12);

        // Options Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), btn -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }).bounds(buttonWidth - 100, buttonHeight + 72 + 12, 98, 20).build());

        // Quit Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), btn -> {
            this.minecraft.stop();
        }).bounds(buttonWidth + 2, buttonHeight + 72 + 12, 98, 20).build());

        // Accessibility Options Button
        SpriteIconButton accessibilityBtn = this.addRenderableWidget(CommonButtons.accessibility(20, btn -> {
            this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
        }, true));
        accessibilityBtn.setPosition(buttonWidth + 104, buttonHeight + 72 + 12);
    }

    public Font getFont() {
        return this.font;
    }

}
