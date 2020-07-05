package org.hypergraphdb.app.sail.helpers;

import info.aduna.iteration.CloseableIteration;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.sail.SailException;

/**
 * created Feb 1, 2010  - 12:47:21 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class QueryEvaluationIteration<T> implements
        CloseableIteration<T, QueryEvaluationException> {

    private final CloseableIteration<? extends T, SailException> src;

    public QueryEvaluationIteration(
            CloseableIteration<? extends T, SailException> src) {

        assert src != null;

        this.src = src;

    }

    public boolean hasNext() throws QueryEvaluationException {

        try {

            return src.hasNext();

        } catch (SailException ex) {

            throw new QueryEvaluationException(ex);

        }

    }

    public T next() throws QueryEvaluationException {

        try {

            return (T) src.next();

        } catch(SailException ex) {

            throw new QueryEvaluationException(ex);

        }

    }

    public void remove() throws QueryEvaluationException {

        try {

            src.remove();

        } catch(SailException ex) {

            throw new QueryEvaluationException(ex);

        }

    }

    public void close() throws QueryEvaluationException {

        try {

            src.close();

        } catch(SailException ex) {

            throw new QueryEvaluationException(ex);

        }

    }

}

