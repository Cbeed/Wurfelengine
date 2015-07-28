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
package com.bombinggames.caveland.GameObjects.collectibles;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.CustomPlayer;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;

/**
 *
 * @author Benedikt Vogler
 */
public class ConstructionSite extends CollectibleContainer implements Interactable  {
	private static final long serialVersionUID = 1L;
	private final byte result;
	private final CollectibleType[] neededItems;
	private final int[] neededAmount;

	/**
	 * the resulting block
	 * @param result 
	 */
	public ConstructionSite(byte result) {
		super();
		this.result = result;
		//if (result==11) {
			neededAmount = new int[]{2,1};
			neededItems = new CollectibleType[]{CollectibleType.STONE, CollectibleType.WOOD };
		//}
	}
	
	public String getStatusString(){
		String string = "";
		for (int i = 0; i < neededItems.length; i++) {
			string += count(neededItems[i])+"/"+ neededAmount[i] +" "+neededItems[i] + ", ";
		}
		return string;
	}
	
	/**
	 * transforms the construction site into the wanted building
	 * @return true if success
	 */
	public boolean build(){
		//check ingredients
		for (int i = 0; i < neededItems.length; i++) {
			if (count(neededItems[i]) < neededAmount[i])
				return false;
		}
		getPosition().toCoord().setBlock(Block.getInstance(result));
		dispose();
		return true;
	}
	
	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof CustomPlayer) {
			CollectibleContainerWindow selectionWindow = new ConstructionSiteWindow(view, this);
			selectionWindow.register(view, ((CustomPlayer) actor).getPlayerNumber());
		}
	}
	
	private class ConstructionSiteWindow extends CollectibleContainerWindow {

		public ConstructionSiteWindow(CustomGameView view, ConstructionSite parent) {
			super(view, parent);
			addSelectionNames("Build:"+ parent.getStatusString());
		}

		@Override
		public int confirm(CustomGameView view, AbstractEntity actor) {
			int num = super.confirm(view, actor);
			if (num==2) build();
			return num;
		}
	}
}