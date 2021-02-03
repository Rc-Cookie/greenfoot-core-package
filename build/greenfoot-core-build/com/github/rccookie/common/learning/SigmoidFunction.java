package com.github.rccookie.common.learning;

public class SigmoidFunction implements NeuronFunction {
    @Override
    public double calculate(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
