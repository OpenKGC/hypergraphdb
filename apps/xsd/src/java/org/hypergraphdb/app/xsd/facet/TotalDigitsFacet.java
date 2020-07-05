/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd.facet;


/**
 *
 * --- Pasted from the spec ---
 * [Definition:]   totalDigits controls the maximum number of values in the value space of
 * datatypes derived from decimal, by restricting it to numbers that are expressible as i ? 10^-n
 * where i and n are integers such that |i| < 10^totalDigits and 0 <= n <= totalDigits. The value of
 * totalDigits must be a positiveInteger. The term totalDigits is chosen to reflect the fact that it
 * restricts the value space to those values that can be represented lexically using at most
 * totalDigits digits. Note that it does not restrict the lexical space directly; a lexical
 * representation that adds additional leading zero digits or trailing fractional zero digits is
 * still permitted.
 */
public class TotalDigitsFacet implements ConstrainingFacet
{
    private boolean fixed;
    private String annotation;
    private int value;

    /**
     *
     * @return int
     */
    public int getValue()
    {
        return value;
    }

    /**
     *
     * @param value int
     */
    public void setValue(int value)
    {
        if (1 > value)
        {
            throw new IllegalArgumentException(
                "The value must be larger or equal than 1. Passed value: " +
                value + ".");
        }
        this.value = value;
    }

    /**
     *
     * @return boolean
     */
    public boolean isFixed()
    {
        return fixed;
    }

    /**
     *
     * @param fixed boolean
     */
    public void setFixed(boolean fixed)
    {
        this.fixed = fixed;
    }

    /**
     *
     * @return String
     */
    public String getAnnotation()
    {
        return annotation;
    }

    /**
     *
     * @param annotation String
     */
    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }

} //TotalDigitsFacet.
