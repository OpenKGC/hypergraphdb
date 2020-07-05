package org.hypergraphdb.app.xsd;

import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.util.Pair;
import org.hypergraphdb.handle.UUIDHandleFactory;

/**
 * 
 * <p>
 * This class lists all statically defined HyperGraph data (i.e. all atoms with 
 * preconstructed persistent handles) for the XSD <em>application</em>. The statically
 * defined data essentially covers standard namespaces and built-in data types. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
class XSDHyperGraphData
{
	//
	// A few standard namespaces:
	//
	
	static final Pair<HGPersistentHandle, XMLNamespace>
		XML_SCHEMA = new Pair<HGPersistentHandle, XMLNamespace>(
				UUIDHandleFactory.I.makeHandle("5c4fa066-5e23-11db-a327-233fb450b841"),
				new XMLNamespace("http://www.w3.org/2001/XMLSchema"));
	
	//
	// Built-in XML Schema data types.
	//
	static final Pair<HGPersistentHandle, XSDTypeName>
		XSD_BOOLEAN = new Pair<HGPersistentHandle, XSDTypeName>(
				UUIDHandleFactory.I.makeHandle("7ce2dc27-5e23-11db-a327-233fb450b841"),
				new XSDTypeName(XML_SCHEMA.getSecond(), "boolean"));
	
	static final XSDTypeImplementation 
		XSD_BOOLEAN_IMPLEMENTATION = new XSDTypeImplementation(XSD_BOOLEAN.getFirst(),
				UUIDHandleFactory.I.makeHandle()); // todo - can't get the handle of boolean type in a static way anymore!!!!
															   //HGPredefinedTypes.BOOLEAN.getHandle());
	
	static Pair [] ATOMS = new Pair[]
	{
		XML_SCHEMA,
		XSD_BOOLEAN
	};
}