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


import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A highly extensible delegating list implementation.
 */
public abstract class DelegatingEList<E> extends AbstractEList<E> implements Cloneable, Serializable 
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates an empty instance.
   */
  public DelegatingEList() 
  {
    super();
  }

  /**
   * Creates an instance that is a copy of the collection.
   * @param collection the initial contents of the list.
   */
  public DelegatingEList(Collection<? extends E> collection) 
  {
    addAll(collection);
  }

  /**
   * Returns the list that acts as the backing store.
   * @return the list that acts as the backing store.
   */
  protected abstract List<E> delegateList();

  /**
   * Returns the number of objects in the list.
   * @return the number of objects in the list.
   */
  @Override
  public int size() 
  {
    return delegateSize();
  }

  /**
   * Returns the number of objects in the backing store list.
   * @return the number of objects in the backing store list.
   */
  protected int delegateSize()
  {
    return delegateList().size();
  }

  /**
   * Returns whether the list has zero size.
   * @return whether the list has zero size.
   */
  @Override
  public boolean isEmpty() 
  {
    return delegateIsEmpty();
  }

  /**
   * Returns whether the backing store list has zero size.
   * @return whether the backing store list has zero size.
   */
  protected boolean delegateIsEmpty() 
  {
    return delegateList().isEmpty();
  }

  /**
   * Returns whether the list contains the object.
   * @param object the object in question.
   * @return whether the list contains the object.
   */
  @Override
  public boolean contains(Object object) 
  {
    return delegateContains(object);
  }

  /**
   * Returns whether the backing store list contains the object.
   * @param object the object in question.
   * @return whether the backing store list contains the object.
   */
  protected boolean delegateContains(Object object) 
  {
    return delegateList().contains(object);
  }

  /**
   * Returns whether the list contains each object in the collection.
   * @return whether the list contains each object in the collection.
   * @see #contains
   * @see #useEquals
   */
  @Override
  public boolean containsAll(Collection<?> collection) 
  {
    return delegateContainsAll(collection);
  }

  /**
   * Returns whether the backing store list contains each object in the collection.
   * @return whether the backing store list contains each object in the collection.
   * @see #contains
   * @see #useEquals
   */
  protected boolean delegateContainsAll(Collection<?> collection) 
  {
    return delegateList().containsAll(collection);
  }

  /**
   * Returns the position of the first occurrence of the object in the list.
   * @param object the object in question.
   * @return the position of the first occurrence of the object in the list.
   */
  @Override
  public int indexOf(Object object) 
  {
    return delegateIndexOf(object);
  }

  /**
   * Returns the position of the first occurrence of the object in the backing store list.
   * @param object the object in question.
   * @return the position of the first occurrence of the object in the backing store list.
   */
  protected int delegateIndexOf(Object object) 
  {
    return delegateList().indexOf(object);
  }

  /**
   * Returns the position of the last occurrence of the object in the list.
   * @param object the object in question.
   * @return the position of the last occurrence of the object in the list.
   */
  @Override
  public int lastIndexOf(Object object) 
  {
    return delegateLastIndexOf(object);
  }

  /**
   * Returns the position of the last occurrence of the object in the backing store list.
   * @param object the object in question.
   * @return the position of the last occurrence of the object in the backing store list.
   */
  protected int delegateLastIndexOf(Object object) 
  {
    return delegateList().lastIndexOf(object);
  }

  /**
   * Returns an array containing all the objects in sequence.
   * @return an array containing all the objects in sequence.
   */
  @Override
  public Object[] toArray() 
  {
    return delegateToArray();
  }

  /**
   * Returns an array containing all the objects in the backing store list in sequence.
   * @return an array containing all the objects in the backing store list in sequence.
   */
  protected Object[] delegateToArray() 
  {
    return delegateList().toArray();
  }

  /**
   * Returns an array containing all the objects in sequence.
   * @param array the array that will be filled and returned, if it's big enough;
   * otherwise, a suitably large array of the same type will be allocated and used instead.
   * @return an array containing all the objects in sequence.
   */
  @Override
  public <T> T[] toArray(T[] array) 
  {
    return delegateToArray(array);
  }

  /**
   * Returns an array containing all the objects in the backing store list in sequence.
   * @param array the array that will be filled and returned, if it's big enough;
   * otherwise, a suitably large array of the same type will be allocated and used instead.
   * @return an array containing all the objects in sequence.
   */
  protected <T> T[] delegateToArray(T[] array) 
  {
    return delegateList().toArray(array);
  }

  /**
   * Returns the object at the index.
   * This implementation delegates to {@link #resolve resolve} 
   * so that clients may transform the fetched object.
   * @param index the position in question.
   * @return the object at the index.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   * @see #resolve
   * @see #basicGet
   */
  @Override
  public E get(int index) 
  {
    return resolve(index, delegateGet(index));
  }

  /**
   * Returns the object at the index in the backing store list.
   * @param index the position in question.
   * @return the object at the index.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   */
  protected E delegateGet(int index) 
  {
    return delegateList().get(index);
  }

  /**
   * Returns the object at the index without {@link #resolve resolving} it.
   * @param index the position in question.
   * @return the object at the index.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   * @see #resolve
   * @see #get
   */
  @Override
  protected E basicGet(int index) 
  {
    return delegateGet(index);
  }
  
  @Override
  protected E primitiveGet(int index)
  {
    return delegateGet(index);
  }

  /**
   * Sets the object at the index 
   * and returns the old object at the index;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #didSet didSet} and {@link #didChange didChange}.
   * @param index the position in question.
   * @param object the object to set.
   * @return the old object at the index.
   * @see #set
   */
  @Override
  public E setUnique(int index, E object)
  {
    E oldObject = delegateSet(index, validate(index, object));
    didSet(index, object, oldObject);
    didChange();
    return oldObject;
  }

  /**
   * Sets the object at the index in the backing store list
   * and returns the old object at the index.
   * @param object the object to set.
   * @return the old object at the index.
   */
  protected E delegateSet(int index, E object)
  {
    return delegateList().set(index, object);
  }

  /**
   * Adds the object at the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #didAdd didAdd} and {@link #didChange didChange}.
   * after uniqueness checking.
   * @param object the object to be added.
   * @see #add(Object)
   */
  @Override
  public void addUnique(E object) 
  {
    int size = size();
    delegateAdd(validate(size, object));
    didAdd(size, object);
    didChange();
  }

  /**
   * Adds the object at the end of the backing store list.
   * @param object the object to be added.
   */
  protected void delegateAdd(E object) 
  {
    delegateList().add(object);
  }

  /**
   * Adds the object at the given index in the list;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #didAdd didAdd} and {@link #didChange didChange}.
   * @param object the object to be added.
   * @see #add(int, Object)
   */
  @Override
  public void addUnique(int index, E object) 
  {
    delegateAdd(index, validate(index, object));
    didAdd(index, object);
    didChange();
  }

  /**
   * Adds the object at the given index in the backing store list.
   * @param object the object to be added.
   */
  protected void delegateAdd(int index, E object) 
  {
    delegateList().add(index, object);
  }

  /**
   * Adds each object of the collection to the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #didAdd didAdd} and {@link #didChange didChange}.
   * @param collection the collection of objects to be added.
   * @see #addAll(Collection)
   */
  @Override
  public boolean addAllUnique(Collection<? extends E> collection) 
  {
    if (collection.isEmpty())
    {
      return false;
    }
    else
    {
      int i = size();
      for (E object : collection)
      {
        delegateAdd(validate(i, object));
        didAdd(i, object);
        didChange();
        i++;
      }
  
      return true;
    }
  }

  /**
   * Adds each object of the collection at each successive index in the list 
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #didAdd didAdd} and {@link #didChange didChange}.
   * @param index the index at which to add.
   * @param collection the collection of objects to be added.
   * @return whether any objects were added.
   * @see #addAll(int, Collection)
   */
  @Override
  public boolean addAllUnique(int index, Collection<? extends E> collection) 
  {
    if (collection.isEmpty())
    {
      return false;
    }
    else
    {
      for (E object : collection)
      {
        delegateAdd(index, validate(index, object));
        didAdd(index, object);
        didChange();
        index++;
      }

      return true;
    }
  }

  /**
   * Adds each object from start to end of the array at the index of list 
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #delegateAdd(Object) delegatedAdd}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param objects the objects to be added.
   * @param start the index of first object to be added.
   * @param end the index past the last object to be added.
   * @return whether any objects were added.
   * @see #addAllUnique(int, Object[], int, int)
   */
  @Override
  public boolean addAllUnique(Object [] objects, int start, int end) 
  {
    int growth = end - start;
    
    if (growth == 0)
    {
      return false;
    }
    else
    {
      int index = size();
      for (int i = start; i < end; ++i, ++index)
      {
        @SuppressWarnings("unchecked") E object = (E)objects[i];
        delegateAdd(validate(index, object));
        didAdd(index, object);
        didChange();
      }
  
      return true;
    }
  }

  /**
   * Adds each object from start to end of the array at each successive index in the list 
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #delegateAdd(int, Object) delegatedAdd}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param index the index at which to add.
   * @param objects the objects to be added.
   * @param start the index of first object to be added.
   * @param end the index past the last object to be added.
   * @return whether any objects were added.
   * @see #addAllUnique(Object[], int, int)
   */
  @Override
  public boolean addAllUnique(int index, Object [] objects, int start, int end) 
  {
    int growth = end - start;

    if (growth == 0)
    {
      return false;
    }
    else
    {
      for (int i = start; i < end; ++i, ++index)
      {
        @SuppressWarnings("unchecked") E object = (E)objects[i];
        delegateAdd(validate(index, object));
        didAdd(index, object);
        didChange();
      }
  
      return true;
    }
  }

  /**
   * Removes the object from the list and returns whether the object was actually contained by the list.
   * This implementation uses {@link #indexOf indexOf} to find the object
   * and delegates to {@link #remove(int) remove(int)} 
   * in the case that it finds the object.
   * @param object the object to be removed.
   * @return whether the object was actually contained by the list.
   */
  @Override
  public boolean remove(Object object) 
  {
    int index = indexOf(object);
    if (index >= 0)
    {
      remove(index);
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Removes each object of the collection from the list and returns whether any object was actually contained by the list.
   * @param collection the collection of objects to be removed.
   * @return whether any object was actually contained by the list.
   */
  @Override
  public boolean removeAll(Collection<?> collection) 
  {
    boolean modified = false;
    for (ListIterator<?> i = listIterator(); i.hasNext(); )
    {
      if (collection.contains(i.next()))
      {
        i.remove();
        modified = true;
      }
    }

    return modified;
  }

  /**
   * Removes the object at the index from the list and returns it.
   * This implementation delegates to {@link #didRemove didRemove} and {@link #didChange didChange}.
   * @param index the position of the object to remove.
   * @return the removed object.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   */
  @Override
  public E remove(int index) 
  {
    E oldObject = delegateRemove(index);
    didRemove(index, oldObject);
    didChange();

    return oldObject;
  }

  /**
   * Removes the object at the index from the backing store list and returns it.
   * @return the removed object.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   */
  protected E delegateRemove(int index) 
  {
    return delegateList().remove(index);
  }

  /**
   * Removes from the list each object not contained by the collection
   * and returns whether any object was actually removed.
   * This delegates to {@link #remove(int) remove(int)} 
   * in the case that it finds an object that isn't retained.
   * @param collection the collection of objects to be retained.
   * @return whether any object was actually removed.
   */
  @Override
  public boolean retainAll(Collection<?> collection) 
  {
    boolean modified = false;
    for (ListIterator<?> i = listIterator(); i.hasNext(); )
    {
      if (!collection.contains(i.next()))
      {
        i.remove();
        modified = true;
      }
    }
    return modified;
  }

  /**
   * Clears the list of all objects.
   */
  @Override
  public void clear() 
  {
    doClear(size(), delegateToArray());
  }

  /**
   * Does the actual job of clearing all the objects.
   * @param oldSize the size of the list before it is cleared.
   * @param oldData old values of the list before it is cleared.
   */
  protected void doClear(int oldSize, Object [] oldData) 
  {
    delegateClear();

    didClear(oldSize, oldData);
    didChange();
  }

  /**
   * Clears the backing store list of all objects.
   */
  protected void delegateClear() 
  {
    delegateList().clear();
  }

  /**
   * Moves the object at the source index of the list to the target index of the list
   * and returns the moved object.
   * This implementation delegates to {@link #didMove didMove} and {@link #didChange didChange}.
   * @param targetIndex the new position for the object in the list.
   * @param sourceIndex the old position of the object in the list.
   * @return the moved object.
   * @exception IndexOutOfBoundsException if either index isn't within the size range.
   */
  @Override
  public E move(int targetIndex, int sourceIndex)
  {
    int size = size();
    if (targetIndex >= size || targetIndex < 0)
      throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + size);

    if (sourceIndex >= size || sourceIndex < 0)
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + size);

    E object;
    if (targetIndex != sourceIndex)
    {
      object = delegateMove(targetIndex, sourceIndex);
      didMove(targetIndex, object, sourceIndex);
      didChange();
    }
    else
    {
      object = delegateGet(sourceIndex);
    }
    return object;
  }

  /**
   * Moves the object at the source index in the backing store list by removing it and adding it at the new target index.
   * @param targetIndex the new position for the object in the list.
   * @param sourceIndex the old position of the object in the list.
   * @return the moved object.
   * @exception IndexOutOfBoundsException if either index isn't within the size range.
   * @since 2.3
   */
  protected E delegateMove(int targetIndex, int sourceIndex)
  {
    E result = delegateRemove(sourceIndex);
    delegateAdd(targetIndex, result);
    return result;
  }

  /**
   * Returns whether the object is a list with corresponding equal objects.
   * This implementation uses either <code>equals</code> or <code>"=="</code> depending on {@link #useEquals useEquals}.
   * @return whether the object is a list with corresponding equal objects.
   * @see #useEquals
   */
  @Override
  public boolean equals(Object object) 
  {
    return delegateEquals(object);
  }

  /**
   * Returns whether the object is a list with corresponding equal objects to those in the backing store list.
   * @return whether the object is a list with corresponding equal objects.
   */
  protected boolean delegateEquals(Object object) 
  {
    return delegateList().equals(object);
  }

  /**
   * Returns a hash code computed from each object's hash code.
   * @return a hash code.
   */
  @Override
  public int hashCode() 
  {
    return delegateHashCode();
  }

  /**
   * Returns the hash code of the backing store list.
   * @return a hash code.
   */
  protected int delegateHashCode() 
  {
    return delegateList().hashCode();
  }

  /**
   * Returns a string of the form <code>"[object1, object2]"</code>.
   * @return a string of the form <code>"[object1, object2]"</code>.
   */
  @Override
  public String toString() 
  {
    return delegateToString();
  }

  /**
   * Returns a the string form of the backing store list.
   * @return a the string form of the backing store list.
   */
  protected String delegateToString() 
  {
    return delegateList().toString();
  }

  /**
   * Returns an iterator over the backing store list.
   * @return an iterator.
   */
  protected Iterator<E> delegateIterator() 
  {
    return delegateList().iterator();
  }

  /**
   * An extensible iterator implementation.
   * @deprecated
   * @see AbstractEList.EIterator
   */
  @Deprecated
  protected class EIterator<E1> extends AbstractEList<E>.EIterator<E1>
  {
    // Pointless extension
  }

  /**
   * An extended read-only iterator that does not {@link DelegatingEList#resolve resolve} objects.
   * @deprecated
   * @see AbstractEList.NonResolvingEIterator
   */
  @Deprecated
  protected class NonResolvingEIterator<E1> extends AbstractEList<E>.NonResolvingEIterator<E1>
  {
    // Pointless extension
  }

  /**
   * Returns a list iterator over the backing store list.
   * @return a list iterator.
   */
  protected ListIterator<E> delegateListIterator() 
  {
    return delegateList().listIterator();
  }

  /**
   * An extensible list iterator implementation.
   * @deprecated
   * @see AbstractEList.EListIterator
   */
  @Deprecated
  protected class EListIterator<E1> extends AbstractEList<E>.EListIterator<E1>
  {
    /**
     * Creates an instance.
     */
    public EListIterator() 
    {
      super();
    }

    /**
     * Creates an instance advanced to the index.
     * @param index the starting index.
     */
    public EListIterator(int index) 
    {
      super(index);
    }
  }

  /**
   * An extended read-only list iterator that does not {@link DelegatingEList#resolve resolve} objects.
   * @deprecated
   * @see AbstractEList.NonResolvingEListIterator
   */
  @Deprecated
  protected class NonResolvingEListIterator<E1> extends AbstractEList<E>.NonResolvingEListIterator<E1>
  {
    /**
     * Creates an instance.
     */
    public NonResolvingEListIterator()
    {
      super();
    }

    /**
     * Creates an instance advanced to the index.
     * @param index the starting index.
     */
    public NonResolvingEListIterator(int index) 
    {
      super(index);
    }
  }

  /**
   * An unmodifiable version of {@link DelegatingEList}.
   */
  public static class UnmodifiableEList<E> extends DelegatingEList<E>
  {
    private static final long serialVersionUID = 1L;

    protected List<E> underlyingList;

    /**
     * Creates an initialized instance.
     * @param underlyingList the backing store list.
     */
    public UnmodifiableEList(List<E> underlyingList) 
    {
      this.underlyingList = underlyingList;
    }

    @Override
    protected List<E> delegateList()
    {
      return underlyingList;
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public E set(int index, E object) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public boolean add(E object) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public void add(int index, E object) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> collection) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public boolean remove(Object object) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public E remove(int index) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public boolean removeAll(Collection<?> collection) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public boolean retainAll(Collection<?> collection) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public void clear() 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public void move(int index, E object) 
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public E move(int targetIndex, int sourceIndex)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Returns the {@link DelegatingEList#basicIterator basic iterator}.
     * @return the basic iterator.
     */
    @Override
    public Iterator<E> iterator() 
    {
      return basicIterator();
    }

    /**
     * Returns the {@link #basicListIterator() basic list iterator}.
     * @return the basic list iterator.
     */
    @Override
    public ListIterator<E> listIterator() 
    {
      return basicListIterator();
    }
  
    /**
     * Returns the {@link #basicListIterator(int) basic list iterator} advanced to the index.
     * @param index the starting index.
     * @return the basic list iterator.
     */
    @Override
    public ListIterator<E> listIterator(int index) 
    {
      return basicListIterator(index);
    }
  }

  /**
   * Returns an <b>unsafe</b> list that provides a {@link #resolve non-resolving} view of the backing store list.
   * @return an <b>unsafe</b> list that provides a non-resolving view of the backing store list.
   */
  @Override
  protected List<E> basicList()
  {
    return delegateBasicList();
  }

  /**
   * Returns an <b>unsafe</b> list that provides a {@link #resolve non-resolving} view of the backing store list.
   * @return an <b>unsafe</b> list that provides a non-resolving view of the backing store list.
   */
  protected List<E> delegateBasicList()
  {
    return delegateList();
  }
}
