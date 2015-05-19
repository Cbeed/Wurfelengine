package com.bombinggames.caveland.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombinggames.caveland.Caveland;
import com.bombinggames.wurfelengine.Core.WEScreen;
import com.bombinggames.wurfelengine.WE;

/**
 *
 * @author Benedikt Vogler
 */
public class CreditsScreen extends WEScreen {
    private final Stage stage;
	private final Texture background;

    /**
     *
     */
    public CreditsScreen() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), WE.getEngineView().getBatch());
                
		final Label credits = new Label(Caveland.getCredits(), WE.getEngineView().getSkin());
		credits.setWidth(500);
		credits.setAlignment(Align.center);
		credits.setPosition(stage.getWidth()/2-credits.getWidth()/2, 150);
		stage.addActor(credits);
		
		TextButton showWEcredits = new TextButton("Show Wurfel Engine Credits", WE.getEngineView().getSkin());
		showWEcredits.setPosition(stage.getWidth()-100-showWEcredits.getWidth(), 150);
		showWEcredits.addListener(
			new ChangeListener() {
				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					if ("Show Wurfel Engine Credits".equals(((TextButton) actor).getText().toString())){
						credits.setText(WE.getCredits());
						((TextButton) actor).setText("Show Caveland Credits");
					} else {
						credits.setText(Caveland.getCredits());
						((TextButton) actor).setText("Show Wurfel Engine Credits");
					}
				}
			}
		);
		stage.addActor(showWEcredits);
		
		background = new Texture(Gdx.files.internal("com/bombinggames/Caveland/MainMenu/credits_wallpaper.jpg"));
    }

    @Override
    public void renderImpl(float dt) {
		//update
		
		//render
        //clear & set background to black
        //Gdx.gl20.glClearColor(0.75f, 0.63f, 0.36f, 1f );
        //Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
        //update camera and set the projection matrix
        WE.getEngineView().getBatch().begin();
			WE.getEngineView().getBatch().draw(background, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			WE.getEngineView().getFont().draw(WE.getEngineView().getBatch(), "FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 20);
			if (WE.CVARS.getValueB("DevMode")) WE.getEngineView().getFont().draw(WE.getEngineView().getBatch(), Gdx.input.getX()+ ","+Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
        WE.getEngineView().getBatch().end();
		stage.act(dt);
		stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
		WE.getEngineView().addInputProcessor(stage);
		WE.getEngineView().addInputProcessor(new InputListener());
		
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
        stage.dispose();
    }

	private class InputListener implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
			//update
			if (keycode == Input.Keys.ESCAPE)
				WE.showMainMenu();
			
            return true;
			
        }

        @Override
        public boolean keyUp(int keycode) {
            return true;
        }

        @Override
        public boolean keyTyped(char character) {
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return true;
        }

        @Override
        public boolean scrolled(int amount) {
            return true;
        }
    }
	
    
}
