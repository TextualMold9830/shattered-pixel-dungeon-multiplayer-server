package com.shatteredpixel.shatteredpixeldungeon.plugins.events;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

public class DungeonGenerateLevelEvent extends Event {
    public int depth;
    public Level level;

    public DungeonGenerateLevelEvent(int depth, Level level) {
        this.depth = depth;
        this.level = level;
    }
}
