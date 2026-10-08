package frc.robot;

/**
 * IEncoder
 */
public interface IEncoder {

    @FunctionalInterface
    public interface EncoderSetPins {
        public void setPins(int pinA, int pinB);
    }

    default void init(int id) {
        PIRobot.getDriverBoard().addEncoder(this::setPins, id);
        // PIRobot.getDriverBoard().addEncoderTest(this);
    }

    public void setPins(int pinA, int pinB);
    public long getDistanceTicks();
    public void reset();


    // public void update();
}
