/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractLogicBlock;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class LiftLogic extends AbstractEntity implements Interactable {
	private static final long serialVersionUID = 1L;
	
	public LiftLogic() {
		super((byte) 0);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		return this;
	}

	
	@Override
	public void update(float dt) {
		super.update(dt);
		ArrayList<MineCart> nearbyLoren = getPosition().getEntitiesNearby(2, MineCart.class);
		AbstractLogicBlock hole = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (hole != null && (hole instanceof Portal))
			nearbyLoren.forEach((l) -> {
				l.setPosition(((Portal) hole).getTarget());
			});
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
	}
	
}
