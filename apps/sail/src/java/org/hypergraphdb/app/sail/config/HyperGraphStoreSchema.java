/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.hypergraphdb.app.sail.config;

/**
 * created Jan 29, 2010  - 2:11:39 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Defines constants for the MemoryStore schema which is used by
 * {@link HyperGraphStoreFactory}s to initialize {@link org.hypergraphdb.app.sail.HyperGraphStore}s.
 *
 * @author Arjohn Kampman
 */
public class HyperGraphStoreSchema {

	/** The MemoryStore schema namespace (<tt>http://www.openrdf.org/config/sail/HyperGraph#</tt>). */
	public static final String NAMESPACE = "http://www.openrdf.org/config/sail/HyperGraph#";

	/** <tt>http://www.openrdf.org/config/sail/HyperGraph#persist</tt> */
	public final static URI LOCATION;


	static {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		LOCATION = factory.createURI(NAMESPACE, "location");
	}
}
             