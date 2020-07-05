package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents the sense (a WordNet synset) of an adjective.
 * </p>
 * 
 * @author Borislav Iordanov
 * 
 */
public class AdjSynsetLink extends SynsetLink
{
    public AdjSynsetLink()
    {
        super();
    }

    public AdjSynsetLink(HGHandle[] targets)
    {
        super(targets);
    }

    public String toString()
    {
        return "Adjective(" + getGloss() + ")";
    }
}