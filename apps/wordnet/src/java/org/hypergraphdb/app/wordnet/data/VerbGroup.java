package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Associates verb senses that are similar in meaning.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class VerbGroup extends SemanticLink
{
    public VerbGroup()
    {
        super();
    }

    public VerbGroup(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}