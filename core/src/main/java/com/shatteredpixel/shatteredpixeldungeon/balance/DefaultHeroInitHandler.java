package com.shatteredpixel.shatteredpixeldungeon.balance;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.optional.FragmentOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.watabou.utils.Random;

public class DefaultHeroInitHandler extends HeroInitHandler{

    @Override
    protected void onMatchedHero(Hero hero) {
        //We init common stuff
        int bestStr = 10;
        int bestLvl = 1;
        int bestExp = 0;
        int bestExpLevel = 0;
        for (Hero h: Dungeon.heroes){
            if (h != null) {
                if (bestStr < h.getSTR()) {
                    bestStr = h.getSTR();
                }
                if (h.lvl >= bestLvl){
                    bestLvl = h.lvl;
                    if (bestExpLevel < bestLvl) {
                        bestExp = h.exp;
                        bestExpLevel = h.lvl;
                    }else if (bestExpLevel == bestLvl){
                        if (bestExp > h.exp){
                            bestExp = h.exp;
                        }
                    } else {
                        bestExpLevel = bestLvl;
                        bestExp = h.exp;
                    }
                }
            }
        }
        hero.setSTR(bestStr);
        hero.lvl = bestLvl;
        hero.exp = bestExp;
        if (Dungeon.balance.useFragments) {
            new FragmentOfUpgrade(hero).quantity(Statistics.upgradesUsed).collect(hero);
        }
        Random.pushGenerator();
        Weapon weapon = null;
        Armor armor = null;
        switch (bestStr) {
            case 12:
            case 13: weapon = (Weapon) Generator.random(Generator.Category.WEP_T2); armor = new LeatherArmor(); break;
            case 14:
            case 15: weapon = (Weapon) Generator.random(Generator.Category.WEP_T3); armor = new MailArmor(); break;
            case 16:
            case 17: weapon = (Weapon) Generator.random(Generator.Category.WEP_T4); armor = new ScaleArmor(); break;
            case 18:
            case 19:
            case 20:
                //is 21 really possible? Maybe a warden used the seed from the wandmaker quest or something
            case 21: weapon = (Weapon) Generator.random(Generator.Category.WEP_T5); armor = new PlateArmor(); break;


        }
        if (armor != null) {
            armor.bind(hero);
            hero.belongings.setArmor(armor);
            //If warrior affix seal
            if (hero.heroClass.equals(HeroClass.WARRIOR)){
                hero.belongings.armor().affixSeal(new BrokenSeal(), hero);
            }
            hero.belongings.armor().activate(hero);
        }
        if (weapon != null) {
            weapon.bind(hero);
            hero.belongings.setWeapon(weapon);
            //a mage doesn't want to lose his staff
            if (hero.heroClass == HeroClass.MAGE) {
                MagesStaff staff = new MagesStaff(new WandOfMagicMissile());
                staff.bind(hero);
                staff.collect(hero);
                staff.activate(hero);
            }
        }

        if (Ghost.Quest.processed() && Dungeon.balance.multipleGhostReward) {
            //Enchant armor or weapon randomly
            if (Random.Int(2) == 0 && hero.belongings.weapon() instanceof Weapon) {
                ((Weapon) hero.belongings.weapon()).enchant();
            } else {
                hero.belongings.armor().inscribe();
            }
        }
    }
}
