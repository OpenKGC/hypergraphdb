package org.hypergraphdb.app.xsd;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * 
 * <p>
 * Represents an association between an XML Schema type name and a HyperGraph
 * <code>HGAtomType</code> implemenation. Based on this association, the system will
 * know which HyperGraph type to use when storing and retrieving values of a given
 * XML Schema type. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class XSDTypeImplementation extends HGPlainLink
{
	public XSDTypeImplementation(HGHandle xsdTypeNameAtom, HGHandle hgTypeAtom)
	{
		super(new HGHandle[] { xsdTypeNameAtom, hgTypeAtom});
	}
	
	public XSDTypeImplementation(HGHandle [] targetSet)
	{
		super(targetSet);
	}
	
	public HGHandle getXSDTypeName() { return this.getTargetAt(0); }
	public HGHandle getHGTypeAtom() { return this.getTargetAt(1); }
}