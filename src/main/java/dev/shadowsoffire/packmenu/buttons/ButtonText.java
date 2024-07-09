package dev.shadowsoffire.packmenu.buttons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * @param key        Language key of the displayed text
 * @param xOff       X-offset of the displayed text, relative to the center of the button
 * @param yOff       Y-offset of the displayed text, relative to the center of the button
 * @param color      Text color
 * @param dropShadow If the drawn text will have a drop shadow
 */
public record ButtonText(String key, int xOff, int yOff, int color, boolean dropShadow) {

    public static final ButtonText EMPTY = new ButtonText("", 0, 0, 0xFFFFFF, false);

    public static final Codec<ButtonText> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.STRING.fieldOf("key").forGetter(ButtonText::key),
        Codec.INT.optionalFieldOf("x_offset", 0).forGetter(ButtonText::xOff),
        Codec.INT.optionalFieldOf("y_offset", -4).forGetter(ButtonText::yOff),
        Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(ButtonText::color),
        Codec.BOOL.optionalFieldOf("drop_shadow", true).forGetter(ButtonText::dropShadow))
        .apply(inst, ButtonText::new));

}
