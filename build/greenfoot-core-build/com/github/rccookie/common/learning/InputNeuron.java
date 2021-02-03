package com.github.rccookie.common.learning;

public abstract class InputNeuron extends Neuron implements Cloneable {

    public InputNeuron() {}


    @Override
    public void change(double range) {}

    @Override
    public double getValue() {
        return getInput();
    }

    @Override
    public abstract double getInput();

    @Override
    public double getBias() {
        return 0;
    }
}
