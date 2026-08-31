package xox.labvorty.vortylib.data.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import xox.labvorty.vortylib.gui.screen.ModListScreen;
import xox.labvorty.vortylib.gui.widget.ThemedButton;

@EventBusSubscriber(modid = "vortylib", value = Dist.CLIENT)
public class MainMenuButtonHandler {
    private static final int SIZE = 20;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        if (screen instanceof TitleScreen) {
            int x = screen.width / 2 + 104;
            int y = screen.height / 4 + 56;
            event.addListener(buildButton(x, y));
        } else if (screen instanceof PauseScreen) {
            int x = screen.width / 2 + 111;
            int y = screen.height / 4 + 50;

            event.addListener(buildButton(x, y));
        }
    }

    private static ThemedButton buildButton(int x, int y) {
        return ThemedButton.builder(Component.literal("V"), b -> {
            Minecraft minecraft = Minecraft.getInstance();

            minecraft.setScreen(new ModListScreen(minecraft.screen));
        }).bounds(x, y, SIZE, SIZE).build();
    }
}