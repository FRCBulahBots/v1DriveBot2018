/// FRC Team 3753 Bulahbots!
// Code written by Nicholas Lowrey-Dufour :)


package org.usfirst.frc.team3753.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a sample program to demonstrate how to use a gyro sensor to make a
 * robot drive straight. This program uses a joystick to drive forwards and
 * backwards while the gyro is used for direction keeping.
 */
public class Robot extends IterativeRobot {
	
	
	AHRS ahrs; // NavX MXP Class Library
	
	///DriveTrain Rotation section
	private JoystickToAngle DriveJoytoAng = new JoystickToAngle(RobotParamCollection.driveControllerDeadband, RobotParamCollection.turnKP);
	/// End of DriveTrain Rotation section
	
	/// Drive Motors
	private VictorSP DriveMotorLeft= new VictorSP(RobotParamCollection.kLeftMotorPort);
	private VictorSP DriveMotorRight = new VictorSP(RobotParamCollection.kRightMotorPort);
	
	private DifferentialDrive DriveTrain = new DifferentialDrive(DriveMotorLeft, DriveMotorRight);
	/// End of Drive Motors
	
	/// Elevator Manipulator
	private ElevatorManipulator elevator = new ElevatorManipulator(RobotParamCollection.kLiftMotorPort, RobotParamCollection.kLiftMotorMinLimitSwitchPort, RobotParamCollection.kLiftMotorMaxLimitSwitchPort);
	/// End of Elevator Manipulator
	
	/// Box Manipulator
	private BoxManipulator boxmanipulator = new BoxManipulator(RobotParamCollection.kLeftsideBoxMaipulatorPort, RobotParamCollection.kRightsideBoxManipulatorPort, RobotParamCollection.kMotorSpeedBoxManipulator);
	
	/// End of Box Manipulator
	private Joystick m_joystick = new Joystick(RobotParamCollection.kJoystickPort); // Main joystick controller for Robot

	@Override
	public void robotInit() {
		initNavXMXP();
	}

	@Override
	public void teleopPeriodic() {
		DriveJoytoAng.feed(m_joystick.getRawAxis(RobotParamCollection.DriveTrainRotateJoystickAxis), ahrs.getAngle()); // Feed the Angle calculator before we read from it
		DriveTrain.arcadeDrive(m_joystick.getY() * -1, DriveJoytoAng.getTurnData());
		
		elevator.loopFeed(((m_joystick.getRawAxis(2)*-1) + m_joystick.getRawAxis(3)));
		boxmanipulator.processCMD(RobotEnums.BoxManipulator.HOLD);
		if(m_joystick.getRawButton(5)) {
			boxmanipulator.processCMD(RobotEnums.BoxManipulator.DEPOSIT);
		}
		if(m_joystick.getRawButton(6)) {
			boxmanipulator.processCMD(RobotEnums.BoxManipulator.RECEIVE);
		}
		boxmanipulator.execute();
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
        SmartDashboard.putNumber(   "Gyro Angle",              ahrs.getAngle());

        SmartDashboard.putBoolean(  "IMU_IsMoving",         ahrs.isMoving());
        SmartDashboard.putBoolean(  "IMU_IsRotating",       ahrs.isRotating());
  
        /* Sensor Board Information                                                 */
       // SmartDashboard.putString(   "FirmwareVersion",      ahrs.getFirmwareVersion());
        /* Connectivity Debugging Support                                           */
        //SmartDashboard.putNumber(   "IMU_Byte_Count",       ahrs.getByteCount());
       // SmartDashboard.putNumber(   "IMU_Update_Count",     ahrs.getUpdateCount());
	}
}
