package org.hypergraphdb.app.wordnet.stats;

import org.hypergraphdb.app.wordnet.WNGraph;

/**
 * 
 * <p>
 * This statistic calculates the maximal depth of the IS-A relationship DAG. This
 * includes all <code>Isa</code> relations and all relations from sub-types of 
 * <code>Isa</code>.
 * </p>
 *
 * <p>
 * This is calculated by doing a depth-first traversal starting from the noun
 * sense root (the WordNet "entity" sense). Every time a leaf in the DAG is reached
 * its depth is replaced as the tentative result if it is greater than the current
 * one. 
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class NounIsaDepth extends DepthStat
{
	@Override
	protected Long doCalculation()
	{	
		WNGraph wngraph = new WNGraph(graph);
		return super.doCalculation(wngraph.getNounIsaRoot(), 
								   wngraph.isaRelatedGenerator(true, false));
	}
}