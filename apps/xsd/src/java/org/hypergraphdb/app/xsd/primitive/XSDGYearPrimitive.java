/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd.primitive;

import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;

/**
 *
 */
public class XSDGYearPrimitive implements HGAtomType
{
    private HGAtomType hgdbType = null;

    /**
     *
     * @param hg HyperGraph
     */
    public void setHyperGraph(HyperGraph hg)
    {
        hgdbType = hg.getTypeSystem().getAtomType(Integer.class);
        /**@todo find out whether this is really necessary.*/
        hgdbType.setHyperGraph(hg);
    }

    /**
     *
     * @param handle HGPersistentHandle
     * @param targetSet LazyRef
     * @param incidenceSet LazyRef
     * @return Object
     */
    public Object make(HGPersistentHandle handle,
                       LazyRef targetSet,
                       IncidenceSetRef incidenceSet)
    {
        final Integer result = (Integer)hgdbType.make(handle, targetSet, incidenceSet);

        return result;
    }

    /**
     *
     * @param value String
     * @return boolean
     */
    public boolean evaluateRestrictions(int value)
    {
        return true;
    }

    /**
     *
     * @param instance Object
     * @return HGPersistentHandle
     */
    public HGPersistentHandle store(Object o)
    {
        Integer instance = (Integer)o;

        /**@todo evaluate restrictions */
        boolean passes = evaluateRestrictions(instance);

        return hgdbType.store(instance);
    }

    /**
     *
     * @param handle HGPersistentHandle
     */
    public void release(HGPersistentHandle handle)
    {
        hgdbType.release(handle);
    }

    /**
     *
     * @param general Object
     * @param specific Object
     * @return boolean
     */
    public boolean subsumes(Object general, Object specific)
    {
        return false;
    }

}
