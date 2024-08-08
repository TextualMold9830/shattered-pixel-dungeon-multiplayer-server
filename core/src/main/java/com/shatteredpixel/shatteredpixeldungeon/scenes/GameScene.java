/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Ripple;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DiscardedItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonWallsTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.FogOfWar;
import com.shatteredpixel.shatteredpixeldungeon.tiles.GridTileMap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.RaisedTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.WallBlockingTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.CharHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.LootIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.MenuPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ResumeIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoPlant;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoTrap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.watabou.glwrap.Blending;
import com.watabou.input.ControllerHandler;
import com.watabou.input.KeyBindings;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
//FIXME
//FIXME
//FIXME
public class GameScene extends PixelScene {

	static GameScene scene;

	private SkinnedBlock water;
	public static DungeonTerrainTilemap tiles;
	private GridTileMap visualGrid;
	private TerrainFeaturesTilemap terrainFeatures;
	private RaisedTerrainTilemap raisedTerrain;
	private DungeonWallsTilemap walls;
	private WallBlockingTilemap wallBlocking;
	private FogOfWar fog;
	private HeroSprite hero;

	private MenuPane menu;
	private StatusPane status;

	private BossHealthBar boss;

	private GameLog log;

	private static CellSelector cellSelector;

	private Group terrain;
	private Group customTiles;
	private Group levelVisuals;
	private Group levelWallVisuals;
	private Group customWalls;
	private Group ripples;
	private Group plants;
	private Group traps;
	private Group heaps;
	private Group mobs;
	private Group floorEmitters;
	private Group emitters;
	private Group effects;
	private Group gases;
	private Group spells;
	private Group statuses;
	private Group emoicons;
	private Group overFogEffects;
	private Group healthIndicators;

	private static boolean invVisible = true;

	private Toolbar toolbar;
	private Toast prompt;

	private AttackIndicator attack;
	private LootIndicator loot;
	private ActionIndicator action;
	private ResumeIndicator resume;

	{
		inGameScene = true;
	}

	@Override
	public void create() {

		if (Dungeon.heroes == null || Dungeon.level == null) {
			ShatteredPixelDungeon.switchNoFade(TitleScene.class);
			return;
		}

		Dungeon.level.playLevelMusic();

		//SPDSettings.lastClass(Dungeon.heroes.heroClass.ordinal());

		super.create();
		Camera.main.zoom(GameMath.gate(minZoom, defaultZoom + SPDSettings.zoom(), maxZoom));
		Camera.main.edgeScroll.set(1);

		switch (SPDSettings.cameraFollow()) {
			case 4:
			default:
				Camera.main.setFollowDeadzone(0);
				break;
			case 3:
				Camera.main.setFollowDeadzone(0.2f);
				break;
			case 2:
				Camera.main.setFollowDeadzone(0.5f);
				break;
			case 1:
				Camera.main.setFollowDeadzone(0.9f);
				break;
		}

		scene = this;

		terrain = new Group();
		add(terrain);

		water = new SkinnedBlock(
				Dungeon.level.width() * DungeonTilemap.SIZE,
				Dungeon.level.height() * DungeonTilemap.SIZE,
				Dungeon.level.waterTex()) {

			@Override
			protected NoosaScript script() {
				return NoosaScriptNoLighting.get();
			}

			@Override
			public void draw() {
				//water has no alpha component, this improves performance
				Blending.disable();
				super.draw();
				Blending.enable();
			}
		};
		water.autoAdjust = true;
		terrain.add(water);

		ripples = new Group();
		terrain.add(ripples);

		DungeonTileSheet.setupVariance(Dungeon.level.map.length, Dungeon.seedCurDepth());

		tiles = new DungeonTerrainTilemap();
		terrain.add(tiles);

		customTiles = new Group();
		terrain.add(customTiles);

		for (CustomTilemap visual : Dungeon.level.customTiles) {
			addCustomTile(visual);
		}

		visualGrid = new GridTileMap();
		terrain.add(visualGrid);

		terrainFeatures = new TerrainFeaturesTilemap(Dungeon.level.plants, Dungeon.level.traps);
		terrain.add(terrainFeatures);

		levelVisuals = Dungeon.level.addVisuals();
		add(levelVisuals);

		floorEmitters = new Group();
		add(floorEmitters);

		heaps = new Group();
		add(heaps);

		for (Heap heap : Dungeon.level.heaps.valueList()) {
			addHeapSprite(heap);
		}

		emitters = new Group();
		effects = new Group();
		healthIndicators = new Group();
		emoicons = new Group();
		overFogEffects = new Group();

		mobs = new Group();
		add(mobs);
		for (Hero notSprite : Dungeon.heroes) {
			if (notSprite != null) {
				hero = new HeroSprite(notSprite);
				hero.place(notSprite.pos);
				hero.updateArmor(notSprite);
				mobs.add(hero);
			}
		}

		for (Mob mob : Dungeon.level.mobs) {
			addMobSprite(mob);
		}

		raisedTerrain = new RaisedTerrainTilemap();
		add(raisedTerrain);

		walls = new DungeonWallsTilemap();
		add(walls);

		customWalls = new Group();
		add(customWalls);

		for (CustomTilemap visual : Dungeon.level.customWalls) {
			addCustomWall(visual);
		}

		levelWallVisuals = Dungeon.level.addWallVisuals();
		add(levelWallVisuals);

		wallBlocking = new WallBlockingTilemap();
		add(wallBlocking);

		add(emitters);
		add(effects);

		gases = new Group();
		add(gases);

		for (Blob blob : Dungeon.level.blobs.values()) {
			blob.emitter = null;
			addBlobSprite(blob);
		}


		fog = new FogOfWar(Dungeon.level.width(), Dungeon.level.height());
		add(fog);

		spells = new Group();
		add(spells);

		add(overFogEffects);

		statuses = new Group();
		add(statuses);

		add(healthIndicators);
		//always appears ontop of other health indicators
		add(new TargetHealthIndicator());

		add(emoicons);

		add(cellSelector = new CellSelector(tiles));

		int uiSize = SPDSettings.interfaceSize();

		menu = new MenuPane();
		menu.camera = uiCamera;
		menu.setPos(uiCamera.width - MenuPane.WIDTH, uiSize > 0 ? 0 : 1);
		add(menu);

		status = new StatusPane(SPDSettings.interfaceSize() > 0);
		status.camera = uiCamera;
		status.setRect(0, uiSize > 0 ? uiCamera.height - 39 : 0, uiCamera.width, 0);
		add(status);

		boss = new BossHealthBar();
		boss.camera = uiCamera;
		boss.setPos(6 + (uiCamera.width - boss.width()) / 2, 20);
		add(boss);

		resume = new ResumeIndicator();
		resume.camera = uiCamera;
		add(resume);

		action = new ActionIndicator();
		action.camera = uiCamera;
		add(action);

		loot = new LootIndicator();
		loot.camera = uiCamera;
		add(loot);

		attack = new AttackIndicator();
		attack.camera = uiCamera;
		add(attack);

		log = new GameLog();
		log.camera = uiCamera;
		log.newLine();
		add(log);

		if (uiSize > 0) {
			bringToFront(status);
		}

		toolbar = new Toolbar();
		toolbar.camera = uiCamera;
		add(toolbar);

		{
			toolbar.setRect(0, uiCamera.height - toolbar.height(), uiCamera.width, toolbar.height());
		}

		layoutTags();

		switch (InterLevelSceneServer.mode) {
			case RESURRECT:
				for (Hero hero : Dungeon.heroes) {
					if (hero != null) {
						Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
						ScrollOfTeleportation.appearVFX(hero);
						SpellSprite.show(hero, SpellSprite.ANKH);
						new Flare(5, 16).color(0xFFFF00, true).show(hero.getSprite(), 4f);
					}
				}
				break;
			case RETURN:
				for (Hero hero : Dungeon.heroes) {
					if (hero != null) {
						ScrollOfTeleportation.appearVFX(hero);
					}
				}
				break;
			case DESCEND:
			case FALL:
				if (Dungeon.depth == Statistics.deepestFloor) {
					switch (Dungeon.depth) {
						case 1:
						case 6:
						case 11:
						case 16:
						case 21:
							int region = (Dungeon.depth + 4) / 5;
							if (!Document.INTROS.isPageRead(region)) {
								add(new WndStory(Document.INTROS.pageBody(region)).setDelays(0.6f, 1.4f));
								Document.INTROS.readPage(region);
							}
							break;
					}
				}
				for (Hero hero : Dungeon.heroes) {
					if (hero != null) {
						if (hero.isAlive()) {
							Badges.validateNoKilling();
						}
						break;
					}
				}
		}

		ArrayList<Item> dropped = Dungeon.droppedItems.get(Dungeon.depth);
		if (dropped != null) {
			for (Item item : dropped) {
				int pos = Dungeon.level.randomRespawnCell(null);
				if (pos == -1) pos = Dungeon.level.entrance();
				if (item instanceof Potion) {
					((Potion) item).shatter(pos);
				} else if (item instanceof Plant.Seed && !Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
					Dungeon.level.plant((Plant.Seed) item, pos);
				} else if (item instanceof Honeypot) {
					Dungeon.level.drop(((Honeypot) item).shatter(null, pos), pos);
				} else {
					Dungeon.level.drop(item, pos);
				}
			}
			Dungeon.droppedItems.remove(Dungeon.depth);
		}
		for (Hero hero : Dungeon.heroes) {
			if (hero != null) {
				hero.next();

			}
		}
		switch (InterLevelSceneServer.mode) {
			case FALL:
			case DESCEND:
			case CONTINUE:
				Camera.main.snapTo(hero.center().x, hero.center().y - DungeonTilemap.SIZE * (defaultZoom / Camera.main.zoom));
				break;
			case ASCEND:
				Camera.main.snapTo(hero.center().x, hero.center().y + DungeonTilemap.SIZE * (defaultZoom / Camera.main.zoom));
				break;
			default:
				Camera.main.snapTo(hero.center().x, hero.center().y);
		}
		Camera.main.panTo(hero.center(), 2.5f);

		if (InterLevelSceneServer.mode != InterLevelSceneServer.Mode.NONE) {
			if (Dungeon.depth == Statistics.deepestFloor
					&& (InterLevelSceneServer.mode == InterLevelSceneServer.Mode.DESCEND || InterLevelSceneServer.mode == InterLevelSceneServer.Mode.FALL)) {
				GLog.h(Messages.get(this, "descend"), Dungeon.depth);
				Sample.INSTANCE.play(Assets.Sounds.DESCEND);

				for (Char ch : Actor.chars()) {
					if (ch instanceof DriedRose.GhostHero) {
						((DriedRose.GhostHero) ch).sayAppeared();
					}
				}

				int spawnersAbove = Statistics.spawnersAlive;
				if (spawnersAbove > 0 && Dungeon.depth <= 25) {
					for (Mob m : Dungeon.level.mobs) {
						if (m instanceof DemonSpawner && ((DemonSpawner) m).spawnRecorded) {
							spawnersAbove--;
						}
					}

					if (spawnersAbove > 0) {
						if (Dungeon.bossLevel()) {
							GLog.n(Messages.get(this, "spawner_warn_final"));
						} else {
							GLog.n(Messages.get(this, "spawner_warn"));
						}
					}
				}

			} else if (InterLevelSceneServer.mode == InterLevelSceneServer.Mode.RESET) {
				GLog.h(Messages.get(this, "warp"));
			} else if (InterLevelSceneServer.mode == InterLevelSceneServer.Mode.RESURRECT) {
				GLog.h(Messages.get(this, "resurrect"), Dungeon.depth);
			} else {
				GLog.h(Messages.get(this, "return"), Dungeon.depth);
			}
			for (Hero hero : Dungeon.heroes) {
				if (hero != null) {
					if (hero.hasTalent(Talent.ROGUES_FORESIGHT)
							&& Dungeon.level instanceof RegularLevel && Dungeon.branch == 0) {
						int reqSecrets = Dungeon.level.feeling == Level.Feeling.SECRETS ? 2 : 1;
						for (Room r : ((RegularLevel) Dungeon.level).rooms()) {
							if (r instanceof SecretRoom) reqSecrets--;
						}

						//60%/90% chance, use level's seed so that we get the same result for the same level
						//offset seed slightly to avoid output patterns
						Random.pushGenerator(Dungeon.seedCurDepth() + 1);
						if (reqSecrets <= 0 && Random.Int(10) < 3 + 3 * hero.pointsInTalent(Talent.ROGUES_FORESIGHT)) {
							GLog.p(Messages.get(this, "secret_hint"));
						}
						Random.popGenerator();
					}
					boolean unspentTalents = false;
					for (int i = 1; i <= hero.talents.size(); i++) {
						if (hero.talentPointsAvailable(i) > 0) {
							unspentTalents = true;
							break;
						}
					}
					if (unspentTalents) {
						GLog.newLine();
						GLog.w(Messages.get(Dungeon.heroes, "unspent"));
						StatusPane.talentBlink = 10f;
						WndHero.lastIdx = 1;
					}
				}
				}
				switch (Dungeon.level.feeling) {
					case CHASM:
						GLog.w(Messages.get(this, "chasm"));
						break;
					case WATER:
						GLog.w(Messages.get(this, "water"));
						break;
					case GRASS:
						GLog.w(Messages.get(this, "grass"));
						break;
					case DARK:
						GLog.w(Messages.get(this, "dark"));
						break;
					case LARGE:
						GLog.w(Messages.get(this, "large"));
						break;
					case TRAPS:
						GLog.w(Messages.get(this, "traps"));
						break;
					case SECRETS:
						GLog.w(Messages.get(this, "secrets"));
						break;
				}

				for (Mob mob : Dungeon.level.mobs) {
					if (!mob.buffs(ChampionEnemy.class).isEmpty()) {
						GLog.w(Messages.get(ChampionEnemy.class, "warn"));
					}
				}
			for (Hero hero: Dungeon.heroes) {
				if (hero == null) continue;
				if (hero.buff(AscensionChallenge.class) != null) {
					hero.buff(AscensionChallenge.class).saySwitch();
				}
			}
				DimensionalSundial.sundialWarned = true;
				if (DimensionalSundial.spawnMultiplierAtCurrentTime() > 1) {
					GLog.w(Messages.get(DimensionalSundial.class, "warning"));
				} else {
					DimensionalSundial.sundialWarned = false;
				}

				InterLevelSceneServer.mode = InterLevelSceneServer.Mode.NONE;



			//Tutorial
			if (SPDSettings.intro()) {

				if (Document.ADVENTURERS_GUIDE.isPageFound(Document.GUIDE_INTRO)) {
					GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_INTRO);
				} else if (ControllerHandler.isControllerConnected()) {
					GLog.p(Messages.get(GameScene.class, "tutorial_move_controller"));
				} else if (SPDSettings.interfaceSize() == 0) {
					GLog.p(Messages.get(GameScene.class, "tutorial_move_mobile"));
				} else {
					GLog.p(Messages.get(GameScene.class, "tutorial_move_desktop"));
				}
				toolbar.visible = toolbar.active = false;
				status.visible = status.active = false;
			}

			if (!SPDSettings.intro() &&
					Rankings.INSTANCE.totalNumber > 0 &&
					!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_DIEING)) {
				GLog.p(Messages.get(Guidebook.class, "hint"));
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_DIEING);
			}

			fadeIn();

			//re-show WndResurrect if needed
			for (Hero hero : Dungeon.heroes) {
				if (hero != null) {

					if (!hero.isAlive()) {
						//check if hero has an unblessed ankh
						Ankh ankh = null;
						for (Ankh i : hero.belongings.getAllItems(Ankh.class)) {
							if (!i.isBlessed()) {
								ankh = i;
							}
						}
						if (ankh != null && GamesInProgress.gameExists(GamesInProgress.curSlot)) {
							add(new WndResurrect(ankh));
						} else {
							//gameOver();
						}
					}

				}
			}
		}
	}

	public void destroy() {

		//tell the actor thread to finish, then wait for it to complete any actions it may be doing.
		if (!waitForActorThread(4500, true)) {
			Throwable t = new Throwable();
			t.setStackTrace(actorThread.getStackTrace());
			throw new RuntimeException("timeout waiting for actor thread! ", t);
		}

		Emitter.freezeEmitters = false;

		scene = null;
		Badges.saveGlobal();
		Journal.saveGlobal();

		super.destroy();
	}

	public static void endActorThread() {
		if (actorThread != null && actorThread.isAlive()) {
			Actor.keepActorThreadAlive = false;
			actorThread.interrupt();
		}
	}

	public boolean waitForActorThread(int msToWait, boolean interrupt) {
		if (actorThread == null || !actorThread.isAlive()) {
			return true;
		}
		synchronized (actorThread) {
			if (interrupt) actorThread.interrupt();
			try {
				actorThread.wait(msToWait);
			} catch (InterruptedException e) {
				ShatteredPixelDungeon.reportException(e);
			}
			return !Actor.processing();
		}
	}

	@Override
	public synchronized void onPause() {
		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
	}

	private static Thread actorThread;

	//sometimes UI changes can be prompted by the actor thread.
	// We queue any removed element destruction, rather than destroying them in the actor thread.
	private ArrayList<Gizmo> toDestroy = new ArrayList<>();

	//the actor thread processes at a maximum of 60 times a second
	//this caps the speed of resting for higher refresh rate displays
	private float notifyDelay = 1 / 60f;

	//todo send it
	public static boolean updateItemDisplays = false;

	public static boolean tagDisappeared = false;
	public static boolean updateTags = false;

	@Override
	public synchronized void update() {
		lastOffset = null;

		if (updateItemDisplays) {
			updateItemDisplays = false;
			QuickSlotButton.refresh();
			if (ActionIndicator.action instanceof MeleeWeapon.Charger) {
				//Champion weapon swap uses items, needs refreshing whenever item displays are updated
				ActionIndicator.refresh();
			}
		}

		if (Dungeon.heroes == null || scene == null) {
			return;
		}

		super.update();

		if (notifyDelay > 0) notifyDelay -= Game.elapsed;

		if (!Emitter.freezeEmitters) water.offset(0, -5 * Game.elapsed);
			//TODO: check this
			if (!Actor.processing()) {
			if (actorThread == null || !actorThread.isAlive()) {

				actorThread = new Thread() {
					@Override
					public void run() {
						Actor.process();
					}
				};

				//if cpu cores are limited, game should prefer drawing the current frame
				if (Runtime.getRuntime().availableProcessors() == 1) {
					actorThread.setPriority(Thread.NORM_PRIORITY - 1);
				}
				actorThread.setName("SHPD Actor Thread");
				Thread.currentThread().setName("SHPD Render Thread");
				Actor.keepActorThreadAlive = true;
				actorThread.start();
			} else if (notifyDelay <= 0f) {
				notifyDelay += 1 / 60f;
				synchronized (actorThread) {
					actorThread.notify();
				}
			}
		}
		for (Hero hero : Dungeon.heroes) {
			if(hero != null) {
				if (hero.ready && hero.paralysed == 0) {
					log.newLine();
				}
			}
	}

		if (updateTags){
			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagAction = action.visible;
			tagResume = resume.visible;

			layoutTags();

		} else if (tagAttack != attack.active ||
				tagLoot != loot.visible ||
				tagAction != action.visible ||
				tagResume != resume.visible) {

			boolean tagAppearing = (attack.active && !tagAttack) ||
									(loot.visible && !tagLoot) ||
									(action.visible && !tagAction) ||
									(resume.visible && !tagResume);

			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagAction = action.visible;
			tagResume = resume.visible;

			//if a new tag appears, re-layout tags immediately
			//otherwise, wait until the hero acts, so as to not suddenly change their position
			if (tagAppearing)   layoutTags();
			else                tagDisappeared = true;

		}
		for (Hero hero : Dungeon.heroes) {
			if (hero != null) {
				cellSelector.enable(hero.ready);
			}
		}
		if (!toDestroy.isEmpty()) {
			for (Gizmo g : toDestroy) {
				g.destroy();
			}
			toDestroy.clear();
		}
	}

	private static Point lastOffset = null;

	@Override
	public synchronized Gizmo erase (Gizmo g) {
		Gizmo result = super.erase(g);
		if (result instanceof Window){
			lastOffset = ((Window) result).getOffset();
		}
		return result;
	}

	private boolean tagAttack    = false;
	private boolean tagLoot      = false;
	private boolean tagAction    = false;
	private boolean tagResume    = false;

	public static void layoutTags() {

		updateTags = false;

		if (scene == null) return;

		//move the camera center up a bit if we're on full UI and it is taking up lots of space
		{
			Camera.main.setCenterOffset(0, 0);
		}
		//Camera.main.panTo(Dungeon.hero.sprite.center(), 5f);

		//primarily for phones displays with notches
		//TODO Android never draws into notch atm, perhaps allow it for center notches?
		RectF insets = DeviceCompat.getSafeInsets();
		insets = insets.scale(1f / uiCamera.zoom);

		boolean tagsOnLeft = SPDSettings.flipTags();
		float tagWidth = Tag.SIZE + (tagsOnLeft ? insets.left : insets.right);
		float tagLeft = tagsOnLeft ? 0 : uiCamera.width - tagWidth;

		float y = SPDSettings.interfaceSize() == 0 ? scene.toolbar.top()-2 : scene.status.top()-2;
		if (SPDSettings.interfaceSize() == 0){
			if (tagsOnLeft) {
				scene.log.setRect(tagWidth, y, uiCamera.width - tagWidth - insets.right, 0);
			} else {
				scene.log.setRect(insets.left, y, uiCamera.width - tagWidth - insets.left, 0);
			}
		} else {
			if (tagsOnLeft) {
				scene.log.setRect(tagWidth, y, 160 - tagWidth, 0);
			} else {
				scene.log.setRect(insets.left, y, 160 - insets.left, 0);
			}
		}

		float pos = scene.toolbar.top();
		if (tagsOnLeft && SPDSettings.interfaceSize() > 0){
			pos = scene.status.top();
		}

		if (scene.tagAttack){
			scene.attack.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.attack.flip(tagsOnLeft);
			pos = scene.attack.top();
		}

		if (scene.tagLoot) {
			scene.loot.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.loot.flip(tagsOnLeft);
			pos = scene.loot.top();
		}

		if (scene.tagAction) {
			scene.action.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.action.flip(tagsOnLeft);
			pos = scene.action.top();
		}

		if (scene.tagResume) {
			scene.resume.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.resume.flip(tagsOnLeft);
		}
	}
	
	@Override
	protected void onBackPressed() {
		if (!cancel()) {
			add( new WndGame() );
		}
	}

	public void addCustomTile( CustomTilemap visual){
		customTiles.add( visual.create() );
	}

	public void addCustomWall( CustomTilemap visual){
		customWalls.add( visual.create() );
	}
	
	private void addHeapSprite( Heap heap ) {
		ItemSprite sprite = heap.sprite = (ItemSprite)heaps.recycle( ItemSprite.class );
		sprite.revive();
		sprite.link( heap );
		heaps.add( sprite );
	}
	
	private void addDiscardedSprite( Heap heap ) {
		heap.sprite = (DiscardedItemSprite)heaps.recycle( DiscardedItemSprite.class );
		heap.sprite.revive();
		heap.sprite.link( heap );
		heaps.add( heap.sprite );
	}
	
	private void addPlantSprite( Plant plant ) {

	}

	private void addTrapSprite( Trap trap ) {

	}
	
	private void addBlobSprite( final Blob gas ) {
		if (gas.emitter == null) {
			gases.add( new BlobEmitter( gas ) );
		}
	}
	
	private synchronized void addMobSprite( Mob mob ) {
		CharSprite sprite = mob.sprite();
		sprite.visible = Dungeon.visibleforAnyHero(mob.pos);
		mobs.add( sprite );
		sprite.link( mob );
		sortMobSprites();
	}

	//ensures that mob sprites are drawn from top to bottom, in case of overlap
	public static void sortMobSprites(){
		if (scene != null){
			synchronized (scene) {
				scene.mobs.sort(new Comparator() {
					@Override
					public int compare(Object a, Object b) {
						//elements that aren't visual go to the end of the list
						if (a instanceof Visual && b instanceof Visual) {
							return (int) Math.signum((((Visual) a).y + ((Visual) a).height())
									- (((Visual) b).y + ((Visual) b).height()));
						} else if (a instanceof Visual){
							return -1;
						} else if (b instanceof Visual){
							return 1;
						} else {
							return 0;
						}
					}
				});
			}
		}
	}
	
	private synchronized void prompt( String text ) {
		
		if (prompt != null) {
			prompt.killAndErase();
			toDestroy.add(prompt);
			prompt = null;
		}
		
		if (text != null) {
			prompt = new Toast( text ) {
				@Override
				protected void onClose() {
					cancel();
				}
			};
			prompt.camera = uiCamera;
			prompt.setPos( (uiCamera.width - prompt.width()) / 2, uiCamera.height - 60 );

			add( prompt );
		}
	}
	
	private void showBanner( Banner banner ) {
		banner.camera = uiCamera;

		float offset = Camera.main.centerOffset.y;
		banner.x = align( uiCamera, (uiCamera.width - banner.width) / 2 );
		banner.y = align( uiCamera, (uiCamera.height - banner.height) / 2 - banner.height/2 - 16 - offset );

		addToFront( banner );
	}
	
	// -------------------------------------------------------

	public static void add( Plant plant ) {
		if (scene != null) {
			scene.addPlantSprite( plant );
		}
	}

	public static void add( Trap trap ) {
		if (scene != null) {
			scene.addTrapSprite( trap );
		}
	}
	
	public static void add( Blob gas ) {
		Actor.add( gas );
		if (scene != null) {
			scene.addBlobSprite( gas );
		}
	}
	
	public static void add( Heap heap ) {
		if (scene != null) {
			//heaps that aren't added as part of levelgen don't count for exploration bonus
			heap.autoExplored = true;
			scene.addHeapSprite( heap );
		}
	}
	
	public static void discard( Heap heap ) {
		if (scene != null) {
			scene.addDiscardedSprite( heap );
		}
	}
	
	public static void add( Mob mob ) {
		Dungeon.level.mobs.add( mob );
		if (scene != null) {
			scene.addMobSprite(mob);
			Actor.add(mob);
		}
	}

	public static void addSprite( Mob mob ) {
		scene.addMobSprite( mob );
	}
	
	public static void add( Mob mob, float delay ) {
		Dungeon.level.mobs.add( mob );
		scene.addMobSprite( mob );
		Actor.addDelayed( mob, delay );
	}
	
	public static void add( EmoIcon icon ) {
		scene.emoicons.add( icon );
	}
	
	public static void add( CharHealthIndicator indicator ){
		if (scene != null) scene.healthIndicators.add(indicator);
	}
	
	public static void add( CustomTilemap t, boolean wall ){
		if (scene == null) return;
		if (wall){
			scene.addCustomWall(t);
		} else {
			scene.addCustomTile(t);
		}
	}
	
	public static void effect( Visual effect ) {
		if (scene != null) scene.effects.add( effect );
	}

	public static void effectOverFog( Visual effect ) {
		scene.overFogEffects.add( effect );
	}
	
	public static Ripple ripple( int pos ) {
		if (scene != null) {
			Ripple ripple = (Ripple) scene.ripples.recycle(Ripple.class);
			ripple.reset(pos);
			return ripple;
		} else {
			return null;
		}
	}
	
	public static synchronized SpellSprite spellSprite() {
		return (SpellSprite)scene.spells.recycle( SpellSprite.class );
	}
	
	public static synchronized Emitter emitter() {
		if (scene != null) {
			Emitter emitter = (Emitter)scene.emitters.recycle( Emitter.class );
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}

	public static synchronized Emitter floorEmitter() {
		if (scene != null) {
			Emitter emitter = (Emitter)scene.floorEmitters.recycle( Emitter.class );
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}
	
	public static FloatingText status() {
		return scene != null ? (FloatingText)scene.statuses.recycle( FloatingText.class ) : null;
	}
	
	public static void pickUp( Item item, int pos ) {
		if (scene != null) scene.toolbar.pickup( item, pos );
	}

	public static void pickUpJournal( Item item, int pos ) {
		if (scene != null) scene.menu.pickup( item, pos );
	}

	public static void flashForDocument( Document doc, String page ){
		if (scene != null) {
			scene.menu.flashForPage( doc, page );
		}
	}

	public static void endIntro(){
		if (scene != null){
			SPDSettings.intro(false);
			scene.add(new Tweener(scene, 2f){
				@Override
				protected void updateValues(float progress) {
					if (progress <= 0.5f) {
						scene.status.alpha(2*progress);
						scene.status.visible = scene.status.active = true;
						scene.toolbar.visible = scene.toolbar.active = false;
					} else {
						scene.status.alpha(1f);
						scene.status.visible = scene.status.active = true;
						scene.toolbar.alpha((progress - 0.5f)*2);
						scene.toolbar.visible = scene.toolbar.active = true;
					}
				}
			});
			GameLog.wipe();
			if (SPDSettings.interfaceSize() == 0){
				GLog.p(Messages.get(GameScene.class, "tutorial_ui_mobile"));
			} else {
				GLog.p(Messages.get(GameScene.class, "tutorial_ui_desktop",
						KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.HERO_INFO, ControllerHandler.isControllerConnected())),
						KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.INVENTORY, ControllerHandler.isControllerConnected()))));
			}

			//clear hidden doors, it's floor 1 so there are only the entrance ones
			for (int i = 0; i < Dungeon.level.length(); i++){
				if (Dungeon.level.map[i] == Terrain.SECRET_DOOR){
					Dungeon.level.discover(i);
					discoverTile(i, Terrain.SECRET_DOOR);
				}
			}
		}
	}
	
	public static void updateKeyDisplay(){
		if (scene != null) scene.menu.updateKeys();
	}

	public static void showlevelUpStars(){
		if (scene != null) scene.status.showStarParticles();
	}

	public static void resetMap() {
		if (scene != null) {
			scene.tiles.map(Dungeon.level.map, Dungeon.level.width() );
			scene.visualGrid.map(Dungeon.level.map, Dungeon.level.width() );
			scene.terrainFeatures.map(Dungeon.level.map, Dungeon.level.width() );
			scene.raisedTerrain.map(Dungeon.level.map, Dungeon.level.width() );
			scene.walls.map(Dungeon.level.map, Dungeon.level.width() );
		}
		updateFog();
	}

	//updates the whole map
	public static void updateMap() {
		if (scene != null) {
			scene.tiles.updateMap();
			scene.visualGrid.updateMap();
			scene.terrainFeatures.updateMap();
			scene.raisedTerrain.updateMap();
			scene.walls.updateMap();
			updateFog();
		}
	}
	
	public static void updateMap( int cell ) {
		if (scene != null) {
			scene.tiles.updateMapCell( cell );
			scene.visualGrid.updateMapCell( cell );
			scene.terrainFeatures.updateMapCell( cell );
			scene.raisedTerrain.updateMapCell( cell );
			scene.walls.updateMapCell( cell );
			//update adjacent cells too
			updateFog( cell, 1 );
		}
	}

	public static void plantSeed( int cell ) {
		if (scene != null) {
			scene.terrainFeatures.growPlant( cell );
		}
	}
	
	public static void discoverTile( int pos, int oldValue ) {
		if (scene != null) {
			scene.tiles.discover( pos, oldValue );
		}
	}
	
	public static void show( Window wnd ) {
		if (scene != null) {
			cancel();
			scene.addToFront(wnd);
		}
	}

	public static boolean showingWindow(){
		if (scene == null) return false;

		for (Gizmo g : scene.members){
			if (g instanceof Window) return true;
		}

		return false;
	}

	public static boolean interfaceBlockingHero(){
		if (scene == null) return false;

		if (showingWindow()) return true;

		return false;
	}

	public static void centerNextWndOnInvPane(){

	}

	public static void updateFog(){
		if (scene != null) {
			scene.fog.updateFog();
			scene.wallBlocking.updateMap();
		}
	}

	public static void updateFog(int x, int y, int w, int h){
		if (scene != null) {
			scene.fog.updateFogArea(x, y, w, h);
			scene.wallBlocking.updateArea(x, y, w, h);
		}
	}
	
	public static void updateFog( int cell, int radius ){
		if (scene != null) {
			scene.fog.updateFog( cell, radius );
			scene.wallBlocking.updateArea( cell, radius );
		}
	}
	
	public static void afterObserve() {
		if (scene != null) {
			boolean stealthyMimics = MimicTooth.stealthyMimics();
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob.sprite != null) {
					if (stealthyMimics && mob instanceof Mimic && mob.state == mob.PASSIVE && mob.sprite.visible){
						//mimics stay visible in fog of war after being first seen
						mob.sprite.visible = true;
					} else {
						mob.sprite.visible = Dungeon.visibleforAnyHero(mob.pos);
					}
				}
				if (mob instanceof Ghoul){
					for (Ghoul.GhoulLifeLink link : mob.buffs(Ghoul.GhoulLifeLink.class)){
						link.updateVisibility();
					}
				}
			}
		}
	}

	public static void flash( int color ) {
		flash( color, true);
	}

	public static void flash( int color, boolean lightmode ) {
		if (scene != null) {
			//greater than 0 to account for negative values (which have the first bit set to 1)
			if (color > 0 && color < 0x01000000) {
				scene.fadeIn(0xFF000000 | color, lightmode);
			} else {
				scene.fadeIn(color, lightmode);
			}
		}
	}
	@Deprecated
	public static void gameOver() {

	}

	//FIXME
	public static void bossSlain() {
			Banner bossSlain = new Banner( BannerSprites.get( BannerSprites.Type.BOSS_SLAIN ) );
			bossSlain.show( 0xFFFFFF, 0.3f, 5f );
			scene.showBanner( bossSlain );
			
			Sample.INSTANCE.play( Assets.Sounds.BOSS );

	}
	
	public static void handleCell( int cell ) {
		cellSelector.select( cell, PointerEvent.LEFT );
	}
	
	public static void selectCell( CellSelector.Listener listener ) {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener){
			cellSelector.listener.onSelect(null);
		}
		cellSelector.listener = listener;
		//FIXME
		cellSelector.enabled = Dungeon.heroes.ready;
		if (scene != null) {
			scene.prompt(listener.prompt());
		}
	}
	
	public static boolean cancelCellSelector() {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
			cellSelector.resetKeyHold();
			cellSelector.cancel();
			return true;
		} else {
			return false;
		}
	}
	
	public static WndBag selectItem( WndBag.ItemSelector listener, Hero hero ) {
		cancel();

		if (scene != null) {
			{
				WndBag wnd = WndBag.getBag( listener, hero );
				show(wnd);
				return wnd;
			}
		}
		
		return null;
	}
	
	public static boolean cancel() {
		cellSelector.resetKeyHold();
		for(Hero hero: Dungeon.heroes) {
			if (hero != null && (hero.curAction != null || hero.resting)) {

				hero.curAction = null;
				hero.resting = false;
				return true;

			} else {

				return cancelCellSelector();

			}
		}
		return cancelCellSelector();
	}
	
	public static void ready() {
		selectCell( defaultCellListener );
		QuickSlotButton.cancel();
		if (scene != null && scene.toolbar != null) scene.toolbar.examining = false;
		if (tagDisappeared) {
			tagDisappeared = false;
			updateTags = true;
		}
	}
	//FIXME
	public static void checkKeyHold(){
		cellSelector.processKeyHold();
	}
	
	public static void resetKeyHold(){
		cellSelector.resetKeyHold();
	}

	public static void examineCell( Integer cell ) {
		if (cell == null
				|| cell < 0
				|| cell > Dungeon.level.length()
				|| (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell])) {
			return;
		}

		ArrayList<Object> objects = getObjectsAtCell(cell);

		if (objects.isEmpty()) {
			GameScene.show(new WndInfoCell(cell));
		} else if (objects.size() == 1){
			examineObject(objects.get(0));
		} else {
			String[] names = getObjectNames(objects).toArray(new String[0]);

			GameScene.show(new WndOptions(Icons.get(Icons.INFO),
					Messages.get(GameScene.class, "choose_examine"),
					Messages.get(GameScene.class, "multiple_examine"),
					names){
				@Override
				protected void onSelect(int index) {
					examineObject(objects.get(index));
				}
			});

		}
	}

	private static ArrayList<Object> getObjectsAtCell( int cell ){
		ArrayList<Object> objects = new ArrayList<>();
		for(Hero hero: Dungeon.heroes) {
			if (hero != null) {
				if (cell == hero.pos) {
					objects.add(Dungeon.heroes);

				} else if (Dungeon.visibleforAnyHero(cell)) {
					Mob mob = (Mob) Actor.findChar(cell);
					if (mob != null) objects.add(mob);
				}
			}
		}
		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null && heap.seen) objects.add(heap);

		Plant plant = Dungeon.level.plants.get( cell );
		if (plant != null) objects.add(plant);

		Trap trap = Dungeon.level.traps.get( cell );
		if (trap != null && trap.visible) objects.add(trap);

		return objects;
	}

	private static ArrayList<String> getObjectNames( ArrayList<Object> objects ){
		ArrayList<String> names = new ArrayList<>();
		for (Object obj : objects){
			if (obj instanceof Hero)        names.add(((Hero) obj).className().toUpperCase(Locale.ENGLISH));
			else if (obj instanceof Mob)    names.add(Messages.titleCase( ((Mob)obj).name() ));
			else if (obj instanceof Heap)   names.add(Messages.titleCase( ((Heap)obj).title() ));
			else if (obj instanceof Plant)  names.add(Messages.titleCase( ((Plant) obj).name() ));
			else if (obj instanceof Trap)   names.add(Messages.titleCase( ((Trap) obj).name() ));
		}
		return names;
	}

	public static void examineObject(Object o){
		if (o instanceof Hero){
			GameScene.show( new WndHero() );
		} else if ( o instanceof Mob && ((Mob) o).isActive() ){
			GameScene.show(new WndInfoMob((Mob) o));
			if (o instanceof Snake && !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_SURPRISE_ATKS)){
				GLog.p(Messages.get(Guidebook.class, "hint"));
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_SURPRISE_ATKS);
			}
		} else if ( o instanceof Heap && !((Heap) o).isEmpty() ){
			GameScene.show(new WndInfoItem((Heap)o));
		} else if ( o instanceof Plant ){
			GameScene.show( new WndInfoPlant((Plant) o) );
		} else if ( o instanceof Trap ){
			GameScene.show( new WndInfoTrap((Trap) o));
		} else {
			GameScene.show( new WndMessage( Messages.get(GameScene.class, "dont_know") ) ) ;
		}
	}

	//FIXME
	private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (Dungeon.heroes.handle( cell )) {
				Dungeon.heroes.next();
			}
		}

		@Override
		public String prompt() {
			return null;
		}
	};
	public static void addHeroSprite(Hero hero){
		CharSprite sprite  = hero.getSprite();
		sprite.visible = true;
		sprite.link(hero);
	}

}
