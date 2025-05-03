package dev.shadowsoffire.packmenu.reload;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import dev.shadowsoffire.packmenu.PackMenu;
import dev.shadowsoffire.packmenu.buttons.JsonButton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class ButtonManager extends SimpleJsonResourceReloadListener {

    public static final Gson GSON = new GsonBuilder().create();

    protected Map<ResourceLocation, JsonButton> buttons = new TreeMap<>();

    public ButtonManager() {
        super(GSON, "buttons");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager mgr, ProfilerFiller profiler) {
        this.buttons.clear();
        for (Entry<ResourceLocation, JsonElement> obj : objects.entrySet()) {
            try {
                JsonButton btn = JsonButton.CODEC.decode(JsonOps.INSTANCE, obj.getValue()).getOrThrow().getFirst();
                this.buttons.put(obj.getKey(), btn);
            }
            catch (Exception e) {
                PackMenu.LOGGER.error("Failed to load button {}.", obj.getKey());
                PackMenu.LOGGER.error("Exception: ", e);
            }
        }
        PackMenu.LOGGER.info("Loaded {} buttons from resources.", this.buttons.size());
    }

    public Collection<JsonButton> getButtons() {
        return this.buttons.values();
    }

}
