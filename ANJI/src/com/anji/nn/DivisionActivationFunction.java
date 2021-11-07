package com.anji.nn;

public class DivisionActivationFunction implements ActivationFunction{


    public final static String NAME = ActivationFunctionType.DIVISION.toString();

    @Override
    public double apply(double input) {
        return input;
    }

    @Override
    public double getMaxValue() {
        return 0;
    }

    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public long cost() {
        return 69;
    }
}
