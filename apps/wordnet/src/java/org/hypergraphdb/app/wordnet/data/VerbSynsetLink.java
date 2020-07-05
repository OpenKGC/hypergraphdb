package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents the sense (WordNet synset) of a verb.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class VerbSynsetLink extends SynsetLink
{

    public VerbSynsetLink()
    {
        super();
    }

    public VerbSynsetLink(HGHandle[] targets)
    {
        super(targets);
    }

    public String toString()
    {
        return "Verb(" + getGloss() + ")";
    }
}
