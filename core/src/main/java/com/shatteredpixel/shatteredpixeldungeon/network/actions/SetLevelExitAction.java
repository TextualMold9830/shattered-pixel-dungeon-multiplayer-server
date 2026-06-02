package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SetLevelExitAction implements NetworkAction {
    public final int pos;

    @Contract(pure = true)
    public SetLevelExitAction(int pos) {
        this.pos = pos;
    }

    @Override
    @Contract(pure = true)
    public @NotNull String actionName() {
        return "set_level_exit";
    }
}
