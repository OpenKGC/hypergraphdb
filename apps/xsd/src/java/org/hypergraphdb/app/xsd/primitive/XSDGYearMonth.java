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
 *
 * @todo add time zone support.
 */
public class XSDGYearMonth
{
    private byte month;
    private short year;

    public void setMonth(byte month)
    {
        this.month = month;
    }
    public byte getMonth()
    {
        return month;
    }

    public void setYear(short year)
    {
        this.year = year;
    }
    public short getYear()
    {
        return year;
    }

    public String toString()
    {
        return year+"-"+month;
    }

    /**
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (null != obj && obj instanceof XSDGYearMonth)
        {
            XSDGYearMonth aGYearMonth = (XSDGYearMonth) obj;

            result = true;
            if (aGYearMonth.month != month)
            {
                result = false;
            }

            if (true == result && aGYearMonth.year != year)
            {
                result = false;
            }
        }

        return result;
    }

}
