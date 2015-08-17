package com.bombinggames.caveland.Game;

import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Generator;

/**
 *
 * @author Benedikt Vogler
 */
public class ChunkGenerator implements Generator {
	/**
	 * every block below this border is  a cave
	 */
	public static final int CAVESBORDER = 1000;
	
	/**width of a room */
	static final float g = 21;
	/**
	 * padding
	 */
	static final float p = 5;
	static final float yStrech = 0.5f;
	/**
	 * how thick is the wall
	 */
	static final float wallsize =1;
	static final float roomWithPadding = g+p+wallsize;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		for (int y = 1000; y < 1100; y++) {
			for (int x = 0; x < 50; x++) {
				int result = insideOutside(x,y,4);
				if (result ==1)
					System.out.print(".");
				else if (result ==0)
					System.out.print("#");
				else if (result ==-1)
					System.out.print("_");
				else if (result ==2)
					System.out.print("i");
				else if (result ==3)
					System.out.print("o");
			}
			System.out.println("");
		}
	}
	
	@Override
	public byte generate(int x, int y, int z) {
		if (y<CAVESBORDER) {//overworld
			
			//floor
			if (z<3) return 2;
			if (z==3) return 1;
		} else {
			//underworld
			int insideout = insideOutside(x, y, z);
			
			if (insideout==2)
				return 11;
			
			if (insideout==3)
				return 11;
				
			//walls
			if (insideout==0) {//build a wall
				if (z<=4)
					return 3;
				else 
					return 4;//invisible wall
			}
			
			if (insideout==-1)//build air for outside
				return 0;	
			
			if (z==3)
				if ((x*y*2+x+y*3+500) % 8==y % 7)
					if (x%2==0 && y%2==0){
						if (y%5==0)
							return 42;
						else
							return 44;
					} else {
						if (y%8==0)
							return 43;
						else
							return 2;
					}
			
			//floor
			if (z<=2)
				return 2;
			
		}
		return 0;
	}
	
	/**
	 * the the entities which should be spawned at this coordiante
	 * @param x
	 * @param y
	 * @param z
	 * @return 
	 */
	@Override
	public AbstractEntity[] generateEntities(int x, int y, int z){
		return null;
	}
	
		/**
	 * the the entities which should be spawned at this coordiante
	 * @param x
	 * @param y
	 * @param z 
	 */
	@Override
	public void configureLogicBlocks(int x, int y, int z){
		if (y>CAVESBORDER) {
			//apply p
			float xRoom = (((x) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
			float yRoom = (((y*yStrech) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;

			//loch in der Decke
			if (xRoom==g-5 && yRoom==p+2 && z == 4) {
				Portal portal; 
				if (getCaveNumber(x, y, z)==0) {
					//exit to surface
					((Portal) new Coordinate(x, y, z).getLogic()).setTarget(new Coordinate(0, 0, 5));
				} else {
					((Portal) new Coordinate(x, y, z).getLogic()).setTarget(getCaveExit(getCaveNumber(x, y, z)+1));
				}
				//portal.setValue((byte) 1);
				//portal.enableEnemySpawner();
			}
			//loch im Boden
			if (xRoom==5 && yRoom==g-p-4 && z == 4){
				((Portal) new Coordinate(x, y, z).getLogic()).setTarget(getCaveExit(getCaveNumber(x, y, z)-1));
				
			}
		} else {
			if (x==0 && y==0 && z==4) {
				((Portal) new Coordinate(x, y, z).getLogic()).setTarget(getCaveEntry(0).addVector(0, 0, 3));
			}
		}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return 
	 */
	public static int getCaveNumber(int x, int y, int z){
		return (int) Math.floor(x / roomWithPadding);
	}
	
	/**
	 * copy safe
	 * @param caveNumber
	 * @return 
	 */
	public static Coordinate getCaveEntry(int caveNumber){
		return new Coordinate((int) (roomWithPadding*caveNumber+g), CAVESBORDER+52, 4);
	}
	
	/**
	 * copy safe
	 * @param caveNumber
	 * @return 
	 */
	public static Coordinate getCaveExit(int caveNumber){
		return new Coordinate((int) (roomWithPadding*caveNumber+10), CAVESBORDER+62, 4);
	}
	
	/**
	 * copy safe
	 * @param caveNumber
	 * @return 
	 */
	public static Coordinate getCaveCenter(int caveNumber){
		return new Coordinate((int) (roomWithPadding*caveNumber+p+wallsize+g/2), (int) (CAVESBORDER+(p+g)/yStrech), 4);
	}
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return -1 outside, 0 middle, 1 inside, 2 entry, 3 exit
	 */
	public static int insideOutside(int x, int y, int z){
		//apply p
		float xRoom = (((x) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
		float yRoom = (((y*yStrech) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
		
		if (xRoom<0) return -1;
		if (yRoom<0) return -1;
		//yRoom*=yStrech;//strech;

		boolean firstCheckInside = true;//standard firstCheckInside is inside
		
		//fix/hack for even walls and staggered map
		if (xRoom < g/2 && y%2 == 1)
			xRoom++;
		
		if (
			   xRoom + yRoom <= g/2.0f//top left
			|| xRoom - yRoom >= g/2.0f//top right
			|| xRoom + yRoom >= 3 * g/2.0f//bottom right
			|| yRoom - xRoom >= g/2.0f//bottom left
		) {
			firstCheckInside=false;//is outside
		}
		
		//check again with checking walls
		if (
			   xRoom + yRoom <= g/2.0f-wallsize
			|| xRoom - yRoom >= g/2.0f+wallsize
			|| xRoom + yRoom >= 3 * g/2.0f+wallsize
			|| yRoom - xRoom >= g/2.0f+wallsize
		) {
			return -1; //if outside and still outside inside definetely outside
		} else {
			if (firstCheckInside==false)
				return 0;//must be in middle if was outside and now inside
			else {
				if (xRoom==5 && yRoom==g-5 && z==4)
					return 2;
				if (xRoom==g-5 && yRoom==5 && z==4)
					return 3;
				return 1;//still inside
			}
		}
	}
	
	/**
	 * 
	 * @param coord
	 * @return 
	 * @see #insideOutside(int, int, int) 
	 */
	public static int insideOutside(Coordinate coord){
		return insideOutside(coord.getX(), coord.getY(), coord.getZ());
	}
}
