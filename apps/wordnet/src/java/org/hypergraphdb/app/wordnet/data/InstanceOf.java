package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a type-token relationships between two WordNet synsets.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class InstanceOf extends Isa
{
	public InstanceOf() 
	{
		super();
	}

	public InstanceOf(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}