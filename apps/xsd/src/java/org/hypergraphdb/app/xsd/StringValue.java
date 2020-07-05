package org.hypergraphdb.app.xsd;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.javaprimitive.StringType;

/**
 * Have similar ifaces for all of the XSD primitive types ?
 */
public abstract class StringValue implements HGAtomType
{
    private HyperGraph hg;
    private String value;

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * Evaluate the validity of the current value against all the known
     * metainfo. Do it implicitely in setValue instead ? To be implemented by
     * the generated classes.
     * @return boolean
     */
    abstract boolean evaluate();

    ////////////////////////////////////////////////////////////////////

    public void setHyperGraph(HyperGraph hg)
    {
        this.hg = hg;
    }

    public Object make(HGPersistentHandle handle,
                       LazyRef < HGHandle[]>targetSet,
                       IncidenceSetRef incidenceSet)
    {
        HGHandle h = hg.getTypeSystem().getTypeHandle(String.class);
        StringType st = (StringType) hg.get(h);

        /**@todo doesn't match store.*/
        return st.make(handle, targetSet, incidenceSet);
    }

    public HGPersistentHandle store(Object instance)
    {
        HGHandle h = hg.getTypeSystem().getTypeHandle(String.class);
        StringType st = (StringType) hg.get(h);

        /**@todo seems somewhat weird..*/
        return st.store(((StringValue)instance).getValue());
    }

    public void release(HGPersistentHandle handle)
    {
    }

    public boolean subsumes(Object general, Object specific)
    {
        return false;
    }
}
