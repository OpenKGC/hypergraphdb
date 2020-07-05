/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd.primitive;

import java.math.BigDecimal;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.app.xsd.RestrictionViolationException;

/**
 * @todo BigInt type?
 */
public class XSDDecimalPrimitive implements HGAtomType
{
    private HyperGraph hg;

    /**
     *
     * @param hg HyperGraph
     */
    public void setHyperGraph(HyperGraph hg)
    {
        this.hg = hg;
    }

    /**
     *
     */
    public Object make(HGPersistentHandle handle,
                       LazyRef<HGHandle[]> targetSet,
                       IncidenceSetRef incidenceSet)
    {
        if (hg.getHandleFactory().nullHandle().equals(handle))
        {
            return null;
        }

        byte[] bytes = hg.getStore().getData(handle);
        String s = new String(bytes);

        BigDecimal result = new BigDecimal(s);

        return result;
    }

    /**
     *
     * @return boolean
     */
    public boolean evaluateRestrictions(BigDecimal value)
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
        BigDecimal instance = (BigDecimal)o;

        /**@todo evaluate restrictions */
        boolean passes = evaluateRestrictions(instance);
        if(false==passes)
        {
            throw new RestrictionViolationException(instance +
                " does not satisfy the type's restrictions.");
        }

        String s = instance.toString();
        byte[] bytes = s.getBytes();

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
