package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents the sense (a WordNet synset) of an adverb.
 * </p>
 * 
 * @author Borislav Iordanov
 * 
 */
public class AdverbSynsetLink extends SynsetLink
{
    public AdverbSynsetLink()
    {
        super();
    }

    public AdverbSynsetLink(HGHandle[] targets)
    {
        super(targets);
    }

    public String toString()
    {
        return "Adverb(" + getGloss() + ")";
    }
}
