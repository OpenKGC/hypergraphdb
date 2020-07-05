package org.hypergraphdb.app.sail.gremlin;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.sail.SailGraph;
import com.tinkerpop.gremlin.functions.Function;
import com.tinkerpop.gremlin.functions.FunctionHelper;
import com.tinkerpop.gremlin.statements.EvaluationException;
import org.apache.commons.jxpath.ExpressionContext;
import org.hypergraphdb.app.sail.HyperGraphStore;

/**
 * created Feb 12, 2010  - 3:41:36 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */

public class OpenFunction implements Function {
  public static final String FUNCTION_NAME = "open";

  public Graph invoke(final ExpressionContext context, final Object[] parameters) {

    if (null != parameters && parameters.length == 1) {
      Object object = FunctionHelper.nodeSetConversion(parameters[0]);
      if (object instanceof String) {
        return new SailGraph(new HyperGraphStore((String) object));
      }
    }
    throw EvaluationException.createException(FunctionHelper.makeFunctionName(HyperSailFunctions.NAMESPACE_PREFIX, FUNCTION_NAME),
        EvaluationException.EvaluationErrorType.UNSUPPORTED_PARAMETERS);
  }

  public String getName() {
    return FUNCTION_NAME;
  }
}