/// FRC Team 3753 Bulahbots!
// Code written by Nicholas Lowrey-Dufour :)


package org.usfirst.frc.team3753.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
/**
 * This is a sample program to demonstrate how to use a gyro sensor to make a
 * robot drive straight. This program uses a joystick to drive forwards and
 * backwards while the gyro is used for direction keeping.
 */


public class Robot extends IterativeRobot {
	
	
	AHRS ahrs; // NavX MXP Class Library
	
    // Keeps track of time state was entered
    private Timer autonStateTimer;
    
    // Keeps track of current state
    private int autonState;
    
    // Sendable chooser to keep track of selected Auto modes
    @SuppressWarnings("rawtypes")
	private SendableChooser autoCmdFieldChooser;
    private int robotFieldPos = 0;
    // List of possible states    
    private final static int AUTON_STATE_DRIVE_FORWARD = 1;
    private final static int AUTON_STATE_ELEVATORRAISE = 2;
    private final static int AUTON_STATE_ELEVATORHOLD = 3;
    private final static int AUTON_STATE_DRIVE_STOP = 4;
    private final static int AUTON_STATE_FINISHED = 5;
    private final static int AUTON_STATE_DRIVE_PICKSWITCHSIDE = 6;
    
	///DriveTrain Rotation section
	private JoystickToAngle DriveJoytoAng = new JoystickToAngle(RobotParamCollection.kdriveControllerDeadband, RobotParamCollection.kturnKP);
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
	private BoxManipulator boxmanipulator = new BoxManipulator(RobotParamCollection.kLeftsideBoxMaipulatorPort, RobotParamCollection.kRightsideBoxManipulatorPort, RobotParamCollection.kBoxMaipulatorActuatorin, RobotParamCollection.kBoxMaipulatorActuatorout, RobotParamCollection.kMotorSpeedBoxManipulator);
	/// End of Box Manipulator
	
	private Joystick m_joystick = new Joystick(RobotParamCollection.kJoystickPort); // Main joystick controller for Robot

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void robotInit() {
		initNavXMXP();
		
		// Init the Camera server
		CameraServer.getInstance().startAutomaticCapture();
		// Test LED Controller stuffs here.
		I2CLEDController ledcontroller = new I2CLEDController();
		ledcontroller.setLEDAnimation(LEDFX.FX_MODE_CHASE_RAINBOW);
		System.out.println("LED CONTROLS SENT!!");
		autoCmdFieldChooser = new SendableChooser();
		autoCmdFieldChooser.addDefault("Center", 0);
		autoCmdFieldChooser.addObject("Left Side", 1);
		autoCmdFieldChooser.addObject("Right Side", 2);
	    SmartDashboard.putData("Autonomous Side Selector", autoCmdFieldChooser);
	    
	    // Activate Compressor
	    Compressor c = new Compressor(0);

	    c.setClosedLoopControl(true);
	    
	}

	@Override
	public void teleopPeriodic() {
		processDriveTrain();
		processElevator();
		processBoxManipulator();
		pushNavXDataToDash();
	}
	
	
	public void processElevator() {
		elevator.loopFeed(((m_joystick.getRawAxis(2) * -1) + m_joystick.getRawAxis(3)));
	}
	
	public void processBoxManipulator() {
		boxmanipulator.processCMD(RobotEnums.BoxManipulator.HOLD);
		if(m_joystick.getRawButton(5)) {
			boxmanipulator.processCMD(RobotEnums.BoxManipulator.RECEIVE);
		}
		if(m_joystick.getRawButton(6)) {
			boxmanipulator.processCMD(RobotEnums.BoxManipulator.DEPOSIT);
		}
		
		if (m_joystick.getRawButton(1)) {
			boxmanipulator.loopFeed(0.0f, -1);
		}
		else {
			boxmanipulator.loopFeed(0.0f, 1);
		}
		
		boxmanipulator.execute();
	}
	
	public void processDriveTrain() {
		DriveJoytoAng.feed(m_joystick.getRawAxis(RobotParamCollection.DriveTrainRotateJoystickAxis), ahrs.getAngle()); // Feed the Angle calculator before we read from it
		DriveTrain.arcadeDrive(m_joystick.getY() * -1 * RobotParamCollection.kMotorSpeedDriveTrain, DriveJoytoAng.getTurnData());

	}
	
	public void initNavXMXP() {
		try {
	          ahrs = new AHRS(SPI.Port.kMXP);
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
	      }
	}
	
	// Helper method to change to new state and reset timer so
    // states can keep track of how long they have been running.

    private void changeAutonState(int nextState) {
    	if (nextState != autonState) {
    		autonState = nextState;
    		autonStateTimer.reset();
    	}
    }
	
	public void autonomousInit() {
		// Fetch FMS switch side data
		robotFieldPos = (int) autoCmdFieldChooser.getSelected();
    	// Reset auton state to initial drive forward and reset the timer
    	autonState = AUTON_STATE_DRIVE_FORWARD;
    	autonStateTimer = new Timer();
        // Not sure if start() is required anymore, but it shouldn't hurt
        autonStateTimer.start();
	}
	
	public void autonomousPeriodic() {
    	autoDrivenRaise_1();
		//autoDrivenMovetoSwitchfromCenter();
	}
	
	public void autoDrivenRaise_1() {
switch (autonState) {
    	
    	case AUTON_STATE_DRIVE_FORWARD: {
    		// Drive forward at half power for 1.8 seconds
    		//DriveTrain.arcadeDrive(0.65f, 0.0f);
    		DriveJoytoAng.feed(0.0f, ahrs.getAngle());
    		DriveTrain.arcadeDrive(0.65f, DriveJoytoAng.getTurnData());
    		if (autonStateTimer.hasPeriodPassed(1.8)) {
    			changeAutonState(AUTON_STATE_DRIVE_STOP);
    		}
    		break;
    	}

    	case AUTON_STATE_DRIVE_STOP: {
    		// Turn off drive motors
    		DriveTrain.arcadeDrive(0.0f, 0.0f);
    		// Transition to ELevator raise state
    		if (autonStateTimer.hasPeriodPassed(.5)) {
    			changeAutonState(AUTON_STATE_ELEVATORRAISE);
    		}
    		break;
    	}

    	case AUTON_STATE_ELEVATORRAISE: {
    		elevator.loopFeed(-0.4f);
    		if (autonStateTimer.hasPeriodPassed(3.75f)) {
    			changeAutonState(AUTON_STATE_ELEVATORHOLD);
    		}
    		break;
    	}

    	case AUTON_STATE_ELEVATORHOLD: {
    		elevator.loopFeed(0.0f);
    		String gameData;
    		gameData = DriverStation.getInstance().getGameSpecificMessage();
                    if(gameData.length() > 0)
                    {
    		  switch(gameData.charAt(0)) { // This code switches based on the switch color
    		  case 'L': {
    			  if(robotFieldPos == 1) {
    				  boxmanipulator.processCMD(RobotEnums.BoxManipulator.DEPOSIT);
    			  } else {
    				  boxmanipulator.processCMD(RobotEnums.BoxManipulator.HOLD);
    			  }
    			  break;
    		  }
    		  case 'R': {
    			  if(robotFieldPos == 2) {
    				  boxmanipulator.processCMD(RobotEnums.BoxManipulator.DEPOSIT);
    			  } else {
    				  boxmanipulator.processCMD(RobotEnums.BoxManipulator.HOLD);
    			  }
    			  break;
    		  }
    		  }
                    }
    		boxmanipulator.execute();
    		if (autonStateTimer.hasPeriodPassed(4.3f)) {
    			changeAutonState(AUTON_STATE_FINISHED);
    		}
    		break;
    	}

    	case AUTON_STATE_FINISHED: {
    		// Stopped :)
    		boxmanipulator.processCMD(RobotEnums.BoxManipulator.HOLD);
    		boxmanipulator.execute();
    		break;
    	}
    	}

	}

	public void autoDrivenMovetoSwitchfromCenter() {
switch (autonState) {
    	
    	case AUTON_STATE_DRIVE_FORWARD: {
    		// Drive forward at half power for 1.8 seconds
    		//DriveTrain.arcadeDrive(0.65f, 0.0f);
    		DriveJoytoAng.feed(0.0f, ahrs.getAngle());
    		DriveTrain.arcadeDrive(0.65f, DriveJoytoAng.getTurnData());
    		if (autonStateTimer.hasPeriodPassed(0.6)) {
    			changeAutonState(AUTON_STATE_DRIVE_PICKSWITCHSIDE);
    		}
    		break;
    	}

    	
    	case AUTON_STATE_DRIVE_PICKSWITCHSIDE: {
    		String gameData;
    		gameData = DriverStation.getInstance().getGameSpecificMessage();
                    if(gameData.length() > 0)
                    {
    		  switch(gameData.charAt(0)) { // This code switches based on the switch color
    		  case 'L': {
    	    		DriveJoytoAng.overrideAngle(-80.0f);
    	    		DriveJoytoAng.autoFeed(ahrs.getAngle());
    	    		DriveTrain.arcadeDrive(0.35f, DriveJoytoAng.getTurnData() * 1.3f);
    	    		
    			  break;
    		  }
    		  case 'R': {
    			  DriveJoytoAng.overrideAngle(80.0f);
    			  DriveJoytoAng.autoFeed(ahrs.getAngle());
  	    		DriveTrain.arcadeDrive(0.35f, DriveJoytoAng.getTurnData() * 1.3f);
  	    		DriveTrain.arcadeDrive(0.25f, 0.0f);
    			  break;
    		  }
    		  }
                    }
            		if (autonStateTimer.hasPeriodPassed(2.7)) {
            			//changeAutonState(AUTON_STATE_DRIVE_STOP);
            		}
    	}
    	
    	case AUTON_STATE_DRIVE_STOP: {
    		// Turn off drive motors
    		//DriveTrain.arcadeDrive(0.0f, 0.0f);
    		// Transition to ELevator raise state
    		DriveTrain.arcadeDrive(0.65f, 0.0f);
    		if (autonStateTimer.hasPeriodPassed(.5)) {
    			changeAutonState(AUTON_STATE_ELEVATORRAISE);
    		}
    		break;
    	}

    	case AUTON_STATE_ELEVATORRAISE: {
    		DriveTrain.arcadeDrive(0.00f, 0.0f);
    		elevator.loopFeed(-0.4f);
    		if (autonStateTimer.hasPeriodPassed(3.75f)) {
    			changeAutonState(AUTON_STATE_ELEVATORHOLD);
    		}
    		break;
    	}

    	case AUTON_STATE_ELEVATORHOLD: {
    		elevator.loopFeed(0.0f);

    		boxmanipulator.execute();
    		if (autonStateTimer.hasPeriodPassed(4.3f)) {
    			changeAutonState(AUTON_STATE_FINISHED);
    		}
    		break;
    	}

    	case AUTON_STATE_FINISHED: {
    		// Stopped :)
    		boxmanipulator.processCMD(RobotEnums.BoxManipulator.HOLD);
    		boxmanipulator.execute();
    		break;
    	}
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
