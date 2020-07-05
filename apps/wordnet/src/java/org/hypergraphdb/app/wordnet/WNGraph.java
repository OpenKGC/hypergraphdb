package org.hypergraphdb.app.wordnet;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.app.wordnet.data.*;
import org.hypergraphdb.atom.HGAtomSet;
import org.hypergraphdb.algorithms.*;
import org.hypergraphdb.handle.UUIDHandleFactory;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.util.HGUtils;
import org.hypergraphdb.util.Pair;

/**
 * 
 * <p>
 * A collection of utility methods for applying graph traversals and
 * algorithms onto the HyperGraph representation of WordNet.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class WNGraph
{
	private HyperGraph graph;
	private Map<Class<? extends WNStat<?>>, WNStat<?>> stats = new HashMap<Class<? extends WNStat<?>>, WNStat<?>>();
	
	private static HGPersistentHandle VERB_ISA_ROOT = 
		UUIDHandleFactory.I.makeHandle("ec0de085-4ed6-4b73-b520-cb21406c1881");
	
	public WNGraph(HyperGraph graph)
	{
		this.graph = graph;
	}
	
	/**
	 * <p>Return the underlying HyperGraph instance.</p>
	 */
	public HyperGraph getGraph() 
	{
		return graph;
	}

	/**
	 * <p>
	 * Return the <code>HGHandle</code> of a WordNet <code>Word</code> instance given its lemma.
	 * </p>
	 * 
	 * @param lemma
	 * @return
	 */
	public HGHandle findWord(String lemma)
	{
		return hg.findOne(graph, hg.and(hg.type(Word.class), hg.eq("lemma", lemma)));
	}
	
	/**
	 * <p>
	 * Return the value of a global WordNet statistic by its type or null if that statistic
	 * hasn't been calculated yet. 
	 * </p>
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public synchronized <T> T getStatisticValue(Class<? extends WNStat<T>> type)
	{
		return getStatistic(type).getValue();
	}
	
	/**
	 * <p>
	 * Return the instance of a global WordNet statistic by its type. A new instance will
	 * be created and added to the HyperGraphDB if it doesn't already exist. Note that
	 * the statistic will <b>NOT</b> automatically calculated. If you have not previously
	 * triggered a calculation of the statistic and want to lazily calculate it as soon
	 * as it is needed, call its <code>isCalculated</code> to check and trigger the 
	 * calculation on the spot.
	 * </p>
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")	
	public synchronized <T> WNStat<T> getStatistic(Class<? extends WNStat<T>> type)
	{
		WNStat<T> s = (WNStat<T>)stats.get(type);		
		if (s == null)
		{
			s = hg.getOne(graph, hg.type(type));
			if (s == null)
				try
				{
					s = (WNStat<T>)type.newInstance();
					s.setHyperGraph(graph);
					graph.add(s);
				}
				catch (Exception ex)
				{
					throw new RuntimeException(ex);
				}
			stats.put(type, s);			
		}
		return s;
	}
	
	/**
	 * <p>
	 * Find the top-level ancestor of node in an IS-A hierarchy. Note that this
	 * doesn't perform any cycle detection - it could loop forever if the underlying
	 * graph structure is not a DAG.
	 * </p>
	 * 
	 * @param synset A sense participating in an IS-A hierarchy.
	 * @return The top-level ancestor of <code>synset</code>
	 */
	public HGHandle findIsaRoot(HGHandle synset)
	{
		HGALGenerator gen = isaRelatedGenerator(false, true);
	    while (true)
	    {
	    	HGSearchResult<Pair<HGHandle, HGHandle>> rs = gen.generate(synset);
	    	try
	    	{
		    	if (!rs.hasNext()) 
		    		return synset;
		        synset = rs.next().getSecond();
	    	}
	    	finally
	    	{
				HGUtils.closeNoException(rs);
	    	}
	    }	
	}
	
	/**
	 * <p>
	 * Assuming <code>synset</code> is a descendant of <code>root</code> in an IS-A
	 * hierarchy, finding the distance between them.
	 * </p>
	 * 
	 * <p>
	 * In case the method finds out that <code>synset</code> is not a descendent of 
	 * <code>root</code>, then <code>-1</code> is returned. 
	 * </p>
	 * 
	 * @param synset The descendant.
	 * @param root The root.
	 * @return The distance between <code>synset</code> and <code>root</code> in an IS-A DAG.
	 */
	public long getIsaDepth(HGHandle synset, HGHandle root)
	{
		HGALGenerator gen = isaRelatedGenerator(false, true);
		Double r = GraphClassics.dijkstra(synset, root, gen);
		if (r == null)
			return -1;
		else
			return r.longValue();
	}
	
	/**
	 * <p>
	 * Retrieve the root of the noun ISA hierarchy. Recent versions of WordNet have the word
	 * "entity" mapped in a single synset which is the top-level noun sense.
	 * </p>
	 * 
	 * @return
	 */
	public HGHandle getNounIsaRoot()
	{
		HGHandle wh = hg.findOne(graph, hg.and(hg.type(Word.class), hg.eq("lemma", "entity")));
		return hg.findOne(graph, hg.and(hg.type(NounSynsetLink.class), hg.incident(wh)));
	}
	
	/** 
	 * <p>Return the root verb sense of the IS-A hierarchy. Unlike nouns, WordNet
	 * doesn't define a top-level verb sense (simply because there isn't a good enough
	 * candidate in the English vocabulary). However, for computational purposes we
	 * can connect all top-level nodes in the verb IS-A DAG to a single root.</p>
	 * 
	 * <p>
	 * This method will return the dummy verb sense that serves as the top-level node.
	 * If it hasn't been already create, it will create and connect it to all existing
	 * top-level nodes. Which, of course, is somewhat computational intensive.
	 * </p>
	 */
	public HGHandle getVerbIsaRoot()
	{
		if (graph.get(VERB_ISA_ROOT) != null)
			return VERB_ISA_ROOT;
		
		// Otherwise, we have to find all roots, add the VERB_ISA_ROOT as an
		// empty synset and connect them to it.
		HGAtomSet verbRoots = new HGAtomSet();
		HGAtomSet visited = new HGAtomSet();
		HGALGenerator gen = isaRelatedGenerator(false, true);
		HGSearchResult<HGHandle> rs = graph.find(hg.type(VerbSynsetLink.class));
		try
		{
			while (rs.hasNext())
			{
				HGHandle current = rs.next();
				if (visited.contains(current))
					continue;				
			    while (true)
			    {
			    	visited.add(current);
			    	HGSearchResult<Pair<HGHandle, HGHandle>> siblings = gen.generate(current);
			    	try
			    	{
				    	if (!siblings.hasNext()) 
				    	{
				    		verbRoots.add(current);
				    		break;
				    	}
				        current = siblings.next().getSecond();
				        if (visited.contains(current))
				        	break;
			    	}
			    	finally
			    	{
						HGUtils.closeNoException(siblings);
			    	}
			    }				
			}
		}
		finally
		{
			HGUtils.closeNoException(rs);			
		}
		
		VerbSynsetLink theroot = new VerbSynsetLink();
		graph.define(VERB_ISA_ROOT, theroot);
		for (HGHandle r : verbRoots)
			graph.add(new KindOf(r, (HGHandle)VERB_ISA_ROOT));
		return VERB_ISA_ROOT;
	}
	
	/**
	 * <p>
	 * Return a generator for the <code>Isa</code> semantic relationships. The generator
	 * will examine atoms that are related to the current one through <code>Isa</code> or
	 * one of its derived relationship types (InstanceOf, KindOf).
	 * </p>
	 * 
	 * @param returnChildren Whether to include more specific (i.e. hyponym) concepts
	 * than the current one being examined.
	 * @param returnParents Whether to include more general (i.e. hypernym) concepts
	 * than the current one being examined.
	 * @return The adjency generator.
	 */
	public HGALGenerator isaRelatedGenerator(boolean returnChildren, boolean returnParents)
	{
		final HGAtomPredicate isaPred = hg.type(Isa.class);
		final HGAtomPredicate kindOfPred = hg.type(KindOf.class);
		final HGAtomPredicate instanceOfPred = hg.type(InstanceOf.class);
		final HGAtomPredicate pred = new HGAtomPredicate()
		{
			public boolean satisfies(HyperGraph graph, HGHandle handle)
			{
				return kindOfPred.satisfies(graph, handle) ||
					   instanceOfPred.satisfies(graph, handle) ||
					   isaPred.satisfies(graph, handle);
			}
		};
		return new DefaultALGenerator(graph, pred, null, returnChildren, returnParents, false);
	}

	/**
	 * <p>Get a generator for a symmetric semantic relationship (such as the <code>Similar</code>
	 * relationships between adjective senses.</p>
	 */
	public HGALGenerator relatedGenerator(Class<? extends SemanticLink> type)
	{
		return new DefaultALGenerator(graph, hg.type(type), null, true, true, false);
	}
	
	/**
	 * <p>Get an atom adjency generator for a WordNet semantic relationship.</p>
	 * 
	 * @param type One of the <code>SemanticLink</code> sub-classes.
	 * @param leftToRight <code>true</code> if the second targets of the relationships
	 * should be traversed and <code>false</code> if the first targets should be
	 * traversed.  
	 */
	public HGALGenerator relatedGenerator(Class<? extends SemanticLink> type, boolean leftToRight)
	{
		return new DefaultALGenerator(graph, hg.type(type), null, !leftToRight, leftToRight, false);
	}
	
	public HGTraversal relatedDepthFirst(HGHandle start, Class<? extends SemanticLink> type, boolean leftToRight)
	{
		return new HGDepthFirstTraversal(start, relatedGenerator(type, leftToRight));
	}
	
	public HGTraversal relatedBreadthFirst(HGHandle start, Class<? extends SemanticLink> type, boolean leftToRight)
	{
		return new HGBreadthFirstTraversal(start, relatedGenerator(type, leftToRight));
	}
	
	public List<HGHandle> findRelated(HGHandle synset, Class<? extends SemanticLink> type, boolean leftToRight)
	{
		return hg.findAll(graph, hg.apply(hg.targetAt(graph, leftToRight ? 1 : 0),
				hg.and(hg.type(type), 
					   hg.incident(synset), 
					   hg.orderedLink(leftToRight ? synset : graph.getHandleFactory().anyHandle(),
							   	      leftToRight ? graph.getHandleFactory().anyHandle() : synset))
				));
	}
	
	/**
	 * <p>
	 * Return a WordNet sense by its offset/id in the original WordNet files. Note that
	 * this id is not stable across WordNet versions. However, in general, there is a way
	 * to convert ids between consecutive versions so it's relatively safe to use it as
	 * an identifier outside HyperGraphDB.
	 * </p>
	 * 
	 * @param id
	 * @return
	 */
	public HGHandle getSenseById(long id)
	{
		return hg.findOne(graph, hg.and(hg.typePlus(SynsetLink.class), hg.eq("id", id)));
	}
	
	/**
	 * <p>Retrieve the sense type (i.e. the concrete <code>SynsetLink</code> type atom)
	 * for a given part of speech.</p>
	 * 
	 * @param pos The part of speech.
	 * @return the <code>HGHandle</code> of the type atom representing the sense type.
	 */
	public HGHandle getSenseType(Pos pos)
	{
		switch (pos)
		{
			case noun : return graph.getTypeSystem().getTypeHandle(NounSynsetLink.class);
			case verb : return graph.getTypeSystem().getTypeHandle(VerbSynsetLink.class);
			case adverb : return graph.getTypeSystem().getTypeHandle(AdverbSynsetLink.class);
			case adjective : return graph.getTypeSystem().getTypeHandle(AdjSynsetLink.class);
			default: return null;
		}
	}

	/**
	 * <p>
	 * Retrieve all senses of a particular word if used as a specific part-of-speech.
	 * The part-of-speech is identified by the <code>senseType</code> parameter.
	 * </p>
	 * <p>
	 * Alternatively, you can use one of the <code>get<em>T</em>Senses</code> methods
	 * where <em>T</em> is one of <em>Noun</em>, <em>Verb</em>, <em>Adverb</em> or
	 * <em>Adj</em>.
	 * </p>
	 * @param word The word handle.
	 * @param senseType The sense type - this corresponds to one of the sub-classes of
	 * the {@link SynsetLink} class. 
	 * @return
	 */
	public List<HGHandle> getSenses(HGHandle word, HGHandle senseType)
	{
		return hg.findAll(graph, hg.and(hg.type(senseType), hg.incident(word)));
	}

	/**
	 * <p>
	 * Retrieve all senses of a particular word if used as a specific part-of-speech.
	 * The part-of-speech is identified by the <code>senseType</code> parameter.
	 * </p>
	 * <p>
	 * Alternatively, you can use one of the <code>get<em>T</em>Senses</code> methods
	 * where <em>T</em> is one of <em>Noun</em>, <em>Verb</em>, <em>Adverb</em> or
	 * <em>Adj</em>.
	 * </p>
	 * @param word The word handle.
	 * @param senseType The sense type - one of the sub-classes of
	 * the {@link SynsetLink} class. 
	 * @return
	 */	
	public List<HGHandle> getSenses(HGHandle word, Class<? extends SynsetLink> type)
	{
		return hg.findAll(graph, hg.and(hg.type(type), hg.incident(word)));
	}	
	
	/**
	 * <p>
	 * Return all senses of <code>word</code> when used as a noun.
	 * </p>
	 */
	public List<HGHandle> getNounSenses(HGHandle word)
	{
		return getSenses(word, NounSynsetLink.class);
	}

	/**
	 * <p>
	 * Return all senses of <code>word</code> when used as a verb.
	 * </p>
	 */
	public List<HGHandle> getVerbSenses(HGHandle word)
	{
		return getSenses(word, VerbSynsetLink.class);
	}
	
	/**
	 * <p>
	 * Return all senses of <code>word</code> when used as an adverb.
	 * </p>
	 */
	public List<HGHandle> getAdverbSenses(HGHandle word)
	{
		return getSenses(word, AdverbSynsetLink.class);
	}
	
	/**
	 * <p>
	 * Return all senses of <code>word</code> when used as an adjective.
	 * </p>
	 */
	public List<HGHandle> getAdjSenses(HGHandle word)
	{
		return getSenses(word, AdjSynsetLink.class);
	}	
}