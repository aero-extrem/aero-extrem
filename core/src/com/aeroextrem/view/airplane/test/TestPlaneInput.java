package com.aeroextrem.view.airplane.test;

import com.aeroextrem.engine.common3d.behaviour.BehaviourInput;
import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntIntMap;
import org.jetbrains.annotations.NotNull;

import static com.badlogic.gdx.Input.Keys.*;

public class TestPlaneInput implements BehaviourInput {

	private Input input;

	private final TestPlaneData data;

	public TestPlaneInput(TestPlaneData data) {
		this.data = data;
	}

	@Override
	public void onCreate(@NotNull GameResource resource) {
		input = new Input();
	}

	@Override
	public @NotNull InputProcessor onBindInput() {
		return input;
	}

	@Override
	public void onInputUpdate() {
		input.update();
	}

	class Input extends InputAdapter {

		private final IntIntMap keys = new IntIntMap();

		private boolean hardPitch = false;
		private boolean hardYaw = false;
		private boolean hardRoll = false;

		boolean brakes = false;

		private static final float THRUST_SPD = 0.01f;
		private static final float DEFLECT_SPD = 0.02f;

		@Override
		public boolean keyDown(int keycode) {
			keys.put(keycode, keycode);
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			if (character == 'b' || character == 'B') {
				brakes = !brakes;
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			keys.remove(keycode, 0);
			return false;
		}

		public void update() {
			// Toggles
			if(keys.containsKey(Y))
				data.thrust = 1f;
			else if(keys.containsKey(X))
				data.thrust = 0f;

			// Hard controls
			if(keys.containsKey(L)) {
				if(keys.containsKey(A)) {
					data.yaw = -1f;
					hardYaw = true;
				} else if(keys.containsKey(D)) {
					data.yaw = 1f;
					hardYaw = true;
				}

				if(keys.containsKey(W)) {
					data.pitch = -1f;
					hardPitch = true;
				} else if(keys.containsKey(S)) {
					data.pitch = 1f;
					hardPitch = true;
				}

				if(keys.containsKey(Q)) {
					data.roll = -1f;
					hardRoll = true;
				} else if(keys.containsKey(E)) {
					data.roll = 1f;
					hardRoll = true;
				}
			}
			// Soft controls
			else {
				// Reset hard controls
				if(hardYaw) {
					data.yaw = 0f;
					hardYaw = false;
				}
				if(hardPitch) {
					data.pitch = 0f;
					hardPitch = false;
				}
				if(hardRoll) {
					data.roll = 0f;
					hardRoll = false;
				}

				// Calculate soft controls
				if(keys.containsKey(A))
					data.yaw = Math.max(-1f, data.yaw - DEFLECT_SPD);
				else if(keys.containsKey(D))
					data.yaw = Math.min(1f, data.yaw + DEFLECT_SPD);

				if(keys.containsKey(W))
					data.pitch = Math.max(-1f, data.pitch - DEFLECT_SPD);
				else if(keys.containsKey(S))
					data.pitch = Math.min(1f, data.pitch + DEFLECT_SPD);

				if(keys.containsKey(Q))
					data.roll = Math.max(-1f, data.roll - DEFLECT_SPD);
				else if(keys.containsKey(E))
					data.roll = Math.min(1f, data.roll - DEFLECT_SPD);

				if(keys.containsKey(SHIFT_LEFT))
					data.thrust = Math.min(1f, data.thrust + THRUST_SPD);
				else if(keys.containsKey(CONTROL_LEFT))
					data.thrust = Math.max(1f, data.thrust - THRUST_SPD);
			}
		}

	}

}
