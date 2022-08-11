package shadows.packmenu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;

@Mod(PackMenu.MODID)
public class PackMenu {

	public static final String MODID = "packmenu";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public PackMenu() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			new PackMenuClient().load();
		} else LOGGER.error("Running on a dedicated server, disabling mod.");
		ModLoadingContext.get().registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
	}

}
