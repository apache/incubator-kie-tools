/**
 * Copyright (c) 2005-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.Array;
import org.eclipse.emf.common.util.BasicEList;


/**
 * A {@link BasicEList basic list} that implements {@link InternalEList}.
 */
public class BasicInternalEList<E> extends BasicEList<E> implements InternalEList<E>
{
  private static final long serialVersionUID = 1L;

  protected final Class<? extends E> dataClass;

  public BasicInternalEList(Class<? extends E> dataClass)
  {
    super();
    this.dataClass = dataClass;
  }

  public BasicInternalEList(Class<? extends E> dataClass, int initialCapacity)
  {
    super();
    this.dataClass = dataClass;

    if (initialCapacity < 0)
    {
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);  
    }

    data = newData(initialCapacity);
  }

  public BasicInternalEList(Class<? extends E> dataClass, Collection<? extends E> collection)
  {
    super();
    this.dataClass = dataClass;
    size = collection.size();

    // Conditionally create the data.
    //
    if (size > 0)
    { 
      // Allow for a bit-shift of growth.
      //
      data = newData(size + size / 8 + 1); 
      collection.toArray(data);
    }
  }

  public BasicInternalEList(Class<? extends E> dataClass, int size, Object[] data)
  {
    super(size, data);
    this.dataClass = dataClass;
  }

  @Override
  protected Object [] newData(int capacity)
  {
    return (Object [])Array.newInstance(dataClass, capacity);
  }

  public NotificationChain basicRemove(Object object, NotificationChain notifications)
  {
    super.remove(object);
    return notifications;
  }

  public NotificationChain basicAdd(E object, NotificationChain notifications)
  {
    super.add(object);
    return notifications;
  }

  @Override
  public Iterator<E> basicIterator()
  {
    return super.basicIterator();
  }

  @Override
  public List<E> basicList()
  {
    return super.basicList();
  }

  @Override
  public ListIterator<E> basicListIterator()
  {
    return super.basicListIterator();
  }

  @Override
  public ListIterator<E> basicListIterator(int index)
  {
    return super.basicListIterator(index);
  }

  public boolean basicContains(Object object)
  {
    return super.contains(object);
  }

  public boolean basicContainsAll(Collection<?> collection)
  {
    return super.containsAll(collection);
  }

  public int basicIndexOf(Object object)
  {
    return super.indexOf(object);
  }

  public int basicLastIndexOf(Object object)
  {
    return super.lastIndexOf(object);
  }

  public Object[] basicToArray()
  {
    return super.toArray();
  }

  public <T> T[] basicToArray(T[] array)
  {
    return super.toArray(array);
  }
}
