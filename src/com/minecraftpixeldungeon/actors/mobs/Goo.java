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

import java.util.HashSet;

import com.minecraftpixeldungeon.Assets;
import com.minecraftpixeldungeon.Badges;
import com.minecraftpixeldungeon.Dungeon;
import com.minecraftpixeldungeon.actors.Char;
import com.minecraftpixeldungeon.actors.blobs.Blob;
import com.minecraftpixeldungeon.actors.blobs.GooWarn;
import com.minecraftpixeldungeon.actors.blobs.ToxicGas;
import com.minecraftpixeldungeon.actors.buffs.Buff;
import com.minecraftpixeldungeon.actors.buffs.LockedFloor;
import com.minecraftpixeldungeon.actors.buffs.Ooze;
import com.minecraftpixeldungeon.effects.CellEmitter;
import com.minecraftpixeldungeon.effects.Speck;
import com.minecraftpixeldungeon.effects.particles.ElmoParticle;
import com.minecraftpixeldungeon.items.artifacts.LloydsBeacon;
import com.minecraftpixeldungeon.items.keys.SkeletonKey;
import com.minecraftpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.minecraftpixeldungeon.items.weapon.enchantments.Death;
import com.minecraftpixeldungeon.levels.Level;
import com.minecraftpixeldungeon.levels.SewerBossLevel;
import com.minecraftpixeldungeon.scenes.GameScene;
import com.minecraftpixeldungeon.sprites.CharSprite;
import com.minecraftpixeldungeon.sprites.GooSprite;
import com.minecraftpixeldungeon.ui.BossHealthBar;
import com.minecraftpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Goo extends Mob {
	{
		name = "Goo";
		HP = HT = 100;
		EXP = 10;
		defenseSkill = 8;
		spriteClass = GooSprite.class;

		loot = new LloydsBeacon().identify();
		lootChance = 0.333f;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
	}

	private int pumpedUp = 0;

	@Override
	public int damageRoll() {
		int min = (HP*2 <= HT) ? 3 : 2;
		int max = (HP*2 <= HT) ? 12 : 8;
		if (pumpedUp > 0) {
			pumpedUp = 0;
			for (int i = 0; i < Level.NEIGHBOURS9DIST2.length; i++) {
				int j = pos + Level.NEIGHBOURS9DIST2[i];
				if (Level.insideMap(j) && Level.passable[j])
					CellEmitter.get(j).burst(ElmoParticle.FACTORY, 10);
			}
			Sample.INSTANCE.play( Assets.SND_BURNING );
			return Random.NormalIntRange( min*3, max*3 );
		} else {
			return Random.NormalIntRange( min, max );
		}
	}

	@Override
	public int attackSkill( Char target ) {
		int attack = 10;
		if (HP*2 <= HT) attack = 15;
		if (pumpedUp > 0) attack *= 2;
		return attack;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int)(super.defenseSkill(enemy) * ((HP*2 <= HT)? 1.5 : 1));
	}

	@Override
	public int dr() {
		return 2;
	}

	@Override
	public boolean act() {

		if (Level.water[pos] && HP < HT) {
			sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			if (HP*2 == HT) {
				BossHealthBar.bleed(false);
				((GooSprite)sprite).spray(false);
			}
			HP++;
		}

		return super.act();
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return (pumpedUp > 0) ? distance( enemy ) <= 2 : super.canAttack(enemy);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int( 3 ) == 0) {
			Buff.affect( enemy, Ooze.class );
			enemy.sprite.burst( 0x000000, 5 );
		}

		if (pumpedUp > 0) {
			Camera.main.shake( 3, 0.2f );
		}

		return damage;
	}

	@Override
	protected boolean doAttack( Char enemy ) {
		if (pumpedUp == 1) {
			((GooSprite)sprite).pumpUp();
			for (int i = 0; i < Level.NEIGHBOURS9DIST2.length; i++) {
				int j = pos + Level.NEIGHBOURS9DIST2[i];
				if (Level.insideMap(j) && Level.passable[j])
					GameScene.add(Blob.seed(j, 2, GooWarn.class));
			}
			pumpedUp++;

			spend( attackDelay() );

			return true;
		} else if (pumpedUp >= 2 || Random.Int( (HP*2 <= HT) ? 2 : 5 ) > 0) {

			boolean visible = Dungeon.visible[pos];

			if (visible) {
				if (pumpedUp >= 2) {
					((GooSprite) sprite).pumpAttack();
				}
				else
					sprite.attack( enemy.pos );
			} else {
				attack( enemy );
			}

			spend( attackDelay() );

			return !visible;

		} else {

			pumpedUp++;

			((GooSprite)sprite).pumpUp();

			for (int i=0; i < Level.NEIGHBOURS9.length; i++) {
				int j = pos + Level.NEIGHBOURS9[i];
				if (Level.passable[j]) {
					GameScene.add(Blob.seed(j, 2, GooWarn.class));
				}
			}

			if (Dungeon.visible[pos]) {
				sprite.showStatus( CharSprite.NEGATIVE, "!!!" );
				GLog.n( "Goo is pumping itself up!" );
			}

			spend( attackDelay() );

			return true;
		}
	}

	@Override
	public boolean attack( Char enemy ) {
		boolean result = super.attack( enemy );
		pumpedUp = 0;
		return result;
	}

	@Override
	protected boolean getCloser( int target ) {
		pumpedUp = 0;
		return super.getCloser( target );
	}
	
	@Override
	public void move( int step ) {
		Dungeon.level.seal();
		super.move( step );
	}

	@Override
	public void damage(int dmg, Object src) {
		boolean bleeding = (HP*2 <= HT);
		super.damage(dmg, src);
		if ((HP*2 <= HT) && !bleeding){
			BossHealthBar.bleed(true);
			GLog.w("Goo Becomes Enraged!!");
			sprite.showStatus(CharSprite.NEGATIVE, "enraged");
			((GooSprite)sprite).spray(true);
			yell("GLUUUURP!");
			spend( TICK );
		}
		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*2);
	}

	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		Dungeon.level.unseal();
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop();
		
		Badges.validateBossSlain();
		
		yell( "glurp... glurp..." );
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell( "GLURP-GLURP!" );
	}
	
	@Override
	public String description() {
		return
			"Little is known about The Goo. It's quite possible that it is not even a creature, but rather a " +
			"conglomerate of vile substances from the sewers that somehow gained basic intelligence. " +
			"Regardless, dark magic is certainly what has allowed Goo to exist.\n\n" +
			"Its gelatinous nature has let it absorb lots of dark energy, you feel a chill just from being near. " +
			"If goo is able to attack with this energy you won't live for long.";
	}

	private final String PUMPEDUP = "pumpedup";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		bundle.put( PUMPEDUP , pumpedUp );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		super.restoreFromBundle( bundle );

		pumpedUp = bundle.getInt( PUMPEDUP );
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
		if ((HP*2 <= HT)) BossHealthBar.bleed(true);

	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}