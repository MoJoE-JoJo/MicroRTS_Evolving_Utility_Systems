package com.anji.nn.addons;

import com.anji.nn.ActivationFunction;
import com.anji.nn.ActivationFunctionType;

public class MaxActivationFunction implements ActivationFunction {

    public final static String NAME = ActivationFunctionType.MAX.toString();

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
