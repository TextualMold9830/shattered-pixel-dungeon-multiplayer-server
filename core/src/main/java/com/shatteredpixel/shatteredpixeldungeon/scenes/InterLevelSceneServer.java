package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.network.Server;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.Random;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.getNearClearCell;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.heroes;

public class InterLevelSceneServer {
    private static final float TIME_TO_FADE = 0.3f;

    private static final String TXT_DESCENDING	= "Descending...";
    private static final String TXT_ASCENDING	= "Ascending...";
    private static final String TXT_LOADING		= "Loading...";
    private static final String TXT_RESURRECTING= "Resurrecting...";
    private static final String TXT_RETURNING	= "Returning...";
    private static final String TXT_FALLING		= "Falling...";
    private static final String TXT_INCORRECT_MODE = "Incorrect Interlevel scene mode";

    private static final String ERR_FILE_NOT_FOUND	= "File not found. For some reason.";
    private static final String ERR_GENERIC			= "Something went wrong..."	;

    private static final String TXT_WELCOME			= "Welcome to the level %d of Pixel Dungeon!";
    private static final String TXT_WELCOME_BACK	= "Welcome back to the level %d of Pixel Dungeon!";
    private static final String TXT_NIGHT_MODE		= "Be cautious, since the dungeon is even more dangerous at night!";

    private static final String TXT_CHASM	= "Your steps echo across the dungeon.";
    private static final String TXT_WATER	= "You hear the water splashing around you.";
    private static final String TXT_GRASS	= "The smell of vegetation is thick in the air.";
    private static final String TXT_SECRETS	= "The atmosphere hints that this floor hides many secrets.";
    @Deprecated
    private static void ShowStoryIfNeed(int depth)
    {
    }

    private static void sendMessage(boolean ascend){
        if (ascend) {
            if (Dungeon.depth < Statistics.deepestFloor) {
                GLog.h( TXT_WELCOME_BACK, Dungeon.depth );
            } else {
                GLog.h( TXT_WELCOME, Dungeon.depth );
                Sample.INSTANCE.play(Assets.Sounds.DESCEND );
            }
            switch (Dungeon.level.feeling) {
                case CHASM:
                    GLog.w( TXT_CHASM );
                    break;
                case WATER:
                    GLog.w( TXT_WATER );
                    break;
                case GRASS:
                    GLog.w( TXT_GRASS );
                    break;
                default:
            }
        }
    }

    public static void descend(@Nullable Hero hero)  {// спуск
        try {
            Generator.reset();
            for (int i = 0; i < heroes.length; i++) {
                SendData.sendInterLevelScene(i,"DESCEND");
            }
            Actor.fixTime();
            if (Dungeon.depth > 0) {
                Dungeon.saveLevel();
            }

            Level level;
            level = getNextLevel();
            if (hero == null) {
                Dungeon.switchLevel(level, level.entrance);
            } else {
                Dungeon.switchLevel(level, level.entrance, hero);
            }
            for (int i = 0; i < heroes.length; i++) {
                SendData.sendInterLevelSceneFadeOut(i);
            }
            ShowStoryIfNeed(Dungeon.depth);
            sendMessage(false);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }
    public static  void  fall(Hero  hero){
     fall(hero,false);
    }
    public static void fall(Hero hero, boolean fallIntoPit) {

        try {
            Generator.reset();
            for (int i = 0; i < heroes.length; i++) {
                SendData.sendInterLevelScene(i, "FALL");
            }
            Actor.fixTime();
            Dungeon.saveLevel();

            Level level;
            level = getNextLevel();
            Dungeon.switchLevel(level, fallIntoPit ? level.pitCell() : level.randomRespawnCell(), hero);

            for (int i = 0; i < heroes.length; i++) {
                SendData.sendInterLevelSceneFadeOut(i);
            }
            for (Hero hero_ : heroes) {
                if (hero_ != null && hero.isAlive()) {
                    Chasm.heroLand(hero_);
                }
            }

            ShowStoryIfNeed(Dungeon.depth);
            sendMessage(false);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }
    private static Level getNextLevel() throws IOException {

        if (Dungeon.depth >= Statistics.deepestFloor) {
            Level level = Dungeon.newLevel();
//will return in the future
//            DungeonPostGenerateLevelEvent event = new DungeonPostGenerateLevelEvent(level);
//            Server.pluginManager.fireEvent(event);
//            level = event.level;
            return level;
        } else {
            Dungeon.depth++;
            return Dungeon.loadLevel();
        }
    };

    public static void ascend(Hero hero) {
        try {
            Dungeon.saveLevel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Generator.reset();
        for (int i = 0; i < heroes.length; i++) {
            SendData.sendInterLevelScene(i,"ASCEND");
        }
        Actor.fixTime();

            Dungeon.saveLevel();
            Dungeon.depth--;
            Level level = Dungeon.loadLevel();
            Dungeon.switchLevel(level, level.exit, hero);

            for (int i = 0; i < heroes.length; i++) {
                SendData.sendInterLevelSceneFadeOut(i);
            }
            sendMessage(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }

    public static void returnTo(int  depth, int pos, Hero  hero) {
        try {
            Generator.reset();
            if (depth != Dungeon.depth) {
                for (int i = 0; i < heroes.length; i++) {
                    SendData.sendInterLevelScene(i,"RETURN");
                }

                Actor.fixTime();
                Dungeon.saveLevel();
                Dungeon.depth = depth;
                Level level = Dungeon.loadLevel();
                Dungeon.switchLevel(level, pos, hero);
                for (int i = 0; i < heroes.length; i++) {
                    SendData.sendInterLevelSceneFadeOut(i);
                    sendMessage(true);
                }
            } else {
                hero.pos = getNearClearCell(pos);
            }
            ScrollOfTeleportation.appear(hero, hero.pos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }

    public static void restore() { //when loading from save

        try {
            Generator.reset();
            Actor.fixTime();

            GLog.wipe();

            Dungeon.loadGame(StartScene.curClass);
            if (Dungeon.depth == -1) {
                Dungeon.depth = Statistics.deepestFloor;
                Dungeon.switchLevel(Dungeon.loadLevel(StartScene.curClass));
            } else {
                Level level = Dungeon.loadLevel(StartScene.curClass);
                Dungeon.switchLevel(level);
            }
        }catch (IOException  e){
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }

    @SuppressWarnings("fallthrough")
    public static void resurrect(Hero hero)  { //respawn by ankh

        Generator.reset();
        for (int i = 0; i< heroes.length; i++) {
            SendData.sendInterLevelScene(i, "RESURRECT");
        }
        Actor.fixTime();
        switch (Settings.resurrectMode){
            case RESET_LEVEL: {
                if (Dungeon.bossLevel(Dungeon.depth)) {
                    hero.resurrect(Dungeon.depth);
                    Dungeon.depth--;
                    Level level = Dungeon.newLevel();
                    Dungeon.switchLevelToAll(level, level.entrance);
                } else {
                    hero.resurrect(-1);
                    Dungeon.resetLevel();
                }
            }
            case RESPAWN_HERO:
            {
                Dungeon.switchLevel(Dungeon.level,Dungeon.level.entrance, hero);
            }
        }
        ScrollOfTeleportation.appear(hero,hero.pos);
        for (int i = 0; i< heroes.length; i++) {
            SendData.sendInterLevelSceneFadeOut(i);
        }

        sendMessage(false);
        Game.switchScene( GameScene.class );
    }

}
