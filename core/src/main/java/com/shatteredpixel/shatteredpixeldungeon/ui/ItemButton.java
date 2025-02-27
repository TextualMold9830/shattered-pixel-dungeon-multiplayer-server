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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.NinePatch;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

//essentially a RedButton version of ItemSlot
public class ItemButton extends Component {

	protected NinePatch bg;
	protected Item item;

	@Override
	protected void createChildren() {
		super.createChildren();
	}

	protected void onClick() {}

	protected boolean onLongClick(){
		return false;
	}

	@Override
	protected void layout() {
		super.layout();

		bg.x = x;
		bg.y = y;
		bg.size( width, height );
	}

	public Item item(){
		return item;
	}

	public void item( Item item ) {
		this.item = item;
	}

	public void clear(){
		item = null;
	}

	public ItemSlot slot(){
		return slot;
	}

}
