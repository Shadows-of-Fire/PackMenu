package dev.shadowsoffire.packmenu.buttons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public record ButtonIcon(ResourceLocation texture, int width, int height) {

    public static final Codec<ButtonIcon> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        ResourceLocation.CODEC.fieldOf("texture").forGetter(ButtonIcon::texture),
        Codec.intRange(0, Short.MAX_VALUE).fieldOf("width").forGetter(ButtonIcon::width),
        Codec.intRange(0, Short.MAX_VALUE).fieldOf("height").forGetter(ButtonIcon::height))
        .apply(inst, ButtonIcon::new));

}
