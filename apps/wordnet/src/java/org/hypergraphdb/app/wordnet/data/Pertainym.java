package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a relational adjective. Adjectives that are pertainyms are 
 * usually defined by such phrases as "of or pertaining to" and do not have antonyms. 
 * A pertainym can point to a noun or another pertainym.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class Pertainym extends SemanticLink
{
    public Pertainym()
    {
        super();
    }

    public Pertainym(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }

}
