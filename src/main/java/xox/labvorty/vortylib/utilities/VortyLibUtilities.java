package xox.labvorty.vortylib.utilities;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

public class VortyLibUtilities {
    @OnlyIn(Dist.CLIENT)
    public static boolean isKeyOfKeysDown(List<Integer> keys) {
        for (Integer key : keys) {
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key)) {
                return true;
            }
        }

        return false;
    }

    public static Component createHoldBar(int ticks, int maxTicks) {
        int bars = 40;
        int filled = Math.min(bars, (ticks * bars) / maxTicks);

        MutableComponent component = Component.empty();

        component.append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY));

        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                component.append(Component.literal("|")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                component.append(Component.literal("|")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        component.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));

        return component;
    }

    public static void serializeVector3f(CompoundTag compoundTag, Vector3f vector3f) {
        serializeSpecificVector3f(compoundTag, vector3f, "vector3f");
    }

    public static void serializeSpecificVector3f(CompoundTag compoundTag, Vector3f vector3f, String name) {
        compoundTag.putFloat(name + "_x", vector3f.x);
        compoundTag.putFloat(name + "_y", vector3f.y);
        compoundTag.putFloat(name + "_z", vector3f.z);
    }

    public static Vector3f deserializeVector3f(CompoundTag compoundTag) {
        return deserializeSpecificVector3f(compoundTag, "vector3f");
    }

    public static Vector3f deserializeSpecificVector3f(CompoundTag compoundTag, String name) {
        return new Vector3f(
                compoundTag.getFloat(name + "_x"),
                compoundTag.getFloat(name + "_y"),
                compoundTag.getFloat(name + "_z")
        );
    }
}
