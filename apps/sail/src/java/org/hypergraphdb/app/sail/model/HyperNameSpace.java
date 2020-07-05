package org.hypergraphdb.app.sail.model;

import org.openrdf.model.Namespace;

/**
 * created Feb 1, 2010  - 10:10:19 AM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperNameSpace implements Namespace {
  public String name;
  public String prefix;

  public String getName() {
    return name;
  }

  public HyperNameSpace() {
  }

  public HyperNameSpace(String prefix, String name) {

    this.name = name;
    this.prefix = prefix;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }
}
