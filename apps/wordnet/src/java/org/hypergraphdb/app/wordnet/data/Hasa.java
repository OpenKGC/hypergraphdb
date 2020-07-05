package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a part-whole semantic relationship between WordNet synsets.
 * This is an arity 2 link where the first target is the whole/holonym and
 * the second target is the part/meronym.
 * </p>
 * <p>
 * This is further subdivided into <em>group membership</em>, <em>substance</em>
 * and <em>part of</em> part-whole relationships represented by the respective 
 * derived classes.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class Hasa extends SemanticLink
{
	public Hasa() 
	{
		super();
	}

	public Hasa(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}
