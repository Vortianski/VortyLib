package xox.labvorty.vortylib.data.commands;

import java.util.Locale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import xox.labvorty.vortylib.VortyLib;

@EventBusSubscriber(modid = VortyLib.MODID)
public class DimensionTeleportCommand {
    private static final int REQUIRED_PERMISSION_LEVEL = 3;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimtp")
                .requires(src -> src.hasPermission(REQUIRED_PERMISSION_LEVEL))
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(ctx -> teleport(ctx, null))
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .executes(ctx -> teleport(ctx, Vec3Argument.getVec3(ctx, "pos"))))));
    }

    private static int teleport(CommandContext<CommandSourceStack> ctx, Vec3 requestedPos)
            throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel target = DimensionArgument.getDimension(ctx, "dimension");

        Vec3 dest = requestedPos != null ? requestedPos : player.position();

        player.teleportTo(target, dest.x, dest.y, dest.z, player.getYRot(), player.getXRot());

        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT,
                "Teleported to %s at %.2f %.2f %.2f",
                target.dimension().location(), dest.x, dest.y, dest.z)), true);
        return 1;
    }
}