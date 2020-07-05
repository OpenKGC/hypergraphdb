package org.hypergraphdb.app.sail.queryConditions;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.query.HGQueryCondition;

/**
 * created Feb 2, 2010  - 4:51:34 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class ExactPositions implements HGAtomPredicate, HGQueryCondition {
  HGHandle[] list;
  public ExactPositions(HGHandle[] list) {
    this.list = list;
  }

  public boolean satisfies(HyperGraph graph, HGHandle handle) {
    if (handle instanceof HGLink) {

    }
    return false;
  }
}
