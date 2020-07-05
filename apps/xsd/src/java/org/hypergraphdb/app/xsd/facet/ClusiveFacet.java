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
 */
public abstract class ClusiveFacet<T> implements ConstrainingFacet
{
    public enum Variants
    {
        MAXINCLUSIVE, MAXEXCLUSIVE, MINEXCLUSIVE, MININCLUSIVE
    }


    private boolean fixed;
    private String annotation;
    private final Variants variant;
    protected String limit;

    /**
     * ClusiveFacet
     *
     * @param variant Variants
     */
    protected ClusiveFacet(Variants variant)
    {
        this.variant = variant;
    }

    public boolean isFixed()
    {
        return fixed;
    }

    public void setFixed(boolean fixed)
    {
        this.fixed = fixed;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }

    public Variants getVariant()
    {
        return variant;
    }

    /**
     *
     */
    public void setLimit(String limit)
    {
        this.limit = limit;
    }

    /**
     *
     */
    public String getLimit()
    {
        return limit;
    }

}
