package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Expresses opposing meaning between two adjective or adverb senses.
 * </p>
 * 
 * @author Borislav Iordanov
 * 
 */
public class Antonym extends SemanticLink
{
    public Antonym()
    {
        super();
    }

    public Antonym(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}