package com.aeroextrem.scenario.simulation;

import com.aeroextrem.database.PlaybackCache;
import com.aeroextrem.database.RecordRow;
import com.aeroextrem.engine.common3d.resource.PhysicsPartInstance;
import com.aeroextrem.engine.util.ChaseCameraController;
import com.aeroextrem.util.AeroExtrem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import static com.aeroextrem.view.airplane.test.TestPlaneResource.NODE_FUSELAGE;

/** Aus einer SQL-Datenbank rekreierte Simulation */
public class PlaybackSimulation extends Simulation {

	private final int recordingID;
	private final PlaybackCache playback;

	private boolean done = false;
	private RecordRow lastRow = null;

	public PlaybackSimulation(int recordingID) {
		this.recordingID = recordingID;
		this.playback = new PlaybackCache(AeroExtrem.db, recordingID);
	}

	@Override
	protected void createUI() {
		menu.create();
		menuController.createMenu(new PlaybackPauseWindow());
	}

	@Override
	protected void setupInput() {
		//inputProcessor.putProcessor(INPUT_PAUSE,		pauseMenuInput = new InputSwitch(menu.getStage()));
		Matrix4 planeTrans = plane.getNode(NODE_FUSELAGE).globalTransform;
		inputProcessor.putProcessor(INPUT_CHASE_CAM,	inputCam = new ChaseCameraController(cam, planeTrans));
		inputProcessor.putProcessor(INPUT_SIM,		inputSim = new SimulationInput(this, pauseMenuInput));
	}

	@Override
	protected void renderDebugScreen(SpriteBatch sb) {
		debugFont.draw(sb, String.format("[RED]PLAYBACK…[]"), 0, Gdx.graphics.getHeight() - 20);
	}

	@Override
	public void render() {
		readFrame();

		super.render();
	}

	protected void readFrame() {
		RecordRow row;
		if(!done && (row = playback.getNextRow()) != null) {
			setData(lastRow = row);
		} else {
			setData(lastRow);
			done = true;
			showPauseMenu = true;
		}
	}

	private void setData(RecordRow row) {
		PhysicsPartInstance part = plane.partMap.get(NODE_FUSELAGE);
		part.ms.transform.set(
				row.PosX, row.PosY, row.PosZ,
				row.RotX, row.RotY, row.RotZ, row.RotW
		);
		planeData.pitch = row.DeflectPitch;
		planeData.roll = row.DelfectRoll;
		planeData.yaw = row.DeflectYaw;
		planeData.thrust = row.Thrust;
	}
}
