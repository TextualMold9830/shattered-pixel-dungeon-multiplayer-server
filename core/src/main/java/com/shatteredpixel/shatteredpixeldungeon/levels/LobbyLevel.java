package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.DeviceCompat;

public class LobbyLevel extends DeadEndLevel {
    private static int SIZE = 7;
    @Override
    protected boolean build() {

        setSize(9, 9);

        for (int i=2; i < SIZE; i++) {
            for (int j=2; j < SIZE; j++) {
                map[i * width() + j] = Terrain.EMPTY;
            }
        }

        for (int i=1; i <= SIZE; i++) {
            map[width() + i] =
                    map[width() * SIZE + i] =
                            map[width() * i + 1] =
                                    map[width() * i + SIZE] =
                                            Terrain.WATER;
        }

        int entrance = exit();

        //different exit behaviour depending on main branch or side one
        if (Dungeon.branch == 0) {
            transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_EXIT));
        } else {
            transitions.add(new LevelTransition(this,
                    entrance,
                    LevelTransition.Type.BRANCH_ENTRANCE,
                    Dungeon.depth,
                    0,
                    LevelTransition.Type.BRANCH_EXIT));
        }
        map[entrance] = Terrain.EXIT;
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.ENTRANCE) {
                set(i, Terrain.EXIT);
            }
        }
        return true;
    }
    public int exit(){
        return (SIZE-1) * width() + SIZE / 2 + 1;

    }

    @Override protected void createItems(){
        if(DeviceCompat.isDebug()) {
            super.createItems();
            Potion potion = new PotionOfExperience();
            potion.quantity(10);
            this.drop(potion, exit() - this.width());
            StoneOfDeepSleep sods = new StoneOfDeepSleep();
            sods.quantity(10, false);
            this.drop(new ScrollOfRemoveCurse(), exit() - this.width() + 1);
            WandOfWarding wandOfWarding = new WandOfWarding();
            wandOfWarding.cursed = true;
            this.drop(wandOfWarding, exit() - this.width() + 2);
            this.drop(sods, exit() - this.width() + 3);
        }
    }
}
