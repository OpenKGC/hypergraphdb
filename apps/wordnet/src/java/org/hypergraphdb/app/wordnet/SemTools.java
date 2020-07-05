package org.hypergraphdb.app.wordnet;

import java.util.*;

import org.hypergraphdb.*;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.query.impl.TraversalBasedQuery;
import org.hypergraphdb.util.Pair;
import org.hypergraphdb.algorithms.GraphClassics;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGBreadthFirstTraversal;
import org.hypergraphdb.algorithms.HGTraversal;
import org.hypergraphdb.app.wordnet.data.*;
import org.hypergraphdb.app.wordnet.ext.ConceptualDensity;
import org.hypergraphdb.app.wordnet.stats.NounIsaDepth;
import org.hypergraphdb.app.wordnet.stats.VerbIsaDepth;

/**
 * 
 * <p>
 * A collection of functions to perform semantic analysis in WordNet. This mainly
 * has to do with similarity measures needed to do WSD (word sense disambiguation)
 * with WordNet as the lexical resources. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class SemTools
{
	private HyperGraph graph;
	private WNGraph wn;
	private ConceptualDensity conceptualDensity = null;
	
	private long countNounDescendents(HGHandle s)
	{
	    HGQuery<?> q1 = new TraversalBasedQuery(wn.relatedBreadthFirst(s, InstanceOf.class, false)), 
	               q2 = new TraversalBasedQuery(wn.relatedBreadthFirst(s, KindOf.class, false));
	    q1.setHyperGraph(graph);
	    q2.setHyperGraph(graph);
		return hg.count(q1) + hg.count(q2);
	}

	private long countVerbDescendents(HGHandle s)
	{
	    HGQuery<?> query = new TraversalBasedQuery(wn.relatedBreadthFirst(s, Entails.class, false));
	    query.setHyperGraph(graph);
		return hg.count(query);
	}
	
	public SemTools(HyperGraph graph)
	{
		this.graph = graph;
		this.wn = new WNGraph(graph);
	}

	/**
	 * Return the associated {@WNGraph} instance.
	 */
	public WNGraph getWNGraph()
	{
		return wn;
	}
	
	/**
     * Return the underlying HyperGraphDB instance.
	 */
	public HyperGraph getGraph()
	{
		return graph;
	}
	
	/**
	 * <p>
	 * Get the lazily initialized instance of the {@link ConceptualDensity}
	 * calculator.
	 * </p>
	 */
	public ConceptualDensity getConceptualDensity()
	{
		if (conceptualDensity == null)
			conceptualDensity = new ConceptualDensity(wn);
		return conceptualDensity;
	}
	
	/**
	 * <p>Compute and return a WordNet-intrinsic information content
	 * measure for a synset. The measure is computed according to 
	 * the following formula:<br><br>
	 * <code>IC(s) = 1 - log(hypo(s) + 1)/log(total_synset_count)</code>
	 * <br><br>
	 * where <code>hypo(s)</code> is the number of hyponyms of the synset
	 * and <code>total_synset_count</code> is the total number of synsets
	 * stored for the part-of-speech of <code>synset</code>.
	 * </p>
	 * 
	 * <p>
	 * This formula was proposed in the master's thesis of Nuno Seco titled
	 * "Computational Models of Similarity in Lexical Ontologies", University
	 * College Dublin. While in general information content is calculated
	 * as -log(p(synset)) where the probability p(synset) is extracted from
	 * a corpus, the idea here is to apply the <em>principle of cognitive
	 * saliency</em> (Zavaracky - "humans created new concepts where there is 
	 * need to differentiate from what already exists"). So, the probability
	 * of the top-level concept in the taxonomy is 1, and the probability
	 * of a leaf node is 1/total_synset_count and then the above formula follows.
	 * </p>
	 * 
	 * <p>
	 * To derive hypo(synset), for nouns all variants of the Isa relation are used
	 * while for verbs the Entails relation is used. Adjectives and adverbs are
	 * not organized in a taxonomical hierarchy and therefore for them this
	 * measure is meaningless and the method will return 0.
	 * </p>
	 * @param synset
	 * @return The information content of the synset concept for nouns and verbs, and
	 * 0 for all other parts of speech.
	 */
	public double getInformationContent(HGHandle synset)
	{
		SynsetLink s = graph.get(synset);
		if (s instanceof NounSynsetLink)
		{
			double hypo = countNounDescendents(synset);
			double total = hg.count(graph, hg.type(NounSynsetLink.class));
			return 1.0 - Math.log(hypo + 1.0)/Math.log(total);
		}
		else if (s instanceof VerbSynsetLink)
		{
			double hypo = countVerbDescendents(synset);
			double total = hg.count(graph, hg.type(VerbSynsetLink.class));
			return 1.0 - Math.log(hypo + 1.0)/Math.log(total);
		}
		else			
			return 0;
	}
	
	/**
	 * <p>Return all closest common parent between two sysnets in am <em>Isa</em> semantic 
	 * relationships hierarchy. Because isa relationships form a DAG, there may be more
	 * than one such ancestors at equal distance.
	 * </p>
	 * 
	 * @param synset1
	 * @param synset2
	 */
	public Set<HGHandle> getAllLeastCommonSubsumers(HGHandle synset1, HGHandle synset2)
	{
		Set<HGHandle> result = new HashSet<HGHandle>();
		
		if (synset1.equals(synset2))
		{
			result.add(synset1);
			return result;
		}

		// We proceed in a way similar to the algorithm that finds a single LCA (least
		// common ancestor) implemented in 'getLeastCommonSubsumer'. However, instead
		// of stopping at the first one that is found, we continue until all roots of the
		// hyponymy DAG are reached.
		// 
		// We must be careful to include only LCAs in the
		// result set. So for any ancestor that we find during the traversal, we must make
		// sure that it doesn't have a descendant which is LCA of synset1 and synset2. Depending
		// on the structure of the sub-graph that we are examining we may reach an ancestor
		// 'X' that is not an LCA but before we have examined the path which contains a the actual
		// LCA. The key observation in the following algorithm is that we will eventually 
		// examine that path at which point we can invalidate X as an LCA and remove it from
		// the result set. 
		//
		// All atoms reachable from an already found ancestor are ancestors also and could
		// potentially be reached from other paths and added as candidate LCAs. So we need
		// to keep track of all of them. 
		// 
		// Consider the set 'A' of all common ancestors to synset1 and synset2. This set
		// can be partitioned into the set 'R' of all LCAs (the result set) and the set 'I' 
		// of all the other ancestors which are not LCAs (the "invalid" set). So the algorithm
		// here maintains both the sets R and I in such a way that at the end
		// R contains only LCAs. R is represented by the variable 'result' and I by the variable
		// 'invalid'. When a common ancestor X is found below, the two set are updated as follows:
		//
		// if X's sibling in the Isa link is in I or R then
		// 		add X to I
		//      if X is in R, remove it from there
		// else if X itself is not in I or R then
		//	    add X to R
		//
		// Essentially, the first time an atom is found to be an ancestor, we add it to the
		// result set tentatively. We then keep adding its own ancestors to the set of invalid
		// ones. And as soon as an atom is found to be a parent of something already examined
		// it is added as an invalid ancestors and removed as a candidate of the result set.
		// Maybe to much explanation for something not so complicated, but anyway...
		
		Set<HGHandle> invalid = new HashSet<HGHandle>();
		
		HGTraversal t1 = new HGBreadthFirstTraversal(synset1, wn.isaRelatedGenerator(false, true));
		HGTraversal t2 = new HGBreadthFirstTraversal(synset2, wn.isaRelatedGenerator(false, true));
		
		while (true)
		{
			if (t1.hasNext())
			{
				Pair<HGHandle, HGHandle> x = t1.next();
				if (t2.isVisited(x.getSecond()))
				{
					HGHandle sibling = ((HGLink)graph.get(x.getFirst())).getTargetAt(0);
					if (invalid.contains(sibling) || result.contains(sibling))
					{
						invalid.add(x.getSecond());
						result.remove(x.getSecond());
					}
					else if (!invalid.contains(x.getSecond()) && !result.contains(x.getSecond()))
						result.add(x.getSecond());
				}
			}
			else if (!t2.hasNext())
				break;
			if (t2.hasNext())
			{
				Pair<HGHandle, HGHandle> x = t2.next();			
				if (t1.isVisited(x.getSecond()))
				{
					HGHandle sibling = ((HGLink)graph.get(x.getFirst())).getTargetAt(0);
					if (invalid.contains(sibling) || result.contains(sibling))
					{
						invalid.add(x.getSecond());
						result.remove(x.getSecond());
					}
					else if (!invalid.contains(x.getSecond()) && !result.contains(x.getSecond()))
						result.add(x.getSecond());					
				}
			}
		}		
		return result;
	}
	
    /**
     * <p>Return the closest common parent between two sysnets in am <em>Isa</em> semantic 
     * relationships hierarchy. A breadth-first traversal here stops as soon as one 
     * such ancestor is found.
     * </p>
     * 
     * @param synset1
     * @param synset2
     */	
	public HGHandle getLeastCommonSubsumer(HGHandle synset1, HGHandle synset2)
	{
		if (synset1.equals(synset2))
			return synset1;
		
		// The process here goes up the chains
		// of parents in the taxonomy DAG for both synsets and stops as soon as
		// a parent from one traversal is found in the other. Going breadth-first
		// ensures that the closest common parent is found indeed.
		
		HGTraversal t1 = new HGBreadthFirstTraversal(synset1, wn.isaRelatedGenerator(false, true));
		HGTraversal t2 = new HGBreadthFirstTraversal(synset2, wn.isaRelatedGenerator(false, true));
		
		while (true)
		{
			if (t1.hasNext())
			{
				Pair<HGHandle, HGHandle> x = t1.next();
				if (t2.isVisited(x.getSecond()))
					return x.getSecond();
			}
			else if (!t2.hasNext())
				break;
			if (t2.hasNext())
			{
				Pair<HGHandle, HGHandle> y = t2.next();			
				if (t1.isVisited(y.getSecond()))
					return y.getSecond();
			}
		}
		return null;
	}
	
	/**
	 * <p>Return the shortest number of ISA edges connecting two synsets or
	 * -1 if they are not connected. Note that noun senses are always connected, but
	 * verb senses not necessarily. The <code>Similar</code> will be used for adjectives
	 * while adverbs are not supported.</p>
	 */
	public long getPathLength(HGHandle s1, HGHandle s2)
	{
		SynsetLink x = graph.get(s1);
		SynsetLink y = graph.get(s2);
		if (!x.getClass().equals(y.getClass()))
			throw new IllegalArgumentException("Can't calculate path b/w different parts of speech.");
		HGALGenerator gen = null;
		if (x instanceof NounSynsetLink || x instanceof VerbSynsetLink)
			gen = wn.isaRelatedGenerator(true, true);
		else if (x instanceof AdjSynsetLink)
			gen = wn.relatedGenerator(Similar.class);
		else
			throw new IllegalArgumentException("Unsupported SynsetLink type for path length: " + x.getClass().getName());
		Double r = GraphClassics.dijkstra(s1, s2, gen);
		if (r == null)
			return -1;
		else
			return r.longValue();
	}
	
	/**
	 * <p>
	 * Calculate the Wu-Palmer similarity measure between two concepts. Because this
	 * measure assumes a taxonomy DAG (Directed-Acyclic-Graph) organization of the concepts,
	 * it only works for nouns and verb as they exhibit such an organization via IS-A
	 * relationships. The synsets s1 and s2 must be of the same type (either verb or noun
	 * sysnets). 
	 * </p>
	 * 
	 * <p>
	 * The measure is calculated by: <code>2*depth(lcs(s1, s2))/(depth(s1) + depth(s2))</code>
	 * where depth(x) is the number of edges from the root of the taxonomy to the concept x and
	 * lcs(s1,s2) is the least-common-subsumer of the two concepts s1 and s2. When s1 and
	 * </p>
	 *
	 * <p>
	 * If s1 == s2 then Double.MAX_VALUE is returned.
	 * </p> 
	 */
	public double getWuPalmerSimilarity(HGHandle s1, HGHandle s2)
	{
		SynsetLink x = graph.get(s1);
		SynsetLink y = graph.get(s2);
		if (!x.getClass().equals(y.getClass()))
			throw new IllegalArgumentException("Can't calculate similarity b/w different parts of speech.");
		HGHandle root = null;
		if (x instanceof NounSynsetLink)
			root = wn.getNounIsaRoot();
		else if (x instanceof VerbSynsetLink)
			root = wn.getVerbIsaRoot();
		else
			throw new IllegalArgumentException("Can only calculate Wu-Palmer similarity for nouns or verbs.");
		
		long xdepth = wn.getIsaDepth(s1, root);
		long ydepth = wn.getIsaDepth(s2, root);
		
		if (xdepth == -1 || ydepth == -1)
			return 0.0;
		
		Set<HGHandle> lcsSet = getAllLeastCommonSubsumers(s1, s2);
		double result = 0.0;
		double xydepths = xdepth + ydepth;
		for (HGHandle lcs : lcsSet)
		{
			long d = wn.getIsaDepth(lcs, root);
			if (d > 0)
				result = Math.max(result, 2.0*(double)d/xydepths);
		}
		return result;
	}
	
	/**
	 * <p>
	 * Calculate the Leacock-Chodorow similarty between two senses in WordNet. This
	 * measure assumes a hierarchically organized taxonomy and therefore works
	 * only for noun and verb senses. The synsets s1 and s2 must be of the same type 
	 * (either verb or noun sysnets). 
	 * </p>
	 * 
	 * <p>
	 * The measure calculates <code>-Log[dist(s1,s2)/(2*taxonomyDepth)]</code>
	 * where <code>taxonomyDepth</code> is the longest distance between the root
	 * and any leaf and <code>dist(s1, s2)</code> is the shortest distance between
	 * s1 and s2. The denominator is a scaling factor.
	 * </p>
	 *
	 * <p>
	 * If s1 == s2 then Double.MAX_VALUE is returned.
	 * </p>
	 */
	public double getLeacockChodorowSimilarity(HGHandle s1, HGHandle s2)
	{
		long pathLength = getPathLength(s1, s2);
		if (pathLength == -1)
			return Double.MAX_VALUE;
		else
		{			
			SynsetLink x = graph.get(s1);
			WNStat<Long> depthStatistic;
			if (x instanceof NounSynsetLink)			
				depthStatistic = wn.getStatistic(NounIsaDepth.class);
			else if (x instanceof VerbSynsetLink)
				depthStatistic = wn.getStatistic(VerbIsaDepth.class);
			else
				throw new IllegalArgumentException(
						"Can't calculate measure because the taxonomy doesn't have a max-depth statistic for this type of synset: " 
						+ x.getClass().getName());
			if (!depthStatistic.isCalculated())
				depthStatistic.calculate();
			return -Math.log((double)pathLength/2*depthStatistic.getValue());
		}
	}
	
	/**
	 * <p>
	 * Compute the Resnik similarity between two synsets that are either both verbs or nouns.
	 * The formula is <code>InformationContent(LeastCommonSubsumer(s1, s2))</code>. Where there
	 * is more than on LCS (a.k.a. L(east)C(ommon)A(ncestor)), the max over all of them is taken. 
	 * </p>
	 */
	public double getResnikSimilarity(HGHandle s1, HGHandle s2)
	{
		Set<HGHandle> lcsSet = getAllLeastCommonSubsumers(s1, s2);
		double result = 0.0;
		for (HGHandle h : lcsSet)
			result = Math.max(result, getInformationContent(h));
		return result;
	}
	
	/**
	 * <p>
	 * Compute the Jiang-Conrath similarity between two synsets that are either both verbs or nouns.
	 * The formula is <code>1 - [IC(s1) + IC(s2) - 2*IC(LCS(s1, s2))]/2</code>. IC refers to information
	 * content and LCS to least common subsumer. Where there
	 * is more than on LCS (a.k.a. L(east)C(ommon)A(ncestor)), the max of IC(LCS(s1, s2))
	 * over all of them is taken. 
	 * </p>
	 */
	public double getJiangConrathSimilarity(HGHandle s1, HGHandle s2)
	{
		Set<HGHandle> lcsSet = getAllLeastCommonSubsumers(s1, s2);
		double ics = 0.0;
		for (HGHandle h : lcsSet)
			ics = Math.max(ics, getInformationContent(h));
		return 1.0 -(getInformationContent(s1) + getInformationContent(s2) - 2.0*ics)/2.0;
	}

	/**
	 * <p>
	 * Compute the Lin similarity between two synsets that are either both verbs or nouns.
	 * The formula is <code>2*IC(LCS(s1,s2))/(IC(s1) + IC(s2))</code>. IC refers to information
	 * content and LCS to least common subsumer. Where there
	 * is more than on LCS (a.k.a. L(east)C(ommon)A(ncestor)), the max of IC(LCS(s1,s2))
	 * over all of them is taken. 
	 * </p>
	 */	
	public double getLinSimilarity(HGHandle s1, HGHandle s2)
	{
		Set<HGHandle> lcsSet = getAllLeastCommonSubsumers(s1, s2);
		double ics = 0.0;
		for (HGHandle h : lcsSet)
			ics = Math.max(ics, getInformationContent(h));
		return 2.0*ics/(getInformationContent(s1) + getInformationContent(s2));
	}
}