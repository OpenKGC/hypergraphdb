package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * States a similarity relation between two adjective synsets.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class Similar extends SemanticLink
{

	public Similar() {
		super();
	}

	public Similar(HGHandle[] outgoingSet) {
		super(outgoingSet);
	}

}
