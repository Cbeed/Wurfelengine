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

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * A table containing all blocks where you can choose your block.
 * @author Benedikt Vogler
 */
public class PlacableSelector extends Table {
    private Table table;
    private ScrollPane scroll; 
	private final PlacableGUI placableGUI;
	
	private enum Mode{
		blocks,
		entities;
	}
		
	private Mode mode = Mode.blocks;

	
    /**
     *
     * @param colorGUI the linked preview of the selection
     */
    public PlacableSelector(PlacableGUI colorGUI) {
        this.placableGUI = colorGUI;
        
        setWidth(400);
        setHeight(Gdx.graphics.getHeight()-100);
        setPosition(-300, 0);
        addListener(new BlockSelInpListener(this));
    }
    
    /**
     *
     */
    public void show(){
        setX(0);
        if (!hasChildren()){
            table = new Table();
            table.pad(10).defaults().expandX().space(4);

            scroll = new ScrollPane(table, WE.getEngineView().getSkin());
            add(scroll).expand().fill();
			
			if (mode == Mode.blocks) {
				if (!table.hasChildren()){//add blocks
					for (int i = 0; i < AbstractGameObject.OBJECTTYPESNUM; i++) {
						table.row();
						table.add(new Label(Integer.toString(i), WE.getEngineView().getSkin())).expandX().fillX();

						Drawable dbl = new BlockDrawable(i);
						Button button = new Button(dbl);
						button.addListener(new ButtonListener(i, button));
						//button.setStyle(style);
						table.add(button);

						table.add(new Label(Block.getInstance(i, 0).getName(), WE.getEngineView().getSkin()));
					}
				}
			} else {//add entities
				if (!table.hasChildren()){
					for (int i = 0; i < AbstractGameObject.OBJECTTYPESNUM; i++) {//shoud loop over registered map
						table.row();
						table.add(new Label(Integer.toString(i), WE.getEngineView().getSkin())).expandX().fillX();

						Drawable dbl = new EntityDrawable("nameofregisteredentity");
						Button button = new Button(dbl);
						button.addListener(new ButtonListener(i, button));
						//button.setStyle(style);
						table.add(button);

						table.add(new Label("nameofregisteredentity", WE.getEngineView().getSkin()));
					}
				}
			}
        }
    }
    
    /**
     *
     */
    public void hide(){
        setX(-getWidth()*2/3f);
        if (hasChildren()){
            scroll.clearListeners();
            clear();
        }
    }

	void showBlocks() {
		mode = Mode.blocks;
		table.clearChildren();
		show();
	}

	void showEntities() {
		mode = Mode.entities;
		table.clearChildren();
		show();
	}

     private class BlockSelInpListener extends InputListener {
        private PlacableSelector parentRef;

        private BlockSelInpListener(PlacableSelector parent) {
            this.parentRef = parent;
        }

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            parentRef.show();
            return false;
        }
    }
     
    private class ButtonListener extends ClickListener {
         private int id;
         private Button parent; 
        ButtonListener(int id, Button parent){
            this.id = id;
            this.parent = parent;
        }
                
        @Override
        public void clicked(InputEvent event, float x, float y) {
            placableGUI.setBlock(id, 0);
        };
     }
    
}
