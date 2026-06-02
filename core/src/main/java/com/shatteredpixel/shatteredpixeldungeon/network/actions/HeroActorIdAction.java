package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;

public class HeroActorIdAction extends HeroPatchAction {
    public final int actorId;

    @Contract(pure = true)
    public HeroActorIdAction(int actorId) {
        this.actorId = actorId;
    }
}
