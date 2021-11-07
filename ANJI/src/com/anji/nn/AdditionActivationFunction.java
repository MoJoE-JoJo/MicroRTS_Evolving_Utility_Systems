package com.anji.nn;

public class AdditionActivationFunction implements ActivationFunction {


    public final static String NAME = ActivationFunctionType.ADDITION.toString();

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
        return 42;
    }
}
