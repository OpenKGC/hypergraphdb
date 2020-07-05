/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import java.util.ArrayList;
import java.util.Collection;

import org.hypergraphdb.app.xsd.facet.ClusiveFacet;
import org.hypergraphdb.app.xsd.facet.ConstrainingFacet;
import org.hypergraphdb.app.xsd.facet.MaxExclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MaxInclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MinExclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MinInclusiveFacet;
import org.xml.sax.Attributes;
import org.hypergraphdb.app.xsd.facet.LengthFacet;
import org.hypergraphdb.app.xsd.facet.MaxLengthFacet;
import org.hypergraphdb.atom.HGSubsumes;
import org.hypergraphdb.app.xsd.facet.EnumerationFacet;
import org.hypergraphdb.app.xsd.facet.WhiteSpaceFacet;
import org.hypergraphdb.app.xsd.facet.MinLengthFacet;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.app.xsd.facet.PatternFacet;
import org.hypergraphdb.app.xsd.FacetsDescriptor.Cardinality;
import org.hypergraphdb.HGValueLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.app.xsd.facet.TotalDigitsFacet;
import org.hypergraphdb.app.xsd.facet.FractionDigitsFacet;


/**
 *
 */
public class SimpleTypeImporter
{
    private Class subsumesClass;
    private Collection<ConstrainingFacet> facets;
    private SchemaImporter importer;
    private String qualifiedName;
    private XSDTypeName typeName;


    /**
     *
     * @param hg HyperGraph
     */
    /*package*/ SimpleTypeImporter(SchemaImporter importer)
    {
        this.importer=importer;
    }

    /**
     *
     * @param attributes Attributes
     */
    public void startDefinition(Attributes attributes)
    {
        qualifiedName = attributes.getValue("name");
        typeName = new XSDTypeName(null, qualifiedName);

        facets = new ArrayList<ConstrainingFacet>();
        subsumesClass = null;
    }

    /**
     *
     */
    public void endDefinition()
    {
        HGHandle typeHandle = importer.hg.getTypeSystem().getTypeHandle(importer.resolveToUri(
            qualifiedName));

        if (null == typeHandle)
        {
            HGHandle aHandle = importer.hg.add(null, SimpleTypeConstructor.HANDLE);

            //Name-type link.
            /**@todo get the local name.*/
            XSDTypeName name = new XSDTypeName(null, qualifiedName);
            HGHandle nameHandle = importer.hg.add(name);
            XSDTypeImplementation nameType = new XSDTypeImplementation(
                nameHandle, aHandle);
            importer.hg.add(nameType);

            //Base type link.
            /*HGHandle baseTypeHandle = hg.add(subsumesClass);
             HGSubsumes subsumesLink = new HGSubsumes(baseTypeHandle,
                aHandle);
            hg.add(subsumesLink);*/
            HGHandle subsumesTypeHandle = importer.hg.add(subsumesClass.getCanonicalName());
            HGSubsumes subsumesLink = new HGSubsumes(subsumesTypeHandle, aHandle);
            importer.hg.add(subsumesLink);

            /**@todo use the facets of the base type*/
            FacetsDescriptorBase facetsDes = new FacetsDescriptorBase();
            facetsDes.setOrdered(false);
            facetsDes.setBounded(false);
            facetsDes.setCardinality(Cardinality.COUNTABLY_INFINITE);
            facetsDes.setNumeric(false);
            facetsDes.setSupportedFacets(new String[]
                                         {LengthFacet.class.getCanonicalName(),
                                         MinLengthFacet.class.getCanonicalName(),
                                         MaxLengthFacet.class.getCanonicalName(),
                                         PatternFacet.class.getCanonicalName(),
                                         EnumerationFacet.class.getCanonicalName(),
                                         WhiteSpaceFacet.class.getCanonicalName()});
            for (ConstrainingFacet facet : facets)
            {
                facetsDes.addFacet(facet);
            }

            //Restriction link.
            /**@todo should it be HGRel or HGRelType ?*/
            /**@todo first a properly define HGRelType must be created.*/
            /*HGHandle facetsHandle = hg.add(facetsDes);
                         HGRel restrictionLink = new HGRel("restriction",
                new HGHandle[]
                {aHandle, facetsHandle});*/
            HGValueLink restrictionLink = new HGValueLink(facetsDes,
                new HGHandle[]
                {aHandle});

            importer.hg.add(restrictionLink);

            System.out.println("Registered: "+importer.hg.getPersistentHandle(aHandle)
               + " as " + importer.resolveToUri(qualifiedName));
            
            importer.hg.getTypeSystem().addAlias(aHandle, importer.resolveToUri(qualifiedName));
        }
    } //endDefinition.

    /**
     *
     * @param attributes Attributes
     */
    public void startRestriction(Attributes attributes)
    {
        String base = attributes.getValue("base");

        /**@todo incomplete lookup, just for demo purposes*/
        int colon = base.indexOf(':');
        String s = base.substring(1 + colon);

        /**@todo HGRelXxx investigation*/
        subsumesClass = XSDPrimitiveTypeSystem.getInstance().
                        getPrimitiveClass(s);
    }

    /**
     *
     * @param attributes Attributes
     */
    public void doMinInclusive(Attributes attributes)
    {
        String value = attributes.getValue("value");

        /**@todo make the base type choice generic; see intValueExact and similar.*/
        ClusiveFacet facet = new MinInclusiveFacet();
        facet.setLimit(value);
        facets.add(facet);
    }

    /**
     *
     * @param attributes Attributes
     */
    public void doMinExclusive(Attributes attributes)
    {
        String value = attributes.getValue("value");

        ClusiveFacet facet = new MinExclusiveFacet();
        facet.setLimit(value);
        facets.add(facet);
    }

    /**
     *
     * @param attributes Attributes
     */
    public void doMaxExclusive(Attributes attributes)
    {
        String value = attributes.getValue("value");

        ClusiveFacet facet = new MaxExclusiveFacet();
        facet.setLimit(value);
        facets.add(facet);
    }

    /**
     *
     * @param attributes Attributes
     */
    public void doMaxInclusive(Attributes attributes)
    {
        String value = attributes.getValue("value");

        ClusiveFacet facet = new MaxInclusiveFacet(value);
        facets.add(facet);
    }

    /**
     *
     * @param attributes Attributes
     */
    public void doPattern(Attributes attributes)
    {
        String value = attributes.getValue("value");

        PatternFacet facet = new PatternFacet();
        facet.setValue(value);
        facets.add(facet);
    }

    /**
     *
     * @param attributes Attributes
     */
    public void doTotalDigits(Attributes attributes)
    {
        String value = attributes.getValue("value");
        TotalDigitsFacet facet = new TotalDigitsFacet();
        facet.setValue(Integer.valueOf(value));

        facets.add(facet);
    }

    /**
     *
     * @param attributes Attributes
     */
    public void doFractionDigits(Attributes attributes)
    {
        String value = attributes.getValue("value");
        FractionDigitsFacet facet = new FractionDigitsFacet();
        facet.setValue(Integer.valueOf(value));

        facets.add(facet);
    }


} //SimpleTypeImporter.
