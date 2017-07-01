package com.aeroextrem.scenario.simulation;

import com.aeroextrem.engine.common3d.Common3D;
import com.aeroextrem.engine.common3d.resource.InstanceIdentifier;
import com.aeroextrem.engine.resource.ResourceManager;
import com.aeroextrem.engine.util.ChaseCameraController;
import com.aeroextrem.scenario.view.skybox.SkyboxResource;
import com.aeroextrem.scenario.view.terrain.TerrainResource;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/** Platzhalter für die Simulation */
public class Simulation extends Common3D {

	ChaseCameraController chaseCam;
	BitmapFont debugFont;

	@Override
	public void load() {
		super.load();
		ResourceManager.load(TerrainResource.class);
		ResourceManager.load(SkyboxResource.class);
	}

	@Override
	public void lateLoad() {
		ResourceManager.lateLoad(TerrainResource.class);
		ResourceManager.lateLoad(SkyboxResource.class);

		super.lateLoad();

		//InstanceIdentifier terrain = spawn(ResourceManager.get(TerrainResource.class));
		InstanceIdentifier sky = spawn(ResourceManager.get(SkyboxResource.class));

		chaseCam = new ChaseCameraController(cam, ((ModelInstance)instances.get(sky).instance).transform);
		Gdx.input.setInputProcessor(chaseCam);

		debugFont = new BitmapFont();
	}

	@Override
	protected void renderUI(SpriteBatch sb) {
		sb.begin();
		sb.setColor(0f, 0f, 0f, 1f);
		debugFont.draw(sb, String.format("Theta: %.2f, Phi: %.2f", chaseCam.theta, chaseCam.phi), 20, 20);
		sb.end();
	}

	@Override
	protected void updateCamera() {
		chaseCam.update();
	}

	@Override
	public void dispose() {
		ResourceManager.unload(TerrainResource.class);
	}
}
