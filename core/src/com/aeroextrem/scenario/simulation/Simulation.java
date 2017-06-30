package com.aeroextrem.scenario.simulation;

import com.aeroextrem.engine.common3d.Common3D;
import com.aeroextrem.engine.common3d.resource.InstanceIdentifier;
import com.aeroextrem.engine.resource.ResourceManager;
import com.aeroextrem.engine.util.ChaseCameraController;
import com.aeroextrem.scenario.view.skybox.SkyboxResource;
import com.aeroextrem.scenario.view.terrain.TerrainResource;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/** Platzhalter für die Simulation */
public class Simulation extends Common3D {

	ChaseCameraController cameraController;

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

		InstanceIdentifier terrain = spawn(ResourceManager.get(TerrainResource.class));
		InstanceIdentifier sky = spawn(ResourceManager.get(SkyboxResource.class));

		Vector3 pos = new Vector3();
		cameraController = new ChaseCameraController(cam, ((ModelInstance)instances.get(terrain).instance).transform.getTranslation(pos));
		Gdx.input.setInputProcessor(cameraController);
	}

	@Override
	protected void renderUI(SpriteBatch sb) {

	}

	@Override
	protected void updateCamera() {
		cameraController.update();
	}

	@Override
	public void dispose() {
		ResourceManager.unload(TerrainResource.class);
	}
}
