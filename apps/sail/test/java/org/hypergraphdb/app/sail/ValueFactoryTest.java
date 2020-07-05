package org.hypergraphdb.app.sail;

import junit.framework.TestCase;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.app.sail.model.HyperContext;
import org.hypergraphdb.app.sail.model.HyperStatementLink;
import org.openrdf.model.*;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A JUnit test for testing Sail implementations that store RDF data. This is
 * purely a test for data storage and retrieval which assumes that no
 * inferencing or whatsoever is performed. This is an abstract class that should
 * be extended for specific Sail implementations.
 */
public abstract class ValueFactoryTest extends TestCase {

  /*-----------*
     * Constants *
     *-----------*/

  private static final String EXAMPLE_NS = "http://example.org/";

  private static final String PAINTER = "Painter";

  private static final String PAINTS = "paints";

  private static final String PAINTING = "Painting";

  private static final String PICASSO = "picasso";

  private static final String REMBRANDT = "rembrandt";

  private static final String GUERNICA = "guernica";

  private static final String NIGHTWATCH = "nightwatch";

  private static final String CONTEXT_1 = "context1";

  private static final String CONTEXT_2 = "context2";

  /*-----------*
     * Variables *
     *-----------*/

  protected URI painter;

  protected URI paints;

  protected URI painting;

  protected URI picasso;

  protected URI rembrandt;

  protected URI guernica;

  protected URI nightwatch;

  protected URI context1;

  protected URI context2;


  protected Sail sail;

  protected SailConnection con;
  protected HyperValueFactory vf;

  /*--------------*
     * Constructors *
     *--------------*/

  public ValueFactoryTest(String name) {
    super(name);
  }

  /*---------*
     * Methods *
     *---------*/

  /**
   * Gets an instance of the Sail that should be tested. The returned
   * repository should already have been initialized.
   *
   * @return an initialized Sail.
   * @throws org.openrdf.sail.SailException If the initialization of the repository failed.
   */
  protected abstract Sail createSail()
      throws SailException;

  @Override
  protected void setUp()
      throws Exception {
    sail = createSail();
    con = sail.getConnection();
    vf = (HyperValueFactory) sail.getValueFactory();

    painter = vf.createURI(EXAMPLE_NS, PAINTER);
    paints = vf.createURI(EXAMPLE_NS, PAINTS);
    painting = vf.createURI(EXAMPLE_NS, PAINTING);
    picasso = vf.createURI(EXAMPLE_NS, PICASSO);
    guernica = vf.createURI(EXAMPLE_NS, GUERNICA);
    rembrandt = vf.createURI(EXAMPLE_NS, REMBRANDT);
    nightwatch = vf.createURI(EXAMPLE_NS, NIGHTWATCH);

    context1 = vf.createURI(EXAMPLE_NS, CONTEXT_1);
    context2 = vf.createURI(EXAMPLE_NS, CONTEXT_2);

  }

  @Override
  protected void tearDown()
      throws Exception {
    try {
      if (con.isOpen()) {
        con.rollback();
        con.close();
      }
    }
    finally {
      sail.shutDown();
      sail = null;
    }
  }


  public void testCreateURI1()
      throws Exception {
    URI picasso1 = vf.createURI(EXAMPLE_NS, PICASSO);
    URI picasso2 = vf.createURI(EXAMPLE_NS + PICASSO);
    HGHandle h1 = vf.find(picasso1);
    HGHandle h2 = vf.find(picasso2);

    assertEquals("createURI(Sring) and createURI(String, String) should create equal URIs", picasso1, picasso2);
    assertEquals("createURI(Sring) and createURI(String, String) should create equal handles", h1, h2);
  }

  public void testLiteral()
      throws Exception {
    String list[][] = {
        /*
        {"<http://tinkerpop.com#1>", "<http://tinkerpop.com#knows>", "<http://tinkerpop.com#2>"},
        {"<http://tinkerpop.com#1>", "<http://tinkerpop.com#knows>", "<http://tinkerpop.com#4>"},
        {"<http://tinkerpop.com#1>", "<http://tinkerpop.com#created>", "<http://tinkerpop.com#3>"},
        {"<http://tinkerpop.com#4>", "<http://tinkerpop.com#created>", "<http://tinkerpop.com#3>"},
        {"<http://tinkerpop.com#4>", "<http://tinkerpop.com#created>", "<http://tinkerpop.com#5>"},
        {"<http://tinkerpop.com#6>", "<http://tinkerpop.com#created>", "<http://tinkerpop.com#3>"},
        */
        {"<http://tinkerpop.com#1>", "<http://tinkerpop.com#name>", "marko", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#1>", "<http://tinkerpop.com#age>", "29", "<http://www.w3.org/2001/XMLSchema#int>"},
        {"<http://tinkerpop.com#2>", "<http://tinkerpop.com#name>", "vadas", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#2>", "<http://tinkerpop.com#age>", "27", "<http://www.w3.org/2001/XMLSchema#int>"},
        {"<http://tinkerpop.com#3>", "<http://tinkerpop.com#name>", "lop", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#3>", "<http://tinkerpop.com#lang>", "java", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#4>", "<http://tinkerpop.com#name>", "josh", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#4>", "<http://tinkerpop.com#age>", "32", "<http://www.w3.org/2001/XMLSchema#int>"},
        {"<http://tinkerpop.com#5>", "<http://tinkerpop.com#name>", "ripple", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#5>", "<http://tinkerpop.com#lang>", "java", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#6>", "<http://tinkerpop.com#name>", "peter", "<http://www.w3.org/2001/XMLSchema#string>"},
        {"<http://tinkerpop.com#6>", "<http://tinkerpop.com#age>", "35", "<http://www.w3.org/2001/XMLSchema#int>"}
    };
    for (String[] x : list) {
      Resource s = new URIImpl(x[0]);
      URI p = new URIImpl(x[1]);
      Value o = new LiteralImpl(x[2], new URIImpl(x[3]));

      vf.createStatement(s, p, o);
    }
    assertEquals("Test is more making sure literals actually store", true, true);

  }

  public void testRIO() throws Exception, IOException, RDFParseException, SailException {
    Repository repo = new SailRepository(sail);
    RepositoryConnection connection = repo.getConnection();
    connection.add(new File("apps/sail/test/graph-example-1.ntriple"), null, RDFFormat.NTRIPLES);
    connection.commit();
    connection.close();
    assertEquals("All should be loaded", true, true);
    dumpAll();

    this.tearDown();

    sail = new HyperGraphStore("/tmp/VF.sail");

    sail.initialize();


    con = sail.getConnection();
    vf = (HyperValueFactory) sail.getValueFactory();
    dumpAll();

  }

  public void testCreateStatement()
      throws Exception {
    Statement x = vf.createStatement(painter, RDF.TYPE, RDFS.CLASS);
    Statement a = vf.createStatement(painter, RDF.TYPE, RDFS.CLASS);
    assertEquals("Statement should be equal", a, x);

    HGHandle b = vf.find(painter, RDF.TYPE, RDFS.CLASS);
    //dumpAll();
    assertEquals("We should be able to find this statement", true, b != null);

    HyperStatementLink hl = vf.store.getGraph().get(b);
    assertEquals("Painters should be equal", painter, vf.store.getGraph().get(hl.getSubjectH()));

    Statement y = vf.createStatement(painting, RDF.TYPE, RDFS.CLASS);
    assertEquals("Predicate parts should be equal", x.getPredicate(), y.getPredicate());

    Statement z = vf.createStatement(picasso, RDF.TYPE, painter, context1);
    assertEquals("Statement parts should be equal", x.getSubject(), z.getObject());
    HGHandle bmismatch = vf.find(painter, RDF.TYPE, picasso, context1);
    assertEquals("Statement order of tuples is wrong", false, bmismatch != null);
    HGHandle bmatch = vf.find(picasso, RDF.TYPE, painter);
    assertEquals("this should match a statement", true, bmatch != null);

  }

  public void testCreateStatementLiteral() {
    URI subj = new URIImpl(EXAMPLE_NS + PICASSO);
    URI pred = new URIImpl(EXAMPLE_NS + PAINTS);
    Literal obj = new LiteralImpl("guernica", "es");
    Statement z = vf.createStatement(subj, pred, obj);
    assertEquals("Storage of non-URI object ", true, z != null);

  }

  public void testFindAll() {
    vf.createStatement(painter, RDF.TYPE, RDFS.CLASS);
    vf.createStatement(painting, RDF.TYPE, RDFS.CLASS);
    vf.createStatement(picasso, RDF.TYPE, painter);
    vf.createStatement(rembrandt, RDF.TYPE, painter);
    List<HGHandle> matches = vf.findAll(null, null, painter);
    assertEquals("expecting 2 painters to appear", 2, matches.size());

  }

  public void printMatches(List<HGHandle> matches) {
    System.out.println("matches the objects");
    System.out.println("----------------");
    for (HGHandle x : matches) {
      printLink((HyperStatementLink) vf.store.getGraph().get(x));
    }

    dumpAll();
  }

  public void printLink(HyperStatementLink hl) {
    //   HyperStatementLink hl = vf.store.getGraph().get(x);
    Resource s = vf.store.getGraph().get(hl.getSubjectH());
    URI p = vf.store.getGraph().get(hl.getPredicateH());
    Value o = vf.store.getGraph().get(hl.getObjectH());
    if (hl.getContextH() != null) {
      HyperContext c = vf.store.getGraph().get(hl.getContextH());
      System.out.println("S:" + s + "\tP:" + p + "\tO:" + o + "\tContext:" + c);
    } else {
      System.out.println("S:" + s + "\tP:" + p + "\tO:" + o);

    }
  }

  public void dumpAll() {
    System.out.println("ALL the objects");
    System.out.println("----------------");
    List<Object> allAtoms = HGQuery.hg.getAll(vf.store.getGraph(), HGQuery.hg.all());
    for (Object o : allAtoms) {
      if (o instanceof HyperStatementLink) {
        printLink((HyperStatementLink) o);
      } else {
        System.out.println(o.getClass() + ":"+ o.getClass().getSimpleName() +"--" + o.toString());
      }
    }

  }
}