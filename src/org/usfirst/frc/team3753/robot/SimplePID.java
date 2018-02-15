package org.usfirst.frc.team3753.robot;

public class SimplePID { // A really simple class to well... Implement PID control
	private double P, I, D;
    private double integral, derivative, prev_error, setpoint = 0.0f;
	
	
	public SimplePID(double vP, double vI, double vD) {
		P = vP;
		I= vI;
		D = vD;
	}
	
	public void setP(int val)
    {
        P = val;
    }
	
	public void setI(int val)
    {
        I = val;
    }
	
	public void setD(int val)
    {
        D = val;
    }
	
	public double calcPIDfromTargetandInput(double target, double input) {
        double error = target - input; // Error = Target - Actual
        integral += (error * 0.02f); // Integral is increased by the error*time (which is .02 seconds at normal DS telopPeriodic frequency)
        derivative = (error - prev_error) / 0.02f;
        return (P*error + I*this.integral + D*derivative);
	}
}
