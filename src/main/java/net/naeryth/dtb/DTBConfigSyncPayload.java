package net.naeryth.dtb;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DTBConfigSyncPayload(
        int version, boolean enabled, float strength, float gamma, boolean respectNV
) implements CustomPayload {

    public static final CustomPayload.Id<DTBConfigSyncPayload> ID =
            new Id<>(Identifier.of("true_darkness", "config_sync"));

    public static final PacketCodec<RegistryByteBuf, DTBConfigSyncPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT,  DTBConfigSyncPayload::version,
                    PacketCodecs.BOOL,     DTBConfigSyncPayload::enabled,
                    PacketCodecs.FLOAT,    DTBConfigSyncPayload::strength,
                    PacketCodecs.FLOAT,    DTBConfigSyncPayload::gamma,
                    PacketCodecs.BOOL,     DTBConfigSyncPayload::respectNV,
                    DTBConfigSyncPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }  // (If your mappings use getType(), use that.)
}