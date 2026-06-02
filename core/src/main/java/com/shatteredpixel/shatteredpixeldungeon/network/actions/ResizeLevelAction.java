package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import org.jetbrains.annotations.NotNull;

public class ResizeLevelAction implements NetworkAction {
    public final Level level;

    public ResizeLevelAction(@NotNull Level level) {
        this.level = level;
    }

    @Override
    public @NotNull String actionName() {
        return "resize_level";
    }
}
