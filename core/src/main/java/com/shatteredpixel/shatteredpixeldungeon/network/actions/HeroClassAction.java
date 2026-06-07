package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class HeroClassAction extends HeroPatchAction {
    public final String className;

    @Contract(pure = true)
    public HeroClassAction(@NotNull String className) {
        this.className = className;
    }

    @Contract(pure = true)
    public HeroClassAction(@NotNull HeroClass heroClass) {
        this(heroClass.name());
    }
}
