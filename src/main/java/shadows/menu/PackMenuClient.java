package shadows.menu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import shadows.menu.buttons.AnchorPoint;
import shadows.menu.logo.Logo;
import shadows.menu.panorama.VariedRenderSkyboxCube;
import shadows.menu.reload.ButtonManager;
import shadows.menu.slideshow.Slideshow;
import shadows.placebo.config.Configuration;
import shadows.placebo.util.RunnableReloader;

public class PackMenuClient {

	public static final File RESOURCE_PACK = new File(FMLPaths.GAMEDIR.get().toFile(), "packmenu/resources.zip");
	public static final File FOLDER_PACK = new File(FMLPaths.GAMEDIR.get().toFile(), "packmenu/resources");
	public static final ButtonManager BUTTON_MANAGER = new ButtonManager();

	static {
		MainMenuScreen.PANORAMA_RESOURCES = new VariedRenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
	}

	public static final VariedRenderSkyboxCube PANORAMA_RESOURCES = (VariedRenderSkyboxCube) MainMenuScreen.PANORAMA_RESOURCES;
	public static boolean drawTitle = true;
	public static boolean drawSplash = true;
	public static boolean drawJavaEd = true;
	public static boolean drawForgeInfo = true;
	public static boolean drawPanorama = false;
	public static Offset title, javaEd, forgeWarn, splash;
	public static boolean folderPack = false;
	public static float splashRotation = -20.0F;
	public static int splashColor = 16776960 | (255 << 24);
	public static AnchorPoint splashAnchor = AnchorPoint.MIDDLE_CENTER;
	public static List<ResourceLocation> slideshowTextures;
	public static int slideshowDuration = 200;
	public static int slideshowTransition = 20;
	public static boolean slideshow = false;
	public static boolean panoramaFade = false;
	public static float panoramaSpeed = 1;
	public static int panoramaVariations = 1;

	public static Logo logo = null;

	public void load() {
		MinecraftForge.EVENT_BUS.addListener(this::hijackMenu);
		loadConfig();
		if (!folderPack && !RESOURCE_PACK.exists()) {
			try (InputStream stream = this.getClass().getResourceAsStream("/resources.zip")) {
				RESOURCE_PACK.getParentFile().mkdir();
				RESOURCE_PACK.createNewFile();
				byte[] buffer = new byte[600000];
				FileOutputStream outStream = new FileOutputStream(RESOURCE_PACK);
				int i = 0;
				while ((i = stream.read(buffer)) != -1) {
					outStream.write(buffer, 0, i);
				}
				outStream.close();
			} catch (IOException e) {
				PackMenu.LOGGER.error("Failed to copy default resouces into the game directory!");
			}
		} else if (folderPack && !FOLDER_PACK.exists()) {
			try (InputStream stream = this.getClass().getResourceAsStream("/resources.zip")) {
				boolean existed = RESOURCE_PACK.exists();
				if (!existed) {
					RESOURCE_PACK.getParentFile().mkdir();
					RESOURCE_PACK.createNewFile();
					byte[] buffer = new byte[600000];
					FileOutputStream outStream = new FileOutputStream(RESOURCE_PACK);
					int i = 0;
					while ((i = stream.read(buffer)) != -1) {
						outStream.write(buffer, 0, i);
					}
					outStream.close();
				}
				FOLDER_PACK.mkdir();
				try (ZipFile zipFile = new ZipFile(RESOURCE_PACK)) {
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						File entryDestination = new File(FOLDER_PACK, entry.getName());
						if (entry.isDirectory()) {
							entryDestination.mkdirs();
						} else {
							entryDestination.getParentFile().mkdirs();
							try (InputStream in = zipFile.getInputStream(entry);
									OutputStream out = new FileOutputStream(entryDestination)) {
								IOUtils.copy(in, out);
							}
						}
					}
				} catch (IOException e) {
					PackMenu.LOGGER.error("Failed to unzip default resouces into the game directory!");
				}
				if (!existed) RESOURCE_PACK.delete(); //Remove zip if this is a folder pack and we just generated it.
			} catch (IOException e) {
				PackMenu.LOGGER.error("Failed to copy default resouces into the game directory!");
			}
		}

		Minecraft.getInstance().getResourcePackList().addPackFinder(new IPackFinder() {
			@Override
			public void findPacks(Consumer<ResourcePackInfo> map, IFactory factory) {
				final ResourcePackInfo packInfo = ResourcePackInfo.createResourcePack(PackMenu.MODID, true, () -> folderPack ? new FolderPack(FOLDER_PACK) : new FilePack(RESOURCE_PACK), factory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILTIN);
				if (packInfo == null) {
					PackMenu.LOGGER.error("Failed to load resource pack, some things may not work.");
					return;
				}
				map.accept(packInfo);
			}
		});

		((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(BUTTON_MANAGER);
		((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new RunnableReloader(() -> {
			int var = ThreadLocalRandom.current().nextInt(panoramaVariations);
			PANORAMA_RESOURCES.setVariation(var);
		}));
	}

	@SubscribeEvent
	public void hijackMenu(GuiOpenEvent e) {
		if (e.getGui() != null && e.getGui().getClass() == MainMenuScreen.class) {
			e.setGui(new ExtendedMenuScreen(panoramaFade));
		}
	}

	public static void loadConfig() {
		Configuration cfg = new Configuration(PackMenu.MODID);
		drawTitle = cfg.getBoolean("Draw Title", "general", true, "If the title (the giant minecraft text) is drawn.");
		drawSplash = cfg.getBoolean("Draw Splash", "general", true, "If the splash text is drawn.");
		drawJavaEd = cfg.getBoolean("Draw Java Edition", "general", true, "If the \"Java Edition\" text is drawn.");
		drawForgeInfo = cfg.getBoolean("Draw Forge Info", "general", true, "If forge information is drawn at the top center.  This includes beta and update warnings.");
		drawPanorama = cfg.getBoolean("Draw Panorama", "general", false, "If the vanilla panorama, and it's fade-in, are rendered.  Enabling this disables the use of the custom background options.");
		title = getOffset("Title", AnchorPoint.TITLE, cfg);
		javaEd = getOffset("Java Edition Text", AnchorPoint.JAVAED, cfg);
		forgeWarn = getOffset("Forge Info", AnchorPoint.FORGE, cfg);
		splash = getOffset("Splash Text", AnchorPoint.SPLASH, cfg);
		splashRotation = cfg.getFloat("Rotation", "splash text", splashRotation, -360F, 360F, "The rotation value of the splash text.");
		splashColor = cfg.getInt("Color", "splash text", splashColor, -Integer.MAX_VALUE, Integer.MAX_VALUE, "The color of the splash text.");
		folderPack = cfg.getBoolean("Folder Pack", "general", true, "If the resource pack is loaded from /resources instead of /resources.zip");
		logo = Logo.read(cfg);
		String[] slideshow = cfg.getStringList("Textures", "slideshow", new String[0], "The list of textures to be displayed on the slideshow.  If empty, the slideshow is ignored.");
		slideshowTextures = new ArrayList<>();
		for (String s : slideshow) {
			try {
				slideshowTextures.add(new ResourceLocation(s));
			} catch (ResourceLocationException e) {
				e.printStackTrace();
			}
		}
		slideshowDuration = cfg.getInt("Duration", "slideshow", 200, 1, 1000000, "How long between slideshow transitions.");
		slideshowTransition = cfg.getInt("Transition Duration", "slideshow", 20, 1, 1000000, "How long the slideshow transition lasts.");
		panoramaFade = cfg.getBoolean("Panorama Fade In", "general", panoramaFade, "If the Panorama has a fade-in effect.");
		panoramaSpeed = cfg.getFloat("Panorama Speed", "general", 1, 0.01F, 100F, "A multiplier on panorama speed.");
		panoramaVariations = cfg.getInt("Panorama Variations", "general", panoramaVariations, 1, 10, "The number of variations of panorama that exist.  Panorama files other than the original set must have the form panorama<y>_<z>.png.  For example the first file of varation #2 would be panorama1_0.png");
		PackMenuClient.slideshow = !slideshowTextures.isEmpty();
		Slideshow.reset();
		if (cfg.hasChanged()) cfg.save();
	}

	private static Offset getOffset(String key, AnchorPoint def, Configuration cfg) {
		AnchorPoint anchor = AnchorPoint.valueOf(cfg.getString("Anchor Point", key, def.toString(), "The anchor point for this element."));
		int x = cfg.getInt("X Offset", key, 0, -1000, 1000, "The X offset for this element.");
		int y = cfg.getInt("Y Offset", key, 0, -1000, 1000, "The Y Offset for this element.");
		return new Offset(anchor, x, y);
	}

	public static class Offset {

		public final AnchorPoint anchor;
		public final int x, y;

		public Offset(AnchorPoint anchor, int x, int y) {
			this.anchor = anchor;
			this.x = x;
			this.y = y;
		}

		public int getX(ExtendedMenuScreen scn) {
			return anchor.getX(scn) + x;
		}

		public int getY(ExtendedMenuScreen scn) {
			return anchor.getY(scn) + y;
		}

	}

}
