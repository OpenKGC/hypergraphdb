/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd.primitive;

import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.HGHandle;

/**
 *
 */
public class XSDGDayPrimitive implements HGAtomType
{
    public HyperGraph hg;

    public void setHyperGraph(HyperGraph hg)
    {
        this.hg=hg;
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
        byte[] bytes = hg.getStore().getData(handle);

        XSDGDay result = new XSDGDay();
        result.setDay(bytes[0]);

        return result;
    }

    /**
     *
     * @return boolean
     */
    public boolean evaluateRestrictions(XSDGDay value)
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
        XSDGDay instance = (XSDGDay)o;

        /**@todo evaluate restrictions */
        boolean passes = evaluateRestrictions(instance);

        byte bytes[] = new byte[1];
        bytes[0] = instance.getDay();

        return hg.getStore().store(bytes);
    }

    /**
     *
     * @param handle HGPersistentHandle
     */
    public void release(HGPersistentHandle handle)
    {
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
