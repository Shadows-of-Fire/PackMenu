package dev.shadowsoffire.packmenu.buttons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/**
 * @param texture The texture of the icon
 * @param width   The width, in pixels, of the texture
 * @param height  The height, in pixels, of the texture
 * @param xOff    X-offset of the icon, relative to the center of the button
 * @param yOff    Y-offset of the icon, relative to the center of the button
 */
public record ButtonIcon(ResourceLocation texture, int width, int height, int xOff, int yOff) {

    public static ButtonIcon EMPTY = new ButtonIcon(MissingTextureAtlasSprite.getLocation(), 0, 0, 0, 0);

    public static final Codec<ButtonIcon> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        ResourceLocation.CODEC.fieldOf("texture").forGetter(ButtonIcon::texture),
        Codec.intRange(0, Short.MAX_VALUE).fieldOf("width").forGetter(ButtonIcon::width),
        Codec.intRange(0, Short.MAX_VALUE).fieldOf("height").forGetter(ButtonIcon::height),
        Codec.INT.optionalFieldOf("x_offset", 0).forGetter(ButtonIcon::xOff),
        Codec.INT.optionalFieldOf("y_offset", 0).forGetter(ButtonIcon::yOff))
        .apply(inst, ButtonIcon::new));

}
