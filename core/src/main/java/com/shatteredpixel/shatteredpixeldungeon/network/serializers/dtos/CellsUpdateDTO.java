package com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos;

import org.jetbrains.annotations.Nullable;

public class CellsUpdateDTO {
    public final int[] positions;
    @Nullable
    public final int[] tiles;
    @Nullable
    public final int[] states;

    public CellsUpdateDTO(int[] positions, @Nullable int[] tiles, @Nullable int[] states) {
        this.positions = positions;
        this.tiles = tiles;
        this.states = states;
    }
}
