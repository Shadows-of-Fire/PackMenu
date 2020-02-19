package shadows.menu.slideshow;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import shadows.menu.ExtendedMenuScreen;
import shadows.menu.PackMenu;
import shadows.menu.PackMenuClient;

@EventBusSubscriber(value = Dist.CLIENT, modid = PackMenu.MODID)
public class Slideshow {

	private static long ticks = 0;
	private static int index = 0;
	private static boolean fading = false;

	public static void render(ExtendedMenuScreen screen, float partialTicks) {
		Minecraft mc = screen.getMinecraft();
		mc.getTextureManager().bindTexture(PackMenuClient.slideshowTextures.get(index));
		Screen.blit(0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128);

		if (fading) {
			GlStateManager.enableBlend();
			mc.getTextureManager().bindTexture(PackMenuClient.slideshowTextures.get(nextIndex()));

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, getAlphaFade(partialTicks));
			Screen.blit(0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128);

			GlStateManager.disableBlend();

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@SubscribeEvent
	public static void tick(ClientTickEvent e) {
		if (e.phase == Phase.END && Minecraft.getInstance().currentScreen instanceof ExtendedMenuScreen) {
			ticks++;
			boolean wasFading = fading;
			fading = (ticks % (PackMenuClient.slideshowDuration + PackMenuClient.slideshowTransition)) > PackMenuClient.slideshowDuration;
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

	public static float getAlphaFade(float partial) {
		float counterProgress = ((ticks + partial) % (PackMenuClient.slideshowDuration + PackMenuClient.slideshowTransition)) - PackMenuClient.slideshowDuration;

		float durationTeiler = 1F / PackMenuClient.slideshowTransition;
		float alpha = durationTeiler * counterProgress;
		return alpha;
	}

}
