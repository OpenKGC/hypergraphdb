package org.hypergraphdb.app.xsd.facet;


/**
 *
 */
public class PatternFacet implements ConstrainingFacet
{
    private String value;
    private String annotation;

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }
}
