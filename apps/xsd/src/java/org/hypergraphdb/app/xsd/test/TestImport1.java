/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd.test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.app.xsd.RestrictionViolationException;
import org.hypergraphdb.app.xsd.SchemaImporter;
import org.hypergraphdb.app.xsd.XSDPrimitiveTypeSystem;

/**
 *
 */
public class TestImport1
{
    private static final String DATABASELOCATION = "xsdapp";

    /**
     *
     * @param args String[]
     */
    public static void main(String[] args)
    {
        System.out.println("Executing complex type tests...\n------\n\n");

        TestImport1.exercizeImportSchema3();
        
//        TestImport1.exercizeImportSchema();
//
//        TestImport1.testComplexLength3();
//        TestImport1.testComplexUsAddress();
//        TestImport1.testPurchaseOrder();
//        TestImport1.exercizeImportSchema2();
//        TestImport1.exercizeDecimalMinInclusive();
//        TestImport1.exercizeDecimalMaxInclusive();
//        TestImport1.exercizeDecimalMinExclusive();
//        TestImport1.exercizeDecimalMaxExclusive();
//        TestImport1.exercizeDecimalTotalDigits();
//        TestImport1.exercizeDecimalFractionDigits();
    }

    /**
     * Importing a simple derivation of xs:string.
     */
    private static void exercizeImportSchema()
    {
        HGPersistentHandle pHandle = null;
        HyperGraph hg = new HyperGraph();
        final String original = "1024";

        //phase1
        try
        {
            hg.open(TestImport1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            SchemaImporter importer = new SchemaImporter(hg);
            importer.importSchema("/org/hypergraphdb/app/xsd/test/test1.xsd");

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#SKU");
            HGHandle handle = hg.add(original, typeHandle);
            pHandle = hg.getPersistentHandle(handle);
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

        //phase2
        try
        {
            hg = new HyperGraph();
            hg.open(TestImport1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            SchemaImporter importer = new SchemaImporter(hg);
            importer.importSchema("/org/hypergraphdb/app/xsd/test/test1.xsd");

            String retrieved = (String) hg.get(pHandle);
            hg.remove(pHandle);

            if (!original.equals(retrieved))
            {
                throw new RuntimeException(
                    "Initial and stored SKUs do not match!");
            }
            
            retrieved=null;

            System.out.println("PASSED: exercizeImportSchema.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizeImportSchema.


    /**
     * Store and retrieve a file:///c:/kobrix/test1.xsd#myInteger.
     */
    private static void exercizeImportSchema2()
    {
        HGPersistentHandle pHandle = null;
        HyperGraph hg = new HyperGraph();
        /**@todo make the integer types work with ints, bytes, shorts.*/
        final BigDecimal original = new BigDecimal(14000);

        //phase1
        try
        {
            hg.open(TestImport1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            SchemaImporter importer = new SchemaImporter(hg);
            importer.importSchema("/org/hypergraphdb/app/xsd/test/test1.xsd");

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger");
            HGHandle handle = hg.add(original, typeHandle);
            pHandle = hg.getPersistentHandle(handle);
        } finally
        {
            hg.close();
        }

        //phase2
        try
        {
            hg.open(TestImport1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            SchemaImporter importer = new SchemaImporter(hg);
            importer.importSchema("/org/hypergraphdb/app/xsd/test/test1.xsd");

            BigDecimal retrieved = (BigDecimal) hg.get(pHandle);
            hg.remove(pHandle);

            if (!original.equals(retrieved))
            {
                throw new RuntimeException(
                    "Initial and stored myIntegers do not match!");
            }

            System.out.println("PASSED: exercizeImportSchema2.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizeImportSchema2.

    
    /**
     * Importing a more complex schema - purchase order (po.xsd). Includes global element definition.
     */
    private static void exercizeImportSchema3()
    {
        HGPersistentHandle pHandle = null;
        HyperGraph hg = new HyperGraph();

        //phase1
        try
        {
            hg.open(TestImport1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            SchemaImporter importer = new SchemaImporter(hg);
            importer.importSchema("/org/hypergraphdb/app/xsd/test/po.xsd");

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#purchaseOrder");
            
            HGHandle handle = hg.add(new Object(), typeHandle);
            pHandle = hg.getPersistentHandle(handle);
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

        //phase2
        try
        {
            hg = new HyperGraph();
            hg.open(TestImport1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            SchemaImporter importer = new SchemaImporter(hg);
            importer.importSchema("/org/hypergraphdb/app/xsd/test/po.xsd");

            String retrieved = (String) hg.get(pHandle);
            hg.remove(pHandle);
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizeImportSchema3.


    /**
     * Work with a file:///c:/kobrix/test1.xsd#myInteger's that are at the ends of the range of
     * allowed values.
     */
    private static void exercizeDecimalMinInclusive()
    {
        HyperGraph hg = null;
        BigDecimal original = new BigDecimal(9999);

        //expected to fail the store operation.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger");
            hg.add(original, typeHandle);
        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            }
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(10001);
        //should pass.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(10000);
        //should pass.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);

            System.out.println("PASSED: exercizeDecimalMinInclusive.");
        } finally
        {
            hg.close();
        }

    } //exercizeDecimalMinInclusive.

    /**
     * Work with a file:///c:/kobrix/test1.xsd#myInteger's that are at the ends of the range of
     * allowed values.
     */
    private static void exercizeDecimalMaxInclusive()
    {
        HyperGraph hg = null;
        BigDecimal original = new BigDecimal(100000);

        //expected to fail the store operation.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger");
            hg.add(original, typeHandle);
        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            }
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(99999);
        //should pass.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(100001);
        //expected to fail the store operation.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger");
            hg.add(original, typeHandle);

        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            } else
            {
                System.out.println("PASSED: exercizeDecimalMaxInclusive.");
            }
        } finally
        {
            hg.close();
        }

    } //exercizeDecimalMaxInclusive.

    /**
     * Work with a file:///c:/kobrix/test1.xsd#myInteger1's that are at the ends of the range of
     * allowed values.
     */
    private static void exercizeDecimalMinExclusive()
    {
        HyperGraph hg = null;
        BigDecimal original = new BigDecimal(999);

        //expected to fail the store operation.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger1");
            hg.add(original, typeHandle);
        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            }
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(1001);
        //should pass.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger1");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(1000);
        //should fail.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger1");
            hg.add(original, typeHandle);

            System.out.println("PASSED: exercizeDecimalMinExclusive.");
        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            }
        } finally
        {
            hg.close();
        }
    } //exercizeDecimalMinExclusive.

    /**
     * Work with a file:///c:/kobrix/test1.xsd#myInteger1's that are at the ends of the range of
     * allowed values.
     */
    private static void exercizeDecimalMaxExclusive()
    {
        HyperGraph hg = null;
        BigDecimal original = new BigDecimal(10000);

        //expected to fail the store operation.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger1");
            hg.add(original, typeHandle);
        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            }
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(9998);
        //should pass.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger1");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        original = new BigDecimal(9999);
        //expected to fail the store operation.
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#myInteger1");
            hg.add(original, typeHandle);

        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            } else
            {
                System.out.println("PASSED: exercizeDecimalMaxExclusive.");
            }
        } finally
        {
            hg.close();
        }

    } //exercizeDecimalMaxExclusive.

    /**
     * Works with a file:///c:/kobrix/test1.xsd#money's that are at the ends of the range of
     * allowed values.
     */
    private static void exercizeDecimalTotalDigits()
    {
        HyperGraph hg = null;

        //should pass.
        BigDecimal original = new BigDecimal("922337.5807");
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#money");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        //should pass.
        original = new BigDecimal("-922337.5808");
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#money");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        //should fail.
        original = new BigDecimal("99999999999");
        try
        {
            hg = TestImport1.setup();

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#money");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            } else
            {
                System.out.println("PASSED: exercizeDecimalTotalDigits.");
            }
        } finally
        {
            hg.close();
        }

    } //exercizeDecimalTotalDigits.

    /**
     * Works with a file:///c:/kobrix/test1.xsd#money's that are at the ends of the range of
     * allowed values.
     */
    private static void exercizeDecimalFractionDigits()
    {
        HyperGraph hg = null;

        //should pass.
        BigDecimal original = new BigDecimal("922337.5807");
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#money");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        //should pass.
        original = new BigDecimal("-922337.5808");
        try
        {
            hg = TestImport1.setup();

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#money");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } finally
        {
            hg.close();
        }

        //should fail.
        original = new BigDecimal("999999.99999");
        try
        {
            hg = TestImport1.setup();

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "file:///c:/kobrix/test1.xsd#money");
            HGHandle handle = hg.add(original, typeHandle);
            hg.remove(handle);
        } catch (RuntimeException t)
        {
            if (!(t instanceof RestrictionViolationException))
            {
                throw t;
            } else
            {
                System.out.println("PASSED: exercizeDecimalFractionDigits.");
            }
        } finally
        {
            hg.close();
        }

    } //exercizeDecimalFractionDigits.

    
    /**
     * A complex type similar to length3 but with more xs:string elements.
     */
    @SuppressWarnings("unchecked")
    private static void testComplexUsAddress()
    {
        HGPersistentHandle pHandle = null;
        HyperGraph hg = new HyperGraph();
        
        String name = "tx";
        String street = "royal";
        String city = "irving";

        //phase1
        try
        {
            hg.open(TestImport1.DATABASELOCATION);

            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            SchemaImporter importer = new SchemaImporter(hg);
            importer.importSchema("/org/hypergraphdb/app/xsd/test/testcomplex1.xsd");

            //use of the imported types.
            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle("USAddress");
            
            Map<String,Object> address = new HashMap<String,Object>();
            address.put("name", name);
            address.put("street", street);
            address.put("city", city);
            
            HGHandle handle = hg.add(address, typeHandle);

            pHandle = hg.getPersistentHandle(handle);
        } finally
        {
            hg.close();
        }

        //phase2
        try
        {
            hg.open(TestImport1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            Map<String,Object> address = (Map<String,Object>)hg.get(pHandle);

            String s = (String)address.get("name");
            if(false==s.equals(name))
            {
               throw new RuntimeException("Initial and stored names do not match!");
            }

            s = (String)address.get("street");
            if(false==s.equals(street))
            {
               throw new RuntimeException("Initial and stored streets do not match!");
            }

            s = (String)address.get("city");
            if(false==s.equals(city))
            {
               throw new RuntimeException("Initial and stored cities do not match!");
            }

            System.out.println("PASSED: testComplexUsAddress.");
        } finally
        {
            hg.close();
        }

    } //testComplexUsAddress.

    
   /**
    * Importing a composite complex type that has couple of members with type of USAddress.
    */
   @SuppressWarnings("unchecked")
   private static void testPurchaseOrder()
   {
       HGPersistentHandle pHandle = null;
       HyperGraph hg = new HyperGraph();
       
       String name1 = "tx";
       String street1 = "royal";
       String city1 = "irving";

       String name2 = "name2";
       String street2 = "street2";
       String city2 = "city2";

       //phase1
       try
       {
           hg.open(TestImport1.DATABASELOCATION);

           XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

           SchemaImporter importer = new SchemaImporter(hg);
           importer.importSchema("/org/hypergraphdb/app/xsd/test/testcomplex1.xsd");

           //use of the imported types.
           HGHandle typeHandle = hg.getTypeSystem().getTypeHandle("PurchaseOrderType");
           
           Map<String,Object> shipToAddress = new HashMap<String,Object>();
           shipToAddress.put("name", name1);
           shipToAddress.put("street", street1);
           shipToAddress.put("city", city1);
           
           Map<String,Object> billToAddress = new HashMap<String,Object>();
           billToAddress.put("name", name2);
           billToAddress.put("street", street2);
           billToAddress.put("city", city2);

           Map<String,Object> purchaseOrder = new HashMap<String,Object>();
           purchaseOrder.put("shipTo", shipToAddress);
           purchaseOrder.put("billTo", billToAddress);
           purchaseOrder.put("product", "cheese");
           
           HGHandle handle = hg.add(purchaseOrder, typeHandle);

           pHandle = hg.getPersistentHandle(handle);
       } finally
       {
           hg.close();
       }

       //phase2
       try
       {
           hg.open(TestImport1.DATABASELOCATION);
           XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

           Map<String,Object> purchaseOrder = (Map<String,Object>)hg.get(pHandle);
           
           Map<String,Object> shipTo = (Map<String,Object>)purchaseOrder.get("shipTo");
           String s = (String)shipTo.get("name");
           if(false==s.equals(name1))
           {
              throw new RuntimeException("Initial and stored names for shipTo do not match!");
           }
           s = (String)shipTo.get("street");
           if(false==s.equals(street1))
           {
              throw new RuntimeException("Initial and stored streets for shipTo do not match!");
           }
           s = (String)shipTo.get("city");
           if(false==s.equals(city1))
           {
              throw new RuntimeException("Initial and stored cities for shipTo do not match!");
           }
           
           Map<String,Object> billTo = (Map<String,Object>)purchaseOrder.get("billTo");
           s = (String)billTo.get("name");
           if(false==s.equals(name2))
           {
              throw new RuntimeException("Initial and stored names for billTo do not match!");
           }
           s = (String)billTo.get("street");
           if(false==s.equals(street2))
           {
              throw new RuntimeException("Initial and stored streets for billTo do not match!");
           }
           s = (String)billTo.get("city");
           if(false==s.equals(city2))
           {
              throw new RuntimeException("Initial and stored cities for billTo do not match!");
           }
           
           s = (String)purchaseOrder.get("product");
           if(false=="cheese".equals(s))
           {
              throw new RuntimeException("Initial and stored products do not match!");
           }
           
           System.out.println("PASSED: testPurchaseOrder.");
       } finally
       {
           hg.close();
       }
   } //testPurchaseOrder.
   

   /**
    * Importing a complexType that has a sequence of one xs:decimal and one xs:string members.
    */
   @SuppressWarnings("unchecked")
   private static void testComplexLength3()
   {
       HGPersistentHandle pHandle = null;
       HyperGraph hg = new HyperGraph();
       
       BigDecimal size = new BigDecimal("1024");
       String unit = "miles";

       //phase1
       try
       {
           hg.open(TestImport1.DATABASELOCATION);

           XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

           SchemaImporter importer = new SchemaImporter(hg);
           importer.importSchema("/org/hypergraphdb/app/xsd/test/testcomplex1.xsd");

           //use of the imported types.
           HGHandle typeHandle = hg.getTypeSystem().getTypeHandle("length3");
           
           Map<String,Object> someLength = new HashMap<String,Object>();
           someLength.put("size", size);
           someLength.put("unit", unit);
                       
           HGHandle handle = hg.add(someLength, typeHandle);

           pHandle = hg.getPersistentHandle(handle);
       } finally
       {
           hg.close();
       }

       //phase2
       try
       {
           hg.open(TestImport1.DATABASELOCATION);
           XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

           Map<String,Object> someLength = (Map<String,Object>)hg.get(pHandle);

           BigDecimal bd = (BigDecimal)someLength.get("size");
           if(0 != size.compareTo(bd))
           {
              throw new RuntimeException("Initial and stored sizes do not match!");
           }

           String s = (String)someLength.get("unit");
           if(false==s.equals(unit))
           {
              throw new RuntimeException("Initial and stored units do not match!");
           }

           System.out.println("PASSED: testComplexLength3.");
       } finally
       {
           hg.close();
       }

   } //testComplexLength3.

   ////////////////////////////
    // Private Helper Methods //
    ////////////////////////////

    /**
     *
     */
    private static HyperGraph setup()
    {
        HyperGraph result = new HyperGraph();

        result.open(TestImport1.DATABASELOCATION);
        XSDPrimitiveTypeSystem.getInstance().bootstrap(result);

        SchemaImporter importer = new SchemaImporter(result);
        importer.importSchema("/org/hypergraphdb/app/xsd/test/test1.xsd");

        return result;
    }

} //TestImport1.
