package com.aeroextrem.engine.util;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.ArrayMap;

/** An {@link InputProcessor} that delegates to a map of other InputPorcessors.
 * Delegation for an event stops if a processor returns true, which indicates that the event was hanled.
 * Based on {@link com.badlogic.gdx.InputMultiplexer}.
 * 
 * @author terorie
 * @author Natan Sweet */
public class InputMappedMultiplexer implements InputProcessor {

	private ArrayMap<String, InputProcessor> processors = new ArrayMap<>(4);
	
	public void putProcessor(String name, InputProcessor processor) {
		processors.put(name, processor);
	}
	
	public void removeProcessor(String name) {
		processors.removeKey(name);
	}
	
	public int size() {
		return processors.size;
	}
	
	public void clear() {
		processors.clear();
	}

	@Override
	public boolean keyDown(int keycode) {
		for(int i = 0, n = processors.size; i < n; ++i)
			if(processors.getValueAt(i).keyDown(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		for(int i = 0, n = processors.size; i < n; ++i) 
			if(processors.getValueAt(i).keyTyped(character)) return true;
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		for(int i = 0, n = processors.size; i < n; ++i)
			if(processors.getValueAt(i).keyUp(keycode)) return true;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(int i = 0, n = processors.size; i < n; ++i)
			if(processors.getValueAt(i).touchDown(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(int i = 0, n = processors.size; i < n; ++i)
			if(processors.getValueAt(i).touchUp(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for(int i = 0, n = processors.size; i < n; ++i)
			if(processors.getValueAt(i).touchDragged(screenX, screenY, pointer)) return true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(int i = 0, n = processors.size; i < n; ++i)
			if(processors.getValueAt(i).mouseMoved(screenX, screenY)) return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		for(int i = 0, n = processors.size; i < n; ++i)
			if(processors.getValueAt(i).scrolled(amount)) return true;
		return false;
	}
}
