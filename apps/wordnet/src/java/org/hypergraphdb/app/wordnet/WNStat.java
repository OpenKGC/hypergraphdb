package org.hypergraphdb.app.wordnet;

import java.util.Date;

import org.hypergraphdb.HGGraphHolder;
import org.hypergraphdb.HyperGraph;

/**
 * 
 * <p>
 * Represents some global WordNet statistic that can be calculated and saved in the
 * HyperGraphDB instance. Global statistics usually take time to calculate and they
 * are stable for a given WordNet version/dataset. A single <code>WNStat</code>
 * implementation holds the value calculated and implements the actual algorithm
 * to do the calculation. The resulting value of the statistic can be anything,
 * from a simple number to a complex structure, as long as it can be stored in
 * HyperGraphDB (i.e. the type system can handle it). 
 * </p>
 * 
 * <p>
 * <code>WNStat</code> are singletons automatically stored in the database. To get
 * the instance of a specific <code>WNStat</code> call the {@link WNGraph#getStatistic(Class)}
 * method.
 * </p>
 * 
 * <p>
 * Because the type of the result value is arbitrary, this
 * class can in fact by used to manage any sort of pre-computed values on the WordNet database,
 * not just statistics in the number/mathematical sense
 * But we keep the name <code>WNStat</code> because it reflects the main intent.
 * </p>
 * 
 * <p>
 * The main restriction on the value of a <code>WNStat</code> is that it can't be
 * <code>null</code> as that particular value indicates that the computation hasn't
 * been performed yet.
 * </p>
 * 
 * <p>
 * Note: implementations should be default constructible.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public abstract class WNStat<T> implements HGGraphHolder
{
	private Date calculationTimestamp = null;
	private T value = null;
	protected HyperGraph graph = null;
	
	/**
	 * <p>
	 * Implement this method to perform the actual calculation of this
	 * statistic and return its value.
	 * </p>
	 */
	protected abstract T doCalculation();

	/**
	 * <p>Perform a calculation of this statistic, regardless of whether it
	 * already has a value or not. Then update its value as well as the time stamp
	 * of the calculation in the HyperGraphDB storage and return the value.
	 * </p>
	 *   
	 * @return The value of the statistic.
	 */
	public T calculate()
	{
		value = doCalculation();
		calculationTimestamp = new Date();
		graph.update(this);
		return value;
	}
	
	/**
	 * <p>Return <code>true</code> if this statistic has already been computed and
	 * <code>false</code> otherwise. In general, this method should be called before
	 * attempting to get the value of the statistic.
	 */
	public boolean isCalculated() 
	{ 
		return value != null; 
	}

	/**
	 * <p>Return the value of this statistic or <code>null</code> if it hasn't
	 * been computed yet.</p>
	 */
	public T getValue()
	{
		return value;
	}
	
	/**
	 * <p>
	 * Set this statistic's value - use with caution. 
	 * </p>
	 * 
	 * @param value
	 */
	public void setValue(T value)
	{
		this.value = value;
	}

	/**
	 * <p>Return the precise date/time at which computation of
	 * this statistic completed, or <code>null</code> if the computation
	 * has been completed yet.
	 */
	public Date getCalculationTimestamp()
	{
		return calculationTimestamp;
	}

	/**
	 * <p>
	 * Set the precise date/time of the completion of the calculation
	 * of this statistic - not intended for public use.
	 * </p>
	 * 
	 * @param calculationTimestamp
	 */
	public void setCalculationTimestamp(Date calculationTimestamp)
	{
		this.calculationTimestamp = calculationTimestamp;
	}

	/**
	 * <p>Return the <code>HyperGraph</code> instance holding the data
	 * upon which this statistic was computed.</p>
	 */
	public HyperGraph getHyperGraph()
	{
		return graph;
	}

	/**
	 * <p>
	 * Set the <code>HyperGraph</code> instance holding the data
	 * upon which this statistic was computed - intented for internal use
	 * only.
	 * </p>
	 */
	public void setHyperGraph(HyperGraph graph)
	{
		this.graph = graph;
	}	
}