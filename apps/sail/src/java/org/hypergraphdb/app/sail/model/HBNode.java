package org.hypergraphdb.app.sail.model;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.openrdf.model.BNode;

/**
 * created Feb 1, 2010  - 1:17:28 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HBNode extends HResource implements BNode{//}, HResourceI {
//  public String id;

  public HBNode(String id) {
    this.val = id;
  }

  public HBNode() {
  }


  public HBNode(BNode x) {
    this.val = x.getID();
  }

  public String getID() {
    return val;
  }

  public void setId(String id) {
    this.val = id;
  }

  public String stringValue() {
    return val;
  }
   @Override
  public HGHandle addIt(HyperGraph graph) {

    return graph.add(this);
  }
}