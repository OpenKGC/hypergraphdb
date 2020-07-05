/*
 * This file is part of the HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2005-2006
 *  Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import java.util.ArrayList;
import java.util.Collection;

import org.hypergraphdb.app.xsd.FacetsDescriptor.Cardinality;
import org.hypergraphdb.app.xsd.facet.ConstrainingFacet;
import org.hypergraphdb.HGHandle;

/**
 *
 */
public class FacetsDescriptorBase
{
    private boolean ordered;
    private boolean bounded;
    private Cardinality cardinality;
    private boolean numeric;
    private String[] supportedFacets;
    private Collection<ConstrainingFacet> facets = new ArrayList<ConstrainingFacet>();

    /////////////////////////////////////
    // FacetsDescriptor Implementation //
    /////////////////////////////////////

    /**
     *
     */
    public FacetsDescriptorBase()
    {

    }

    /**
     *
     */
    public FacetsDescriptorBase(HGHandle[] handles)
    {

    }

    public boolean isOrdered()
    {
        return ordered;
    }

    public void setOrdered(boolean ordered)
    {
        this.ordered = ordered;
    }

    public boolean isBounded()
    {
        return bounded;
    }

    public void setBounded(boolean bounded)
    {
        this.bounded = bounded;
    }

    public Cardinality getCardinality()
    {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality)
    {
        this.cardinality = cardinality;
    }

    public boolean isNumeric()
    {
        return numeric;
    }

    public void setNumeric(boolean numeric)
    {
        this.numeric = numeric;
    }

    public void setSupportedFacets(String[] supportedFacets)
    {
        this.supportedFacets = supportedFacets;
    }

    public String[] getSupportedFacets()
    {
        return supportedFacets;
    }

    public void addFacet(ConstrainingFacet facet)
    {
        facets.add(facet);
    }

    /**
     *
     * @param facets Collection
     */
    public void setFacets(Collection<ConstrainingFacet> facets)
    {
        this.facets=facets;
    }

    /**
     *
     * @return Collection
     */
    public Collection<ConstrainingFacet> getFacets()
    {
        return facets;
    }

}
