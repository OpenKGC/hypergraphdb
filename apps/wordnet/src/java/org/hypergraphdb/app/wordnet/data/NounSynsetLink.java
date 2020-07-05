package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents the sense (WordNet synset) of a noun. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class NounSynsetLink extends SynsetLink
{

    public NounSynsetLink()
    {
        super();
    }

    public NounSynsetLink(HGHandle[] targets)
    {
        super(targets);
    }

    public String toString()
    {
        return "Noun(" + getGloss() + ")";
    }
}