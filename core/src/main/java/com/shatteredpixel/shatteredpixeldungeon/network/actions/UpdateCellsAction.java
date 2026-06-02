package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
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

    public UpdateCellsAction(int cell, @NotNull Level level) {
        this.positions = new int[]{cell};
        this.tiles = new int[]{level.map[cell]};
        int state = 0;
        if (level.visited[cell]) state = 1;
        else if (level.mapped[cell]) state = 2;
        this.states = new int[]{state};
    }

    @Override
    public @NotNull String actionName() {
        return "update_cells";
    }
}
