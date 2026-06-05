package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import org.jetbrains.annotations.NotNull;

public class SpecialSlotsDefinitionAction implements LiveStateNetworkAction {
    public final Hero hero;

    public SpecialSlotsDefinitionAction(@NotNull Hero hero) {
        this.hero = hero;
    }

    @Override
    public @NotNull String actionName() {
        return "inventory_define_special_slots";
    }
}
