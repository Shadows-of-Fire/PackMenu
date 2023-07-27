package dev.shadowsoffire.packmenu.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.shadowsoffire.packmenu.ExtendedMenuScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Redirect(at = @At(value = "FIELD", ordinal = 3), method = "preloadResources(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;")
    private static CubeMap getCUBE_MAP() {
        return ExtendedMenuScreen.VARIED_CUBE_MAP;
    }
}
