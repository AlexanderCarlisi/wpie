package frc.robot;

public interface IServo {
    @FunctionalInterface
    public interface ServoSetPin {
        public void setPin(int pin);
    }

    default void init(int id) {
        PIRobot.getDriverBoard().addServo(this::setPin, id);
    }

    public void setPin(int pin);
    public void setAngle(double radians);
    public void setAngle(double radians, double radiansPerSecond);
    public double getAngle();
}
