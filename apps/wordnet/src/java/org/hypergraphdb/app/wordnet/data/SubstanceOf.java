package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a whole-part relationship between noun senses where one 
 * thing (the first target) is the substance that the other things (the
 * second target is made of).
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class SubstanceOf extends Hasa
{
	public SubstanceOf() 
	{
		super();
	}

	public SubstanceOf(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}
