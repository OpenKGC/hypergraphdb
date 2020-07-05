package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Expresses a causative relationship between two verb senses. The first target
 * is the cause and the second the "resultative" (e.g. "give" is a causative of
 * "have"). There are only a handful 209 such relations in WordNet 2.1
 * </p>
 * 
 * @author Borislav Iordanov
 * 
 */
public class Cause extends SemanticLink
{
    public Cause()
    {
        super();
    }

    public Cause(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}