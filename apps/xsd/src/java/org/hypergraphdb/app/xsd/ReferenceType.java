/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGHandleFactory;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.HGAtomTypeBase;

/**
 * Type that refers to another type, resolved on use. Used to represent global
 * elements, ie : <xsd:element name="purchaseOrder" type="PurchaseOrderType"/>.
 * "purchaseOrder" is the reference type and "PurchaseOrderType" is the actual
 * type referred to.
 */
public class ReferenceType extends HGAtomTypeBase
{
   /** @todo namespaces and stuff. */
   private String actualType;
   private HGAtomType type;

   
   public ReferenceType(String actualType)
   {
      this.actualType = actualType;
   }

   public String getActualType()
   {
      return actualType;
   }
   
   public void setActualType(String actualType)
   {
      this.actualType=actualType;
   }
   
   public Object make(
      HGPersistentHandle handle, LazyRef<HGHandle[]> targetSet,
      IncidenceSetRef incidenceSet)
   {
      if (null == type)
      {
         type = graph.getTypeSystem().getType(actualType);
      }

      return type.make(handle, targetSet, incidenceSet);
   }

   public void release(
      HGPersistentHandle handle)
   {
      if (null == type)
      {
         type = graph.getTypeSystem().getType(actualType);
      }

      type.release(handle);
   }

   public HGPersistentHandle store(
      Object instance)
   {
      if (null == type)
      {
         type = graph.getTypeSystem().getType(actualType);
      }

      return type.store(instance);
   }

} // ReferenceType.