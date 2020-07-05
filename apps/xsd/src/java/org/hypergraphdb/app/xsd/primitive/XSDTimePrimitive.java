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
 * Represent the XSD time as an XSDTime type.
 *
 * @todo add parsing of the "hh:mm:ss.sss" format
 */
public class XSDTimePrimitive implements HGAtomType
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

        XSDTime result = new XSDTime();
        result.setHour(bytes[0]);
        result.setMinute(bytes[1]);

        int intBits = (0xFF000000 & bytes[2] << 24);
        intBits |= (0xFF0000 & bytes[3] << 16);
        intBits |= (0xFF00 & bytes[4] << 8);
        intBits |= (0xFF & bytes[5]);
        result.setSeconds(Float.intBitsToFloat(intBits));

        return result;
    }

    /**
     *
     * @param value String
     * @return boolean
     */
    public boolean evaluateRestrictions(XSDTime value)
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
        XSDTime instance = (XSDTime)o;

        /**@todo evaluate restrictions */
        boolean passes = evaluateRestrictions(instance);

        byte[] bytes = new byte[6];

        bytes[0] = instance.getHour();
        bytes[1] = instance.getMinute();
        int intBits = Float.floatToRawIntBits(instance.getSeconds());
        bytes[2] = (byte) ((intBits >>> 24) & 0xFF);
        bytes[3] = (byte) ((intBits >>> 16) & 0xFF);
        bytes[4] = (byte) ((intBits >>> 8) & 0xFF);
        bytes[5] = (byte) ((intBits >>> 0) & 0xFF);

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
