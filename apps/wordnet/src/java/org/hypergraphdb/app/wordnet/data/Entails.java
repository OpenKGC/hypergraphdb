package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * A verb sense relationships - A entails B if A cannot be done unless B is done.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class Entails extends SemanticLink
{
	public Entails() 
	{
		super();
	}

	public Entails(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}
