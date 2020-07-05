package org.hypergraphdb.app.sail.helpers;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.IteratorIteration;
import org.hypergraphdb.app.sail.model.HyperContext;
import org.openrdf.model.Namespace;
import org.openrdf.sail.SailException;

import java.util.Iterator;

/**
 * created Jan 29, 2010  - 7:25:42 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class ContextIteration extends IteratorIteration<HyperContext, SailException> implements
    CloseableIteration<HyperContext, SailException>
{

	public ContextIteration(Iterator<? extends HyperContext> iter) {
		super(iter);
	}

	public void close()
		throws SailException
	{
		// do nothing
	}
}