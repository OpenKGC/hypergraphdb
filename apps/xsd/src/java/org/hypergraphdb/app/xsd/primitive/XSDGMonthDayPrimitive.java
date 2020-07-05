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
public class XSDGMonthDayPrimitive implements HGAtomType
{
    private HyperGraph hg;

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
        if (hg.getHandleFactory().nullHandle().equals(handle))
        {
            return null;
        }

        byte[] bytes = hg.getStore().getData(handle);

        XSDGMonthDay result = new XSDGMonthDay();
        result.setMonth(bytes[0]);
        result.setDay(bytes[1]);

        return result;
    }

    /**
     *
     * @param value String
     * @return boolean
     */
    public boolean evaluateRestrictions(XSDGMonthDay value)
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
        XSDGMonthDay instance = (XSDGMonthDay)o;

        /**@todo evaluate restrictions */
        boolean passes = evaluateRestrictions(instance);

        byte[] bytes = new byte[2];

        bytes[0] = instance.getMonth();
        bytes[1] = instance.getDay();

        return hg.getStore().store(bytes);
    }

    /**
     * @todo almost nowhere this method has been properly impl.
     */
    public void release(HGPersistentHandle handle)
    {
    }

    public boolean subsumes(Object general, Object specific)
    {
        return false;
    }

}
