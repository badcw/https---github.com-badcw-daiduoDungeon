/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
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
package com.daiduo.lightning.levels;

import com.daiduo.lightning.Assets;
import com.daiduo.lightning.Dungeon;
import com.daiduo.lightning.DungeonTilemap;
import com.daiduo.lightning.actors.mobs.npcs.Imp;
import com.daiduo.lightning.levels.Room.Type;
import com.daiduo.lightning.levels.traps.BlazingTrap;
import com.daiduo.lightning.levels.traps.CursingTrap;
import com.daiduo.lightning.levels.traps.DisarmingTrap;
import com.daiduo.lightning.levels.traps.ExplosiveTrap;
import com.daiduo.lightning.levels.traps.FlockTrap;
import com.daiduo.lightning.levels.traps.FrostTrap;
import com.daiduo.lightning.levels.traps.GrippingTrap;
import com.daiduo.lightning.levels.traps.GuardianTrap;
import com.daiduo.lightning.levels.traps.LightningTrap;
import com.daiduo.lightning.levels.traps.OozeTrap;
import com.daiduo.lightning.levels.traps.PitfallTrap;
import com.daiduo.lightning.levels.traps.RockfallTrap;
import com.daiduo.lightning.levels.traps.SpearTrap;
import com.daiduo.lightning.levels.traps.SummoningTrap;
import com.daiduo.lightning.levels.traps.TeleportationTrap;
import com.daiduo.lightning.levels.traps.VenomTrap;
import com.daiduo.lightning.levels.traps.WarpingTrap;
import com.daiduo.lightning.levels.traps.WeakeningTrap;
import com.daiduo.lightning.messages.Messages;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class CityLevel extends RegularLevel {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_CITY;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_CITY;
	}
	
	protected boolean[] water() {
		return Patch.generate( this, feeling == Feeling.WATER ? 0.65f : 0.45f, 4 );
	}
	
	protected boolean[] grass() {
		return Patch.generate( this, feeling == Feeling.GRASS ? 0.60f : 0.40f, 3 );
	}

	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{ BlazingTrap.class, FrostTrap.class, SpearTrap.class, VenomTrap.class,
				ExplosiveTrap.class, GrippingTrap.class, LightningTrap.class, RockfallTrap.class, OozeTrap.class, WeakeningTrap.class,
				CursingTrap.class, FlockTrap.class, GuardianTrap.class, PitfallTrap.class, SummoningTrap.class, TeleportationTrap.class,
				DisarmingTrap.class, WarpingTrap.class};
	}

	@Override
	protected float[] trapChances() {
		return new float[]{ 8, 8, 8, 8,
				4, 4, 4, 4, 4, 4,
				2, 2, 2, 2, 2, 2,
				1, 1 };
	}
	
	@Override
	protected boolean assignRoomType() {
		if (!super.assignRoomType()) return false;
		
		for (Room r : rooms) {
			if (r.type == Type.TUNNEL) {
				r.type = Type.PASSAGE;
			}
		}

		return true;
	}
	
	@Override
	protected void decorate() {
		
		for (int i=0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int( 10 ) == 0) {
				map[i] = Terrain.EMPTY_DECO;
			} else if (map[i] == Terrain.WALL && Random.Int( 8 ) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		placeSign();
	}
	
	@Override
	protected void createItems() {
		super.createItems();
		
		Imp.Quest.spawn( this );
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(CityLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Messages.get(CityLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CityLevel.class, "exit_desc");
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				return Messages.get(CityLevel.class, "deco_desc");
			case Terrain.EMPTY_SP:
				return Messages.get(CityLevel.class, "sp_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(CityLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addCityVisuals( this, visuals );
		return visuals;
	}

	public static void addCityVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Smoke( i ) );
			}
		}
	}
	
	private static class Smoke extends Emitter {
		
		private int pos;
		
		private static final Emitter.Factory factory = new Factory() {
			
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				SmokeParticle p = (SmokeParticle)emitter.recycle( SmokeParticle.class );
				p.reset( x, y );
			}
		};
		
		public Smoke( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 4, p.y - 2, 4, 0 );
			
			pour( factory, 0.2f );
		}
		
		@Override
		public void update() {
			if (visible = Dungeon.visible[pos]) {
				super.update();
			}
		}
	}
	
	public static final class SmokeParticle extends PixelParticle {
		
		public SmokeParticle() {
			super();
			
			color( 0x000000 );
			speed.set( Random.Float( 8 ), -Random.Float( 8 ) );
		}
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan = 2f;
		}
		
		@Override
		public void update() {
			super.update();
			float p = left / lifespan;
			am = p > 0.8f ? 1 - p : p * 0.25f;
			size( 8 - p * 4 );
		}
	}
}