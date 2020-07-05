package org.hypergraphdb.app.tm;

import java.util.HashSet;

import java.util.List;
import java.util.Set;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicMapSystem;
import org.hypergraphdb.*;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.query.AtomProjectionCondition;
import org.hypergraphdb.query.OrderedLinkCondition;
import org.hypergraphdb.query.impl.DefaultKeyBasedQuery;
import org.hypergraphdb.query.impl.PipeQuery;
import org.hypergraphdb.util.ValueSetter;

public final class HGTopicMapSystem implements TopicMapSystem
{
	private HyperGraph graph;
	private boolean closeHGOnExit = false;
	
	public HGTopicMapSystem(HyperGraph hg)
	{
		this.graph = hg;
	}
	
	public HyperGraph getGraph()
	{
		return graph;
	}
	
	public void close()
	{
		if (closeHGOnExit)
			graph.close();
	}

	public Locator toLocator(String reference)
	{
		return U.ensureLocator(graph, null, reference);
	}

	public Locator toLocator(String base, String reference)
	{
		return U.ensureLocator(graph, base, reference);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TopicMapObject> T locate(Locator itemIdentifier)
	{
		HGHandle lh = graph.getHandle(itemIdentifier);
		if (lh == null)
			lh = U.ensureLocator(graph, itemIdentifier); 
		return (T)
				U.getOneRelated(graph, 
							    HGTM.hSourceLocator, 
							    lh, 
							    null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TopicMapObject> T locate(String itemIdentifier)
	{
		return (T)locate(U.ensureLocator(graph, null, itemIdentifier));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TopicMapObject> T locateByIndicator(Locator subjectIdentifier)
	{
		HGHandle lh = graph.getHandle(subjectIdentifier);
		if (lh == null)
			lh = U.ensureLocator(graph, subjectIdentifier);
		return (T)
				U.getOneRelated(graph, 
							    HGTM.hSubjectIdentifier, 
							    lh, 
							    null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TopicMapObject> T locateByIndicator(String subjectIdentifier)
	{
		return (T)locate(U.ensureLocator(graph, null, subjectIdentifier));
	}

	@SuppressWarnings("unchecked")
	public <T extends TopicMapObject> T locateBySubject(Locator subjectLocator)
	{
		HGHandle lh = graph.getHandle(subjectLocator);
		if (lh == null)
			lh = U.ensureLocator(graph, subjectLocator);
		return (T)
				U.getOneRelated(graph, 
							    HGTM.hSubjectLocator, 
							    lh, 
							    null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TopicMapObject> T locateBySubject(String subjectLocator)
	{
		return (T)locate(U.ensureLocator(graph, null, subjectLocator));
	}
	
	public List<TopicMap> getTopicMaps()
	{
		return hg.<TopicMap>findAll(graph, hg.apply(hg.deref(graph), hg.type(HGTopicMap.class)));
	}
	
	public TopicMap createTopicMap(String uri) throws TopicMapExistsException
	{
		Locator l = U.findLocator(graph, uri);
		if (l == null)
		{
			l = U.makeLocator(uri);
		}
		HGTopicMap result = new HGTopicMap();
		result.setBaseLocator(l);
		result.graph = graph;
		graph.add(result);
		result.system = this;
		return result;
	}

	public TopicMap createTopicMap(String baseLocatorReference, String baseLocatorNotation)
			throws TopicMapExistsException
	{
		if (!baseLocatorNotation.equals("URI"))
			throw new TMAPIRuntimeException("Unsupported locator notation '" + baseLocatorNotation + "'"); 
		return createTopicMap(baseLocatorReference);
	}

	public Set getBaseLocators()
	{
		HashSet<Object> result = new HashSet<Object>();
		for (HGHandle h : hg.<HGHandle>findAll(graph, new AtomProjectionCondition("baseLocator", hg.type(HGTopicMap.class))))
		{
			result.add(graph.get(h));
		}
		return result;
	}

	public boolean getFeature(String featureName) throws FeatureNotRecognizedException
	{
		return false;
	}

	public String getProperty(String propertyName)
	{
		return null;
	}

	public TopicMap getTopicMap(String baseLocatorReference)
	{
		Locator l = U.findLocator(graph, baseLocatorReference);
		return l == null ? null : getTopicMap(l);
	}

	public TopicMap getTopicMap(Locator baseLocator)
	{
		HGHandle h = hg.findOne(graph, hg.and(hg.type(HGTopicMap.class), 
											  hg.eq("baseLocator", baseLocator)));
		if (h == null)
			return null;
		else
		{
			HGTopicMap map = (HGTopicMap)graph.get(h);
			map.system = this;
			return map;
		}
	}

	public TopicMap getTopicMap(String baseLocatorReference, String baseLocatorNotation)
	{
		if (!baseLocatorNotation.equals("URI"))
			throw new TMAPIRuntimeException("Unsupported locator notation '" + baseLocatorNotation + "'");
		else
			return getTopicMap(baseLocatorReference);
	}
	
	public void load(String uri)
	{
//		TMXMLUtils.load(uri, this);
	}
	
	// Search utilities
	
	/**
	 * <p>
	 * Find all topics associated to a given type according to prescribed role types. There are
	 * two versions of this method, one taking <code>HGHandle</code> identifying the topics
	 * and the other taking <code>Topic</code> instances.
	 * </p>
	 * 
	 * @param t The topic whose associated topics are being sought.
	 * @param roleType The type of the role of <code>t</code> in the associations.
	 * @param targetRoleType The type of the role of associated topics to be returned.
	 * @return A list of topics associated to <code>t</code> with role type <code>targetRoleType</code>.
	 */
	public List<Topic> findAssociated(HGHandle ht, HGHandle hRoleType, HGHandle hTargetRoleType)
	{
        // q1 will produce all associations in which 'item' is part
        HGQuery q1 = HGQuery.make(graph, 
        			   hg.apply(HGQuery.hg.targetAt(graph, 2), 
        						hg.and(hg.type(HGAssociationRole.class), 
        							   hg.orderedLink(ht, hRoleType, graph.getHandleFactory().anyHandle()))));
        // A link condition constraining roles such that the role type is 'h' and the association is set as a key
        // to a piped query 
        final OrderedLinkCondition linkCondition = HGQuery.hg.orderedLink(graph.getHandleFactory().anyHandle(),
        															  hTargetRoleType,
        															  graph.getHandleFactory().anyHandle());
        DefaultKeyBasedQuery pipe = new DefaultKeyBasedQuery(
        	graph,
            HGQuery.hg.apply(HGQuery.hg.targetAt(graph, 0),
                             hg.and(HGQuery.hg.type(HGAssociationRole.class), linkCondition)),
            new ValueSetter() { public void set(Object value) { linkCondition.setTarget(2, (HGHandle)value); } });
        HGQuery query = new PipeQuery(q1, pipe);
        query.setHyperGraph(graph);
        return hg.findAll(query);		
	}
	
	/**
	 * <p>
	 * Find all topics associated to a given type according to prescribed role types.
	 * </p>
	 * 
	 * @param t The topic whose associated topics are being sought.
	 * @param roleType The type of the role of <code>t</code> in the associations.
	 * @param targetRoleType The type of the role of associated topics to be returned.
	 * @return A list of topics associated to <code>t</code> with role type <code>targetRoleType</code>.
	 */
	public List<Topic> findAssociated(Topic t, Topic roleType, Topic targetRoleType)
	{
		return findAssociated(graph.getHandle(t), graph.getHandle(roleType), graph.getHandle(targetRoleType));
	}
}