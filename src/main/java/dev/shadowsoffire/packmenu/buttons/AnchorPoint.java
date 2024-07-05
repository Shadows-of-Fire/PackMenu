package dev.shadowsoffire.packmenu.buttons;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import dev.shadowsoffire.packmenu.ExtendedMenuScreen;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;

public enum AnchorPoint {
    TOP_LEFT(s -> 0, s -> 0),
    TOP_CENTER(s -> s.width / 2, s -> 0),
    TOP_RIGHT(s -> s.width, s -> 0),
    MIDDLE_LEFT(s -> 0, s -> s.height / 2),
    MIDDLE_CENTER(s -> s.width / 2, s -> s.height / 2),
    MIDDLE_RIGHT(s -> s.width, s -> s.height / 2),
    BOTTOM_LEFT(s -> 0, s -> s.height),
    BOTTOM_CENTER(s -> s.width / 2, s -> s.height),
    BOTTOM_RIGHT(s -> s.width, s -> s.height),
    DEFAULT(s -> s.width / 2, s -> s.height / 4 + 48),
    SPLASH(s -> s.width / 2 + 90, s -> 70),
    TITLE(s -> s.width, s -> 30),
    FORGE(s -> 0, s -> 0);;

    public static final Codec<AnchorPoint> CODEC = PlaceboCodecs.enumCodec(AnchorPoint.class);

    private Function<ExtendedMenuScreen, Integer> xFunc;
    private Function<ExtendedMenuScreen, Integer> yFunc;

    private AnchorPoint(Function<ExtendedMenuScreen, Integer> xFunc, Function<ExtendedMenuScreen, Integer> yFunc) {
        this.xFunc = xFunc;
        this.yFunc = yFunc;
    }

    public int getX(ExtendedMenuScreen scn) {
        return this.xFunc.apply(scn);
    }

    public int getY(ExtendedMenuScreen scn) {
        return this.yFunc.apply(scn);
    }
}
