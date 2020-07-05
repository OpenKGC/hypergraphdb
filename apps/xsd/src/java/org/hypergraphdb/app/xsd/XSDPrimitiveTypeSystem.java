/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HGValueLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.app.xsd.FacetsDescriptor.Cardinality;
import org.hypergraphdb.app.xsd.facet.EnumerationFacet;
import org.hypergraphdb.app.xsd.facet.FractionDigitsFacet;
import org.hypergraphdb.app.xsd.facet.LengthFacet;
import org.hypergraphdb.app.xsd.facet.MaxExclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MaxInclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MaxLengthFacet;
import org.hypergraphdb.app.xsd.facet.MinExclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MinInclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MinLengthFacet;
import org.hypergraphdb.app.xsd.facet.PatternFacet;
import org.hypergraphdb.app.xsd.facet.TotalDigitsFacet;
import org.hypergraphdb.app.xsd.facet.WhiteSpaceFacet;
import org.hypergraphdb.app.xsd.primitive.XSDBase64BinaryPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDBooleanPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDDatePrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDDateTimePrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDDecimalPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDDoublePrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDDurationPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDFloatPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDGDayPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDGMonthDayPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDGMonthPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDGYearMonthPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDGYearPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDHexBinaryPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDStringPrimitive;
import org.hypergraphdb.app.xsd.primitive.XSDTimePrimitive;
import org.hypergraphdb.atom.HGSubsumes;
import org.hypergraphdb.event.HGListenerAtom;
import org.hypergraphdb.event.HGOpenedEvent;
import org.hypergraphdb.query.AtomTypeCondition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Registers the XSD primitive types. Uses an XMLSchema.xsd to import them as
 * HGDB types. It also statically defines some rules and relationships regarding
 * the types.
 */
public class XSDPrimitiveTypeSystem extends DefaultHandler
{
   private static final XSDPrimitiveTypeSystem instance = new XSDPrimitiveTypeSystem();
   private final Map<String, Class<?>> primitiveNames = new HashMap<String, Class<?>>();
   private final Map<String, Class<?>> constrainingFacets = new HashMap<String, Class<?>>();
   private final Map<String, Object> fundamentalFacets = new HashMap<String, Object>();

   private XMLNamespace namespace;
   private HyperGraph hg;
   private FacetsDescriptorBase facets;
   private HGHandle theHandle;
   private boolean registering;
   private Collection<String> facetClasses;

   /**
    * 
    * @param hg
    *           HyperGraph
    */
   private XSDPrimitiveTypeSystem()
   {
      initializeTables();
   }

   /**
    * 
    * @param name
    *           String
    * @return Class
    */
   public Class<?> getPrimitiveClass(
      final String name)
   {
      return primitiveNames.get(name);
   }

   /**
    * 
    * @param hg
    *           HyperGraph
    */
   public void bootstrap(
      final HyperGraph hg)
   {
      /** @todo this registration routine belongs to somewhere else. */
      HGListenerAtom regListener = new HGListenerAtom(
            "org.hypergraphdb.event.HGOpenedEvent",
            "org.hypergraphdb.app.xsd.RegistrationListener");

      boolean registered = false;
      HGSearchResult<HGHandle> rs = hg.find(new AtomTypeCondition(HGListenerAtom.class));
      while (rs.hasNext())
      {
         HGHandle lsnrHandle = rs.next();
         HGListenerAtom lsnr = (HGListenerAtom) hg.get(lsnrHandle);
         if (lsnr.getEventClassName().equals(regListener.getEventClassName())
               && lsnr.getListenerClassName().equals(regListener.getListenerClassName()))
         {
            registered = true;
         }
      }
      rs.close();

      if (!registered)
      {
         hg.add(regListener);
         // fake the open event.
         RegistrationListener l = new RegistrationListener();
         l.handle(hg, new HGOpenedEvent());
      }
   }

   /**
    * 
    */
   public static XSDPrimitiveTypeSystem getInstance()
   {
      return instance;
   }

   /**
    * Define the primitive types using the specified XMLSchema.xsd file as a
    * source.
    * 
    * @param xmlschemaFile
    *           an XMLSchema.xsd file
    */
   public void importPrimitives(
      final HyperGraph hg, String xmlschemaFile)
   {
      this.hg = hg;
      namespace = new XMLNamespace("http://www.w3.org/2001/XMLSchema");

      try
      {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         parserFactory.setValidating(false);
         parserFactory.setNamespaceAware(false);

         SAXParser parser = parserFactory.newSAXParser();
         InputStream input = getClass().getResourceAsStream(xmlschemaFile);
         parser.parse(input, this);
      } catch (Exception exception)
      {
         exception.printStackTrace();
      }
   }

   /**
    * @todo hide the sax methods into a private inner class?
    */
   public void startElement(
      String uri, String localName, String qName, Attributes attributes)
   {
      if ("xs:simpleType".equals(qName))
      {
         handleSimpleType(attributes.getValue("name"));
      } else if (registering && "hfp:hasFacet".equals(qName))
      {
         handleHasFacet(attributes.getValue("name"));
      } else if (registering && "hfp:hasProperty".equals(qName))
      {
         handleHasProperty(attributes.getValue("name"), attributes.getValue("value"));
      }
   } // startElement.

   /**
    * Start defining a new primitive type.
    * 
    * @param primitiveName
    *           String
    */
   private void handleSimpleType(
      String primitiveName)
   {
      Class<?> handler = primitiveNames.get(primitiveName);
      if (null != handler)
      {
         HGHandle aHandle = hg.getTypeSystem().getTypeHandle(
               namespace.getUri() + '#' + primitiveName);

         if (null == aHandle)
         {
            theHandle = hg.add(null, SimpleTypeConstructor.HANDLE);

            facetClasses = new ArrayList<String>();
            /**
             * @todo FacetsDescriptor to derive from HGRel so it can *really*
             *       point to the type ?
             */
            facets = new FacetsDescriptorBase();

            XSDTypeName stringName = new XSDTypeName(namespace, primitiveName);
            HGHandle stringNameHandle = hg.add(stringName);
            XSDTypeImplementation nameType = new XSDTypeImplementation(stringNameHandle,
                  theHandle);
            hg.add(nameType);

            Class<?> base = primitiveNames.get(primitiveName);
            HGHandle baseHandle = hg.add(base.getCanonicalName());
            HGSubsumes subsumesLink = new HGSubsumes(baseHandle, theHandle);
            hg.add(subsumesLink);

            hg.getTypeSystem().addAlias(theHandle,
                  namespace.getUri() + '#' + primitiveName);

            /**@todo there is similar code in the SchemaImporter (above) that does not
             * do anything..*/
            System.out.println("Registered: "+hg.getPersistentHandle(theHandle)
                  + " as " + namespace.getUri() + '#' + primitiveName);

            registering = true;
         }

         /**
          * @todo Set up the "namespace" link; in the real code that link will
          *       be queried for and then the new atom type will be added to its
          *       target list.
          */
         // HGValueLink xsdNamespace = new HGValueLink(namespace, new HGHandle[]
         // {aHandle/*, anHandle*/});
         // HGHandle namespaceHandle = hg.add(namespace);
      }
   }

   /**
    * 
    */
   private void handleHasFacet(
      String facetName)
   {
      Class<?> aClass = constrainingFacets.get(facetName);

      if (null != aClass)
      {
         facetClasses.add(aClass.getCanonicalName());
      }
   }

   /**
    * 
    */
   private void handleHasProperty(
      String name, String value)
   {
      /** @todo not implemented yet. */
      facets.setOrdered(false);
      facets.setBounded(false);
      facets.setCardinality(Cardinality.COUNTABLY_INFINITE);
      facets.setNumeric(false);
   }

   /**
    * Completes the registration of a primitive type and resets the members that
    * are involved in the process.
    */
   public void endElement(
      String url, String localName, String qName) throws SAXException
   {
      if ("xs:simpleType".equals(qName) && registering)
      {
         registering = false;

         facets.setSupportedFacets(facetClasses.toArray(new String[] {}));

         HGValueLink restrictionLink = new HGValueLink(facets, new HGHandle[]
         { theHandle });
         try
         {
            hg.add(restrictionLink);
         } catch (Exception ex)
         {
            ex.printStackTrace();
         }

         facetClasses = null;
         facets = null;
         theHandle = null;
      }
   } // endElement.

   /**
    * 
    */
   private void initializeTables()
   {
      primitiveNames.put("string", XSDStringPrimitive.class);
      primitiveNames.put("boolean", XSDBooleanPrimitive.class);
      primitiveNames.put("decimal", XSDDecimalPrimitive.class);
      primitiveNames.put("float", XSDFloatPrimitive.class);
      primitiveNames.put("double", XSDDoublePrimitive.class);
      primitiveNames.put("duration", XSDDurationPrimitive.class);
      primitiveNames.put("dateTime", XSDDateTimePrimitive.class);
      primitiveNames.put("time", XSDTimePrimitive.class);
      primitiveNames.put("date", XSDDatePrimitive.class);
      primitiveNames.put("gYearMonth", XSDGYearMonthPrimitive.class);
      primitiveNames.put("gYear", XSDGYearPrimitive.class);
      primitiveNames.put("gMonthDay", XSDGMonthDayPrimitive.class);
      primitiveNames.put("gDay", XSDGDayPrimitive.class);
      primitiveNames.put("gMonth", XSDGMonthPrimitive.class);
      primitiveNames.put("hexBinary", XSDHexBinaryPrimitive.class);
      primitiveNames.put("base64Binary", XSDBase64BinaryPrimitive.class);
      /** @todo what about these? */
      // primitiveNames.put("anyURI", ?);
      // primitiveNames.put("QName", ?);
      // primitiveNames.put("NOTATION", ?);
      constrainingFacets.put("length", LengthFacet.class);
      constrainingFacets.put("minLength", MinLengthFacet.class);
      constrainingFacets.put("maxLength", MaxLengthFacet.class);
      constrainingFacets.put("pattern", PatternFacet.class);
      constrainingFacets.put("enumeration", EnumerationFacet.class);
      constrainingFacets.put("whiteSpace", WhiteSpaceFacet.class);
      constrainingFacets.put("maxInclusive", MaxInclusiveFacet.class);
      constrainingFacets.put("maxExclusive", MaxExclusiveFacet.class);
      constrainingFacets.put("minExclusive", MinExclusiveFacet.class);
      constrainingFacets.put("minInclusive", MinInclusiveFacet.class);
      constrainingFacets.put("totalDigits", TotalDigitsFacet.class);
      constrainingFacets.put("fractionDigits", FractionDigitsFacet.class);

      fundamentalFacets.put("equal", new Object());
      fundamentalFacets.put("ordered", new Object());
      fundamentalFacets.put("bounded", new Object());
      fundamentalFacets.put("cardinality", new Object());
      fundamentalFacets.put("numeric", new Object());
   }

} // TypeRegistrar.
