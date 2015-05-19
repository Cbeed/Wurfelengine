package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.Core.Controller;
import com.bombinggames.wurfelengine.Core.GameView;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.Core.Gameobjects.CoreData;
import static com.bombinggames.wurfelengine.Core.Gameobjects.CoreData.GAME_EDGELENGTH;
import static com.bombinggames.wurfelengine.Core.Gameobjects.CoreData.GAME_EDGELENGTH2;
import com.bombinggames.wurfelengine.Core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.Core.Gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.Core.Map.Point;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class MineCart extends AbstractInteractable {
	private static final long serialVersionUID = 1L;
	private static final float MAXSPEED = 5;
	/**
	 * the height of the bottom plate
	 */
	private static final int BOTTOMHEIGHT = GAME_EDGELENGTH/3;

	private MovableEntity passenger;
	private ArrayList<MovableEntity> content = new ArrayList<>(5);
	private float rollingCycle;
	private transient long isPlayingSound;
	private transient SimpleEntity front = new SimpleEntity((byte) 42,(byte) 1);
	/**
	 * empirical factor to match the front side with the rear
	 */
	private final transient static float frontOffset = 63;
	
	/**
	 *
	 */
	public MineCart() {
		super((byte) 42, 0);
		setName("MineCart");
		setOrientation(new Vector2(1, 1));
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		front = (SimpleEntity) front.spawn(point.cpy().addVector(0, CoreData.GAME_DIAGLENGTH2, 0));//the front is located in front
		front.setSaveToDisk(false);
		addChild(front);
		return this;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		if (getPosition().isInMemoryAreaHorizontal()) {
			Point pos = getPosition();
			CoreData block = pos.getBlock();

			//on tracks?
			if (block!=null && block.getId() == 55) {
				setFriction(0.001f);

				switch (block.getValue()) {
					case 0://straight left bottom to top right
						setOrientation(new Vector2(
								getMovement().y >= 0 && getMovement().x <= 0 ? -1 : 1,
								getMovement().y >= 0 && getMovement().x <= 0 ? 1 : -1
							)
						);
						//move on y=-x
						float x = pos.getRelToCoordX();
						pos.setPositionRelativeToCoord(
							x,
							-x,
							pos.getRelToCoordZ()
						);
						break;
					case 1:
						setOrientation(
							new Vector2(
								getMovement().y >= 0 && getMovement().x >= 0 ? 1 : -1,
								getMovement().y >= 0 && getMovement().x >= 0 ? 1 : -1
							)
						);
						//move on y=-x
						x = pos.getRelToCoordX();
						pos.setPositionRelativeToCoord(
							x,
							x,
							pos.getRelToCoordZ()
						);
						break;
					case 3:
					case 5:
						int y;
						if (getMovement().y > 0
							|| (getMovement().y == 0 && pos.getY() - pos.toCoord().toPoint().getY() < 0)) {//on top and moving down
							y = 1;
						} else {
							y = -1;
						}

						setOrientation(
							new Vector2(
								0,
								y//coming from top right
							)
						);
						break;
					case 2:
					case 4:
						setOrientation(
							new Vector2(
								getMovement().x >= 0 ? 1 : -1,//coming from left
								0
							)
						);
						break;
				}

				//start moving?
				if (getSpeedHor()> 0) {
					setSpeedHorizontal(MAXSPEED);//start moving
					if( isPlayingSound == 0)
						isPlayingSound = Controller.getSoundEngine().loop("wagon", getPosition());
				}
			} else {//offroad
				setFriction(0.005f);
				if (isPlayingSound!=0) {
					Controller.getSoundEngine().stop("wagon", isPlayingSound);
					isPlayingSound = 0;
				}
			}
			
			//copy position to fron
			front.setPosition(getPosition().cpy().addVector(0, frontOffset, 0));

			//animation
			//moving down left or up right
			if (
				(getOrientation().y > 0
				&&
				getOrientation().y > getOrientation().x)
				||
				(getOrientation().y < 0
				&&
				getOrientation().y < getOrientation().x)
			) {
				setValue((byte) 0);
				front.setValue((byte) 1);
			} else {
				setValue((byte) 3);
				front.setValue((byte) 4);
			}

			rollingCycle += getMovementHor().len()*GAME_EDGELENGTH*dt/1000f;//save change in distance in this sprite, distance*m/s
			rollingCycle %= GAME_EDGELENGTH/4; //cycle each 0,25m
			if (rollingCycle >= GAME_EDGELENGTH/8) {//new sprite half of the circle length
				front.setValue((byte) (front.getValue()+1)); //next step in animation
			}

			//logic
			//if transporting object
			if (passenger != null) {
				//give same speed as minecart
				passenger.setMovement(getMovementHor());
				
				//while standing at ground in mine cart force into it
				if (passenger.getPosition().getZ() <= pos.getZ()+BOTTOMHEIGHT) {
					Point tmp = pos.cpy();
					tmp.setZ( pos.getZ()+BOTTOMHEIGHT );//a little bit higher then the minecart
					passenger.setPosition(tmp);
					passenger.setFloating(true);
					if (passenger instanceof CustomPlayer) {
						((CustomPlayer) passenger).idle();
						((CustomPlayer) passenger).forceBunnyHop();
					}
				} else {
					passenger.setFloating(false);
				}
				
				//check if passenger exited the cart
				if (
					passenger.getPosition().getZ() - pos.getZ() > GAME_EDGELENGTH2
					|| getPosition().distanceToHorizontal(passenger) > GAME_EDGELENGTH
				) {
					passengerLeave();
				}
			} else {
				//add objects
				ArrayList<Collectible> ents = Controller.getMap().getEntitys(Collectible.class);
				for (Collectible ent : ents) {
					if (ent.canBePickedByParent(this) && ent.getPosition().distanceTo(pos)<80 && ent.getMovement().z <0){
						if (add(ent))
							ent.dispose();
					}
				}
			}

			//hit objects in front
			if (getSpeed() > 0) {
				ArrayList<MovableEntity> entitiesInFront;
				entitiesInFront = pos.cpy().addVector(getOrientation().scl(80)).getEntitiesNearby(40, MovableEntity.class);
				for (MovableEntity ent : entitiesInFront) {
					if (this != ent) {//don't collide with itself
						ent.setMovement(
							new Vector3(
								(float) (getMovement().x + Math.random() * 0.5f - 0.25f),
								(float) (getMovement().y + Math.random() * 0.5f - 0.25f),
								(float) Math.random()
							)
						);
						ent.addToHor(getSpeed());
					}
				}
			}
		}
	}

	/**
	 * the passengers must enter by themself
	 * @param passenger 
	 */
	public void setPassanger(MovableEntity passenger) {
		this.passenger = passenger;
//		if (passenger.getMovement().z > 0) {
//			passenger.getMovement().z = 0;//fall into chuchu
//		}
		
		
		//set passenger in the center the mine cart
		Point tmp = passenger.getPosition().cpy();
		tmp.setZ(passenger.getPosition().getZ());//kepp z
		passenger.setPosition(tmp);
	}

	/**
	 *
	 * @return
	 */
	public MovableEntity getPassenger() {
		return passenger;
	}

	/**
	 * emtpies teh lore
	 *
	 * @return
	 */
	public ArrayList<MovableEntity> getContent() {
		ArrayList<MovableEntity> tmp = content;
		content = new ArrayList<>(5);

		return tmp;
	}

	boolean add(MovableEntity obj) {
		if (content.size() < 5) {
			content.add(obj);
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param list
	 */
	public void addAll(ArrayList<MovableEntity> list) {
		if (list != null) {
			content.addAll(list);
		}
	}

	@Override
	public void damage(byte value) {
		super.damage(value);
		if (getHealth()<=0) {
			Controller.getSoundEngine().stop("wagon", isPlayingSound);
			Controller.getSoundEngine().play("robot1destroy", getPosition());
			((Collectible) Collectible.create(Collectible.CollectibleType.IRONORE).spawn(getPosition())).sparkle();
		}
	}
	
	/**
	 *
	 */
	public void turn() {
		setOrientation(
			new Vector2(
				-getOrientation().x,
				-getOrientation().y
			)
		);
	}

	@Override
	public void interact(AbstractEntity actor, GameView view) {
		if (actor instanceof MovableEntity){
			if (passenger==null){
				setPassanger((MovableEntity) actor);
			} else {
				passengerLeave();
			}
		}
	}
	
	void passengerLeave(){
		passenger.setFloating(false);
		passenger=null;
	}
	
	/**
	 * overrides deserialisation
	 *
	 * @param stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject(); //fills fld1 and fld2;
		front = new SimpleEntity((byte) 42,(byte) 1);
		front.spawn(getPosition().cpy().addVector(0, CoreData.GAME_DIAGLENGTH2, 0));//the front is located in front
		front.setSaveToDisk(false);
	}

}