package dev.shadowsoffire.packmenu;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.shadowsoffire.packmenu.buttons.AnchorPoint;
import dev.shadowsoffire.packmenu.buttons.ButtonAction;
import dev.shadowsoffire.packmenu.logo.Logo;
import dev.shadowsoffire.packmenu.reload.ButtonManager;
import dev.shadowsoffire.packmenu.reload.Supporters;
import dev.shadowsoffire.packmenu.slideshow.Slideshow;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.util.RunnableReloader;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources.PathResourcesSupplier;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PackMenu.MODID, dist = Dist.CLIENT)
public class PackMenu {

    public static final String MODID = "packmenu";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final File FOLDER_PACK = new File(FMLPaths.GAMEDIR.get().toFile(), "packmenu/resources");
    public static final ButtonManager BUTTON_MANAGER = new ButtonManager();

    public static boolean drawTitle = true;
    public static boolean drawSplash = true;
    public static boolean drawForgeInfo = true;
    public static boolean drawPanorama = false;

    public static Offset title, /* javaEd, */ forgeWarn, splash;
    public static float splashRotation = -20.0F;
    public static int splashColor = 16776960 | 255 << 24;
    public static AnchorPoint splashAnchor = AnchorPoint.MIDDLE_CENTER;

    public static List<ResourceLocation> slideshowTextures;
    public static int slideshowDuration = 200;
    public static int slideshowTransition = 20;
    public static boolean slideshowRepeat = true;
    public static boolean slideshow = false;
    public static boolean randomSlideshow = false;

    public static boolean panoramaFade = false;
    public static float panoramaSpeed = 1;
    public static int panoramaVariations = 1;
    public static String patreonUrl = "https://www.patreon.com/Shadows_of_Fire?fan_landing=true";
    public static Logo logo = null;

    public PackMenu() {
        NeoForge.EVENT_BUS.addListener(this::hijackMenu);
        loadConfig();
        if (!FOLDER_PACK.exists()) {
            this.getClass().getResource("/builtin_resource_pack").getPath();
            try {
                Path src = Paths.get(this.getClass().getResource("/builtin_resource_pack").toURI());
                Files.walkFileTree(src, new SimpleFileVisitor<Path>(){
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        File output = new File(FOLDER_PACK, path.subpath(1, path.getNameCount()).toString());
                        output.getParentFile().mkdirs();
                        Files.copy(path, output.toPath());
                        return FileVisitResult.CONTINUE;
                    };
                });
            }
            catch (IOException | URISyntaxException e) {
                PackMenu.LOGGER.error("Failed to copy default resouces into the game directory!");
                e.printStackTrace();
            }
        }

        PackLocationInfo info = new PackLocationInfo(PackMenu.MODID, Component.literal("Packmenu Builtin"), PackSource.BUILT_IN, Optional.empty());
        PackSelectionConfig config = new PackSelectionConfig(true, Pack.Position.TOP, true);

        Minecraft.getInstance().getResourcePackRepository().addPackFinder(map -> {
            Pack.ResourcesSupplier resources = new PathResourcesSupplier(FOLDER_PACK.toPath());
            final Pack packInfo = Pack.readMetaAndCreate(info, resources, PackType.CLIENT_RESOURCES, config);
            if (packInfo == null) {
                PackMenu.LOGGER.error("Failed to load resource pack, some things may not work.");
                return;
            }
            map.accept(packInfo);
        });

        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(BUTTON_MANAGER);
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(new RunnableReloader(() -> {
            int var = ThreadLocalRandom.current().nextInt(panoramaVariations);
            ExtendedMenuScreen.VARIED_CUBE_MAP.setVariation(var);
        }));
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(Supporters.INSTANCE);
        ButtonAction.registerCodecs();
    }

    @SubscribeEvent
    public void hijackMenu(ScreenEvent.Opening e) {
        if (e.getScreen() != null && e.getScreen().getClass() == TitleScreen.class) {
            e.setNewScreen(new ExtendedMenuScreen(panoramaFade));
        }
    }

    public static void loadConfig() {
        Configuration cfg = new Configuration(PackMenu.MODID);
        drawTitle = cfg.getBoolean("Draw Title", "general", true, "If the title (the giant minecraft text) is drawn.");
        drawSplash = cfg.getBoolean("Draw Splash", "general", true, "If the splash text is drawn.");
        drawForgeInfo = cfg.getBoolean("Draw Forge Info", "general", true, "If forge information is drawn at the top center.  This includes beta and update warnings.");
        drawPanorama = cfg.getBoolean("Draw Panorama", "general", false, "If the vanilla panorama, and it's fade-in, are rendered.  Enabling this disables the use of the custom background options.");
        title = getOffset("Title", AnchorPoint.TITLE, cfg);
        forgeWarn = getOffset("Forge Info", AnchorPoint.FORGE, cfg);
        splash = getOffset("Splash Text", AnchorPoint.SPLASH, cfg);
        splashRotation = cfg.getFloat("Rotation", "splash text", splashRotation, -360F, 360F, "The rotation value of the splash text.");
        splashColor = cfg.getInt("Color", "splash text", splashColor, -Integer.MAX_VALUE, Integer.MAX_VALUE, "The color of the splash text.");
        logo = Logo.read(cfg);
        
        String[] slideshow = cfg.getStringList("Textures", "slideshow", new String[0], "The list of textures to be displayed on the slideshow.  If empty, the slideshow is ignored.");
        slideshowTextures = new ArrayList<>();
        for (String s : slideshow) {
            try {
                slideshowTextures.add(ResourceLocation.parse(s));
            }
            catch (ResourceLocationException e) {
                e.printStackTrace();
            }
        }
        slideshowDuration = cfg.getInt("Duration", "slideshow", 200, 1, 1000000, "How long between slideshow transitions.");
        slideshowTransition = cfg.getInt("Transition Duration", "slideshow", 20, 1, 1000000, "How long the slideshow transition lasts.");
        slideshowRepeat = cfg.getBoolean("Repeat", "slideshow", true, "If the slideshow will be repeated when the final frame hits. Set to false to only do a single run.");
        randomSlideshow = cfg.getBoolean("Random", "slideshow", false, "If the order of slides in the slideshow will be randomized. Requires slideshow repeat to be enabled.");
        
        panoramaFade = cfg.getBoolean("Panorama Fade In", "general", panoramaFade, "If the Panorama has a fade-in effect.");
        panoramaSpeed = cfg.getFloat("Panorama Speed", "general", 1, 0.01F, 100F, "A multiplier on panorama speed.");
        panoramaVariations = cfg.getInt("Panorama Variations", "general", panoramaVariations, 1, 10,
            "The number of variations of panorama that exist.  Panorama files other than the original set must have the form panorama<y>_<z>.png.  For example the first file of varation #2 would be panorama1_0.png");
        int var = ThreadLocalRandom.current().nextInt(panoramaVariations);
        ExtendedMenuScreen.VARIED_CUBE_MAP.setVariation(var);
        PackMenu.slideshow = !slideshowTextures.isEmpty();
        Slideshow.reset();
        patreonUrl = cfg.getString("Patreon Url", "support", patreonUrl, "The URL that the link on the supporters page goes to.");
        if (cfg.hasChanged()) cfg.save();
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
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
            return this.anchor.getX(scn) + this.x;
        }

        public int getY(ExtendedMenuScreen scn) {
            return this.anchor.getY(scn) + this.y;
        }

    }

}
