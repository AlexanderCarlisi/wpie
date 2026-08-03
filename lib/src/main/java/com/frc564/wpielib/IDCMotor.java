package com.frc564.wpielib;

/**
 * 
 * IDCMotor
 * 
 * @author Alexander Carlisi
 */
public interface IDCMotor {
    
    default void init(int id) {
        Robot.getDriverBoard().addDCMotor(this::setPins, id);
    }
    default void init(int id, boolean invert) {
        Robot.getDriverBoard().addDCMotor(this::setPins, id);
        if (invert) invert();
    }

    public void setPins(int posPin, int negPin);
    public int[] getPins();

    public void attachEncoder(IEncoder encoder);
    public IEncoder getEncoder();

    /**
     * @param dutyCycle [-100%, 100%] +ccw, -cw
     */
    public void setDutyCycle(double dutyCycle);

    void invert();

    void setPWM(int pwm);

	int getPWM();

    double getDutyCycle();
}
