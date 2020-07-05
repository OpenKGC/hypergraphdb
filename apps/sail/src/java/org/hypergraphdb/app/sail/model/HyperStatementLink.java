package org.hypergraphdb.app.sail.model;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;
import org.hypergraphdb.app.sail.exceptions.HyperGraphException;

/**
 * created Feb 2, 2010  - 9:39:58 AM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperStatementLink extends HGPlainLink {

  public HyperStatementLink(HGHandle... outgoingSet) throws HyperGraphException {
    super(outgoingSet);
    if (outgoingSet.length < 3 || outgoingSet.length > 4) {
      throw new HyperGraphException("Woops..HyperStatementLink should have 3 or 4 links.. it only has:" + outgoingSet.length);
    }
  }

  public HGHandle getSubjectH() {
//    if (outgoingSet.length >= 3) {
    return outgoingSet[0];
//    } else {
//      return null;
    //   }
  }

  public HGHandle getPredicateH() {
//    if (outgoingSet.length >= 3) {
    return outgoingSet[1];
//    } else {
//      return null;
    //   }
  }

  public HGHandle getObjectH() {
    //   if (outgoingSet.length >= 3) {
    return outgoingSet[2];
    //   } else {
    //     return null;
    //   }
  }

  public HGHandle getContextH() {
    if (outgoingSet.length >= 4) {
      return outgoingSet[3];
    } else {
      return null;
    }
  }
}
