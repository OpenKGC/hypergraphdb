package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a hypernym-hyponym relationship between WordNet synsets. This is
 * a link of arity 2 where the first target is the parent/hypernym/general
 * concept and the second is the child/hyponym/specific concept. Note that this
 * further subdivided into an inheritance (kind-of) and instanceof (type-token)
 * relationships. If this further distinction is irrelevant to an application,
 * a simple procedure that could eliminate it (replace all HyperGraph atoms of the 
 * subtypes with atoms of the this parent type). 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class Isa extends SemanticLink
{
	public Isa() 
	{
		super();
	}

	public Isa(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}