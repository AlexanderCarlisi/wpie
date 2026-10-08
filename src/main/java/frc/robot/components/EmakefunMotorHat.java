package frc.robot.components;

import java.util.ArrayList;

import frc.robot.IDCMotor;
import frc.robot.IDriverBoard;
import frc.robot.IEncoder;
import frc.robot.IServo;
import frc.robot.IEncoder.EncoderSetPins;
import frc.robot.IServo.ServoSetPin;

/**
 * EmakefunMotorHat
 *
 * @author Alexander Carlisi
 * @see <a href="https://github.com/DFRobotdl/RaspberryPi-MotorDriveBoard">Emakefun Motor Drive Board Reference</a>
 * @see <a href="https://github.com/DFRobotdl/RaspberryPi-MotorDriveBoard/blob/master/schematic/RaspBerryDriverBoard.pdf">PCB Schematic</a>
 * @implNote Stepper Motors are unimplemented, because they're not on the parts sheet :)
 */
public class EmakefunMotorHat implements IDriverBoard {

    private static final int _MAX_DC_MOTORS = 4;
    private static final int _MAX_SERVOS = 8;
    private static final int _MAX_ENCODERS = 4;
    // private static final int MAX_STEPPER_MOTORS = 2;

    private static final ArrayList<Integer> _DC_MOTOR_IDS = new ArrayList<>();
    private static final ArrayList<Integer> _ENCODER_IDS = new ArrayList<>();
    private static final ArrayList<Integer> _SERVO_IDS_USED = new ArrayList<>();
    private static final int[] _SERVO_IDS = {8, 9, 10, 11, 12, 13, 14, 15};

    private static boolean s_initialized = false;
    private PCA9685 _pca9685; // Fixed typo from _pca9865

    private static final ArrayList<IEncoder> _ENCODER_REFERENCES = new ArrayList<>();

    public EmakefunMotorHat() {
        if (s_initialized) throw new IllegalStateException("Only one EmakefunMotorHat can be initialized.");
        s_initialized = true;
        _pca9685 = new PCA9685(1, 0x60);
        _pca9685.setPWMFreq(50); // 50Hz when using Servos, but can do 1kHz if only Motors
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
            case 0 -> dcMotorSetPins.setPins(0, 1);
            case 1 -> dcMotorSetPins.setPins(3, 2);
            case 2 -> dcMotorSetPins.setPins(4, 5);
            case 3 -> dcMotorSetPins.setPins(7, 6);
            default -> throw new IllegalArgumentException("DCMotor Id must be between 0 and 3");
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
            case 0 -> encoderSetPins.setPins(5, 6);
            case 1 -> encoderSetPins.setPins(13, 19);
            case 2 -> encoderSetPins.setPins(20, 21);
            case 3 -> encoderSetPins.setPins(16, 26);
            default -> throw new IllegalArgumentException("Encoder Id must be between 0 and 3");
        }
    }

    @Override
    public void addServo(ServoSetPin servoSetPin, int id) {
        if (_SERVO_IDS_USED.contains(id))
            throw new IllegalArgumentException("Cannot initialize Servo with same Id multiple times.");
        else if (_SERVO_IDS_USED.size() >= _MAX_SERVOS)
            throw new IllegalStateException("Cannot instantiate more than 8 Servos."); // Updated to match max
        else if (id >= _SERVO_IDS.length || id < 0) // Fixed out-of-bounds check
            throw new IllegalArgumentException("Servo Id must be between 0 and 7");
        else
            _SERVO_IDS_USED.add(id);

        servoSetPin.setPin(_SERVO_IDS[id]);
    }

    private void setPin(int pin, boolean high) {
        if (high) _pca9685.setPWM(pin, 4096, 0);
        else _pca9685.setPWM(pin, 0, 4096);
    }

    private void setPWM(int pin, int value) {
        if (value > 4095) _pca9685.setPWM(pin, 4096, 0);
        else _pca9685.setPWM(pin, 0, value);
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
        dutyCycle = Math.max(Math.min(dutyCycle, 1), -1);
        int pwm = (int) Math.round(dutyCycle * 255);
        setPWM(motor, pwm);
    }

    @Override
    public void setAngle(IServo servo, double angleDegrees) {
        // Clamp angle between 0 and 180 degrees for safety
        angleDegrees = Math.max(0, Math.min(180, angleDegrees));

        // Map 0-180 degrees to approximately 1.0ms (205 ticks) to 2.0ms (410 ticks) pulse widths
        int pulseTicks = (int) Math.round(205 + (angleDegrees / 180.0) * (410 - 205));

        setPWM(servo.getPin(), pulseTicks);
    }

    @Override
    public void addEncoderTest(IEncoder encoder) {
        _ENCODER_REFERENCES.add(encoder);
    }

    @Override
    public void update() {
        for (IEncoder encoder : _ENCODER_REFERENCES) {
            encoder.update();
        }
    }
}
