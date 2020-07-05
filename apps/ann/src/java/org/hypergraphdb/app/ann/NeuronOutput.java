package org.hypergraphdb.app.ann;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * <p>
 * Represents a connection between a neuron in the output layer of a neural net
 * and an arbitrary atom associated with that output. The relation is 1-1.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class NeuronOutput extends HGPlainLink
{
    public NeuronOutput(HGHandle...args)
    {
        super(args);
        if (args == null || args.length != 2)
            throw new RuntimeException("NeronOutput: expecting a neuron handle as 1st target and an arbitrary atom handle as 2nd target.");
    }
    
    public HGHandle getNeuron()
    {
        return getTargetAt(0);
    }
    
    public HGHandle getOutputAtom()
    {
        return getTargetAt(1);
    }
}