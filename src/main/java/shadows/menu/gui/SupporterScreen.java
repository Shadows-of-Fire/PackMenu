package shadows.menu.gui;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import shadows.menu.PackMenu;
import shadows.menu.PackMenuClient;
import shadows.menu.reload.Supporters;

@EventBusSubscriber(value = Dist.CLIENT, modid = PackMenu.MODID)
public class SupporterScreen extends Screen {

	public static final String PM_PATREON = "https://www.patreon.com/Shadows_of_Fire?fan_landing=true";

	protected final Screen parent;
	protected static final Int2IntMap colorFades = new Int2IntOpenHashMap();
	protected static long ticks = 0;
	protected final Random rand = new Random();
	protected final ITextComponent patreon, patreon2, patreon3;

	public SupporterScreen(Screen parent) {
		super(new TranslationTextComponent("packmenu.supporters"));
		this.parent = parent;
		colorFades.defaultReturnValue(-1);
		patreon = new TranslationTextComponent("packmenu.support.modpack").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, PackMenuClient.patreonUrl)));
		patreon2 = new TranslationTextComponent("packmenu.support").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, PM_PATREON)));
		patreon3 = new TranslationTextComponent("packmenu.support1");
	}

	@Override
	protected void init() {
		this.addButton(new Button(5, this.height - 25, 40, 20, DialogTexts.GUI_BACK, (p_213056_1_) -> {
			this.minecraft.setScreen(this.parent);
		}));
	}

	protected boolean isBigLinkHovered(int mouseX, int mouseY) {
		int texWidth = this.font.width(patreon);
		return mouseY > (this.height - font.lineHeight * 2 - 5) && mouseY < (this.height - 5) && mouseX > (this.width / 2 - texWidth) && mouseX < (this.width / 2 + texWidth);
	}

	protected boolean isTinyLinkHovered(int mouseX, int mouseY) {
		int texWidth = this.font.width(patreon3);
		return mouseY > (this.height - font.lineHeight * 2 - 5) && mouseY < (this.height - 5) && mouseX > (this.width - texWidth - 5) && mouseX < (this.width - 5);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);

		matrixStack.scale(2, 2, 2);
		font.drawShadow(matrixStack, this.title, (this.width / 2 - font.width(this.title)) / 2, 5, 0xEEEEEE);
		font.drawShadow(matrixStack, this.patreon, (this.width / 2 - font.width(this.patreon)) / 2, (this.height - font.lineHeight * 2 - 5) / 2, isBigLinkHovered(mouseX, mouseY) ? TextFormatting.RED.getColor() : TextFormatting.DARK_RED.getColor());
		matrixStack.scale(0.5F, 0.5F, 0.5F);

		int color = isTinyLinkHovered(mouseX, mouseY) ? 0x33BB33 : 0x009900;
		font.drawShadow(matrixStack, this.patreon2, (this.width - font.width(this.patreon2) / 2 - font.width(this.patreon3) / 2) - 5, (this.height - font.lineHeight * 2 - 5), color);
		font.drawShadow(matrixStack, this.patreon3, (this.width - font.width(this.patreon3)) - 5, (this.height - font.lineHeight * 2 - 5) + font.lineHeight, color);

		List<String> names = Supporters.INSTANCE.getSupporters();
		int width = (int) (this.width * 0.66);
		int strWidth = 0;
		int renders = 3;
		ITextComponent rendering = new StringTextComponent("");
		for (int i = 0; i < names.size(); i++) {
			String s = names.get(i);
			rendering = new TranslationTextComponent("%s %s", rendering, comp(i, s + "    "));
			if ((strWidth = font.width(rendering)) >= width) {
				font.drawShadow(matrixStack, rendering, this.width / 2 - strWidth / 2, renders++ * (2 + font.lineHeight), 0xCCCCCC);
				rendering = new StringTextComponent("");
			} else if (i == names.size() - 1) {
				font.drawShadow(matrixStack, rendering, this.width / 2 - strWidth / 2, renders++ * (2 + font.lineHeight), 0xCCCCCC);
			}
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderBackground(MatrixStack matrixStack, int vOffset) {
		this.renderDirtBackground(vOffset);
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (isBigLinkHovered((int) Math.round(pMouseX), (int) Math.round(pMouseY))) return this.handleComponentClicked(patreon.getStyle());
		if (isTinyLinkHovered((int) Math.round(pMouseX), (int) Math.round(pMouseY))) return this.handleComponentClicked(patreon2.getStyle());
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	ITextComponent comp(int index, String s) {
		StringTextComponent comp = new StringTextComponent(s);
		if (colorFades.get(index) != -1) {
			int color = colorFades.get(index);
			return comp.withStyle(Style.EMPTY.withColor(Color.fromRgb(color)));
		} else {
			if (rand.nextInt(1600) == 0) {
				colorFades.put(index, 0x9999FF);
			}
			return comp;
		}
	}

	static int step(int color) {
		return color + 0x010100 - 1;
	}

	@SubscribeEvent
	public static void tickFades(ClientTickEvent e) {
		if (e.phase == Phase.END) {
			colorFades.replaceAll((k, v) -> v == -1 || step(v) == 0xCCCCCC ? -1 : step(v));
		}
	}

}
