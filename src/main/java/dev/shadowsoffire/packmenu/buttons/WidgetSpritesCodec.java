package dev.shadowsoffire.packmenu.buttons;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class WidgetSpritesCodec {

    /**
     * Codec for {@link WidgetSprites} which allows specifying a minimal number of sprites.
     * <p>
     * Only the `enabled` key is mandatory, and all values will fallback to this value if absent.
     * If `disabled` is specified, then `disabled_focused` will fallback to that value instead of `enabled`.
     */
    public static final Codec<WidgetSprites> INSTANCE = RecordCodecBuilder.create(inst -> inst.group(
        ResourceLocation.CODEC.fieldOf("enabled").forGetter(WidgetSprites::enabled),
        ResourceLocation.CODEC.optionalFieldOf("disabled").forGetter(opt(WidgetSprites::disabled)),
        ResourceLocation.CODEC.optionalFieldOf("enabled_focused").forGetter(opt(WidgetSprites::enabledFocused)),
        ResourceLocation.CODEC.optionalFieldOf("disabled_focused").forGetter(opt(WidgetSprites::disabledFocused)))
        .apply(inst, WidgetSpritesCodec::create));

    public static Function<WidgetSprites, Optional<ResourceLocation>> opt(Function<WidgetSprites, ResourceLocation> getter) {
        return s -> Optional.of(getter.apply(s));
    }

    public static WidgetSprites create(ResourceLocation enabled, Optional<ResourceLocation> disabled, Optional<ResourceLocation> enabledFocused, Optional<ResourceLocation> disabledFocused) {
        return new WidgetSprites(enabled, disabled.orElse(enabled), enabledFocused.orElse(enabled), disabledFocused.orElse(disabled.orElse(enabled)));
    }

}
