package org.hypergraphdb.app.ann;

import java.util.HashMap;

import org.hypergraphdb.HGHandle;

/**
 * <p>
 * A map HGHandle -> Double that will return a specified default on missing keys.
 * </p>
 */
public class ActivationMap extends HashMap<HGHandle, Double> 
{
    private static final long serialVersionUID = 6911585894112990613L;
    
    private Double def;
    
    public ActivationMap(Double def)
    {
        this.def = def;
    }
    
    @Override
    public Double get(Object key) 
    { 
        Double value = super.get(key);
        return value == null ? def : value;
    }
}