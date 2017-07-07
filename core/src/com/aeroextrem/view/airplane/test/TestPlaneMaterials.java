package com.aeroextrem.view.airplane.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

/** Materialien des Testflugzeugs */
class TestPlaneMaterials {

	public static final Material PLANE_STATIC = new Material(ColorAttribute.createDiffuse(Color.BLUE), ColorAttribute.createSpecular(Color.WHITE));
	public static final Material PLANE_DYNAMIC = new Material(ColorAttribute.createDiffuse(Color.ROYAL), ColorAttribute.createSpecular(Color.WHITE));

}
