package org.hypergraphdb.app.tm;

import org.hypergraphdb.annotation.AtomReference;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

public class HGVariant extends HGScopedObject implements Variant
{
	String value;
	@AtomReference("symbolic")
	Locator dataType;
	
	public HGVariant()
	{
	}
	
	@HGIgnore
	public Topic getReifier()
	{
		return (Topic)graph.get(U.getReifierOf(graph, graph.getHandle(this)));
	}
	
	@HGIgnore
	public void setReifier(Topic topic)
	{
		U.setReifierOf(graph, graph.getHandle(this), graph.getHandle(topic));
	}

	public TopicName getTopicName()
	{
		return (TopicName)U.getOneRelated(graph, HGTM.hVariantOf, graph.getHandle(this), null);
	}

	public Locator getResource()
	{
		return getDataType();
	}
	
	public void setResource(Locator resource)
	{
		setDataType(resource);
	}

	public Locator getDataType()
	{
		return dataType;
	}
	
	public void setDataType(Locator dataType)
	{
		this.dataType = dataType;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}	
	
	public void remove() throws TMAPIException
	{
		for (Locator l : getSourceLocators())
			removeSourceLocator(l);					
		graph.remove(graph.getHandle(this), false);
	}		
}