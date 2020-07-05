package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents an exceptional form in adverb superlatives. The first target 
 * is the exceptional form and the second the adverb itself. This is
 * a link b/w {@Word} instances. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class AdverbExcLink extends ExcLink
{
    public AdverbExcLink()
    {
        super();
    }

    public AdverbExcLink(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}