package com.frc564.wpielib.components;

import java.util.ArrayList;

import com.frc564.wpielib.IDCMotor;
import com.frc564.wpielib.IDriverBoard;
import com.frc564.wpielib.IEncoder.EncoderSetPins;
import com.pi4j.context.Context;

/**
 * 
 * EmakefunMotorHat
 * 
 * @author Alexander Carlisi
 * @see <a href="https://github.com/DFRobotdl/RaspberryPi-MotorDriveBoard">Emakefun Motor Drive Board Reference</a>
 * @see <a href="https://github.com/DFRobotdl/RaspberryPi-MotorDriveBoard/blob/master/schematic/RaspBerryDriverBoard.pdf">PCB Schematic</a>
 * @implNote Stepper Motors are unimplemented, because they're not on the parts sheet :)
 */
public class EmakefunMotorHat implements IDriverBoard {

    private static final int _MAX_DC_MOTORS = 4;
    private static final int _MAX_SERVOS = 6;
    private static final int _MAX_ENCODERS = 4;
    // private static final int MAX_STEPPER_MOTORS = 2;

    private static final ArrayList<Integer> _DC_MOTOR_IDS = new ArrayList<>();
    private static final ArrayList<Integer> _ENCODER_IDS = new ArrayList<>();

    private static boolean s_initialized = false;
    private final Context _PI4J;
    private PCA9685 _pca9865;

    public EmakefunMotorHat(Context pi4j) {
        if (s_initialized) throw new IllegalStateException("Only one EmakefunMotorHat can be initialized.");
        s_initialized = true;
        _pca9865 = new PCA9685(pi4j, 1, 0x60);
        _PI4J = pi4j;
    }

    @Override
    public void addDCMotor(DCMotorSetPins dcMotorSetPins, int id) {
        if (_DC_MOTOR_IDS.contains(id)) 
            throw new IllegalArgumentException("Cannot initialize DCMotor with same Id multiple times.");
        else if (_DC_MOTOR_IDS.size() >= _MAX_DC_MOTORS)
            throw new IllegalStateException("Cannot instantiate more than 4 DCMotors.");
        else 
            _DC_MOTOR_IDS.add(id);

        switch(id) {
            case 0: {
                dcMotorSetPins.setPins(0, 1);
                break;
            }
            case 1: {
                dcMotorSetPins.setPins(3, 2);
                break;
            }
            case 2: { 
                dcMotorSetPins.setPins(4, 5);
                break;
            }
            case 3: { 
                dcMotorSetPins.setPins(7, 6);
                break;
            }
            default: 
                throw new IllegalArgumentException("DCMotor Id must be between 0 and 3");
        }
    }

    @Override
    public void addEncoder(EncoderSetPins encoderSetPins, int id) {
        if (_ENCODER_IDS.contains(id))
            throw new IllegalArgumentException("Cannot initialize Encoder with same Id multiple times.");
        else if (_ENCODER_IDS.size() >= _MAX_ENCODERS)
            throw new IllegalStateException("Cannot instantiate more than 4 Encoders.");
        else 
            _ENCODER_IDS.add(id);

        switch(id) {
            case 0: { 
                encoderSetPins.setPins(_PI4J, 5, 6);
                break;
            }
            case 1: {
                encoderSetPins.setPins(_PI4J, 13, 19);
                break;
            }
            case 2: {
                encoderSetPins.setPins(_PI4J, 20, 21);
                break;
            }
            case 3: {
                encoderSetPins.setPins(_PI4J, 16, 26);
                break;
            }
            default: 
                throw new IllegalArgumentException("Encoder Id must be between 0 and 3");
        }
    }

    private void setPin(int pin, boolean high) {
        if (high) _pca9865.setPWM(pin, 4096, 0);
        else _pca9865.setPWM(pin, 0, 4096);
    }

    private void setPWM(int pin, int value) {
        if (value > 4095) _pca9865.setPWM(pin, 4096, 0);
        else _pca9865.setPWM(pin, 0, value);
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
     * @param dutyCycle [-1,1] +ccw/-cw
     */
    @Override
    public void setDutyCycle(IDCMotor motor, double dutyCycle) {
        dutyCycle = Math.clamp(dutyCycle, -1, 1);
        int pwm = (int) Math.round(dutyCycle * 255);
        setPWM(motor, pwm);
    }
}
