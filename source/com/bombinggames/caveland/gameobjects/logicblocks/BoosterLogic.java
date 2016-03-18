package com.bombinggames.caveland.gameobjects.logicblocks;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.PointLightSource;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class BoosterLogic extends AbstractBlockLogicExtension {

	private boolean power = false;
	private PointLightSource light;

	/**
	 *
	 *
	 * @param block
	 * @param coord
	 */
	public BoosterLogic(Block block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		if (light == null) {
			light = new PointLightSource(new Color(0.8f, 0.0f, 0.3f, 1f), 1, 12, WE.getGameplay().getView());
			light.setSaveToDisk(false);
			light.disable();
			light.spawn(getPosition().toPoint().add(0, 0, Block.GAME_EDGELENGTH2 * 0.5f));
		}
		//power surrounding cables
		power = false;
		Block neighBlock = getPosition().cpy().goToNeighbour(1).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 1
		) {
			power = true;
		}

		neighBlock = getPosition().cpy().goToNeighbour(3).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 3
		) {
			power = true;
		}

		neighBlock = getPosition().cpy().goToNeighbour(5).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 1
		) {
			power = true;
		}

		neighBlock = getPosition().cpy().goToNeighbour(7).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 3
		) {
			power = true;
		}

		if (power) {
			light.enable();
		} else {
			light.disable();
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isEnabled() {
		return power;
	}

	@Override
	public void dispose() {
		light.dispose();
	}
}