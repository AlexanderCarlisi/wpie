package com.frc564.wpielib;

import com.pi4j.context.Context;

public class Robot {
    @FunctionalInterface
    public interface InitDriverBoard {
        IDriverBoard init(Context pi4j);
    }

    private static Robot s_robot;

    private final IDriverBoard _DRIVER_BOARD;

    public Robot(Context pi4j, InitDriverBoard initDriverBoard) {
        _DRIVER_BOARD = initDriverBoard.init(pi4j);
    }

    public static IDriverBoard getDriverBoard() {
        return s_robot._DRIVER_BOARD;
    }
}