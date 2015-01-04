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

package com.BombingGames.WurfelEngine.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * A view is an object which renders the data. Game space or not does not matter for this class.
 * @author Benedikt Vogler
 */
public abstract class View {
    private static ShaderProgram shader;
	
    public abstract SpriteBatch getBatch();
    public abstract ShapeRenderer getShapeRenderer();
	
	/**
	 * true if current rendering is debug only
	 */
	private boolean inDebug;
	
    public void init(){
		String vertexShader;
		String fragmentShader;
		//shaders are very fast to load and the asset loader does not support text files out of the box
		if (CVar.get("LEnormalMapRendering").getValueb()){
			vertexShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/vertexNM.vs").readString();
			fragmentShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/fragmentNM.fs").readString();
		} else {
			vertexShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/vertex.vs").readString();
			fragmentShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/fragment.fs").readString();
		}
		//Setup shader
		ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
		//print any warnings
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		
		//setup default uniforms
		shader.begin();
 
		//our normal map
		shader.setUniformi("u_normals", 1); //GL_TEXTURE1
		//Light RGB and intensity (alpha)
		Vector3 LIGHT_COLOR = new Vector3(1f, 0.8f, 0.6f);
 
			//Ambient RGB and intensity (alpha)
		Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 1f);
		
		float AMBIENT_INTENSITY = 0.2f;
		float LIGHT_INTENSITY = 1f;
		
		//LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
		shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
		shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
		
		shader.end();
    }
    
    public ShaderProgram getShader() {
        return shader;
    }
	
    	/**
	 * enable debug rendering only
	 * @param debug 
	 */
	void setDebugRendering(boolean debug) {
		this.inDebug = debug;
	}
	
		/**
	 * 
	 * @return true if current rendering is debug only
	 */
	public boolean debugRendering() {
		return inDebug;
	}
}
