package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Explosion extends AbstractEntity {
	private final int radius;
	private static Sound explosionsound;

	public Explosion(Point pos, int radius) {
		super(0, pos);
		this.radius = radius;
		if (explosionsound == null)
            explosionsound = WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/explosion2.ogg");
    }

	@Override
	public void update(float delta) {
	}

	/**
	 * explodes
	 * @return 
	 */
	@Override
	public AbstractEntity spawn() {
		super.spawn();
		//replace blocks by air
		for (int x=-radius; x<radius; x++)
            for (int y=-radius*2; y<radius*2; y++)
                for (int z=-radius; z<radius; z++){
                    //place air
                     if (x*x + (y/2)*(y/2)+ z*z < radius*radius){
                        Controller.getMap().setDataSafe(
                            getPosition().cpy().getCoord().addVector(x, y, z) , Block.getInstance(0)
                        );
                     }
                }
        
        for (int x=-radius; x<radius; x++)
            for (int y=-radius*2; y<radius*2; y++)
                for (int z=-radius; z<radius; z++){
                    
                    //spawn effect
                    if (x*x + (y/2)*(y/2)+ z*z >= radius*radius-4 &&
                        x*x + (y/2)*(y/2)+ z*z <= radius*radius){
						AbstractEntity effect = new AnimatedEntity(
							31,
							0,
							getPosition().cpy().getCoord().addVector(x, y, z),
							new int[]{700,2000},
							true,
							false
						).spawn();
						//get every entity which is attacked
                        ArrayList<AbstractMovableEntity> list =
							Controller.getMap().getEntitysOnCoord(
								effect.getPosition().getCoord(),
								AbstractMovableEntity.class
							);
                        for (AbstractMovableEntity ent : list) {
                            if (!(ent instanceof PlayerWithWeapon))//don't damage player with weapons
                                ent.damage(1000);
                        }
                    }
                }
        explosionsound.play();
        Controller.requestRecalc();
		dispose();
		return this;
	}
}
