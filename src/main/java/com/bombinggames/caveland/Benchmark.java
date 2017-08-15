package com.bombinggames.caveland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import static com.bombinggames.caveland.Caveland.VERSION;
import com.bombinggames.caveland.gameobjects.Quadrocopter;
import com.bombinggames.caveland.mainmenu.CustomLoading;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.DevTools;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.MoveToAi;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt S. Vogler
 */
public class Benchmark {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Caveland.configureEngine();
		//WE.getCVars().register(cvar, VERSION);
		WE.getCVars().get("ignorePlayer").setValue(true);
		WE.addPostLaunchCommands(() -> {
			BenchmarkView view = new BenchmarkView();
			WE.initAndStartGame(
				new CustomLoading(),
				new BenchmarkController(view),
				view
			);
		});

		WE.launch("Caveland Benchmark " + VERSION, args);
	}

	private static class BenchmarkController extends Controller {

		private float watch;
		private BenchmarkMovement movement;
		private int stage;
		private final BenchmarkView view;
		private Path logFile;

		BenchmarkController(BenchmarkView view) {
			super();
			setMapName("benchmark");
			useSaveSlot(0);
			this.view = view;
		}

		@Override
		public void update(float dt) {
			super.update(dt);
			float dts = Gdx.graphics.getRawDeltaTime();
			//wait 5 seconds then start measurement
			int initTime = 1;
			if (watch < initTime && watch + dts > initTime) {
				getDevTools().setCapacity(12000);//1 minute at 5 ms/frame
				startStage(0);
			}
			watch += dts;
		}

		private AbstractEntity create(BenchmarkView view) {
			movement = new BenchmarkMovement(this, view);
			movement.setColiding(false);
			movement.setFloating(true);
			return movement;
		}

		private void endStage(int i) {
			if (logFile == null) {
				DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HH_mm_ss");
				logFile = Paths.get("./benchmark" + LocalDateTime.now().format(FORMATTER)
					+ ".csv");
			}
			try {
				if (!logFile.toFile().exists())
					logFile.toFile().createNewFile();
				final BufferedWriter writer = Files.newBufferedWriter(logFile,
					StandardCharsets.UTF_8, StandardOpenOption.APPEND);
				String res = getDevTools().getDataAsString();
				writer.write("stage"+i+","+res+"\n");
				writer.flush();
			} catch (IOException ex) {
				Logger.getLogger(DevTools.class.getName()).log(Level.SEVERE, null, ex);
			}
			System.out.println("Average delta for stage " + i + ": " + getDevTools().getAverageDelta());
			startStage(i + 1);
		}
		
		private void startStage(int stage){
			this.stage = stage;
			if (stage==0) {
				movement.spawn(new Coordinate(0, 0, 4).toPoint());
			}	
			
			getDevTools().clear();
			
			//start movement
			int stageCenterY = stage<2?Chunk.getBlocksY()*-2:Chunk.getBlocksY()*2;
			int stageDistanceX = Chunk.getBlocksX();
			movement.getPosition().set(new Coordinate(0, stageCenterY, 3).toPoint());
			MoveToAi ai = new MoveToAi(
				new Coordinate(stageDistanceX, stageCenterY, 3).toPoint()
			);
			ai.setMinspeed(2);
			movement.addComponent(ai);
			
			if (stage==1){
				view.addCamera(view.getCamera());
				//view.getCamera().update(0);
			}
			
			if (stage==3){
				for (int i = 0; i < stageDistanceX; i++) {
					new Quadrocopter().spawn(new Coordinate(i*1, stageCenterY, 5).toPoint());
					new Quadrocopter().spawn(new Coordinate(i*1, stageCenterY, 6).toPoint());
					new Quadrocopter().spawn(new Coordinate(i*1, stageCenterY, 7).toPoint());
				}
			}
		}

		private int getStage() {
			return stage;
		}
	}

	private static class BenchmarkView extends GameView {

		private Camera camera;

		@Override
		public void init(Controller controller, GameView oldView) {
			super.init(controller, oldView);
			camera = new Camera(this);
			
			camera.setFocusEntity(((BenchmarkController) controller).create(this));
		}

		private Camera getCamera() {
			return camera;
		}

	}

	private static class BenchmarkMovement extends MovableEntity {

		private static final long serialVersionUID = 1L;
		private final BenchmarkView view;
		private final BenchmarkController controller;

		BenchmarkMovement(BenchmarkController controller, BenchmarkView view) {
			super((byte) 0);
			this.controller = controller;
			this.view = view;
			setHidden(true);
		}

		@Override
		public void update(float dt) {
			super.update(dt);
			//next stage when arrived
			if (getComponent(MoveToAi.class) == null){
				controller.endStage(controller.getStage());
			}
		}

		@Override
		public boolean handleMessage(Telegram msg) {
			return false;
		}
	}

}
