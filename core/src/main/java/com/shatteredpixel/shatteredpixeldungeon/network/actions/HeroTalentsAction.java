package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

public class HeroTalentsAction extends HeroPatchAction {
    public final JSONArray talents;

    @Contract(pure = true)
    public HeroTalentsAction(@NotNull JSONArray talents) {
        this.talents = talents;
    }
}
