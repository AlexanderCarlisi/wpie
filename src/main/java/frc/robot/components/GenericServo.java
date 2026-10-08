package frc.robot.components;

import frc.robot.IServo;
import frc.robot.PIRobot;

public class GenericServo implements IServo {
    private int _pin;
    private double _angleDegrees;

    public GenericServo(int id) {
        init(id);
    }

    @Override
    public void setPin(int pin) {
        _pin = pin;
    }

    @Override
    public void setAngle(double degrees) {
        _angleDegrees = degrees;
        PIRobot.getDriverBoard().setAngle(this, degrees);
    }

    @Override
    public double getAngleDegrees() {
        return _angleDegrees;
    }

    @Override
    public int getPin() {
        return _pin;
    }
}
