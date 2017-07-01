package com.aeroextrem.util;

import com.badlogic.gdx.InputProcessor;

import static com.badlogic.gdx.Input.Keys.ESCAPE;

/** Schaltet das Handeln von Inputs an oder aus (Observer-Pattern / Proxy) */
public class InputSwitch implements InputProcessor {

	private final InputProcessor processor;

	private final int escapeKey;

	/** Input handled? */
	public boolean enabled;

	public InputSwitch(InputProcessor processor) {
		this(processor, ESCAPE);
	}

	public InputSwitch(InputProcessor processor, int escapeKey) {
		this.processor = processor;
		this.escapeKey = escapeKey;
	}

	@Override
	public boolean keyUp(int keycode) {
		processor.keyUp(keycode);
		return keycode != escapeKey && enabled;
	}

	@Override
	public boolean keyDown(int keycode) {
		processor.keyDown(keycode);
		return keycode != escapeKey && enabled;
	}

	@Override
	public boolean keyTyped(char character) {
		processor.keyTyped(character);
		return enabled;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		processor.mouseMoved(screenX, screenY);
		return enabled;
	}

	@Override
	public boolean scrolled(int amount) {
		processor.scrolled(amount);
		return enabled;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		processor.touchDragged(screenX, screenY, pointer);
		return enabled;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		processor.touchDown(screenX, screenY, pointer, button);
		return enabled;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		processor.touchUp(screenX, screenY, pointer, button);
		return enabled;
	}

}
