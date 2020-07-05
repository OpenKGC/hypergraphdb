package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a domain relationship between synsets. The intent is the group senses
 * into broader domains (topical classes), based on a broad category or region. There's not that many
 * domain relationships in the original WordNet data though.  
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class DomainLink extends SemanticLink
{
	public DomainLink() 
	{
		super();
	}

	public DomainLink(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}