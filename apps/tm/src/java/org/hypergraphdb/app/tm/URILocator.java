	package org.hypergraphdb.app.tm;

import java.net.URI;

import org.tmapi.core.Locator;

public class URILocator implements Locator
{
	private URI uri = null;

	public static String escape(String s)
	{
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			switch (c)
			{
				case '%':
				{
					if (i < s.length() + 2 &&
						Character.digit(s.charAt(i+1), 16) > -1 &&
						Character.digit(s.charAt(i+2), 16) > -1)
					{
						result.append(c);
						break;
					}
				}
/*				case ';': case '/': case '?': case ':': case '@':
				case '&': case '=': case '+': case '$': case ',': */
				case ' ': case '<': case '>': case '"':
				case '{': case '}': case '|': case '\\':
				case '^': case '[': case ']': case '`': 
				case '#': // controversial!
				{
					result.append('%');
					result.append(Integer.toHexString(c));
					break;
				}
				default:
				{
					if (c >= 0 && c <= 0x1F || c == 0x7F)
					{
						result.append('%');
						result.append(Integer.toHexString(c));
					}
					else
						result.append(c);
				}
			}			
		}
		return result.toString();
	}
	
	public URILocator()
	{		
	}
	
	public URILocator(String uri)
	{
		try { this.uri = new URI(escape(uri)); }
		catch (Exception ex) { throw new RuntimeException(ex); }
	}
	
	public URILocator(URI uri)
	{
		this.uri = uri;
	}
	
	public String getNotation()
	{		
		return "URI";
	}

	public void setReference(String uri)
	{
		try { this.uri = new URI(escape(uri)); }
		catch (Exception ex) { throw new RuntimeException(ex); }
	}
	
	public String getReference()
	{
		return uri.toString();
	}

	public URILocator resolveRelative(String relativePath)
	{
		try
		{
			relativePath = escape(relativePath);
			if (uri.toString().endsWith("/"))
				return new URILocator(new URI(uri.toString() + relativePath));
			else
				return new URILocator(new URI(uri.toString() + "/" + relativePath));
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public URILocator resolveLocal(String localName)
	{
		String s = uri.toString();
		localName = escape(localName);
		if (s.endsWith("/"))
			s = s.substring(0, s.length() - 1);
		return new URILocator(s + "#" + localName);
	}
	
	public String toExternalForm()
	{
		return uri.toString();
	}
	
	public String toString()
	{
		return toExternalForm();
	}
	
	public int hashCode()
	{
		if (uri == null) return 0;
		else return uri.hashCode();
	}
	
	public boolean equals(Object o)
	{
		if (! (o instanceof URILocator)) return false;
		URILocator x = (URILocator)o;
		if (uri == null)
			return x.uri == null;
		else
			return uri.equals(x.uri);
	}
}