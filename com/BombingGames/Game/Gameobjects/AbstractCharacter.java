package com.BombingGames.Game.Gameobjects;

import com.BombingGames.Game.Controller;
import com.BombingGames.Game.Map;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 *A character is an entity wich can walk around.
 * @author Benedikt
 */
public abstract class AbstractCharacter extends AbstractEntity {
   /* Always one of them must be 1 to prevent a division with 0.*/
   private float[] dir = {1,0,0};
   private String controls = "WASD";

   /**
    * 
    * @param id
    */
   protected AbstractCharacter(int id) {
        super(id);
    }
   
   /**
    * provides a factor for the vector
    */
   private float speed;
   
   private Sound fallingSound;
   private Sound runningSound;
   
   /**
     * These method should define what happens when the object  jumps. It should call super.jump(int velo)
     * @see com.BombingGames.Game.Gameobjects.AbstractCharacter#jump(float)
     */
    public abstract void jump();
    
    /**
     * Returns the side of the current position.
     * @return
     * @see com.BombingGames.Game.Blocks.Block#getSideNumb(int, int) 
     */
    protected int getSideNumb() {
        return Block.getSideID(getPos()[0], getPos()[1]);
    }  
    
   /**
     * Lets the player walk.
     * @param up move up?
     * @param down move down?
     * @param left move left?
     *  @param right move right?
     * @param walkingspeed the higher the speed the bigger the steps. Should be in m/s.
     *  @param delta time which has passed since last call
     * @throws SlickException
     */
    public void walk(boolean up, boolean down, boolean left, boolean right, float walkingspeed) throws SlickException {
            speed = walkingspeed;
            
            //update the movement vector
            dir[0] = 0;
            dir[1] = 0;
               
            if (up)    dir[1] = -1;
            if (down)  dir[1] = 1;
            if (left)  dir[0] = -1;
            if (right) dir[0] = 1;
   }
    
   /**
     * Make a step on the coordinate grid.
     * @param x left or right step
     * @param y the coodinate steps
     */
    private void makeCoordinateStep(int x, int y){
        //mirror the position around the center
        setPos(
            new float[]{
                getPos()[0] -x*Block.DIM2,
                getPos()[1] -y*Block.DIM2
            }
        );
        
        addToAbsCoords(0, y, 0);
        if (x < 0){
            if (getRelCoords()[1] % 2 == 1) addToAbsCoords(-1, 0, 0);
        } else {
            if (getRelCoords()[1] % 2 == 0) addToAbsCoords(1, 0, 0);
        }
    }
    
   /**
     * Updates the block.
     * @param delta time since last update
     */
    @Override
    public void update(int delta) {
        //scale that the velocity vector is always an unit vector (only x and y)
        double vectorLenght = Math.sqrt(dir[0]*dir[0] + dir[1]*dir[1]);
        if (vectorLenght > 0){
            dir[0] /= vectorLenght;
            dir[1] /= vectorLenght;
        }
            
        /*VERTICAL MOVEMENT*/
        float oldHeight = getHeight();
        
        //calculate new height
        float t = delta/1000f; //t = time in s
        dir[2] += -Map.GRAVITY*t; //in m/s
        setHeight(getHeight() + dir[2] * GAMEDIMENSION * t); //in m
        
        //vertical colission
        //land if standing in or under 0-level or there is an obstacle
        if (dir[2] <= 0
            && (getHeight() <= 0 || onGround())
        ) {
            //stop sound
            if (fallingSound != null) fallingSound.stop();
            dir[2] = 0;
            //newPosZ = 0;
            //set coord
            setHeight((int)(oldHeight/GAMEDIMENSION)*GAMEDIMENSION);
        }
        
         /*HORIZONTAL MOVEMENT*/
        float[] oldpos = getPos();
        
        //calculate new position
        float newx = getPos()[0] + delta * speed * dir[0];
        float newy = getPos()[1] + delta * speed * dir[1];

        //horizontal colision check
        boolean validmovement = true;

        //check for movement in x
        //top corner
        int neighbourNumber = Block.getSideID(newx, newy - Block.DIM2); 
        if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
            validmovement = false;
        //bottom corner
        neighbourNumber = Block.getSideID(newx, newy + Block.DIM2); 
        if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
            validmovement = false;

        //find out the direction of the movement
        if (oldpos[0] - newx > 0) {
            //check left corner
            neighbourNumber = Block.getSideID(newx - Block.DIM2, newy);
            if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
                validmovement = false;
        } else {
            //check right corner
            neighbourNumber = Block.getSideID(newx + Block.DIM2, newy);
            if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
                validmovement = false;
        }

        //check for movement in y
        //left corner
        neighbourNumber = Block.getSideID(newx - Block.DIM2, newy); 
        if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
            validmovement = false;

        //right corner
        neighbourNumber = Block.getSideID(newx + Block.DIM2, newy); 
        if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
            validmovement = false; 

        if (oldpos[1] - newy > 0) {
            //check top corner
            neighbourNumber = Block.getSideID(newx, newy - Block.DIM2);
            if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
                validmovement = false;
        } else {
            //check bottom corner
            neighbourNumber = Block.getSideID(newx, newy + Block.GAMEDIMENSION/2);
            if (neighbourNumber != 8 && Controller.getNeighbourBlock(getRelCoords(), neighbourNumber).isObstacle())
                validmovement = false;
        }

        //if movement allowed => move player   
        if (validmovement) {                
            setPos(0, newx);
            setPos(1, newy);

            //track the coordiante change, if there is one
            int sidennumb = getSideNumb();              
            switch(sidennumb) {
                case 0:
                case 1:
                        makeCoordinateStep(1, -1);
                        break;
                case 2:    
                case 3:
                        makeCoordinateStep(1, 1);
                        break;
                case 4:
                case 5:
                        makeCoordinateStep(-1, 1);
                        break;
                case 6:
                case 7:
                        makeCoordinateStep(-1, -1);
                        break;    
            }
        }
        
        //uncomment this line to see where to player stands:
        Controller.getMapDataSafe(getRelCoords()[0], getRelCoords()[1], getRelCoords()[2]-1).setLightlevel(30);
        
        /* SOUNDS */
        //should the runningsound be played?
        if (runningSound != null)
            if (speed > 0.5f){
                if (!runningSound.playing()) runningSound.play();
            }  else runningSound.stop();
        
        //should the fallingsound be played?
        if (fallingSound != null
            && dir[2] < -1
            && ! fallingSound.playing()
           )
            fallingSound.play();
    }
   

    /**
     * Jump with a specific speed
     * @param velo the velocity in m/s
     */
    public void jump(float velo) {
        if (onGround()) dir[2] = velo;
    }
    
    /**
     * Returns a normalized vector wich contains the direction of the block.
     * @return 
     */
    public float[] getDirectionVector(){
        return dir;
    }

    /**
     * Sets the sound to be played when falling
     * @param fallingSound
     */
    public void setFallingSound(Sound fallingSound) {
        this.fallingSound = fallingSound;
    }

    /**
     * Set the sound to be played when running.
     * @param runningSound
     */
    public void setRunningSound(Sound runningSound) {
        this.runningSound = runningSound;
    }
    
   /**
     * Set the controls.
     * @param controls either "arrows" or "WASD".
     */
    public void setControlls(String controls){
        if ("arrows".equals(controls) || "WASD".equals(controls))
            this.controls = controls;
    }
    
   /**
     * Returns the Controls
     * @return either "arrows" or "WASD".
     */
    public String getControlls(){
        return controls;
    }
}
