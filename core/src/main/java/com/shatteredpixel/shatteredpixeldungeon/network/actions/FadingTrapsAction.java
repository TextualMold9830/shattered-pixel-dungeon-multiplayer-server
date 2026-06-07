package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class FadingTrapsAction {

    @Contract(value = "-> fail", pure = true)
    private FadingTrapsAction() {
        throw new RuntimeException();
    }

    public static class Update implements ImmutableNetworkAction {
        public final int tileX;
        public final int tileY;
        public final int tileW;
        public final int tileH;
        public final int @NotNull[] data;
        public final float alpha;
        public final boolean isNew;

        @Contract(pure = true)
        public Update(int tileX, int tileY, int tileW, int tileH, int @NotNull[] data, float alpha, boolean isNew) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.tileW = tileW;
            this.tileH = tileH;
            this.data = data;
            this.alpha = alpha;
            this.isNew = isNew;
        }

        @Override
        @Contract(pure = true)
        public @NotNull String actionName() {
            return "fading_traps";
        }
    }

    public static class Kill implements ImmutableNetworkAction {
        @Override
        @Contract(pure = true)
        public @NotNull String actionName() {
            return "fading_traps";
        }
    }
}
