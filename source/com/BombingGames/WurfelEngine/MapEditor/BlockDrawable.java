/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
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

package com.BombingGames.WurfelEngine.MapEditor;

import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * a class what renders a block using the drawableinterface.
 * @author Benedikt Vogler
 */
public class BlockDrawable extends TextureRegionDrawable {
    private RenderBlock block;
    private float size = -0.5f;
	private float x;
    
    /**
     *
     * @param id
     */
    public BlockDrawable(byte id) {
        this.block = new RenderBlock(id,(byte) 0);
		block.setScaling(size);
    }
	

	/**
	 * 
	 * @param id block id
	 * @param value block value
	 * @param size relative size
	 */
	BlockDrawable(byte id, byte value, float size) {
		this.block = new RenderBlock(id,value);
		this.size = size;
	}

	/**
	 *
	 * @param x
	 */
	public void setX(float x){
		this.x = x;
	}
	
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
		if (block.getId()!=0) {
			block.render(WE.getEngineView(), (int) (x+this.x), (int) y, null, true);
		}
    }

	/**
	 *
	 * @return
	 */
	@Override
	public float getLeftWidth() {
		return CoreData.VIEW_WIDTH2*(1f+size);
	}
	
	/**
	 *
	 * @return
	 */
	@Override
	public float getRightWidth() {
		return CoreData.VIEW_WIDTH2*(1f+size);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getTopHeight() {
		return (CoreData.VIEW_HEIGHT2+CoreData.VIEW_DEPTH2)*(1f+size);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getBottomHeight() {
		return (CoreData.VIEW_HEIGHT2+CoreData.VIEW_DEPTH2)*(1f+size);
	}
	
    /**
     *
     * @return
     */
    @Override
    public float getMinHeight() {
        return (CoreData.VIEW_HEIGHT+CoreData.VIEW_DEPTH)*(1f+size);
    }

    /**
     *
     * @return
     */
    @Override
    public float getMinWidth() {
        return CoreData.VIEW_WIDTH*(1f+size);
    }
}
