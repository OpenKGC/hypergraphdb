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

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.app.xsd.XSDPrimitiveTypeSystem;
import org.hypergraphdb.app.xsd.primitive.XSDGDay;
import org.hypergraphdb.app.xsd.primitive.XSDGMonth;
import org.hypergraphdb.app.xsd.primitive.XSDGMonthDay;
import org.hypergraphdb.app.xsd.primitive.XSDGYearMonth;
import org.hypergraphdb.app.xsd.primitive.XSDTime;

/**
 *
 */
public class Driver1
{
    private static final String DATABASELOCATION = "xsdapp";

    /**
     *
     * @param args String[]
     */
    public static void main(String[] args)
    {
        System.out.println("Executing simple unit tests...\n------\n\n");
        Driver1.exercizePrimitiveDateTime();
        Driver1.exercizePrimitiveDate();
        Driver1.exercizePrimitiveDuration();
        Driver1.exercizePrimitiveTime();
        Driver1.exercizePrimitiveString();
        Driver1.exercizePrimitiveDouble();
        Driver1.exercizePrimitiveGYear();
        Driver1.exercizePrimitiveGMonthDay();
        Driver1.exercizePrimitiveBase64Binary();
        Driver1.exercizePrimitiveBoolean();
        Driver1.exercizePrimitiveDecimal();
        Driver1.exercizePrimitiveFloat();
        Driver1.exercizePrimitiveGDay();
        Driver1.exercizePrimitiveGMonth();
        Driver1.exercizePrimitiveGYearMonth();
        Driver1.exercizePrimitiveHexBinary();
    }

    /**
     *
     */
    private static void exercizePrimitiveDateTime()
    {
        HyperGraph hg = new HyperGraph();
        long currentTime = System.currentTimeMillis();
        java.util.Date aDateTime = new java.util.Date(currentTime);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#dateTime");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD dateTime not registered with HGDB!");
            }

            HGHandle handle = hg.add(aDateTime, typeHandle);
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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            java.util.Date newDateTime=(java.util.Date)hg.get(pHandle);
            hg.remove(pHandle);

            if(0!=aDateTime.compareTo(newDateTime))
            {
                throw new RuntimeException("Initial and stored dateTimes do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveDateTime.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveDateTime.

    /**
     *
     */
    private static void exercizePrimitiveDate()
    {
        HyperGraph hg = new HyperGraph();
        long currentTime = System.currentTimeMillis();
        java.sql.Date aDate = new java.sql.Date(currentTime);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#date");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD date not registered with HGDB!");
            }

            HGHandle handle = hg.add(aDate, typeHandle);
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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            java.sql.Date newDateTime=(java.sql.Date)hg.get(pHandle);
            hg.remove(pHandle);

            if(0!=aDate.compareTo(newDateTime))
            {
                throw new RuntimeException("Initial and stored dates do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveDate.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveDate.

    /**
     *
     */
    private static void exercizePrimitiveDuration()
    {
        HyperGraph hg = new HyperGraph();
        final long duration = 24*60*60;
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#duration");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD duration not registered with HGDB!");
            }

            HGHandle handle = hg.add(duration, typeHandle);
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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            long newDuration=(Long)hg.get(pHandle);
            hg.remove(pHandle);

            if(duration != newDuration)
            {
                throw new RuntimeException("Initial and stored durations do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveDuration.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveDuration.

    /**
     *
     */
    private static void exercizePrimitiveTime()
    {
        HyperGraph hg = new HyperGraph();
        final XSDTime xsdTime = new XSDTime();
        xsdTime.setHour((byte)13);
        xsdTime.setMinute((byte)55);
        xsdTime.setSeconds(12.345F);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#time");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD time not registered with HGDB!");
            }

            HGHandle handle = hg.add(xsdTime, typeHandle);
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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            XSDTime newTime=(XSDTime)hg.get(pHandle);
            hg.remove(pHandle);

            if(!xsdTime.equals(newTime))
            {
                throw new RuntimeException("Initial and stored times do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveTime.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveTime.

    /**
     *
     */
    private static void exercizePrimitiveString()
    {
        HyperGraph hg = new HyperGraph();
        String original = "This is a test string.";
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#string");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD string not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            String retrieved=(String)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored strings do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveString.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveString.

    /**
     *
     */
    private static void exercizePrimitiveDouble()
    {
        HyperGraph hg = new HyperGraph();
        final double original = Math.PI;
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#double");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD double not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            double retrieved=(Double)hg.get(pHandle);
            hg.remove(pHandle);

            if(original != retrieved)
            {
                throw new RuntimeException("Initial and stored doubles do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveDouble.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveDouble.

    /**
     *
     */
    private static void exercizePrimitiveGYear()
    {
        HyperGraph hg = new HyperGraph();
        final int original = 1999;
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#gYear");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD gYear not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            int retrieved=(Integer)hg.get(pHandle);
            hg.remove(pHandle);

            if(original != retrieved)
            {
                throw new RuntimeException("Initial and stored gYears do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveGYear.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveGYear.

    /**
     *
     */
    private static void exercizePrimitiveGMonthDay()
    {
        HyperGraph hg = new HyperGraph();
        final XSDGMonthDay original = new XSDGMonthDay();
        original.setMonth((byte)12);
        original.setDay((byte)25);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#gMonthDay");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD gMonthDay not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            XSDGMonthDay retrieved=(XSDGMonthDay)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored gMonthDays do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveGMonthDay.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveGMonthDay.

    /**
     *
     */
    private static void exercizePrimitiveBase64Binary()
    {
        HyperGraph hg = new HyperGraph();
        /**@todo select a public base64 encoder/decord*/
        final String original = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz";
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#base64Binary");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD base64Binary not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            String retrieved=(String)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored base64Binaries do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveBase64Binary.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveBase64Binary.

    /**
     *
     */
    private static void exercizePrimitiveHexBinary()
    {
        HyperGraph hg = new HyperGraph();
        /**@todo select a public base64 encoder/decord*/
        final String original = "FECECECA1243ABCD0A0D";
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#hexBinary");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD hexBinary not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            String retrieved=(String)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored hexBinaries do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveHexBinary.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveHexBinary.

    /**
     *
     */
    private static void exercizePrimitiveBoolean()
    {
        HyperGraph hg = new HyperGraph();
        final boolean original = true;
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#boolean");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD boolean not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            boolean retrieved=(Boolean)hg.get(pHandle);
            hg.remove(pHandle);

            if(original != retrieved)
            {
                throw new RuntimeException("Initial and stored booleans do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveBoolean.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveBoolean.

    /**
     *
     */
    private static void exercizePrimitiveDecimal()
    {
        HyperGraph hg = new HyperGraph();
        final BigDecimal original = new BigDecimal(1000000000);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#decimal");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD decimal not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            BigDecimal retrieved=(BigDecimal)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored decimals do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveDecimal.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveDecimal.

    /**
     *
     */
    private static void exercizePrimitiveFloat()
    {
        HyperGraph hg = new HyperGraph();
        final float original = (float)Math.PI;
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#float");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD float not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            float retrieved=(Float)hg.get(pHandle);
            hg.remove(pHandle);

            if(original != retrieved)
            {
                throw new RuntimeException("Initial and stored floats do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveFloat.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveFloat.

    /**
     *
     */
    private static void exercizePrimitiveGDay()
    {
        HyperGraph hg = new HyperGraph();
        final XSDGDay original = new XSDGDay();
        original.setDay((byte)23);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#gDay");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD gDay not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            XSDGDay retrieved=(XSDGDay)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored gDays do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveGDay.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveGDay.

    /**
     *
     */
    private static void exercizePrimitiveGMonth()
    {
        HyperGraph hg = new HyperGraph();
        final XSDGMonth original = new XSDGMonth();
        original.setMonth((byte)11);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#gMonth");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD gMonth not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            XSDGMonth retrieved=(XSDGMonth)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored gMonths do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveGMonth.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveGMonth.

    /**
     *
     */
    private static void exercizePrimitiveGYearMonth()
    {
        HyperGraph hg = new HyperGraph();
        final XSDGYearMonth original = new XSDGYearMonth();
        original.setMonth((byte)11);
        original.setYear((short)1999);
        HGPersistentHandle pHandle = null;

        //phase1
        try
        {
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
                "http://www.w3.org/2001/XMLSchema#gYearMonth");

            if(null==typeHandle)
            {
                throw new RuntimeException("XSD gYearMonth not registered with HGDB!");
            }

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
            hg.open(Driver1.DATABASELOCATION);
            XSDPrimitiveTypeSystem.getInstance().bootstrap(hg);

            XSDGYearMonth retrieved=(XSDGYearMonth)hg.get(pHandle);
            hg.remove(pHandle);

            if(!original.equals(retrieved))
            {
                throw new RuntimeException("Initial and stored gYearMonths do not match!");
            }

            System.out.println("PASSED: exercizePrimitiveGYearMonth.");
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
            hg.close();
        }

    } //exercizePrimitiveGYearMonth.


    ///////////

    public static class A
    {
        private String field2;
        private String field1;
        private org.hypergraphdb.app.xsd.test.Driver1.B field3;

        public A()
        {

        }

        public String getField1()
        {
            return field1;
        }

        public String getField2()
        {
            return field2;
        }

        public org.hypergraphdb.app.xsd.test.Driver1.B getField3()
        {
            return field3;
        }

        public void setField1(String field1)
        {
            this.field1 = field1;
        }

        public void setField2(String field2)
        {
            this.field2 = field2;
        }

        public void setField3(org.hypergraphdb.app.xsd.test.Driver1.B field3)
        {
            this.field3 = field3;
        }

    }

    public static class B
    {
        private String field1;

        public B()
        {

        }

        public String getField1()
        {
            return field1;
        }

        public void setField1(String field1)
        {
            this.field1 = field1;
        }

    }

} //Driver1.