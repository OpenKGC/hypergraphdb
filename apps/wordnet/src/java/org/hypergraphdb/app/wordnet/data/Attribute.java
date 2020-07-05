package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;

/**
 * 
 * <p>
 * Semantic link between adjectives and nouns. It can go in either direction
 * (nound->adjective or adjective->noun). It doesn't seem very clear to me how
 * attributes are defined in WordNet as it documentation is rather slim
 * (actually non-existing) on the subject. It looks like an
 * <code>Attribute</code> relation exists between a noun and an adjective
 * synset whenever the adjective can express some attribute value for the noun.
 * The example given in the WordNet manpage is the adjective "heavy" that can be
 * an attribute of the noun "weight". It seems that in the WordNet DB, those
 * links are either defined for the adjective synset or the noun synset, but not
 * for both even though the relation is in fact unidirectional (i.e. "adjective
 * A can be an attribute of noun N"). So maybe we need to change our
 * representation to consolidate this and create HGDB links where the first
 * target is always the adjective synset and the second is always the noun
 * synset. There a little above a 1000 of those in WordNet 2.1.
 * </p>
 * 
 * @author Borislav Iordanov
 * 
 */
public class Attribute extends SemanticLink
{
    public Attribute()
    {
        super();
    }

    public Attribute(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }
}