package org.hypergraphdb.app.sail.model;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.HyperGraph;
import org.openrdf.model.Value;

/**
 * created Jan 29, 2010 - 3:34:19 PM
 * 
 * @author IanHolsman Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public abstract class HValue implements Value
{
    public String val;

    public HValue()
    {
    }

    public HValue(Value x)
    {

        this.setVal(x);
    }

    public HValue(String val)
    {
        this.val = val;
    }

    public int hashCode()
    {
        return val == null ? 0 : val.hashCode();
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o instanceof Value)
        {
            Value otherNode = (Value) o;
            return val == null ? otherNode.stringValue() == null : val.equals(otherNode.stringValue());
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.stringValue();
    }

    public void setVal(String val)
    {
        this.val = val;
    }

    public void setVal(Value x)
    {
        this.val = x.stringValue();
    }

    public String stringValue()
    {
        return val;
    }

    public String getVal()
    {

        return val;
    }

    public static HGHandle find(HyperGraph graph, Value h)
    {

        return HGQuery.hg.findOne(graph,
                                  HGQuery.hg.and(HGQuery.hg.typePlus(HValue.class),
                                                 HGQuery.hg.eq("val",
                                                               h.toString())));

    }

    public abstract HGHandle addIt(HyperGraph graph);

}