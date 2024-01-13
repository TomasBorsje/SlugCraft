package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        INSTANCE.messageBuilder(PlayClientsideSoundPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PlayClientsideSoundPacket::encode)
                .decoder(PlayClientsideSoundPacket::new)
                .consumerMainThread(PlayClientsideSoundPacket::handle)
                .add();
        // Hard rain start, server to clients
        INSTANCE.messageBuilder(HardRainStartPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(HardRainStartPacket::encode)
                .decoder(HardRainStartPacket::new)
                .consumerMainThread(HardRainStartPacket::handle)
                .add();
        // Start threat music
        INSTANCE.messageBuilder(StartThreatMusicPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(StartThreatMusicPacket::encode)
                .decoder(StartThreatMusicPacket::new)
                .consumerMainThread(StartThreatMusicPacket::handle)
                .add();
        // Stop threat music
        INSTANCE.messageBuilder(StopThreatMusicPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(StopThreatMusicPacket::encode)
                .decoder(StopThreatMusicPacket::new)
                .consumerMainThread(StopThreatMusicPacket::handle)
                .add();
    }

    public static void sendToServer(Object message) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }
    public static void sendToAll(Object message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
    public static void sendToClient(Object message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
