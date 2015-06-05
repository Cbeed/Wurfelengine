/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.bombinggames.wurfelengine.Core.SoundEngine;

import com.badlogic.gdx.audio.Sound;
import com.bombinggames.wurfelengine.Core.Camera;
import com.bombinggames.wurfelengine.Core.GameView;
import com.bombinggames.wurfelengine.Core.Gameobjects.CoreData;
import com.bombinggames.wurfelengine.Core.Map.AbstractPosition;
import com.bombinggames.wurfelengine.WE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages the sounds in the game world.
 * @author Benedikt Vogler
 */
public class SoundEngine {
	private final HashMap<String, Sound> sounds = new HashMap<>(10);
	private ArrayList<SoundInstance> playingLoops = new ArrayList<>(4);
	private GameView view;

	/**
	 *
	 */
	public SoundEngine() {
		register("landing", "com/bombinggames/wurfelengine/Core/SoundEngine/Sounds/landing.wav");
		register("splash", "com/bombinggames/wurfelengine/Core/SoundEngine/Sounds/splash.wav");
		register("wind", "com/bombinggames/wurfelengine/Core/SoundEngine/Sounds/wind.ogg");
		register("explosion", "com/bombinggames/wurfelengine/Core/SoundEngine/Sounds/explosion2.wav");
	}
	
	/**
	 * Registers a soundIterator. The soundIterator must be loaded via asset manager.
 You can not register a soundIterator twice.
	 * @param identifier name of soundIterator
	 * @param path path of the soundIterator
	 */
	public void register(String identifier, String path){
		if (!sounds.containsKey(identifier)){
			sounds.put(identifier, (Sound) WE.getAsset(path));
		}
	}
	
	/***
	 * 
	 * @param identifier name of soundIterator
	 */
	public void play(String identifier){
		Sound result = sounds.get(identifier);
		if (result!=null)
			result.play(WE.CVARS.getValueF("sound"));
	}
	
	/***
	 * Plays soundIterator with decreasing volume depending on distance.
	 * @param identifier name of soundIterator
	 * @param pos the position of the soundIterator in the world. if it is null then play at center
	 */
	public void play(String identifier, AbstractPosition pos){
		Sound result = sounds.get(identifier);
		if (result != null){
			float volume =1;
			if (pos!=null)
				volume = getVolume(pos);
			else
				volume *= WE.CVARS.getValueF("sound");
			if (volume >= 0.1) //only play soundIterator louder>10%
				result.play(volume);
		}
	}
	
	/***
	 * 
	 * @param identifier name of soundIterator
	 * @param volume
	 * @return 
	 */
	public long play(String identifier, float volume){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume*WE.CVARS.getValueF("sound"));
		return 0;
	}
	
	/***
	 * 
	 * @param identifier name of soundIterator
	 * @param volume 
	 * @param pitch
	 * @return 
	 */
	public long play(String identifier, float volume, float pitch){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume*WE.CVARS.getValueF("sound"), pitch, 0);
		return 0;
	}
	
	/***
	 * 
	 * @param identifier name of soundIterator
	 * @param volume the volume in the range [0,1]
	 * @param pitch the pitch multiplier, 1 == default, &gt;1 == faster, &lt;1 == slower, the value has to be between 0.5 and 2.0
	 * @param pan panning in the range -1 (full left) to 1 (full right). 0 is center position.
	 * @return 
	 */
	public long play(String identifier, float volume, float pitch, float pan){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume*WE.CVARS.getValueF("sound"), pitch, pan);
		return 0;
	}
	
	/**
	 * playingLoops a soundIterator.
	 * @param identifier name of soundIterator
	 * @return the instance id
	 * @see com.​badlogic.​gdx.​audio.​Sound#loop
	 */
	public long loop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.loop(WE.CVARS.getValueF("sound"));
		return 0;
	}
	
	/**
	 * playingLoops a soundIterator. Sound decay not working.
	 * @param identifier name of soundIterator
	 * @param pos the position of the soundIterator in the game world. Should be a reference to the position of the object and no copy so that it updates itself.
	 * @return the instance id
	 * @see com.badlogic.​gdx.​audio.​Sound#loop()
	 */
	public long loop(String identifier, AbstractPosition pos) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			long id = result.loop(WE.CVARS.getValueF("sound"));
			playingLoops.add(new SoundInstance(this, result, id, pos));
			return id;
		}
		return 0;
	}
	
	

	/**
	 * Stops all instances of this soundIterator.
	 * @param identifier name of soundIterator
	 */
	public void stop(String identifier) {
		Sound result = sounds.get(identifier);
		//remove from playing loops list
		for (Iterator<SoundInstance> soundIterator = playingLoops.iterator(); soundIterator.hasNext();) {
			if (soundIterator.next().sound.equals(result))
				soundIterator.remove();
		}
		if (result != null)
			result.stop();
	}
	
	/**
	 * Stops a specifiy instance of the soundIterator.
	 * @param identifier name of soundIterator
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String) }.
	 * @see com.badlogic.gdx.audio.Sound#stop()
	 */
	public void stop(String identifier, long instance) {
		Sound result = sounds.get(identifier);
		//remove from playing loops list if with this instance id
		for (Iterator<SoundInstance> soundIterator = playingLoops.iterator(); soundIterator.hasNext();) {
			SoundInstance instanceSound = soundIterator.next();
			if (instanceSound.sound.equals(result) && instanceSound.id ==instance)
				soundIterator.remove();
		}
		if (result != null)
			result.stop(instance);
	}

	/**
	 * Set the volume of a playing instance.
	 * @param identifier name of soundIterator
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String) }.
	 * @param volume 
	 * @see com.​badlogic.​gdx.​audio.​Sound#setVolume()
	 */
	public void setVolume(String identifier, long instance, float volume) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.setVolume(instance, volume);
	}
	
	/**
	 *
	 * @param dt
	 */
	public void update(float dt){
		for (SoundInstance sound : playingLoops) {
			sound.update();
		}
	}
	
	/**
	 * disposes the sounds
	 */
	public void dispose(){
		//if you dispose the sounds they do not play if you reload a game
//		for (Sound s : sounds.values()) {
//			s.dispose();
//		}
	}
	
	/**
	 * calculates the volume of a soundIterator based on the positon in the game world. Compares to cameras.
	 * @param pos position in the world.
	 * @return multiplied with the settings for the volume
	 */
	protected float getVolume(AbstractPosition pos){
		float volume = 1;
		if (view != null) {
			//calculate minimal distance to camera
			float minDistance = Float.POSITIVE_INFINITY;
			for (Camera camera : view.getCameras()) {
				float distance = pos.toPoint().distanceTo(camera.getCenter());
				if (distance < minDistance)
					minDistance = distance;
			}

			int decay = WE.CVARS.getValueI("soundDecay");
			volume = decay*CoreData.GAME_EDGELENGTH / (minDistance*minDistance + decay*CoreData.GAME_EDGELENGTH);//loose energy radial
			if (volume > 1)
				volume = 1;
		}
		return volume*WE.CVARS.getValueF("sound");
	}

	/**
	 *
	 * @param view
	 */
	public void setView(GameView view) {
		this.view = view;
	}

}
