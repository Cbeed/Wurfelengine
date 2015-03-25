/*
 * Copyright 2013 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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
package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Benedikt Vogler
 */
public class EntityShadow extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * the parent class. The object where this is the shadow
	 */
    private AbstractEntity character;

	/**
	 *
	 * @param character
	 */
	protected EntityShadow(AbstractEntity character) {
		super(32);
		this.character = character;
		setSaveToDisk(false);
    }

    @Override
    public void update(float dt) {
		setColor(
			new Color(.5f, .5f, .5f, 1-(character.getPosition().getZ()- getPosition().getZ())/4/Block.GAME_EDGELENGTH)
		);
		if (character==null)
			dispose();
		else {
			//find height of shadow surface
			Coordinate tmpPos = character.getPosition().getCoord().cpy();
			if (tmpPos.isInMemoryArea()){
				tmpPos.setZ(tmpPos.getZ());//to clamp to grid
				while (tmpPos.getZ() > 0 && tmpPos.cpy().addVector(new float[]{0, 0, -1}).getBlock().isTransparent())
					tmpPos.addVector(new float[]{0, 0, -1});

				setPosition(character.getPosition().cpy());
				getPosition().setZ(tmpPos.getPoint().getZ());
			}
		}
    }

    @Override
    public void render(GameView view, Camera camera) {
		if (!shouldBeDisposed()){
			super.render(view, camera);
		}
    }
}