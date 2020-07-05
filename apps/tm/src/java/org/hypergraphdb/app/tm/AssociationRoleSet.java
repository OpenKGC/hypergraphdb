package org.hypergraphdb.app.tm;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.util.HGUtils;
import org.tmapi.core.TMAPIRuntimeException;

/**
 * 
 * <p>
 * This exposes the target set of an association's roles as a Java <code>Set</code>
 * as required by the TMAPI.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
class AssociationRoleSet implements Set<HGAssociationRole>
{
	HyperGraph graph;
	HGAssociation ass;

	AssociationRoleSet(HyperGraph graph, HGAssociation ass)
	{	
		this.graph  = graph;
		this.ass = ass;
	
	}

	HGAssociationRole get(int i)
	{
		HGAssociationRole r = (HGAssociationRole)graph.get(ass.targetSet[i]);
		r.graph = graph;
		return r;
	}
	
	public boolean add(HGAssociationRole o)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends HGAssociationRole> c)
	{
		throw new UnsupportedOperationException();	
	}

	public void clear()
	{
		throw new UnsupportedOperationException();	
	}

	public boolean contains(Object o)
	{
		for (HGHandle h : ass.targetSet)
			if (HGUtils.eq(o, graph.get(h)))
				return true;
		return false;
	}

	public boolean containsAll(Collection<?> c)
	{
		for (Object x : c)
			if (!contains(x))
				return false; 
		return true;
	}

	public boolean isEmpty()
	{
		return ass.targetSet.length == 0;
	}

	public Iterator<HGAssociationRole> iterator()
	{
		final int currSize = size();
		return new Iterator<HGAssociationRole>()
		{
			int i = 0;
			public void remove() { throw new UnsupportedOperationException(); }
			public boolean hasNext() 
			{
				if (currSize != ass.targetSet.length)
					throw new ConcurrentModificationException();
				return i < ass.targetSet.length; 
			}
			public HGAssociationRole next()  
			{ 
				if (currSize != ass.targetSet.length)
					throw new ConcurrentModificationException();				
				return get(i++); 
			} 		
		};
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();	
	}

	public boolean removeAll(Collection<?> c)
	{
		HGAssociationRole [] roles = new HGAssociationRole[ass.targetSet.length];
		for (int i = 0; i < roles.length; i++)
			roles[i] = (HGAssociationRole)graph.get(ass.targetSet[i]);
		for (HGAssociationRole r:roles)
			try { r.remove(); } catch (Exception ex) { throw new TMAPIRuntimeException(ex); }
		return true;
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();	}

	public int size()
	{
		return ass.targetSet.length;
	}

	public Object[] toArray()
	{
		Object [] result = new Object[ass.targetSet.length];
		for (int i = 0; i < ass.targetSet.length; i++)
			result[i] = get(i);
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a)
	{
        if (a.length < ass.targetSet.length)
            a = (T[])java.lang.reflect.Array.
            		newInstance(a.getClass().getComponentType(), ass.targetSet.length);        
		for (int i = 0; i < ass.targetSet.length; i++)
			a[i] = (T)get(i);
		return a;
	}	
}