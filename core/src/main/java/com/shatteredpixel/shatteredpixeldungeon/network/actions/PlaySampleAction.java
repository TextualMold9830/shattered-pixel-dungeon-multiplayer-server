package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaySampleAction implements ImmutableNetworkAction {
    @NotNull public final String id;
    public final float leftVolume;
    public final float rightVolume;
    public final float rate;
    public final float pitch;
    @Nullable public final Float delay;

    public PlaySampleAction(@NotNull String id, float leftVolume, float rightVolume, float rate, float pitch, @Nullable Float delay) {
        this.id = id;
        this.leftVolume = leftVolume;
        this.rightVolume = rightVolume;
        this.rate = rate;
        this.pitch = pitch;
        this.delay = delay;
    }
    @Override
    public @NotNull String actionName() {
        return "play_sample";
    }

}
