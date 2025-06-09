package com.shatteredpixel.shatteredpixeldungeon.items.optional;


import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentOfUpgrade extends Item {
    private static String AC_USE = "use";
    public static int image = new ScrollOfUpgrade().image();
    {
        identify(null);
    }
    private final WndBag.ItemSelector selector = new WndBag.ItemSelector() {
        @Override
        public String textPrompt() {
            return "Choose an item to upgrade";
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item.isUpgradable();
        }

        @Override
        public void onSelect(Item item) {
            if(item != null) {
                upgradeItem(item, getOwner());
            }
        }
    };

    public FragmentOfUpgrade(Hero hero) {
        bind(hero);
    }

    public FragmentOfUpgrade() {
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (canUse(hero)){
            actions.add(AC_USE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)){
            onUse(hero);
        }
    }

    public void onUse(Hero hero) {
        GameScene.selectItem(selector, hero);
    }
    public Item upgradeItem( Item item, Hero hero ){
        upgradeAnimation( curUser );

        Degrade.detach( curUser, Degrade.class );

        //logic for telling the user when item properties change from upgrades
        //...yes this is rather messy
        if (item instanceof Weapon){
            Weapon w = (Weapon) item;
            boolean wasCursed = w.cursed;
            boolean wasHardened = w.enchantHardened;
            boolean hadCursedEnchant = w.hasCurseEnchant();
            boolean hadGoodEnchant = w.hasGoodEnchant();

            item = w.upgradeFragmented(hero);

            if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
                removeCurse(curUser);
            } else if (w.cursedKnown && wasCursed && !w.cursed){
                weakenCurse(curUser);
            }
            if (wasHardened && !w.enchantHardened){
                GLog.w( Messages.get(Weapon.class, "hardening_gone") );
            } else if (hadGoodEnchant && !w.hasGoodEnchant()){
                GLog.w( Messages.get(Weapon.class, "incompatible") );
            }

        } else if (item instanceof Armor){
            Armor a = (Armor) item;
            boolean wasCursed = a.cursed;
            boolean wasHardened = a.glyphHardened;
            boolean hadCursedGlyph = a.hasCurseGlyph();
            boolean hadGoodGlyph = a.hasGoodGlyph();

            item = a.upgradeFragmented(hero);

            if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()){
                removeCurse( curUser );
            } else if (a.cursedKnown && wasCursed && !a.cursed){
                weakenCurse( curUser );
            }
            if (wasHardened && !a.glyphHardened){
                GLog.w( Messages.get(Armor.class, "hardening_gone") );
            } else if (hadGoodGlyph && !a.hasGoodGlyph()){
                GLog.w( Messages.get(Armor.class, "incompatible") );
            }

        } else if (item instanceof Wand || item instanceof Ring) {
            boolean wasCursed = item.cursed;

            item = item.upgradeFragmented(hero);

            if (item.cursedKnown && wasCursed && !item.cursed){
                removeCurse( curUser );
            }

        } else {
            item = item.upgradeFragmented(hero);
        }

        Badges.validateItemLevelAquired( item );
        Badges.validateMageUnlock();

        Catalog.countUse(item.getClass());

        return item;
    }

    public static void weakenCurse( Hero hero ){
        GLog.p( Messages.get(ScrollOfUpgrade.class, "weaken_curse") );
        hero.getSprite().emitter().start( ShadowParticle.UP, 0.05f, 5 );
    }

    public static void removeCurse( Hero hero ){
        GLog.p( Messages.get(ScrollOfUpgrade.class, "remove_curse") );
        hero.getSprite().emitter().start( ShadowParticle.UP, 0.05f, 10 );
        Badges.validateClericUnlock();
    }

    @Override
    public int energyVal() {
        return 10 * quantity;
    }

    @Override
    public String name() {
        return "Fragment of Upgrade";
    }

    @Override
    public int image() {
        return image;
    }
    public static void upgradeAnimation(Hero hero){
        hero.getSprite().emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
    }
    public static class Upgrade implements Bundlable {
        public String uuid;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            uuid = bundle.getString("uuid");
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put("uuid", uuid);
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof Upgrade)) return false;

            Upgrade upgrade = (Upgrade) o;
            return Objects.equals(uuid, upgrade.uuid);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(uuid);
        }

        public Upgrade(String uuid) {
            this.uuid = uuid;
        }

        public Upgrade() {
        }
    }

}
