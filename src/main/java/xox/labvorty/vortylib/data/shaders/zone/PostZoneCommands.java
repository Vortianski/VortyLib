package xox.labvorty.vortylib.data.shaders.zone;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public final class PostZoneCommands {
    private static int nextId = 0;

    @SubscribeEvent
    public static void onRegister(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("vortylib")
                        .requires(source -> source.hasPermission(4))
                        .then(
                                Commands.literal("post_zone_test")
                                        .then(Commands.literal("add")
                                                .executes(c -> add(4.0))
                                                .then(Commands.argument("size", DoubleArgumentType.doubleArg(0.5, 64.0))
                                                        .executes(c -> add(DoubleArgumentType.getDouble(c, "size")))
                                                )
                                        )
                                        .then(Commands.literal("mode")
                                                .then(Commands.argument("mode", IntegerArgumentType.integer(0, 3))
                                                        .executes(c -> {
                                                            PostZoneRenderer.effectMode = IntegerArgumentType.getInteger(c, "mode");
                                                            return 1;
                                                        })
                                                )
                                        )
                                        .then(Commands.literal("clear")
                                                .executes(c -> {
                                                    PostZoneRenderer.clear();
                                                    return 1;
                                                })
                                        )
                        )
        );
    }

    private static int add(double size) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) return 0;
        Vec3 center = p.getEyePosition().add(p.getLookAngle().scale(size * 0.5 + 2.0));
        PostZoneRenderer.put(nextId++, AABB.ofSize(center, size, size, size));
        return 1;
    }
}