package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Generally links sense that are somehow related. As of WordNet 2.1, this is
 * only used for verb and adjective senses.
 * </p>
 * 
 * @author Borislav Iordanov
 * 
 */
public class AlsoSee extends SemanticLink
{
    public AlsoSee()
    {
        super();
    }

    public AlsoSee(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}