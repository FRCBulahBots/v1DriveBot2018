package org.usfirst.frc.team3753.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;

public class BoxManipulator {
	private Spark rightSideMotors;
	private Spark leftSideMotors;
	private double motorSpeed;
	private int motordir;
	private DoubleSolenoid armSolenoid;
	public BoxManipulator(int rightsidemotorport, int leftsidemotorport, int inactuatorport, int outactuatorport, double speed) {
		rightSideMotors = new Spark(rightsidemotorport);
		leftSideMotors = new Spark(leftsidemotorport);
		armSolenoid = new DoubleSolenoid(inactuatorport, outactuatorport);
		motorSpeed = speed;
	}
	
	public void overrideSpeed(double speed) {
		motorSpeed = speed;
	}
	
	public void loopFeed(double joyval, int actuatorpos) {
//		if(joyval > 0.2f) {
//			processCMD(RobotEnums.BoxManipulator.RECEIVE);
//		} else if (joyval < 0.2f && joyval > -0.2f) {
//			processCMD(RobotEnums.BoxManipulator.HOLD);
//		} else {
//			processCMD(RobotEnums.BoxManipulator.DEPOSIT);
//		}
		switch(actuatorpos) {
		case -1:
			armSolenoid.set(DoubleSolenoid.Value.kReverse);
			break;
		case 0:
			armSolenoid.set(DoubleSolenoid.Value.kOff);
			break;
		case 1:
			armSolenoid.set(DoubleSolenoid.Value.kForward);
			break;
		}
	}
	
	public void execute() {
		driveMotors(motordir);
	}
	
	private void driveMotors(int dir) {
		rightSideMotors.set(dir * motorSpeed * -1);
		leftSideMotors.set(dir * motorSpeed);
	}
	
	public void processCMD(RobotEnums.BoxManipulator cmd) {	
		switch (cmd){
			case RECEIVE:
				motordir = 1;
				break;
			case HOLD:
				motordir = 0;
				break;
			case DEPOSIT:
				motordir = -1;
				break;
		}
	}
}
