package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import org.jetbrains.annotations.NotNull;

public class SetLevelStatesAction implements NetworkAction {
    public final Level level;

    public SetLevelStatesAction(@NotNull Level level) {
        this.level = level;
    }

    @Override
    public @NotNull String actionName() {
        return "set_level_states";
    }
}
