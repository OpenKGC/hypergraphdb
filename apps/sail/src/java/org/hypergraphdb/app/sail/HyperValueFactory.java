package org.hypergraphdb.app.sail;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.app.sail.exceptions.HyperGraphException;
import org.hypergraphdb.app.sail.model.*;
import org.hypergraphdb.query.OrderedLinkCondition;
import org.openrdf.model.*;
import org.openrdf.model.impl.ValueFactoryBase;

import java.util.ArrayList;
import java.util.List;

/**
 * created Feb 1, 2010  - 12:29:39 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperValueFactory extends ValueFactoryBase {
  HyperGraphStore store;

  public HyperValueFactory(HyperGraphStore store) {
    this.store = store;
    // this.graph = store.getGraph();
  }

  public HGHandle find(URI h) {

    return HGQuery.hg.findOne(store.getGraph(), HGQuery.hg.and(HGQuery.hg.typePlus(HURI.class),
        HGQuery.hg.eq("namespace", h.getNamespace()), HGQuery.hg.eq("localName", h.getLocalName())));

  }

  public HGHandle find(BNode h) {

    return HGQuery.hg.findOne(store.getGraph(), HGQuery.hg.and(HGQuery.hg.typePlus(HBNode.class),
        HGQuery.hg.eq("val", h.stringValue())));
  }

  public HGHandle find(Resource h) {

    if (h instanceof URI) {
      return find((URI) h);
    } else if (h instanceof BNode) {
      return find((BNode) h);
    }
    System.out.println("FIND R:" + h.stringValue());
    return HGQuery.hg.findOne(store.getGraph(), HGQuery.hg.and(HGQuery.hg.typePlus(HResource.class),
        HGQuery.hg.eq("val", h.stringValue())));

  }

  public HGHandle find(Literal h) {

    // String x = h.toString();
    return HGQuery.hg.findOne(store.getGraph(), HGQuery.hg.and(HGQuery.hg.typePlus(HLiteral.class),
        HGQuery.hg.eq("val", h.toString())));

  }

  public HGHandle find(Value h) {

    if (h instanceof Resource) {
      return find((Resource) h);
    } else if (h instanceof Literal) {
      return find((Literal) h);
    }

    return HGQuery.hg.findOne(store.getGraph(), HGQuery.hg.and(HGQuery.hg.typePlus(HValue.class),
        HGQuery.hg.eq("val", h.stringValue())));
  }


  public URI createURI(String uri) {
    HURI huri = new HURI(uri);
    HGHandle h = find(huri);
    if (h == null) {
      //graph.add(huriNew);
      huri.addIt(store.getGraph());
      return huri;
    } else {
      return store.getGraph().get(h);
    }
  }

  public URI createURI(String namespace, String localName) {
    HURI huri = new HURI(namespace, localName);
    HGHandle h = find(huri);
    if (h == null) {
      huri.addIt(store.getGraph());
      return huri;
    } else {
      return store.getGraph().get(h);
    }
  }

  public BNode createBNode(String nodeID) {
    HBNode hnode = new HBNode(nodeID);
    HGHandle h = find(hnode);
    if (h == null) {
      hnode.addIt(store.getGraph());
      return hnode;
    } else {
      return store.getGraph().get(h);
    }
  }

  public Literal createLiteral(String value) {
    HLiteral hnode = new HLiteral(value);
    HGHandle h = find(hnode);
    if (h == null) {
      hnode.addIt(store.getGraph());
      return hnode;
    }
    return store.getGraph().get(h);
  }

  public Literal createLiteral(String value, String language) {
    HLiteral hnode = new HLiteral(value, language);
    HGHandle h = find(hnode);
    if (h == null) {
      hnode.addIt(store.getGraph());
      return hnode;
    }
    return store.getGraph().get(h);
  }

  public Literal createLiteral(String value, URI dataType) {
    HLiteral hnode = new HLiteral(value, dataType);
    HGHandle h = find(hnode);
    if (h == null) {
      hnode.addIt(store.getGraph());
      return hnode;
    }
    return store.getGraph().get(h);
  }


  public HGHandle findStatementLink(HGHandle hs, HGHandle hp, HGHandle ho, HGHandle hc) {
    HGHandle links[];
    if (hc == null) {
      links = new HGHandle[3];
    } else {
      links = new HGHandle[4];
      links[3] = hc;
    }
    links[0] = hs;
    links[1] = hp;
    links[2] = ho;


    return HGQuery.hg.findOne(store.getGraph(),
        HGQuery.hg.and(HGQuery.hg.typePlus(HyperStatementLink.class),
            new OrderedLinkCondition(links)));
  }


  public List<HGHandle> findAll(Resource s, URI p, Value o, Resource... contexts) {
    HGHandle hs = null;
    if (s != null) {
      hs = find(s);
      // if we can't find the value/resource it means there aren't any links there.. so return emptyset
      if (hs == null) {
        return new ArrayList<HGHandle>();
      }
    }
    HGHandle hp = null;
    if (p != null) {
      hp = find(p);
      if (hp == null) {
        return new ArrayList<HGHandle>();
      }
    }
    HGHandle ho = null;
    if (o != null) {
      ho = find(o);
      if (ho == null) {
        return new ArrayList<HGHandle>();
      }
    }
    HGHandle hc = null;
    if (contexts.length > 0) {
      if (contexts[0] != null) {
        hc = store.lookupContext(contexts[0].stringValue());
        if (hc == null) {
          return new ArrayList<HGHandle>();
        }

      }
      return findAllStatementLink(hs, hp, ho, hc);
    } else {
      return findAllStatementLink(hs, hp, ho);
    }
  }

  public List<HGHandle> findAllStatementLink(HGHandle hs, HGHandle hp, HGHandle ho, HGHandle... hc) {
    HGHandle[] links;
    boolean bArityTest = false;
    if (hc.length > 0) {

      if (hc[0] != null) {
        links = new HGHandle[4];
        links[3] = hc[0];
      } else {
        bArityTest = true;
        links = new HGHandle[3];
        // links[3] = HGQuery.hg.anyHandle();
      }
    } else {
      links = new HGHandle[3];
    }

    List<HGHandle> rs;
    if (hs != null) {
      links[0] = hs;
    } else {
      links[0] = HGQuery.hg.anyHandle();
    }
    if (hp != null) {
      links[1] = hp;
    } else {
      links[1] = HGQuery.hg.anyHandle();
    }
    if (ho != null) {
      links[2] = ho;
    } else {
      links[2] = HGQuery.hg.anyHandle();
    }

    if (bArityTest) {
      rs = HGQuery.hg.findAll(store.getGraph(),
          HGQuery.hg.and(HGQuery.hg.typePlus(HyperStatementLink.class), HGQuery.hg.orderedLink(links), HGQuery.hg.arity(3)));
    } else {
      rs = HGQuery.hg.findAll(store.getGraph(),
          HGQuery.hg.and(HGQuery.hg.typePlus(HyperStatementLink.class), HGQuery.hg.orderedLink(links)));
    }
    return rs;
  }

  public HGHandle find(Resource s, URI p, Value o) {
    return find(s, p, o, null);
  }

  public HGHandle find(Resource s, URI p, Value o, Resource context) {
    HGHandle hs = find(s);
    HGHandle hp = find(p);
    HGHandle ho = find(o);
    HGHandle hc = null;
    if (context != null) {
      hc = store.lookupContext(context.stringValue());
      if (hc == null) {
        return null;
      }
    }
    if (hs == null) {
      return null;
    }
    if (hp == null) {
      return null;
    }
    if (ho == null) {
      return null;
    }
    return findStatementLink(hs, hp, ho, hc);
  }

  // TODO optimize this

  public List<Statement> findStatement(Resource s, URI p, Value o, Resource... context) {

    List<HGHandle> allHandle;
    if (context.length == 0) {
      allHandle = findAll(s, p, o);
    } else {
      allHandle = findAll(s, p, o, context[0]);
    }
    List<Statement> rs = new ArrayList<Statement>();
    for (HGHandle h : allHandle) {
      rs.add(new HyperContextStatement(store.getGraph(), store.getGraph().<HyperStatementLink>get(h)));
    }
    return rs;
  }

  public Statement createStatement(Resource s, URI p, Value o) {
    return createStatement(s, p, o, null);
  }


  public Statement createStatement(Resource s, URI p, Value o, Resource context) {
    HGHandle hs = find(s);
    HGHandle hp = find(p);
    HGHandle ho = find(o);
    HGHandle hc = null;
    if (context != null) {
      hc = store.lookupContext(context.stringValue());
      if (hc == null) {
        hc = store.getGraph().add(new HyperContext(context));
      }
    }
    if (hs == null) {
      s = fixResource(s);
      //hs = addit((HResource) s);
      hs = ((HResource) s).addIt(store.getGraph());
    }
    if (hp == null) {
      p = fixURI(p);
      hp = ((HURI) p).addIt(store.getGraph());
    }
    if (ho == null) {
      o = fixValue(o);
      ho = ((HValue) o).addIt(store.getGraph());

    }
    //  HyperStatementLink link = new HyperStatementLink(hs, hp, ho, hc);
    HyperStatementLink link;
    HGHandle hLink = findStatementLink(hs, hp, ho, hc);
    if (hLink == null) {
      try {
        if (hc == null) {
          link = new HyperStatementLink(hs, hp, ho);
        } else {
          link = new HyperStatementLink(hs, hp, ho, hc);
        }
        store.getGraph().add(link);

      } catch (HyperGraphException e) {
        return null;
      }
    } else {
      link = store.getGraph().get(hLink);
    }
    /*
    s = store.getGraph().get(link.getSubjectH());
    o = store.getGraph().get(link.getObjectH());
    p = store.getGraph().get(link.getPredicateH());
    if (context != null) {
      context = store.getGraph().get(link.getContextH());
    }
    */

    return new HyperContextStatement(store.getGraph(), link);
  }

  public static HValue fixValue(Value y) {
    if (y instanceof HValue) {
      return (HValue) y;
    }
    if (y instanceof Resource) {
      return fixResource((Resource) y);
    } else if (y instanceof Literal) {
      return fixLiteral((Literal) y);
    } else {
      System.err.println("ERR: unknown class " + y);
      return null;
    }
  }

  public static HResource fixResource(Resource y) {
    if (y instanceof HResource) {
      return (HResource) y;
    }
    if (y instanceof URI) {
      return fixURI((URI) y);
    } else if (y instanceof BNode) {
      return fixBNode((BNode) y);
    } else {
      System.err.println("ERR: unknown class " + y);
      return null;
    }
  }

  public static HLiteral fixLiteral(Literal y) {
    if (y instanceof HLiteral) {
      return (HLiteral) y;
    } else {
      return new HLiteral(y);
    }
  }

  public static HURI fixURI(URI y) {
    if (y instanceof HURI) {
      return (HURI) y;
    }
    return new HURI(y);
  }

  public static HBNode fixBNode(BNode y) {
    if (y instanceof HBNode) {
      return (HBNode) y;
    }
    return new HBNode(y);
  }


}
