package xox.labvorty.vortylib.data.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.slf4j.Logger;
import xox.labvorty.vortylib.data.text.ClientFloatingTexts;

public record FloatingTextPacket(
        Component text,
        Vec3 position,
        int followEntityId,
        Vector3f rotations,
        float scale,
        int lifetime,
        int fadeIn,
        int fadeOut,
        boolean popIn,
        float riseSpeed,
        boolean seeThrough,
        boolean fullBright,
        int wrapWidth,
        boolean dropShadow,
        boolean followPosition,
        boolean followXRotation,
        boolean followYRotation,
        float typewriterSpeed
) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<FloatingTextPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("nssrpg", "floating_text_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FloatingTextPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                ComponentSerialization.STREAM_CODEC.encode(buf, packet.text);
                buf.writeDouble(packet.position.x);
                buf.writeDouble(packet.position.y);
                buf.writeDouble(packet.position.z);
                buf.writeInt(packet.followEntityId);
                buf.writeFloat(packet.rotations.x);
                buf.writeFloat(packet.rotations.y);
                buf.writeFloat(packet.rotations.z);
                buf.writeFloat(packet.scale);
                buf.writeVarInt(packet.lifetime);
                buf.writeVarInt(packet.fadeIn);
                buf.writeVarInt(packet.fadeOut);
                buf.writeBoolean(packet.popIn);
                buf.writeFloat(packet.riseSpeed);
                buf.writeBoolean(packet.seeThrough);
                buf.writeBoolean(packet.fullBright);
                buf.writeVarInt(packet.wrapWidth);
                buf.writeBoolean(packet.dropShadow);
                buf.writeBoolean(packet.followPosition);
                buf.writeBoolean(packet.followXRotation);
                buf.writeBoolean(packet.followYRotation);
                buf.writeFloat(packet.typewriterSpeed);
            },
            (buf) -> {
                Component text = ComponentSerialization.STREAM_CODEC.decode(buf);
                Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
                int followEntityId = buf.readInt();
                Vector3f rotations = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                float scale = Mth.clamp(buf.readFloat(), 0.001f, 1.0f);
                int lifetime = Mth.clamp(buf.readVarInt(), 1, 6000);
                int fadeIn = Mth.clamp(buf.readVarInt(), 0, lifetime);
                int fadeOut = Mth.clamp(buf.readVarInt(), 0, lifetime);
                boolean popIn = buf.readBoolean();
                float riseSpeed = buf.readFloat();
                boolean seeThrough = buf.readBoolean();
                boolean fullBright = buf.readBoolean();
                int wrapWidth = Mth.clamp(buf.readVarInt(), 20, 400);
                boolean dropShadow = buf.readBoolean();
                boolean followPosition = buf.readBoolean();
                boolean followXRotation = buf.readBoolean();
                boolean followYRotation = buf.readBoolean();
                float typewriterSpeed = Mth.clamp(buf.readFloat(), 0.0f, 1000.0f);

                return new FloatingTextPacket(
                        text, position, followEntityId, rotations, scale, lifetime,
                        fadeIn, fadeOut, popIn, riseSpeed, seeThrough, fullBright, wrapWidth, dropShadow,
                        followPosition, followXRotation, followYRotation, typewriterSpeed
                );
            }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final FloatingTextPacket payload, final IPayloadContext payloadContext) {
        if (payloadContext.flow() == PacketFlow.SERVERBOUND) {
            if (payloadContext.player() instanceof ServerPlayer player) {
                boolean allowed = canBroadcast(player);

                if (allowed) {
                    PacketDistributor.sendToAllPlayers(payload);
                }
            }
            return;
        }

        ClientFloatingTexts.add(payload);
    }

    private static boolean canBroadcast(ServerPlayer player) {
        return player.hasPermissions(2) || player.server.isSingleplayerOwner(player.getGameProfile());
    }

    public static Builder builder(Component text, Vec3 position) {
        return new Builder(text, position);
    }

    public static class Builder {
        private final Component text;
        private Vec3 position;
        private int followEntityId = -1;
        private final Vector3f rotations = new Vector3f();
        private float scale = 0.025f;
        private int lifetime = 60;
        private int fadeIn = 5;
        private int fadeOut = 15;
        private boolean popIn = true;
        private float riseSpeed = 0.0f;
        private boolean seeThrough = false;
        private boolean fullBright = true;
        private int wrapWidth = 160;
        private boolean dropShadow = false;
        private boolean followPosition = true;
        private boolean followXRotation = false;
        private boolean followYRotation = false;
        private float typewriterSpeed = 0.0f;

        private Builder(Component text, Vec3 position) {
            this.text = text;
            this.position = position;
        }

        public Builder follow(Entity entity, Vec3 offset) {
            this.followEntityId = entity.getId();
            this.position = offset;
            return this;
        }

        public Builder followPosition(boolean followPosition) {
            this.followPosition = followPosition;
            return this;
        }

        public Builder followXRotation(boolean followXRotation) {
            this.followXRotation = followXRotation;
            return this;
        }

        public Builder followYRotation(boolean followYRotation) {
            this.followYRotation = followYRotation;
            return this;
        }

        public Builder rotations(float pitch, float yaw, float roll) {
            this.rotations.set(pitch, yaw, roll);
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder lifetime(int ticks) {
            this.lifetime = ticks;
            return this;
        }

        public Builder fade(int fadeIn, int fadeOut) {
            this.fadeIn = fadeIn;
            this.fadeOut = fadeOut;
            return this;
        }

        public Builder popIn(boolean popIn) {
            this.popIn = popIn;
            return this;
        }

        public Builder riseSpeed(float blocksPerTick) {
            this.riseSpeed = blocksPerTick;
            return this;
        }

        public Builder seeThrough(boolean seeThrough) {
            this.seeThrough = seeThrough;
            return this;
        }

        public Builder fullBright(boolean fullBright) {
            this.fullBright = fullBright;
            return this;
        }

        public Builder wrapWidth(int wrapWidth) {
            this.wrapWidth = wrapWidth;
            return this;
        }

        public Builder dropShadow(boolean dropShadow) {
            this.dropShadow = dropShadow;
            return this;
        }

        public Builder typewriter(float charactersPerTick) {
            this.typewriterSpeed = charactersPerTick;
            return this;
        }

        public FloatingTextPacket build() {
            return new FloatingTextPacket(
                    text, position, followEntityId, new Vector3f(rotations), scale,
                    lifetime, fadeIn, fadeOut, popIn, riseSpeed, seeThrough, fullBright, wrapWidth, dropShadow,
                    followPosition, followXRotation, followYRotation, typewriterSpeed
            );
        }
    }
}