package com.github.rccookie.common.learning;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import com.github.rccookie.common.util.Console;

/**
 * A layer based neural network.
 */
public class Network implements Cloneable {
    
    final List<List<Neuron>> layers = new ArrayList<>();


    public Network(final List<Neuron> inputNeurons, final int... layerSizes) {
        layers.add(new ArrayList<>(inputNeurons));
        for (int layer = 0; layer < layerSizes.length; layer++) {
            final List<Neuron> layerNeurons = new ArrayList<>(layerSizes[layer]);

            for (int i = 0; i < layerSizes[layer]; i++) {
                layerNeurons.add(new Neuron(layers.get(layer)));
            }

            layers.add(layerNeurons);
        }

        update();
    }

    public Network update() {
        for (final List<Neuron> layer : layers)
            for (final Neuron neuron : layer)
                neuron.update();
        return this;
    }

    public Network change(final double range) {
        for (final List<Neuron> layer : layers)
            for (final Neuron neuron : layer)
                neuron.changeAll(range);
        return update();
    }

    public Network change() {
        for (final List<Neuron> layer : layers)
            for (final Neuron neuron : layer)
                neuron.changeAll();
        return update();
    }

    public double get(final int outputIndex) {
        return getOutputLayer().get(outputIndex).getValue();
    }

    public List<Neuron> getLayer(final int index) {
        return layers.get(index);
    }

    public List<Neuron> getInputLayer() {
        return getLayer(0);
    }

    public List<Neuron> getOutputLayer() {
        return getLayer(layerCount() - 1);
    }

    public Neuron getNeuron(final int layer, final int index) {
        return layers.get(layer).get(index);
    }

    public Edge getEdge(final int layer, final int neuronIndex, final int edgeIndex) {
        return layers.get(layer).get(neuronIndex).getEdges().get(edgeIndex);
    }

    public List<Neuron> getNeurons() {
        final List<Neuron> neurons = new ArrayList<>();
        for (final List<Neuron> layer : this.layers)
            neurons.addAll(layer);
        return neurons;
    }

    public List<Edge> getEdges() {
        final List<Edge> edges = new ArrayList<>();
        for (final List<Neuron> layer : layers)
            for (final Neuron neuron : layer)
                for (final Edge edge : neuron.getEdges())
                    edges.add(edge);
        return edges;
    }

    @Override
    public String toString() {
        return "Neural network(" + layerCount() + " layers, " + neuronCount() + " neurons, " + edgeCount() + " edges): "
                + super.toString();
    }

    public int layerCount() {
        return layers.size();
    }

    public int neuronCount() {
        int count = 0;
        for (final List<Neuron> layer : layers)
            count += layer.size();
        return count;
    }

    public int edgeCount() {
        int count = 0;
        for (final List<Neuron> layer : layers)
            for (final Neuron neuron : layer) {
                count += neuron.edgeCount();
            }
        return count;
    }

    public int outputCount() {
        return getOutputLayer().size();
    }

    public boolean save() {
        return save("saves");
    }

    public boolean save(final String directory) {

        //The content is not edited into the file, but first completly generated and then
        //written into the file all at once. Saves about 60% of time.

        final StringBuilder content = new StringBuilder();

        content.append("neurons: ");
        for (int i = 1; i < layerCount(); i++) {
            content.append("layer: ");
            for (final Neuron neuron : layers.get(i))
            content.append(neuron.getBias() + ", ");
        }

        content.append("edges: ");
        for (final Edge edge : getEdges()) {
            content.append(edge.getWeight() + ", ");
        }


        try {
            final Formatter file = new Formatter(directory + "\\network.nwk");

            file.format("%s", content);

            file.close();
        } catch (final Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    @Override
    protected Network clone() {
        final List<List<Double>> biases = new ArrayList<>();

        for (int currentLayer = 1; currentLayer < layers.size(); currentLayer++) {
            final List<Double> layerBiases = new ArrayList<>();
            for (final Neuron neuron : layers.get(currentLayer))
                layerBiases.add(neuron.getBias());
            biases.add(layerBiases);
        }

        final List<Double> weights = new ArrayList<>();

        for (final Edge edge : getEdges())
            weights.add(edge.getWeight());

        return new Network(getInputLayer(), biases, weights);
    }

    public Network envolve(final double range) {
        final Network envolved = clone();
        envolved.change(range);
        return envolved;
    }

    public Network envolve() {
        final Network envolved = clone();
        envolved.change();
        return envolved;
    }

    public List<Network> nextGeneration(final double range, final int generationSize) {
        final List<Network> nextGen = new ArrayList<>();
        for (int i = 0; i < generationSize; i++)
            nextGen.add(envolve(range));
        return nextGen;
    }

    public List<Network> nextGeneration(final int generationSize) {
        final List<Network> nextGen = new ArrayList<>();
        for (int i = 0; i < generationSize; i++)
            nextGen.add(envolve());
        return nextGen;
    }

    /**
     * Assumes the given neurons, biases and weights are not {@code null} and have
     * the correct size.
     * 
     * @param inputLayer
     * @param biases
     * @param weights
     * @throws IndexOutOfBoundsException If the given number of weights and biases
     *                                   don't match
     * @throws NullPointerException      If some of the input is {@code nulls}
     */
    private Network(final List<Neuron> inputLayer, final List<List<Double>> biases, final List<Double> weights)
            throws IndexOutOfBoundsException, NullPointerException {

        layers.add(inputLayer);

        // Add neurons
        for (final List<Double> layerBiases : biases) {
            final List<Neuron> layer = new ArrayList<>();

            // Add neuron with its weight to layer
            for (final double bias : layerBiases)
                layer.add(new Neuron(bias));

            layers.add(layer);
        }

        int currentWeight = 0;

        // Add edges
        for (int currentLayer = 1; currentLayer < layers.size(); currentLayer++) {

            // Add edges to each neuron in layer
            for (final Neuron neuron : layers.get(currentLayer)) {

                // Connect neuron to each neuron of the layer before
                for (final Neuron neuronBefore : layers.get(currentLayer - 1)) {
                    neuron.addBefore(new Edge(neuronBefore, weights.get(currentWeight++)));
                }

            }
        }

        update();
    }

    public static Network load(final List<Neuron> inputLayer) {
        return load("saves\\network.nwk", inputLayer);
    }

    public static Network load(final String filepath, final List<Neuron> inputLayer) {
        final File file = new File(filepath);

        try {
            final Scanner scanner = new Scanner(file);

            // Neurons
            final List<List<Double>> biases = new ArrayList<>();
            while (scanner.hasNext()) {
                final StringBuilder current = new StringBuilder(scanner.next());
                current.deleteCharAt(current.length() - 1);

                if ("neurons".equals(current.toString()))
                    continue;
                if ("edges".equals(current.toString()))
                    break;

                if ("layer".equals(current.toString())) {
                    biases.add(new ArrayList<Double>());
                    continue;
                }

                biases.get(biases.size() - 1).add(Double.parseDouble(current.toString()));
            }

            // Edges
            final List<Double> weights = new ArrayList<>();
            while (scanner.hasNext()) {
                final StringBuilder current = new StringBuilder(scanner.next());
                current.deleteCharAt(current.length() - 1);

                if ("edges".equals(current.toString()))
                    continue;

                weights.add(Double.parseDouble(current.toString()));
            }

            scanner.close();

            return new Network(inputLayer, biases, weights);
        } catch (final FileNotFoundException e) {
            System.out.println(e);
        }
        return null;
    }

    public static void main(final String[] args) {
        final ArrayList<Neuron> in = new ArrayList<>();
        for (int j = 0; j < 16; j++)
            in.add(new StaticNeuron(Math.random()));

        Network best = new Network(in, 16, 16, 1);
        double target = 1;
        double bestResult = best.get(0);

        for(int i=0; bestResult != target; i++) {
            Network evo = best.clone().change();
            double result = evo.get(0);
            if(Math.abs(result - target) < Math.abs(bestResult - target)) {
                best = evo;
                bestResult = result;
                Console.map(i, bestResult);
            }
        }
    }
}
