package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdateCellsAction implements NetworkAction {
    public final int[] positions;
    @Nullable
    public final int[] tiles;
    @Nullable
    public final int[] states;

    public UpdateCellsAction(int[] positions, @Nullable int[] tiles, @Nullable int[] states) {
        this.positions = positions;
        this.tiles = tiles;
        this.states = states;
    }

    public UpdateCellsAction(int cell, int tile, int state) {
        this.positions = new int[]{cell};
        this.tiles = new int[]{tile};
        this.states = new int[]{state};
    }

    @Override
    public @NotNull String actionName() {
        return "update_cells";
    }
}
