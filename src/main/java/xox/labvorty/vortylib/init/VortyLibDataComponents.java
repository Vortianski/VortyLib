package xox.labvorty.vortylib.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xox.labvorty.vortylib.VortyLib;

public class VortyLibDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, VortyLib.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> GROUP_COMPONENT = DATA_COMPONENT_TYPES.register(
            "group_component",
            () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> GROUP_ITEM_COMPONENT = DATA_COMPONENT_TYPES.register(
            "group_item_component",
            () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .build()
    );
}
