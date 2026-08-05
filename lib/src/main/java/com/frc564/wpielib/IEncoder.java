package com.frc564.wpielib;

import com.pi4j.context.Context;

/**
 * IEncoder
 */
public interface IEncoder {

    @FunctionalInterface
    public interface EncoderSetPins {
        public void setPins(Context pi4j, int pinA, int pinB);
    }

    default void init(int id) {
        PIRobot.getDriverBoard().addEncoder(this::setPins, id);
    }

    public void setPins(Context pi4j, int pinA, int pinB);
    public long getDistanceTicks();
    public void reset();
}
