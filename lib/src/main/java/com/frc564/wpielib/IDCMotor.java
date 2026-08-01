package com.frc564.wpielib;

/**
 * 
 * IDCMotor
 * 
 * @author Alexander Carlisi
 */
public interface IDCMotor {
    
    default void init(int id) {

    }

    public void setPins(int posPin, int negPin);
    public int[] getPins();

    public void attachEncoder(IEncoder encoder);
    public IEncoder getEncoder();

    public void setPWM(int channel, int on, int off);

    /**
     * @param dutyCycle [-100%, 100%] +ccw, -cw
     */
    public void setDutyCycle(double dutyCycle);
}
