package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import org.jetbrains.annotations.NotNull;

public class BuffUpdateAction implements LiveStateNetworkAction {
    public final Buff buff;

    public BuffUpdateAction(@NotNull Buff buff) {
        this.buff = buff;
    }

    @Override
    public @NotNull String actionName() {
        return "buff_update";
    }
}
