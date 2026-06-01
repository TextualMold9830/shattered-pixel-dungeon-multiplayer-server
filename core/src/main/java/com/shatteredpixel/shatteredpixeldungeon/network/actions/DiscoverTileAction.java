package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DiscoverTileAction implements NetworkAction {
    public final int pos;
    public final int oldValue;

    @Contract(pure = true)
    public DiscoverTileAction(int pos, int oldValue) {
        this.pos = pos;
        this.oldValue = oldValue;
    }

    @Override
    @Contract(pure = true)
    public @NotNull String actionName() {
        return "discover_tile";
    }
}
