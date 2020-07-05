package org.hypergraphdb.app.sail.model;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.datatypes.XMLDatatypeUtil;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * created Feb 1, 2010  - 1:17:28 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HLiteral extends HValue implements Literal {

 // public String val;
  /**
   * The literal's label.
   */
  public String label;

  /**
   * The literal's language tag (null if not applicable).
   */
  public String language;

  /**
   * The literal's datatype (null if not applicable).
   */
  public HURI datatype;

  /*--------------*
     * Constructors *
     *--------------*/

  public HLiteral() {
  }

  /**
   * Creates a new plain literal with the supplied label.
   *
   * @param label The label for the literal, must not be <tt>null</tt>.
   */
  public HLiteral(String label) {
    this(label, null, null);
  }

  /**
   * Creates a new plain literal with the supplied label and language tag.
   *
   * @param label    The label for the literal, must not be <tt>null</tt>.
   * @param language The language tag for the literal.
   */
  public HLiteral(String label, String language) {
    this(label, language, null);
  }

  /**
   * Creates a new datyped literal with the supplied label and datatype.
   *
   * @param label    The label for the literal, must not be <tt>null</tt>.
   * @param datatype The datatype for the literal.
   */
  public HLiteral(String label, URI datatype) {
    this(label, null, datatype);

  }

  @Override
  public void setVal(Value x) {
    val = this.toString();
  }

  @Override
  public String getVal() {
    return this.toString();
  }

  /**
   * Creates a new Literal object, initializing the variables with the supplied
   * parameters.
   */
  public HLiteral(String label, String language, URI datatype) {
    assert label != null;

    setLabel(label);
    if (language != null) {
      setLanguage(language.toLowerCase());
    }
    if (datatype != null) {
      setDatatype(datatype);
    }
    this.label = label;
    this.val = this.toString();
  }

  public HLiteral(Literal x) {
    this.label = x.getLabel();
    this.language = x.getLanguage();
    this.setDatatype(x.getDatatype());
    this.val = this.toString();
  }

  /*---------*
   * Methods *
   *---------*/

  public void setLabel(String label) {
    this.label = label;
    this.val = this.toString();
  }

  public String getLabel() {

    if (label == null) {
      if (val != null) {
        fromVal(val);
      }

      System.out.println("ouch.. Literals shouldn't have null labels.");
      //   return "";
    }
    return label;
  }

  public void fromVal(String v) {
    //  "marko"^^<http://www.w3.org/2001/XMLSchema#string>
    //  "marko"@es^^<http://www.w3.org/2001/XMLSchema#string>
    if (v.charAt(0) == '"') {
      int posn = v.indexOf('"', 1);
      label = v.substring(1, posn);
      int posn2 = v.indexOf('@', posn);
      int posn3 = v.indexOf("^^<", posn);

      if (posn2 > 0) {
        if (posn3 > 0) {
          language = v.substring(posn2 + 1, posn3);

        } else {
          language = v.substring(posn2 + 1);
        }
      }
      if (posn3 > 0) {
        String uri = v.substring(posn3 + 3);
        uri = uri.substring(0, uri.length() - 1);
        this.datatype = new HURI(uri);
      }
    }

  }
  /*
    public static void main(String x[]) {
      HLiteral xy = new HLiteral();
      xy.fromVal("\"marko\"^^<http://www.w3.org/2001/XMLSchema#string>");
      xy.fromVal("\"marko\"@es^^<http://www.w3.org/2001/XMLSchema#string>");
    }

  */

  public void setLanguage(String language) {
    this.language = language;
    this.val = this.toString();
  }

  public String getLanguage() {
    return language;
  }

  public void setDatatype(URI datatype) {
    if (datatype == null) {
      this.datatype = null;
      return;
    }
    if (datatype instanceof HURI) {
      this.datatype = (HURI) datatype;
    } else {
      this.datatype = new HURI(datatype);
    }
    this.val = this.toString();
  }

  public URI getDatatype() {
    return datatype;
  }

  // Overrides Object.equals(Object), implements Literal.equals(Object)

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o instanceof Literal) {
      Literal other = (Literal) o;

      // Compare labels
      if (!label.equals(other.getLabel())) {
        return false;
      }

      // Compare datatypes
      if (datatype == null) {
        if (other.getDatatype() != null) {
          return false;
        }
      } else {
        if (!datatype.equals(other.getDatatype())) {
          return false;
        }
      }

      // Compare language tags
      if (language == null) {
        if (other.getLanguage() != null) {
          return false;
        }
      } else {
        if (!language.equals(other.getLanguage())) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  // overrides Object.hashCode(), implements hashCode()

  @Override
  public int hashCode() {

    return this.getLabel().hashCode();
  }

  /**
   * Returns the label of the literal.
   */
  @Override
  public String toString() {
    StringBuilder sb;
    if (label != null) {
      sb = new StringBuilder(label.length() * 2);

      sb.append('"');
      sb.append(label);
      sb.append('"');

    } else {
      sb = new StringBuilder("-NULL LABEL-".length() * 2);
      sb.append("-NULL LABEL-");
    }
    if (language != null) {
      sb.append('@');
      sb.append(language);
    }

    if (datatype != null) {
      sb.append("^^<");
      sb.append(datatype.toString());
      sb.append(">");
    }

    return sb.toString();
  }

  public String stringValue() {
    return label;
  }

  public boolean booleanValue() {
    return XMLDatatypeUtil.parseBoolean(getLabel());
  }

  public byte byteValue() {
    return XMLDatatypeUtil.parseByte(getLabel());
  }

  public short shortValue() {
    return XMLDatatypeUtil.parseShort(getLabel());
  }

  public int intValue() {
    return XMLDatatypeUtil.parseInt(getLabel());
  }

  public long longValue() {
    return XMLDatatypeUtil.parseLong(getLabel());
  }

  public float floatValue() {
    return XMLDatatypeUtil.parseFloat(getLabel());
  }

  public double doubleValue() {
    return XMLDatatypeUtil.parseDouble(getLabel());
  }

  public BigInteger integerValue() {
    return XMLDatatypeUtil.parseInteger(getLabel());
  }

  public BigDecimal decimalValue() {
    return XMLDatatypeUtil.parseDecimal(getLabel());
  }

  public XMLGregorianCalendar calendarValue() {
    return XMLDatatypeUtil.parseCalendar(getLabel());
  }

  @Override
  public HGHandle addIt(HyperGraph graph) {

    if ( this.label==null) {
      System.out.println("Why are you adding a NULL label?");
    }
    return graph.add(this);
  }
}