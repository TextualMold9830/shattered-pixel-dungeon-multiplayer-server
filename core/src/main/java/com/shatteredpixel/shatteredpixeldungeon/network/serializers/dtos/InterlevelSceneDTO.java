package com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos;

import com.nikita22007.multiplayer.utils.text.LocalizedString;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterLevelSceneServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InterlevelSceneDTO {
    public enum FadeTime {
        SLOW_FADE, NORM_FADE, FAST_FADE
    }

    @Nullable
    public final String state;
    @Nullable
    public final InterLevelSceneServer.Mode mode;
    @Nullable
    public final LocalizedString customMessage;
    @Nullable
    public final Float scrollSpeed;
    @Nullable
    public final String loadingTexture;
    @Nullable
    public final FadeTime fadeTime;
    public final boolean resetLevel;

    public InterlevelSceneDTO(@NotNull String state, @Nullable String customMessage) {
        this(state, customMessage == null ? null : LocalizedString.raw(customMessage));
    }

    public InterlevelSceneDTO(@NotNull String state, @Nullable LocalizedString customMessage) {
        this.state = state;
        this.mode = null;
        this.customMessage = customMessage;
        this.scrollSpeed = null;
        this.loadingTexture = null;
        this.fadeTime = null;
        this.resetLevel = false;
    }

    public InterlevelSceneDTO(@NotNull InterLevelSceneServer.Mode mode, @Nullable String loadingTexture, @Nullable FadeTime fadeTime) {
        this(mode, loadingTexture, fadeTime, null, null, false);
    }

    public InterlevelSceneDTO(@NotNull InterLevelSceneServer.Mode mode, @Nullable String loadingTexture, @Nullable FadeTime fadeTime, @Nullable Float scrollSpeed, @Nullable LocalizedString customMessage) {
        this(mode, loadingTexture, fadeTime, scrollSpeed, customMessage, false);
    }

    public InterlevelSceneDTO(@NotNull InterLevelSceneServer.Mode mode, @Nullable String loadingTexture, @Nullable FadeTime fadeTime, @Nullable Float scrollSpeed, @Nullable LocalizedString customMessage, boolean resetLevel) {
        this.state = null;
        this.mode = mode;
        this.customMessage = customMessage;
        this.scrollSpeed = scrollSpeed;
        this.loadingTexture = loadingTexture;
        this.fadeTime = fadeTime;
        this.resetLevel = resetLevel;
    }
}
