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
package com.minecraftpixeldungeon.items;

import com.minecraftpixeldungeon.Assets;
import com.minecraftpixeldungeon.Dungeon;
import com.minecraftpixeldungeon.actors.hero.Hero;
import com.minecraftpixeldungeon.actors.hero.HeroClass;
import com.minecraftpixeldungeon.actors.hero.HeroSubClass;
import com.minecraftpixeldungeon.effects.Speck;
import com.minecraftpixeldungeon.sprites.CharSprite;
import com.minecraftpixeldungeon.sprites.ItemSpriteSheet;
import com.minecraftpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Dewdrop extends Item {

	private static final String TXT_VALUE	= "%+dHP";
	
	{
		name = "dewdrop";
		image = ItemSpriteSheet.DEWDROP;
		
		stackable = true;
	}
	
	@Override
	public boolean doPickUp( Hero hero ) {
		
		DewVial vial = hero.belongings.getItem( DewVial.class );
		
		if (hero.HP < hero.HT || vial == null || vial.isFull()) {
			
			int value = 1 + (Dungeon.depth - 1) / 5;
			if (hero.subClass == HeroSubClass.WARDEN) {
				value+=2;
			}
			
			int effect = Math.min( hero.HT - hero.HP, value * quantity );
			if (effect > 0) {
				hero.HP += effect;
				hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
				hero.sprite.showStatus( CharSprite.POSITIVE, TXT_VALUE, effect );
			} else {
				GLog.i("You already have full health.");
				return false;
			}
			
		} else {
			
			vial.collectDew( this );
			
		}
		
		Sample.INSTANCE.play( Assets.SND_DEWDROP );
		hero.spendAndNext( TIME_TO_PICK_UP );
		
		return true;
	}

	@Override
	//max of one dew in a stack
	public Item quantity(int value) {
		quantity = Math.min( value, 1);
		return this;
	}

	@Override
	public String info() {
		return "A crystal clear dewdrop.\n\nDue to the magic of this place, pure water has minor restorative properties.";
	}
}
