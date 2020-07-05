package org.hypergraphdb.app.wordnet.stats;

import org.hypergraphdb.app.wordnet.WNGraph;
import org.hypergraphdb.app.wordnet.data.KindOf;

/**
 * 
 * <p>
 * This statistic calculates the maximal depth of the {@KindOf} relationship DAG. 
 * <code>Isa</code>.
 * </p>
 *
 * <p>
 * This is calculated by doing a depth-first traversal starting from the verb
 * sense root as returned by {@link WNGraph#getVerbIsaRoot()}. 
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class VerbIsaDepth extends DepthStat
{
	@Override
	protected Long doCalculation()
	{
		WNGraph wngraph = new WNGraph(graph);
		return super.doCalculation(wngraph.getVerbIsaRoot(), 
								   wngraph.relatedGenerator(KindOf.class, false));

	}
}