/**
 * Copyright (c) 2003-2010 IBM Corporation and others.
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
import org.eclipse.emf.ecore.InternalEObject;


/**
 * A list of entries where each entry consists of a feature and a single value of that feature's type.
 */
public interface FeatureMap extends EList<FeatureMap.Entry>
{
  /**
   * A pair consisting of a feature and a single value of that feature's type.
   */
  interface Entry
  {
    /**
     * Returns the feature.
     * @return the feature.
     */
    EStructuralFeature getEStructuralFeature();

    /**
     * Returns the value.
     * @return the value.
     */
    Object getValue();
    
    interface Internal extends Entry
    {
      NotificationChain inverseAdd(InternalEObject owner, int featureID, NotificationChain notifications);      
      NotificationChain inverseRemove(InternalEObject owner, int featureID, NotificationChain notifications);
      NotificationChain inverseAdd(InternalEObject owner, Object otherEnd, int featureID, NotificationChain notifications);      
      NotificationChain inverseRemove(InternalEObject owner, Object otherEnd, int featureID, NotificationChain notifications);
      void validate(Object value);
      Internal createEntry(Object value);
      Internal createEntry(InternalEObject value);
    }
  }
  
  interface ValueListIterator<E> extends EContentsEList.FeatureListIterator<E>
  {
    void add(EStructuralFeature eStructuralFeature, Object value);
  }
  
  ValueListIterator<Object> valueListIterator();
  ValueListIterator<Object> valueListIterator(int index);

  <T> EList<T> list(EStructuralFeature feature);

  EStructuralFeature getEStructuralFeature(int index);
  Object getValue(int index);
  Object setValue(int index, Object value);

  Object get(EStructuralFeature feature, boolean resolve);
  void set(EStructuralFeature feature, Object object);
  boolean isSet(EStructuralFeature feature);
  void unset(EStructuralFeature feature);

  boolean add(EStructuralFeature feature, Object value);
  void add(int index, EStructuralFeature feature, Object value);

  boolean addAll(EStructuralFeature feature, Collection<?> values);
  boolean addAll(int index, EStructuralFeature feature, Collection<?> values);

  interface Internal extends FeatureMap, InternalEList<Entry>, EStructuralFeature.Setting
  {
    int getModCount();
    EObject getEObject();

    Object resolveProxy(EStructuralFeature feature, int entryIndex, int index, Object object);

    int size(EStructuralFeature feature);
    boolean isEmpty(EStructuralFeature feature);
    boolean contains(EStructuralFeature feature, Object object);
    boolean containsAll(EStructuralFeature feature, Collection<?> collection);
    int indexOf(EStructuralFeature feature, Object object);
    int lastIndexOf(EStructuralFeature feature, Object object);
    Iterator<Object> iterator(EStructuralFeature feature);
    ListIterator<Object> listIterator(EStructuralFeature feature);
    ListIterator<Object> listIterator(EStructuralFeature feature, int index);
    // List subList(EStructuralFeature feature, int from, int to);
    // EList list(EStructuralFeature feature);
    EStructuralFeature.Setting setting(EStructuralFeature feature);
    List<Object> basicList(EStructuralFeature feature);
    Iterator<Object> basicIterator(EStructuralFeature feature);
    ListIterator<Object> basicListIterator(EStructuralFeature feature);
    ListIterator<Object> basicListIterator(EStructuralFeature feature, int index);

    /**
     * @since 2.4
     */
    Object[] basicToArray(EStructuralFeature feature); 

    /**
     * @since 2.4
     */
    <T> T[] basicToArray(EStructuralFeature feature, T[] array); 
    
    /**
     * @since 2.4
     */
    int basicIndexOf(EStructuralFeature feature, Object object);

    /**
     * @since 2.4
     */
    int basicLastIndexOf(EStructuralFeature feature, Object object);

    /**
     * @since 2.4
     */
    boolean basicContains(EStructuralFeature feature, Object object);

    /**
     * @since 2.4
     */
    boolean basicContainsAll(EStructuralFeature feature, Collection<?> collection);

    Object[] toArray(EStructuralFeature feature);
    <T> T[] toArray(EStructuralFeature feature, T [] array);
    void add(EStructuralFeature feature, int index, Object object);
    boolean addAll(EStructuralFeature feature, int index, Collection<?> collection);
    void addUnique(EStructuralFeature feature, Object object);
    void addUnique(EStructuralFeature feature, int index, Object object);
    boolean addAllUnique(Collection<? extends Entry> collection);
    void addUnique(Entry.Internal entry);
    boolean addAllUnique(Entry.Internal [] entries, int start, int end);

    /**
     * @since 2.4
     */
    boolean addAllUnique(int index, Entry.Internal [] entries, int start, int end);
    NotificationChain basicAdd(EStructuralFeature feature, Object object, NotificationChain notifications);
    boolean remove(EStructuralFeature feature, Object object);
    Object remove(EStructuralFeature feature, int index);
    boolean removeAll(EStructuralFeature feature, Collection<?> collection);
    NotificationChain basicRemove(EStructuralFeature feature, Object object, NotificationChain notifications);
    boolean retainAll(EStructuralFeature feature, Collection<?> collection);
    void clear(EStructuralFeature feature);
    void move(EStructuralFeature feature, int index, Object object);
    Object move(EStructuralFeature feature, int targetIndex, int sourceIndex);
    Object get(EStructuralFeature feature, int index, boolean resolve);
    Object set(EStructuralFeature feature, int index, Object object);
    Object setUnique(EStructuralFeature feature, int index, Object object);

    interface Wrapper
    {
      FeatureMap featureMap();
    }

    Wrapper getWrapper();
    void setWrapper(Wrapper wrapper);
  }
}
