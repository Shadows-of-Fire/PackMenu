package shadows.menu;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import shadows.menu.atm.ATMMenuScreen;
import shadows.placebo.config.Configuration;

public class PackMenuClient {

	public static boolean enableATMMenu = false;

	public static boolean drawTitle = true;
	public static boolean drawSplash = true;
	public static boolean drawJavaEd = true;
	public static boolean drawForgeInfo = true;
	public static boolean drawPanorama = false;
	public static Offset ssp, smp, mods, custom, options, quit, lang, access, title, javaEd, forgeWarn, splash;
	public static URI customButtonDest = null;

	public static void load() {
		MinecraftForge.EVENT_BUS.register(new PackMenuClient());
		Configuration cfg = new Configuration(PackMenu.MODID);
		enableATMMenu = cfg.getBoolean("All The Mods Menu", "ATM", false, "If the custom ATM Menu is used.  All other config values are ignored when using this option.");
		drawTitle = cfg.getBoolean("Draw Title", "general", true, "If the title (the giant minecraft text) is drawn.");
		drawSplash = cfg.getBoolean("Draw Splash", "general", true, "If the splash text is drawn.");
		drawJavaEd = cfg.getBoolean("Draw Java Edition", "general", true, "If the \"Java Edition\" text is drawn.");
		drawForgeInfo = cfg.getBoolean("Draw Forge Info", "general", true, "If forge information is drawn at the top center.  This includes beta and update warnings.");
		drawPanorama = cfg.getBoolean("Draw Panorama", "general", false, "If the panorama, and it's fade-in, are rendered.  Enabling this disables the use of the background image.");
		ssp = getOffset("Singleplayer", cfg);
		smp = getOffset("Multiplayer", cfg);
		mods = getOffset("Mods", cfg);
		custom = getOffset("Custom Button", cfg);
		options = getOffset("Options", cfg);
		quit = getOffset("Quit Game", cfg);
		lang = getOffset("Language", cfg);
		access = getOffset("Accessibility Options", cfg);
		title = getOffset("Title", cfg);
		javaEd = getOffset("Java Edition Text", cfg);
		forgeWarn = getOffset("Forge Info", cfg);
		splash = getOffset("Splash Text", cfg);
		String url = cfg.getString("Custom URL", "Custom Button", "", "A URL for this button to link to.  (This was previously the realms button.)");
		try {
			customButtonDest = new URL(url).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			PackMenu.LOGGER.error("Invalid URL for custom button.");
		}
		if (cfg.hasChanged()) cfg.save();
	}

	@SubscribeEvent
	public void hijackMenu(GuiOpenEvent e) {
		if (e.getGui().getClass() == MainMenuScreen.class) {
			e.setGui(enableATMMenu ? new ATMMenuScreen() : new ExtendedMenuScreen());
		}
	}

	private static Offset getOffset(String key, Configuration cfg) {
		int x = cfg.getInt("X Offset", key, 0, -1000, 1000, "The X offset for this button.");
		int y = cfg.getInt("Y Offset", key, 0, -1000, 1000, "The Y Offset for this button.");
		return new Offset(x, y);
	}

	public static class Offset {

		public final int x, y;

		public Offset(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

}
