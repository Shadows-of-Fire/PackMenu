package dev.shadowsoffire.packmenu.buttons;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ButtonText(String key, String hoverKey, int xOff, int yOff, int color, int hoverColor, boolean dropShadow) {

    public static final ButtonText EMPTY = new ButtonText("", "", 0, 0, 0xFFFFFF, 0xFFFFFF, false);

    public static final Codec<ButtonText> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.STRING.fieldOf("key").forGetter(ButtonText::key),
        Codec.mapEither(Codec.STRING.fieldOf("hover_key"), Codec.STRING.fieldOf("key")).xmap(Either::unwrap, Either::left).forGetter(ButtonText::hoverKey),
        Codec.INT.optionalFieldOf("x_offset", 0).forGetter(ButtonText::xOff),
        Codec.INT.optionalFieldOf("y_offset", -4).forGetter(ButtonText::yOff),
        Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(ButtonText::color),
        Codec.mapEither(Codec.INT.fieldOf("hover_color"), Codec.INT.optionalFieldOf("color", 0xFFFFFF)).xmap(Either::unwrap, Either::left).forGetter(ButtonText::hoverColor),
        Codec.BOOL.optionalFieldOf("drop_shadow", true).forGetter(ButtonText::dropShadow))
        .apply(inst, ButtonText::new));

}
