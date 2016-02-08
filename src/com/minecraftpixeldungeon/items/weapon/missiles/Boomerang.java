/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
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
package com.minecraftpixeldungeon.items.weapon.missiles;

import com.minecraftpixeldungeon.Dungeon;
import com.minecraftpixeldungeon.actors.Char;
import com.minecraftpixeldungeon.actors.hero.Hero;
import com.minecraftpixeldungeon.items.Item;
import com.minecraftpixeldungeon.sprites.ItemSpriteSheet;
import com.minecraftpixeldungeon.sprites.MissileSprite;

public class Boomerang extends MissileWeapon {

	{
		name = "boomerang";
		image = ItemSpriteSheet.BOOMERANG;
		
		STR = 10;

		stackable = false;

		unique = true;
		bones = false;
	}

	@Override
	public int min() {
		return 1 + level();
	}

	@Override
	public int max() {
		return 5 + 2 * level();
	}

	@Override
	public boolean isUpgradable() {
		return true;
	}
	
	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	@Override
	public Item upgrade( boolean enchant ) {
		super.upgrade( enchant );
		
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public Item degrade() {
		return super.degrade();
	}

	@Override
	public void proc( Char attacker, Char defender, int damage ) {
		super.proc( attacker, defender, damage );
		if (attacker instanceof Hero && ((Hero)attacker).rangedWeapon == this) {
			circleBack( defender.pos, (Hero)attacker );
		}
	}

	@Override
	protected void miss( int cell ) {
		circleBack( cell, curUser );
	}

	private void circleBack( int from, Hero owner ) {

		((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
				reset( from, curUser.pos, curItem, null );

		if (throwEquiped) {
			owner.belongings.weapon = this;
			owner.spend( -TIME_TO_EQUIP );
			Dungeon.quickslot.replaceSimilar(this);
			updateQuickslot();
		} else
		if (!collect( curUser.belongings.backpack )) {
			Dungeon.level.drop( this, owner.pos ).sprite.drop();
		}
	}

	private boolean throwEquiped;

	@Override
	public void cast( Hero user, int dst ) {
		throwEquiped = isEquipped( user );
		super.cast( user, dst );
	}
	
	@Override
	public String desc() {
		String info =
			"Thrown to the enemy this flat curved wooden missile will return to the hands of its thrower.";
		switch (imbue) {
			case LIGHT:
				info += "\n\nIt was balanced to be lighter. ";
				break;
			case HEAVY:
				info += "\n\nIt was balanced to be heavier. ";
				break;
			case NONE:
		}
		return info;
	}
}