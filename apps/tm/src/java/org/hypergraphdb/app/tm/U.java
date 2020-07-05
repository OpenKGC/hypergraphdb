package org.hypergraphdb.app.tm;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hypergraphdb.*;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.atom.HGRel;
import org.hypergraphdb.query.HGQueryCondition;
import org.hypergraphdb.util.HGUtils;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;

class U
{
	static final Object UNKNOWN = new Object();
	
	static String handleIRI(String iri)
	{
		return iri;
	}
	
	static TopicMap getTopicMapOf(HGTopicMapObjectBase tmObject)
	{
		// return (TopicMap)hg.findOne(tmObject.graph, hg.type(HGTopicMap.class));
		throw new UnsupportedOperationException();
	}
	
	static Locator makeLocator(String reference)
	{
		Locator result = null;
		try { result =  new URILocator(reference); } 
		catch (Exception ex) { throw new RuntimeException(ex); }
		return result;
	}
	
	static Locator makeLocator(String base, String relative)
	{
		try
		{
			relative = URILocator.escape(relative);
			base = URILocator.escape(base);
			URI rel = new URI(relative);
			if (rel.isAbsolute())
				return new URILocator(rel);
			else
				return new URILocator(new URI(base).resolve(rel));
		}
		catch (Exception ex) { throw new RuntimeException(ex); }
	}

	static Locator makeLocalLocator(String base, String localName)
	{
		try
		{
			URILocator result = new URILocator(base);
			if (localName != null && localName.length() > 0)
				result = result.resolveLocal(localName);
			return result;
		}
		catch (Exception ex) { throw new RuntimeException(ex); }
	}
	
	static Locator findLocator(HyperGraph graph, String uri)
	{
		HGHandle h = findLocatorHandle(graph, uri); 
		return h == null ? null : (Locator)graph.get(h);
	}
	
	static HGHandle findLocatorHandle(HyperGraph graph, String uri)
	{
		return hg.findOne(graph, hg.and(hg.type(URILocator.class), hg.eq("reference", uri)));
	}
	
	static HGHandle findLocatorHandle(HyperGraph graph, Locator locator)
	{
		HGHandle h = graph.getHandle(locator);
		if (h == null)
		{
			h = findLocatorHandle(graph, locator.getReference());
		}
		return h;
	}
	
	/**
	 * Ensure the passed in locator instance is recorded in the graph and
	 * return its HGDB handle.
	 */
	static HGHandle ensureLocator(HyperGraph graph, Locator l)
	{
		HGHandle h = hg.findOne(graph, hg.and(hg.type(l.getClass()), 
											  hg.eq("reference", l.getReference())));
		return h != null ? h : graph.add(l);
	}
	
	static Locator ensureLocator(HyperGraph graph, String base, String reference)
	{
		Locator l = base == null ? U.makeLocator(reference) : U.makeLocator(base,  reference);
		HGHandle h = hg.findOne(graph, hg.and(hg.type(l.getClass()), 
				  hg.eq("reference", l.getReference())));		
		if (h != null)
			return (Locator)graph.get(h);
		else
		{
			graph.add(l); 
			return l; 
		}
	}
	
	static HGHandle getTypeOf(HyperGraph graph, HGHandle h)
	{
		HGHandle rel = hg.findOne(graph, 
				  hg.and(hg.type(HGTM.hTypeOf), 
						 hg.incident(h),
						 hg.orderedLink(graph.getHandleFactory().anyHandle(), h)));
		if (rel == null)
			return null;
		else
			return ((HGLink)graph.get(rel)).getTargetAt(0);
	}
	
	static void setTypeOf(HyperGraph graph, HGHandle object, HGHandle type)
	{
		HGHandle rel = hg.findOne(graph, 
								  hg.and(hg.type(HGTM.hTypeOf),
										 hg.incident(object),
										 hg.orderedLink(graph.getHandleFactory().anyHandle(), object)));
		if (rel != null)
			graph.remove(rel);
		if (type != null)
			graph.add(new HGRel(HGTM.TypeOf, new HGHandle [] {type, object}), HGTM.hTypeOf);
	}	

	static HGHandle getReifierOf(HyperGraph graph, HGHandle h)
	{
		HGHandle rel = hg.findOne(graph, 
				  hg.and(hg.type(HGTM.hReifierOf), 
						 hg.incident(h),
						 hg.orderedLink(graph.getHandleFactory().anyHandle(), h)));
		if (rel == null)
			return null;
		else
			return ((HGLink)graph.get(rel)).getTargetAt(0);
	}
	
	static void setReifierOf(HyperGraph graph, HGHandle object, HGHandle reifier)
	{
		HGHandle rel = hg.findOne(graph, 
								  hg.and(hg.type(HGTM.hReifierOf), 
										 hg.incident(object),
										 hg.orderedLink(graph.getHandleFactory().anyHandle(), object)));
		if (rel != null)
			graph.remove(rel);
		if (reifier != null)
			graph.add(new HGRel(HGTM.ReifierOf, new HGHandle [] {reifier, object}), HGTM.hReifierOf);
	}
	
	/**
	 * Get all 'first' related to a given 'second' in an ordered link or, vice-versa,
	 * all 'second' related to a given 'first' in an ordered link. Whether first or
	 * second is required is indicated by putting null in the corresponding parameter. 
	 */
	static <T> Set<T> getRelatedObjects(HyperGraph graph, HGHandle relType, HGHandle first, HGHandle second)
	{
		HashSet<T> result = new HashSet<T>();
		HGQueryCondition relQuery = hg.and(hg.type(relType),
							   hg.incident(first == null ? second : first),	
							   hg.orderedLink(new HGHandle[] { 
								first == null ? graph.getHandleFactory().anyHandle() : first,
								second == null ? graph.getHandleFactory().anyHandle() : second
							   }));
		int idx = (first == null ? 0 : 1);
		List<T> L = hg.findAll(graph, 
				hg.apply(hg.deref(graph),
						 hg.apply(hg.linkProjection(idx), 
								  hg.apply(hg.deref(graph), relQuery))));
		result.addAll(L);					
		return result;
	}
	
	static void removeRelations(HyperGraph graph, HGHandle relType, HGHandle first, HGHandle second)
	{
		HashSet<HGHandle> relations = new HashSet<HGHandle>();
		HGSearchResult<HGHandle> rs = null;
		try
		{
			rs = graph.find(hg.and(hg.type(relType), 
					   			   hg.incident(first == null ? second : first),					
								   hg.orderedLink(new HGHandle[] { 
									first == null ? graph.getHandleFactory().anyHandle() : first,
									second == null ? graph.getHandleFactory().anyHandle() : second
								   })));
			int idx = (first == null ? 0 : 1); 
			while (rs.hasNext())
				relations.add(rs.next());
		}
		finally
		{
			HGUtils.closeNoException(rs);
		}
		for (HGHandle h : relations)
			graph.remove(h);
	}
	
	/**
	 * Get the 'first' related to a given 'second' in an ordered link or, vice-versa,
	 * the 'second' related to a given 'first' in an ordered link. Whether first or
	 * second is required is indicated by putting null in the corresponding parameter. 
	 */
	static Object getOneRelated(HyperGraph graph, HGHandle relType, HGHandle first, HGHandle second)
	{
		HGHandle h = hg.findOne(graph, hg.and(hg.type(relType), 
				   				hg.incident(first == null ? second : first),				
							    hg.orderedLink(new HGHandle[] { 
								first == null ? graph.getHandleFactory().anyHandle() : first,
								second == null ? graph.getHandleFactory().anyHandle() : second
							   })));
		int idx = (first == null ? 0 : 1);
		if (h != null)
			return graph.get(((HGLink)graph.get(h)).getTargetAt(idx));
		else
			return null;
	}	
	
	static void dettachFromMap(HyperGraph graph, HGHandle todettach)
	{
		List<HGHandle> all = hg.findAll(graph, hg.and(hg.type(HGTM.hMapMember), 
				   hg.incident(todettach),
				   hg.orderedLink(new HGHandle[] { 
					todettach,
					graph.getHandleFactory().anyHandle()
				   })));
		for (HGHandle h : all)
			graph.remove(h);
	}
}