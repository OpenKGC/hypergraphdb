package org.hypergraphdb.app.sail;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConcurrencyTest;
import org.openrdf.sail.SailException;

public class HyperGraphSailConcurrencyTest extends SailConcurrencyTest
{
    public HyperGraphSailConcurrencyTest()    
    {
        super("HGDB Sail Concurrency Test");
    }
    
    @Override
    protected Sail createSail() throws SailException
    {
        return new HyperGraphStore("/tmp/hyperStore.sail");
    }
}
