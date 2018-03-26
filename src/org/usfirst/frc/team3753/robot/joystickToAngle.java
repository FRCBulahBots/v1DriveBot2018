package org.usfirst.frc.team3753.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class JoystickToAngle {

	double finalOutput;
	double deadBandOffset = 0.1f; // values used to show to determine if joystick is close enough to 0 to be considered 'untouched' so gyro can take over.
	double currentJoystickValue;
	double currentGyroAngle;
	double gyroSetpointAngle;
	double kPSetpoint = 0.009f;
	boolean gyroControl = false;
	
	
	public JoystickToAngle(double deadband, double kP) {
		kPSetpoint = kP;
		deadBandOffset = deadband;
	}

	
	public void overrideAngle(double ang) {
		gyroSetpointAngle = ang;
	}
	
	public void feed(double joyval, double gyroangle) { // Call this whenever we can to feed the sumer with new joystick data
		currentJoystickValue = joyval;
		currentGyroAngle = gyroangle;
		if (!(!(joyval > (deadBandOffset * -1)) || (joyval > deadBandOffset))) { // Joystick is within deadband value Need to do Gyro compensation.
			gyroControl = true;
//			finalOutput = (gyroSetpointAngle - gyroangle) * kPSetpoint;
			finalOutput = clamp((gyroSetpointAngle - gyroangle) * kPSetpoint, -1, 1); // Ensure values are clamped for the 
		} else {
			gyroControl = false;
			finalOutput = joyval * 1.0f;
		gyroSetpointAngle = gyroangle;
		}
		pushDashData();
	}
	
	
	public void autoFeed(double gyroangle) { // Call this whenever we can to feed the sumer with new joystick data
		currentGyroAngle = gyroangle;
			gyroControl = true;
//			finalOutput = (gyroSetpointAngle - gyroangle) * kPSetpoint;
			finalOutput = clamp((gyroSetpointAngle - gyroangle) * kPSetpoint, -1, 1); // Ensure values are clamped for the 
		pushDashData();
	}
	
	public double getTurnData() {
		return finalOutput;
	}
	
	private void pushDashData() {
	//	SmartDashboard.putNumber("Turning Joystick value: ", currentJoystickValue);
		SmartDashboard.putBoolean("Gyro in control: ", gyroControl);
		SmartDashboard.putNumber("Gyro Setpoint Angle: ", gyroSetpointAngle);
		SmartDashboard.putNumber("Turning output Value: ", finalOutput);
	}
	
	public static double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
}
