package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(SlugCraft.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    private static int id = 0;

    public static void register() {
        // Quickfire round start, server to clients
        INSTANCE.messageBuilder(QuickfireRoundStartPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(QuickfireRoundStartPacket::encode)
                .decoder(QuickfireRoundStartPacket::new)
                .consumerMainThread(QuickfireRoundStartPacket::handle)
                .add();
        // Threat music start, server to clients
        INSTANCE.messageBuilder(ThreatMusicStartPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ThreatMusicStartPacket::encode)
                .decoder(ThreatMusicStartPacket::new)
                .consumerMainThread(ThreatMusicStartPacket::handle)
                .add();
        // Hard rain start, server to clients
        INSTANCE.messageBuilder(HardRainStartPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(HardRainStartPacket::encode)
                .decoder(HardRainStartPacket::new)
                .consumerMainThread(HardRainStartPacket::handle)
                .add();
    }

    public static void sendToServer(Object message) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static void sendToAll(Object message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
