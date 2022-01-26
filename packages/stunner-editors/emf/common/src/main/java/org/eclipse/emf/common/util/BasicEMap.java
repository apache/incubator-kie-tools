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
package  org.eclipse.emf.common.util;


import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * A highly extensible map implementation.
 */
public class BasicEMap<K, V> implements EMap<K, V>, Serializable
{
  private static final long serialVersionUID = 2L;

  /**
   * An extended implementation interface for caching hash values 
   * and for updating an entry that may be manufactured as a uninitialized instance by a factory.
   * No client is expected to use this interface, 
   * other than to implement it in conjunction with a map implementation.
   */
  public interface Entry<K, V> extends Map.Entry<K, V>
  {
    /**
     * Sets the key.
     * This should only be called by the map implementation,
     * since the key of an entry already in the map must be immutable.
     * @param key the key.
     */
    void setKey(K key);

    /**
     * Returns the hash code of the key.
     * Only the map implementation would really care.
     */
    int getHash();

    /**
     * Sets the hash code of the key.
     * This should only be called by the map implementation,
     * since the hash code of the key of an entry already in the map must be immutable.
     * @param hash the hash.
     */
    void setHash(int hash);
  }

  /**
   * The underlying list of entries.
   */
  protected transient EList<Entry<K, V>> delegateEList;

  /**
   * The size of the map.
   */
  protected transient int size;

  /**
   * The array of entry lists into which the hash codes are indexed.
   */
  protected transient BasicEList<Entry<K, V>> [] entryData;

  /**
   * The modification indicator used to ensure iterator integrity.
   */
  protected transient int modCount;

  /**
   * An implementation class to hold the views.
   */
  protected static class View<K, V>
  {
    /**
     * The map view.
     */
    public transient Map<K, V> map;

    /**
     * The map key set view.
     */
    public transient Set<K> keySet;

    /**
     * The entry set view.
     */
    public transient Set<Map.Entry<K, V>> entrySet;

    /**
     * The values collection view.
     */
    public transient Collection<V> values;

    /**
     * Creates an empty instance.
     */
    public View()
    {
      super();
    }
  }

  /**
   * The various alternative views of the map.
   */
  protected transient View<K, V> view;

  /**
   * Creates an empty instance.
   */
  public BasicEMap() 
  {
    initializeDelegateEList();
  } 

  /**
   * Initializes the {@link #delegateEList}.
   * This implementation illustrates the precise pattern that is used to 
   * delegate a list implementation's callback methods to the map implementation.
   */ 
  protected void initializeDelegateEList()
  {
    delegateEList =
      new BasicEList<Entry<K, V>>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected void didAdd(int index, Entry<K, V> newObject)
        {
          doPut(newObject);
        }

        @Override
        protected void didSet(int index, Entry<K, V> newObject, Entry<K, V> oldObject)
        {
          didRemove(index, oldObject);
          didAdd(index, newObject);
        }

        @Override
        protected void didRemove(int index, Entry<K, V> oldObject)
        {
          doRemove(oldObject);
        }

        @Override
        protected void didClear(int size, Object [] oldObjects)
        {
          doClear();
        }

        @Override
        protected void didMove(int index, Entry<K, V> movedObject, int oldIndex)
        {
          doMove(movedObject);
        }
      };
  }

  /**
   * Creates an empty instance with the given capacity.
   * @param initialCapacity the initial capacity of the map before it must grow.
   * @exception IllegalArgumentException if the <code>initialCapacity</code> is negative.
   */
  public BasicEMap(int initialCapacity)
  {
    this();

    if (initialCapacity < 0)
    {
      throw new IllegalArgumentException("Illegal Capacity:" + initialCapacity);
    }

    entryData = newEntryData(initialCapacity);
  }

  /**
   * Creates an instance that is a copy of the map.
   * @param map the initial contents of the map.
   */
  public BasicEMap(Map<? extends K, ? extends V> map) 
  {
    this();
    int mapSize = map.size();
    if (mapSize > 0)
    {
      entryData = newEntryData(2 * mapSize);
      putAll(map);
    }
  }

  /**
   * Returns new allocated entry data storage.
   * Clients may override this to create typed storage, but it's not likely.
   * The cost of type checking via a typed array is negligible.
   * @param capacity the capacity of storage needed.
   * @return new entry data storage.
   */
  @SuppressWarnings("unchecked")
  protected BasicEList<Entry<K, V>> [] newEntryData(int capacity)
  {
    return new BasicEList[capacity];
  }

  /**
   * Ensures that the entry data is created 
   * and is populated with contents of the delegate list.
   */
  protected void ensureEntryDataExists()
  {
    if (entryData == null)
    {
      entryData = newEntryData(2 * size + 1);

      // This should be transparent.
      //
      int oldModCount = modCount;
      size = 0;
      for (Entry<K, V> entry : delegateEList)
      {
        doPut(entry);
      }
      modCount = oldModCount;
    }
  }

  /**
   * Returns a new allocated list of entries.
   * Clients may override this to create typed storage.
   * The cost of type checking via a typed array is negligible.
   * The type must be kept in synch with {@link #newEntry(int, Object, Object) newEntry}.
   * @return a new list of entries.
   * @see #newEntry(int, Object, Object)
   */
  protected BasicEList<Entry<K, V>> newList()
  {
    return
      new BasicEList<Entry<K, V>>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        public Object [] newData(int listCapacity)
        {
          return new BasicEMap.EntryImpl[listCapacity];
        }
      };
  }

  /**
   * Returns a new entry.
   * The key is {@link #validateKey validated} and the value is {@link #validateValue validated}.
   * Clients may override this to create typed storage.
   * The type must be kept in synch with {@link #newList newEntry}.
   * @param hash the cached hash code of the key.
   * @param key the key.
   * @param value the value.
   * @return a new entry.
   * @see #newList
   */
  protected Entry<K, V> newEntry(int hash, K key, V value)
  {
    validateKey(key);
    validateValue(value);
    return new EntryImpl(hash, key, value);
  }

  /**
   * Sets the value of the entry, and returns the former value.
   * The value is {@link #validateValue validated}.
   * @param entry the entry.
   * @param value the value.
   * @return the former value, or <code>null</code>.
   */
  protected V putEntry(Entry<K, V> entry, V value)
  {
    return entry.setValue(value);
  }

  /**
   * Returns whether <code>equals</code> rather than <code>==</code> should be used to compare keys.
   * The default is to return <code>true</code> but clients can optimize performance by returning <code>false</code>.
   * The performance difference is highly significant.
   * @return whether <code>equals</code> rather than <code>==</code> should be used to compare keys.
   */
  protected boolean useEqualsForKey()
  {
    return true;
  }

  /**
   * Returns whether <code>equals</code> rather than <code>==</code> should be used to compare values.
   * The default is to return <code>true</code> but clients can optimize performance by returning <code>false</code>.
   * The performance difference is highly significant.
   * @return whether <code>equals</code> rather than <code>==</code> should be used to compare values.
   */
  protected boolean useEqualsForValue()
  {
    return true;
  }

  /**
   * Resolves the value associated with the key and returns the result.
   * This implementation simply returns the <code>value</code>;
   * clients can use this to transform objects as they are fetched.
   * @param key the key of an entry.
   * @param value the value of an entry.
   * @return the resolved value.
   */
  protected V resolve(K key, V value)
  {
    return value;
  }

  /**
   * Validates a new key.
   * This implementation does nothing,
   * but clients may throw runtime exceptions
   * in order to handle constraint violations.
   * @param key the new key.
   * @exception IllegalArgumentException if a constraint prevents the object from being added.
   */
  protected void validateKey(K key)
  {
    // Do nothing.
  }

  /**
   * Validates a new key.
   * This implementation does nothing,
   * but clients may throw runtime exceptions
   * in order to handle constraint violations.
   * @param value the new value.
   * @exception IllegalArgumentException if a constraint prevents the object from being added.
   */
  protected void validateValue(V value)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that the entry has been added.
   * This implementation does nothing;
   * clients can use this to monitor additions to the map.
   * @param entry the added entry.
   */
  protected void didAdd(Entry<K, V> entry)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that the entry has an updated value.
   * This implementation does nothing;
   * clients can use this to monitor value changes in the map.
   * @param entry the new entry.
   */
  protected void didModify(Entry<K, V> entry, V oldValue)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that the entry has been removed.
   * This implementation does nothing;
   * clients can use this to monitor removals from the map.
   * @param entry the removed entry.
   */
  protected void didRemove(Entry<K, V> entry)
  {
    // Do nothing.
  }

  /**
   * Called to indicate that the map has been cleared.
   * This implementation does calls {@link #didRemove didRemove} for each entry;
   * clients can use this to monitor clearing of the map.
   * @param oldEntryData the removed entries.
   */
  protected void didClear(BasicEList<Entry<K, V>> [] oldEntryData)
  {
    if (oldEntryData != null)
    {
      for (int i = 0; i < oldEntryData.length; ++i)
      {
        BasicEList<Entry<K, V>> eList = oldEntryData[i];
        if (eList != null)
        {
          @SuppressWarnings("unchecked") Entry<K, V> [] entries = (Entry<K, V> [])eList.data;
          int size = eList.size;
          for (int j = 0; j < size; ++j)
          {
            Entry<K, V> entry = entries[j];
            didRemove(entry);
          }
        }
      }
    }
  }

  /**
   * Returns the number of entries in the map.
   * @return the number of entries in the map.
   */
  public int size()
  {
    return size;
  }

  /**
   * Returns whether the map has zero size.
   * @return whether the map has zero size.
   */
  public boolean isEmpty()
  {
    return size == 0;
  }

  /*
   * Javadoc copied from interface.
   */
  public int indexOfKey(Object key) 
  {
    if (useEqualsForKey() && key != null)
    {
      for (int i = 0, size = delegateEList.size(); i < size; ++i)
      {
        Entry<K, V> entry = delegateEList.get(i);
        if (key.equals(entry.getKey()))
        {
          return i;
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateEList.size(); i < size; ++i)
      {
        Entry<K, V> entry = delegateEList.get(i);
        if (key == entry.getKey())
        {
          return i;
        }
      }
    }

    return -1;
  }

  /*
   * Javadoc copied from interface.
   */
  public boolean containsKey(Object key) 
  {
    if (size > 0)
    {
      ensureEntryDataExists();
      int hash = hashOf(key);
      int index = indexOf(hash);
      int entryIndex = entryIndexForKey(index, hash, key);
      return entryIndex != -1;
    }
    else
    {
      return false;
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public boolean containsValue(Object value) 
  {
    if (size > 0)
    {
      ensureEntryDataExists();

      if (useEqualsForValue() && value != null) 
      {
        for (int i = 0; i < entryData.length; ++i)
        {
          BasicEList<Entry<K, V>> eList = entryData[i];
          if (eList != null)
          {
            @SuppressWarnings("unchecked") Entry<K, V> [] entries = (Entry<K, V> [])eList.data;
            int size = eList.size;
            for (int j = 0; j < size; ++j)
            {
              Entry<K, V> entry = entries[j];
              if (value.equals(entry.getValue()))
              {
                return true;
              }
            }
          }
        }
      }
      else 
      {
        for (int i = 0; i < entryData.length; ++i)
        {
          BasicEList<Entry<K, V>> eList = entryData[i];
          if (eList != null)
          {
            @SuppressWarnings("unchecked") Entry<K, V> [] entries = (Entry<K, V> [])eList.data;
            int size = eList.size;
            for (int j = 0; j < size; ++j)
            {
              Entry<K, V> entry = entries[j];
              if (value == entry.getValue())
              {
                return true;
              }
            }
          }
        }
      }
    }

    return false;
  }

  /*
   * Javadoc copied from interface.
   */
  public V get(Object key) 
  {
    if (size > 0)
    {
      ensureEntryDataExists();
      int hash = hashOf(key);
      int index = indexOf(hash);
      Entry<K, V> entry = entryForKey(index, hash, key);
      if (entry != null)
      {
        @SuppressWarnings("unchecked") K object = (K)key;
        return resolve(object, entry.getValue());
      }
    }

    return null;
  }

  /*
   * Javadoc copied from interface.
   */
  public V put(K key, V value) 
  {
    ensureEntryDataExists();

    int hash = hashOf(key);
    if (size > 0)
    {
      int index = indexOf(hash);
      Entry<K, V> entry = entryForKey(index, hash, key);
      if (entry != null)
      {
        V result = putEntry(entry, value);
        didModify(entry, result);
        return result;
      }
    }

    Entry<K, V> entry = newEntry(hash, key, value);
    delegateEList.add(entry);
    return null;
  }

  /**
   * Adds the new entry to the map.
   * @param entry the new entry.
   */
  protected void doPut(Entry<K, V> entry)
  {
    if (entryData == null)
    {
      ++modCount;
      ++size;
    }
    else
    {
      int hash = entry.getHash();
      grow(size + 1);
      int index = indexOf(hash);
      BasicEList<Entry<K, V>> eList = entryData[index];
      if (eList == null)
      {
        eList = entryData[index] = newList();
      }
      eList.add(entry);
      ++size;
      didAdd(entry);
    }
  }

  /*
   * Javadoc copied from source.
   */
  public V removeKey(Object key) 
  {
    ensureEntryDataExists();

    int hash = hashOf(key);
    int index = indexOf(hash);
    Entry<K, V> entry = entryForKey(index, hash, key);
    if (entry != null)
    {
      remove(entry);
      return entry.getValue();
    }
    else
    {
      return null;
    }
  }

  /**
   * Removes the entry from the map.
   * @param entry an entry in the map.
   */
  protected void doRemove(Entry<K, V> entry)
  {
    if (entryData == null)
    {
      ++modCount;
      --size;
    }
    else
    {
      Object key = entry.getKey();
      int hash = entry.getHash();
      int index = indexOf(hash);
      removeEntry(index, entryIndexForKey(index, hash, key));
      didRemove(entry);
    }
  }

  /**
   * Removes the fully indexed entry from the map and returns it's value.
   * @param index the index in the entry data
   * @param entryIndex the index in the list of entries.
   * @return the value of the entry.
   */
  protected V removeEntry(int index, int entryIndex)
  {
    ++modCount;
    --size;

    Entry<K, V> entry = entryData[index].remove(entryIndex);
    return entry.getValue();
  }

  /* 
   * Javadoc copied from interface.
   */
  public void putAll(Map<? extends K, ? extends V> map) 
  {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet())
    {
      put(entry.getKey(), entry.getValue());
    }
  }

  /* 
   * Javadoc copied from interface.
   */
  public void putAll(EMap<? extends K, ? extends V> map) 
  {
    for (Map.Entry<? extends K, ? extends V> entry : map)
    {
      put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Clears the map.
   */
  protected void doClear() 
  {
    if (entryData == null)
    {
      ++modCount;
      size = 0;
      didClear(null);
    }
    else
    {
      ++modCount;
      BasicEList<Entry<K, V>> [] oldEntryData = entryData;
      entryData = null;
      size = 0;
      didClear(oldEntryData);
    }
  }

  /**
   * Increments the modification count.
   */
  protected void doMove(Entry<K, V> entry) 
  {
    ++modCount;
  }

  protected class DelegatingMap implements EMap.InternalMapView<K, V>
  {
    public DelegatingMap()
    {
      super();
    }

    public EMap<K, V> eMap()
    {
      return BasicEMap.this;
    }

    public int size()
    {
      return BasicEMap.this.size();
    }

    public boolean isEmpty()
    {
      return BasicEMap.this.isEmpty();
    }

    public boolean containsKey(Object key)
    {
      return BasicEMap.this.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
      return BasicEMap.this.containsValue(value);
    }

    public V get(Object key)
    {
      return BasicEMap.this.get(key);
    }

    public V put(K key, V value)
    {
      return BasicEMap.this.put(key, value);
    }

    public V remove(Object key)
    {
      return BasicEMap.this.removeKey(key);
    }

    public void putAll(Map<? extends K, ? extends V> map)
    {
      BasicEMap.this.putAll(map);
    }

    public void clear()
    {
      BasicEMap.this.clear();
    }

    public Set<K> keySet()
    {
      return BasicEMap.this.keySet();
    }

    public Collection<V> values()
    {
      return BasicEMap.this.values();
    }

    public Set<Entry<K, V>> entrySet()
    {
      return BasicEMap.this.entrySet();
    }

    @Override
    public boolean equals(Object object)
    {
      return BasicEMap.this.equals(object);
    }

    @Override
    public int hashCode()
    {
      return BasicEMap.this.hashCode();
    }

    @Override
    public String toString()
    {
      return BasicEMap.this.toString();
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public Map<K, V> map()
  {
    if (view == null)
    {
      view = new View<K, V>();
    }
    if (view.map == null)
    {
      view.map = new DelegatingMap();
    }

    return view.map;
  }

  /*
   * Javadoc copied from interface.
   */
  public Set<K> keySet() 
  {
    if (view == null)
    {
      view = new View<K, V>();
    }

    if (view.keySet == null) 
    {
      view.keySet = 
        new AbstractSet<K>() 
        {
          @Override
          public Iterator<K> iterator() 
          {
            return BasicEMap.this.size == 0 ?  ECollections.<K>emptyEList().iterator() : new BasicEMapKeyIterator();
          }

          @Override
          public int size() 
          {
            return BasicEMap.this.size;
          }

          @Override
          public boolean contains(Object key) 
          {
            return BasicEMap.this.containsKey(key);
          }

          @Override
          public boolean remove(Object key) 
          {
            int oldSize = BasicEMap.this.size;
            BasicEMap.this.removeKey(key);
            return BasicEMap.this.size != oldSize;
          }

          @Override
          public void clear() 
          {
            BasicEMap.this.clear();
          }
       };
    }
    return view.keySet;
  }

  /*
   * Javadoc copied from interface.
   */
  public Collection<V> values() 
  {
    if (view == null)
    {
      view = new View<K, V>();
    }
    if (view.values == null) 
    {
      view.values = 
        new AbstractCollection<V>() 
        {
          @Override
          public Iterator<V> iterator() 
          {
            return BasicEMap.this.size == 0 ? ECollections.<V>emptyEList().iterator() : new BasicEMapValueIterator();
          }

          @Override
          public int size() 
          {
            return size;
          }

          @Override
          public boolean contains(Object value) 
          {
            return containsValue(value);
          }

          @Override
          public void clear() 
          {
            BasicEMap.this.clear();
          }
        };
    }
    return view.values;
  }

  /*
   * Javadoc copied from interface.
   */
  public Set<Map.Entry<K, V>> entrySet() 
  {
    if (view == null)
    {
      view = new View<K, V>();
    }
    if (view.entrySet == null) 
    {
      view.entrySet = new AbstractSet<Map.Entry<K, V>>() 
      {
        @Override
        public int size() 
        {
          return BasicEMap.this.size;
        }

        @Override
        public boolean contains(Object object) 
        {
          if (BasicEMap.this.size > 0 && object instanceof Map.Entry<?, ?>)
          {
            BasicEMap.this.ensureEntryDataExists();
            @SuppressWarnings("unchecked") Map.Entry<K, V> otherEntry = (Map.Entry<K, V>)object;
            Object key = otherEntry.getKey();
  
            int hash = key == null ? 0 : key.hashCode();
            int index = BasicEMap.this.indexOf(hash);
            BasicEList<Entry<K, V>> eList = entryData[index];
            if (eList != null)
            {
              @SuppressWarnings("unchecked") Entry<K, V> [] entries = (Entry<K, V> [])eList.data;
              int size = eList.size;
              for (int j = 0; j < size; ++j)
              {
                Entry<K, V> entry = entries[j];
                if (entry.getHash() == hash && entry.equals(otherEntry))
                {
                  return true;
                }
              }
            }
          }
          return false;
        }

        @Override
        public boolean remove(Object object) 
        {
          if (BasicEMap.this.size > 0 && object instanceof Map.Entry<?, ?>)
          {
            BasicEMap.this.ensureEntryDataExists();
            @SuppressWarnings("unchecked") Map.Entry<K, V> otherEntry = (Map.Entry<K, V>)object;
            Object key = otherEntry.getKey();
            int hash = key == null ? 0 : key.hashCode();
            int index = BasicEMap.this.indexOf(hash);
            BasicEList<Entry<K, V>> eList = entryData[index];
            if (eList != null)
            {
              @SuppressWarnings("unchecked") Entry<K, V> [] entries = (Entry<K, V> [])eList.data;
              int size = eList.size;
              for (int j = 0; j < size; ++j)
              {
                Entry<K, V> entry = entries[j];
                if (entry.getHash() == hash && entry.equals(otherEntry)) 
                {
                  // BasicEMap.this.removeEntry(index, j);
                  remove(otherEntry);
                  return true;
                }
              }
            }
          }
          return false;
        }

        @Override
        public void clear() 
        {
          BasicEMap.this.clear();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() 
        {
          return BasicEMap.this.size == 0 ? ECollections.<Map.Entry<K, V>>emptyEList().iterator() : new BasicEMapIterator<Map.Entry<K, V>>();
        }
      };
    }

    return view.entrySet;
  }

  /**
   * A simple and obvious entry implementation.
   */
  protected class EntryImpl implements Entry<K, V>
  {
    /**
     * The cached hash code of the key.
     */
    protected int hash;

    /**
     * The key.
     */
    protected K key;

    /**
     * The value.
     */
    protected V value;
  
    /**
     * Creates a fully initialized instance.
     * @param hash the hash code of the key.
     * @param key the key.
     * @param value the value.
     */
    public EntryImpl(int hash, K key, V value)
    {
      this.hash = hash;
      this.key = key;
      this.value = value;
    }

    public int getHash() 
    {
      return hash;
    }

    public void setHash(int hash) 
    {
      this.hash = hash;
    }

    public K getKey() 
    {
      return key;
    }

    public void setKey(K key) 
    {
      throw new RuntimeException();
    }

    public V getValue() 
    {
      return value;
    }

    public V setValue(V value)
    {
      BasicEMap.this.validateValue(value);

      V oldValue = this.value;
      this.value = value;
      return oldValue;
    }

    @Override
    public boolean equals(Object object) 
    {
      if (object instanceof Map.Entry<?, ?>)
      {
        @SuppressWarnings("unchecked") Map.Entry<K, V> entry = (Map.Entry<K, V>)object;
  
        return 
          (BasicEMap.this.useEqualsForKey() && key != null ? key.equals(entry.getKey()) : key == entry.getKey())  &&
          (BasicEMap.this.useEqualsForValue() && value != null ? value.equals(entry.getValue()) : value == entry.getValue());
      }
      else
      {
        return false;
      }
    }

    @Override
    public int hashCode() 
    {
      return hash ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString() 
    {
      return key + "->" + value;
    }
  }

  /**
   * An iterator over the map entry data.
   */
  protected class BasicEMapIterator<U> implements Iterator<U> 
  {
    /**
     * The cursor in the entry data.
     */
    protected int cursor;

    /**
     * The cursor in the list of entries.
     */
    protected int entryCursor = -1;

    /**
     * The last cursor in the entry data.
     */
    protected int lastCursor;

    /**
     * The cursor in the list of entries.
     */
    protected int lastEntryCursor;

    /**
     * The modification count expected of the map.
     */
    protected int expectedModCount = modCount;

    /**
     * Creates an instance.
     */
    BasicEMapIterator()
    {
      if (BasicEMap.this.size > 0)
      {
        scan();
      }
    }

    /**
     * Called to yield the iterator result for the entry.
     * This implementation returns the entry itself.
     * @param entry the entry.
     * @return the iterator result for the entry.
     */
    @SuppressWarnings("unchecked")
    protected U yield(Entry<K, V> entry)
    {
      return (U)entry;
    }

    /**
     * Scans to the new entry.
     */
    protected void scan()
    {
      BasicEMap.this.ensureEntryDataExists();
      if (entryCursor != -1)
      {
        ++entryCursor;
        BasicEList<Entry<K, V>> eList = BasicEMap.this.entryData[cursor];
        if (entryCursor < eList.size)
        {
          return;
        }
        ++cursor;
      }

      for (; cursor < BasicEMap.this.entryData.length; ++cursor)
      {
        BasicEList<Entry<K, V>> eList = BasicEMap.this.entryData[cursor];
        if (eList != null && !eList.isEmpty())
        {
          entryCursor = 0;
          return;
        }
      }

      entryCursor = -1;
    }

    /**
     * Returns whether there are more objects.
     * @return whether there are more objects.
     */
    public boolean hasNext() 
    {
      return entryCursor != -1;
    }

    /**
     * Returns the next object and advances the iterator.
     * @return the next object.
     * @exception NoSuchElementException if the iterator is done.
     */
    public U next() 
    {
      if (BasicEMap.this.modCount != expectedModCount)
      {
        throw new ConcurrentModificationException();
      }

      if (entryCursor == -1)
      {
        throw new NoSuchElementException();
      }

      lastCursor = cursor;
      lastEntryCursor = entryCursor;

      scan();
      @SuppressWarnings("unchecked") Entry<K, V> result = (Entry<K, V>)BasicEMap.this.entryData[lastCursor].data[lastEntryCursor];
      return yield(result);
    }

    /**
     * Removes the entry of the last object returned by {@link #next()} from the map,
     * it's an optional operation.
     * @exception IllegalStateException
     * if <code>next</code> has not yet been called,
     * or <code>remove</code> has already been called after the last call to <code>next</code>.
     */
    public void remove() 
    {
      if (modCount != expectedModCount)
      {
        throw new ConcurrentModificationException();
      }

      if (lastEntryCursor == -1)
      {
        throw new IllegalStateException();
      }

      delegateEList.remove(entryData[lastCursor].get(lastEntryCursor));

      expectedModCount = BasicEMap.this.modCount;
      lastEntryCursor = -1;
      if (cursor == lastCursor && entryCursor != -1)
      {
        --entryCursor;
      }
    }
  }

  /**
   * An iterator over the map key data.
   */
  protected class BasicEMapKeyIterator extends BasicEMapIterator<K>
  {
    /**
     * Creates an instance.
     */
    BasicEMapKeyIterator()
    {
      super();
    }

    /**
     * Called to yield the iterator result for the entry.
     * This implementation returns the key of the entry.
     * @param entry the entry.
     * @return the key of the entry.
     */
    @Override
    protected K yield(Entry<K, V> entry)
    {
      return entry.getKey();
    }
  }

  /**
   * An iterator over the map value data.
   */
  protected class BasicEMapValueIterator extends BasicEMapIterator<V>
  {
    /**
     * Creates an instance.
     */
    BasicEMapValueIterator()
    {
      super();
    }

    /**
     * Called to yield the iterator result for the entry.
     * This implementation returns the value of the entry.
     * @param entry the entry.
     * @return the value of the entry.
     */
    @Override
    protected V yield(Entry<K, V> entry)
    {
      return entry.getValue();
    }
  }

  /**
   * Called to return the hash code of the key.
   * @param key the key.
   * @return the hash code of the object.
   */
  protected int hashOf(Object key)
  {
    return key == null ? 0 : key.hashCode();
  }

  /**
   * Called to return the entry data index corresponding to the hash code.
   * @param hash the hash code.
   * @return the index corresponding to the hash code.
   */
  protected int indexOf(int hash)
  {
    return (hash & 0x7FFFFFFF) % entryData.length;
  }

  /**
   * Called to return the entry given the index, the hash, and the key.
   * @param index the entry data index of the key.
   * @param hash the hash code of the key.
   * @param key the key.
   * @return the entry.
   */
  protected Entry<K, V> entryForKey(int index, int hash, Object key)
  {
    BasicEList<Entry<K, V>> eList = entryData[index];
    if (eList != null)
    {
      Object [] entries = eList.data;
      int size = eList.size;
      if (useEqualsForKey() && key != null) 
      {
        for (int j = 0; j < size; ++j)
        {
          @SuppressWarnings("unchecked") Entry<K, V> entry = (Entry<K, V>)entries[j];
          if (entry.getHash() == hash && key.equals(entry.getKey())) 
          {
            return entry;
          }
        }
      } 
      else 
      {
        for (int j = 0; j < size; ++j)
        {
          @SuppressWarnings("unchecked") Entry<K, V> entry = (Entry<K, V>)entries[j];
          if (entry.getKey() == key) 
          {
            return entry;
          }
        }
      }
    }

    return null;
  }

  /**
   * Called to return the entry list index given the index, the hash, and the key.
   * @param index the entry data index of the key.
   * @param hash the hash code of the key.
   * @param key the key.
   * @return the entry list index.
   */
  protected int entryIndexForKey(int index, int hash, Object key)
  {
    if (useEqualsForKey() && key != null) 
    {
      BasicEList<Entry<K, V>> eList = entryData[index];
      if (eList != null)
      {
        Object [] entries = eList.data;
        int size = eList.size;
        for (int j = 0; j < size; ++j)
        {
          @SuppressWarnings("unchecked") Entry<K, V> entry = (Entry<K, V>)entries[j];
          if (entry.getHash() == hash && key.equals(entry.getKey())) 
          {
            return j;
          }
        }
      }
    } 
    else 
    {
      BasicEList<Entry<K, V>> eList = entryData[index];
      if (eList != null)
      {
        Object [] entries = eList.data;
        int size = eList.size;
        for (int j = 0; j < size; ++j)
        {
          @SuppressWarnings("unchecked") Entry<K, V> entry = (Entry<K, V>)entries[j];
          if (entry.getKey() == key) 
          {
            return j;
          }
        }
      }
    }

    return -1;
  }

  /**
   * Grows the capacity of the map
   * to ensure that no additional growth is needed until the size exceeds the specified minimum capacity.
   */
  protected boolean grow(int minimumCapacity) 
  {
    ++modCount;
    int oldCapacity = entryData == null ? 0 : entryData.length;
    if (minimumCapacity > oldCapacity)
    {
      BasicEList<Entry<K, V>> [] oldEntryData = entryData;
      entryData = newEntryData(2 * oldCapacity + 4);

      for (int i = 0; i < oldCapacity; ++i)
      {
        BasicEList<Entry<K, V>> oldEList = oldEntryData[i];
        if (oldEList != null)
        {
          Object [] entries = oldEList.data;
          int size = oldEList.size;
          for (int j = 0; j < size; ++j)
          {
            @SuppressWarnings("unchecked") Entry<K, V> entry = (Entry<K, V>)entries[j];
            int index = indexOf(entry.getHash());
            BasicEList<Entry<K, V>> eList = entryData[index];
            if (eList == null)
            {
              eList = entryData[index] = newList();
            }
            eList.add(entry);
          }
        }
      }

      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public boolean contains(Object object)
  {
    return delegateEList.contains(object);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public boolean containsAll(Collection<?> collection)
  {
    return delegateEList.containsAll(collection);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public int indexOf(Object object)
  {
    return delegateEList.indexOf(object);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public int lastIndexOf(Object object)
  {
    return delegateEList.lastIndexOf(object);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public Object[] toArray()
  {
    return delegateEList.toArray();
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public <T> T[] toArray(T [] array)
  {
    return delegateEList.toArray(array);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public Entry<K, V> get(int index)
  {
    return delegateEList.get(index);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public Map.Entry<K, V> set(int index, Map.Entry<K, V> object)
  {
    return delegateEList.set(index, (Entry<K, V>)object);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public boolean add(Map.Entry<K, V> object)
  {
    return delegateEList.add((Entry<K, V>)object);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public void add(int index, Map.Entry<K, V> object)
  {
    delegateEList.add(index, (Entry<K, V>)object);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  @SuppressWarnings("unchecked")
  public boolean addAll(Collection<? extends Map.Entry<K, V>> collection)
  {
    return delegateEList.addAll((Collection<? extends Entry<K, V>>)collection);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  @SuppressWarnings("unchecked")
  public boolean addAll(int index, Collection<? extends Map.Entry<K, V>> collection)
  {
    return delegateEList.addAll(index, (Collection<? extends Entry<K, V>>)collection);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public boolean remove(Object object)
  {
    if (object instanceof Map.Entry<?, ?>)
    {
      return delegateEList.remove(object);
    }
    else
    {
      boolean result = containsKey(object);
      removeKey(object);
      return result;
    }
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public boolean removeAll(Collection<?> collection)
  {
    return delegateEList.removeAll(collection);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public Map.Entry<K, V> remove(int index)
  {
    return delegateEList.remove(index);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public boolean retainAll(Collection<?> collection)
  {
    return delegateEList.retainAll(collection);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public void clear()
  {
    delegateEList.clear();
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public void move(int index, Map.Entry<K, V> object)
  {
    delegateEList.move(index, (Entry<K, V>)object);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  public Map.Entry<K, V> move(int targetIndex, int sourceIndex)
  {
    return delegateEList.move(targetIndex, sourceIndex);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  @SuppressWarnings("unchecked")
  public Iterator<Map.Entry<K, V>> iterator()
  {
    return (Iterator<Map.Entry<K, V>>)(Iterator<?>)delegateEList.iterator();
  }
  
  /**
   * Delegates to {@link #delegateEList}.
   */
  @SuppressWarnings("unchecked")
  public ListIterator<Map.Entry<K, V>> listIterator()
  {
    return (ListIterator<Map.Entry<K, V>>)(ListIterator<?>)(delegateEList.listIterator());
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  @SuppressWarnings("unchecked")
  public ListIterator<Map.Entry<K, V>> listIterator(int index)
  {
    return (ListIterator<Map.Entry<K, V>>)(ListIterator<?>)delegateEList.listIterator(index);
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  @SuppressWarnings("unchecked")
  public List<Map.Entry<K, V>> subList(int start, int end)
  {
    return (List<Map.Entry<K, V>>)(List<?>)delegateEList.subList(start, end);
  }

  @Override
  public int hashCode()
  {
    return delegateEList.hashCode();
  }

  @Override
  public boolean equals(Object object)
  {
    if (object instanceof List<?>)
    {
      return delegateEList.equals(object);
    }
    else
    {
      return false;
    }
  }

  /**
   * Delegates to {@link #delegateEList}.
   */
  @Override
  public String toString()
  {
    return delegateEList.toString();
  }
}
