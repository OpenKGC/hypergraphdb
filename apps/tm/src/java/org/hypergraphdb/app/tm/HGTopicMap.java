package org.hypergraphdb.app.tm;

import java.util.HashSet;

import java.util.Set;

import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.annotation.AtomReference;
import org.hypergraphdb.annotation.HGIgnore;
import org.hypergraphdb.atom.HGRel;
import org.hypergraphdb.util.HGUtils;
import org.hypergraphdb.HGException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HGSearchResult;
import org.tmapi.core.Association;
import org.tmapi.core.HelperObjectConfigurationException;
import org.tmapi.core.HelperObjectInstantiationException;
import org.tmapi.core.Locator;
import org.tmapi.core.MergeException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.UnsupportedHelperObjectException;

public class HGTopicMap extends HGTopicMapObjectBase implements TopicMap
{
	@AtomReference("hard")
	private Locator baseLocator = null;
	HGTopicMapSystem system = null;
	
	public HGTopicMap()
	{
	}
	
	public void close()
	{
	}

	public Association createAssociation()
	{
		HGAssociation ass = new HGAssociation();
		ass.graph = graph;
		HGHandle h = graph.add(ass);
		graph.add(new HGRel(HGTM.MapMember, new HGHandle[] { h, graph.getHandle(this)}), 
				  HGTM.hMapMember);				
		return ass;
	}

	public Locator createLocator(String reference)
	{
		URILocator l = new URILocator(reference);
		return (Locator)graph.get(U.ensureLocator(graph, l));
	}

	public Locator createLocator(String reference, String notation)
	{
		if (!notation.equals("URI"))
			throw new RuntimeException("Unsupported notation : " + notation);
		return createLocator(reference);
	}

	public Topic createTopic()
	{
		HGTopic topic = new HGTopic();
		topic.graph = graph;
		HGHandle h = graph.add(topic);
		graph.add(new HGRel(HGTM.MapMember, new HGHandle[] { h, graph.getHandle(this)}), 
				  HGTM.hMapMember);		
		return topic;
	}

	public Set<Association> getAssociations()
	{
		HashSet<Association> result = new HashSet<Association>();
		HGSearchResult<HGHandle> rs = null;
		try
		{
			HGHandle mapHandle = graph.getHandle(this);	
			// Stated in English, the query finds all atoms of type HGAssociation
			// that are the 1st element of a link of type HGTM.hMapMember which
			// also point to the atom 'mapHandle'
			rs = graph.find(hg.and(hg.type(HGAssociation.class), 
								   hg.apply(hg.linkProjection(0), 
										    hg.apply(hg.deref(graph),
												     hg.and(hg.type(HGTM.hMapMember), 
													   	    hg.incident(mapHandle))))));
			while (rs.hasNext())			
				result.add((Association)graph.get(rs.next()));
		}
		finally
		{
			HGUtils.closeNoException(rs);
		}
		return result;
	}

	public Set<Topic> getTopics()
	{
		HashSet<Topic> result = new HashSet<Topic>();
		HGSearchResult<HGHandle> rs = null;
		try
		{
			HGHandle mapHandle = graph.getHandle(this);	
			// Stated in English, the query finds all atoms of type HGTopic
			// that are the 1st element of a link of type HGTM.hMapMember which
			// also point to the atom 'mapHandle'
			rs = graph.find(hg.and(hg.type(HGTopic.class), 
								   hg.apply(hg.linkProjection(0), 
										    hg.apply(hg.deref(graph),
												     hg.and(hg.type(HGTM.hMapMember), 
													   	    hg.incident(mapHandle))))));
			while (rs.hasNext())			
				result.add((Topic)graph.get(rs.next()));
		}
		finally
		{
			HGUtils.closeNoException(rs);
		}
		return result;		
	}
	
	public void setBaseLocator(Locator baseLocator)
	{
		this.baseLocator = baseLocator;
	}
	
	public Locator getBaseLocator()
	{
		return baseLocator;
	}

	public Object getHelperObject(Class arg0)
			throws UnsupportedHelperObjectException,
			HelperObjectInstantiationException,
			HelperObjectConfigurationException
	{
		return null;
	}

	public TopicMapObject getObjectById(String objectId)
	{
		HGPersistentHandle h = graph.getHandleFactory().makeHandle(objectId);
		HGTopicMapObjectBase o = (HGTopicMapObjectBase)graph.get(h);
		return o;
	}

	@HGIgnore
	public Topic getReifier()
	{
		return (Topic)graph.get(U.getReifierOf(graph, graph.getHandle(this)));
	}
	
	@HGIgnore
	public void setReifier(Topic t)
	{
		U.setReifierOf(graph, graph.getHandle(this), graph.getHandle(t));
	}
	
	public TopicMapSystem getTopicMapSystem()
	{
		return system;
	}

	public void mergeIn(TopicMap arg0) throws MergeException
	{
	}
	
	public void remove() throws TMAPIException
	{
//		graph.getTransactionManager().beginTransaction();
		try
		{
			for (Association a : getAssociations())
				a.remove();
			for (Topic t : getTopics())
			{
				HGHandle tHandle = graph.getHandle(t);
				U.removeRelations(graph, HGTM.hTypeOf, tHandle, null);
				U.removeRelations(graph, HGTM.hScopeOf, null, tHandle);
				t.remove();
			}
			for (Locator l : getSourceLocators())
				removeSourceLocator(l);			
			graph.remove(graph.getHandle(this));
	//		graph.getTransactionManager().endTransaction(true);			
		}
		catch (Throwable t)
		{
			try 
			{ 
		//		graph.getTransactionManager().endTransaction(false);
				throw new TMAPIRuntimeException(t);
			}
			catch (Exception ex) 
			{  
				throw new HGException("Exception during transaction rollback caused by :" + t.toString(), ex);
			}
		}
	}
}