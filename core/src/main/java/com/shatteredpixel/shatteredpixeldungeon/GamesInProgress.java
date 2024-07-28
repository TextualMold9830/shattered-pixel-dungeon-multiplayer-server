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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class GamesInProgress {
	
	public static final int MAX_SLOTS = HeroClass.values().length;
	
	//null means we have loaded info and it is empty, no entry means unknown.
	private static HashMap<Integer, Info> slotStates = new HashMap<>();
	public static int curSlot;
	
	public static HeroClass selectedClass;
	
	private static final String GAME_FOLDER = "game%d";
	private static final String GAME_FILE	= "game.dat";
	private static final String DEPTH_FILE	= "depth%d.dat";
	private static final String DEPTH_BRANCH_FILE	= "depth%d-branch%d.dat";
	@Deprecated
	public static boolean gameExists(int slot){
		return FileUtils.dirExists(gameFolder())
				&& FileUtils.fileLength(gameFile()) > 1;
	}
	public static boolean gameExists(){
		return FileUtils.dirExists(gameFolder())
				&& FileUtils.fileLength(gameFile()) > 1;
	}

	public static String gameFolder(){
		return Messages.format(GAME_FOLDER);
	}
	
	public static String gameFile( ){
		return gameFolder() + "/" + GAME_FILE;
	}
	
	public static String depthFile(int depth, int branch ) {
		if (branch == 0) {
			return gameFolder() + "/" + Messages.format(DEPTH_FILE, depth);
		} else {
			return gameFolder() + "/" + Messages.format(DEPTH_BRANCH_FILE, depth, branch);
		}
	}
	
	public static int firstEmpty(){
		for (int i = 1; i <= MAX_SLOTS; i++){
			if (check(i) == null) return i;
		}
		return -1;
	}
	
	public static ArrayList<Info> checkAll(){
		ArrayList<Info> result = new ArrayList<>();
		for (int i = 1; i <= MAX_SLOTS; i++){
			Info curr = check(i);
			if (curr != null) result.add(curr);
		}
		Collections.sort(result, scoreComparator);
		return result;
	}
	
	public static Info check( int slot ) {
		
		if (slotStates.containsKey( slot )) {
			
			return slotStates.get( slot );
			
		} else if (!gameExists( slot )) {
			
			slotStates.put(slot, null);
			return null;
			
		} else {
			
			Info info;
			try {
				
				Bundle bundle = FileUtils.bundleFromFile(gameFile());
				info = new Info();
				info.slot = slot;
				Dungeon.preview(info, bundle);
				
				//saves from before v1.4.3 are not supported
				if (info.version < ShatteredPixelDungeon.v1_4_3) {
					info = null;
				}

			} catch (IOException e) {
				info = null;
			} catch (Exception e){
				ShatteredPixelDungeon.reportException( e );
				info = null;
			}
			
			slotStates.put( slot, info );
			return info;
			
		}
	}

	public static void set(int slot) {
		Info info = new Info();
		info.slot = slot;
		
		info.depth = Dungeon.depth;
		info.challenges = Dungeon.challenges;

		info.seed = Dungeon.seed;
		info.customSeed = Dungeon.customSeedText;
		info.daily = Dungeon.daily;
		info.dailyReplay = Dungeon.dailyReplay;
		
//		info.level = Dungeon.heroes.lvl;
//		info.str = Dungeon.heroes.STR;
//		info.strBonus = Dungeon.heroes.STR() - Dungeon.heroes.STR;
//		info.exp = Dungeon.heroes.exp;
//		info.hp = Dungeon.heroes.HP;
//		info.ht = Dungeon.heroes.HT;
//		info.shld = Dungeon.heroes.shielding();
//		info.heroClass = Dungeon.heroes.heroClass;
//		info.subClass = Dungeon.heroes.subClass;
//		info.armorTier = Dungeon.heroes.tier();
//
		info.goldCollected = Statistics.goldCollected;
		info.maxDepth = Statistics.deepestFloor;

		slotStates.put( slot, info );
	}
	
	public static void setUnknown( int slot ) {
		slotStates.remove( slot );
	}
	
	public static void delete( int slot ) {
		slotStates.put(0, null );
	}
	public static void delete() {
		slotStates.put(0, null );
	}

	public static class Info {
		public int slot;
		
		public int depth;
		public int version;
		public int challenges;

		public long seed;
		public String customSeed;
		public boolean daily;
		public boolean dailyReplay;

		public int level;
		public int str;
		public int strBonus;
		public int exp;
		public int hp;
		public int ht;
		public int shld;
		public HeroClass heroClass;
		public HeroSubClass subClass;
		public int armorTier;
		
		public int goldCollected;
		public int maxDepth;
	}
	
	public static final Comparator<GamesInProgress.Info> scoreComparator = new Comparator<GamesInProgress.Info>() {
		@Override
		public int compare(GamesInProgress.Info lhs, GamesInProgress.Info rhs ) {
			int lScore = (lhs.level * lhs.maxDepth * 100) + lhs.goldCollected;
			int rScore = (rhs.level * rhs.maxDepth * 100) + rhs.goldCollected;
			return (int)Math.signum( rScore - lScore );
		}
	};
}
