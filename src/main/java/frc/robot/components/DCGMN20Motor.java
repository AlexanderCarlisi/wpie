package frc.robot.components;

import frc.robot.IDCMotor;
import frc.robot.IEncoder;
import frc.robot.PIRobot;


/**
 * 
 * DCGMN20Motor
 */
public class DCGMN20Motor implements IDCMotor {

    private final double _DUTY_CYCLE_LIMIT = 1;

    private final int[] _PINS = new int[2];
    private IEncoder _encoder = null;

    private int _currentPwm;
    private double _currentDutyCycle;

    
    public DCGMN20Motor(int id) {
        init(id);
    }
    public DCGMN20Motor(int id, boolean invert) {
        init(id, invert);
    }


    @Override
    public void setPins(int posPin, int negPin) {
        _PINS[0] = posPin;
        _PINS[1] = negPin;
    }

    @Override
    public int[] getPins() {
        return _PINS;
    }

    @Override
    public void attachEncoder(IEncoder encoder) {
        _encoder = encoder;
    }

    @Override
    public IEncoder getEncoder() {
        return _encoder;
    }

    @Override
    public void invert() {
        int pin0 = _PINS[0];
        _PINS[0] = _PINS[1];
        _PINS[1] = pin0;
    }

    @Override
    public void setPWM(int pwm) {
        _currentPwm = Math.max(Math.min(pwm, (int) (255 * _DUTY_CYCLE_LIMIT)), (int) (-255 * _DUTY_CYCLE_LIMIT));

        PIRobot.getDriverBoard().setPWM(this, _currentPwm);
    }

    @Override
    public void setDutyCycle(double dutyCycle) {
        _currentDutyCycle = Math.max(Math.min(dutyCycle, _DUTY_CYCLE_LIMIT), -_DUTY_CYCLE_LIMIT);
        setPWM((int) Math.round((dutyCycle * 255)));
    }

    @Override
    public int getPWM() {
        return _currentPwm;
    }

    @Override
    public double getDutyCycle() {
        return _currentDutyCycle;
    }
}
