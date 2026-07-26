package xox.labvorty.vortylib.compat.iris;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import xox.labvorty.vortylib.configs.ClientConfig;

@EventBusSubscriber
public class IrisCompat {
    @SubscribeEvent
    public static void warn(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer localPlayer = event.getPlayer();

        if (!ModList.get().isLoaded("chaoslib") && ModList.get().isLoaded("iris") && ClientConfig.CHAOSLIB_WARNING.get()) {
            MutableComponent prefix = Component.literal("[VortyLib] ")
                    .withStyle(ChatFormatting.GOLD);

            MutableComponent message = Component.translatable("vortylib.chaoslib_warning")
                    .withStyle(ChatFormatting.YELLOW);

            MutableComponent disableHint = Component.translatable("vortylib.chaoslib_warning.hint")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GRAY)
                            .withItalic(true)
                            .withUnderlined(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vl_disable_chaoslib_warning"))
                    );

            localPlayer.sendSystemMessage(prefix.copy().append(message));
            localPlayer.sendSystemMessage(disableHint);
        }
    }
}
