package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * A noun sense relation representing group membership. The first target is the 
 * group and the second its member (i.e. a zoological family of species and
 * a particular species as a member of that family).
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class MemberOf extends Hasa
{
	public MemberOf() 
	{
		super();
	}

	public MemberOf(HGHandle[] outgoingSet) 
	{
		super(outgoingSet);
	}
}
