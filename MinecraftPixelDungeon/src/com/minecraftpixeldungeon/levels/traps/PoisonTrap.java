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
import com.minecraftpixeldungeon.actors.Actor;
import com.minecraftpixeldungeon.actors.Char;
import com.minecraftpixeldungeon.actors.buffs.Buff;
import com.minecraftpixeldungeon.actors.buffs.Poison;
import com.minecraftpixeldungeon.effects.CellEmitter;
import com.minecraftpixeldungeon.effects.particles.PoisonParticle;
import com.minecraftpixeldungeon.sprites.TrapSprite;

public class PoisonTrap extends Trap{

	{
		name = "Poison trap";
		color = TrapSprite.VIOLET;
		shape = TrapSprite.CROSSHAIR;
	}

	@Override
	public void activate() {

		Char ch = Actor.findChar( pos );

		if (ch != null) {
			Buff.affect( ch, Poison.class ).set( Poison.durationFactor( ch ) * (4 + Dungeon.depth / 2) );
		}

		CellEmitter.center( pos ).burst( PoisonParticle.SPLASH, 3 );

	}

	@Override
	public String desc() {
		return "A small dart-blower must be hidden nearby, activating this trap will cause it to shoot a poisoned dart at you.";
	}
}