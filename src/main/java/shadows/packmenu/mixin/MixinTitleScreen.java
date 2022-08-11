package shadows.packmenu.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import shadows.packmenu.panorama.VariedRenderSkyboxCube;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

	@Shadow
	public static CubeMap CUBE_MAP = new VariedRenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
}
