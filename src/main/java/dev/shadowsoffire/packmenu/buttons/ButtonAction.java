package dev.shadowsoffire.packmenu.buttons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.packmenu.PackMenu;
import dev.shadowsoffire.placebo.codec.CodecMap;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

/**
 * Jsonified {@link OnPress} handlers for {@link JsonButton}.
 */
public interface ButtonAction extends CodecProvider<ButtonAction>, OnPress {

    public static final CodecMap<ButtonAction> CODEC = new CodecMap<>("Button Action");

    public static void registerCodecs() {
        register("connect_to_server", ConnectToServer.CODEC);
        register("reload", Reload.CODEC);
        register("open_screen", OpenScreen.CODEC);
        register("open_url", OpenUrl.CODEC);
        register("quit", Quit.CODEC);
        register("none", None.CODEC);
    }

    private static void register(String id, Codec<? extends ButtonAction> codec) {
        CODEC.register(PackMenu.loc(id), codec);
    }

    /**
     * Opens a server as specified by `ip_address`.
     */
    public static record ConnectToServer(String ipAddress) implements ButtonAction {

        public static Codec<ConnectToServer> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("ip_address").forGetter(ConnectToServer::ipAddress))
            .apply(inst, ConnectToServer::new));

        @Override
        public Codec<ConnectToServer> getCodec() {
            return CODEC;
        }

        @Override
        public void onPress(Button button) {
            Minecraft mc = Minecraft.getInstance();
            ServerData data = getOrCreateServerData(this.ipAddress());
            ServerAddress addr = ServerAddress.parseString(this.ipAddress());
            ConnectScreen.startConnecting(mc.screen, mc, addr, data, false, null);
        }

        public static ServerData getOrCreateServerData(String ip) {
            JoinMultiplayerScreen scn = new JoinMultiplayerScreen(Minecraft.getInstance().screen);
            scn.init(Minecraft.getInstance(), 0, 0);
            ServerList list = scn.getServers();
            for (int i = 0; i < list.size(); i++) {
                ServerData data = list.get(i);
                if (data.ip.equals(ip)) return data;
            }
            ServerData data = new ServerData("Packmenu Managed Server", ip, ServerData.Type.OTHER);
            list.add(data, true);
            list.save();
            return data;
        }
    }

    /**
     * Reloads the packmenu config and all resource packs.
     */
    public static record Reload() implements ButtonAction {

        public static Codec<Reload> CODEC = Codec.unit(Reload::new);

        @Override
        public Codec<Reload> getCodec() {
            return CODEC;
        }

        @Override
        public void onPress(Button button) {
            PackMenu.loadConfig();
            Minecraft.getInstance().reloadResourcePacks();
        }
    }

    /**
     * Opens a screen as specified by `screen`.
     * 
     * @see {@link ScreenType}
     */
    public static record OpenScreen(ScreenType type) implements ButtonAction {

        public static Codec<OpenScreen> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ScreenType.CODEC.fieldOf("screen").forGetter(OpenScreen::type))
            .apply(inst, OpenScreen::new));

        @Override
        public Codec<OpenScreen> getCodec() {
            return CODEC;
        }

        @Override
        public void onPress(Button button) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(this.type.createNewScreen(mc.screen));
        }
    }

    /**
     * Opens a URL as specified by `url`.
     */
    public static record OpenUrl(String url) implements ButtonAction {

        public static Codec<OpenUrl> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("url").forGetter(OpenUrl::url))
            .apply(inst, OpenUrl::new));

        @Override
        public Codec<OpenUrl> getCodec() {
            return CODEC;
        }

        @Override
        public void onPress(Button button) {
            Util.getPlatform().openUri(this.url);
        }
    }

    /**
     * Quits the game.
     */
    public static record Quit() implements ButtonAction {

        public static Codec<Quit> CODEC = Codec.unit(Quit::new);

        @Override
        public Codec<Quit> getCodec() {
            return CODEC;
        }

        @Override
        public void onPress(Button button) {
            Minecraft.getInstance().stop();
        }
    }

    /**
     * Performs no action.
     */
    public static record None() implements ButtonAction {

        public static Codec<None> CODEC = Codec.unit(None::new);

        @Override
        public Codec<None> getCodec() {
            return CODEC;
        }

        @Override
        public void onPress(Button button) {
            // No-op
        }
    }

}
