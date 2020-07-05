package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * 
 * <p>
 * Represents exceptions: the first target is the exceptional form and the rest
 * of the targets (usually there's only one) represents the stem. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class ExcLink extends HGPlainLink
{

	public ExcLink() {
		super();
	}

	public ExcLink(HGHandle[] outgoingSet) {
		super(outgoingSet);
	}

	public void setTargets(HGHandle [] _outgoingSet)
	{
		outgoingSet = _outgoingSet;
	}
}
