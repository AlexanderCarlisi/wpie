package com.frc564.wpielib.components;

import java.util.ArrayList;

import com.frc564.wpielib.IDCMotor;
import com.frc564.wpielib.IDriverBoard;
import com.pi4j.context.Context;

public class EmakefunMotorHat implements IDriverBoard {
    private static final int MAX_DC_MOTORS = 4;
    private static final int MAX_SERVOS = 6;
    private static final int MAX_STEPPER_MOTORS = 2;

    // Only one should exist
    private static boolean s_initialized = false;
    private static ArrayList<Integer> s_dcMotorIds = new ArrayList<>();

    private final PCA9685 _PCA9685;

    private EmakefunMotorHat(Context pi4j) {
        _PCA9685 = new PCA9685(pi4j, 1, 0x60);
    }

    @Override
    public EmakefunMotorHat init(Context pi4j) {
        if (s_initialized) throw new IllegalStateException("Only one EmakefunMotorHat can be initialized.");
        s_initialized = true;
        return new EmakefunMotorHat(pi4j);
    }

    @Override
    public void addDCMotor(DCMotorSetPins dcMotorSetPins, int id) {
        if (s_dcMotorIds.contains(id)) 
            throw new IllegalArgumentException("Cannot initialize DCMotor with same Id multiple times.");
        else 
            s_dcMotorIds.add(id);

        switch(id) {
            case 0: dcMotorSetPins.setPins(0, 1);
            case 1: dcMotorSetPins.setPins(3, 2);
            case 2: dcMotorSetPins.setPins(4, 5);
            case 3: dcMotorSetPins.setPins(7, 6);
            default: 
                throw new IllegalArgumentException("DCMotor Id must be between 0 and 3");
        }
    }

    private void setPin(int pin, boolean high) {
        if (high) _PCA9685.setPWM(pin, 4096, 0);
        else _PCA9685.setPWM(pin, 0, 4096);
    }

    private void setPWM(int pin, int value) {
        if (value > 4095) _PCA9685.setPWM(pin, 4096, 0);
        else _PCA9685.setPWM(pin, 0, value);
    }

    /**
     * Sets raw 12-bit PWM ON/OFF values (0 to 4095) for a specific channel.
     * 
     * @param id MotorId
     * @param pwm [-255, 255], +ccw/-cw
     */
    @Override
    public void setPWM(IDCMotor motor, int pwm) {
        pwm *= 16; // match resolution
        if (pwm > 0) {
            setPin(motor.getPins()[1], false);
            setPWM(motor.getPins()[0], Math.abs(pwm));
        } else if (pwm < 0) {
            setPin(motor.getPins()[0], false);
            setPWM(motor.getPins()[1], Math.abs(pwm));
        } else {
            setPin(motor.getPins()[0], false);
            setPin(motor.getPins()[1], false);
        }
    }

    /**
     * Set PWM as a duty cycle
     * 
     * @param channel PWM Channel
     * @param dutyCycle 
     */
    @Override
    public void setDutyCycle(IDCMotor motor, double dutyCycle) {
        
    }
}
