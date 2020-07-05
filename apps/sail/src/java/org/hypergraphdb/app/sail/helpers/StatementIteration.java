package org.hypergraphdb.app.sail.helpers;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.IteratorIteration;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.sail.SailException;

import java.util.Iterator;

/**
 * created Jan 29, 2010  - 7:25:42 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class StatementIteration extends IteratorIteration<Statement, SailException> implements
    CloseableIteration<Statement, SailException>
{

	public StatementIteration(Iterator<? extends Statement> iter) {
		super(iter);
	}

	public void close()
		throws SailException
	{
		// do nothing
	}
}