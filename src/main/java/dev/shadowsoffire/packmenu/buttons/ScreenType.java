package dev.shadowsoffire.packmenu.buttons;

import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.serialization.Codec;

import dev.shadowsoffire.packmenu.gui.SupporterScreen;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.neoforged.neoforge.client.gui.ModListScreen;

public enum ScreenType {
    SINGLEPLAYER(SelectWorldScreen::new),
    MULTIPLAYER(JoinMultiplayerScreen::new),
    MODS(ModListScreen::new),
    LANGUAGE(m -> new LanguageSelectScreen(m, Minecraft.getInstance().options, Minecraft.getInstance().getLanguageManager())),
    OPTIONS(m -> new OptionsScreen(m, Minecraft.getInstance().options)),
    ACCESSIBILITY(m -> new AccessibilityOptionsScreen(m, Minecraft.getInstance().options)),
    RESOURCE_PACKS(ScreenType::makePackSelection),
    SUPPORTERS(SupporterScreen::new),
    REALMS(RealmsMainScreen::new);

    public static final Codec<ScreenType> CODEC = PlaceboCodecs.enumCodec(ScreenType.class);

    protected final Function<Screen, Screen> supplier;

    private ScreenType(Function<Screen, Screen> supplier) {
        this.supplier = supplier;
    }

    public Screen createNewScreen(Screen parent) {
        return this.supplier.apply(parent);
    }

    public static PackSelectionScreen makePackSelection(Screen main) {
        Minecraft mc = Minecraft.getInstance();
        OptionsScreen optScn = new OptionsScreen(main, mc.options);
        optScn.init(mc, 40, 40);

        Consumer<PackRepository> applyPacks = repo -> {
            mc.options.updateResourcePacks(repo);
            mc.setScreen(main);
        };

        return new PackSelectionScreen(mc.getResourcePackRepository(), applyPacks, mc.getResourcePackDirectory(), Component.translatable("resourcePack.title"));
    }

}
