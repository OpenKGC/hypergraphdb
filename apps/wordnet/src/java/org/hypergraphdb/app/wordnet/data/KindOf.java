package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents regular kind-of hypernym-hyponym relationship between two
 * WordNet synsets. For nouns, this is the familiar inheritance relationship
 * (as in inheritance in OO programming language and as opposed to a type-instance
 * relationship which is also spelled out in English with an "is a"). The first
 * target is a kind of the second target. For verbs, the interpretation is 
 * similar (e.g. 'fly' is kind of 'travel'). In addition, for verbs this can represent
 * a <em>troponymy</em> relationship which is intepreted as "in the manner of".
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class KindOf extends Isa
{
	public KindOf() 
	{
		super();
	}

	public KindOf(HGHandle...outgoingSet) 
	{
		super(outgoingSet);
	}
}