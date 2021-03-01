package shadows.menu.buttons;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

public enum ScreenType implements Function<Screen, Screen> {
	SINGLEPLAYER(WorldSelectionScreen::new),
	MULTIPLAYER(MultiplayerScreen::new),
	MODS(ModListScreen::new),
	LANGUAGE(
			m -> new LanguageScreen(m, Minecraft.getInstance().gameSettings, Minecraft.getInstance().getLanguageManager())),
	OPTIONS(m -> new OptionsScreen(m, Minecraft.getInstance().gameSettings)),
	ACCESSIBILITY(m -> new AccessibilityScreen(m, Minecraft.getInstance().gameSettings)),
	RESOURCE_PACKS(new RPScreenFunction());

	protected Function<Screen, Screen> supplier;

	private ScreenType(Function<Screen, Screen> supplier) {
		this.supplier = supplier;
	}

	@Override
	public Screen apply(Screen t) {
		return supplier.apply(t);
	}

	public static class RPScreenFunction implements Function<Screen, Screen> {

		@Override
		public PackScreen apply(Screen t) {
			Minecraft mc = Minecraft.getInstance();
			OptionsScreen optScn = new OptionsScreen(t, mc.gameSettings);
			optScn.init(mc, 40, 40);
			return new PackScreen(t, mc.getResourcePackList(), optScn::func_241584_a_, mc.getFileResourcePacks(), new TranslationTextComponent("resourcePack.title"));
		}

	}

}
