package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface NetworkAction {
    @Contract(pure = true)
    @NotNull String actionName();
}
