package com.ancevt.d2d2.engine;

import com.ancevt.d2d2.sound.Sound;

import java.io.InputStream;

public interface SoundManager {

    Sound createSound(InputStream inputStream);

    Sound createSoundFromAsset(String assetFileName);

    void cleanup();
}
