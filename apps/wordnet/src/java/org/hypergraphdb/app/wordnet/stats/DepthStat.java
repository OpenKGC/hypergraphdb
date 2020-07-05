package org.hypergraphdb.app.wordnet.stats;

import java.util.LinkedList;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGDepthFirstTraversal;
import org.hypergraphdb.app.wordnet.WNStat;
import org.hypergraphdb.app.wordnet.data.Isa;
import org.hypergraphdb.util.Pair;

/**
 * 
 * <p>
 * A statistic that stores the maximal hierarchical depth within a WordNet
 * semantic relationship hierarchy (assumed acyclical).
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public abstract class DepthStat extends WNStat<Long>
{
	protected Long doCalculation(HGHandle root, HGALGenerator generator)
	{		
		// A stack that maintains the current chain being explored in a depth first traversal.
		LinkedList<Pair<HGHandle, Long>> currentChain = new LinkedList<Pair<HGHandle, Long>>();
		// Keep track of the maximum depth found
		long resultDepth = 0;
		currentChain.addLast(new Pair<HGHandle, Long>(root, 0l));
		HGDepthFirstTraversal traversal = new HGDepthFirstTraversal(root, generator); 
		while (traversal.hasNext())
		{
			Pair<HGHandle, HGHandle> step = traversal.next();
			// To check whether our last node in the chain was a leaf. It is
			// if the current link being examined doesn't contain it in its 
			// parent target position (i.e. position 1).
			Isa isa = graph.get(step.getFirst());
			if (!isa.getTargetAt(1).equals(currentChain.getLast().getFirst()))
			{
				resultDepth = Math.max(resultDepth, currentChain.getLast().getSecond());
				// rewind the current chain stack
				while (!currentChain.getLast().getFirst().equals(isa.getTargetAt(1)))
					currentChain.removeLast();
			}
			currentChain.addLast(new Pair<HGHandle, Long>(
				step.getSecond(), currentChain.getLast().getSecond() + 1));			
		}
		return resultDepth;
	}	
}
