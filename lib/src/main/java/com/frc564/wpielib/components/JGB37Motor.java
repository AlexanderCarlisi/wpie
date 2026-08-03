package com.frc564.wpielib.components;

import com.frc564.wpielib.IDCMotor;
import com.frc564.wpielib.IEncoder;
import com.frc564.wpielib.Robot;
import com.frc564.wpielib.SlewRateLimiter;

/**
 * 
 * JGB37Motor
 * 
 * @author Alexander Carlisi
 * @see <a href="https://www.hiwonder.com/products/hall-encoder-dc-geared-motor?variant=40451123871831">Motor Reference</a>
 * @implNote Because the JGB37Motor has a stall torque of 3.2A, but the EmakefunMotorHat
 *  is rated for 3A per channel, this motor is implemented with a SlewRateLimiter to cover 
 *  some of the cases of over currenting the board.
 * @implNote For the same reason mentioned prior, the max duty cycle is limited to 75%.
 *  arguably it could be increased to around 80%, but we're playing it safe.
 * @implNote The dutycycle limit will be represented in the getPWM and getDutyCycle methods, but the
 *  slew rate limiter will not be.
 */
public class JGB37Motor implements IDCMotor {

    private final double _DUTY_CYCLE_LIMIT = 0.75;
    private final SlewRateLimiter _SRL = new SlewRateLimiter(2);

    private final int[] _PINS = new int[2];
    private IEncoder _encoder = null;

    private int _currentPwm;
    private double _currentDutyCycle;


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
        _currentPwm = Math.clamp(pwm, (int) (-255 * _DUTY_CYCLE_LIMIT), (int) (255 * _DUTY_CYCLE_LIMIT));

        // Motor needs to be SlewRateLimited since its peak is for 3.2A on a 3A rated DriverBoard
        pwm = (int) Math.round(_SRL.calculate(_currentPwm));

        Robot.getDriverBoard().setPWM(this, pwm);
    }

    @Override
    public void setDutyCycle(double dutyCycle) {
        _currentDutyCycle = Math.clamp(dutyCycle, -_DUTY_CYCLE_LIMIT, _DUTY_CYCLE_LIMIT);
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
