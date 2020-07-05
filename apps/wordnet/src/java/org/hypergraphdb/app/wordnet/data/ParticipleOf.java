package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Relates adjectives (first target) that are past participles of verbs (second target).
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class ParticipleOf extends SemanticLink
{
    public ParticipleOf()
    {
        super();
    }

    public ParticipleOf(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}