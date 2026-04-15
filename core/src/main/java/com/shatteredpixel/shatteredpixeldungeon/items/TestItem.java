package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTest;

import java.util.ArrayList;

public class TestItem extends Item {

    public static final String AC_WINDOW = "WINDOW";

    public TestItem() {
        defaultAction = AC_WINDOW;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_WINDOW);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_WINDOW)) return "Open Window";
        return super.actionName(action, hero);
    }

    @Override
    public String name() {
        return "Test Item";
    }

    @Override
    public int image() {
        return ItemSpriteSheet.SOMETHING;
    }

    @Override
    public String desc() {
        return "An item that opens a test window.";
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_WINDOW)) {
            WndTest wnd = new WndTest(hero);
            SendData.sendWindow(hero.networkID, "wnd_generic", wnd.getId(), wnd.toGenericJSON());
        } else {
            super.execute(hero, action);
        }
    }
}
