package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a domain relationship between synsets. The first target
 * represents a broad category while the second "falls" under that category. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class InCategory extends DomainLink
{
	public InCategory() 
	{
		super();
	}

	public InCategory(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}
