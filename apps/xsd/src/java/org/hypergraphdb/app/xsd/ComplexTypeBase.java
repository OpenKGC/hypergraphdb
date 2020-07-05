/*
 * This file is part of the HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;

public class ComplexTypeBase implements HGAtomType
{
   protected HyperGraph hg;

   public ComplexTypeBase()
   {  
   }
      
   public Object make(
      HGPersistentHandle handle, LazyRef<HGHandle[]> targetSet,
      IncidenceSetRef incidenceSet)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void release(
      HGPersistentHandle handle)
   {
      // TODO Auto-generated method stub

   }

   public HGPersistentHandle store(
      Object instance)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean subsumes(
      Object general, Object specific)
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void setHyperGraph(
      HyperGraph hg)
   {
      this.hg = hg;
   }

}
