/**
 * Copyright (c) 2009-2010 IBM Corporation and others.
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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;


/**
 * A highly extensible abstract list implementation {@link #data() logically backed by an array} that is <b>never<b> modified.
 *
 */
public abstract class ArrayDelegatingEList<E> extends AbstractEList<E> implements RandomAccess, Cloneable, Serializable
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates an empty instance with no initial capacity.
   * The data storage will be null.
   */
  public ArrayDelegatingEList()
  {
    super();
  }

  /**
   * Creates an instance that is a copy of the collection.
   * @param collection the initial contents of the list.
   */
  public ArrayDelegatingEList(Collection<? extends E> collection)
  {
    // Conditionally create the data.
    //
    int size = collection.size();
    if (size > 0)
    {
      Object [] data = newData(size);
      collection.toArray(data);
      setData(data);
    }
  }

  /**
   * Creates an initialized instance that directly uses the given arguments.
   * @param data the underlying storage of the list.
   */
  protected ArrayDelegatingEList(Object [] data)
  {
    setData(data);
  }

  /**
   * Returns new allocated data storage.
   * Clients may override this to create typed storage.
   * The cost of type checking via a typed array is negligible.
   * @return new data storage.
   */
  protected Object [] newData(int capacity)
  {
    return new Object [capacity];
  }
  
  /**
   * Assigns the object into the data storage at the given index and returns the object that's been stored.
   * Clients can monitor access to the storage via this method.
   * @param index the position of the new content.
   * @param object the new content.
   * @return the object that's been stored.
   *
   */
  protected E assign(Object [] data, int index, E object)
  {
    data[index] = object;
    return object;
  }

  /**
   * Returns the number of objects in the list.
   * @return the number of objects in the list.
   */
  @Override
  public final int size()
  {
    Object[] data = data();
    return data == null ? 0 : data.length;
  }

  /**
   * Returns whether the list has zero size.
   * @return whether the list has zero size.
   */
  @Override
  public boolean isEmpty()
  {
    return data() == null;
  }

  /**
   * Returns whether the list contains the object.
   * This implementation uses either <code>equals</code> or <code>"=="</code> depending on {@link #useEquals useEquals}.
   * @param object the object in question.
   * @return whether the list contains the object.
   * @see #useEquals
   */
  @Override
  public boolean contains(Object object)
  {
    Object[] data = data();
    if (data != null)
    {
      if (useEquals() && object != null)
      {
        for (Object datum : data)
        {
          if (object.equals(datum))
          {
            return true;
          }
        }
      }
      else
      {
        for (Object datum : data)
        {
          if (datum == object)
          {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Returns the position of the first occurrence of the object in the list.
   * This implementation uses either <code>equals</code> or <code>"=="</code> depending on {@link #useEquals useEquals}.
   * @param object the object in question.
   * @return the position of the first occurrence of the object in the list.
   */
  @Override
  public int indexOf(Object object)
  {
    Object[] data = data();
    if (data != null)
    {
      if (useEquals() && object != null)
      {
        for (int i = 0, size = data.length; i < size; ++i)
        {
          if (object.equals(data[i]))
          {
            return i;
          }
        }
      }
      else
      {
        for (int i = 0, size = data.length; i < size; ++i)
        {
          if (data[i] == object)
          {
            return i;
          }
        }
      }
    }
    return -1;
  }

  /**
   * Returns the position of the last occurrence of the object in the list.
   * This implementation uses either <code>equals</code> or <code>"=="</code> depending on {@link #useEquals useEquals}.
   * @param object the object in question.
   * @return the position of the last occurrence of the object in the list.
   */
  @Override
  public int lastIndexOf(Object object)
  {
    Object[] data = data();
    if (data != null)
    {
      if (useEquals() && object != null)
      {
        for (int i = data.length - 1; i >= 0; --i)
        {
          if (object.equals(data[i]))
          {
            return i;
          }
        }
      }
      else
      {
        for (int i = data.length - 1; i >= 0; --i)
        {
          if (data[i] == object)
          {
            return i;
          }
        }
      }
    }
    return -1;
  }

  /**
   * Returns an array containing all the objects in sequence.
   * Clients may override {@link #newData newData} to create typed storage in this case.
   * @return an array containing all the objects in sequence.
   * @see #newData
   */
  @Override
  public Object[] toArray()
  {
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    Object[] result = newData(size);

    // Guard for no data.
    //
    if (size > 0)
    {
      System.arraycopy(data, 0, result, 0, size);
    }
    return result;
  }

  /**
   * Returns an array containing all the objects in sequence.
   * @param array the array that will be filled and returned, if it's big enough;
   * otherwise, a suitably large array of the same type will be allocated and used instead.
   * @return an array containing all the objects in sequence.
   * @see #newData
   */
  @Override
  public <T> T[] toArray(T[] array)
  {
    // Guard for no data.
    //
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    if (size > 0)
    {
      if (array.length < size)
      {
        @SuppressWarnings("unchecked") T [] newArray = (T[])Array.newInstance(array.getClass().getComponentType(), size);
        array  = newArray;
      }

      System.arraycopy(data, 0, array, 0, size);
    }

    if (array.length > size)
    {
      array[size] = null;
    }

    return array;
  }

  /**
   * Returns direct <b>unsafe</b> access to the underlying data storage.
   * Clients may <b>not</b> modify this array's elements
   * but <b>may</b> assume that the array remains valid even as the list is modified.
   * The array's length is exactly the same as the list's {@link #size() size};
   * <code>null</code> is used to represent the empty list.
   * @return direct <b>unsafe</b> access to the underlying data storage.
   */
  public abstract Object [] data();

  /**
   * Updates directly and <b>unsafely</b> the underlying data storage.
   * Clients <b>must</b> be aware that this subverts all callbacks
   * and hence possibly the integrity of the list.
   * The list implementation itself calls this method whenever a modification of the list requires a new backing array.
   * @param data the new underlying data storage.
   */
  public void setData(Object [] data)
  {
    // Do nothing.
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
  @SuppressWarnings({"unchecked", "null"})
  @Override
  public E get(int index)
  {
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    if (index >= size)
      throw new BasicIndexOutOfBoundsException(index, size);

    return resolve(index, (E)data[index]);
  }

  /**
   * Returns the object at the index without {@link #resolve resolving} it.
   * @param index the position in question.
   * @return the object at the index.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   * @see #resolve
   * @see #get
   */
  @SuppressWarnings({"unchecked", "null"})
  @Override
  public E basicGet(int index)
  {
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    if (index >= size)
      throw new BasicIndexOutOfBoundsException(index, size);

    return (E)data[index];
  }
  
  @Override
  @SuppressWarnings({"unchecked"})
  protected E primitiveGet(int index)
  {
    return (E)data()[index];
  }

  /**
   * Sets the object at the index
   * and returns the old object at the index;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didSet didSet}, and {@link #didChange didChange}.
   * @param index the position in question.
   * @param object the object to set.
   * @return the old object at the index.
   * @see #set
   */
  @Override
  public E setUnique(int index, E object)
  {
    Object[] data = copy();
    @SuppressWarnings("unchecked") E oldObject = (E)data[index];
    assign(data, index, validate(index, object));
    setData(data);
    didSet(index, object, oldObject);
    didChange();
    return oldObject;
  }

  /**
   * Adds the object at the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * after uniqueness checking.
   * @param object the object to be added.
   * @see #add(Object)
   */
  @Override
  public void addUnique(E object)
  {
    int size = size();
    Object [] data = grow(size + 1);
    assign(data, size, validate(size, object));
    setData(data);
    didAdd(size, object);
    didChange();
  }

  /**
   * Adds the object at the given index in the list;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param object the object to be added.
   * @see #add(int, Object)
   */
  @Override
  public void addUnique(int index, E object)
  {
    Object[] oldData = data();
    int size = oldData == null ? 0 : oldData.length;
    Object[] data = grow(size + 1);
    E validatedObject = validate(index, object);
    if (index != size)
    {
      System.arraycopy(oldData, index, data, index + 1, size - index);
    }
    assign(data, index, validatedObject);
    setData(data);
    didAdd(index, object);
    didChange();
  }

  /**
   * Adds each object of the collection to the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param collection the collection of objects to be added.
   * @see #addAll(Collection)
   */
  @Override
  public boolean addAllUnique(Collection<? extends E> collection)
  {
    int growth = collection.size();
    if (growth != 0)
    {
      int oldSize = size();
      int size = oldSize + growth;
      Object[] data = grow(size);

      Iterator<? extends E> objects = collection.iterator();
      for (int i = oldSize; i < size; ++i)
      {
        E object = objects.next();
        assign(data, i, validate(i, object));
      }
      setData(data);
      for (int i = oldSize; i < size; ++i)
      {
        @SuppressWarnings("unchecked") E object = (E)data[i];
        didAdd(i, object);
        didChange();
      }
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Adds each object of the collection at each successive index in the list
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param index the index at which to add.
   * @param collection the collection of objects to be added.
   * @return whether any objects were added.
   * @see #addAll(int, Collection)
   */
  @Override
  public boolean addAllUnique(int index, Collection<? extends E> collection)
  {
    int growth = collection.size();
    if (growth != 0)
    {
      Object[] oldData = data();
      int oldSize = oldData == null ? 0 : oldData.length;
      int size = oldSize + growth;
      Object[] data = grow(size);

      int shifted = oldSize - index;
      if (shifted > 0)
      {
        System.arraycopy(oldData, index, data, index + growth, shifted);
      }

      Iterator<? extends E> objects = collection.iterator();
      for (int i = 0; i < growth; ++i)
      {
        E object = objects.next();
        int currentIndex = index + i;
        assign(data, currentIndex, validate(currentIndex, object));
      }
      setData(data);
      for (int i = 0; i < growth; ++i)
      {
        @SuppressWarnings("unchecked") E object = (E)data[index];
        didAdd(index, object);
        didChange();
        index++;
      }
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Adds each object from start to end of the array at the index of list
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param objects the objects to be added.
   * @param start the index of first object to be added.
   * @param end the index past the last object to be added.
   * @return whether any objects were added.
   * @see #addAllUnique(Object[], int, int)
   */
  @Override
  public boolean addAllUnique(Object [] objects, int start, int end)
  {
    int growth = end - start;
    if (growth > 0)
    {
      int oldSize = size();
      int size = oldSize + growth;
      Object[] data = grow(size);

      for (int i = start, index = size; i < end; ++i, ++index)
      {
        @SuppressWarnings("unchecked") E object = (E)objects[i];
        assign(data, index, validate(index, object));
      }
      setData(data);
      for (int i = start, index = size; i < end; ++i, ++index)
      {
        @SuppressWarnings("unchecked") E object = (E)objects[i];
        didAdd(index, object);
        didChange();
      }
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Adds each object from start to end of the array at each successive index in the list
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
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
    if (growth > 0)
    {
      Object[] oldData = data();
      int oldSize = oldData == null ? 0 : oldData.length;
      int size = oldSize + growth;
      Object[] data = grow(size);

      int shifted = oldSize - index;
      if (shifted > 0)
      {
        System.arraycopy(oldData, index, data, index + growth, shifted);
      }

      for (int i = start, currentIndex = index; i < end; ++i, ++currentIndex)
      {
        @SuppressWarnings("unchecked") E object = (E)objects[i];
        assign(data, currentIndex, validate(currentIndex, object));
      }
      setData(data);
      for (int i = start, currentIndex = index; i < end; ++i, ++currentIndex)
      {
        @SuppressWarnings("unchecked") E object = (E)objects[i];
        didAdd(currentIndex, object);
        didChange();
      }
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
  @SuppressWarnings("null")
  @Override
  public boolean removeAll(Collection<?> collection)
  {
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    boolean modified = false;
    for (int i = size; --i >= 0; )
    {
      if (collection.contains(data[i]))
      {
        remove(i);
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
  @SuppressWarnings("null")
  @Override
  public E remove(int index)
  {
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    if (index >= size)
      throw new BasicIndexOutOfBoundsException(index, size);

    @SuppressWarnings("unchecked") E oldObject = (E)data[index];

    Object[] newData = newData(size - 1);
    System.arraycopy(data, 0, newData, 0, index);
    int shifted = size - index - 1;
    if (shifted > 0)
    {
      System.arraycopy(data, index+1, newData, index, shifted);
    }

    setData(newData);
    didRemove(index, oldObject);
    didChange();

    return oldObject;
  }

  /**
   * Removes from the list each object not contained by the collection
   * and returns whether any object was actually removed.
   * This delegates to {@link #remove(int) remove(int)}
   * in the case that it finds an object that isn't retained.
   * @param collection the collection of objects to be retained.
   * @return whether any object was actually removed.
   */
  @SuppressWarnings("null")
  @Override
  public boolean retainAll(Collection<?> collection)
  {
    boolean modified = false;
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    for (int i = size; --i >= 0; )
    {
      if (!collection.contains(data[i]))
      {
        remove(i);
        modified = true;
      }
    }
    return modified;
  }

  /**
   * Clears the list of all objects.
   * This implementation discards the data storage without modifying it
   * and delegates to {@link #didClear didClear} and {@link #didChange didChange}.
   */
  @Override
  public void clear()
  {
    Object[] oldData = data();
    int oldSize = oldData == null ? 0 : oldData.length;

    // Give it all back to the garbage collector.
    //
    setData(null);

    didClear(oldSize, oldData);
    didChange();
  }

  /**
   * Moves the object at the source index of the list to the target index of the list
   * and returns the moved object.
   * This implementation delegates to {@link #assign assign}, {@link #didMove didMove}, and {@link #didChange didChange}.
   * @param targetIndex the new position for the object in the list.
   * @param sourceIndex the old position of the object in the list.
   * @return the moved object.
   * @exception IndexOutOfBoundsException if either index isn't within the size range.
   */
  @SuppressWarnings("null")
  @Override
  public E move(int targetIndex, int sourceIndex)
  {
    Object[] data = copy();
    int size = data == null ? 0 : data.length;
    if (targetIndex >= size)
      throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + size);

    if (sourceIndex >= size)
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + size);

    @SuppressWarnings("unchecked") E object = (E)data[sourceIndex];
    if (targetIndex != sourceIndex)
    {
      if (targetIndex < sourceIndex)
      {
        System.arraycopy(data, targetIndex, data, targetIndex + 1, sourceIndex - targetIndex);
      }
      else
      {
        System.arraycopy(data, sourceIndex + 1, data, sourceIndex, targetIndex - sourceIndex);
      }
      assign(data, targetIndex, object);
      setData(data);
      didMove(targetIndex, object, sourceIndex);
      didChange();
    }
    return object;
  }

  /**
   * Grows the capacity of the list to exactly the new size.
   */
  protected Object[] grow(int size)
  {
    Object[] oldData = data();
    Object[] data = newData(size);
    if (oldData != null)
    {
      System.arraycopy(oldData, 0, data, 0, oldData.length);
    }
    return data;
  }

  private static Object [] EMPTY_ARRAY = new Object [0];

  /**
   * Returns a copy of the existing data array.
   */
  protected Object[] copy()
  {
    Object[] data = data();
    if (data != null)
    {
      Object[] newData = newData(data.length);
      System.arraycopy(data, 0, newData, 0, data.length);
      return newData;
    }
    else
    {
      return EMPTY_ARRAY;
    }
  }

  /**
   * Returns an iterator.
   * This implementation allocates a {@link ArrayDelegatingEList.EIterator}.
   * @return an iterator.
   * @see ArrayDelegatingEList.EIterator
   */
  @Override
  public Iterator<E> iterator()
  {
    return new EIterator<E>();
  }

  /**
   * An extensible iterator implementation.
   */
  protected class EIterator<E1> extends AbstractEList<E>.EIterator<E1>
  {
    /**
     * The expected data array of the containing list.
     */
    protected Object [] expectedData = data();

    /**
     * Checks that the modification count and data array are as expected.
     * @exception ConcurrentModificationException if the modification count is not as expected.
     */
    @Override
    protected void checkModCount()
    {
      if (data() != expectedData)
      {
        throw new ConcurrentModificationException();
      }
    }
  }

  /**
   * Returns a read-only iterator that does not {@link #resolve resolve} objects.
   * This implementation allocates a {@link NonResolvingEIterator}.
   * @return a read-only iterator that does not resolve objects.
   */
  @Override
  protected Iterator<E> basicIterator()
  {
    return new NonResolvingEIterator<E>();
  }

  /**
   * An extended read-only iterator that does not {@link ArrayDelegatingEList#resolve resolve} objects.
   */
  protected class NonResolvingEIterator<E1> extends AbstractEList<E>.NonResolvingEIterator<E1>
  {
    /**
     * The expected data array of the containing list.
     */
    protected Object [] expectedData = data();

    /**
     * Checks that the modification count and data array are as expected.
     * @exception ConcurrentModificationException if the modification count is not as expected.
     */
    @Override
    protected void checkModCount()
    {
      if (data() != expectedData)
      {
        throw new ConcurrentModificationException();
      }
    }
  }

  /**
   * Returns a list iterator.
   * This implementation allocates a {@link ArrayDelegatingEList.EListIterator}.
   * @return a list iterator.
   * @see ArrayDelegatingEList.EListIterator
   */
  @Override
  public ListIterator<E> listIterator()
  {
    return new EListIterator<E>();
  }

  /**
   * Returns a list iterator advanced to the given index.
   * This implementation allocates a {@link ArrayDelegatingEList.EListIterator}.
   * @param index the starting index.
   * @return a list iterator advanced to the index.
   * @see ArrayDelegatingEList.EListIterator
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   */
  @Override
  public ListIterator<E> listIterator(int index)
  {
    int size = size();
    if (index < 0 || index > size)
      throw new BasicIndexOutOfBoundsException(index, size);

    return new EListIterator<E>(index);
  }

  /**
   * An extensible list iterator implementation.
   */
  protected class EListIterator<E1> extends AbstractEList<E>.EListIterator<E1>
  {
    /**
     * The expected data array of the containing list.
     */
    protected Object [] expectedData = data();

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

    /**
     * Checks that the modification count and data array are as expected.
     * @exception ConcurrentModificationException if the modification count is not as expected.
     */
    @Override
    protected void checkModCount()
    {
      if (data() != expectedData)
      {
        throw new ConcurrentModificationException();
      }
    }
  }

  /**
   * Returns a read-only list iterator that does not {@link #resolve resolve} objects.
   * This implementation allocates a {@link NonResolvingEListIterator}.
   * @return a read-only list iterator that does not resolve objects.
   */
  @Override
  protected ListIterator<E> basicListIterator()
  {
    return new NonResolvingEListIterator<E>();
  }

  /**
   * Returns a read-only list iterator advanced to the given index that does not {@link #resolve resolve} objects.
   * This implementation allocates a {@link NonResolvingEListIterator}.
   * @param index the starting index.
   * @return a read-only list iterator advanced to the index.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   */
  @Override
  protected ListIterator<E> basicListIterator(int index)
  {
    int size = size();
    if (index < 0 || index > size)
      throw new BasicIndexOutOfBoundsException(index, size);

    return new NonResolvingEListIterator<E>(index);
  }

  /**
   * An extended read-only list iterator that does not {@link ArrayDelegatingEList#resolve resolve} objects.
   */
  protected class NonResolvingEListIterator<E1> extends AbstractEList<E>.NonResolvingEListIterator<E1>
  {
    /**
     * The expected data array of the containing list.
     */
    protected Object [] expectedData = data();

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

    /**
     * Checks that the modification count and data array are as expected.
     * @exception ConcurrentModificationException if the modification count is not as expected.
     */
    @Override
    protected void checkModCount()
    {
      if (data() != expectedData)
      {
        throw new ConcurrentModificationException();
      }
    }
  }

  /**
   * Returns an <b>unsafe</b> list that provides a {@link #resolve non-resolving} view of the underlying data storage.
   * @return an <b>unsafe</b> list that provides a non-resolving view of the underlying data storage.
   */
  @Override
  protected List<E> basicList()
  {
    Object[] data = data();
    int size = data == null ? 0 : data.length;
    if (size == 0)
    {
      return ECollections.emptyEList();
    }
    else
    {
      return new BasicEList.UnmodifiableEList<E>(size, data);
    }
  }
}
