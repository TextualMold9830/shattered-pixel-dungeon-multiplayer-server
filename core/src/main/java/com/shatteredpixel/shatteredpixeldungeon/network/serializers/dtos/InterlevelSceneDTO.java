package com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos;

import org.jetbrains.annotations.Nullable;

public class InterlevelSceneDTO {
    public final String state;
    @Nullable
    public final String customMessage;

    public InterlevelSceneDTO(String state, @Nullable String customMessage) {
        this.state = state;
        this.customMessage = customMessage;
    }
}
