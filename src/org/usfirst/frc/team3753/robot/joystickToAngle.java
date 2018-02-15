package org.usfirst.frc.team3753.robot;

public class joystickToAngle {

	double finalAngle = 0.0f; // We need to keep this within a range of -180 and 180. We will start in neutral (0).
	double multiplier = 0.0f;
	
	public joystickToAngle(double mul) {
		multiplier = mul;
	}
	
	public void setMultiplier(double mul) {
		multiplier = mul;
	}
	
	public void overrideAngle(double ang) {
		finalAngle = ang;
	}
	
	public void feed(double val) { // Call this whenever we can to feed the sumer with new joystick data
		finalAngle += (val * multiplier);
		// finalAngle = clamp(finalAngle, -180, 180); // May not to clamp if AHRS is using continious output scheme.
	}
	
	public double getAngle() {
		return finalAngle;
	}
	
	public static double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
}
