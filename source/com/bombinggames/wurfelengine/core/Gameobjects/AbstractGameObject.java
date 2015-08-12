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
package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_DEPTH2;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_HEIGHT2;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_WIDTH2;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;
import java.io.Serializable;

/**
 *An AbstractGameObject is something wich can be found in the game world.
 * @author Benedikt
 */
public abstract class AbstractGameObject implements Serializable, HasID {
	private transient static final long serialVersionUID = 2L;
	
    /**The sprite texture which contains every object texture*/
    private transient static TextureAtlas spritesheet;
	private transient static String spritesheetPath = "com/bombinggames/wurfelengine/core/images/Spritesheet";
    private transient static Pixmap pixmap;
    private transient static AtlasRegion[][][] sprites = new AtlasRegion['z'][Block.OBJECTTYPESNUM][Block.VALUESNUM];//{category}{id}{value}
    private transient static int drawCalls =0;
	private static Texture textureDiff;
	private static Texture textureNormal;
	
    /**
     *disposes static fields
     */
    public static void staticDispose(){
        spritesheet.dispose();//is this line needed?
        WE.getAssetManager().unload(spritesheetPath+".txt");
        spritesheet = null;
        sprites = new AtlasRegion['z'][Block.OBJECTTYPESNUM][Block.VALUESNUM];
        //pixmap.dispose();
        pixmap = null;
    }
	
	/**
	 * the diffuse map
	 * @return 
	 */
	public static Texture getTextureDiffuse() {
		return textureDiff;
	}

	/**
	 * the normal map
	 * @return 
	 */
	public static Texture getTextureNormal() {
		return textureNormal;
	}
	
	/**
     *
     * @return can be null if pixmap loadin is disabled
     */
    public static Pixmap getPixmap() {
        return pixmap;
    }
	
	/**
	 * Set your custom spritesheet path. the suffix will be added
	 * @param customPath format like "com/bombinggames/wurfelengine/core/images/Spritesheet" without suffix 
	 */
	public static void setCustomSpritesheet(String customPath) {
		AbstractGameObject.spritesheetPath = customPath;
	}

	/**
	 * path of the spritesheet
	 * @return 
	 */
	public static String getSpritesheetPath() {
		return spritesheetPath;
	}
	
	/**
     *
     * @return
     */
    public static AtlasRegion[][][] getSprites() {
        return sprites;
    }

    /**
     * Reset couner for this frame
     */
    public static void resetDrawCalls() {
        AbstractGameObject.drawCalls = 0;
    }

    /**
     * Maybe not quite correct. A single block has only one drawcall even it should consist of three.
     * @return 
     */
    public static int getDrawCalls() {
        return drawCalls;
    }
	
	/**
     * Load the spritesheet from memory.
     */
    public static void loadSheet() {
        //spritesheet = new TextureAtlas(Gdx.files.internal("com/bombinggames/Game/Blockimages/Spritesheet.txt"), true);
        Gdx.app.log("AGameObject", "getting spritesheet");
        if (spritesheet == null) {
            spritesheet = WE.getAsset(spritesheetPath+".txt");
        }
		textureDiff = spritesheet.getTextures().first();
        if (WE.CVARS.getValueB("LEnormalMapRendering"))
			textureNormal = WE.getAsset(spritesheetPath+"Normal.png");
		
        //load again for pixmap, allows access to image color data;
        if (WE.CVARS.getValueB("loadPixmap")) {
			if (pixmap == null) {
				//pixmap = WurfelEngine.getInstance().manager.get("com/bombinggames/Game/Blockimages/Spritesheet.png", Pixmap.class);
				pixmap = new Pixmap(
					Gdx.files.internal(spritesheetPath+".png")
				);
			}
		}
    }

    /**
     * Returns a sprite texture. You may use your own method like in <i>Block</i>.
     * @param category the category of the sprite e.g. "b" for blocks
     * @param id the id of the object
     * @param value the value of the object
     * @return 
     */
    public static AtlasRegion getSprite(final char category, final int id, final int value) {
        if (spritesheet == null) return null;
        if (sprites[category][id][value] == null){ //load if not already loaded
            AtlasRegion sprite = spritesheet.findRegion(category+Integer.toString(id)+"-"+value);
            if (sprite == null){ //if there is no sprite show the default "sprite not found sprite" for this category
                Gdx.app.debug("Spritesheet", category+Integer.toString(id)+"-"+value + " not found");
                sprite = spritesheet.findRegion(category+"0-0");
                if (sprite == null) {//load generic error sprite if category sprite failed
                    sprite = spritesheet.findRegion("error");
                    if (sprite == null) throw new NullPointerException("Sprite and category error not found and even the generic error sprite could not be found. Something with the sprites is fucked up.");
                }
            }
            sprites[category][id][value] = sprite;
            return sprite;
        } else {
            return sprites[category][id][value];
        }
    }


    //getter & setter
    
     /**
     * Returns the spritesheet used for rendering.
     * @return the spritesheet used by the objects
     */
    public static TextureAtlas getSpritesheet() {
        return spritesheet;
    }
	
	//render information
    private boolean hidden; 
    private float rotation;
	private float scaling;
	private byte graphicsID;
	
	/**
	 * default is RGBA 0x80808080.
	 */
	private transient Color tint = new Color(0.5f, 0.5f, 0.5f, 1); 
	
    /**
     * Creates an object.
     * @param id the id of the object which is used for rendering
     */
    protected AbstractGameObject(byte id) {
		this.graphicsID = id;
    }
	
    /**
     * Get the category letter for accessing sprites.
     * @return
     */
    public abstract char getCategory();
    
	/**
	 *
	 * @return
	 */
	public abstract int getDimensionZ();
	
	  /**
     * Return the coordinates of the SelfAware object.
     * @return Reference to the position object which points to the location in the game world.
     */
    public abstract AbstractPosition getPosition();
    
    /**
     * Set the coordinates without safety check. May use different object pointing to the same position.
     * @param pos the coordinates you want to set
     */
    public abstract void setPosition(AbstractPosition pos);

	/**
     * Returns the depth of the object. Nearer objects have a bigger depth.
	 * @param view
     * @return distance from zero level
     */
    public int getDepth(GameView view) {
        return (int) (getPosition().getDepth(view)
            + getDimensionZ()/AbstractPosition.SQRT2
        );
    }
	
    /**
     * When calling sprite.draw this hsould also be called for statistics.
     */
    protected void increaseDrawCalls(){
        drawCalls++;
    }
        
    /**
     * Draws an object in the color of the light engine and with the lightlevel. Only draws if not hidden.
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     */
    public void render(GameView view, Camera camera) {
        render(
            view,
            camera,
            null
        );
    }
    
    /**
     * Draws an object if it is not hidden and not clipped.
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     * @param color custom blending color
     */
    public void render(GameView view, Camera camera, Color color) {
        if (!hidden) {  
			if (WE.CVARS.getValueB("enableFog")) {
				//can use CVars for dynamic change. using harcored values for performance reasons
				float factor = (float) (Math.exp( 0.025f*(camera.getVisibleFrontBorderHigh()-getPosition().toCoord().getY()-18.0) )-1 );
				if (color ==null) {
					color = new Color(0.5f, 0.5f, 0.5f, 1).add(
						0.3f*factor,
						0.4f*factor,
						1f*factor,
						0f
					);
				} else {
					color.add(
						0.3f*factor,
						0.4f*factor,
						1f*factor,
						0f
					);
				}
			}
            render(
                view,
                getPosition().getViewSpcX(view),
                getPosition().getViewSpcY(view),
				color
            );
        }
    }
    
    /**
     * Renders at a custom position.
     * @param view
     * @param xPos rendering position, center of sprite in projection (?) space 
     * @param yPos rendering position, center of sprite in projection (?) space
     */
    public void render(GameView view, int xPos, int yPos) {
		render(view, xPos, yPos, null);
    }
    
	    /**
     * Renders at a custom position with a custom light.
     * @param view
     * @param xPos rendering position, center of sprite in projection space (?)
     * @param yPos rendering position, center of sprite in projection space (?)
	 * @param color color which gets multiplied with the tint. No change ( multiply with 1) is RGBA 0x80808080.
     */
    public void render(GameView view, int xPos, int yPos, Color color) {
		if (getId() != 0){
			AtlasRegion texture = getSprite(getCategory(), graphicsID, getValue());
			Sprite sprite = new Sprite(texture);
			sprite.setOrigin(
				texture.originalWidth/2 - texture.offsetX,
				VIEW_HEIGHT2 - texture.offsetY
			);
			sprite.rotate(rotation);
			sprite.scale(scaling);

			sprite.setPosition(xPos+texture.offsetX-texture.originalWidth/2,
				yPos//center
					-VIEW_HEIGHT2
					+texture.offsetY
			);
			
			//hack for transient field tint
			if (tint == null) tint = new Color(0.5f, 0.5f, 0.5f, 1); 
			if (color!=null)
				sprite.setColor(tint.cpy().mul(color.r+0.5f, color.g+0.5f, color.b+0.5f, color.a+0.5f));
			else sprite.setColor(tint);
        
			if (view.debugRendering()){
				ShapeRenderer sh = view.getShapeRenderer();
				sh.begin(ShapeRenderer.ShapeType.Line);
				//sprite outline
				sh.rect(
					sprite.getX(),
					sprite.getY(),
					sprite.getWidth(),
					sprite.getHeight()
				);
				//crossing lines
				sh.line(
					xPos-VIEW_WIDTH2,
					yPos-VIEW_DEPTH2,
					xPos+VIEW_WIDTH2,
					yPos+VIEW_DEPTH2
				);
				sh.line(
					xPos-VIEW_WIDTH2,
					yPos+VIEW_DEPTH2,
					xPos+VIEW_WIDTH2,
					yPos-VIEW_DEPTH2
				);
				//bounding box
				sh.line(xPos-VIEW_WIDTH2, yPos, xPos, yPos-VIEW_DEPTH2);
				sh.line(xPos-VIEW_WIDTH2, yPos, xPos, yPos+VIEW_DEPTH2);
				sh.line(xPos, yPos-VIEW_DEPTH2, xPos+VIEW_WIDTH2, yPos);
				sh.line(xPos, yPos+VIEW_DEPTH2, xPos+VIEW_WIDTH2, yPos);
				sh.end();
			} else {
				sprite.draw(view.getBatch());
				drawCalls++;
			}
		}
    }
	
    //getter & setter

	/**
	 * the id of the sprite. should be the same as id but in some cases some objects share their sprites.
	 * @return 
	 */
	public byte getSpriteId() {
		return graphicsID;
	}



    /**
     * Returns the name of the object
     * @return the name of the object
     */
	@Override
    public abstract String getName();


    /**
     * Returns the rotation of the object.
     * @return
     */
    public float getRotation() {
        return rotation;
    }

	/**
	 * Returns the scale factor of the object.
	 * @return 0 is no scaling
	 */
	public float getScaling() {
		return scaling;
	}
	
    /**
     * Returns true, when set as hidden. Hidden objects are not rendered even when they are clipped ("clipped" by the meaning of the raytracing).
     * @return if the object is invisible
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Hides an object. It won't be rendered.
     * @param hidden
     */
    public void setHidden(boolean hidden){
        this.hidden = hidden;
    }

    /**
     *
     * @param rotation set the rotation in degrees.
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
	
	/**
	 * 
	 * @param scaling 0 no scaling
	 */
	public void setScaling(float scaling){
		this.scaling = scaling;
	}
    
	/**
	 * the id of the sprite. should be the same as id but in some cases some objects share their sprites.
	 * @param id 
	 */
	public void setGraphicsId(byte id) {
		graphicsID = id;
	}
	
	/**
	 * give the object a tint. The default brightness is RGBA 0x808080FF so you can make it brighter and darker by modifying R, G and B.
	 * @param color 
	 */
	public void setColor(Color color) {
		this.tint = color;
	}

	/**
	 * get the tint of the object. The default brightness is RGBA 0x808080FF so you can make it brighter and darker by modifying R, G and B.
	 * @return not copy safe
	 */
	public Color getColor() {
		return tint;
	}
	
	/**
	 * Should i.g. not be used for rendering.
	 * @return the sprite used for rendering
	 */
	public AtlasRegion getAtlasRegion(){
		return getSprite(getCategory(), graphicsID, getValue());
	}
}