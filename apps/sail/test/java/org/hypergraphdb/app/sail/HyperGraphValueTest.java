package org.hypergraphdb.app.sail;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

/**
 * created Jan 31, 2010  - 9:41:37 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperGraphValueTest extends ValueFactoryTest {
  public HyperGraphValueTest(String name) {
    super(name);
  }

  @Override
  protected Sail createSail() throws SailException {
    final Sail sail = new HyperGraphStore("/tmp/VF.sail");

    sail.initialize();
    final SailConnection conn = sail.getConnection();

    try {

      conn.clear();

      conn.clearNamespaces();

      conn.commit();

    } finally {

      conn.close();

    }
    return sail;

  }

}