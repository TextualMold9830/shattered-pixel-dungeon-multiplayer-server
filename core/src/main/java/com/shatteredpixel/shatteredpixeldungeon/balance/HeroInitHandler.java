package com.shatteredpixel.shatteredpixeldungeon.balance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.watabou.utils.FileUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class HeroInitHandler {
    public static ArrayList<HeroInitHandler> all = new ArrayList<>();
    static {
        //noinspection StaticInitializerReferencesSubClass
        all.add(new DefaultHeroInitHandler());
    }
    //null for all hero classes
    private HeroClass targetClass;
    //null for any depth
    private Integer targetDepth;
    public HeroInitHandler(String subPath){
        FileHandle handle = FileUtils.getFileHandle("balance/" + subPath);
        switch (handle.nameWithoutExtension()){
            case "warrior": targetClass = HeroClass.WARRIOR; break;
            case "mage": targetClass = HeroClass.MAGE; break;
            case "rogue": targetClass = HeroClass.ROGUE; break;
            case "huntress": targetClass = HeroClass.HUNTRESS; break;
            case "duelist": targetClass = HeroClass.DUELIST; break;
            case "cleric": targetClass = HeroClass.CLERIC; break;
            default:
        }
        String parentName = handle.parent().nameWithoutExtension().replace("/", "");
        if (parentName.equals("common")){
            // Affects every depth
        } else {
            try {
                targetDepth = Integer.parseInt(parentName);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Gdx.app.error("Balance", parentName + " is not a valid depth");
            }
        }
    }
    public void load(JSONObject object){

    }
    public final void onHeroInit(Hero hero){
        //check if class matches
        if (targetClass == null || hero.heroClass == targetClass){
        //Check if depth matches
        if (targetDepth == null || Dungeon.depth == targetDepth){
            onMatchedHero(hero);
        }
        }
    }
    protected void onMatchedHero(Hero hero){
        //add items, set strength, do stuff based on loaded json

    }

    public HeroInitHandler() {
    }
}
