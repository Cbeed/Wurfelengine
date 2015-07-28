/*
 *
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Gameobjects.Side;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 *
 * @author Benedikt Vogler
 */
public class Torch extends RenderBlock {
	private static final long serialVersionUID = 1L;
	public final static int RADIUS = 3;
	public final static float POINTRADIUS = 3*Block.GAME_EDGELENGTH;
	public final static float BRIGHTNESS = 8f;
	private boolean postSpawnUpdate = false;

	
	public Torch(Block data){
		super(data);
		lightNearbyBlocks();
	}

	@Override
	public RenderBlock spawn(Coordinate coord) {
		RenderBlock a = super.spawn(coord);
		lightNearbyBlocks();
		return a; 
	}
	
	public void lightNearbyBlocks(){
		if (getPosition()!=null) {
			Point pos = getPosition().toPoint();
			float flicker = (float) Math.random();
			//light blocks under the torch
			for (int x = -RADIUS; x < RADIUS; x++) {
				for (int y = -RADIUS*2; y < RADIUS*2; y++) {
					Block block = getPosition().cpy().addVector(x, y, -1).getBlock();
					if (block!=null) {
						float pow = pos.distanceTo(getPosition().cpy().addVector(x, y, 0).toPoint())/(float) Block.GAME_EDGELENGTH+1;
						float l  = (1 +BRIGHTNESS) / (pow*pow);
						block.addLightlevel(l*(0.15f+flicker*0.03f), Side.TOP,0);
						block.addLightlevel(l*(0.15f+flicker*0.005f), Side.TOP,1);
						block.addLightlevel(l*(0.15f+flicker*0.005f), Side.TOP,2);
					}
				}
			}
		}
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		lightNearbyBlocks();
	}
	
}
