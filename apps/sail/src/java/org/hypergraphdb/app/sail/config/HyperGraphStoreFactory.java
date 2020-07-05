package org.hypergraphdb.app.sail.config;

import org.hypergraphdb.app.sail.HyperGraphStore;
import org.openrdf.sail.Sail;
import org.openrdf.sail.config.SailConfigException;
import org.openrdf.sail.config.SailFactory;
import org.openrdf.sail.config.SailImplConfig;

/**
 * created Jan 29, 2010  - 2:08:28 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperGraphStoreFactory implements SailFactory {
  public static final String SAIL_TYPE = "openrdf:HyperGraph";

  public String getSailType() {
    return SAIL_TYPE;
  }

  public SailImplConfig getConfig() {
    return new HyperGraphStoreConfig();
  }

  public Sail getSail(SailImplConfig config) throws SailConfigException {
    if (!SAIL_TYPE.equals(config.getType())) {
      throw new SailConfigException("Invalid Sail type: " + config.getType());
    }


    if (config instanceof HyperGraphStoreConfig) {
      HyperGraphStoreConfig hyperConfig = (HyperGraphStoreConfig) config;

      HyperGraphStore hyperStore = new HyperGraphStore(hyperConfig.getLocation());
      return hyperStore;
    }

    throw new IllegalArgumentException("Supplied config objects should be an HyperGraphStoreConfig, is: "
        + config.getClass());

  }
}
