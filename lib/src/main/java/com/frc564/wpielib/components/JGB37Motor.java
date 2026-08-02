package com.frc564.wpielib.components;

import com.frc564.wpielib.IDCMotor;
import com.frc564.wpielib.IEncoder;

public class JGB37Motor implements IDCMotor {

    @Override
    public void setPins(int posPin, int negPin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPins'");
    }

    @Override
    public int[] getPins() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPins'");
    }

    @Override
    public void attachEncoder(IEncoder encoder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'attachEncoder'");
    }

    @Override
    public IEncoder getEncoder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEncoder'");
    }

    @Override
    public void setPWM(int channel, int on, int off) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPWM'");
    }

    @Override
    public void setDutyCycle(double dutyCycle) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDutyCycle'");
    }
    
}
