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
import com.minecraftpixeldungeon.actors.blobs.Blob;
import com.minecraftpixeldungeon.actors.blobs.ConfusionGas;
import com.minecraftpixeldungeon.scenes.GameScene;
import com.minecraftpixeldungeon.sprites.TrapSprite;

public class ConfusionTrap extends Trap {

	{
		name = "Confusion gas trap";
		color = TrapSprite.TEAL;
		shape = TrapSprite.GRILL;
	}

	@Override
	public void activate() {

		GameScene.add(Blob.seed(pos, 300 + 20 * Dungeon.depth, ConfusionGas.class));

	}

	@Override
	public String desc() {
		return "Triggering this trap will set a cloud of confusion gas loose within the immediate area.";
	}
}
