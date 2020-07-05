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
public class MaxInclusiveFacet extends ClusiveFacet
{
    public MaxInclusiveFacet()
    {
        super(Variants.MAXINCLUSIVE);
    }

    public MaxInclusiveFacet(final String limit)
    {
        super(Variants.MAXINCLUSIVE);
        this.limit = limit;
    }
}
