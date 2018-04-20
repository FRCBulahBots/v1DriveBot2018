package org.usfirst.frc.team3753.robot;

public class RobotParamCollection {
	/// CONSTANT VALUES HERE
	public static final int DriveTrainRotateJoystickAxis = 4; // Joystick Axis we will use for turning the robot	
	
	public static final int kLeftMotorPort = 0; // Drive Motor Left side
	public static final int kRightMotorPort =  1; // Drive Motor Right side
	public static final double kMotorSpeedDriveTrain = 1.0f;
	public static final int kRightsideBoxManipulatorPort = 3; // Box Manipulator Right-side Drive motor Port
	public static final int kLeftsideBoxMaipulatorPort = 4; // Box Manipulator Left-side Drive motor Port
	public static final int kBoxMaipulatorActuatorin = 0; // Box Manipulator Solenoid In
	public static final int kBoxMaipulatorActuatorout = 1; // Box Manipulator Solenoid out
	public static final double kMotorSpeedBoxManipulator = 1.0f;
	
	public static final int kLiftMotorPort = 2; // Elevator Drive motor Port
	public static final int kLiftMotorMinLimitSwitchPort = 0; // Elevator Max Pos Limit Switch Port (DIO)
	public static final int kLiftMotorMaxLimitSwitchPort = 1; // Elevator Mix Pos Limit Switch Port (DIO)
	
	public static final int kJoystickPort = 0; // Controller joystick port on DS
	
	public static final double kturnKP = 0.009; // proportional turning constant for Yaw Gyro rotation Compensation
	public static final double kdriveControllerDeadband = 0.32f; // configurable deadband for when gyro should take control or not

}
