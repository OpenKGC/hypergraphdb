package org.hypergraphdb.app.sail;

import info.aduna.iteration.CloseableIteration;
import org.hypergraphdb.app.sail.HyperGraphStore;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

/**
 * created Feb 1, 2010  - 9:37:48 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class FRGTHIS {
  public static void main(String args[]) throws SailException {
    final Sail sail = new HyperGraphStore("/tmp/hyperStore.sail");

    sail.initialize();

    final SailConnection conn = sail.getConnection();

    try {

      CloseableIteration<? extends Statement, SailException> stIter = conn.getStatements(null, null, null, false);
      dumpDB("all",stIter);
      CloseableIteration<? extends Statement, SailException> stIter2 = conn.getStatements(
          new URIImpl("http://example.org/Painter"), null, null, false);
      dumpDB("Subject",stIter2);
      CloseableIteration<? extends Statement, SailException> stIter3 = conn.getStatements(null,
          new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), null, false);
      dumpDB("Predicate",stIter3);
      CloseableIteration<? extends Statement, SailException> stIter4 = conn.getStatements(null,
          new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
          new URIImpl("http://www.w3.org/2000/01/rdf-schema#Class"), false);
      dumpDB("Pred+Object",stIter4);

 CloseableIteration<? extends Statement, SailException> stIter5 = conn.getStatements(null,
          null,
          new URIImpl("http://www.w3.org/2000/01/rdf-schema#Class"), false);
      dumpDB("Object",stIter5);

 CloseableIteration<? extends Statement, SailException> stIter6 = conn.getStatements(
          new URIImpl("http://example.org/Painter"),
         new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
          new URIImpl("http://www.w3.org/2000/01/rdf-schema#Class"), false);
      dumpDB("ALL",stIter6);


      conn.commit();

    } finally {

      conn.close();

    }

  }

  public static void dumpDB(String msg,CloseableIteration<? extends Statement, SailException> stIter) {
    try {
      System.out.println("Dumping RS:"+msg);
      if (!stIter.hasNext())  {
        System.out.println("\tEMPTY");
      }
      while (stIter.hasNext()) {
        Statement s = stIter.next();
        System.out.println("S:" + s.getSubject() + "\tP:" + s.getPredicate() + "\tO:" + s.getObject() + "\tC:" + s.getContext());
      }
    } catch (Exception e) {
      System.out.println("---ERR:" + e);
      e.printStackTrace();
    }
    System.out.println("-----\n");
  }
}
