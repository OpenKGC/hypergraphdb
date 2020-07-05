package org.hypergraphdb.app.xsd.facet;


/**
 *
 */
public class EnumerationFacet implements ConstrainingFacet
{
    /**@todo value - A set of values from the value space of the {base type definition}"*/
    private String annotation;

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }
}
