package com.shatteredpixel.shatteredpixeldungeon.balance;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import org.json.JSONObject;

import java.io.IOException;

//Holds all the balancing settings. Once a game is started these can not be changed
public class BalanceData implements Bundlable {
    public boolean globalStrength = true;
    private static final String KEY_GLOBAL_STRENGTH = "global_strength";
    public boolean foodSatisfiesEveryHero = true;
    public static final String KEY_FOOD_SATISFIES_EVERY_HERO = "food_satisfies_every_hero";
    public boolean useFragments = true;
    public static final String KEY_USE_FRAGMENTS = "use_fragments_of_upgrade";
    public boolean multipleGhostReward = true;
    public static final String MULTIPLE_GHOST_REWARD = "multiple_ghost_reward";

    public static BalanceData load() {
        BalanceData data = new BalanceData();
        try {
            data.restoreFromBundle(FileUtils.bundleFromFile("balance/balance.json"));
        } catch (IOException e) {
            Bundle bundle = new Bundle();
            data.storeInBundle(bundle);
            FileUtils.getFileHandle("balance/balance.json").writeString(bundle.toString(4), false);
        }
        return data;

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        for (String key : bundle.getKeys()) {
            switch (key) {
                case KEY_GLOBAL_STRENGTH: globalStrength = bundle.getBoolean(key); break;
                case KEY_FOOD_SATISFIES_EVERY_HERO: foodSatisfiesEveryHero = bundle.getBoolean(KEY_FOOD_SATISFIES_EVERY_HERO); break;
                case KEY_USE_FRAGMENTS: useFragments = bundle.getBoolean(KEY_USE_FRAGMENTS); break;
                case MULTIPLE_GHOST_REWARD: multipleGhostReward = bundle.getBoolean(MULTIPLE_GHOST_REWARD); break;
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(KEY_GLOBAL_STRENGTH, globalStrength);
        bundle.put(KEY_FOOD_SATISFIES_EVERY_HERO, foodSatisfiesEveryHero);
        bundle.put(KEY_USE_FRAGMENTS, useFragments);
        bundle.put(MULTIPLE_GHOST_REWARD, multipleGhostReward);
    }
}
