package shadows.menu.buttons;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Util;
import shadows.menu.PackMenuClient;

@SuppressWarnings("deprecation")
public enum ButtonAction {
	CONNECT_TO_SERVER(ai -> { //Data: Server IP (String)
		Minecraft mc = Minecraft.getInstance();
		mc.displayGuiScreen(new ConnectingScreen(mc.currentScreen, mc, (ServerData) ai.getData()));
	}, j -> new ServerData("Minecraft Server", j.get("data").getAsString(), false)),
	LOAD_WORLD(ai -> { //Data: World Name (String)

	}, j -> j.get("data").getAsString()),
	RELOAD(ai -> { //Data: null
		PackMenuClient.loadConfig();
		Minecraft.getInstance().reloadResources();
	}, j -> null),
	OPEN_GUI(ai -> { //Data: ScreenType (String)
		Minecraft.getInstance().displayGuiScreen(((ScreenType) ai.getData()).apply(Minecraft.getInstance().currentScreen));
	}, j -> ScreenType.valueOf(j.get("data").getAsString().toUpperCase(Locale.ROOT))),
	OPEN_URL(ai -> { //Data: Link (URI)
		Util.getOSType().openURI((URI) ai.getData());
	}, j -> {
		try {
			return new URI(j.get("data").getAsString());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}),
	QUIT(ai -> { //Data: null
		Minecraft.getInstance().shutdown();
	}, j -> null);

	private Consumer<ActionInstance> action;
	private Function<JsonObject, Object> reader;

	/**
	 * Creates a button action.
	 * @param action The action instance containing this action.  It is assumed have this action as it's specified action
	 * and any related data required in the data object.
	 */
	ButtonAction(Consumer<ActionInstance> action, Function<JsonObject, Object> reader) {
		this.action = action;
		this.reader = reader;
	}

	public void onPress(ActionInstance button) {
		action.accept(button);
	}

	public Object readData(JsonObject json) {
		return reader.apply(json);
	}

}
