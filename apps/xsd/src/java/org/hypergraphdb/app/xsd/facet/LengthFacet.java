package org.hypergraphdb.app.xsd.facet;


/**
 *
 */
public class LengthFacet implements ConstrainingFacet
{
    /*Per specification should be infinite. See:
     [Definition:]   nonNegativeInteger is derived from integer by setting
     thevalue of minInclusive to be 0. This results in the standard
     mathematical concept of the non-negative integers. The value space of
     nonNegativeInteger is the infinite set {0,1,2,...}.
     The base type of nonNegativeInteger is integer.*/
    private int value;
    /*If {fixed} is true, then types for which the current type is the {base
     type definition} cannot specify a value for length other than {value}.
     Mihail's note: applies to all facets that have a "fixed" attribute.*/
    private boolean fixed;
    private String annotation;

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
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
