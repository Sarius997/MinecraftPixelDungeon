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
package com.minecraftpixeldungeon.levels.traps;

import com.minecraftpixeldungeon.Dungeon;
import com.minecraftpixeldungeon.ResultDescriptions;
import com.minecraftpixeldungeon.actors.Actor;
import com.minecraftpixeldungeon.actors.Char;
import com.minecraftpixeldungeon.effects.CellEmitter;
import com.minecraftpixeldungeon.effects.Lightning;
import com.minecraftpixeldungeon.effects.SpellSprite;
import com.minecraftpixeldungeon.effects.particles.SparkParticle;
import com.minecraftpixeldungeon.items.Heap;
import com.minecraftpixeldungeon.items.Item;
import com.minecraftpixeldungeon.items.wands.Wand;
import com.minecraftpixeldungeon.items.weapon.melee.MagesStaff;
import com.minecraftpixeldungeon.levels.Level;
import com.minecraftpixeldungeon.sprites.TrapSprite;
import com.minecraftpixeldungeon.utils.GLog;
import com.minecraftpixeldungeon.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class LightningTrap extends Trap {

	{
		name = "Lightning trap";
		color = TrapSprite.TEAL;
		shape = TrapSprite.CROSSHAIR;
	}

	@Override
	public void activate() {

		Char ch = Actor.findChar( pos );

		if (ch != null) {
			ch.damage( Math.max( 1, Random.Int( ch.HP / 3, 2 * ch.HP / 3 ) ), LIGHTNING );
			if (ch == Dungeon.hero) {

				Camera.main.shake( 2, 0.3f );

				if (!ch.isAlive()) {
					Dungeon.fail( Utils.format( ResultDescriptions.TRAP, name ) );
					GLog.n( "You were killed by a discharge of a lightning trap..." );
				}
			}

			ArrayList<Lightning.Arc> arcs = new ArrayList<>();
			arcs.add(new Lightning.Arc(pos - Level.WIDTH, pos + Level.WIDTH));
			arcs.add(new Lightning.Arc(pos - 1, pos + 1));

			ch.sprite.parent.add( new Lightning( arcs, null ) );
		}

		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null){
			//TODO: this should probably charge staffs too
			Item item = heap.items.peek();
			if (item instanceof Wand){
				Wand wand = (Wand)item;
				((Wand)item).curCharges += (int)Math.ceil((wand.maxCharges - wand.curCharges)/2f);
			}
		}

		CellEmitter.center( pos ).burst( SparkParticle.FACTORY, Random.IntRange( 3, 4 ) );
	}

	//FIXME: this is bad, handle when you rework resistances, make into a category
	public static final Electricity LIGHTNING = new Electricity();
	public static class Electricity {
	}

	@Override
	public String desc() {
		return "A mechanism with a large amount of energy stored into it. " +
				"Triggering the trap will discharge that energy into whatever activates it.";
	}
}
