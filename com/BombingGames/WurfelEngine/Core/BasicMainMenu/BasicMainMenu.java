/*
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

package com.BombingGames.WurfelEngine.Core.BasicMainMenu;

import com.BombingGames.WurfelEngine.Core.AbstractMainMenu;

/**
 *This class provides a simple basic main menu if you don't want to set up your own and just want to test your game controllers.
 * @author Benedikt Vogler
 */
public class BasicMainMenu extends AbstractMainMenu {
 
    private static View View;
    private static Controller Controller;
    private static Class gameController[];
    private static Class gameViews[];

    /**
     * Use this constructor to pass your controller and views in order of the main menu
     * @param gameController An array filled with game controllers
     * @param gameViews An array filled with game views.
     */
    public BasicMainMenu(Class gameController[], Class gameViews[]) {
        BasicMainMenu.gameController = gameController;
        BasicMainMenu.gameViews = gameViews;
    }
    
    
    @Override
    public void init(){
        Controller = new Controller(); 
        View = new View();
    }

    @Override
    public void render(float delta) {
        Controller.update((int) (delta*1000));
        View.render(Controller);
        View.update(delta*1000);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Controller.show();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
  
    /**
     * 
     * @return
     */
    public static Controller getController() {
        return Controller;
    }

    /**
     * 
     * @return
     */
    public static View getView() {
        return View;
    }

    public static Class[] getGameController() {
        return gameController;
    }

    public static Class[] getGameViews() {
        return gameViews;
    }
    

    
}
