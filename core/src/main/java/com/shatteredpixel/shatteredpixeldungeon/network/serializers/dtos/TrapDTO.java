package com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos;

import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import org.jetbrains.annotations.Nullable;

public class TrapDTO {
    public final int pos;
    @Nullable
    public final Trap trap;

    public TrapDTO(int pos, @Nullable Trap trap) {
        this.pos = pos;
        this.trap = trap;
    }
}