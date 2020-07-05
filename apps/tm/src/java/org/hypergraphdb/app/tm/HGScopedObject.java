package org.hypergraphdb.app.tm;

import java.util.Set;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.atom.HGRel;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;

public abstract class HGScopedObject extends HGTopicMapObjectBase implements ScopedObject
{
	public void addScopingTopic(Topic t)
	{
		HGHandle rel = hg.findOne(graph, 
				  hg.and(hg.type(HGTM.hScopeOf),
						 hg.incident(graph.getHandle(this)),
						 hg.incident(graph.getHandle(t)),
						 hg.orderedLink(graph.getHandle(this), graph.getHandle(t))));
		if (rel != null)
			graph.add(new HGRel(HGTM.ScopeOf, new HGHandle [] {graph.getHandle(this), graph.getHandle(t)}));
	}

	public Set<HGTopic> getScope()
	{
		return U.getRelatedObjects(graph, HGTM.hScopeOf, graph.getHandle(this), null);
	}

	public void removeScopingTopic(Topic t)
	{
		HGHandle rel = hg.findOne(graph, 
								  hg.and(hg.type(HGTM.hScopeOf),
										 hg.orderedLink(graph.getHandle(this), graph.getHandle(t))));
		if (rel != null)
			graph.remove(rel);
	}
}
