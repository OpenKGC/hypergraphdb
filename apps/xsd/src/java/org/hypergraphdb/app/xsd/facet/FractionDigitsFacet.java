package org.hypergraphdb.app.xsd.facet;


/**
 *
 */
public class FractionDigitsFacet implements ConstrainingFacet
{
    private boolean fixed;
    private String annotation;
    private int value;

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        if (0 > value)
        {
            throw new IllegalArgumentException(
                "The value must be larger or equal than 0. Passed value: " +
                value + ".");
        }
        this.value = value;
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
}
