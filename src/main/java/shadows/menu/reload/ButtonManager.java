package shadows.menu.reload;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import shadows.menu.PackMenu;
import shadows.menu.buttons.JsonButton;

public class ButtonManager extends JsonReloadListener {

	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(JsonButton.class, (JsonDeserializer<JsonButton>) (json, type, ctx) -> {
		return JsonButton.deserialize(json.getAsJsonObject());
	}).setPrettyPrinting().create();

	protected Map<ResourceLocation, JsonButton> buttons = new TreeMap<>();

	public ButtonManager() {
		super(GSON, "buttons");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> objects, IResourceManager mgr, IProfiler profiler) {
		buttons.clear();
		for (Entry<ResourceLocation, JsonObject> obj : objects.entrySet()) {
			try {
				JsonButton btn = GSON.fromJson(obj.getValue(), JsonButton.class);
				buttons.put(obj.getKey(), btn);
			} catch (Exception e) {
				PackMenu.LOGGER.error("Failed to load button {}.", obj.getKey());
				e.printStackTrace();
			}
		}
		PackMenu.LOGGER.info("Loaded {} buttons from resources.", buttons.size());
	}

	public Collection<JsonButton> getButtons() {
		return buttons.values();
	}

}
