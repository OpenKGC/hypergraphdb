/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd.primitive;

/**
 * Recurrence pattern.
 * @todo add time zone support.
 */
public class XSDGMonth
{
    private byte month;

    public byte getMonth()
    {
        return month;
    }

    public void setMonth(byte month)
    {
        this.month = month;
    }

    /**
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (null != obj && obj instanceof XSDGMonth)
        {
            XSDGMonth aGMonth = (XSDGMonth) obj;

            if (aGMonth.month == month)
            {
                result = true;
            }
        }

        return result;
    }

    public String toString()
    {
        return "--"+month;
    }
}
