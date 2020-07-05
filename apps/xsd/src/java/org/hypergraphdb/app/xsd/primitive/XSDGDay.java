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
public class XSDGDay
{
    private byte day;

    public byte getDay()
    {
        return day;
    }

    public void setDay(byte day)
    {
        this.day = day;
    }

    /**
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (null != obj && obj instanceof XSDGDay)
        {
            XSDGDay aGDay = (XSDGDay) obj;

            if (aGDay.day == day)
            {
                result = true;
            }
        }

        return result;
    }

    public String toString()
    {
        return "---"+day;
    }
}
