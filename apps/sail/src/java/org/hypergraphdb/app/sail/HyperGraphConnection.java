package org.hypergraphdb.app.sail;

import info.aduna.iteration.CloseableIteration;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.IncidenceSet;
import org.hypergraphdb.app.sail.exceptions.HyperGraphException;
import org.hypergraphdb.app.sail.helpers.ContextIteration;
import org.hypergraphdb.app.sail.helpers.HyperGraphTripleSource;
import org.hypergraphdb.app.sail.helpers.NamespaceIteration;
import org.hypergraphdb.app.sail.helpers.StatementIteration;
import org.hypergraphdb.app.sail.model.HValue;
import org.hypergraphdb.app.sail.model.HyperContext;
import org.hypergraphdb.app.sail.model.HyperNameSpace;
import org.hypergraphdb.transaction.HGTransactionContext;
import org.hypergraphdb.transaction.HGTransactionException;
import org.hypergraphdb.transaction.TransactionConflictException;
import org.openrdf.model.*;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.evaluation.TripleSource;
import org.openrdf.query.algebra.evaluation.impl.*;
import org.openrdf.query.algebra.evaluation.util.QueryOptimizerList;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailConnectionBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created Jan 29, 2010 - 2:36:07 PM
 * 
 * @author IanHolsman Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperGraphConnection extends SailConnectionBase
{
    private HyperGraphStore hyperStore;
    private HGTransactionContext thisTrans;
    private HyperValueFactory vf;
    private List<StoreOperation> transactionBatch = new ArrayList<StoreOperation>();
    
    public HyperGraphConnection(HyperGraphStore hyperStore)
    {
        super(hyperStore);
        this.hyperStore = hyperStore;
        this.vf = (HyperValueFactory) hyperStore.getValueFactory();
    }

    public ValueFactory getValueFactory()
    {
        return this.hyperStore.getValueFactory();
    }

    @Override
    protected void finalize() throws Throwable
    {
        if (thisTrans != null)
        {
            hyperStore.getGraph().getTransactionManager().endTransaction(true);
            thisTrans = null;
        }
        super.finalize();
    }

    @Override
    protected void closeInternal() throws SailException
    {
        try
        {
            if (thisTrans != null)
            {
                hyperStore.getGraph()
                        .getTransactionManager()
                        .endTransaction(false);
                thisTrans = null;
            }
        }
        catch (HGTransactionException e)
        {
            throw new SailException(e);
        }
    }

    @Override
    protected CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluateInternal(
                  TupleExpr tupleExpr,
                  Dataset dataset,
                  BindingSet bindings,
                  boolean includeInferred)
            throws SailException
    {

        tupleExpr = tupleExpr.clone();

        if (!(tupleExpr instanceof QueryRoot))
        {
            // Add a dummy root node to the tuple expressions to allow the
            // optimizers to modify the actual root node
            tupleExpr = new QueryRoot(tupleExpr);
        }

        TripleSource tripleSource = new HyperGraphTripleSource(this,
                includeInferred);
        EvaluationStrategyImpl strategy = new EvaluationStrategyImpl(
                tripleSource, dataset);

        final QueryOptimizerList optimizerList = new QueryOptimizerList();
        optimizerList.add(new BindingAssigner());
        optimizerList.add(new ConstantOptimizer(strategy));
        optimizerList.add(new CompareOptimizer());
        optimizerList.add(new ConjunctiveConstraintSplitter());
        optimizerList.add(new SameTermFilterOptimizer());
        // only need to optimize the join order this way if we are not
        // using native joins

        optimizerList.add(new FilterOptimizer());

        optimizerList.optimize(tupleExpr, dataset, bindings);
        // System.out.println(tupleExpr.toString());

        try
        {
            return strategy.evaluate(tupleExpr, EmptyBindingSet.getInstance());
        }
        catch (QueryEvaluationException e)
        {
            throw new SailException(e);
        }
        // return null;
    }

    @Override
    protected ContextIteration getContextIDsInternal() throws SailException
    {
        List<HyperContext> allObjects = HGQuery.hg.getAll(this.hyperStore.getGraph(),
                                                          HGQuery.hg.typePlus(HyperContext.class));
        return new ContextIteration(allObjects.iterator());
    }

    @Override
    protected void startTransactionInternal() throws SailException
    {
        hyperStore.getGraph().getTransactionManager().beginTransaction();
        thisTrans = hyperStore.getGraph().getTransactionManager().getContext();
    }

    @Override
    protected void commitInternal() throws SailException
    {
        hyperStore.getGraph().getTransactionManager().commit();
        if (thisTrans != null)
        {
            thisTrans = null;
        }
        transactionBatch.clear();
    }

    @Override
    protected void rollbackInternal() throws SailException
    {
        hyperStore.getGraph().getTransactionManager().abort();
        if (thisTrans != null)
        {
            thisTrans = null;
        }
        transactionBatch.clear();
    }

    @Override
    protected long sizeInternal(Resource... contexts) throws SailException
    {
        // List<HGHandle> allHandles;
        long count = 0;
        if (contexts.length == 0)
        {
            // count = HGQuery.hg.findAll(this.hyperStore.getGraph(),
            // HGQuery.hg.typePlus(HyperContextStatement.class)).size();
            count = vf.findAll(null, null, null).size();
        }
        else
        {
            for (Resource x : contexts)
            {
                count += vf.findAll(null, null, null, x).size();
                /*
                 * HGQuery.hg.findAll(this.hyperStore.getGraph(),
                 * HGQuery.hg.and((HGQuery.hg.eq("context", contexts[0])),
                 * HGQuery.hg.typePlus(HyperContextStatement.class))).size();
                 */
            }
        }
        // return 0;
        return count;
    }

    // TODO.
    // figure out inferred flag. for now.. just use 'false'

    @Override
    protected StatementIteration getStatementsInternal(Resource s, URI p,
                                                       Value o, boolean b,
                                                       Resource... contexts)
            throws SailException
    {
        List<Statement> allObjects;

        if (contexts.length == 0)
        {

            allObjects = vf.findStatement(s, p, o);
            // HGQuery.hg.getAll(this.hyperStore.getGraph(),
            // buildQuery(HyperContextStatement.class, s, p, o, null));
            // }
        }
        else
        {
            allObjects = new ArrayList<Statement>();
            for (Resource context : contexts)
            {
                allObjects.addAll(vf.findStatement(s, p, o, context));
            }

            // allObjects = vf.findStatement(s, p, o, contexts[0]);
            // } else {
            // throw new UnsupportedOperationException();
        }
        return new StatementIteration(allObjects.iterator());
    }

    @Override
    protected void addStatementInternal(Resource s, URI p, Value o,
                                        Resource... contexts)
            throws SailException
    {
        StoreOperation op = new AddOperation(s, p, o, contexts);
        transactionBatch.add(op);
        doNow(op);
    }

    /*
     * protected HGQueryCondition buildQuery(Class x, Resource s, URI p, Value
     * o, Resource context) { List<AtomPartCondition> equals = new
     * ArrayList<AtomPartCondition>();
     * 
     * 
     * if (p != null) { equals.add(HGQuery.hg.eq("predicate", new HURI(p))); }
     * if (s != null) { equals.add(HGQuery.hg.eq("subject",
     * HyperValueFactory.fixResource(s))); } if (o != null) {
     * equals.add(HGQuery.hg.eq("object", HyperValueFactory.fixValue(o))); } if
     * (context != null) { equals.add(HGQuery.hg.eq("context", new
     * HyperContext(context))); } else {
     * 
     * // equals.add(HGQuery.hg.eq("context", new HyperContext("-NONE-")));
     * 
     * } if (equals.size() == 0) { return HGQuery.hg.typePlus(x); } else { And
     * andQ = HGQuery.hg.and(); andQ.add(HGQuery.hg.typePlus(x)); for
     * (AtomPartCondition part : equals) { andQ.add(part); } return andQ; } }
     */

    // TODO: remove unused HResources/HValues & HContexts
    @Override
    protected void removeStatementsInternal(Resource s, URI p, Value o,
                                            Resource... contexts)
            throws SailException
    {
        StoreOperation op = new RemoveOperation(s, p, o, contexts);
        transactionBatch.add(op);
        doNow(op);        
    }

    @Override
    /*
     * NB: if you don't specify a context it will also wipe ALL the HValues
     */
    protected void clearInternal(Resource... contexts) throws SailException
    {
        /*
         * none specified .. wipe ALL the statements.
         */
        List<HGHandle> allHandles;
        if (contexts.length == 0)
        {
            allHandles = vf.findAll(null, null, null);
            for (HGHandle h : allHandles)
            {
                // Object o = this.hyperStore.getGraph().get(h);
                this.hyperStore.getGraph().remove(h);
            }

            List<HGHandle> allContexts = HGQuery.hg.findAll(this.hyperStore.getGraph(),
                                                            HGQuery.hg.typePlus(HyperContext.class));
            for (HGHandle h : allContexts)
            {
                this.hyperStore.getGraph().remove(h);
            }
            // TODO: figure out if this is inspec
            List<HGHandle> allAtoms = HGQuery.hg.findAll(this.hyperStore.getGraph(),
                                                         HGQuery.hg.typePlus(HValue.class));
            for (HGHandle h : allAtoms)
            {
                this.hyperStore.getGraph().remove(h);
            }

        }
        else
        {
            for (Resource context : contexts)
            {
                allHandles = vf.findAll(null, null, null, context);
                // allHandles = HGQuery.hg.findAll(this.hyperStore.getGraph(),
                // HGQuery.hg.and(HGQuery.hg.typePlus(HyperContextStatement.class),
                // HGQuery.hg.eq("context", context)));
                for (HGHandle h : allHandles)
                {
                    this.hyperStore.getGraph().remove(h);
                }
                allHandles = HGQuery.hg.findAll(this.hyperStore.getGraph(),
                                                HGQuery.hg.and(HGQuery.hg.typePlus(HyperContext.class),
                                                               HGQuery.hg.eq("context",
                                                                             context)));
                for (HGHandle h : allHandles)
                {
                    this.hyperStore.getGraph().remove(h);
                }
            }
        }
    }

    @Override
    protected CloseableIteration<? extends Namespace, SailException> getNamespacesInternal()
            throws SailException
    {
        List<HyperNameSpace> allObjects = HGQuery.hg.getAll(this.hyperStore.getGraph(),
                                                            HGQuery.hg.typePlus(HyperNameSpace.class));

        return new NamespaceIteration(allObjects.iterator());
    }

    @Override
    protected String getNamespaceInternal(String prefix) throws SailException
    {
        HGHandle h = this.hyperStore.lookupNameSpacePrefix(prefix);
        if (h == null)
        {
            return null;
        }
        HyperNameSpace ns = this.hyperStore.getGraph().get(h);
        if (ns == null)
        {
            return null;
        }
        return ns.getName();
    }

    @Override
    protected void setNamespaceInternal(String prefix, String namespace)
            throws SailException
    {
        HGHandle h = this.hyperStore.lookupNameSpacePrefix(prefix);
        HyperNameSpace ns = new HyperNameSpace(prefix, namespace);
        if (h == null)
        {
            this.hyperStore.getGraph().add(ns);
        }
        else
        {
            this.hyperStore.getGraph().replace(h, ns);
        }
    }

    @Override
    protected void removeNamespaceInternal(String prefix) throws SailException
    {
        HGHandle h = this.hyperStore.lookupNameSpacePrefix(prefix);
        if (h != null)
        {
            this.hyperStore.getGraph().remove(h);
        }
    }

    @Override
    protected void clearNamespacesInternal() throws SailException
    {
        List<HGHandle> allAtoms = HGQuery.hg.findAll(this.hyperStore.getGraph(),
                                                     HGQuery.hg.typePlus(HyperNameSpace.class));
        for (HGHandle h : allAtoms)
        {
            this.hyperStore.getGraph().remove(h);
        }
    }

    private void doNow(StoreOperation op) throws SailException
    {
        List<StoreOperation> todo = Collections.singletonList(op);
        while (true)
        {
            try 
            { 
                for (StoreOperation next : todo) 
                    next.perform(this); 
                break;
            }
            catch (Throwable t)
            {
                rollback();                
                boolean retry = false;
                for (Throwable cause = t; cause != null; cause = cause.getCause())
                		if (!hyperStore.getGraph().getStore().getTransactionFactory().canRetryAfter(t))
                    {
                        retry = true;
                        break;
                    }
                if (!retry)
                {
                    if (t instanceof RuntimeException)
                        throw (RuntimeException)t;
                    else if (t instanceof SailException)
                        throw (SailException)t;
                    else if (t instanceof Error)
                        throw (Error)t;
                    else 
                        throw new SailException(t);
                }
                autoStartTransaction();
                todo = transactionBatch;
            }
        }
    }
    
    static interface StoreOperation 
    {
        void perform(HyperGraphConnection conn) throws HyperGraphException;
    }
    
    static class AddOperation implements StoreOperation
    {        
        Resource s;
        URI p;
        Value o;
        Resource [] contexts;      
        
        public AddOperation(Resource s, URI p, Value o, Resource [] contexts)
        {
            this.s = s; 
            this.p = p;
            this.o = o; 
            this.contexts = contexts;
        }
        
        public void perform(HyperGraphConnection conn) throws HyperGraphException
        {
            try
            {
                if (contexts.length == 0)
                {
                    conn.vf.createStatement(s, p, o);
                    /*
                     * StatementIteration si = this.getStatementsInternal(s, p, o,
                     * false); if (!si.hasNext()) { Statement i = new
                     * HyperContextStatement(s, p, o, null);
                     * this.hyperStore.getGraph().add(i);
                     * 
                     * }
                     */
                }
                else
                {
                    for (Resource context : contexts)
                    {
                        /*
                         * StatementIteration si = this.getStatementsInternal(s, p,
                         * o, false, context); if (!si.hasNext()) { HGHandle con =
                         * this.hyperStore.lookupContext(context.stringValue()); if
                         * (con == null) { con = this.hyperStore.getGraph().add(new
                         * HyperContext(context)); } HyperContext hc =
                         * this.hyperStore.getGraph().get(con); Statement i = new
                         * HyperContextStatement(s, p, o, hc); HGHandle h =
                         * this.hyperStore.getGraph().add(i);
                         * this.hyperStore.getGraph().add(new HGPlainLink(con, h));
                         * }
                         */
                        conn.vf.createStatement(s, p, o, context);
                    }
                }
            }
            catch (Exception e)
            {
                throw new HyperGraphException(e);
            }            
        }        
    }
    
    static class RemoveOperation implements StoreOperation
    {
        Resource s;
        URI p;
        Value o;
        Resource [] contexts;      
        
        
        public RemoveOperation(Resource s, URI p, Value o, Resource [] contexts)
        {
            this.s = s; 
            this.p = p;
            this.o = o; 
            this.contexts = contexts;
        }
        
        public void perform(HyperGraphConnection conn)
                throws HyperGraphException
        {
            List<HGHandle> allHandles;
            if (contexts.length == 0)
            {
                // allHandles = HGQuery.hg.findAll(this.hyperStore.getGraph(),
                // buildQuery(HyperContextStatement.class, s, p, o, null));
                allHandles = conn.vf.findAll(s, p, o);
                for (HGHandle h : allHandles)
                {
                    conn.hyperStore.getGraph().remove(h);
                }

            }
            else
            {
                for (Resource context : contexts)
                {
                    // allHandles = HGQuery.hg.findAll(this.hyperStore.getGraph(),
                    // buildQuery(HyperContextStatement.class, s, p, o, context));
                    allHandles = conn.vf.findAll(s, p, o, context);
                    for (HGHandle h : allHandles)
                    {
                        conn.hyperStore.getGraph().remove(h);
                    }
                    HGHandle h = conn.hyperStore.lookupContext(context.stringValue());
                    if (h == null)
                        h = conn.hyperStore.getGraph().add(new HyperContext(context));
                    IncidenceSet is = conn.hyperStore.getGraph().getIncidenceSet(h);
                    if (is.size() == 0)
                    {
                        conn.hyperStore.getGraph().remove(h);
                    }
                }
            }
            if (s != null)
            {
                HGHandle h = conn.vf.find(s);
                if (h != null)
                {
                    IncidenceSet is = conn.hyperStore.getGraph().getIncidenceSet(h);
                    if (is.size() == 0)
                    {
                        conn.hyperStore.getGraph().remove(h);
                    }
                }
            }

            if (p != null)
            {
                HGHandle h = conn.vf.find(p);
                if (h != null)
                {
                    IncidenceSet is = conn.hyperStore.getGraph().getIncidenceSet(h);
                    if (is.size() == 0)
                    {
                        conn.hyperStore.getGraph().remove(h);
                    }
                }
            }
            if (o != null)
            {
                HGHandle h = conn.vf.find(o);
                if (h != null)
                {
                    IncidenceSet is = conn.hyperStore.getGraph().getIncidenceSet(h);
                    if (is.size() == 0)
                    {
                        conn.hyperStore.getGraph().remove(h);
                    }
                }
            }            
        }
        
    }
}
