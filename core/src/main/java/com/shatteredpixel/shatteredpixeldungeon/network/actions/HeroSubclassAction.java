package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;

public class HeroSubclassAction extends HeroPatchAction {
    public final int subclassId;

    @Contract(pure = true)
    public HeroSubclassAction(int subclassId) {
        this.subclassId = subclassId;
    }
}
