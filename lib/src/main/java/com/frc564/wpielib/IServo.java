package com.frc564.wpielib;

import com.pi4j.context.Context;

public interface IServo {
    @FunctionalInterface
    public interface ServoSetPin {
        public void setPin(Context pi4j, int pin);
    }

    default void init(int id) {
        PIRobot.getDriverBoard().addServo(this::setPin, id);
    }

    public void setPin(Context pi4j, int pin);
    public void setAngle(double radians);
    public void setAngle(double radians, double radiansPerSecond);
    public double getAngle();
}
