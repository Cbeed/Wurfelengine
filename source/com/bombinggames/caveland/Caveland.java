package com.bombinggames.caveland;

import com.badlogic.gdx.audio.Sound;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.Game.ChunkGenerator;
import com.bombinggames.caveland.Game.CustomGameController;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Bird;
import com.bombinggames.caveland.GameObjects.Enemy;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.caveland.GameObjects.SmokeEmitter;
import com.bombinggames.caveland.GameObjects.Spaceship;
import com.bombinggames.caveland.GameObjects.Vanya;
import com.bombinggames.caveland.GameObjects.collectibles.Bausatz;
import com.bombinggames.caveland.GameObjects.collectibles.TFlint;
import com.bombinggames.caveland.GameObjects.collectibles.TorchCollectible;
import com.bombinggames.caveland.MainMenu.CustomLoading;
import com.bombinggames.caveland.MainMenu.MainMenuScreen;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.CVar.BooleanCVar;
import com.bombinggames.wurfelengine.core.CVar.CVar;
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.CVar.FloatCVar;
import com.bombinggames.wurfelengine.core.CVar.IntCVar;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Map;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import java.io.File;
import java.io.InputStream;

/**
 * 
 * @author Benedikt Vogler
 */
public class Caveland {
	/**
	 * version string of the game Caveland
	 */
	public static final String VERSION = "Alpha 6";
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		WorkingDirectory.setApplicationName("Caveland");
		//game cvars
		WE.CVARS.register( new IntCVar(50), "worldSpinAngle", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "shouldLoadMap", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "enableLightEngine", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "enableFog", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(false), "enableAutoShade", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "LEnormalMapRendering", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "coopVerticalSplitScreen", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new FloatCVar(150), "PlayerTimeTillImpact", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(false), "ignorePlayer", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(false), "godmode", CVar.CVarFlags.CVAR_ARCHIVE);
		
		//register map cvars
		CVarSystem.setCustomMapCVarRegistration(new CavelandMapCVars());
		
		//configure
        WE.setMainMenu(new MainMenuScreen());
		Block.setCustomBlockFactory(new CavelandBlocks());
		AbstractGameObject.setCustomSpritesheet("com/bombinggames/caveland/Spritesheet");
		
		//register entities
		AbstractEntity.registerEntity("Emitter Test", SmokeEmitter.class);
		AbstractEntity.registerEntity("TFlint", TFlint.class);
		AbstractEntity.registerEntity("Torch", TorchCollectible.class);
		AbstractEntity.registerEntity("Construction Kit", Bausatz.class);
		AbstractEntity.registerEntity("Mine Cart", MineCart.class);
		AbstractEntity.registerEntity("Spaceship", Spaceship.class);
		AbstractEntity.registerEntity("Vanya", Vanya.class);
		AbstractEntity.registerEntity("Enemy", Enemy.class);
		AbstractEntity.registerEntity("Bird", Bird.class);
		
		Map.setDefaultGenerator(new ChunkGenerator());
		
		if (args.length > 0){
            //look if contains launch parameters
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-quickstart":
						WE.addPostLaunchCommands(
							() -> {
							CustomGameController controller = new CustomGameController();
							controller.useSaveSlot(0);
							WE.initAndStartGame(controller, new CustomGameView(), new CustomLoading());
							}
						);
                        break;
                }
            }
        }
		
		WE.addPostLaunchCommands(() -> {
			WE.getConsole().addCommand(new GiveCommand());
			WE.getConsole().addCommand(new PortalTargetCommand());
			WE.getConsole().addCommand(new TeleportPlayerCommand());
			
			//load the needed assets
			WE.getAssetManager().load("com/bombinggames/caveland/MainMenu/menusound.wav", Sound.class);
			WE.getAssetManager().load("com/bombinggames/caveland/MainMenu/menusoundAbort.wav", Sound.class);
			WE.getAssetManager().load("com/bombinggames/caveland/MainMenu/bong.wav", Sound.class);
			WE.getAssetManager().finishLoading();

			WE.SOUND.register("menuSelect", "com/bombinggames/caveland/MainMenu/menusound.wav");
			WE.SOUND.register("menuAbort", "com/bombinggames/caveland/MainMenu/menusoundAbort.wav");
			WE.SOUND.register("menuBong", "com/bombinggames/caveland/MainMenu/bong.wav");
		});
		
        WE.launch("Caveland " + VERSION, args);
		
		//unpack map
		if (!new File(WorkingDirectory.getMapsFolder()+"/default").exists()){
			InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/bombinggames/caveland/defaultmap.zip");
			WorkingDirectory.unpackMap(
				"default",
				in
			);
		} else {
			//checck if old format is already there. delete it. also delete if there is 
			if (new File(WorkingDirectory.getMapsFolder()+"/default/map.wem").exists()) {
				WorkingDirectory.deleteDirectory(new File(WorkingDirectory.getMapsFolder()+"/default/"));
				InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/bombinggames/caveland/defaultmap.zip");
				WorkingDirectory.unpackMap(
					"default",
					in
				);
			}
		}
    }

	/**
	 * Credtis of caveland.
	 * @return 
	 */
	public static String getCredits(){
		return "Caveland\n" +
			"\n" +
			"a game by\n" +
			"Benedikt S. Vogler\n" +
			"\n" +
			"Art\n" +
			"Frederic Brueckner\n" +
			"\n" +
			"Music & Sound\n" +
			"\"SteinImBrett\":\n" +
			"Felix von Dohlen\n" +
			"Marcel Gohsen\n" +
			"\n" +
			"Quality Assurance\n" +
			"Thomas Vogt\n" +
			"\n" +
			"Special Thanks to\n" +
			"Felix Guenther\n" +
			"Vanya Gercheva\n" +
			"Ulrike Vogler\n" +
			"Gereon Vogler\n" +
			"Rene Weiszer\n" +
			"Bernhard Vogler\n" +
			"\"Janosch\" Frierich\n" +
			"Pia Lenszen\n" +
			"reddit.com/r/gamedev\n" +
			"Bauhaus University Weimar\n\n"
			+ "Wurfel Engine uses libGDX.\n";
	}	
}
