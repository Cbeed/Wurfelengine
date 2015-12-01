package com.bombinggames.caveland.GameObjects.collectibles;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.ActionBox.BoxModes;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.Game.CavelandBlocks.CLBlocks;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.logicblocks.ConstructionSite;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityBlock;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class ConstructionKit extends Collectible implements Interactable {
	private static final long serialVersionUID = 1L;
	private transient EntityBlock preview;

	public ConstructionKit() {
		super(CollectibleType.Toolkit);
	}
	
	/**
	 * 
	 * @param coord
	 * @param id the goal id
	 */
	protected void build(Coordinate coord, byte id){
		//don't allow lift on non-hole
		if (id == CLBlocks.LIFT.getId() && getPosition().toCoord().addVector(0, 0, -1).getBlock().getId() != CLBlocks.ENTRY.getId()){
			return;
		}
				
		//spawn construction site
		coord.setBlock(Block.getInstance((byte) 11));
		ConstructionSite constructionSiteLogic = (ConstructionSite) Controller.getMap().getLogic(coord);
		constructionSiteLogic.setResult(id, (byte) 0);
		WE.SOUND.play("metallic");
		dispose();//dispose tool kit
		if (preview != null) {
			preview.dispose();
			preview = null;
		}
	}
	
	byte getResult(int index){
		switch (index) {
			case 0:
				return CavelandBlocks.CLBlocks.OVEN.getId();
			case  1:
				return CavelandBlocks.CLBlocks.ROBOTFACTORY.getId();
			case 2:
				return CLBlocks.POWERSTATION.getId();
			default:
				return CavelandBlocks.CLBlocks.LIFT.getId();
		}
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			new ActionBox("Choose construction", BoxModes.SELECTION, null)
				.addSelectionNames(
					CavelandBlocks.CLBlocks.OVEN.name(),
					CavelandBlocks.CLBlocks.ROBOTFACTORY.name(),
					CavelandBlocks.CLBlocks.POWERSTATION.name(),
					CavelandBlocks.CLBlocks.LIFT.name()
				)
				.setConfirmAction((int result, AbstractEntity actor1) -> {
						build(actor1.getPosition().toCoord(), getResult(result));//spawn construction site
					}
				)
				.setCancelAction(
					(int result, AbstractEntity actor1) -> {
						if (preview != null) {
							preview.dispose();
							preview = null;
						}
					}
				)
				.setSelectAction(
					(boolean up, int result, AbstractEntity actor1) -> {
						//spawn rails
						if (preview == null) {
							preview = (EntityBlock) new EntityBlock(getResult(result),(byte) 0)
								.spawn(actor1.getPosition().toCoord().toPoint());
							preview.setColor(new Color(0.8f, 0.8f, 1.0f, 0.3f));
						} else {
							preview.setSpriteValue((byte) result);
						}
					}
				)
				.register(view, ((Ejira) actor).getPlayerNumber(), actor);
		}
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return true;
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}