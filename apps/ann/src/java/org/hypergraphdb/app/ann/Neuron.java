package org.hypergraphdb.app.ann;

import java.util.Map;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * <p>
 * Represents a neuron in an artificial neural network. The inputs of
 * the neuron are its target set. The weights of the links between inputs
 * and the neuron are stored locally in a <code>double[]</code> which has
 * the same length as the target set.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class Neuron extends HGPlainLink
{
    private double [] weights;

    public Neuron(HGHandle...inputs)
    {
        super(inputs);
    }
    
    /**
     * Activate the neuron using the given activation function and return its activation level.
     */
    public double fire(Map<HGHandle, Double> inputs, ActivationFunction f)
    {
        double activation = 0.0;
        for (int i = 0; i < getArity(); i++)
            activation += inputs.get(getTargetAt(i))*weights[i]; 
        return f.eval(activation);
    }
    
    /**
     * Return the index, within the target set, of a particular input to this
     * neuron.
     */
    public int findInput(HGHandle in)
    {
        for (int i = 0; i < outgoingSet.length; i++)
            if (outgoingSet[i].equals(in))
                return i;
        return -1;
    }

    /**
     * Self-explanatory. The new input is appended as the last element of 
     * this neuron's target set.
     */
    public void addInput(HGHandle in, double weight)
    {
        if (weights == null) 
            weights = new double[0];
        HGHandle [] newTargets = new HGHandle[outgoingSet.length + 1];
        double [] newWeights = new double[weights.length + 1]; 
        System.arraycopy(outgoingSet, 0, newTargets, 0, outgoingSet.length);
        newTargets[outgoingSet.length] = in;
        outgoingSet = newTargets;
        System.arraycopy(weights, 0, newWeights, 0, weights.length);
        newWeights[weights.length] = weight;
        weights = newWeights;
    }
    
    public double[] getWeights()
    {
        return weights;
    }

    public void setWeights(double[] weights)
    {
        this.weights = weights;
    }
}