/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.CVar.CVarSystemMap;
import com.bombinggames.wurfelengine.core.CVar.CVarSystemSave;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import java.io.File;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class CdCommand implements ConsoleCommand {

	@Override
	public String getCommandName() {
		return "cd";
	}

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		if (!parameters.hasMoreElements()) {
			return false;
		}
		String path = WE.getConsole().getPath();

		String enteredPath = parameters.nextToken();
		if (enteredPath.length() > 0) {
			if (!WE.getConsole().checkPath(enteredPath)) {
				WE.getConsole().add("not a valid path");
			} else {
				switch (enteredPath) {
					case "/":
						path = "";
						break;
					case "..":
						if (path.length() > 1) {
							path = "";
						}
						break;
					default:
						path = path.concat(enteredPath);//then add new path
						//if access to map
						if (path.length() > 0) {
							String mapName;
							if (path.contains(":")) {
								mapName = path.substring(0, path.indexOf(':'));
							} else {
								mapName = path;
							}
							//load map cvar
							if (WE.getCVarsMap() == null) {
								WE.getCVars().setMapCVars(new CVarSystemMap(
									new File(WorkingDirectory.getMapsFolder() + "/" + mapName + "/meta.wecvar")
								));
								WE.getCVarsMap().load();
							}

							//access save
							if (path.contains(":")) {
								WE.getCVarsMap().setSaveCVars(new CVarSystemSave(
									new File(
										WorkingDirectory.getMapsFolder() + "/" + mapName + "/save" + path.substring(path.indexOf(':') + 1) + "/meta.wecvar"
									)
								)
								);
								WE.getCVarsSave().load();
							}

						}
						break;
				}
			}
		}
		return true;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "change the directory";
	}

}
