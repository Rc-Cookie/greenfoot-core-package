package com.github.rccookie.common.learning;

public class StaticNeuron extends InputNeuron {

    public static final double DEF_FALUE = 1;

    public double value;

    public StaticNeuron() {
        this(DEF_FALUE);
    }
    public StaticNeuron(double value) {
        this.value = value;
    }

    @Override
    public double getInput() {
        return value;
    }
}
