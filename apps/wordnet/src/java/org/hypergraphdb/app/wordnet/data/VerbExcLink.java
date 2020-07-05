package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Exception past forms of verbs - the first target is the past 
 * participle while the second target is the verb root.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class VerbExcLink extends ExcLink
{
    public VerbExcLink()
    {
        super();
    }

    public VerbExcLink(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}
