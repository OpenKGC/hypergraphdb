/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import java.io.UnsupportedEncodingException;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.handle.UUIDHandleFactory;
import org.hypergraphdb.type.HGAtomTypeBase;

public class ReferenceTypeCtor extends HGAtomTypeBase
{
   public static final HGPersistentHandle HANDLE = 
	   UUIDHandleFactory.I.makeHandle("ba19fe3c-5b32-11db-82a1-a78cc7527afc");
   public static final ReferenceTypeCtor INSTANCE = new ReferenceTypeCtor();

   public Object make(
      HGPersistentHandle handle, LazyRef<HGHandle[]> targetSet,
      IncidenceSetRef incidenceSet)
   {
      ReferenceType result = null;

      byte[] bytes = graph.getStore().getData(handle);
      try
      {
         String actualType = new String(bytes, "UTF-8");
         result = new ReferenceType(actualType);
      } catch (UnsupportedEncodingException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return result;
   }

   public void release(
      HGPersistentHandle handle)
   {
   }

   public HGPersistentHandle store(
      Object instance)
   {
      HGPersistentHandle result = graph.getHandleFactory().nullHandle();

      if (null != instance)
      {
         ReferenceType referenceType = (ReferenceType) instance;

         try
         {
            byte[] bytes = referenceType.getActualType().getBytes("UTF-8");
            result = graph.getStore().store(bytes);
         } catch (UnsupportedEncodingException e)
         {
            e.printStackTrace();
         }
      }
      
      return result;
   }

}