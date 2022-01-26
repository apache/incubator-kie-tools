/**
 * Copyright (c) 2002-2012 IBM Corporation and others.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList.BasicIndexOutOfBoundsException;


/**
 * Support for {@link #EMPTY_ELIST empty} and {@link #unmodifiableEList unmodifiable} <code>EList</code>s.
 */
public class ECollections
{
  // Suppress default constructor for noninstantiability.
  private ECollections()
  {
    super();
  }
  
  /**
   * Moves the object to the new position, if is in the list.
   * @param list
   * @param newPosition the position of the object after the move.
   * @param object the object to move.
   */
  public static <T> void move(List<T> list, int newPosition, T object)
  {
    if (list instanceof EList<?>)
    {
      ((EList<T>)list).move(newPosition, object);
    }
    else
    {
      list.remove(object);
      list.add(newPosition, object);
    }
  }

  /**
   * Moves the object from the old position to the new position.
   * @param list
   * @param targetIndex the position of the object after the move.
   * @param sourceIndex the position of the object before the move.
   * @return the moved object
   */
  public static <T> T move(List<T> list, int targetIndex, int sourceIndex)
  {
    if (list instanceof EList<?>)
    {
      return ((EList<T>)list).move(targetIndex, sourceIndex);
    }
    else
    {
      T object = list.remove(sourceIndex);
      list.add(targetIndex, object);
      return object;
    }    
  }

  /**
   * Reverses the order of the elements in the specified EList.
   */
  public static void reverse(EList<?> list)
  {
    int last = list.size() - 1;
    for (int i = 0; i < last; i++)
    {
      list.move(i, last);
    }
  }
  
  /**
   * Searches for the first occurrence of the given argument in list starting from
   * a specified index.  The equality is tested using the operator <tt>==<tt> and
   * the <tt>equals</tt> method. 
   * @param list
   * @param o an object (can be null)
   * @param fromIndex 
   * @return the index of the first occurrence of the argument in this
   *         list (where index>=fromIndex); returns <tt>-1</tt> if the 
   *         object is not found.
   * @since 2.1.0
   */
  public static int indexOf(List<?> list, Object o, int fromIndex)
  {
    if (fromIndex < 0)
    {
      fromIndex = 0;
    }

    int size = list.size();
    for (int i = fromIndex; i < size; i++)
    {
      Object element = list.get(i);
      if (o == null)
      {
        if (element == null)
        {
          return i;
        }
      }
      else if (o == element || o.equals(element))
      {
        return i;
      }
    }
    return -1;
  }

  /**
   * Sorts the specified list.  Use this method instead of 
   * {@link Collections#sort(java.util.List)} to 
   * avoid errors when sorting unique lists.
   * @since 2.1.0
  */
  public static void sort(EList<?> list)
  {
    Object[] listAsArray = list.toArray();
    Arrays.sort(listAsArray);   
    for (int i=0; i < listAsArray.length; i++)
    {
      int oldIndex = indexOf(list, listAsArray[i], i);
      if (i != oldIndex)
      {
        list.move(i, oldIndex);
      }
    }    
  }
  
  /**
   * Sorts the specified list based on the order defined by the
   * specified comparator.  Use this method instead of 
   * {@link Collections#sort(java.util.List, java.util.Comparator)} to 
   * avoid errors when sorting unique lists.
   * @since 2.1.0
   */
  public static <T> void sort(EList<T> list, Comparator<? super T> comparator)
  {
    Object[] listAsArray = list.toArray();
    @SuppressWarnings("unchecked") Comparator<Object> objectComparator = (Comparator<Object>)comparator;
    Arrays.sort(listAsArray, objectComparator);
    for (int i=0; i < listAsArray.length; i++)
    {
      int oldIndex = indexOf(list, listAsArray[i], i);
      if (i != oldIndex)
      {
        list.move(i, oldIndex);
      }
    }    
  }
  
  /** 
   * Sets the <code>eList</code>'s contents and order to be exactly that of the <code>prototype</code> list.
   * This implementation minimizes the number of notifications the operation will produce.
   * Objects already in the list will be moved, missing objects will be added, and extra objects will be removed.
   * If <code>eList</code>'s contents and order are already exactly that of the <code>prototype</code> list,
   * no change will be made.
   * @param eList the list to set.
   * @param prototypeList the list representing the desired content and order.
   */
  public static <T> void setEList(EList<T> eList, List<? extends T> prototypeList)
  {
    int index = 0;
    for (T prototypeObject : prototypeList)
    {
      if (eList.size() <= index)
      {
        eList.add(prototypeObject);
      }
      else
      {
        boolean done;
        do
        {
          done = true;
          Object targetObject = eList.get(index);
          if (targetObject == null ? prototypeObject != null : !targetObject.equals(prototypeObject))
          {
            int position = indexOf(eList, prototypeObject, index);
            if (position != -1)
            {
              int targetIndex = indexOf(prototypeList, targetObject, index);
              if (targetIndex == -1)
              {
                eList.remove(index);
                done = false;
              }
              else if (targetIndex > position)
              {
                if (eList.size() <= targetIndex)
                {
                  targetIndex = eList.size() - 1;
                }
                eList.move(targetIndex, index);

                done = false;
              }
              else
              {
                eList.move(index, position);
              }
            }
            else
            {
              eList.add(index, prototypeObject);
            }
          }
        }
        while (!done);
      }
      ++index;
    }
    for (int i = eList.size(); i > index;)
    {
      eList.remove(--i);
    }
  }
  
  /**
   * Returns an unmodifiable view of the list.
   * @return an unmodifiable view of the list.
   */
  public static <T> EList<T> unmodifiableEList(EList<? extends T> list)
  {
    return new UnmodifiableEList<T>(list);
  }

  /**
   * Returns an unmodifiable view of the map.
   * @return an unmodifiable view of the map.
   */
  public static <K, V> EMap<K, V> unmodifiableEMap(EMap<? extends K, ? extends V> map)
  {
    return new UnmodifiableEMap<K, V>(map);
  }

  /**
   * An unmodifiable empty list with an efficient reusable iterator.
   */
  public static final EList<?> EMPTY_ELIST = new EmptyUnmodifiableEList();
  
  /**
   * Returns an empty unmodifiable list.
   * @return an empty unmodifiable list.
   */
  @SuppressWarnings("unchecked")
  public static <T> EList<T> emptyEList()
  {
    return (EList<T>)EMPTY_ELIST;
  }

  /**
   * An unmodifiable empty map with an efficient reusable iterator.
   */
  public static final EMap<?, ?> EMPTY_EMAP = new EmptyUnmodifiableEMap();
  
  /**
   * Returns an empty unmodifiable map.
   * @return an empty unmodifiable map.
   */
  @SuppressWarnings("unchecked")
  public static <K, V> EMap<K, V> emptyEMap()
  {
    return (EMap<K, V>)EMPTY_EMAP;
  }

  private static class UnmodifiableEList<E> implements EList<E>
  {
    protected List<? extends E> list;

    public UnmodifiableEList(List<? extends E> list)
    {
      this.list = list;
    }

    public int size()
    {
      return list.size();
    }

    public boolean isEmpty()
    {
      return list.isEmpty();
    }

    public boolean contains(Object o)
    {
      return list.contains(o);
    }

    public Object[] toArray()
    {
      return list.toArray();
    }

    public <T> T[] toArray(T[] a)
    {
      return list.toArray(a);
    }

    @Override
    public String toString()
    {
      return list.toString();
    }

    public Iterator<E> iterator()
    {
      return 
        new Iterator<E>()
        {
          Iterator<? extends E> i = list.iterator();

          public boolean hasNext()
          {
            return i.hasNext();
          }
          public E next()
          {
            return i.next();
          }
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
    }

    public boolean add(E o)
    {
      throw new UnsupportedOperationException();
    }

    public boolean remove(Object o)
    {
      throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> coll)
    {
      return list.containsAll(coll);
    }

    public boolean addAll(Collection<? extends E> coll)
    {
      throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> coll)
    {
      throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> coll)
    {
      throw new UnsupportedOperationException();
    }

    public void clear()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o)
    {
      return list.equals(o);
    }

    @Override
    public int hashCode()
    {
      return list.hashCode();
    }

    public E get(int index)
    {
      return list.get(index);
    }

    public E set(int index, E element)
    {
      throw new UnsupportedOperationException();
    }

    public void add(int index, Object element)
    {
      throw new UnsupportedOperationException();
    }

    public E remove(int index)
    {
      throw new UnsupportedOperationException();
    }

    public int indexOf(Object o)
    {
      return list.indexOf(o);
    }

    public int lastIndexOf(Object o)
    {
      return list.lastIndexOf(o);
    }

    public boolean addAll(int index, Collection<? extends E> collection)
    {
      throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator()
    {
      return listIterator(0);
    }

    public ListIterator<E> listIterator(final int index)
    {
      return 
        new ListIterator<E>()
        {
          ListIterator<? extends E> i = list.listIterator(index);

          public boolean hasNext()
          {
            return i.hasNext();
          }

          public E next()
          {
            return i.next();
          }

          public boolean hasPrevious()
          {
            return i.hasPrevious();
          }

          public E previous()
          {
            return i.previous();
          }

          public int nextIndex()
          {
            return i.nextIndex();
          }

          public int previousIndex()
          {
            return i.previousIndex();
          }

          public void remove()
          {
            throw new UnsupportedOperationException();
          }

          public void set(E o)
          {
            throw new UnsupportedOperationException();
          }

          public void add(E o)
          {
            throw new UnsupportedOperationException();
          }
        };
    }

    public List<E> subList(int fromIndex, int toIndex)
    {
      return new UnmodifiableEList<E>(new BasicEList<E>(list.subList(fromIndex, toIndex)));
    }

    public void move(int newPosition, E o)
    {
      throw new UnsupportedOperationException();
    }

    public E move(int newPosition, int oldPosition)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class UnmodifiableEMap<K, V> extends UnmodifiableEList<Map.Entry<K, V>> implements EMap<K, V>
  {
    protected EMap<? extends K, ? extends V> eMap;
    
    @SuppressWarnings("unchecked")
    public UnmodifiableEMap(EMap<? extends K, ? extends V> eMap)
    {
      super((EMap<K, V>)eMap);
      this.eMap = eMap;
    }
    
    public boolean containsKey(Object key)
    {
      return eMap.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
      return eMap.containsValue(value);
    }

    @SuppressWarnings("unchecked")
    public Set<Map.Entry<K, V>> entrySet()
    {
      return Collections.unmodifiableSet((Set<Map.Entry<K, V>>)(Set<?>)eMap.entrySet());
    }

    public V get(Object key)
    {
      return eMap.get(key);
    }

    public int indexOfKey(Object key)
    {
      return eMap.indexOf(key);
    }

    public Set<K> keySet()
    {
      return Collections.unmodifiableSet(eMap.keySet());
    }

    public Map<K, V> map()
    {
      return Collections.unmodifiableMap(eMap.map());
    }
    
    public Collection<V> values()
    {
      return Collections.unmodifiableCollection(eMap.values());
    }
    
    public V put(K key, V value)
    {
      throw new UnsupportedOperationException();
    }

    public void putAll(EMap<? extends K, ? extends V> map)
    {
      throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends K, ? extends V> map)
    {
      throw new UnsupportedOperationException();
    }

    public V removeKey(Object key)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class BasicEmptyUnmodifiableEList<E>
  {
    public int size()
    {
      return 0;
    }

    public boolean isEmpty()
    {
      return true;
    }

    @Override
    public boolean equals(Object o)
    {
      return Collections.EMPTY_LIST.equals(o);
    }

    @Override
    public int hashCode()
    {
      return Collections.EMPTY_LIST.hashCode();
    }

    public E get(int index)
    {
      Collections.EMPTY_LIST.get(index);
      return null;
    }

    public boolean contains(Object o)
    {
      return false;
    }

    public int indexOf(Object o)
    {
      return -1;
    }

    public int lastIndexOf(Object o)
    {
      return -1;
    }

    ListIterator<E> listIterator = 
      new ListIterator<E>()
      {
        public boolean hasNext()
        {
          return false;
        }
        public E next()
        {
          throw new NoSuchElementException();
        }
        public boolean hasPrevious()
        {
          return false;
        }
        public E previous()
        {
          throw new NoSuchElementException();
        }
        public int nextIndex()
        {
          return 0;
        }
        public int previousIndex()
        {
          return -1;
        }

        public void remove()
        {
          throw new UnsupportedOperationException();
        }
        public void set(E o)
        {
          throw new UnsupportedOperationException();
        }
        public void add(E o)
        {
          throw new UnsupportedOperationException();
        }
     };

    public Iterator<E> iterator()
    {
      return listIterator;
    }

    public ListIterator<E> listIterator()
    {
      return listIterator;
    }

    public ListIterator<E> listIterator(int index)
    {
      return listIterator;
    }

    public List<E> subList(int fromIndex, int toIndex)
    {
      return Collections.<E>emptyList().subList(fromIndex, toIndex);
    }

    public Object[] toArray()
    {
      return Collections.EMPTY_LIST.toArray();
    }

    public <T> T[] toArray(T[] a)
    {
      return Collections.<T>emptyList().toArray(a);
    }

    @Override
    public String toString()
    {
      return Collections.EMPTY_LIST.toString();
    }

    public boolean add(E o)
    {
      throw new UnsupportedOperationException();
    }

    public boolean remove(Object o)
    {
      throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> coll)
    {
      return false;
    }

    public boolean addAll(Collection<? extends E> coll)
    {
      throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> coll)
    {
      throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> coll)
    {
      throw new UnsupportedOperationException();
    }

    public void clear()
    {
      throw new UnsupportedOperationException();
    }

    public E set(int index, E element)
    {
      throw new UnsupportedOperationException();
    }

    public void add(int index, E element)
    {
      throw new UnsupportedOperationException();
    }

    public E remove(int index)
    {
      throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection<? extends E> collection)
    {
      throw new UnsupportedOperationException();
    }

    public void move(int newPosition, E o)
    {
      throw new UnsupportedOperationException();
    }

    public E move(int newPosition, int oldPosition)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class EmptyUnmodifiableEList extends BasicEmptyUnmodifiableEList<Object> implements EList<Object>
  {
    private EmptyUnmodifiableEList()
    {
      super();
    }
  }

  private static class EmptyUnmodifiableEMap extends BasicEmptyUnmodifiableEList<Map.Entry<Object, Object>> implements EMap<Object, Object>
  {
    public boolean containsKey(Object key)
    {
      return false;
    }

    public boolean containsValue(Object value)
    {
      return false;
    }

    public Set<Map.Entry<Object, Object>> entrySet()
    {
      return Collections.emptySet();
    }

    public Object get(Object key)
    {
      return null;
    }

    public int indexOfKey(Object key)
    {
      return -1;
    }

    public Set<Object> keySet()
    {
      return Collections.emptySet();
    }

    public Map<Object, Object> map()
    {
      return Collections.emptyMap();
    }
    
    public Collection<Object> values()
    {
      return Collections.emptyList();
    }
    
    public Object put(Object key, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public void putAll(EMap<? extends Object, ? extends Object> map)
    {
      throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends Object, ? extends Object> map)
    {
      throw new UnsupportedOperationException();
    }

    public Object removeKey(Object key)
    {
      throw new UnsupportedOperationException();
    }    
  }

  /**
   * Returns an immutable list containing just the one object.
   * @return an immutable list containing just the one object.
   * @since 2.7
   */
  public static <T> EList<T> singletonEList(T o)
  {
    return new UnmodifiableEList<T>(Collections.singletonList(o));
  }

  /**
   * Returns an immutable map containing just the one key/value mapping.
   * @return an immutable map containing just the one key/value mapping.
   * @since 2.7
   */
  public static <K, V> EMap<K, V> singletonEMap(K key, V value)
  {
    BasicEMap<K, V> result = new BasicEMap<K, V>(1);
    result.put(key, value);
    return new UnmodifiableEMap<K, V>(result);
  }
  
  /**
   * Returns a mutable list containing the elements of the given iterator.
   * @return a mutable list containing the same elements as the given iterator.
   * @since 2.9
   */
  public static <T> EList<T> toEList(Iterator<? extends T> iterator)
  {
    return ECollections.newBasicEList(iterator);
  }

  /**
   * Returns a list containing the elements of the given iterable.
   * If the iterable is of type {@link EList}, that list itself is returned.
   * If the iterable is of type {@link List}, a {@link ECollections#asEList(List) view} of that list is returned;
   * all changes to view are reflected in the underlying list and all changes to the underlying list are reflected in the view.
   * In all other cases, the result is a {@link ECollections#newBasicEList(Iterable) copy} of the iterable.
   * @return a list containing the same elements as the given iterable.
   * @since 2.9
   */
  public static <T> EList<T> toEList(Iterable<? extends T> iterable)
  {
    if (iterable instanceof EList)
    {
      @SuppressWarnings("unchecked")
      EList<T> result = (EList<T>)iterable;
      return result;
    }
    else if (iterable instanceof List)
    {
      @SuppressWarnings("unchecked")
      final List<T> list = (List<T>)iterable;
      return
        new DelegatingEList<T>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected List<T> delegateList()
          {
            return list;
          }
        };
    }
    else
    {
      return ECollections.newBasicEList(iterable);
    }
  }

  /**
   * Returns a mutable EMap view of the specified map.
   * {@link EList#move(int, int)}, {@link EList#move(int, Object)}, {@link List#add(int, Object)}, and {@link List#addAll(int, Collection)},
   * i.e., the methods that expect to control the exact order of entries in the map's entry set,
   * are not supported and throw {@link UnsupportedOperationException}.
   * All other changes to the EMap write through to the underlying map.
   * @param map the map to which the EMap delegates.
   * @return an EMap view of the specified map.
   * @since 2.9
   */
  public static <K, V> EMap<K, V> asEMap(final Map<K, V> map)
  {
    return 
      new EMap<K,V>()
      {
        public void move(int newPosition, Map.Entry<K, V> object)
        {
          throw new UnsupportedOperationException();
        }

        public Map.Entry<K, V> move(int newPosition, int oldPosition)
        {
          throw new UnsupportedOperationException();
        }
        
        public void add(int index, Map.Entry<K, V> element)
        {
          throw new UnsupportedOperationException();
        }

        public boolean addAll(int index, Collection<? extends Map.Entry<K, V>> c)
        {
          throw new UnsupportedOperationException();
        }

        public int size()
        {
          return map.size();
        }

        public boolean isEmpty()
        {
          return map.isEmpty();
        }

        public boolean contains(Object o)
        {
          return map.entrySet().contains(o) || map.containsKey(o);
        }

        public Iterator<Map.Entry<K, V>> iterator()
        {
          return map.entrySet().iterator();
        }

        public Object[] toArray()
        {
          return map.entrySet().toArray();
        }

        public <T> T[] toArray(T[] a)
        {
          return map.entrySet().toArray(a);
        }

        public boolean add(Map.Entry<K, V> e)
        {
          K key = e.getKey();
          boolean result = map.containsKey(key);
          map.put(key,  e.getValue());
          return result;
        }

        public boolean remove(Object o)
        {
          if (o instanceof Map.Entry)
          {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
            Object key = entry.getKey();
            if (map.containsKey(key))
            {
              map.remove(key);
              return true;
            }
          }
          if (map.containsKey(o))
          {
            map.remove(o);
            return true;
          }
          return false;
        }

        public boolean containsAll(Collection<?> c)
        {
          return map.entrySet().containsAll(c);
        }

        public boolean addAll(Collection<? extends Map.Entry<K, V>> c)
        {
          boolean result = false;
          for (Map.Entry<K, V> entry : c)
          {
            if (add(entry))
            {
              result = true;
            }
          }
          return result;
        }

        public boolean removeAll(Collection<?> c)
        {
          boolean result = false;
          for (Object o : c)
          {
            if (remove(o))
            {
              result = true;
            }
          }
          return result;
        }

        public boolean retainAll(Collection<?> c)
        {
          return listView().retainAll(c);
        }

        public void clear()
        {
          map.clear();
        }

        protected void rangeCheck(int index)
        {
          int size = map.size();
          if (index >= size || index < 0)
            throw new BasicIndexOutOfBoundsException(index, size);
        }

        public Map.Entry<K, V> get(int index)
        {
          rangeCheck(index);
          int i = 0;
          for (Map.Entry<K, V> entry : map.entrySet())
          {
            if (i++ == index)
            {
              return entry;
            }
          }
          return null;
        }

        public Map.Entry<K, V> set(int index, Map.Entry<K, V> element)
        {
          rangeCheck(index);
          int i = 0;
          for (Map.Entry<K, V> entry : map.entrySet())
          {
            if (i++ == index)
            {
              map.remove(entry.getKey());
              return entry;
            }
          }
          return null;
        }

        public Map.Entry<K, V> remove(int index)
        {
          rangeCheck(index);
          int i = 0;
          for (Map.Entry<K, V> entry : map.entrySet())
          {
            if (i++ == index)
            {
              map.remove(entry.getKey());
              return entry;
            }
          }
          return null;
        }

        public int indexOf(Object o)
        {
          int i = 0;
          for (Map.Entry<K, V> entry : map.entrySet())
          {
            if (entry.equals(o))
            {
              return i;
            }
          }
          return -1;
        }

        public int lastIndexOf(Object o)
        {
          return indexOf(o);
        }

        protected List<Map.Entry<K, V>> listView;
        
        protected Map.Entry<K, V> basicGet(int index)
        {
          return get(index);
        }
        
        protected List<Map.Entry<K, V>> listView()
        {
          if (listView == null)
          {
            listView =
              new AbstractList<Map.Entry<K, V>>()
              {
                @Override
                public Map.Entry<K, V> get(int index)
                {
                  return basicGet(index);
                }

                @Override
                public int size()
                {
                  return map.size();
                }
              };
          }
          return listView;
        }
        
        public ListIterator<Map.Entry<K, V>> listIterator()
        {
          return listView().listIterator();
        }

        public ListIterator<Map.Entry<K, V>> listIterator(int index)
        {
          return listView().listIterator(index);
        }

        public List<Map.Entry<K, V>> subList(int fromIndex, int toIndex)
        {
          return listView().subList(fromIndex, toIndex);
        }

        public V get(Object key)
        {
          return map.get(key);
        }

        public V put(K key, V value)
        {
          return map.put(key, value);
        }

        public void putAll(Map<? extends K, ? extends V> m)
        {
          map.putAll(m);
        }

        public void putAll(EMap<? extends K, ? extends V> m)
        {
          map.putAll(m.map());
        }

        public int indexOfKey(Object key)
        {
          int i = 0;
          for (Map.Entry<K, V> entry : map.entrySet())
          {
            if (key == null ? entry.getKey() == null : key.equals(entry.getKey()))
            {
              return i;
            }
          }
          return -1;
        }

        public boolean containsKey(Object key)
        {
          return map.containsKey(key);
        }

        public boolean containsValue(Object value)
        {
          return map.containsValue(value);
        }

        public V removeKey(Object key)
        {
          return map.remove(key);
        }

        public Map<K, V> map()
        {
          return map;
        }

        public Set<Map.Entry<K, V>> entrySet()
        {
          return map.entrySet();
        }

        public Set<K> keySet()
        {
          return map.keySet();
        }

        public Collection<V> values()
        {
          return map.values();
        }

        @Override
        public boolean equals(Object object)
        {
          if (object instanceof List<?>)
          {
            return listView().equals(object);
          }
          else
          {
            return false;
          }
        }

        @Override
        public int hashCode()
        {
          return listView().hashCode();
        }

        @Override
        public String toString()
        {
          return map.toString();
        }
      };
  }

  /**
   * Returns an EList view of the specified list.
   * All changes to the EList write through to the underlying list.
   * @param list the list to which the EList delegates.
   * @return an EList view of the specified list.
   * @since 2.9
   */
  public static <T> EList<T> asEList(final List<T> list)
  {
    return
      new DelegatingEList<T>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected List<T> delegateList()
        {
          return list;
        }
      };
  }

  /**
   * Returns a mutable, fixed-size, random access EList backed by the given array.
   * Changes to the list, i.e., set and move, write through to the array.
   * All other list modifying operations throw {@link UnsupportedOperationException}.
   * This is analogous to {@link Arrays#asList(Object...)} with the advantage that you can {@link EList#move(int, int) move} objects;
   * hence you can {@link #sort(EList) sort} without using {@link List#add(int, Object)} and {@link List#remove(int)}.
   * @param elements the array to which the EList delegates.
   * @return an EList view of the specified array.
   * @since 2.9
   */
  public static <T> EList<T> asEList(final T...elements)
  {
    return
      new BasicEList<T>()
      {
        private static final long serialVersionUID = 1L;

        {
          this.data = elements;
          size = elements.length;
        }

        @Override
        public void setData(int size, Object[] data)
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public void grow(int minimumCapacity)
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public void shrink()
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public T remove(int index)
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object object)
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public void clear()
        {
          throw new UnsupportedOperationException();
        }
      };
  }

  /**
   * Returns an unmodifiable view of the list.
   * @return an unmodifiable view of the list.
   * @since 2.9
   */
  public static <T> EList<T> unmodifiableEList(List<? extends T> list)
  {
    return new UnmodifiableEList<T>(list);
  }

  /**
   * Creates an empty mutable {@link BasicEList>}.
   * @return an empty mutable {@link BasicEList>}.
   * @since 2.9
   */
  public static <T> BasicEList<T> newBasicEList()
  {
    return new BasicEList<T>();
  }

  /**
   * Creates an empty mutable {@link BasicEList>} with the given capacity.
   * @return an empty mutable {@link BasicEList>}.
   * @since 2.9
   */
  public static <T> BasicEList<T> newBasicEListWithCapacity(int capacity)
  {
    return new BasicEList<T>(capacity);
  }

  /**
   * Creates an empty mutable {@link BasicEList>} with a capacity large enough to hold a bit more than the estimated number of elements.
   * If you know the exact size, use {@link #newBasicEListWithCapacity(int)} instead.
   * @return an empty mutable {@link BasicEList>}.
   * @since 2.9
   */
  public static <T> BasicEList<T> newBasicEListWithExpectedSize(int estimatedSize)
  {
    BasicEList<T> result = new BasicEList<T>();
    result.grow(estimatedSize);
    return result;
  }

  /**
   * Creates a mutable {@link BasicEList>} containing the given elements.
   * @return a mutable {@link BasicEList>} containing the given elements.
   * @since 2.9
   */
  public static <T> BasicEList<T> newBasicEList(T... elements)
  {
    BasicEList<T> result = new BasicEList<T>(elements.length);
    for (T t : elements)
    {
      result.add(t);
    }
    return result;
  }

  /**
   * Creates a mutable {@link BasicEList>} containing the given elements.
   * @return a mutable {@link BasicEList>} containing the given elements.
   * @since 2.9
   */
  public static <T> BasicEList<T> newBasicEList(Iterator<? extends T> iterator)
  {
    BasicEList<T> result = new BasicEList<T>();
    while (iterator.hasNext())
    {
      result.add(iterator.next());
    }
    return result;
  }

  /**
   * Creates a mutable {@link BasicEList>} containing the given elements.
   * @return a mutable {@link BasicEList>} containing the given elements.
   * @since 2.9
   */
  public static <T> BasicEList<T> newBasicEList(Iterable<? extends T> iterable)
  {
    if (iterable instanceof Collection)
    {
      return new BasicEList<T>((Collection<? extends T>)iterable);
    }
    else
    {
      BasicEList<T> result = new BasicEList<T>();
      for (T t : iterable)
      {
        result.add(t);
      }
      return result;
    }
  }
}
