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

import com.minecraftpixeldungeon.Assets;
import com.minecraftpixeldungeon.Dungeon;
import com.minecraftpixeldungeon.actors.Actor;
import com.minecraftpixeldungeon.actors.Char;
import com.minecraftpixeldungeon.actors.hero.Hero;
import com.minecraftpixeldungeon.effects.CellEmitter;
import com.minecraftpixeldungeon.effects.Speck;
import com.minecraftpixeldungeon.items.Heap;
import com.minecraftpixeldungeon.items.Item;
import com.minecraftpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.minecraftpixeldungeon.scenes.GameScene;
import com.minecraftpixeldungeon.sprites.TrapSprite;
import com.minecraftpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.LinkedList;

public class TeleportationTrap extends Trap {

	{
		name = "Teleportation trap";
		color = TrapSprite.TEAL;
		shape = TrapSprite.DOTS;
	}

	@Override
	public void activate() {

		CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		Sample.INSTANCE.play( Assets.SND_TELEPORT );

		Char ch = Actor.findChar( pos);
		if (ch instanceof Hero){
			ScrollOfTeleportation.teleportHero( (Hero)ch);
		} else if (ch != null){
			int count = 10;
			int pos;
			do {
				pos = Dungeon.level.randomRespawnCell();
				if (count-- <= 0) {
					break;
				}
			} while (pos == -1);

			if (pos == -1 || Dungeon.bossLevel()) {

				GLog.w(ScrollOfTeleportation.TXT_NO_TELEPORT);

			} else {

				ch.pos = pos;
				ch.sprite.place(ch.pos);
				ch.sprite.visible = Dungeon.visible[pos];

			}
		}

		Heap heap = Dungeon.level.heaps.get(pos);

		if (heap != null){
			int cell = Dungeon.level.randomRespawnCell();

			Item item = heap.pickUp();

			if (cell != -1) {
				Dungeon.level.drop( item, cell );
			}
		}
	}

	@Override
	public String desc() {
		return "Whatever triggers this trap will be warped to some other location on this floor.";
	}
}