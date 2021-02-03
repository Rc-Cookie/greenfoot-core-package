package com.github.rccookie.common.learning;

public class StaticEdge extends Edge {
    public StaticEdge(Neuron before, double weight) {
        super(before, weight);
    }
    public StaticEdge(Neuron before) {
        super(before);
    }


    @Override
    public void change(double range) {}
}
