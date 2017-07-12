package com.aeroextrem.util;

import com.aeroextrem.engine.common3d.behaviour.BehaviourBase;
import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import org.jetbrains.annotations.NotNull;

public interface BehaviourParticle extends BehaviourBase {

	@Override
	void onCreate(@NotNull GameResource resource);

	void onBindParticles(@NotNull ParticleSystem ps);

}
