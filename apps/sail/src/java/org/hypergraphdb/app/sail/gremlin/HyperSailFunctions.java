package org.hypergraphdb.app.sail.gremlin;

import com.tinkerpop.gremlin.functions.FunctionLibrary;


/**
 * created Feb 5, 2010  - 9:37:19 AM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperSailFunctions extends FunctionLibrary {
  public static final String NAMESPACE_PREFIX = "hyperSail";

  public HyperSailFunctions() {
    this.addFunction(NAMESPACE_PREFIX, new OpenFunction());
  }
}
