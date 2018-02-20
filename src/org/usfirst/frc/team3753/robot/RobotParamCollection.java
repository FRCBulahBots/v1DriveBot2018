package org.usfirst.frc.team3753.robot;

public class RobotParamCollection {
	/// CONSTANT VALUES HERE
	public static final double DriveTrainRotatejoytoAngMultiplier = 0.1f; // Will need tuning
	public static final int DriveTrainRotateJoystickAxis = 4; // Joystick Axis we will use for turning the robot	
	
	public static final int kLeftMotorPort = 0; // Drive Motor Left side
	public static final int kRightMotorPort =  1; // Drive Motor Right side
	
	public static final int kRightsideBoxManipulatorPort = 3;
	public static final int kLeftsideBoxMaipulatorPort = 4;
	public static final double kMotorSpeedBoxManipulator = 1.0f;
	
	public static final int kLiftMotorPort = 2; // Elevator Drive motor Port
	public static final int kLiftMotorMinLimitSwitchPort = 0; // Elevator Max Pos Limit Switch Port (DIO)
	public static final int kLiftMotorMaxLimitSwitchPort = 1; // Elevator Mix Pos Limit Switch Port (DIO)
	
	public static final int kJoystickPort = 0; // Controller joystick port on DS
	
	public static final double turnKP = 0.009; // proportional turning constant for Yaw Gyro rotation Compensation
	public static final double driveControllerDeadband = 0.32f; // configurable deadband for when gyro should take control or not

}
