package org.hypergraphdb.app.ann;

/**
 * <p>
 * Represents an differentiable function to be used in neuron activation
 * and backpropagation.
 * </p>
 */
public interface ActivationFunction
{
	/**
	 * Evaluate the function at a point.
	 */
    double eval(double x);
    
	/**
	 * Evaluate the derivative of the function at a point.
	 */
    double deval(double x); 
}
