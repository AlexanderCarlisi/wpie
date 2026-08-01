package com.frc564.wpielib;

public class Robot {
    @FunctionalInterface
    public interface InitDriverBoard {
        IDriverBoard init();
    }

    private static Robot s_robot;

    private final IDriverBoard _DRIVER_BOARD;

    public Robot(InitDriverBoard initDriverBoard) {
        _DRIVER_BOARD = initDriverBoard.init();
    }

    public IDriverBoard getDriverBoard() {
        return _DRIVER_BOARD;
    }
}
