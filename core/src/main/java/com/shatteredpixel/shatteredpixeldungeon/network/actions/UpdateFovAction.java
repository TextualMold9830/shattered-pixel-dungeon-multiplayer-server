package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class UpdateFovAction implements ImmutableNetworkAction {
    @Nullable
    private final Hero hero;
    private final boolean @Nullable [] visible;

    @Contract(pure = true)
    public UpdateFovAction(@NotNull Hero hero, boolean allowLateSerialization) {
        this.hero = allowLateSerialization ? hero : null;
        this.visible = allowLateSerialization ? null : Arrays.copyOf(hero.fieldOfView, hero.fieldOfView.length);
    }

    @Contract(pure = true)
    public boolean @NotNull [] visible() {
        return hero != null ? Objects.requireNonNull(hero.fieldOfView) : Objects.requireNonNull(visible);
    }

    @Override
    @Contract(pure = true)
    public @NotNull String actionName() {
        return "update_fov";
    }
}
