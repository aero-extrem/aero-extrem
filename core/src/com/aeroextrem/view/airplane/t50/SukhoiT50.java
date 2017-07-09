package com.aeroextrem.view.airplane.t50;

import com.badlogic.gdx.math.Vector3;

/** Nodes */
public class SukhoiT50 {

	public static final String BODY = "Body";
	public static final String AILERON_LEFT = "Aileron L";
	public static final String AILERON_RIGHT = "Aileron R";
	public static final String FLAPS_LEFT = "Flaps L";
	public static final String FLAPS_RIGHT = "Flaps R";
	public static final String LEAD_FLAPS_LEFT = "Leading endge flpas L";
	public static final String LEAD_FLAPS_RIGHT = "Leading endge flpas R";
	public static final String RUDDER_LEFT = "Rudder L";
	public static final String RUDDER_RIGHT = "Rudder R";

	public static final String GEAR_ROD_FRONT = "Cylinder";
	public static final String GEAR_ROD_BACKRIGHT = "Cylinder.001";
	public static final String GEAR_ROD_BACKLEFT = "Cylinder.002";
	public static final String GEAR_WHEEL_FRONT = "Cylinder.003";
	public static final String GEAR_WHEEL_BACKLEFT = "Cylinder.004";
	public static final String GEAR_WHEEL_BACKRIGHT = "Cylinder.005";

	// Das ist Sträflingsarbeit
	public static final Vector3 OFFSET_BODY = Vector3.Zero;
	public static final Vector3 OFFSET_AILERON_LEFT = new Vector3(-6.68996f, 0.06661f, 2.87737f);
	public static final Vector3 OFFSET_AILERON_RIGHT = new Vector3(-6.69023f, 0.06662f, -2.7657f);
	public static final Vector3 OFFSET_FLAPS_LEFT = new Vector3(-6.71660f, 0.07348f, 1.97175f);
	public static final Vector3 OFFSET_FLAPS_RIGHT = new Vector3(-6.70870f, 0.07366f, -2.03305f);
	public static final Vector3 OFFSET_LEAD_FLAPS_LEFT = new Vector3(-2.89715f,0.15185f,  0.89634f);
	public static final Vector3 OFFSET_LEAD_FLAPS_RIGHT = new Vector3(-2.88932f, 0.15213f, -0.89324f);
	public static final Vector3 OFFSET_RUDDER_LEFT = new Vector3(-7.25428f, 0.71260f, 1.49440f);
	public static final Vector3 OFFSET_RUDDER_RIGHT = new Vector3(-7.25459f, 0.71297f, -1.49454f);

	public static final Vector3 OFFSET_GEAR_ROD_FRONT = new Vector3(0f, -0.58484f, 0f);
	public static final Vector3 OFFSET_GEAR_ROD_BACKRIGHT = new Vector3(-5.27076f, -0.45215f, -1.13210f);
	public static final Vector3 OFFSET_GEAR_ROD_BACKLEFT = new Vector3(-5.27076f, -0.45216f, 1.16321f);
	public static final Vector3 OFFSET_GEAR_WHEEL_FRONT = new Vector3(0f, -0.97059f, 0f);
	public static final Vector3 OFFSET_GEAR_WHEEL_BACKLEFT = new Vector3(-5.26573f, -0.97059f, 1.16345f);
	public static final Vector3 OFFSET_GEAR_WHEEL_BACKRIGHT =  new Vector3(-5.26573f, -0.97059f, -1.12567f);
	
}
