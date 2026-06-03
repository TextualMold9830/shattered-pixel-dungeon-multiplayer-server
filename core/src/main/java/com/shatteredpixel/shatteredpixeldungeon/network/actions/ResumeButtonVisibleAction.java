package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ResumeButtonVisibleAction implements NetworkAction {
    public final boolean visible;

    @Contract(pure = true)
    public ResumeButtonVisibleAction(boolean visible) {
        this.visible = visible;
    }

    @Override
    public @NotNull String actionName() {
        return "resume_button_visible";
    }
}
