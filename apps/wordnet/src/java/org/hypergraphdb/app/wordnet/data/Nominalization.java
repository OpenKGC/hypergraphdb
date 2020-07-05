package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents a nominalization - a noun formed from another part of speech or another
 * part of speech used as noun (specifically the head of a noun phrase). 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class Nominalization extends SemanticLink
{
    public Nominalization()
    {
        super();
    }

    public Nominalization(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}