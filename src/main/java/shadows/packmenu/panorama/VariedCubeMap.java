package shadows.packmenu.panorama;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import shadows.packmenu.PackMenuClient;

public class VariedCubeMap extends CubeMap {

	private final ResourceLocation[][] locations = new ResourceLocation[10][6];

	public VariedCubeMap(ResourceLocation texture) {
		super(texture);

		for (int i = 0; i < 6; ++i) {
			this.locations[0][i] = new ResourceLocation(texture.getNamespace(), texture.getPath() + '_' + i + ".png");
		}

		for (int variation = 1; variation < 10; variation++) {
			for (int i = 0; i < 6; ++i) {
				this.locations[variation][i] = new ResourceLocation(texture.getNamespace(), texture.getPath() + variation + '_' + i + ".png");
			}
		}
	}

	public void setVariation(int variation) {
		super.images = this.locations[variation];
	}

	@Override
	public CompletableFuture<Void> preload(TextureManager texMngr, Executor backgroundExecutor) {
		CompletableFuture<?>[] completablefuture = new CompletableFuture[PackMenuClient.panoramaVariations * 6];

		for (int i = 0; i < PackMenuClient.panoramaVariations; i++) {
			for (int j = 0; j < 6; j++) {
				completablefuture[i * 6 + j] = texMngr.preload(this.locations[i][j], backgroundExecutor);
			}
		}

		return CompletableFuture.allOf(completablefuture);
	}
}
