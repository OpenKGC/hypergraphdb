package org.hypergraphdb.app.sail.model;

import org.openrdf.model.Resource;

/**
 * created Jan 29, 2010 - 3:34:19 PM
 * 
 * @author IanHolsman Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperContext extends HURI
{
    private static final long serialVersionUID = 5419121580764959498L;

    public HyperContext()
    {
    }

    public HyperContext(String val)
    {
        super(val);
    }

    public HyperContext(Resource r)
    {
        super(r.stringValue());
    }
}
