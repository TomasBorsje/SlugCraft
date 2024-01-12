package io.github.tomasborsje.slugcraft.sound;

import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.ForgeRegistries;

public class ThreatMusicHandler {
    public static SoundInstance currentlyPlayingThreatMusic = null;
    public static void playThreatMusic(ResourceLocation soundName) {
        // If there is already a threat music playing, ignore
        if (currentlyPlayingThreatMusic != null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        // If there is no player, ignore
        if (minecraft.player == null) {
            return;
        }

        // Create and play sound instance
        SoundManager soundManager = minecraft.getSoundManager();
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundName);
        if(soundEvent == null) {
            SlugCraft.LOGGER.error("Failed to play threat music sound instance: Sound event " + soundName + " does not exist");
            return;
        }
        SoundInstance soundInstance = new EntityBoundSoundInstance(soundEvent, SoundSource.RECORDS, 0.6f, 1.0f, minecraft.player, 0);
        soundManager.play(soundInstance);
        currentlyPlayingThreatMusic = soundInstance;
    }

    public static void stopThreatMusic() {
        SlugCraft.LOGGER.info("Stopping all threat music sound instances");

        SoundManager soundManager = net.minecraft.client.Minecraft.getInstance().getSoundManager();
        try {
            soundManager.stop(currentlyPlayingThreatMusic);
            SlugCraft.LOGGER.info("Stopped threat music sound instance " + currentlyPlayingThreatMusic.getSound().getLocation());
        } catch (Exception e) {
            SlugCraft.LOGGER.error("Failed to stop threat music sound instance " + currentlyPlayingThreatMusic.getSound().getLocation() + ": " + e.getMessage());
        }
        currentlyPlayingThreatMusic = null;
    }
    public static boolean isPlayingThreatMusic() {
        return currentlyPlayingThreatMusic != null;
    }
}
