package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import org.jetbrains.annotations.NotNull;

public class EmitterBurstAction implements LiveStateNetworkAction {
    public final Emitter emitter;

    public EmitterBurstAction(@NotNull Emitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public @NotNull String actionName() {
        return "emitter_burst";
    }
}
