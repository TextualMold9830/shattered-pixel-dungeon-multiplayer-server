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

package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Signal;

import static com.shatteredpixel.shatteredpixeldungeon.network.SendData.sendMessage;

public class GLog {

	public static final String TAG = "GAME";
	
	public static final String POSITIVE		= "++ ";
	public static final String NEGATIVE		= "-- ";
	public static final String WARNING		= "** ";
	public static final String CUSTOM = "&&";
	public static final String HIGHLIGHT	= "@@ ";

	public static final String NEW_LINE	    = "\n";
	
	public static Signal<String> update = new Signal<>();

	public static void newLine(){
		update.dispatch( NEW_LINE );
	}
	
	public static void i( String text, Object... args ) {
		
		if (args.length > 0) {
			text = Messages.format( text, args );
		}
		
		DeviceCompat.log( TAG, text );
		update.dispatch( text );
		iWithTarget(null, text, args);
	}
	public static void iWithTarget( Integer ID,  String text, Object... args ) {

		if (args.length > 0) {
			text = Utils.format( text, args );
		}

		sendMessage(ID, text);
	}
	public static void withColor(String text, int color, Object... args) {
		i(CUSTOM+Integer.toHexString(color), text, args);
	}
	
	public static void p( String text, Object... args ) {
		i( POSITIVE + text, args );
	}
	
	public static void n( String text, Object... args ) {
		i( NEGATIVE + text, args );
	}
	
	public static void w( String text, Object... args ) {
		i( WARNING + text, args );
	}
	
	public static void h( String text, Object... args ) {
		i( HIGHLIGHT + text, args );
	}
}
