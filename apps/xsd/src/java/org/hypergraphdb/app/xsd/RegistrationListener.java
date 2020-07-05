package org.hypergraphdb.app.xsd;

import java.net.URI;

import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.event.HGEvent;
import org.hypergraphdb.event.HGListener;

/**
 * 
 */
public class RegistrationListener implements HGListener
{

   /**
    * Registers the XSD predefined types.
    */
   public Result handle(
      HyperGraph hg, HGEvent e)
   {
      hg.getTypeSystem().addPredefinedType(SimpleTypeConstructor.HANDLE,
            SimpleTypeConstructor.INSTANCE, (URI)null);
      SimpleTypeConstructor.INSTANCE.setHyperGraph(hg);
      hg.getTypeSystem().addPredefinedType(ComplexTypeConstructor.HANDLE,
            ComplexTypeConstructor.INSTANCE, (URI)null);
      ComplexTypeConstructor.INSTANCE.setHyperGraph(hg);

      hg.getTypeSystem().addPredefinedType(ReferenceTypeCtor.HANDLE,
            ReferenceTypeCtor.INSTANCE, (URI)null);
      ComplexTypeConstructor.INSTANCE.setHyperGraph(hg);

      XSDPrimitiveTypeSystem.getInstance().importPrimitives(hg,
            "/org/hypergraphdb/app/xsd/XMLSchema.xsd");

      return Result.ok;
   }

}
