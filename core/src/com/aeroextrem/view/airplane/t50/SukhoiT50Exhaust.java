package com.aeroextrem.view.airplane.t50;

import com.aeroextrem.engine.common3d.behaviour.BehaviourVisual;
import com.aeroextrem.engine.resource.GameResource;
import com.aeroextrem.util.BehaviourParticle;
import com.aeroextrem.view.airplane.test.TestPlaneData;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import org.jetbrains.annotations.NotNull;

public class SukhoiT50Exhaust implements BehaviourParticle, BehaviourVisual {

	private final TestPlaneData data;
	private ModelInstance instance;
	private AssetManager asm;
	private ParticleEffect left;
	private ParticleEffect right;

	public SukhoiT50Exhaust(TestPlaneData data) {
		this.data = data;
	}

	@Override
	public void onCreate(@NotNull GameResource resource) {
		asm = new AssetManager();
	}

	@Override
	public void onCreateVisuals(@NotNull ModelInstance instance) {
		this.instance = instance;
	}

	@Override
	public void render(@NotNull ModelBatch batch) {
		left.setTransform(instance.transform);
	}

	@Override
	public void onBindParticles(@NotNull ParticleSystem ps) {
		ParticleEffectLoader.ParticleEffectLoadParameter loadParam =
				new ParticleEffectLoader.ParticleEffectLoadParameter(ps.getBatches());
		asm.load("particle/engine.pfx", ParticleEffect.class, loadParam);
		asm.finishLoading();

		ParticleEffect originalEffect = asm.get("particle/engine.pfx");
		// we cannot use the originalEffect, we must make a copy each time we create new particle effect
		left = originalEffect.copy();
		left.init();
		left.start();  // optional: particle will begin playing immediately
		ps.add(left);

		right = originalEffect.copy();
		right.init();
		right.start();  // optional: particle will begin playing immediately
		ps.add(right);
	}

	@Override
	public void dispose() {
		asm.dispose();
	}

}
