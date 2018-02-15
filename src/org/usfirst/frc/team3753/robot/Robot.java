/// FRC Team 3753 Bulahbots!
// Code written by Nicholas Lowrey-Dufour :)


package org.usfirst.frc.team3753.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a sample program to demonstrate how to use a gyro sensor to make a
 * robot drive straight. This program uses a joystick to drive forwards and
 * backwards while the gyro is used for direction keeping.
 */
public class Robot extends IterativeRobot {
	
	/// CONSTANT VALUES HERE
	private static final double DriveTrainRotatejoytoAngMultiplier = 0.1f; // Will need tuning
	private static final int DriveTrainRotateJoystickAxis = 4; // Joystick Axis we will use for turning the robot
	
	private static final double kP = 0.009; // proportional turning constant for Yaw Gyro rotation Compensation
	
	private static final int kLeftMotorPort = 0; // Drive Motor Left side
	private static final int kRightMotorPort =  1; // Drive Motor Right side
	
	private static final int kLiftMotorPort = 2; // Elevator Drive motor Port
	private static final int kLiftMotorMaxLimitSwitchPort = 0; // Elevator Max Pos Limit Switch Port (DIO)
	private static final int kLiftMotorMinLimitSwitchPort = 1; // Elevator Mix Pos Limit Switch Port (DIO)
	
	private static final int kJoystickPort = 0; // Controller joystick port on DS
	/// END OF CONSTANTS
	
	AHRS ahrs; // NavX MXP Class Library
	
	///DriveTrain Rotation section
	private joystickToAngle DriveJoytoAng = new joystickToAngle(DriveTrainRotatejoytoAngMultiplier);
	/// End of DriveTrain Rotation section
	
	/// Drive Motors
	private VictorSP DriveMotorLeft= new VictorSP(kLeftMotorPort);
	private VictorSP DriveMotorRight = new VictorSP(kRightMotorPort);
	
	private DifferentialDrive DriveTrain = new DifferentialDrive(DriveMotorLeft, DriveMotorRight);
	/// End of Drive Motors
	
	/// Elevator Manipulator
	private ElevatorManipulator elevator = new ElevatorManipulator(kLiftMotorPort, kLiftMotorMinLimitSwitchPort, kLiftMotorMaxLimitSwitchPort);
	/// End of Elevator Manipulator
	
	
	private Joystick m_joystick = new Joystick(kJoystickPort); // Main joystick controller for Robot

	@Override
	public void robotInit() {
		initNavXMXP();
	}

	@Override
	public void teleopPeriodic() {
		DriveJoytoAng.feed(m_joystick.getRawAxis(DriveTrainRotateJoystickAxis)); // Feed the Angle calculator before we read from it
		double turningValue = (DriveJoytoAng.getAngle() - ahrs.getAngle()) * kP;
		// Invert the direction of the turn if we are going backwards
		turningValue = Math.copySign(turningValue, m_joystick.getRawAxis(DriveTrainRotateJoystickAxis)); // Read val from feeder or raw Axis val???
		DriveTrain.arcadeDrive(m_joystick.getY() * -1, turningValue);
		
		elevator.loopFeed(((m_joystick.getRawAxis(2)*-1) + m_joystick.getRawAxis(3)));
		
		pushNavXDataToDash();
	}
	
	
	public void initNavXMXP() {
		try {
	          ahrs = new AHRS(SPI.Port.kMXP); 
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
	      }
	}
	
	public void pushNavXDataToDash() {
		 /* Display 6-axis Processed Angle Data                                      */
        SmartDashboard.putBoolean(  "IMU_Connected",        ahrs.isConnected());
        SmartDashboard.putBoolean(  "IMU_IsCalibrating",    ahrs.isCalibrating());
        SmartDashboard.putNumber(   "IMU_Yaw",              ahrs.getYaw());
        SmartDashboard.putNumber(   "IMU_Pitch",            ahrs.getPitch());
        SmartDashboard.putNumber(   "IMU_Roll",             ahrs.getRoll());
        
        /* Display tilt-corrected, Magnetometer-based heading (requires             */
        /* magnetometer calibration to be useful)                                   */
        
        SmartDashboard.putNumber(   "IMU_CompassHeading",   ahrs.getCompassHeading());
        
        /* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
        SmartDashboard.putNumber(   "IMU_FusedHeading",     ahrs.getFusedHeading());

        /* These functions are compatible w/the WPI Gyro Class, providing a simple  */
        /* path for upgrading from the Kit-of-Parts gyro to the navx-MXP            */
        
        SmartDashboard.putNumber(   "IMU_TotalYaw",         ahrs.getAngle());
        SmartDashboard.putNumber(   "IMU_YawRateDPS",       ahrs.getRate());

        /* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */
        
        SmartDashboard.putNumber(   "IMU_Accel_X",          ahrs.getWorldLinearAccelX());
        SmartDashboard.putNumber(   "IMU_Accel_Y",          ahrs.getWorldLinearAccelY());
        SmartDashboard.putBoolean(  "IMU_IsMoving",         ahrs.isMoving());
        SmartDashboard.putBoolean(  "IMU_IsRotating",       ahrs.isRotating());

        /* Display estimates of velocity/displacement.  Note that these values are  */
        /* not expected to be accurate enough for estimating robot position on a    */
        /* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
        /* of these errors due to single (velocity) integration and especially      */
        /* double (displacement) integration.                                       */
        
        SmartDashboard.putNumber(   "Velocity_X",           ahrs.getVelocityX());
        SmartDashboard.putNumber(   "Velocity_Y",           ahrs.getVelocityY());
        SmartDashboard.putNumber(   "Displacement_X",       ahrs.getDisplacementX());
        SmartDashboard.putNumber(   "Displacement_Y",       ahrs.getDisplacementY());
        
        /* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
        /* NOTE:  These values are not normally necessary, but are made available   */
        /* for advanced users.  Before using this data, please consider whether     */
        /* the processed data (see above) will suit your needs.                     */
        
        SmartDashboard.putNumber(   "RawGyro_X",            ahrs.getRawGyroX());
        SmartDashboard.putNumber(   "RawGyro_Y",            ahrs.getRawGyroY());
        SmartDashboard.putNumber(   "RawGyro_Z",            ahrs.getRawGyroZ());
        SmartDashboard.putNumber(   "RawAccel_X",           ahrs.getRawAccelX());
        SmartDashboard.putNumber(   "RawAccel_Y",           ahrs.getRawAccelY());
        SmartDashboard.putNumber(   "RawAccel_Z",           ahrs.getRawAccelZ());
        SmartDashboard.putNumber(   "RawMag_X",             ahrs.getRawMagX());
        SmartDashboard.putNumber(   "RawMag_Y",             ahrs.getRawMagY());
        SmartDashboard.putNumber(   "RawMag_Z",             ahrs.getRawMagZ());
        SmartDashboard.putNumber(   "IMU_Temp_C",           ahrs.getTempC());
        
        /* Omnimount Yaw Axis Information                                           */
        /* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
        AHRS.BoardYawAxis yaw_axis = ahrs.getBoardYawAxis();
        SmartDashboard.putString(   "YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
        SmartDashboard.putNumber(   "YawAxis",              yaw_axis.board_axis.getValue() );
        
        /* Sensor Board Information                                                 */
        SmartDashboard.putString(   "FirmwareVersion",      ahrs.getFirmwareVersion());
        
        /* Quaternion Data                                                          */
        /* Quaternions are fascinating, and are the most compact representation of  */
        /* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
        /* from the Quaternions.  If interested in motion processing, knowledge of  */
        /* Quaternions is highly recommended.                                       */
        SmartDashboard.putNumber(   "QuaternionW",          ahrs.getQuaternionW());
        SmartDashboard.putNumber(   "QuaternionX",          ahrs.getQuaternionX());
        SmartDashboard.putNumber(   "QuaternionY",          ahrs.getQuaternionY());
        SmartDashboard.putNumber(   "QuaternionZ",          ahrs.getQuaternionZ());
        
        /* Connectivity Debugging Support                                           */
        SmartDashboard.putNumber(   "IMU_Byte_Count",       ahrs.getByteCount());
        SmartDashboard.putNumber(   "IMU_Update_Count",     ahrs.getUpdateCount());
	}
}
