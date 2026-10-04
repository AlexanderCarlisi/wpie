package frc.robot;

/**
 * 
 * SlewRateLimiter
 */
public class SlewRateLimiter {
    private final double _MAX_RATE_CHANGE_PER_SECOND;
    private double _lastOutput = 0.0;
    private long _lastTimeNs = System.nanoTime();

    /**
     * 
     * @param maxRateOfChangePerSec 2.0 means 0% to 100% takes 0.5s
     */
    public SlewRateLimiter(double maxRateOfChangePerSec) {
        _MAX_RATE_CHANGE_PER_SECOND = maxRateOfChangePerSec;
    }

    public double calculate(double targetOutput) {
        long now = System.nanoTime();
        double dt = (now - _lastTimeNs) / 1e9; // convert ns to seconds
        _lastTimeNs = now;

        double maxChange = _MAX_RATE_CHANGE_PER_SECOND * dt;
        double change = targetOutput - _lastOutput;

        // Clamp change within allowed rate
        change = Math.max(-maxChange, Math.min(maxChange, change));
        
        _lastOutput += change;
        return _lastOutput;
    }
}