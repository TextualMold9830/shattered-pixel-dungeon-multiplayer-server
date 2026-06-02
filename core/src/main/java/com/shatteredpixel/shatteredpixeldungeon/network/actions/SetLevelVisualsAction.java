package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import org.jetbrains.annotations.NotNull;

public class SetLevelVisualsAction implements NetworkAction {
    public final Level level;

    public SetLevelVisualsAction(@NotNull Level level) {
        this.level = level;
    }

    @Override
    public @NotNull String actionName() {
        return "set_level_visuals";
    }
}
