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
 * Based on ISO 8601 Date and Time Formats.
 *
 * @todo add timezone support
 */
public class XSDTime
{
    private byte hour;
    private byte minute;
    private float seconds;

    public void setHour(byte hour)
    {
        this.hour = hour;
    }

    public byte getHour()
    {
        return hour;
    }

    public void setMinute(byte minute)
    {
        this.minute = minute;
    }

    public byte getMinute()
    {
        return minute;
    }

    public void setSeconds(float seconds)
    {
        this.seconds = seconds;
    }

    public float getSeconds()
    {
        return seconds;
    }

    /**
     *
     * @return String
     */
    public String toString()
    {
        return hour + ':' + minute + ':' + new Float(seconds).toString();
    }

    /**
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (null != obj && obj instanceof XSDTime)
        {
            XSDTime aTime = (XSDTime) obj;

            result = true;
            if (aTime.hour != hour)
            {
                result = false;
            }

            if (true == result && aTime.minute != minute)
            {
                result = false;
            }

            if (true == result && aTime.seconds != seconds)
            {
                result = false;
            }
        }

        return result;
    }

}
