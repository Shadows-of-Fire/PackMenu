package shadows.menu.buttons;

import java.util.function.Function;

import shadows.menu.ExtendedMenuScreen;

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
	DEFAULT_LOGO(s -> s.width / 2, s -> s.height / 4);

	private Function<ExtendedMenuScreen, Integer> xFunc;
	private Function<ExtendedMenuScreen, Integer> yFunc;

	private AnchorPoint(Function<ExtendedMenuScreen, Integer> xFunc, Function<ExtendedMenuScreen, Integer> yFunc) {
		this.xFunc = xFunc;
		this.yFunc = yFunc;
	}

	public int getX(ExtendedMenuScreen scn) {
		return xFunc.apply(scn);
	}

	public int getY(ExtendedMenuScreen scn) {
		return yFunc.apply(scn);
	}
}
