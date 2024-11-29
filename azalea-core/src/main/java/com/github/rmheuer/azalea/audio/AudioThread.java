package com.github.rmheuer.azalea.audio;

import com.github.rmheuer.azalea.audio.play.PlayingSound;
import com.github.rmheuer.azalea.audio.play.PlayingStream;

import java.util.concurrent.CopyOnWriteArrayList;

final class AudioThread extends Thread {
    private volatile boolean running = false;
    private final AudioSystem system;
    private final CopyOnWriteArrayList<PlayingSound> sounds;

    public AudioThread(AudioSystem system) {
        super("Audio Thread");
        setDaemon(true);

        this.system = system;
        sounds = new CopyOnWriteArrayList<>();
    }

    public void add(PlayingSound sound) {
        sounds.add(sound);
    }

    @Override
    public void run() {
        System.out.println("Starting audio thread");

        running = true;
        while (running) {
            for (PlayingSound sound : sounds) {
                if (sound instanceof PlayingStream)
                    ((PlayingStream) sound).update();

                if (!sound.isPlaying()) {
                    sounds.remove(sound);
                    system.returnSource(sound.end());
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                running = false;
            }
        }

        // Stop all sounds now
        for (PlayingSound sound : sounds) {
            system.returnSource(sound.end());
        }

        System.out.println("Audio thread stopped");
    }

    public void end() {
        running = false;
    }
}
