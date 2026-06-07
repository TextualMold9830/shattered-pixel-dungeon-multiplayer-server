package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class ShowWindowAction implements LiveStateNetworkAction {
    public final @NotNull String type;
    public final int windowID;
    public final @Nullable JSONObject args;

    @Contract(pure = true)
    public ShowWindowAction(@NotNull String type, int windowID, @Nullable JSONObject args) {
        this.type = type;
        this.windowID = windowID;
        this.args = args;
    }

    @Override
    @Contract(pure = true)
    public @NotNull String actionName() {
        return "show_window";
    }
}
