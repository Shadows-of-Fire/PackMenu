package dev.shadowsoffire.packmenu.slideshow;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.shadowsoffire.packmenu.ExtendedMenuScreen;
import dev.shadowsoffire.packmenu.PackMenu;
import dev.shadowsoffire.packmenu.PackMenuClient;
import dev.shadowsoffire.placebo.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = PackMenu.MODID)
public class Slideshow {

    private static long ticks = 0;
    private static int index = 0;
    private static boolean fading = false;

    public static void render(ExtendedMenuScreen screen, GuiGraphics gfx, float partialTicks) {
        gfx.blit(PackMenuClient.slideshowTextures.get(index), 0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128);

        if (fading) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, PackMenuClient.slideshowTextures.get(nextIndex()));
            ClientUtil.colorBlit(gfx.pose(), 0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128, getAlphaFade(partialTicks) << 24 | 0xFFFFFF);
            RenderSystem.disableBlend();
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent e) {
        if (e.phase == Phase.END && Minecraft.getInstance().screen instanceof ExtendedMenuScreen) {
            ticks++;
            boolean wasFading = fading;
            fading = ticks % (PackMenuClient.slideshowDuration + PackMenuClient.slideshowTransition) >= PackMenuClient.slideshowDuration;
            if (wasFading && !fading) index = nextIndex();
        }
    }

    public static void reset() {
        ticks = 0;
        index = 0;
    }

    public static int nextIndex() {
        return index + 1 == PackMenuClient.slideshowTextures.size() ? 0 : index + 1;
    }

    public static int getAlphaFade(float partial) {
        float counterProgress = (ticks + partial) % (PackMenuClient.slideshowDuration + PackMenuClient.slideshowTransition) - PackMenuClient.slideshowDuration;

        float durationTeiler = 1F / PackMenuClient.slideshowTransition;
        float alpha = durationTeiler * counterProgress;
        return (int) (255 * alpha);
    }

}