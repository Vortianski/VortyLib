package xox.labvorty.vortylib.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xox.labvorty.vortylib.VortyLib;
import xox.labvorty.vortylib.entity.SeatEntity;

public class VortyLibEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, VortyLib.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT_ENTITY = ENTITIES.register(
            "seat_entity",
            () -> EntityType.Builder.<SeatEntity>of(
                    SeatEntity::new,
                    MobCategory.MISC
            ).noSummon()
                    .sized(0, 0)
                    .build("seat_entity")
    );
}
