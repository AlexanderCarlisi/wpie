package com.frc564.wpielib;

import com.pi4j.context.Context;

public interface IDriverBoard {
    @FunctionalInterface
    public interface DCMotorSetPins {
        void setPins(int posPin, int negPin);
    }

    public IDriverBoard init(Context pi4j);

    public void addDCMotor(DCMotorSetPins dcMotorSetPins, int id);
    public void setPWM(IDCMotor motor, int pwm);
    public void setDutyCycle(IDCMotor motor, double dutyCycle);
}
