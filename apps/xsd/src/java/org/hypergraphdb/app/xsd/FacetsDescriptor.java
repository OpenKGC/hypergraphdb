package org.hypergraphdb.app.xsd;

import java.util.Collection;
import org.hypergraphdb.app.xsd.facet.ConstrainingFacet;

/**
 *
 */
public interface FacetsDescriptor
{
    enum Cardinality
    {
        FINITE,
        COUNTABLY_INFINITE
    }

    public void setSupportedFacets(Class[] facets);
    public Class[] getSupportedFacets();

    public void addFacet(ConstrainingFacet facet);
    public Collection<ConstrainingFacet> getFacets();

    public boolean isOrdered();
    public void setOrdered(boolean ordered);
    public boolean isBounded();
    public void setBounded(boolean bounded);
    public Cardinality getCardinality();
    public void setCardinality(Cardinality cardinality);
    public boolean isNumeric();
    public void setNumeric(boolean numeric);

    /**@todo "equal" facet api.*/
}
