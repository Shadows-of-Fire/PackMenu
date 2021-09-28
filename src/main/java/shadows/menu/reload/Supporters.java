package shadows.menu.reload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import shadows.menu.PackMenu;

public class Supporters extends ReloadListener<List<String>> {

	public static final Supporters INSTANCE = new Supporters();

	private static final ResourceLocation TEXT_LOCATION = new ResourceLocation(PackMenu.MODID, "texts/supporters.txt");
	private final List<String> supporters = Lists.newArrayList();

	public Supporters() {
	}

	protected List<String> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
		try (IResource iresource = Minecraft.getInstance().getResourceManager().getResource(TEXT_LOCATION);
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));) {
			return bufferedreader.lines().map(String::trim).filter((p_215277_0_) -> {
				return p_215277_0_.hashCode() != 125780783;
			}).collect(Collectors.toList());
		} catch (IOException ioexception) {
			return Collections.emptyList();
		}
	}

	protected void apply(List<String> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
		this.supporters.clear();
		this.supporters.addAll(p_212853_1_);
		Collections.shuffle(this.supporters);
	}

	public List<String> getSupporters() {
		return this.supporters;
	}
}