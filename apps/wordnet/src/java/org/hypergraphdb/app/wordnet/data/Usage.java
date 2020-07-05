package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a relationship where the first target synset is used in the context 
 * identified by the second target synset. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class Usage extends DomainLink
{
	public Usage() 
	{
		super();
	}

	public Usage(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}
