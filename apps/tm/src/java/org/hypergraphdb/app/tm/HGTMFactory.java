package org.hypergraphdb.app.tm;

import java.util.HashMap;
import java.util.Properties;

import org.hypergraphdb.HGEnvironment;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class HGTMFactory extends TopicMapSystemFactory
{
	private Properties props = new Properties();
	private HashMap<String, Boolean> features = new HashMap<String, Boolean>();
	
	@Override
	public boolean getFeature(String name) throws FeatureNotRecognizedException
	{
		return features.get(name);
	}

	@Override
	public String getProperty(String name)
	{
		return props.getProperty(name);
	}

	@Override
	public boolean hasFeature(String name)
	{
		return features.get(name);
	}

	@Override
	public TopicMapSystem newTopicMapSystem() throws TMAPIException
	{
		String location = props.getProperty(HGTM.TOPIC_MAP_SYSTEM_LOCATION);
		if (location == null || location.length() == 0)
			throw new TMAPIException("Please specify a HyperGraphDB directory location via the 'org.hypergraphdb.app.tm.hgdb' property.");		
		return new HGTopicMapSystem(HGEnvironment.get(location));
	}

	@Override
	public void setFeature(String name, boolean value)
			throws FeatureNotSupportedException, 
				   FeatureNotRecognizedException
	{
		features.put(name, value);
	}

	@Override
	public void setProperties(Properties props)
	{
		this.props = new Properties();
		this.props.putAll(props);
	}

	@Override
	public void setProperty(String name, String value)
	{
		props.put(name, value);
	}
}