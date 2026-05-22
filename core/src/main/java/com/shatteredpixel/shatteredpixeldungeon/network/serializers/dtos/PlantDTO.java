package com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos;

import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import org.jetbrains.annotations.Nullable;

public class PlantDTO {
    public final int pos;
    @Nullable
    public final Plant plant;

    public PlantDTO(int pos, @Nullable Plant plant) {
        this.pos = pos;
        this.plant = plant;
    }
}