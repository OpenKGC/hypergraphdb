package org.hypergraphdb.app.sail.model;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.openrdf.model.Resource;

/**
 * created Jan 29, 2010  - 3:34:19 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HResource extends HValue implements Resource {//} HResourceI {
  //String val;

  public HResource() {
  }

  public HResource(String val) {
    this.val = val;
  }

  public HResource ( Resource x) {
    this.val = x.stringValue();
  }


  public void setVal(String val) {
    this.val = val;
  }

  public String stringValue() {
    return val;
  }

  @Override
  public HGHandle addIt(HyperGraph graph) {

    return graph.add(this);
  }
}