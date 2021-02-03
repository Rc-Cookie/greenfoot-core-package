package com.github.rccookie.common.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Neuron {

    public static final NeuronFunction DEF_FUNCTION = new SigmoidFunction();

    public static int MAX_BIAS = 1;
    public static int MIN_BIAS = -MAX_BIAS;

    public static double CHANGE_RATE = .1;

    private double bias;
    private final HashMap<Edge, Double> before = new HashMap<Edge, Double>();
    private final NeuronFunction function;

    public Neuron() {
        this(Math.random() * (MAX_BIAS - MIN_BIAS) + MIN_BIAS, DEF_FUNCTION, null);
    }
    public Neuron(List<Neuron> neuronsBefore) {
        this(Math.random() * (MAX_BIAS - MIN_BIAS) + MIN_BIAS, DEF_FUNCTION, neuronsBefore);
    }
    public Neuron(double bias) {
        this(bias, DEF_FUNCTION, null);
    }
    public Neuron(NeuronFunction function) {
        this(Math.random() * (MAX_BIAS - MIN_BIAS) + MIN_BIAS, function, null);
    }
    public Neuron(double bias, NeuronFunction function, List<Neuron> neuronsBefore) {
        this.bias = bias;
        this.function = function;
        addBeforeNeurons(neuronsBefore);
    }


    public void addBefore(Edge edge) {
        if(edge == null) return;
        before.put(edge, edge.getValue());
    }
    public void addBefore(List<Edge> edges) {
        if(edges == null) return;
        for(Edge edge : edges) addBefore(edge);
    }

    public void addBeforeNeuron(Neuron neuron) {
        if(neuron == null) return;
        addBefore(new Edge(neuron));
    }
    public void addBeforeNeurons(List<Neuron> neurons) {
        if(neurons == null) return;
        for(Neuron neuron : neurons) addBeforeNeuron(neuron);
    }


    public void change(double range) {
        setBias(bias + (Math.random() - 0.5) * (MAX_BIAS - MIN_BIAS) * range);
    }
    public void change() {
        change(CHANGE_RATE);
    }

    public void changeEdges(double range) {
        for(Edge edge : before.keySet()) edge.change(range);
    }
    public void changeEdges() {
        for(Edge edge : before.keySet()) edge.change();
    }

    public void changeAll(double range) {
        change(range);
        changeEdges(range);
        update();
    }
    public void changeAll() {
        change();
        changeEdges();
        update();
    }

    public void setBias(double bias) {
        this.bias = validBias(bias);
    }

    public double getValue() {
        return function.calculate(getInput() + bias);
    }
    public double getInput() {
        double average = 0;
        for(double value : before.values()) average += value;
        return average / before.size();
    }
    public List<Edge> getEdges() {
        return new ArrayList<Edge>(before.keySet());
    }
    public List<Neuron> getNeuronsBefore() {
        ArrayList<Neuron> connected = new ArrayList<>(edgeCount());
        for(Edge edge : before.keySet()) connected.add(edge.getNeuronBefore());
        return connected;
    }
    public int edgeCount() {
        return before.size();
    }
    public double getBias() {
        return bias;
    }

    public void update() {
        for(Edge edge : before.keySet()) addBefore(edge);
    }





    






    static double validBias(double bias) {
        if(bias < MIN_BIAS) return MIN_BIAS;
        if(bias > MAX_BIAS) return MAX_BIAS;
        return Double.isNaN(bias) ? 0 : bias;
    }



    public static void main(String[] args) {
        
    }
}
