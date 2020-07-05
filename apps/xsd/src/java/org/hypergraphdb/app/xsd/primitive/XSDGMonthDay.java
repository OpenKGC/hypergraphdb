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
 * The XSD gMonthDay is a recurrence pattern.
 *
 * @todo add time zone support.
 */
public class XSDGMonthDay
{
    private byte month;
    private byte day;

    public void setMonth(byte month)
    {
        this.month = month;
    }
    public byte getMonth()
    {
        return month;
    }

    public void setDay(byte day)
    {
        if(1>day || 31<day)
        {
            throw new IllegalArgumentException(
                "The day of the month must be between 1 and 31 inclusive.");
        }

        this.day = day;
    }
    public byte getDay()
    {
        return day;
    }

    public String toString()
    {
        return "--"+month+"--"+day;
    }

    /**
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (null != obj && obj instanceof XSDGMonthDay)
        {
            XSDGMonthDay aGMonthDay = (XSDGMonthDay) obj;

            result = true;
            if (aGMonthDay.month != month)
            {
                result = false;
            }

            if (true == result && aGMonthDay.day != day)
            {
                result = false;
            }
        }

        return result;
    }

}
