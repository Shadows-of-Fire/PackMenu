package shadows.menu.slideshow;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
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

	public static void render(ExtendedMenuScreen screen, PoseStack stack, float partialTicks) {
		Minecraft mc = screen.getMinecraft();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, PackMenuClient.slideshowTextures.get(index));
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
		GuiComponent.blit(stack, 0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128);

		if (fading) {
			RenderSystem.enableBlend();
			mc.getTextureManager().bindForSetup(PackMenuClient.slideshowTextures.get(nextIndex()));

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, getAlphaFade(partialTicks));
			GuiComponent.blit(stack, 0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128);

			RenderSystem.disableBlend();

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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

	public static float getAlphaFade(float partial) {
		float counterProgress = (ticks + partial) % (PackMenuClient.slideshowDuration + PackMenuClient.slideshowTransition) - PackMenuClient.slideshowDuration;

		float durationTeiler = 1F / PackMenuClient.slideshowTransition;
		float alpha = durationTeiler * counterProgress;
		return alpha;
	}

}
