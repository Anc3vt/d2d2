package com.ancevt.d2d2.engine.norender;

import com.ancevt.d2d2.engine.SoundManager;
import com.ancevt.d2d2.sound.Sound;

import java.io.InputStream;

public class NoRenderSoundManager implements SoundManager {

    @Override
    public Sound createSound(InputStream inputStream) {
        return null;
    }

    @Override
    public Sound createSoundFromAsset(String assetFileName) {
        return null;
    }

    @Override
    public void cleanup() {

    }

    private static class NoRenderSound implements Sound {

        private float volume = 1f;
        private float pan = 0f;
        private boolean disposed;

        @Override
        public void play() {

        }

        @Override
        public void asyncPlay() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void setVolume(float volume) {
            this.volume = volume;
        }

        @Override
        public float getVolume() {
            return volume;
        }

        @Override
        public void setPan(float pan) {
            this.pan = pan;
        }

        @Override
        public float getPan() {
            return pan;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + "{");
            sb.append("volume=").append(volume);
            sb.append(", pan=").append(pan);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public void dispose() {
            disposed = true;
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}
