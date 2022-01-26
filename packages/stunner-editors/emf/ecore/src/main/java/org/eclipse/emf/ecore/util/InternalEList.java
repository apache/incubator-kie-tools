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
package org.eclipse.emf.ecore.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;


/**
 * An interface that the {@link EObject#eGet(EStructuralFeature) value} of {@link ETypedElement#isMany() multi-valued} {@link EStructuralFeature feature} 
 * is expected to implement.
 * Clients should generally extend one of the the existing lists that implement this API to better accommodate the addition of methods,
 * e.g.,
 * {@link AbstractSequentialInternalEList},
 * {@link BasicInternalEList},
 * {@link NotifyingInternalEListImpl},
 * {@link DelegatingNotifyingInternalEListImpl},
 * {@link EcoreEList.UnmodifiableEList},
 * {@link DelegatingEcoreEList.UnmodifiableEList},
 * or a subclass of one of the above.
 */
public interface InternalEList<E> extends EList<E>
{
  /**
   * Returns the unresolved value.
   */
  E basicGet(int index);

  /**
   * Returns an unmodifiable list that yields unresolved values.
   */
  List<E> basicList();

  /**
   * Returns an iterator that yields unresolved values.
   */
  Iterator<E> basicIterator();

  /**
   * Returns a list iterator that yields unresolved values.
   */
  ListIterator<E> basicListIterator();

  /**
   * Returns a list iterator that yields unresolved values.
   */
  ListIterator<E> basicListIterator(int index);

  /**
   * Returns the array with unresolved values.
   * @since 2.4
   */
  Object[] basicToArray(); 

  /**
   * Returns the array with unresolved values.
   * @since 2.4
   */
  <T> T[] basicToArray(T[] array); 

  /**
   * Returns the index of the object within the list of unresolved values.
   * @since 2.4
   */
  int basicIndexOf(Object object);

  /**
   * Returns the last index of the object within the list of unresolved values.
   * @since 2.4
   */
  int basicLastIndexOf(Object object);

  /**
   * Returns whether the object is contained within the list of unresolved values.
   * @since 2.4
   */
  boolean basicContains(Object object);

  /**
   * Returns whether each object in the collection is contained within the list of unresolved values.
   * @since 2.4
   */
  boolean basicContainsAll(Collection<?> collection);

  /**
   * Removes the object with without updating the inverse.
   */
  NotificationChain basicRemove(Object object, NotificationChain notifications);

  /**
   * Adds the object without updating the inverse.
   */
  NotificationChain basicAdd(E object, NotificationChain notifications);

  /**
   * Adds the object without verifying uniqueness.
   */
  void addUnique(E object);

  /**
   * Adds the object without verifying uniqueness.
   */
  void addUnique(int index, E object);

  /**
   * Adds the objects without verifying uniqueness.
   * @since 2.4
   */
  boolean addAllUnique(Collection<? extends E> collection);

  /**
   * Adds the objects without verifying uniqueness.
   * @since 2.4
   */
  boolean addAllUnique(int index, Collection<? extends E> collection);

  /**
   * Sets the object without verifying uniqueness.
   */
  E setUnique(int index, E object);

  /**
   * Additional API for unsettable lists.
   */
  interface Unsettable<E> extends InternalEList<E>
  {
    /**
     * Returns whether a value is held by the feature of the object.
     * @return whether a value is held by the feature of the object.
     * @see org.eclipse.emf.ecore.EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    boolean isSet();
  
    /**
     * Unsets the value held by the feature of the object.
     * @see org.eclipse.emf.ecore.EObject#eUnset(org.eclipse.emf.ecore.EStructuralFeature)
     */
    void unset();
  }
}
