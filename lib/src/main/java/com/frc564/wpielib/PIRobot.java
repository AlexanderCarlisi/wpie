package com.frc564.wpielib;

import com.pi4j.context.Context;

/**
 * 
 * PIRobot
 */
public class PIRobot {
    private static PIRobot s_robot;

    private Context _pi4j;
    private IDriverBoard _driverBoard;

    private PIRobot(Context pi4j, IDriverBoard driverBoard) {
        _pi4j = pi4j;
        _driverBoard = driverBoard;
    }

    public static void setup(Context pi4j, IDriverBoard driverBoard) {
        // System.err.println("\n\n\nSETTING UP!\n\n\n");
        s_robot = new PIRobot(pi4j, driverBoard);
        // System.err.println("\n\n\nSETTING UP!\n\n\n");
    }

    public static IDriverBoard getDriverBoard() {
        return s_robot._driverBoard;
    }
}