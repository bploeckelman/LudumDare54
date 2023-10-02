package lando.systems.ld54.audio;

import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld54.Assets;

public class AudioManager implements Disposable {

    public MutableFloat soundVolume;
    public MutableFloat musicVolume;

    public static boolean isMusicMuted;
    public static boolean isSoundMuted;

    // none should not have a sound
    public enum Sounds {
        none
        , coin
        , engineStart
        , engineRevving
        , engineLaunch
        , engineRunning
        , thud
        , upgrade
        , powerup
        , radarPing
        , radarReveal
        , swoosh
        , stingAliens1
        , stingAliens2
        , stingTriumphant
        , stingIntense
        , explosion
        , squish
        , dammit
    }

    public enum Musics {
        none
        , mainTheme
        , mainThemeLowpass
        , intro
        , outro
    }

    public ObjectMap<Sounds, SoundContainer> sounds = new ObjectMap<>();
    public ObjectMap<Musics, Music> musics = new ObjectMap<>();

    public Music currentMusic;
    public Musics eCurrentMusic;
    public Music oldCurrentMusic;

    private final Assets assets;
    private final TweenManager tween;

    public AudioManager(Assets assets, TweenManager tween) {
        this.assets = assets;
        this.tween = tween;


//        putSound(Musics.mainTheme, assets.mainTheme);

        musics.put(Musics.mainTheme, assets.mainTheme);
        musics.put(Musics.mainThemeLowpass, assets.mainThemeLowpass);
        musics.put(Musics.intro, assets.intro);
        musics.put(Musics.outro, assets.outro);

        putSound(Sounds.coin, assets.coin);
        putSound(Sounds.engineStart, assets.engineStart);
        putSound(Sounds.engineRevving, assets.engineRevving);
        putSound(Sounds.engineLaunch, assets.engineLaunch);
        putSound(Sounds.engineRunning, assets.engineRunning);
        putSound(Sounds.thud, assets.thud1);
        putSound(Sounds.thud, assets.thud2);
        putSound(Sounds.thud, assets.thud3);
        putSound(Sounds.thud, assets.thud4);
        putSound(Sounds.thud, assets.thud5);
        putSound(Sounds.thud, assets.thud6);
        putSound(Sounds.thud, assets.thud7);
        putSound(Sounds.thud, assets.thud8);
        putSound(Sounds.upgrade, assets.upgrade1);
        putSound(Sounds.powerup, assets.powerup1);
        putSound(Sounds.powerup, assets.powerup2);
//        putSound(Sounds.radar, assets.radar1);
        putSound(Sounds.radarPing, assets.radarPing);
        putSound(Sounds.radarReveal, assets.radarReveal);
        putSound(Sounds.swoosh, assets.swoosh1);
        putSound(Sounds.stingAliens1, assets.stingAliens1);
        putSound(Sounds.stingAliens2, assets.stingAliens2);
        putSound(Sounds.stingTriumphant, assets.stingTriumphant);
        putSound(Sounds.stingIntense, assets.stingIntense);
        putSound(Sounds.explosion, assets.explosion1);
        putSound(Sounds.explosion, assets.explosion2);
        putSound(Sounds.explosion, assets.explosion3);
        putSound(Sounds.squish, assets.squish1);
        putSound(Sounds.squish, assets.squish2);
        putSound(Sounds.squish, assets.squish3);
        putSound(Sounds.squish, assets.squish4);
        putSound(Sounds.squish, assets.dammit);
//        putSound(Sounds.swoosh, assets.swoosh1);
//        putSound(Sounds.bigswoosh, assets.bigSwoosh1);
//        putSound(Sounds.grunt, assets.grunt1);
//        putSound(Sounds.grunt, assets.grunt2);
//        putSound(Sounds.grunt, assets.grunt3);
//        putSound(Sounds.grunt, assets.grunt4);
//        putSound(Sounds.grunt, assets.grunt5);
//        putSound(Sounds.grunt, assets.grunt6);
//        putSound(Sounds.grunt, assets.grunt7);
//        putSound(Sounds.pop, assets.pop1);
//        putSound(Sounds.pop, assets.pop2);
//        putSound(Sounds.pop, assets.pop3);
//        putSound(Sounds.pop, assets.pop4);
//        putSound(Sounds.impact, assets.impact1);
//        putSound(Sounds.error, assets.error1);
//        putSound(Sounds.thud, assets.thud1);
//        putSound(Sounds.bodyHit, assets.bodyHit);
//        putSound(Sounds.gobble, assets.gobble1);
//        putSound(Sounds.gobble, assets.gobble2);
//        putSound(Sounds.gobble, assets.gobble3);
//        putSound(Sounds.gobble, assets.gobble4);
//        putSound(Sounds.gobble, assets.gobble5);
//        putSound(Sounds.ticktock, assets.ticktock);
//        putSound(Sounds.zap, assets.zap1);
//        putSound(Sounds.zap, assets.zap2);
//        putSound(Sounds.zap, assets.zap3);
//        putSound(Sounds.zap, assets.zap4);
//        putSound(Sounds.zap, assets.zap5);
//        putSound(Sounds.zap, assets.zap6);
//        putSound(Sounds.zap, assets.zap7);
//        putSound(Sounds.zap, assets.zap8);
//        putSound(Sounds.zap, assets.zap9);
//        putSound(Sounds.zap, assets.zap10);
//        putSound(Sounds.zap, assets.zap11);
//        putSound(Sounds.zap, assets.zap12);
//        putSound(Sounds.collect, assets.collect1);
//        putSound(Sounds.cannon, assets.cannon1);
//        putSound(Sounds.spawn, assets.spawn1);
//        putSound(Sounds.giggle, assets.giggle1);
//        putSound(Sounds.giggle, assets.giggle2);
//        putSound(Sounds.giggle, assets.giggle3);
//        putSound(Sounds.giggle, assets.giggle4);
//        putSound(Sounds.giggle, assets.giggle5);
//
//        musics.put(Musics.level1Thin, assets.level1Thin);
//        musics.put(Musics.level1Full, assets.level1Full);
//        musics.put(Musics.introMusic, assets.introMusic);
        //musics.put(Musics.mainTheme, assets.mainTheme);
        //musics.put(Musics.mainTheme, assets.mainTheme);

        musicVolume = new MutableFloat(assets.getMusicVolume());
        soundVolume = new MutableFloat(assets.getSoundVolume());

        isMusicMuted = false;
        isSoundMuted = false;

    }

    public void update(float dt) {
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume.floatValue());
            currentMusic.play();
        }

        if (oldCurrentMusic != null) {
            oldCurrentMusic.setVolume(musicVolume.floatValue());
        }
    }

    @Override
    public void dispose() {
        Sounds[] allSounds = Sounds.values();
        for (Sounds sound : allSounds) {
            if (sounds.get(sound) != null) {
                sounds.get(sound).dispose();
            }
        }
        Musics[] allMusics = Musics.values();
        for (Musics music : allMusics) {
            if (musics.get(music) != null) {
                musics.get(music).dispose();
            }
        }
        currentMusic = null;
    }

    public void putSound(Sounds soundType, Sound sound) {
        SoundContainer soundCont = sounds.get(soundType);
        if (soundCont == null) {
            soundCont = new SoundContainer();
        }

        soundCont.addSound(sound);
        sounds.put(soundType, soundCont);
    }

    public long playSound(Sounds soundOption) {
        if (isSoundMuted || soundOption == Sounds.none) return -1;
        return playSound(soundOption, soundVolume.floatValue());
    }

    public long playSound(Sounds soundOption, float volume) {
        volume = volume * soundVolume.floatValue();
        if (isSoundMuted || soundOption == Sounds.none) return -1;

        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont == null) {
            // Gdx.app.log("NoSound", "No sound found for " + soundOption.toString());
            return 0;
        }

        Sound s = soundCont.getSound();
        return (s != null) ? s.play(volume) : 0;
    }

    public void stopSound(Sounds soundOption, long soundId) {
        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont == null) {
            // Gdx.app.log("NoSound", "No sound found for " + soundOption.toString());
            return;
        }

        Sound s = soundCont.getSound();
        s.stop(soundId);
    }

    public long playSound(Sounds soundOption, float volume, float pitch, float pan) {
        volume = volume * soundVolume.floatValue();
        if (isSoundMuted || soundOption == Sounds.none) return -1;

        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont == null) {
            // Gdx.app.log("NoSound", "No sound found for " + soundOption.toString());
            return 0;
        }

        Sound s = soundCont.getSound();
        return (s != null) ? s.play(volume, pitch, pan) : 0;
    }

    public long loopSound(Sounds soundOption, float volume) {
        volume = volume * soundVolume.floatValue();
        if (isSoundMuted || soundOption == Sounds.none) return -1;

        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont == null) {
            // Gdx.app.log("NoSound", "No sound found for " + soundOption.toString());
            return 0;
        }

        Sound s = soundCont.getSound();
        return (s != null) ? s.loop(volume) : 0;
    }

    public long playDirectionalSoundFromVector(Sounds soundOption, Vector2 vector, float viewportWidth) {
        if (isSoundMuted || soundOption == Sounds.none) return -1;

        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont == null) {
            // Gdx.app.log("NoSound", "No sound found for " + soundOption.toString());
            return 0;
        }

        Sound s = soundCont.getSound();
        float midWidth = viewportWidth / 2f;
        float pan = -1 * (midWidth - vector.x) / midWidth;
//        Gdx.app.log("pan: ", String.valueOf(pan));

        return (s != null) ? s.play(soundVolume.floatValue(), 1f, pan) : 0;
    }



    public void stopSound(Sounds soundOption) {
        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont != null) {
            soundCont.stopSound();
        }
    }

    public void stopAllSounds() {
        for (SoundContainer soundCont : sounds.values()) {
            if (soundCont != null) {
                soundCont.stopSound();
            }
        }
    }

    public Music playMusic(Musics musicOptions) {
        return playMusic(musicOptions, true);
    }

    public Music playMusic(Musics musicOptions, boolean playImmediately) {
        return playMusic(musicOptions, playImmediately, true);
    }

    public Music playMusic(Musics musicOptions, boolean playImmediately, boolean looping) {
        if (playImmediately) {
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
            // fade in out streams
            currentMusic = startMusic(musicOptions, looping);
        } else {
            if (currentMusic == null || !currentMusic.isPlaying()) {
                currentMusic = startMusic(musicOptions, looping);
            } else {
                currentMusic.setLooping(false);
                currentMusic.setOnCompletionListener(music -> {
                    currentMusic = startMusic(musicOptions, looping);
                });
            }
        }
        return currentMusic;
    }

    private Music startMusic(Musics musicOptions, boolean looping) {
        Music music = musics.get(musicOptions);
        if (music != null) {
            music.setVolume(musicVolume.floatValue());
            music.setLooping(looping);
            music.play();
        }
        return music;
    }

    public void fadeMusic(Musics musicOption) {
        if (eCurrentMusic == musicOption) return;

    }

    public void stopMusic() {
        for (Music music : musics.values()) {
            if (music != null) music.stop();
        }
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void setMusicVolume(float level) {
        assets.storeMusicVolume(level);
        if (isMusicMuted)
            musicVolume.setValue(0f);
        else
            musicVolume.setValue(level);
    }
    public void setSoundVolume(float level) {
        assets.storeSoundVolume(level);
        if (isSoundMuted)
            soundVolume.setValue(0f);
        else
            soundVolume.setValue(level);
    }

    public void swapMusic() {

        if(assets.mainTheme.isPlaying()) {
            float currentPosition = assets.mainTheme.getPosition();
//            Gdx.app.log("Current position musicA:", String.valueOf(currentPosition));
            assets.mainThemeLowpass.play();
            assets.mainThemeLowpass.setPosition(currentPosition);
            assets.mainTheme.stop();
            currentMusic = assets.mainThemeLowpass;

            return;
        }
        else if(assets.mainThemeLowpass.isPlaying()) {
            float currentPosition = assets.mainThemeLowpass.getPosition();
//            Gdx.app.log("Current position musicB :", String.valueOf(currentPosition));
            assets.mainTheme.play();
            assets.mainTheme.setPosition(currentPosition);
            assets.mainThemeLowpass.stop();
            currentMusic = assets.mainTheme;
            return;
        }
        else {
            return;

        }

    }

}

class SoundContainer {
    public Array<Sound> sounds;
    public Sound currentSound;

    public SoundContainer() {
        sounds = new Array<Sound>();
    }

    public void addSound(Sound s) {
        if (!sounds.contains(s, false)) {
            sounds.add(s);
        }
    }

    public Sound getSound() {
        if (sounds.size > 0) {
            int randIndex = MathUtils.random(0, sounds.size - 1);
            Sound s = sounds.get(randIndex);
            currentSound = s;
            return s;
        } else {
            // Gdx.app.log("No sounds found!");
            return null;
        }
    }

    public void stopSound() {
        if (currentSound != null) {
            currentSound.stop();
        }
    }

    public void dispose() {
        if (currentSound != null) {
            currentSound.dispose();
        }
    }
}
