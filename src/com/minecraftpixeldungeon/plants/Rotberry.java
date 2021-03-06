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
package com.minecraftpixeldungeon.plants;

import com.minecraftpixeldungeon.Dungeon;
import com.minecraftpixeldungeon.items.potions.PotionOfStrength;
import com.minecraftpixeldungeon.sprites.ItemSpriteSheet;

public class Rotberry extends Plant {

	private static final String TXT_DESC =
			"The berries of a young rotberry shrub taste like sweet, sweet death.\n" +
					"\n" +
					"Given several days, this rotberry shrub will grow into another rot heart.";

	{
		image = 7;
		plantName = "Rotberry";
	}

	@Override
	public void activate() {
		Dungeon.level.drop( new Seed(), pos ).sprite.drop();
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed {
		{
			plantName = "Rotberry";

			name = "seed of " + plantName;
			image = ItemSpriteSheet.SEED_ROTBERRY;

			plantClass = Rotberry.class;
			alchemyClass = PotionOfStrength.class;
		}

		@Override
		public String desc() {
			return TXT_DESC;
		}
	}
}
