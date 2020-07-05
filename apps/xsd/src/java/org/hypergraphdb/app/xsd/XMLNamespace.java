package org.hypergraphdb.app.xsd;

/**
 * 
 * <p>
 * Represents an XML namespace as identified by a URI. The uri is stored here as a string, 
 * which is simpler and more portable than a <code>java.net.URI</code>.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class XMLNamespace
{
	private String uri;
	
	public XMLNamespace()
	{		
	}
	
	public XMLNamespace(String uri)
	{
		this.uri = uri;
	}
	
	public String getUri() { return uri; }
	public void setUri(String uri) { this.uri = uri; }
	
	public String toString() { return "XMLNamespace(" + uri + ")"; }
}