package frc.robot;

import frc.robot.IEncoder.EncoderSetPins;
import frc.robot.IServo.ServoSetPin;

/**
 * 
 * IDriverBoard
 */
public interface IDriverBoard {
    @FunctionalInterface
    public interface DCMotorSetPins {
        void setPins(int posPin, int negPin);
    }

    // public void init(Context pi4j);
    public void addDCMotor(DCMotorSetPins dcMotorSetPins, int id);
    public void setPWM(IDCMotor motor, int pwm);
    public void setDutyCycle(IDCMotor motor, double dutyCycle);
    public void addEncoder(EncoderSetPins encoderSetPins, int id);
    public void addServo(ServoSetPin servoSetPin, int id);
}
