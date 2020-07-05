package org.hypergraphdb.app.wordnet.ext;

import java.util.HashMap;
import java.util.Map;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * 
 * <p>
 * Stores arbitrary extended information about a synset, accumulated in a simple
 * attribute map. The intended use is to support "Extended WordNet" features such
 * as POS tagging and WSD of glosses, precomputing some stats about a synset for
 * use in various algorithms, and having a logical form representation of the 
 * gloss defining phrase. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class SynsetInfo extends HGPlainLink
{
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	public SynsetInfo(HGHandle synsetHandle)
	{
		super(synsetHandle);
	}
	
	public SynsetInfo(HGHandle [] synsetHandle)
	{
		super(synsetHandle);		
	}

	public Map<String, Object> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes)
	{
		this.attributes = attributes;
	}	
}