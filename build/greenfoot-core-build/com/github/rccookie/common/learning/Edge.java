package com.github.rccookie.common.learning;

public class Edge {

    public static final double MAX_WEIGHT = 1;
    public static double MIN_WEIGHT = -MAX_WEIGHT;

    public static double CHANGE_RATE = .1;

    private double weight;
    private final Neuron before;

    public Edge(Neuron before) {
        this(before, Math.random() * (MAX_WEIGHT - MIN_WEIGHT) + MIN_WEIGHT);
    }
    public Edge(Neuron before, double weight) {
        this.before = before;
        this.weight = weight;
    }

    public void change(double range) {
        weight += (Math.random() - 0.5) * (MAX_WEIGHT - MIN_WEIGHT) * range;
        if(weight < MIN_WEIGHT) weight = MIN_WEIGHT;
        else if(weight > MAX_WEIGHT) weight = MAX_WEIGHT;
    }
    public void change() {
        change(CHANGE_RATE);
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getValue() {
        return before.getValue() * weight;
    }

    public double getWeight() {
        return weight;
    }

    public Neuron getNeuronBefore() {
        return before;
    }
}
