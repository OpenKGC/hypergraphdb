package org.hypergraphdb.app.xsd;

/**
 * 
 * <p>
 * An <code>XSDTypeName</code> is a fully-qualified name of an XML Schema type. Thus, it
 * consists of an XMLNamespace together with a local name. Type names are stored as 
 * HyperGraphDB atoms and may be linked to <code>HGAtomType</code> atoms that implement
 * the precise syntax and semantics of the corresponding type. This association between
 * a schema type name and a hypergraphdb type is represented by an instance of a
 * <code>XSDTypeImplementation</code> link.   
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class XSDTypeName
{
	private XMLNamespace namespace;
	private String localName;
	
	public XSDTypeName() { }
	public XSDTypeName(XMLNamespace namespace, String localName)
	{
		this.namespace = namespace;
		this.localName = localName;
	}
	
	public final String getLocalName()
	{
		return localName;
	}
	public final void setLocalName(String localName)
	{
		this.localName = localName;
	}
	public final XMLNamespace getNamespace()
	{
		return namespace;
	}
	public final void setNamespace(XMLNamespace namespace)
	{
		this.namespace = namespace;
	}
	
	public String toString()
	{
		return "XMLSchemaType(" + namespace + "#" + localName + ")";
	}
}