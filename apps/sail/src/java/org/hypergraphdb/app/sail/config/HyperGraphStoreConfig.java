/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.hypergraphdb.app.sail.config;

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.sail.config.SailConfigException;
import org.openrdf.sail.config.SailImplConfigBase;

import static org.hypergraphdb.app.sail.config.HyperGraphStoreSchema.LOCATION;
/**
 * @author Ian Holsman
 *  (copied from work done by Arjohn Kampman )
 */
public class HyperGraphStoreConfig extends SailImplConfigBase {

	private String location;


	public HyperGraphStoreConfig() {
		super(HyperGraphStoreFactory.SAIL_TYPE);
	}

	public HyperGraphStoreConfig(String location) {
		this();
		this.location = location;
	}


  public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	@Override
	public Resource export(Graph graph)
	{
		Resource implNode = super.export(graph);

			graph.add(implNode, LOCATION, graph.getValueFactory().createLiteral(location));

		return implNode;
	}

	@Override
	public void parse(Graph graph, Resource implNode)
		throws SailConfigException
	{
		super.parse(graph, implNode);

		try {
			Literal persistValue = GraphUtil.getOptionalObjectLiteral(graph, implNode, LOCATION);
			if (persistValue != null) {
				try {
					setLocation((persistValue).stringValue());
				}
				catch (IllegalArgumentException e) {
					throw new SailConfigException("String value required for " + LOCATION + " property, found "
							+ persistValue);
				}
			}
		}
		catch (GraphUtilException e) {
			throw new SailConfigException(e.getMessage(), e);
		}
	}
}
