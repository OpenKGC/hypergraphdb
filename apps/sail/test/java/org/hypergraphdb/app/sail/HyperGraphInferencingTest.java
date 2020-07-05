package org.hypergraphdb.app.sail;

import org.openrdf.sail.InferencingTest;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import junit.framework.Test;
import junit.framework.TestCase;

public class HyperGraphInferencingTest
{
    static Sail createSail() throws SailException
    {
        final Sail sail = new HyperGraphStore("/tmp/hyperStore.sail.inference");

        sail.initialize();

        final SailConnection conn = sail.getConnection();

        try
        {
            conn.clear();
            conn.clearNamespaces();
            conn.commit();
        }
        finally
        {
            conn.close();
        }

        return sail;
    }
    
    public static Test suite() 
    {
        try
        {
            return InferencingTest.suite(new HyperGraphStore("/tmp/hyperStore.sail.inference"), 
                                         "HGDB Sail Inference Tests");
        }
        catch (Throwable ex) 
        {
            throw new RuntimeException(ex);
        }
    }
}