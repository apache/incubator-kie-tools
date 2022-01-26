/**
 * Copyright (c) 2007-2010 IBM Corporation and others.
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

import org.eclipse.emf.common.notify.impl.DelegatingNotifyingListImpl;

/**
 * A {@link DelegatingNotifyingListImpl delegating notifying list} that implements {@link InternalEList}.
 * @since 2.4
 */
public abstract class DelegatingNotifyingInternalEListImpl<E> extends DelegatingNotifyingListImpl<E> implements InternalEList<E>
{
  private static final long serialVersionUID = 1L;

  public DelegatingNotifyingInternalEListImpl()
  {
    super();
  }

  public DelegatingNotifyingInternalEListImpl(Collection<? extends E> collection)
  {
    super(collection);
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

  @Override
  public E basicGet(int index)
  {
    return super.basicGet(index);
  }

  @Override
  public List<E> basicList()
  {
    return super.basicList();
  }
  
  @Override
  public Iterator<E> basicIterator()
  {
    return super.basicIterator();
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
}
