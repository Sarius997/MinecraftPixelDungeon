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
package com.minecraftpixeldungeon.actors.mobs;

import java.util.ArrayList;

import com.minecraftpixeldungeon.Dungeon;
import com.minecraftpixeldungeon.actors.Actor;
import com.minecraftpixeldungeon.actors.Char;
import com.minecraftpixeldungeon.actors.buffs.Buff;
import com.minecraftpixeldungeon.actors.buffs.Burning;
import com.minecraftpixeldungeon.actors.buffs.Corruption;
import com.minecraftpixeldungeon.actors.buffs.Poison;
import com.minecraftpixeldungeon.effects.Pushing;
import com.minecraftpixeldungeon.items.Item;
import com.minecraftpixeldungeon.items.potions.PotionOfHealing;
import com.minecraftpixeldungeon.levels.Level;
import com.minecraftpixeldungeon.levels.Terrain;
import com.minecraftpixeldungeon.levels.features.Door;
import com.minecraftpixeldungeon.scenes.GameScene;
import com.minecraftpixeldungeon.sprites.SwarmSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Swarm extends Mob {

	{
		name = "swarm of flies";
		spriteClass = SwarmSprite.class;
		
		HP = HT = 50;
		defenseSkill = 5;

		EXP = 3;
		maxLvl = 9;
		
		flying = true;

		loot = new PotionOfHealing();
		lootChance = 0.1667f; //by default, see die()
	}
	
	private static final float SPLIT_DELAY	= 1f;
	
	int generation	= 0;
	
	private static final String GENERATION	= "generation";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GENERATION, generation );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt( GENERATION );
		if (generation > 0) EXP = 0;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 3 );
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (HP >= damage + 2) {
			ArrayList<Integer> candidates = new ArrayList<Integer>();
			boolean[] passable = Level.passable;
			
			int[] neighbours = {pos + 1, pos - 1, pos + Level.WIDTH, pos - Level.WIDTH};
			for (int n : neighbours) {
				if (passable[n] && Actor.findChar( n ) == null) {
					candidates.add( n );
				}
			}
	
			if (candidates.size() > 0) {
				
				Swarm clone = split();
				clone.HP = (HP - damage) / 2;
				clone.pos = Random.element( candidates );
				clone.state = clone.HUNTING;
				
				if (Dungeon.level.map[clone.pos] == Terrain.DOOR) {
					Door.enter( clone.pos );
				}
				
				GameScene.add( clone, SPLIT_DELAY );
				Actor.addDelayed( new Pushing( clone, pos, clone.pos ), -1 );
				
				HP -= clone.HP;
			}
		}
		
		return super.defenseProc(enemy, damage);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}
	
	@Override
	public String defenseVerb() {
		return "evaded";
	}
	
	private Swarm split() {
		Swarm clone = new Swarm();
		clone.generation = generation + 1;
		clone.EXP = 0;
		if (buff( Burning.class ) != null) {
			Buff.affect( clone, Burning.class ).reignite( clone );
		}
		if (buff( Poison.class ) != null) {
			Buff.affect( clone, Poison.class ).set(2);
		}
		if (buff(Corruption.class ) != null) {
			Buff.affect( clone, Corruption.class);
		}
		return clone;
	}
	
	@Override
	public void die( Object cause ){
		//sets drop chance
		lootChance = 1f/((6 + 2*Dungeon.limitedDrops.swarmHP.count ) * (generation+1) );
		super.die( cause );
	}

	@Override
	protected Item createLoot(){
		Dungeon.limitedDrops.swarmHP.count++;
		return super.createLoot();
	}
	
	@Override
	public String description() {
		return
			"The deadly swarm of flies buzzes angrily. Every non-magical attack " +
			"will split it into two smaller but equally dangerous swarms.";
	}
}
