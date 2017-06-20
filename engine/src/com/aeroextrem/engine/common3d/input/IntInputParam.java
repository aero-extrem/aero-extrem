package com.aeroextrem.engine.common3d.input;

import com.badlogic.gdx.math.MathUtils;

/** MVC Modell: Ganzzahlige Benutzereingabe */
public class IntInputParam implements InputParam<Integer> {

	public final int min, max, defaultValue, lerpSpeed;

	private int value;

	public IntInputParam(int min, int max, int defaultValue) {
		this(min, max, defaultValue, 1);
	}

	public IntInputParam(int min, int max, int defaultValue, int lerpSpeed) {
		assert min < max;
		assert min <= value;
		assert defaultValue <= max;

		this.min = min;
		this.max = max;

		this.value = defaultValue;
		this.defaultValue = defaultValue;
		this.lerpSpeed = lerpSpeed;
	}

	// _get and _set don't box like set and get

	public int _set(int val) {
		if(val < min)
			value = min;
		else if(val > max)
			value = max;
		else
			value = val;

		return value;
	}

	public int _get() {
		return value;
	}

	public int lerp() {
		// TODO Ints only
		return _set((int) MathUtils.lerp(value, defaultValue, (lerpSpeed/(float) Math.abs((value)))));
	}

	public int inc() {
		return _set(value+1);
	}

	public int dec() {
		return _set(value-1);
	}

	public int add(int amount) {
		return _set(value + amount);
	}

	@Override
	public void set(Integer val) {
		_set(val);
	}

	@Override
	public Integer get() {
		return _get();
	}

}
