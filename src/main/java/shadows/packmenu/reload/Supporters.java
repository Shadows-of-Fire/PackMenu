package shadows.packmenu.reload;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import shadows.packmenu.PackMenu;

public class Supporters extends SimplePreparableReloadListener<List<String>> {

	public static final Supporters INSTANCE = new Supporters();

	private static final ResourceLocation TEXT_LOCATION = new ResourceLocation(PackMenu.MODID, "texts/supporters.txt");
	private final List<String> supporters = Lists.newArrayList();

	public Supporters() {
	}

	@Override
	protected List<String> prepare(ResourceManager p_212854_1_, ProfilerFiller p_212854_2_) {
		return Minecraft.getInstance().getResourceManager().getResource(TEXT_LOCATION).<List<String>>map(r -> {
			try (BufferedReader bufferedreader = r.openAsReader()) {
				return bufferedreader.lines().map(String::trim).filter(p_215277_0_ -> (p_215277_0_.hashCode() != 125780783)).collect(Collectors.toList());
			} catch (IOException ioexception) {
				return Collections.emptyList();
			}
		}).orElse(Collections.emptyList());

	}

	@Override
	protected void apply(List<String> p_212853_1_, ResourceManager p_212853_2_, ProfilerFiller p_212853_3_) {
		this.supporters.clear();
		this.supporters.addAll(p_212853_1_);
		Collections.shuffle(this.supporters);
	}

	public List<String> getSupporters() {
		return this.supporters;
	}
}