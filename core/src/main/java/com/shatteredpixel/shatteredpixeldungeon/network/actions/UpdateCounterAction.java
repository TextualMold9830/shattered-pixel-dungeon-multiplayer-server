package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class UpdateCounterAction implements NetworkAction {
    public final float counter;

    @Contract(pure = true)
    public UpdateCounterAction(float counter) {
        this.counter = counter;
    }

    @Override
    public @NotNull String actionName() {
        return "update_counter";
    }
}
