/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.util;


import java.util.Collection;


/**
 * A <code>BasicEList</code> that allows only {@link #isUnique unique} elements.
 */
public class UniqueEList<E> extends BasicEList<E>
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates an empty instance with no initial capacity.
   */
  public UniqueEList()
  {
    super();
  }

  /**
   * Creates an empty instance with the given capacity.
   * @param initialCapacity the initial capacity of the list before it must grow.
   * @exception IllegalArgumentException if the <code>initialCapacity</code> is negative.
   */
  public UniqueEList(int initialCapacity)
  {
    super(initialCapacity);
  }

  /**
   * Creates an instance that is a copy of the collection, with duplicates removed.
   * @param collection the initial contents of the list.
   */
  public UniqueEList(Collection<? extends E> collection)
  {
    super(collection.size());
    addAll(collection);   
  }

  /**
   * Returns <code>true</code> because this list requires uniqueness.
   * @return <code>true</code>.
   */
  @Override
  protected boolean isUnique()
  {
    return true;
  }

  /**
   * A <code>UniqueEList</code> that {@link #useEquals uses} <code>==</code> instead of <code>equals</code> to compare members.
   */
  public static class FastCompare<E> extends UniqueEList<E>
  {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty instance with no initial capacity.
     */
    public FastCompare()
    {
      super();
    }

    /**
     * Creates an empty instance with the given capacity.
     * @param initialCapacity the initial capacity of the list before it must grow.
     * @exception IllegalArgumentException if the <code>initialCapacity</code> is negative.
     */
    public FastCompare(int initialCapacity)
    {
      super(initialCapacity);
    }

    /**
     * Creates an instance that is a copy of the collection, with duplicates removed.
     * @param collection the initial contents of the list.
     */
    public FastCompare(Collection<? extends E> collection)
    {
      super(collection.size());
      addAll(collection);
    }

    /**
     * Returns <code>false</code> because this list uses <code>==</code>.
     * @return <code>false</code>.
     */
    @Override
    protected boolean useEquals()
    {
      return false;
    }
  }
}
