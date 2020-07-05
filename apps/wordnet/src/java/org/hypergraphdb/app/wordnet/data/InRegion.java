package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;


/**
 * 
 * <p>
 * Represents a relationship where the first target synset pertains to
 * a specific geographical region as identified by the second target synset. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class InRegion extends DomainLink
{
	public InRegion() 
	{
		super();
	}

	public InRegion(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}