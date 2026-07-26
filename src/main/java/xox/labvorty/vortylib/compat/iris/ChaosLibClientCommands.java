package xox.labvorty.vortylib.compat.iris;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import xox.labvorty.vortylib.configs.ClientConfig;

@EventBusSubscriber
public class ChaosLibClientCommands {
    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                com.mojang.brigadier.builder.LiteralArgumentBuilder.<CommandSourceStack>literal("vl_disable_chaoslib_warning")
                        .executes(ctx -> {
                            if (ClientConfig.CHAOSLIB_WARNING.get()) {
                                ClientConfig.CHAOSLIB_WARNING.set(false);
                                ClientConfig.CHAOSLIB_WARNING.save();

                                ctx.getSource().sendSuccess(
                                        () -> Component.translatable("vortylib.chaoslib_warning.disabled")
                                                .withStyle(ChatFormatting.GREEN),
                                        false
                                );
                            }

                            return 1;
                        })
        );
    }
}