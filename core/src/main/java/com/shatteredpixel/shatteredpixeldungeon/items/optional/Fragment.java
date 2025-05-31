package com.shatteredpixel.shatteredpixeldungeon.items.optional;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class Fragment extends Item {
    public static final String AC_USE = "USE";
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
        stackable = true;
        defaultAction = AC_USE;
    }
    String boundUUID;
    @Override
    public int  value() {
        return 0;
    }

    @Override
    public boolean isSimilar(Item item) {
        if (item.getClass().equals(this.getClass())){
            Fragment other = (Fragment) item;
            return boundUUID.equals(other.boundUUID);
        }
        return false;
    }

    public Fragment(String boundUUID) {
        this.boundUUID = boundUUID;
    }
    public Fragment(Hero hero){
        this(hero.uuid);
    }
    public Fragment(){}

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.uuid.equals(boundUUID)) {
            actions.add(AC_USE);
        }
        return actions;
    }

    @Override
    public boolean collect(Bag container) {
        if(container.owner.uuid.equals(boundUUID)) {
            return super.collect(container);
        }
        return false;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)){
            onUse(hero);

        }
    }

    public void onUse(Hero hero){}

    public boolean isOwner(Hero hero) {
        return hero.uuid.equals(boundUUID);
    }
}
