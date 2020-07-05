package org.hypergraphdb.app.xsd.facet;


/**
 *
 */
public class WhiteSpaceFacet implements ConstrainingFacet
{
    public enum Values
    {
        PRESERVE, REPLACE, COLLAPSE
    }


    private Values value;
    private boolean fixed;
    private String annotation;

    public void setValue(Values value)
    {
        this.value = value;
    }

    public Values getValue()
    {
        return value;
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

    /**
     * WhiteSpaceFacet
     */
    public WhiteSpaceFacet()
    {
    }

    /**
     * WhiteSpaceFacet
     */
    public WhiteSpaceFacet(Values value)
    {
        this.value=value;
    }

}
