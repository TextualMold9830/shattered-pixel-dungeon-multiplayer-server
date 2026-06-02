package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class HeroPatchAction implements NetworkAction {
    @Override
    @Contract(pure = true)
    public final @NotNull String actionName() {
        return "hero_patch";
    }
}
