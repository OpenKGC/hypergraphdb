package org.hypergraphdb.app.sail;

import org.hypergraphdb.*;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.app.sail.exceptions.HyperGraphException;
import org.hypergraphdb.app.sail.model.*;
import org.hypergraphdb.indexing.ByPartIndexer;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailBase;

/**
 * created Jan 29, 2010 - 1:18:20 PM
 * 
 * @author IanHolsman Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperGraphStore extends SailBase
{
    private String GraphLocation;
    private HyperGraph graph;

    private final ValueFactory valueFactory = new HyperValueFactory(this);

    public HyperGraphStore()
    {
        super();
    }

    /**
     * Creates a new HyperGraph RDF Store using the provided database
     * connection.
     * 
     * @param location
     *            location of a DataSource
     */
    public HyperGraphStore(String location)
    {
        this.GraphLocation = location;
    }

    public HyperGraph getGraph()
    {
        return graph;
    }

    @Override
    protected void shutDownInternal() throws SailException
    {
    }

    @Override
    protected SailConnection getConnectionInternal() throws SailException
    {
        return new HyperGraphConnection(this);
    }

    public boolean isWritable() throws SailException
    {
        return true;
    }

    public ValueFactory getValueFactory()
    {
        return valueFactory;
    }

    protected void initializeInternal() throws SailException
    {
        if (graph == null)
        {
            try
            {
                graph = HGEnvironment.get(GraphLocation);
                // TODO
                this.createIndices();
            }
            /*
             * catch (SailException e) { throw e; }
             */
            catch (Exception e)
            {
                throw new HyperGraphException(e);
            }
        }
        // factory.setMaxNumberOfTripleTables(maxTripleTables);
        // factory.setTriplesIndexed(triplesIndexed);
        // factory.setSequenced(sequenced);
        // factory.init();
    }

    private void createIndex(HGTypeSystem ts, Class c, String alias,
                             String field)
    {

        HGHandle typeHandle;
        HGPersistentHandle typeH;
        HGHandle check;
        typeHandle = ts.getTypeHandle(c);
        typeH = graph.getPersistentHandle(typeHandle);
        check = ts.getTypeHandle(alias);
        if (check == null)
            ts.addAlias(typeH, alias);
        else if (!check.equals(typeH))
            throw new RuntimeException("Alias already in use: " + alias
                    + ", please change " + alias + " prefix.");
        graph.getIndexManager().register(new ByPartIndexer(typeH,
                new String[] { field }));

    }

    private void createIndices()
    {

        HGTypeSystem ts = graph.getTypeSystem();

        createIndex(ts, HValue.class, "sail_Value", "val");
        createIndex(ts, HLiteral.class, "sail_Literal", "val");
        createIndex(ts, HURI.class, "sail_URI", "localName");
        createIndex(ts, HyperNameSpace.class, "sail_NamesSpace", "prefix");
        // createIndex(ts,HyperContext.class,"sail_Context","context");
    }

    public HGHandle lookupNameSpacePrefix(String prefix)
    {
        HGHandle typeHandle = graph.getTypeSystem()
                .getTypeHandle("sail_NamesSpace");
        ByPartIndexer byProperty = new ByPartIndexer(typeHandle,
                new String[] { "prefix" });
        HGIndex<Object, HGPersistentHandle> index = graph.getIndexManager()
                .getIndex(byProperty);
        return (HGPersistentHandle) index.findFirst(prefix);

    }

    public HGHandle lookupStatementSubject(String subject)
    {
        HGHandle typeHandle = graph.getTypeSystem()
                .getTypeHandle("sail_ContextStatement");
        ByPartIndexer byProperty = new ByPartIndexer(typeHandle,
                new String[] { "subject" });
        HGIndex<Object, HGPersistentHandle> index = graph.getIndexManager()
                .getIndex(byProperty);
        return (HGPersistentHandle) index.findFirst(subject);

    }

    public HGHandle lookupContext(String context)
    {
//        HGHandle typeHandle = graph.getTypeSystem()
//                .getTypeHandle("sail_Context");
//        ByPartIndexer byProperty = new ByPartIndexer(typeHandle,
//                new String[] { "context" });
//        HGIndex<Object, HGPersistentHandle> index = graph.getIndexManager()
//                .getIndex(byProperty);
//        return (HGPersistentHandle) index.findFirst(context);
        return hg.findOne(graph, hg.eq(new HyperContext(context)));
    }

    public HGHandle lookupValue(String val)
    {
        HGHandle typeHandle = graph.getTypeSystem().getTypeHandle("sail_Value");
        ByPartIndexer byProperty = new ByPartIndexer(typeHandle,
                new String[] { "val" });
        HGIndex<Object, HGPersistentHandle> index = graph.getIndexManager()
                .getIndex(byProperty);
        return (HGPersistentHandle) index.findFirst(val);

    }

}
