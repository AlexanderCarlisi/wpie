package com.frc564.wpielib;

public interface IDriverBoard {
    @FunctionalInterface
    public interface DCMotorSetPins {
        void setPins(int posPin, int negPin);
    }

    public void addDCMotor(DCMotorSetPins dcMotorSetPins, int id);
}
