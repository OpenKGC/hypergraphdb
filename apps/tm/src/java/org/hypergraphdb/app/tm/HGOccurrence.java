package org.hypergraphdb.app.tm;

import java.util.Set;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.annotation.AtomReference;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;

public class HGOccurrence extends HGScopedObject implements Occurrence
{
	@AtomReference("symbolic")
	private Locator dataType;
	private String value;
	HGHandle topic;
	Object type = U.UNKNOWN;
	
	public HGOccurrence()
	{
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
	
	public Locator getResource()
	{
		return getDataType();
	}

	public Topic getTopic()
	{
		if (topic == null)
		{
			Object x = (HGTopic)U.getOneRelated(graph, HGTM.hOccurrence, graph.getHandle(this), null);
			topic = graph.getHandle(x); 
		}
		return (Topic)graph.get(topic);
	}

	@HGIgnore
	public Topic getType()
	{
		if (type == U.UNKNOWN)
		{
			type = U.getTypeOf(graph, graph.getHandle(this));
			if (type != null)
				type = graph.get((HGHandle)type);
		}
		return (Topic)type;
	}

	public String getValue()
	{
		return value;
	}

	public void setResource(Locator resource)
	{
		setDataType(resource);;
	}

	@HGIgnore
	public void setType(Topic newType)
	{
		U.setTypeOf(graph, graph.getHandle(this), graph.getHandle(newType));	
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	
	public Locator getDataType()
	{
		return dataType;
	}
	
	public void setDataType(Locator dataType)
	{
		this.dataType = dataType;
	}	
	
	public void remove() throws TopicInUseException
	{
		Set<HGOccurrence> peers = ((HGTopic)getTopic()).occurrences;
		if (peers != null)
			peers.remove(this);
		HGHandle thisH = graph.getHandle(this);
		HGHandle reifier = U.getReifierOf(graph, thisH);
		if (reifier != null)
		{
			U.setReifierOf(graph, thisH, null);
//			((Topic)graph.get(reifier)).remove();
		}				
		setType(null);
		for (Locator l : getSourceLocators())
			removeSourceLocator(l);					
		graph.remove(thisH, false);
	}	
	
	public String toString()
	{
		return "occurrence[" + dataType + "," + value + "]";
	}
}