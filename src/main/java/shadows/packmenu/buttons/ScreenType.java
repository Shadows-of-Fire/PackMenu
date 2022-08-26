package shadows.packmenu.buttons;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.ModListScreen;
import shadows.packmenu.gui.SupporterScreen;

public enum ScreenType implements Function<Screen, Screen> {
	SINGLEPLAYER(SelectWorldScreen::new),
	MULTIPLAYER(JoinMultiplayerScreen::new),
	MODS(ModListScreen::new),
	LANGUAGE(
			m -> new LanguageSelectScreen(m, Minecraft.getInstance().options, Minecraft.getInstance().getLanguageManager())),
	OPTIONS(m -> new OptionsScreen(m, Minecraft.getInstance().options)),
	ACCESSIBILITY(m -> new AccessibilityOptionsScreen(m, Minecraft.getInstance().options)),
	RESOURCE_PACKS(new RPScreenFunction()),
	SUPPORTERS(SupporterScreen::new);

	protected Function<Screen, Screen> supplier;

	private ScreenType(Function<Screen, Screen> supplier) {
		this.supplier = supplier;
	}

	@Override
	public Screen apply(Screen t) {
		return this.supplier.apply(t);
	}

	public static class RPScreenFunction implements Function<Screen, Screen> {

		@Override
		public PackSelectionScreen apply(Screen t) {
			Minecraft mc = Minecraft.getInstance();
			OptionsScreen optScn = new OptionsScreen(t, mc.options);
			optScn.init(mc, 40, 40);
			return new PackSelectionScreen(t, mc.getResourcePackRepository(), optScn::updatePackList, mc.getResourcePackDirectory(), Component.translatable("resourcePack.title"));
		}

	}

}
