package shadows.packmenu.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import shadows.packmenu.panorama.VariedRenderSkyboxCube;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

	@Shadow
	public static CubeMap CUBE_MAP;

	@Inject(at = @At("TAIL"), method = "<clinit>")
	private static void packmenu_init(CallbackInfo ci) {
		CUBE_MAP = new VariedRenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
	}
}