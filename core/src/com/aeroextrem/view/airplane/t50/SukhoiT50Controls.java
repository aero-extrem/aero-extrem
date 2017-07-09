package com.aeroextrem.view.airplane.t50;

import com.aeroextrem.engine.common3d.behaviour.BehaviourInput;
import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntIntMap;
import org.jetbrains.annotations.NotNull;

import static com.badlogic.gdx.Input.Keys.*;

/** Benutzereingaben */
public class SukhoiT50Controls implements BehaviourInput {

	// (0 -> 1)
	public float thrust = 0f;

	// CCWISE -> CWISE (-1 -> 1)
	public float roll = 0f;

	// LEFT -> RIGHT (-1 -> 1)
	public float yaw = 0f;

	// DOWN -> UP (-1 -> 1)
	public float pitch = 0f;

	private SukhoiT50Input input;

	@Override
	public void onCreate(@NotNull GameResource resource) {
		input = new SukhoiT50Input();
	}

	@Override
	public @NotNull InputProcessor onBindInput() {
		return input;
	}

	@Override
	public void onInputUpdate() {
		input.update();
	}

	class SukhoiT50Input extends InputAdapter {

		private final IntIntMap keys = new IntIntMap();

		private boolean hardPitch = false;
		private boolean hardYaw = false;
		private boolean hardRoll = false;

		private static final float THRUST_SPD = 0.01f;
		private static final float DEFLECT_SPD = 0.02f;

		@Override
		public boolean keyDown(int keycode) {
			keys.put(keycode, keycode);
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
				thrust = 1f;
			else if(keys.containsKey(X))
				thrust = 0f;

			// Hard controls
			if(keys.containsKey(SHIFT_LEFT)) {
				if(keys.containsKey(A)) {
					yaw = -1f;
					hardYaw = true;
				} else if(keys.containsKey(D)) {
					yaw = 1f;
					hardYaw = true;
				}

				if(keys.containsKey(W)) {
					pitch = -1f;
					hardPitch = true;
				} else if(keys.containsKey(S)) {
					pitch = 1f;
					hardPitch = true;
				}

				if(keys.containsKey(Q)) {
					roll = -1f;
					hardRoll = true;
				} else if(keys.containsKey(E)) {
					roll = 1f;
					hardRoll = true;
				}
			}
			// Soft controls
			else {
				// Reset hard controls
				if(hardYaw) {
					yaw = 0f;
					hardYaw = false;
				}
				if(hardPitch) {
					pitch = 0f;
					hardPitch = false;
				}
				if(hardRoll) {
					roll = 0f;
					hardRoll = false;
				}

				// Calculate soft controls
				if(keys.containsKey(A))
					yaw = Math.max(-1f, yaw - DEFLECT_SPD);
				else if(keys.containsKey(D))
					yaw = Math.min(1f, yaw + DEFLECT_SPD);

				if(keys.containsKey(W))
					pitch = Math.max(-1f, pitch - DEFLECT_SPD);
				else if(keys.containsKey(S))
					pitch = Math.min(1f, pitch + DEFLECT_SPD);

				if(keys.containsKey(Q))
					roll = Math.max(-1f, roll - DEFLECT_SPD);
				else if(keys.containsKey(E))
					roll = Math.min(1f, roll - DEFLECT_SPD);
			}
		}

	}

}
