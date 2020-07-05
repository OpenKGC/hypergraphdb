package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents an exceptional form in adjective superlatives. The first target 
 * is the exceptional form and the second the adjective itself. This is
 * a link b/w {@Word} instances. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class AdjExcLink extends ExcLink
{
    public AdjExcLink()
    {
        super();
    }

    public AdjExcLink(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}