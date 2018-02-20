package org.usfirst.frc.team3753.robot;
import edu.wpi.first.wpilibj.Spark;

public class BoxManipulator {
	private Spark rightSideMotors;
	private Spark leftSideMotors;
	private double motorSpeed;
	private int motordir;
	
	public BoxManipulator(int rightsidemotorport, int leftsidemotorport, double speed) {
		rightSideMotors = new Spark(rightsidemotorport);
		leftSideMotors = new Spark(leftsidemotorport);
		motorSpeed = speed;
	}
	
	public void overrideSpeed(double speed) {
		motorSpeed = speed;
	}
	
	public void loopFeed(double joyval) {
		if(joyval > 0.2f) {
			processCMD(RobotEnums.BoxManipulator.RECEIVE);
		} else if (joyval < 0.2f && joyval > -0.2f) {
			processCMD(RobotEnums.BoxManipulator.HOLD);
		} else {
			processCMD(RobotEnums.BoxManipulator.DEPOSIT);
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
