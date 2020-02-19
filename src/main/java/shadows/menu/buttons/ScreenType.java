package shadows.menu.buttons;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraftforge.fml.client.gui.GuiModList;

public enum ScreenType implements Function<Screen, Screen> {
	SINGLEPLAYER(WorldSelectionScreen::new),
	MULTIPLAYER(MultiplayerScreen::new),
	MODS(GuiModList::new),
	LANGUAGE(
			m -> new LanguageScreen(m, Minecraft.getInstance().gameSettings, Minecraft.getInstance().getLanguageManager())),
	OPTIONS(m -> new OptionsScreen(m, Minecraft.getInstance().gameSettings)),
	ACCESSIBILITY(m -> new AccessibilityScreen(m, Minecraft.getInstance().gameSettings));

	protected Function<Screen, Screen> supplier;

	private ScreenType(Function<Screen, Screen> supplier) {
		this.supplier = supplier;
	}

	@Override
	public Screen apply(Screen t) {
		return supplier.apply(t);
	}

}
