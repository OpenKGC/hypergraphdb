package org.hypergraphdb.app.ann;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;

/**
 * <p>
 * Represents a runtime portion of a 3-layer feedforward neural network stored in a 
 * HyperGraphDB instance. The activation levels of the 3 layers are maintained in local
 * maps. This class is used both to execute the NN via the <code>feedforward</code> method
 * and the train it with the <code>train</code>. Only the relevant portion of the full
 * network, depending on the passed in inputs and outputs, is ever loaded from permanent
 * storage.  
 * </p>
 */
public class NeuralNet
{
    private static final double INITIAL_OUT_STRENGTH = 0.1;
    
    private HyperGraph graph;
    private ActivationFunction activationFunction = new TanhFun();
    Map<HGHandle, Double> inputLayer = null;
    Map<HGHandle, Double> hiddenLayer = null;
    Map<HGHandle, Double> outputLayer = null;
    private double learningRate = 0.5;
    
    private Map<HGHandle, Double> activateNextLayer(Map<HGHandle, Double> previousLayer, Map<HGHandle, Double> nextLayer)
    {
        for (Map.Entry<HGHandle, Double> in : previousLayer.entrySet())
        {
            Collection<Neuron> downstream = hg.getAll(graph, hg.and(hg.type(Neuron.class), 
                                                                    hg.incident(in.getKey())));
            for (Neuron n : downstream)
                if (!nextLayer.containsKey(n))
                    nextLayer.put(graph.getHandle(n), n.fire(previousLayer, activationFunction));
        }
        return nextLayer;
    }
    
    public NeuralNet(HyperGraph graph)
    {
        this.graph = graph;
    }
        
    public Map<Object, Double> feedforward(HGHandle...inputs)
    {
        ActivationMap inputMap = new ActivationMap(0.0);
        for (HGHandle in : inputs)
            inputMap.put(in, 1.0);
        feedforward(inputMap);
        Map<Object, Double> result = new HashMap<Object, Double>();
        for (Map.Entry<HGHandle, Double> e : outputLayer.entrySet())
            result.put(hg.getOne(graph, hg.apply(hg.targetAt(graph, 1), 
                                                    hg.and(hg.type(NeuronOutput.class), 
                                                           hg.incident(e.getKey())))), e.getValue());
        return result;
    }
    
    public void feedforward(ActivationMap inputs)
    {
        inputLayer = inputs;
        hiddenLayer = activateNextLayer(inputLayer, new ActivationMap(0.0));
        outputLayer = activateNextLayer(hiddenLayer, new ActivationMap(0.0));
    }    
    
    public void backpropagate(Map<HGHandle, Double> fromOutputs)
    {
        Map<HGHandle, Double> outputDeltas = new HashMap<HGHandle, Double>();
        for (Map.Entry<HGHandle, Double> node : fromOutputs.entrySet())
        {        	
        	Double output = outputLayer.get(node.getKey());
            double error = node.getValue() - output;
            outputDeltas.put(node.getKey(), activationFunction.deval(output)*error);
        }

        Map<HGHandle, Double> hiddenDeltas = new HashMap<HGHandle, Double>();        
        for (Map.Entry<HGHandle, Double> node : hiddenLayer.entrySet())
        {
            double error = 0.0;
            for (HGHandle out : fromOutputs.keySet())
            {
                Neuron n = graph.get(out);
                int pos = n.findInput(node.getKey());
                // ignore missing hidden->output layer links
                if (pos >= 0)
                	error += outputDeltas.get(out)*n.getWeights()[pos];
            }
            hiddenDeltas.put(node.getKey(), activationFunction.deval(node.getValue())*error);
        }
        
        for (HGHandle node : fromOutputs.keySet())
        {
            Neuron n = graph.get(node);
            for (int i = 0; i < n.getArity(); i++)
            {
                n.getWeights()[i] += learningRate*(outputDeltas.get(node)*hiddenLayer.get(n.getTargetAt(i)));
            }                
            graph.update(n);
        }
        
        for (HGHandle node : hiddenLayer.keySet())
        {
            Neuron n = graph.get(node);
            for (int i = 0; i < n.getArity(); i++)
            {
                n.getWeights()[i] += learningRate*(hiddenDeltas.get(node)*inputLayer.get(n.getTargetAt(i)));
            }    
            graph.update(n);
        }        
    }

    public void train(Collection<HGHandle> inputs, Collection<HGHandle> outputs, HGHandle selectedOutput)
    {
    	train(inputs, outputs, selectedOutput, 1.0);
    }
    
    public void train(Collection<HGHandle> inputs, 
    				  Collection<HGHandle> outputs, 
    				  HGHandle selectedOutput,
    				  Double selectedOutputActivation)
    {
        Collection<HGHandle> outputNeurons = updateNeuralStructure(inputs, outputs);
        ActivationMap inputMap = new ActivationMap(0.0);
        for (HGHandle in : inputs)
            inputMap.put(in, 1.0);
    	selectedOutput = hg.findOne(graph, hg.apply(hg.targetAt(graph, 0), 
													hg.and(hg.type(NeuronOutput.class), 
														   hg.incident(selectedOutput))));        
        Map<HGHandle, Double> outputMap = new HashMap<HGHandle, Double>();
        for (HGHandle h : outputNeurons)
        	outputMap.put(h, 0.0);
        outputMap.put(selectedOutput, selectedOutputActivation);
        feedforward(inputMap);        
        backpropagate(outputMap);
    }
    
    /**
     * <p>
     * Create a neuron in the hidden layers that connects a set of inputs (arbitrary atoms)
     * and a set of neuron outputs. The method will also create an neuron in the output layer
     * for each elements of the <code>outputs</code> that does have an associated neuron. 
     * </p>
     * 
     * @param inputs A collection of arbitrary input atoms.
     * @param outputs A collection of arbitrary output atoms.
     * @return A collection of the output neurons corresponding to the set of output
     * atoms. 
     */
    public Collection<HGHandle> updateNeuralStructure(Collection<HGHandle> inputs, 
                                   	  				  Collection<HGHandle> outputs)
    {
        HGHandle [] ins = inputs.toArray(new HGHandle[0]);
        HGHandle existing = hg.findOne(graph, hg.and(hg.link(ins), hg.arity(ins.length)));
        Collection<HGHandle> outputNeurons = new HashSet<HGHandle>();
        for (HGHandle out : outputs)
        {
            NeuronOutput no = hg.getOne(graph, hg.and(hg.type(NeuronOutput.class), hg.incident(out)));
            if (no == null)
            {
            	Neuron newOutputNeuron = new Neuron();
            	if (existing != null)
            		newOutputNeuron.addInput(existing, INITIAL_OUT_STRENGTH);
                no = new NeuronOutput(graph.add(newOutputNeuron), out);
                graph.add(no);
            }
            outputNeurons.add(no.getNeuron());
        }            
        if (existing == null)
        	graph.getHandle(createNeuron(inputs, 1.0/ins.length, outputNeurons, INITIAL_OUT_STRENGTH));
        return outputNeurons;
    }
    
    /**
     * <p>
     * Create a neuron in the networks. 
     * </p>
     * 
     * @param inputs Input atoms to this neuron: those may or may not be Neuron atom themselves.
     * @param initialInputWeight The weight of all synapses between the new neuron and its inputs.
     * @param outputs A collection of output <b>neurons</b>. Unlike the inputs collection, those are
     * expected to be neurons.
     * @param initialOutputWeight The initial weight of all synapses between the new neuron and its
     * output connections.
     * @return The HyperGraph handle of the newly added neuron.
     */
    public HGHandle createNeuron(Collection<HGHandle> inputs, 
                                 double initialInputWeight, 
                                 Collection<HGHandle> outputs, 
                                 double initialOutputWeight)
    {
        Neuron n = new Neuron(inputs.toArray(new HGHandle[0]));
        n.setWeights(new double[inputs.size()]);
        for (int i = 0; i < n.getWeights().length; i++)
            n.getWeights()[i] = initialInputWeight;
        HGHandle nh = graph.add(n);        
        for (HGHandle outH : outputs)
        {
            Neuron out = graph.get(outH);
            out.addInput(nh, initialOutputWeight);
            graph.update(out);
        }
        return nh;
    }

	public ActivationFunction getActivationFunction()
	{
		return activationFunction;
	}

	public void setActivationFunction(ActivationFunction activationFunction)
	{
		this.activationFunction = activationFunction;
	}

	public double getLearningRate()
	{
		return learningRate;
	}

	public void setLearningRate(double learningRate)
	{
		this.learningRate = learningRate;
	}    
}