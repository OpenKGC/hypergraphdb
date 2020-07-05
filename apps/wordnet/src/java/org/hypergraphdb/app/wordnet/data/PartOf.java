package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents whole-part relationships between noun senses.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class PartOf extends Hasa
{
	public PartOf() 
	{
		super();
	}

	public PartOf(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}