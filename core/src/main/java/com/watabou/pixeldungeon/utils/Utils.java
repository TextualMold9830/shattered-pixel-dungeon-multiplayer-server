/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.watabou.pixeldungeon.utils;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;
import java.util.Locale;

public class Utils {

	public static String capitalize( String str ) {
		return Character.toUpperCase( str.charAt( 0 ) ) + str.substring( 1 );
	}

	public static String format( String format, Object...args ) {
		try {
			return String.format(Locale.ENGLISH, format, args);
		} catch (Throwable e) {
			Gdx.app.error("Utils", "format failed with string: " + format);
			Gdx.app.error("Utils", e.toString());
			return format;
		}
	}

	public static String VOWELS	= "aoeiu";

	public static String indefinite( String noun ) {
		if (noun.length() == 0) {
			return "a";
		} else {
			return (VOWELS.indexOf( Character.toLowerCase( noun.charAt( 0 ) ) ) != -1 ? "an " : "a ") + noun;
		}
	}

	public static String toSnakeCase(String str) {
		StringBuilder builder = new StringBuilder();
		char[] arr = str.toCharArray();
		for (int i = 0; i < str.length(); i++) {
			if (Character.isUpperCase(arr[i])) {
				builder.append('_');
				builder.append(Character.toLowerCase(arr[i]));
			} else {
				builder.append(arr[i]);
			}
		}
		return builder.toString();
	}

	public static List<Integer> JsonArrayToListInteger(JSONArray arr) {
		List<Integer> res = new ArrayList<Integer>(2);
		try {
			for (int i = 0; i < arr.length(); i++) {
				res.add(arr.getInt(i));
			}
		} catch (Exception e) {
			GLog.n(e.getMessage());
			return null;
		}
		return res;
	}
}
