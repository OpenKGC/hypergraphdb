package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Represents exceptional forms of noun plurals.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class NounExcLink extends ExcLink
{
    public NounExcLink()
    {
        super();
    }

    public NounExcLink(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}