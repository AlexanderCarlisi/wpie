package com.frc564.wpielib;

import com.pi4j.context.Context;

/**
 * 
 * PIRobot
 */
public class PIRobot {
    private static PIRobot s_robot;

    private final Context _PI4J;
    private final IDriverBoard _DRIVER_BOARD;

    private PIRobot(Context pi4j, IDriverBoard driverBoard) {
        _PI4J = pi4j;
        _DRIVER_BOARD = driverBoard;
    }

    public static void setup(Context pi4j, IDriverBoard driverBoard) {
        s_robot = new PIRobot(pi4j, driverBoard);
    }

    public static IDriverBoard getDriverBoard() {
        return s_robot._DRIVER_BOARD;
    }

    public static Context getPi4JContext() {
        return s_robot._PI4J;
    }
}