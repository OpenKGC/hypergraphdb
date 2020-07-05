package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * 
 * <p>
 * Base class that represents a WordNet semantic link between synsets. The base is no
 * strictly needed, but it may be useful in applications that want to explicitly
 * distinguish between WordNet-specific synset relationships and application specific
 * relationships.
 * </p>
 * 
 * <p>
 * Derived classes captures relationships provided by the WordNet <em>pointers</em>. 
 * Note that bidirectional relationships are represented only once (e.g. instead of
 * creating a <em>hypernym</em> AND a <em>hyponim</em> relationships between two
 * synsets, only a single ordered <em>hypernymy</em> link is created). Refer to each
 * particular semantic relationships for specifics of the semantic role of each target
 * in the link. In general, the naming of the concrete semantic link class reflects a
 * left-to-right reading of targets: for example, MemberOf(target1, target2) should be
 * thought of as <em>target1 is a member of target2</em>, i.e. the fist target of the link
 * is a synset representing a concept that can be considered as a group member of the 
 * concept represented by the synset of the second target. 
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class SemanticLink extends HGPlainLink
{
	public SemanticLink() 
	{
		super();
	}

	public SemanticLink(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}

	public String toString()
	{
		return this.getClass().getName();
	}

	public void setTargets(HGHandle [] _outgoingSet)
	{
		outgoingSet = _outgoingSet; 
	}
}
