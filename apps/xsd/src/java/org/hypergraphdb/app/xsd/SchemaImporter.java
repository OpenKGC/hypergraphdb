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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.ComparisonOperator;
import org.hypergraphdb.query.HGQueryCondition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Capable of importing a subset of simpleTypes.
 */
public class SchemaImporter extends DefaultHandler
{
   /* package */HyperGraph hg;
   private String targetNamespace;
   private final Map<String, String> importedNamespaces = new HashMap<String, String>();
   private String schemaFile;

   private ComplexTypeImporter complexType;
   private SimpleTypeImporter simpleType;

   /**
    * 
    * @param uri
    *           String
    */
   public void importSchema(
      String schemaFile)
   {
      HGQueryCondition q = HGQuery.hg.value(schemaFile, ComparisonOperator.EQ);
      HGSearchResult<HGHandle> rs = hg.find(q);

      if (!rs.hasNext())
      {
         this.schemaFile = schemaFile;

         try
         {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating(false);
            parserFactory.setNamespaceAware(false);

            SAXParser parser = parserFactory.newSAXParser();
            InputStream is = getClass().getResourceAsStream(schemaFile);
            parser.parse(is, this);
         } catch (Exception exception)
         {
            exception.printStackTrace();
         }
      }

      rs.close();
   }

   /**
    * 
    */
   public SchemaImporter(HyperGraph hg)
   {  
      this.hg = hg;
   }

   /**
    * 
    */
   public void startElement(
      String uri, String localName, String qName, Attributes attributes)
   {
      String resolvedName = resolveUri(qName);

      if (resolvedName.contains(":schema"))
      {
         targetNamespace = attributes.getValue("targetNamespace");

         for (int i = 0; i < attributes.getLength(); i++)
         {
            String name = attributes.getQName(i);

            final String prefix = "xmlns";
            if (name.startsWith(prefix))
            {
               name = name.substring(prefix.length());

               if (0 == name.length())
               {
                  // the default namespace.
                  name = "";
               } else if (':' == name.charAt(0))
               {
                  name = name.substring(1);
               } else
               {
                  throw new RuntimeException("Unrecognized namespace definition: "
                        + attributes.getQName(i) + '.');
               }

               importedNamespaces.put(name, attributes.getValue(i));
            }
         }
      } else if ("http://www.w3.org/2001/XMLSchema#simpleType".equals(resolvedName))
      {
         simpleType = new SimpleTypeImporter(this);
         simpleType.startDefinition(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#complexType".equals(resolvedName))
      {
         complexType = new ComplexTypeImporter(this);
         complexType.startDefinition(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#sequence".equals(resolvedName))
      {
         complexType.startSequence(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#element".equals(resolvedName))
      {
         if(null==complexType)
         {
            complexType = new ComplexTypeImporter(this);
         }
         /**@todo global elements are special case.*/
         complexType.startElement(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#attribute".equals(resolvedName))
      {
         complexType.doAttribute(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#restriction".equals(resolvedName))
      {
         simpleType.startRestriction(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#minInclusive".equals(resolvedName))
      {
         simpleType.doMinInclusive(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#minExclusive".equals(resolvedName))
      {
         simpleType.doMinExclusive(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#maxInclusive".equals(resolvedName))
      {
         simpleType.doMaxInclusive(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#maxExclusive".equals(resolvedName))
      {
         simpleType.doMaxExclusive(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#pattern".equals(resolvedName))
      {
         simpleType.doPattern(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#totalDigits".equals(resolvedName))
      {
         simpleType.doTotalDigits(attributes);
      } else if ("http://www.w3.org/2001/XMLSchema#fractionDigits".equals(resolvedName))
      {
         simpleType.doFractionDigits(attributes);
      }

   } // startElement.

   /**
    * 
    */
   public void endElement(
      String url, String localName, String qName) throws SAXException
   {
      String resolvedName = resolveUri(qName);

      if (resolvedName.contains(":schema"))
      {
         /** @todo write the timestamp also to do updates? */
         /** @todo what about an "unimport" operation? */
         hg.add(schemaFile);

      } else if ("http://www.w3.org/2001/XMLSchema#simpleType".equals(resolvedName))
      {
         simpleType.endDefinition();
         simpleType = null;
      } else if ("http://www.w3.org/2001/XMLSchema#complexType".equals(resolvedName))
      {
         complexType.endDefinition();
         complexType = null;
      }

   } // endElement.

   /**
    * 
    * @param e
    *           SAXParseException
    */
   public void error(
      SAXParseException e)
   {
      e.printStackTrace();
   }

   /**
    * 
    * @param e
    *           SAXParseException
    */
   public void warning(
      SAXParseException e)
   {
      e.printStackTrace();
   }

   /**
    * 
    * @param e
    *           SAXParseException
    */
   public void fatalError(
      SAXParseException e)
   {
      e.printStackTrace();
   }

   /**
    * 
    * @param qName
    *           String
    * @return String
    */
   /* package */String resolveToUri(
      String qName)
   {
      /** @todo supply the implementation */

      int colon = qName.indexOf(':');
      String localName = qName.substring(1 + colon);

      return targetNamespace + '#' + localName;
   }

   /**
    * 
    * @param qName
    *           String
    * @return String
    */
   public String resolveUri(
      String qName)
   {
      String result = qName;
      int colon = qName.indexOf(':');

      if (-1 != colon)
      {
         String localName = qName.substring(1 + colon);
         String ns = qName.substring(0, colon);

         String uri = importedNamespaces.get(ns);

         if (null == uri)
         {
            System.out.println("No namespace URI registered for " + qName + '.');
         }
         else
         {
            result = uri + '#' + localName;
         }
      }

      return result;
   }
} // SchemaImporter.
