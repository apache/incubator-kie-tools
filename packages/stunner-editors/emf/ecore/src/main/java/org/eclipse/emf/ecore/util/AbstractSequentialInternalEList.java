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

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.NotificationChain;

/**
 * An abstract implementation of {@link InternalEList}.
 * Clients must override either {@link #basicList()} or {@link #basicListIterator(int)}
 * since these two methods delegate to each other and unless that cycle is broken will lead to stack overflow.
 * @since 2.4
 */
public abstract class AbstractSequentialInternalEList<E> extends AbstractSequentialList<E> implements InternalEList<E>
{
  protected AbstractSequentialInternalEList()
  {
    super();
  }

  public boolean addAllUnique(Collection<? extends E> collection)
  {
    return addAllUnique(size(), collection);
  }

  public boolean addAllUnique(int index, Collection<? extends E> collection)
  {
    return addAll(index, collection);
  }

  public void addUnique(E object)
  {
    addUnique(size(), object);
  }

  public void addUnique(int index, E object)
  {
    add(index, object);
  }

  public NotificationChain basicAdd(E object, NotificationChain notifications)
  {
    addUnique(object);
    return notifications;
  }

  public E basicGet(int index)
  {
    return basicList().get(index);
  }

  public Iterator<E> basicIterator()
  {
    return basicListIterator();
  }

  public List<E> basicList()
  {
    return
      new AbstractSequentialList<E>()
      {
        @Override
        public ListIterator<E> listIterator(int index)
        {
          return basicListIterator(index);
        }

        @Override
        public int size()
        {
          return AbstractSequentialInternalEList.this.size();
        }
      };
  }

  public ListIterator<E> basicListIterator()
  {
    return basicListIterator(0);
  }

  public ListIterator<E> basicListIterator(int index)
  {
    return basicList().listIterator(index);
  }

  public NotificationChain basicRemove(Object object, NotificationChain notifications)
  {
    remove(object);
    return notifications;
  }

  public boolean basicContains(Object object)
  {
    return basicList().contains(object);
  }
  
  public boolean basicContainsAll(Collection<?> collection)
  {
    return basicList().containsAll(collection);
  }
  
  public int basicIndexOf(Object object)
  {
    return basicList().indexOf(object);
  }
  
  public int basicLastIndexOf(Object object)
  {
    return basicList().lastIndexOf(object);
  }

  public Object[] basicToArray()
  {
    return basicList().toArray();
  }

  public <T> T[] basicToArray(T[] array)
  {
    return basicList().toArray(array);
  }

  public E setUnique(int index, E object)
  {
    return set(index, object);
  }

  public void move(int newPosition, E object)
  {
    remove(object);
    add(newPosition, object);
  }

  public E move(int newPosition, int oldPosition)
  {
    E movedObject = remove(oldPosition);
    add(newPosition, movedObject);
    return movedObject;
  }
}
