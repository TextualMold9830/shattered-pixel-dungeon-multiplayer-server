package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.nikita22007.multiplayer.utils.text.LocalizedString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ChatMessageAction implements NetworkAction {
    @NotNull
    public final LocalizedString text;

    public ChatMessageAction(@NotNull LocalizedString text) {
        this.text = text;
    }

    @Override
    @Contract(pure = true)
    public @NotNull String actionName() {
        return "messages";
    }
}
