package org.hypergraphdb.app.sail.model;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * created Feb 1, 2010 - 1:17:28 PM
 * 
 * @author IanHolsman Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HURI extends HResource implements URI
{ 
    public String namespace;
    public String localName;

    public HURI(String namespace, String local)
    {
        this.namespace = namespace;
        this.localName = local;
        this.val = this.stringValue();
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
        this.val = this.stringValue();
    }

    public void setLocalName(String localName)
    {
        this.localName = localName;
        this.val = this.stringValue();
    }

    public HURI()
    {
    }

    @Override
    public String toString()
    {
        return this.stringValue();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof URI))
            return false;

        URI huri = (URI) o;

        if (!localName.equals(huri.getLocalName()))
            return false;
        if (!namespace.equals(huri.getNamespace()))
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {        
        return toString().hashCode();
    }

    public HURI(URI x)
    {
        this.namespace = x.getNamespace();
        this.localName = x.getLocalName();
        this.val = this.stringValue();
    }

    public HURI(String uri)
    {
        URI x = new URIImpl(uri);
        this.namespace = x.getNamespace();
        this.localName = x.getLocalName();
        this.val = this.stringValue();

    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getLocalName()
    {
        return localName;
    }

    public String stringValue()
    {
        return namespace + localName;
    }

    @Override
    public HGHandle addIt(HyperGraph graph)
    {

        return graph.add(this);
    }
}
