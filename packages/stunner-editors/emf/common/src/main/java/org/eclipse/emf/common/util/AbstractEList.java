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


import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * A highly extensible abstract list implementation.
 */
public abstract class AbstractEList<E> extends AbstractList<E> implements EList<E>
{
  /**
   * Creates an empty instance with no initial capacity.
   * The data storage will be null.
   */
  public AbstractEList()
  {
    super();
  }

  /**
   * Returns whether <code>equals</code> rather than <code>==</code> should be used to compare members.
   * The default is to return <code>true</code> but clients can optimize performance by returning <code>false</code>.
   * The performance difference is highly significant.
   * @return whether <code>equals</code> rather than <code>==</code> should be used.
   */
  protected boolean useEquals()
  {
    return true;
  }

  /**
   * Returns whether two objects are equal using the {@link #useEquals appropriate} comparison mechanism.
   * @return whether two objects are equal.
   */
  protected boolean equalObjects(Object firstObject, Object secondObject)
  {
    return
      useEquals() && firstObject != null ?
        firstObject.equals(secondObject) :
        firstObject == secondObject;
  }

  /**
   * Returns whether <code>null</code> is a valid object for the list.
   * The default is to return <code>true</code>, but clients can override this to exclude <code>null</code>.
   * @return whether <code>null</code> is a valid object for the list.
   */
  protected boolean canContainNull()
  {
    return true;
  }

  /**
   * Returns whether objects are constrained to appear at most once in the list.
   * The default is to return <code>false</code>, but clients can override this to ensure uniqueness of contents.
   * The performance impact is significant: operations such as <code>add</code> are O(n) as a result requiring uniqueness.
   * @return whether objects are constrained to appear at most once in the list.
   */
  protected boolean isUnique()
  {
    return false;
  }

  /**
   * Validates a new content object and returns the validated object.
   * This implementation checks for null, if {@link #canContainNull necessary} and returns the argument object.
   * Clients may throw additional types of runtime exceptions
   * in order to handle constraint violations.
   * @param index the position of the new content.
   * @param object the new content.
   * @return the validated content.
   * @exception IllegalArgumentException if a constraint prevents the object from being added.
   */
  protected E validate(int index, E object)
  {
    if (!canContainNull() && object == null)
    {
      throw new IllegalArgumentException("The 'no null' constraint is violated");
    }

    return object;
  }

  /**
   * Resolves the object at the index and returns the result.
   * This implementation simply returns the <code>object</code>;
   * clients can use this to transform objects as they are fetched.
   * @param index the position of the content.
   * @param object the content.
   * @return the resolved object.
   */
  protected E resolve(int index, E object)
  {
    return object;
  }

  /**
   * Called to indicate that the data storage has been set.
   * This implementation does nothing;
   * clients can use this to monitor settings to the data storage.
   * @param index the position that was set.
   * @param newObject the new object at the position.
   * @param oldObject the old object at the position.
   */
  protected void didSet(int index, E newObject, E oldObject)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that an object has been added to the data storage.
   * This implementation does nothing;
   * clients can use this to monitor additions to the data storage.
   * @param index the position object the new object.
   * @param newObject the new object at the position.
   */
  protected void didAdd(int index, E newObject)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that an object has been removed from the data storage.
   * This implementation does nothing;
   * clients can use this to monitor removals from the data storage.
   * @param index the position of the old object.
   * @param oldObject the old object at the position.
   */
  protected void didRemove(int index, E oldObject)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that the data storage has been cleared.
   * This implementation calls {@link #didRemove didRemove} for each object;
   * clients can use this to monitor clearing  of the data storage.
   * @param size the original size of the list.
   * @param oldObjects the old data storage being discarded.
   * @see #didRemove
   */
  protected void didClear(int size, Object [] oldObjects)
  {
    if (oldObjects != null)
    {
      for (int i = 0; i < size; ++i)
      {
        @SuppressWarnings("unchecked") E object = (E)oldObjects[i];
        didRemove(i, object);
      }
    }
  }

  /**
   * Called to indicate that an object has been moved in the data storage.
   * This implementation does nothing;
   * clients can use this to monitor movement in the data storage.
   * @param index the position of the moved object.
   * @param movedObject the moved object at the position.
   * @param oldIndex the position the object was at before the move.
   */
  protected void didMove(int index, E movedObject, int oldIndex)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that the data storage has been changed.
   * This implementation does nothing;
   * clients can use this to monitor change in the data storage.
   */
  protected void didChange()
  {
    // Do nothing.
  }

  /**
   * An IndexOutOfBoundsException that constructs a message from the argument data.
   * Having this avoids having the byte code that computes the message repeated/inlined at the creation site.
   */
  protected static class BasicIndexOutOfBoundsException extends IndexOutOfBoundsException
  {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance with a message based on the arguments.
     */
    public BasicIndexOutOfBoundsException(int index, int size)
    {
      super("index=" + index + ", size=" + size);
    }
  }
 
  /**
   * Returns the object at the index without {@link #resolve resolving} it.
   * @param index the position in question.
   * @return the object at the index.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   * @see #resolve
   * @see #get
   */
  protected E basicGet(int index)
  {
    int size = size();
    if (index >= size)
      throw new BasicIndexOutOfBoundsException(index, size);

    return primitiveGet(index);
  }

  /**
   * Returns the object at the index without {@link #resolve resolving} it and without range checking the index.
   * @param index the position in question.
   * @return the object at the index.
   * @see #resolve
   * @see #get
   * @see #basicGet(int)
   */
  protected abstract E primitiveGet(int index);

  /**
   * Sets the object at the index
   * and returns the old object at the index.
   * This implementation delegates to {@link #setUnique setUnique}
   * after range checking and after {@link #isUnique uniqueness} checking.
   * @param index the position in question.
   * @param object the object to set.
   * @return the old object at the index.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   * @exception IllegalArgumentException if there is a constraint violation, e.g., non-uniqueness.
   * @see #setUnique
   */
  @Override
  public E set(int index, E object)
  {
    int size = size();
    if (index >= size)
      throw new BasicIndexOutOfBoundsException(index, size);

    if (isUnique())
    {
      int currentIndex = indexOf(object);
      if (currentIndex >=0 && currentIndex != index)
      {
        throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
      }
    }

    return setUnique(index, object);
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
  public abstract E setUnique(int index, E object);

  /**
   * Adds the object at the end of the list
   * and returns whether the object was added;
   * if {@link #isUnique uniqueness} is required,
   * duplicates will be ignored and <code>false</code> will be returned.
   * This implementation delegates to {@link #addUnique(Object) addUnique(E)}
   * after uniqueness checking.
   * @param object the object to be added.
   * @return whether the object was added.
   * @see #addUnique(Object)
   */
  @Override
  public boolean add(E object)
  {
    if (isUnique() && contains(object))
    {
      return false;
    }
    else
    {
      addUnique(object);
      return true;
    }
  }

  /**
   * Adds the object at the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * after uniqueness checking.
   * @param object the object to be added.
   * @see #add(Object)
   */
  public abstract void addUnique(E object);

  /**
   * Adds the object at the given index in the list.
   * If {@link #isUnique uniqueness} is required,
   * duplicates will be ignored.
   * This implementation delegates to {@link #addUnique(int, Object) addUnique(int, E)}
   * after uniqueness checking.
   * @param object the object to be added.
   * @exception IllegalArgumentException if {@link #isUnique uniqueness} is required,
   * and the object is a duplicate.
   * @see #addUnique(int, Object)
   */
  @Override
  public void add(int index, E object)
  {
    int size = size();
    if (index > size)
      throw new BasicIndexOutOfBoundsException(index, size);

    if (isUnique() && contains(object))
    {
      throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
    }

    addUnique(index, object);
  }

  /**
   * Adds the object at the given index in the list;
   * it does no ranging checking or uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param object the object to be added.
   * @see #add(int, Object)
   */
  public abstract void addUnique(int index, E object);

  /**
   * Adds each object of the collection to the end of the list.
   * If {@link #isUnique uniqueness} is required,
   * duplicates will be {@link #getNonDuplicates removed} from the collection,
   * which could even result in an empty collection.
   * This implementation delegates to {@link #addAllUnique(Collection) addAllUnique(Collection)}
   * after uniqueness checking.
   * @param collection the collection of objects to be added.
   * @see #addAllUnique(Collection)
   */
  @Override
  public boolean addAll(Collection<? extends E> collection)
  {
    if (isUnique())
    {
       collection = getNonDuplicates(collection);
    }
    return addAllUnique(collection);
  }

  /**
   * Adds each object of the collection to the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #assign assign}, {@link #didAdd didAdd}, and {@link #didChange didChange}.
   * @param collection the collection of objects to be added.
   * @see #addAll(Collection)
   */
  public abstract boolean addAllUnique(Collection<? extends E> collection);

  /**
   * Adds each object of the collection at each successive index in the list
   * and returns whether any objects were added.
   * If {@link #isUnique uniqueness} is required,
   * duplicates will be {@link #getNonDuplicates removed} from the collection,
   * which could even result in an empty collection.
   * This implementation delegates to {@link #addAllUnique(int, Collection) addAllUnique(int, Collection)}
   * after uniqueness checking.
   * @param index the index at which to add.
   * @param collection the collection of objects to be added.
   * @return whether any objects were added.
   * @see #addAllUnique(int, Collection)
   */
  @Override
  public boolean addAll(int index, Collection<? extends E> collection)
  {
    int size = size();
    if (index > size)
      throw new BasicIndexOutOfBoundsException(index, size);

    if (isUnique())
    {
      collection = getNonDuplicates(collection);
    }
    return addAllUnique(index, collection);
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
  public abstract boolean addAllUnique(int index, Collection<? extends E> collection);

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
  public abstract boolean addAllUnique(Object [] objects, int start, int end);

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
  public abstract boolean addAllUnique(int index, Object [] objects, int start, int end);

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
    for (int i = size(); --i >= 0; )
    {
      if (collection.contains(primitiveGet(i)))
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
  @Override
  public abstract E remove(int index);

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
    for (int i = size(); --i >= 0; )
    {
      if (!collection.contains(primitiveGet(i)))
      {
        remove(i);
        modified = true;
      }
    }
    return modified;
  }

  /**
   * Moves the object to the index of the list.
   * This implementation uses {@link #indexOf} of find the object
   * and delegates to {@link #move(int, int) move(int, int)}.
   * @param index the new position for the object in the list.
   * @param object the object to be moved.
   * @exception IndexOutOfBoundsException if the index isn't within the size range or the object isn't contained by the list.
   */
  public void move(int index, E object)
  {
    move(index, indexOf(object));
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
  public abstract E move(int targetIndex, int sourceIndex);


  /**
   * Returns whether the object is a list with corresponding equal objects.
   * This implementation uses either <code>equals</code> or <code>"=="</code> depending on {@link #useEquals useEquals}.
   * @return whether the object is a list with corresponding equal objects.
   * @see #useEquals
   */
  @Override
  public boolean equals(Object object) 
  {
    if (object == this)
    {
      return true;
    }

    if (!(object instanceof List<?>))
    {
      return false;
    }

    List<?> list = (List<?>)object;
    int size = size();
    if (list.size() != size)
    {
      return false;
    }

    Iterator<?> objects = list.iterator();
    if (useEquals())
    {
      for (int i = 0; i < size; ++i)
      {
        Object o1 = primitiveGet(i);
        Object o2 = objects.next();
        if (o1 == null ? o2 != null : !o1.equals(o2))
        {
          return false;
        }
      }
    }
    else
    {
      for (int i = 0; i < size; ++i)
      {
        Object o1 = primitiveGet(i);
        Object o2 = objects.next();
        if (o1 != o2)
        {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Returns a hash code computed from each object's hash code.
   * @return a hash code.
   */
  @Override
  public int hashCode() 
  {
    int hashCode = 1;
    for (int i = 0, size = size(); i < size; ++i)
    {
      Object object = primitiveGet(i);
      hashCode = 31 * hashCode + (object == null ? 0 : object.hashCode());
    }
    return hashCode;
  }

  /**
   * Returns a string of the form <code>"[object1, object2]"</code>.
   * @return a string of the form <code>"[object1, object2]"</code>.
   */
  @Override
  public String toString() 
  {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[");
    for (int i = 0, size = size(); i < size; )
    {
      stringBuffer.append(String.valueOf(primitiveGet(i)));
      if (++i < size)
      {
        stringBuffer.append(", ");
      }
    }
    stringBuffer.append("]");
    return stringBuffer.toString();
  }

  /**
   * Returns an iterator.
   * This implementation allocates a {@link AbstractEList.EIterator}.
   * @return an iterator.
   * @see AbstractEList.EIterator
   */
  @Override
  public Iterator<E> iterator()
  {
    return new EIterator<E>();
  }

  /**
   * An extensible iterator implementation.
   */
  protected class EIterator<E1> implements Iterator<E1>
  {
    /**
     * The current position of the iterator.
     */
    protected int cursor = 0;

    /**
     * The previous position of the iterator.
     */
    protected int lastCursor = -1;

    /**
     * Returns whether there are more objects.
     * @return whether there are more objects.
     */
    public boolean hasNext()
    {
      return cursor != size();
    }

    /**
     * Returns the next object and advances the iterator.
     * This implementation delegates to {@link #doNext doNext}.
     * @return the next object.
     * @exception NoSuchElementException if the iterator is done.
     */
    @SuppressWarnings("unchecked")
    public E1 next()
    {
      return (E1)doNext();
    }

    /**
     * Returns the next object and advances the iterator.
     * This implementation delegates to {@link AbstractEList#get get}.
     * @return the next object.
     * @exception NoSuchElementException if the iterator is done.
     */
    protected E doNext()
    {
      try
      {
        E next = get(cursor);
        checkModCount();
        lastCursor = cursor++;
        return next;
      }
      catch (IndexOutOfBoundsException exception)
      {
        checkModCount();
        throw new NoSuchElementException();
      }
    }

    /**
     * Removes the last object returned by {@link #next()} from the list,
     * it's an optional operation.
     * This implementation can also function in a list iterator
     * to act upon on the object returned by calling <code>previous</code>.
     * @exception IllegalStateException
     * if <code>next</code> has not yet been called,
     * or <code>remove</code> has already been called after the last call to <code>next</code>.
     */
    public void remove()
    {
      if (lastCursor == -1)
      {
        throw new IllegalStateException();
      }
      checkModCount();

      try
      {
        AbstractEList.this.remove(lastCursor);
        if (lastCursor < cursor)
        {
          --cursor;
        }
        lastCursor = -1;
      }
      catch (IndexOutOfBoundsException exception)
      {
        throw new ConcurrentModificationException();
      }
    }

    /**
     * Checks that the modification count is as expected.
     * @exception ConcurrentModificationException if the modification count is not as expected.
     */
    protected void checkModCount()
    {
      // Unsupported.
    }
  }

  /**
   * Returns a read-only iterator that does not {@link #resolve resolve} objects.
   * This implementation allocates a {@link NonResolvingEIterator}.
   * @return a read-only iterator that does not resolve objects.
   */
  protected Iterator<E> basicIterator()
  {
    return new NonResolvingEIterator<E>();
  }

  /**
   * An extended read-only iterator that does not {@link AbstractEList#resolve resolve} objects.
   */
  protected class NonResolvingEIterator<E1> extends EIterator<E1>
  {
    /**
     * Returns the next object and advances the iterator.
     * This implementation accesses the data storage directly.
     * @return the next object.
     * @exception NoSuchElementException if the iterator is done.
     */
    @Override
    protected E doNext()
    {
      try
      {
        E next = primitiveGet(cursor);
        checkModCount();
        lastCursor = cursor++;
        return next;
      }
      catch (IndexOutOfBoundsException exception)
      {
        checkModCount();
        throw new NoSuchElementException();
      }
    }

    /**
     * Throws and exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Returns a list iterator.
   * This implementation allocates a {@link AbstractEList.EListIterator}.
   * @return a list iterator.
   * @see AbstractEList.EListIterator
   */
  @Override
  public ListIterator<E> listIterator()
  {
    return new EListIterator<E>();
  }

  /**
   * Returns a list iterator advanced to the given index.
   * This implementation allocates a {@link AbstractEList.EListIterator}.
   * @param index the starting index.
   * @return a list iterator advanced to the index.
   * @see AbstractEList.EListIterator
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
  protected class EListIterator<E1> extends EIterator<E1> implements ListIterator<E1>
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
      cursor = index;
    }

    /**
     * Returns whether there are more objects for {@link #previous}.
     * Returns whether there are more objects.
     */
    public boolean hasPrevious()
    {
      return cursor != 0;
    }

    /**
     * Returns the previous object and advances the iterator.
     * This implementation delegates to {@link #doPrevious doPrevious}.
     * @return the previous object.
     * @exception NoSuchElementException if the iterator is done.
     */
    @SuppressWarnings("unchecked")
    public E1 previous()
    {
      return (E1)doPrevious();
    }

    /**
     * Returns the previous object and advances the iterator.
     * This implementation delegates to {@link AbstractEList#get get}.
     * @return the previous object.
     * @exception NoSuchElementException if the iterator is done.
     */
    protected E doPrevious()
    {
      try
      {
        E previous = get(--cursor);
        checkModCount();
        lastCursor = cursor;
        return previous;
      }
      catch (IndexOutOfBoundsException exception)
      {
        checkModCount();
        throw new NoSuchElementException();
      }
    }

    /**
     * Returns the index of the object that would be returned by calling {@link #next() next}.
     * @return the index of the object that would be returned by calling <code>next</code>.
     */
    public int nextIndex()
    {
      return cursor;
    }

    /**
     * Returns the index of the object that would be returned by calling {@link #previous previous}.
     * @return the index of the object that would be returned by calling <code>previous</code>.
     */
    public int previousIndex()
    {
      return cursor - 1;
    }

    /**
     * Sets the object at the index of the last call to {@link #next() next} or {@link #previous previous}.
     * This implementation delegates to {@link AbstractEList#set set}.
     * @param object the object to set.
     * @exception IllegalStateException
     * if <code>next</code> or <code>previous</code> have not yet been called,
     * or {@link #remove(Object) remove} or {@link #add add} have already been called
     * after the last call to <code>next</code> or <code>previous</code>.
     */
    @SuppressWarnings("unchecked")
    public void set(E1 object)
    {
      doSet((E)object);
    }

    /**
     * Sets the object at the index of the last call to {@link #next() next} or {@link #previous previous}.
     * This implementation delegates to {@link AbstractEList#set set}.
     * @param object the object to set.
     * @exception IllegalStateException
     * if <code>next</code> or <code>previous</code> have not yet been called,
     * or {@link #remove(Object) remove} or {@link #add add} have already been called
     * after the last call to <code>next</code> or <code>previous</code>.
     */
    protected void doSet(E object)
    {
      if (lastCursor == -1)
      {
        throw new IllegalStateException();
      }
      checkModCount();

      try
      {
        AbstractEList.this.set(lastCursor, object);
      }
      catch (IndexOutOfBoundsException exception)
      {
        throw new ConcurrentModificationException();
      }
    }

    /**
     * Adds the object at the {@link #next() next} index and advances the iterator past it.
     * This implementation delegates to {@link #doAdd(Object) doAdd(E)}.
     * @param object the object to add.
     */
    @SuppressWarnings("unchecked")
    public void add(E1 object)
    {
      doAdd((E)object);
    }

    /**
     * Adds the object at the {@link #next() next} index and advances the iterator past it.
     * This implementation delegates to {@link AbstractEList#add(int, Object) add(int, E)}.
     * @param object the object to add.
     */
    protected void doAdd(E object)
    {
      checkModCount();

      try
      {
        AbstractEList.this.add(cursor++, object);
        lastCursor = -1;
      }
      catch (IndexOutOfBoundsException exception)
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
  protected ListIterator<E> basicListIterator(int index)
  {
    int size = size();
    if (index < 0 || index > size)
      throw new BasicIndexOutOfBoundsException(index, size);

    return new NonResolvingEListIterator<E>(index);
  }

  /**
   * An extended read-only list iterator that does not {@link AbstractEList#resolve resolve} objects.
   */
  protected class NonResolvingEListIterator<E1> extends EListIterator<E1>
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

    /**
     * Returns the next object and advances the iterator.
     * This implementation accesses the data storage directly.
     * @return the next object.
     * @exception NoSuchElementException if the iterator is done.
     */
    @Override
    protected E doNext()
    {
      try
      {
        E next = primitiveGet(cursor);
        checkModCount();
        lastCursor = cursor++;
        return next;
      }
      catch (IndexOutOfBoundsException exception)
      {
        checkModCount();
        throw new NoSuchElementException();
      }
    }

    /**
     * Returns the previous object and advances the iterator.
     * This implementation accesses the data storage directly.
     * @return the previous object.
     * @exception NoSuchElementException if the iterator is done.
     */
    @Override
    protected E doPrevious()
    {
      try
      {
        E previous = primitiveGet(--cursor);
        checkModCount();
        lastCursor = cursor;
        return previous;
      }
      catch (IndexOutOfBoundsException exception)
      {
        checkModCount();
        throw new NoSuchElementException();
      }
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public void set(E1 object)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Throws an exception.
     * @exception UnsupportedOperationException always because it's not supported.
     */
    @Override
    public void add(E1 object)
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Returns an <b>unsafe</b> list that provides a {@link #resolve non-resolving} view of the underlying data storage.
   * @return an <b>unsafe</b> list that provides a non-resolving view of the underlying data storage.
   */
  protected abstract List<E> basicList();

  /**
   * Returns the collection of objects in the given collection that are also contained by this list.
   * @param collection the other collection.
   * @return the collection of objects in the given collection that are also contained by this list.
   */
  protected Collection<E> getDuplicates(Collection<?> collection)
  {
    if (collection.isEmpty())
    {
      return ECollections.emptyEList();
    }
    else
    {
      Collection<E> filteredResult = useEquals() ? new BasicEList<E>(collection.size()) : new BasicEList.FastCompare<E>(collection.size());
      for (E object : this)
      {
        if (collection.contains(object))
        {
          filteredResult.add(object);
        }
      }
      return filteredResult;
    }
  }

  /**
   * Returns the collection of objects in the given collection that are not also contained by this list.
   * @param collection the other collection.
   * @return the collection of objects in the given collection that are not also contained by this list.
   */
  protected Collection<E> getNonDuplicates(Collection<? extends E> collection)
  {
    Collection<E> result = useEquals() ?  new UniqueEList<E>(collection.size()) : new UniqueEList.FastCompare<E>(collection.size());
    for (E object : collection)
    {
      if (!contains(object))
      {
        result.add(object);
      }
    }
    return result;
  }
}
