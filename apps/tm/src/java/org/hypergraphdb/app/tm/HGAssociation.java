package org.hypergraphdb.app.tm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.hypergraphdb.HGException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;

public class HGAssociation extends HGScopedObject implements Association, HGLink
{
	HGHandle [] targetSet = HyperGraph.EMPTY_HANDLE_SET;
	Topic type = null;
	
	public HGAssociation()
	{
	}
	
	public HGAssociation(HGHandle [] targetSet)
	{
		this.targetSet = targetSet;		 
	}
	
	public AssociationRole createAssociationRole(Topic player, Topic type)
	{
		HGAssociationRole result = new HGAssociationRole(
				new HGHandle[] {graph.getHandle(player), 
								graph.getHandle(type),
								graph.getHandle(this)});
		result.graph = graph;
		HGHandle [] newTS = new HGHandle[targetSet.length + 1];
		System.arraycopy(targetSet, 0, newTS, 0, targetSet.length);
		newTS[targetSet.length] = graph.add(result);
		targetSet = newTS;
		graph.update(this);
		return result;
	}

	public Set<HGAssociationRole> getAssociationRoles()
	{
		return new AssociationRoleSet(graph, this);
	}
	
	@HGIgnore
	public Topic getReifier()
	{
		HGHandle h = U.getReifierOf(graph, graph.getHandle(this)); 
		return h != null ? (Topic)graph.get(h) : null;
	}
	
	@HGIgnore
	public void setReifier(Topic topic)
	{
		U.setReifierOf(graph, graph.getHandle(this), graph.getHandle(topic));
	}
	
	@HGIgnore
	public Topic getType()
	{
		if (type == null)
		{
			HGHandle h = U.getTypeOf(graph, graph.getHandle(this));		
			type = h != null ? (Topic)graph.get(h) : null;
		}
		return type;
	}

	@HGIgnore
	public void setType(Topic type)
	{
		this.type = type;
		U.setTypeOf(graph, graph.getHandle(this), graph.getHandle(type));
	}

	public int getArity()
	{
		return targetSet.length;
	}

	public HGHandle getTargetAt(int i)
	{
		return targetSet[i];
	}

	public void notifyTargetHandleUpdate(int i, HGHandle handle)
	{
		targetSet[i] = handle;
	}	
	
    public void notifyTargetRemoved(int i)
    {
    	HGHandle [] newOutgoing = new HGHandle[targetSet.length - 1];
    	System.arraycopy(targetSet, 0, newOutgoing, 0, i);
    	System.arraycopy(targetSet, i + 1, newOutgoing, i, targetSet.length - i -1);
    	targetSet = newOutgoing;
    }
    
	void setTargetSet(HGHandle [] newTargetSet)
	{
		this.targetSet = newTargetSet;
	}
	
	public void remove() throws TopicInUseException
	{
		HGHandle thisH = graph.getHandle(this);
		U.dettachFromMap(graph, thisH);
		for (Locator l : getSourceLocators())
			removeSourceLocator(l);		
		setType(null);
		ArrayList<HGAssociationRole> roles = new ArrayList<HGAssociationRole>(getArity());
		roles.addAll(getAssociationRoles());
		for (HGAssociationRole role : roles)
			try { role.remove(); } catch (TMAPIException ex) {throw new HGException(ex); }
		HGHandle reifier = U.getReifierOf(graph, thisH);
		if (reifier != null)
		{
			U.setReifierOf(graph, thisH, null);
			((Topic)graph.get(reifier)).remove();
		}		
		graph.remove(thisH, true);
	}	
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("Association[");
		for (Iterator<HGAssociationRole> i = getAssociationRoles().iterator(); i.hasNext(); )
		{
			HGAssociationRole role = i.next();
			result.append(role.getPlayer() + ":" + role.getType());
			if (i.hasNext())
				result.append(",");
		}
		result.append("]");
		return result.toString();
	}
}