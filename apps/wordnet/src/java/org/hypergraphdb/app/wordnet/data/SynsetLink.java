package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * 
 * <p>
 * A WordNet synset is represented as an unordered hypergraph link between all the words in it.
 * </p>
 *
 * <p>
 * The <code>gloss</code> contains a dictionary style definition of the sense. The <code>id</code>
 * property is the number identifier copied from the original WordNet database. 
 * </p>
 * @author Borislav Iordanov
 *
 */
public class SynsetLink extends HGPlainLink 
{
	private long id;
	private String gloss;

	public SynsetLink() 
	{
	}

	public SynsetLink(HGHandle[] targets) 
	{
		super(targets);
	}

	public SynsetLink(String gloss, HGHandle[] targets) 
	{
		super(targets);
		this.gloss = gloss;
	}

	public String getGloss() 
	{
		return gloss;
	}
	
	public void setGloss(String g) 
	{
		gloss = g;
	}
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public void setTargets(HGHandle [] targets)
	{
		outgoingSet = targets;
	}
	
	public boolean equals(Object object) 
	{
		if (object == null || !(object instanceof SynsetLink))
			return false;
		else
			return id == ((SynsetLink) object).id;		
	}
	
	public int hashCode() 
	{
		return (int)id;
	}
}