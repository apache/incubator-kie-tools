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
package org.eclipse.emf.common.notify.impl;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.NotifyingList;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.DelegatingEList;


/**
 * An extensible implementation of a notifying list that delegates to a backing list.
 */
public abstract class DelegatingNotifyingListImpl<E> extends DelegatingEList<E> implements NotifyingList<E>
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates an empty instance.
   */
  public DelegatingNotifyingListImpl()
  {
    super();
  }

  /**
   * Creates an instance that is a copy of the collection.
   * @param collection the initial contents of the list.
   */
  public DelegatingNotifyingListImpl(Collection<? extends E> collection)
  {
    super(collection);
  }

  /**
   * Returns <code>null</code>.
   * @return <code>null</code>.
   */
  public Object getNotifier()
  {
    return null;
  }

  /**
   * Returns <code>null</code>.
   * @return <code>null</code>.
   */
  public Object getFeature()
  {
    return null;
  }

  /**
   * Returns {@link Notification#NO_FEATURE_ID}.
   * @return <code>Notification.NO_FEATURE_ID</code>.
   */
  public int getFeatureID()
  {
    return Notification.NO_FEATURE_ID;
  }

  /**
   * Returns the result of calling {@link #getFeatureID()}.
   * @param expectedClass the class to which the ID is relative.
   * @return <code>getFeatureID()</code>.
   */
  protected int getFeatureID(Class<?> expectedClass)
  {
    return getFeatureID();
  }

  /**
   * Returns whether the list is considered set, i.e., whether it's not empty.
   * A derived implementation may model this state directly.
   * @return whether the list is considered set.
   */
  protected boolean isSet()
  {
    return !isEmpty();
  }

  /**
   * Returns <code>false</code>.
   * @return <code>false</code>.
   */
  protected boolean hasInverse()
  {
    return false;
  }

  /**
   * Returns <code>!{@link #hasInverse()}</code>.
   * @return <code>!hasInverse</code>.
   */
  @Override
  protected boolean canContainNull()
  {
    return !hasInverse();
  }

  /**
   * Returns <code>false</code>.
   * @return <code>false</code>.
   */
  protected boolean isNotificationRequired()
  {
    return false;
  }

  /**
   * Returns <code>false</code>.
   * @return <code>false</code>.
   */
  protected boolean hasShadow()
  {
    return false;
  }

  /**
   * Does nothing and returns the <code>notifications</code>.
   * Clients can override this to update the inverse of a bidirectional relation.
   * @param object the object that's been added to the list.
   * @param notifications the chain of accumulating notifications.
   * @return the <code>notifications</code>.
   */
  protected NotificationChain shadowAdd(E object, NotificationChain notifications)
  {
    return notifications;
  }

  /**
   * Does nothing and returns the <code>notifications</code>.
   * Clients can override this to update the inverse of a bidirectional relation.
   * @param object the object that's been remove from the list.
   * @param notifications the chain of accumulating notifications.
   * @return the <code>notifications</code>.
   */
  protected NotificationChain shadowRemove(E object, NotificationChain notifications)
  {
    return notifications;
  }

  /**
   * Does nothing and returns the <code>notifications</code>.
   * Clients can override this to update the inverse of a bidirectional relation.
   * @param oldObject the object that's been removed from the list.
   * @param newObject the object that's been added to the list.
   * @param notifications the chain of accumulating notifications.
   * @return the <code>notifications</code>.
   */
  protected NotificationChain shadowSet(E oldObject, E newObject, NotificationChain notifications)
  {
    return notifications;
  }

  /**
   * Does nothing and returns the <code>notifications</code>.
   * Clients can override this to update the inverse of a bidirectional relation.
   * @param object the object that's been added to the list.
   * @param notifications the chain of accumulating notifications.
   * @return the <code>notifications</code>.
   */
  protected NotificationChain inverseAdd(E object, NotificationChain notifications)
  {
    return notifications;
  }

  /**
   * Does nothing and returns the <code>notifications</code>.
   * Clients can override this to update the inverse of a bidirectional relation.
   * @param object the object that's been remove from the list.
   * @param notifications the chain of accumulating notifications.
   * @return the <code>notifications</code>.
   */
  protected NotificationChain inverseRemove(E object, NotificationChain notifications)
  {
    return notifications;
  }

  /*
   * @deprecated
   */
  protected NotificationImpl createNotification(int eventType, Object oldObject, Object newObject, int index)
  {
    throw new UnsupportedOperationException("Please change your code to call new five argument version of this method");
  }

  /**
   * Creates a notification.
   * @param eventType the type of change that has occurred.
   * @param oldObject the value of the notifier's feature before the change occurred.
   * @param newObject the value of the notifier's feature after the change occurred.
   * @param index the position at which the change occurred.
   * @return a new notification.
   */
  protected NotificationImpl createNotification(int eventType, Object oldObject, Object newObject, int index, boolean wasSet)
  {
    return 
      new NotificationImpl(eventType, oldObject, newObject, index, wasSet)
      {
        @Override
        public Object getNotifier()
        {
          return DelegatingNotifyingListImpl.this.getNotifier();
        }

        @Override
        public Object getFeature()
        {
          return DelegatingNotifyingListImpl.this.getFeature();
        }

        @Override
        public int getFeatureID(Class<?> expectedClass)
        {
          return DelegatingNotifyingListImpl.this.getFeatureID(expectedClass);
        }
      };
  }


  /**
   * Creates a notification chain, if the expected capacity exceeds the threshold 
   * at which a list is better than chaining individual notification instances.
   */
  protected NotificationChain createNotificationChain(int capacity)
  {
    return capacity < 100 ? null: new NotificationChainImpl(capacity);
  }

  /**
   * Dispatches a notification to the notifier of the list.
   * @param notification the notification to dispatch.
   */
  protected void dispatchNotification(Notification notification)
  {
    ((Notifier)getNotifier()).eNotify(notification);
  }

  /**
   * Adds the object at the end of the list;
   * it does no uniqueness checking.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseAdd inverseAdd} as {@link #hasInverse required}.
   * @param object the object to be added.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseAdd
   */
  @Override
  public void addUnique(E object)
  {
    if (isNotificationRequired())
    {
      int index = size();
      boolean oldIsSet = isSet();
      doAddUnique(index, object);
      NotificationImpl notification = createNotification(Notification.ADD, null, object, index, oldIsSet);
      if (hasInverse())
      {
        NotificationChain notifications = inverseAdd(object, null);
        if (hasShadow())
        {
          notifications = shadowAdd(object, notifications);
        }
        if (notifications == null)
        {
          dispatchNotification(notification);
        }
        else
        {
          notifications.add(notification);
          notifications.dispatch();
        }
      }
      else
      {
        dispatchNotification(notification);
      }
    }
    else
    {
      doAddUnique(object);
      if (hasInverse())
      {
        NotificationChain notifications = inverseAdd(object, null);
        if (notifications != null) notifications.dispatch();
      }
    }
  }

  /**
   * Adds the object at the end of the list;
   * it does no uniqueness checking, inverse updating, or notification.
   * @param object the object to be added.
   */
  protected void doAddUnique(E object)
  {
    super.addUnique(object);
  }

  /**
   * Adds the object at the given index in the list;
   * it does no ranging checking or uniqueness checking.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseAdd inverseAdd} as {@link #hasInverse required}.
   * @param object the object to be added.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseAdd
   */
  @Override
  public void addUnique(int index, E object)
  {
    if (isNotificationRequired())
    {
      boolean oldIsSet = isSet();
      doAddUnique(index, object);
      NotificationImpl notification = createNotification(Notification.ADD, null, object, index, oldIsSet);
      if (hasInverse())
      {
        NotificationChain notifications = inverseAdd(object, null);
        if (hasShadow())
        {
          notifications = shadowAdd(object, notifications);
        }
        if (notifications == null)
        {
          dispatchNotification(notification);
        }
        else
        {
          notifications.add(notification);
          notifications.dispatch();
        }
      }
      else
      {
        dispatchNotification(notification);
      }
    }
    else
    {
      doAddUnique(index, object);
      if (hasInverse())
      {
        NotificationChain notifications = inverseAdd(object, null);
        if (notifications != null) notifications.dispatch();
      }
    }
  }

  /**
   * Adds the object at the given index in the list;
   * it does no range checking, uniqueness checking, inverse updating, or notification.
   * @param object the object to be added.
   */
  protected void doAddUnique(int index, E object)
  {
    super.addUnique(index, object);
  }

  /**
   * Adds each object of the collection to the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #addAllUnique(int, Collection) addAllUnique(int, Collection)}.
   * @param collection the collection of objects to be added.
   * @see #inverseAdd
   */
  @Override
  public boolean addAllUnique(Collection<? extends E> collection)
  {
    return addAllUnique(size(), collection);
  }

  /**
   * Adds each object of the collection to the end of the list;
   * it does no uniqueness checking, inverse updating, or notification.
   * @param collection the collection of objects to be added.
   */
  protected boolean doAddAllUnique(Collection<? extends E> collection)
  {
    return super.addAllUnique(collection);
  }

  /**
   * Adds each object of the collection at each successive index in the list
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseAdd inverseAdd} as {@link #hasInverse required}.
   * @param index the index at which to add.
   * @param collection the collection of objects to be added.
   * @return whether any objects were added.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseAdd
   */
  @Override
  public boolean addAllUnique(int index, Collection<? extends E> collection)
  {
    int collectionSize = collection.size();
    if (collectionSize == 0)
    {
      return false;
    }
    else
    {
      if (isNotificationRequired())
      {
        boolean oldIsSet = isSet();
        doAddAllUnique(index, collection);
        NotificationImpl notification =
          collectionSize == 1 ?
            createNotification(Notification.ADD, null, collection.iterator().next(), index, oldIsSet) :
            createNotification(Notification.ADD_MANY, null, collection, index, oldIsSet);
        if (hasInverse())
        {
          NotificationChain notifications = createNotificationChain(collectionSize);
          int lastIndex = index + collectionSize;
          for (int i = index; i < lastIndex; ++i)
          {            
            E value = delegateGet(i);
            notifications = inverseAdd(value, notifications);
            notifications = shadowAdd(value, notifications);
          }
          if (notifications == null)
          {
            dispatchNotification(notification);
          }
          else
          {
            notifications.add(notification);
            notifications.dispatch();
          }
        }
        else
        {
          dispatchNotification(notification);
        }
      }
      else
      {
        doAddAllUnique(index, collection);
        if (hasInverse())
        {
          NotificationChain notifications = createNotificationChain(collectionSize);
          int lastIndex = index + collectionSize;
          for (int i = index; i < lastIndex; ++i)
          {            
            notifications = inverseAdd(delegateGet(i), notifications);
          }
          if (notifications != null) notifications.dispatch();
        }
      }

      return true;
    }
  }

  /**
   * Adds each object of the collection at each successive index in the list
   * and returns whether any objects were added;
   * it does no range checking, uniqueness checking, inverse updating, or notification.
   * @param index the index at which to add.
   * @param collection the collection of objects to be added.
   * @return whether any objects were added.
   */
  protected boolean doAddAllUnique(int index, Collection<? extends E> collection)
  {
    return super.addAllUnique(index, collection);
  }

  /**
   * Adds each object from start to end of the array to the end of the list;
   * it does no uniqueness checking.
   * This implementation delegates to {@link #addAllUnique(int, Object[], int, int) addAllUnique(int, Object[], int, int)}.
   * @param objects the objects to be added.
   * @param start the index of first object to be added.
   * @param end the index past the last object to be added.
   * @see #inverseAdd
   */
  @Override
  public boolean addAllUnique(Object [] objects, int start, int end)
  {
    return addAllUnique(size(), objects, start, end);
  }

  /**
   * Adds each object from start to end of the array to the end of the list
   * and returns whether any objects were added;
   * it does no ranging checking, uniqueness checking, inverse updating, or notification.
   * @param objects the objects to be added.
   * @param start the index of first object to be added.
   * @param end the index past the last object to be added.
   * @return whether any objects were added.
   */
  protected boolean doAddAllUnique(Object [] objects, int start, int end)
  {
    return super.addAllUnique(objects, start, end);
  }

  /**
   * Adds each object from start to end of the array at each successive index in the list 
   * and returns whether any objects were added;
   * it does no ranging checking or uniqueness checking.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseAdd inverseAdd} as {@link #hasInverse required}.
   * @param index the index at which to add.
   * @param objects the objects to be added.
   * @param start the index of first object to be added.
   * @param end the index past the last object to be added.
   * @return whether any objects were added.
   * @see #addAllUnique(int, Collection)
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseAdd
   */
  @Override
  public boolean addAllUnique(int index, Object [] objects, int start, int end)
  {
    int collectionSize = end - start;
    if (collectionSize == 0)
    {
      return false;
    }
    else
    {
      if (isNotificationRequired())
      {
        boolean oldIsSet = isSet();
        doAddAllUnique(index, objects, start, end);
        NotificationImpl notification;
        if (collectionSize == 1)
        {
          notification = createNotification(Notification.ADD, null, objects[0], index, oldIsSet);
        }
        else
        {
          if (start != 0 || end != objects.length)
          {
            Object [] actualObjects = new Object [collectionSize];
            for (int i = 0, j = start; j < end; ++i, ++j)
            {
              actualObjects[i] = objects[j];
            }
            notification = createNotification(Notification.ADD_MANY, null, Arrays.asList(actualObjects), index, oldIsSet);
          }
          else
          {
            notification =  createNotification(Notification.ADD_MANY, null, Arrays.asList(objects), index, oldIsSet);
          }
        }
        if (hasInverse())
        {
          NotificationChain notifications = createNotificationChain(collectionSize);
          int lastIndex = index + collectionSize;
          for (int i = index; i < lastIndex; ++i)
          {            
            E value = delegateGet(i);
            notifications = inverseAdd(value, notifications);
            notifications = shadowAdd(value, notifications);
          }
          if (notifications == null)
          {
            dispatchNotification(notification);
          }
          else
          {
            notifications.add(notification);
            notifications.dispatch();
          }
        }
        else
        {
          dispatchNotification(notification);
        }
      }
      else
      {
        doAddAllUnique(index, objects, start, end);
        if (hasInverse())
        {
          NotificationChain notifications = createNotificationChain(collectionSize);
          int lastIndex = index + collectionSize;
          for (int i = index; i < lastIndex; ++i)
          {            
            notifications = inverseAdd(delegateGet(i), notifications);
          }
          if (notifications != null) notifications.dispatch();
        }
      }

      return true;
    }
  }

  /**
   * Adds each object from start to end of the array at each successive index in the list 
   * and returns whether any objects were added;
   * it does no ranging checking, uniqueness checking, inverse updating, or notification.
   * @param index the index at which to add.
   * @param objects the objects to be added.
   * @param start the index of first object to be added.
   * @param end the index past the last object to be added.
   * @return whether any objects were added.
   */
  protected boolean doAddAllUnique(int index, Object [] objects, int start, int end)
  {
    return super.addAllUnique(index, objects, start, end);
  }

  /**
   * Adds the object at the end of the list and returns the potentially updated notification chain;
   * it does no {@link #inverseAdd inverse} updating.
   * This implementation generates notifications as {@link #isNotificationRequired required}.
   * @param object the object to be added.
   * @return the notification chain.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseAdd
   */
  public NotificationChain basicAdd(E object, NotificationChain notifications)
  {
    if (isNotificationRequired())
    {
      int index = size();
      boolean oldIsSet = isSet();
      doAddUnique(index, object);
      NotificationImpl notification = createNotification(Notification.ADD, null, object, index, oldIsSet);
      if (notifications == null)
      {
        notifications = notification;
      }
      else
      {
        notifications.add(notification);
      }
    }
    else
    {
      doAddUnique(size(), object);
    }
    return notifications;
  }

  /**
   * Removes the object at the index from the list and returns it.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseRemove inverseRemove} as {@link #hasInverse required}.
   * @param index the position of the object to remove.
   * @return the removed object.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseRemove
   */
  @Override
  public E remove(int index)
  {
    if (isNotificationRequired())
    {
      NotificationChain notifications = null;
      boolean oldIsSet = isSet();
      if (hasShadow())
      {
        notifications = shadowRemove(basicGet(index), null);
      }
      E oldObject;
      NotificationImpl notification = createNotification(Notification.REMOVE, oldObject = doRemove(index), null, index, oldIsSet);
      if (hasInverse() && oldObject != null)
      {
        notifications = inverseRemove(oldObject, notifications);
        if (notifications == null)
        {
          dispatchNotification(notification);
        }
        else
        {
          notifications.add(notification);
          notifications.dispatch();
        }
      }
      else
      {
        if (notifications == null)
        {
          dispatchNotification(notification);
        }
        else
        {
          notifications.add(notification);
          notifications.dispatch();
        }
      }
      return oldObject;
    }
    else
    {
      E oldObject = doRemove(index);
      if (hasInverse() && oldObject != null)
      {
        NotificationChain notifications = inverseRemove(oldObject, null);
        if (notifications != null) notifications.dispatch();
      }
      return oldObject;
    }
  }

  /**
   * Removes the object at the index from the list and returns it;
   * it does no inverse updating, or notification.
   * @param index the position of the object to remove.
   * @return the removed object.
   * @exception IndexOutOfBoundsException if the index isn't within the size range.
   */
  protected E doRemove(int index)
  {
    return super.remove(index);
  }

  /**
   * Removes each object of the collection from the list and returns whether any object was actually contained by the list.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseRemove inverseRemove} as {@link #hasInverse required}.
   * @param collection the collection of objects to be removed.
   * @return whether any object was actually contained by the list.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseRemove
   */
  @Override
  public boolean removeAll(Collection<?> collection)
  {
    boolean oldIsSet = isSet();

    boolean result = false;
    int [] positions = null;
    if (isNotificationRequired())
    {
      int listSize = collection.size();
      if (listSize > 0)
      {
        NotificationChain notifications = createNotificationChain(listSize);

        // Copy to a list and allocate positions.
        //
        BasicEList<Object> list = new BasicEList<Object>(collection);
        Object [] objects = list.data();
        positions = new int [listSize];
        int count = 0;

        if (isUnique())
        {
          // Count up the objects that will be removed.
          // The objects are exchanged to produce this list's order
          //
          for (ListIterator<E> i = delegateListIterator(); i.hasNext(); )
          {
            E initialObject = i.next();
            E object = initialObject;
            LOOP:
            for (int repeat = 0; repeat < 2; ++repeat)
            {
              for (int j = listSize; --j >= 0; )
              {
                if (equalObjects(object, objects[j]))
                {
                  if (count != j)
                  {
                    Object x = objects[count];
                    objects[count] = objects[j];
                    objects[j] = x;
                  }
                  positions[count++] = i.previousIndex();
                  break LOOP;
                }
              }
              object = resolve(object);
              if (object == initialObject)
              {
                break;
              }
            }
          }
        }
        else
        {
          BasicEList<Object> resultList = new BasicEList<Object>(listSize);
          
          // Count up the objects that will be removed.
          // The objects are exchanged to produce this list's order
          //
          for (ListIterator<E> i = delegateListIterator(); i.hasNext(); )
          {
            E initialObject = i.next();
            E object = initialObject;
            LOOP:
            for (int repeat = 0; repeat < 2; ++repeat)
            {
              for (int j = listSize; --j >= 0; )
              {
                if (equalObjects(object, objects[j]))
                {
                  if (positions.length <= count)
                  {
                    int [] oldPositions = positions;
                    positions = new int [2 * positions.length];
                    System.arraycopy(oldPositions, 0, positions, 0, count);
                  }
                  positions[count++] = i.previousIndex();
                  resultList.add(objects[j]);
                  break LOOP;
                }
              }
              object = resolve(object);
              if (object == initialObject)
              {
                break;
              }
            }
          }
          
          list = resultList;
          objects = resultList.data();
          listSize = count;
          
          if (count > positions.length)
          {
            int [] oldPositions = positions;
            positions = new int [count];
            System.arraycopy(oldPositions, 0, positions, 0, count);
          }
        }

        // If any objects are matched.
        //
        if (count > 0)
        {
          result = true;

          if (hasShadow())
          {
            // Remove from by position in reverse order.
            //
            for (int i = 0; i < count; ++i)
            {
              @SuppressWarnings("unchecked") E object = (E)objects[i];
              notifications = shadowRemove(object, notifications);
            }
          }

          // Remove from by position in reverse order.
          //
          for (int i = count; --i >= 0;)
          {
            doRemove(positions[i]);
          }

          // Compact the results to remove unmatched objects
          //
          if (count != listSize)
          {
            for (int i = listSize; --i >= count; )
            {
              list.remove(i);
            }
            int [] oldPositions = positions;
            positions = new int [count];
            System.arraycopy(oldPositions, 0, positions, 0, count);
          }

          collection = list;
        }
      }
    }
    else
    {
      collection = getDuplicates(collection);

      for (int i = delegateSize(); --i >=0; )
      {
        if (collection.contains(delegateGet(i)))
        {
          doRemove(i);
          result = true;
        }
      }
    }

    if (result)
    {
      if (positions != null)
      {
        int collectionSize = collection.size();
        NotificationImpl notification =
          (collectionSize == 1 ?
            createNotification(Notification.REMOVE, collection.iterator().next(), null, positions[0], oldIsSet) :
            createNotification(Notification.REMOVE_MANY, collection, positions, positions[0], oldIsSet));

        NotificationChain notifications = createNotificationChain(collectionSize);
        if (hasInverse())
        {
          for (Iterator<?> i = collection.iterator(); i.hasNext(); )
          {
            @SuppressWarnings("unchecked") E object = (E)i.next();
            notifications = inverseRemove(object, notifications);
          }
          if (notifications == null)
          {
            dispatchNotification(notification);
          }
          else
          {
            notifications.add(notification);
            notifications.dispatch();
          }
        }
        else
        {
          if (notifications == null)
          {
            dispatchNotification(notification);
          }
          else
          {
            notifications.add(notification);
            notifications.dispatch();
          }
        }
      }
      else if (hasInverse())
      {
        NotificationChain notifications = createNotificationChain(collection.size());
        for (Iterator<?> i = collection.iterator(); i.hasNext(); )
        {
          @SuppressWarnings("unchecked") E object = (E)i.next();
          notifications = inverseRemove(object, notifications);
        }
        if (notifications != null) notifications.dispatch();
      }
      return true;
    }
    else
    {
      return false;
    }
  }
  
  /**
   * Returns the resolved object from this list for the purpose of testing whether {@link #removeAll(Collection)} applies to it.
   * @param object the object to be resolved.
   * @return the resolved object from this list for the purpose of testing whether removeAll applies to it.
   */
  protected E resolve(E object)
  {
    return object;
  }

  /**
   * Removes each object of the collection from the list and returns whether any object was actually contained by the list;
   * it does no inverse updating, or notification.
   * @param collection the collection of objects to be removed.
   * @return whether any object was actually contained by the list.
   */
  protected boolean doRemoveAll(Collection<?> collection)
  {
    return super.removeAll(collection);
  }

  /**
   * Removes the object from the list and returns the potentially updated notification chain;
   * it does no {@link #inverseRemove inverse} updating.
   * This implementation generates notifications as {@link #isNotificationRequired required}.
   * @param object the object to be removed.
   * @return the notification chain.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseRemove
   */
  public NotificationChain basicRemove(Object object, NotificationChain notifications)
  {
    int index = indexOf(object);
    if (index != -1)
    {
      if (isNotificationRequired())
      {
        boolean oldIsSet = isSet();
        Object oldObject = doRemove(index);
        NotificationImpl notification = createNotification(Notification.REMOVE, oldObject, null, index, oldIsSet);
        if (notifications == null) 
        {
          notifications = notification;
        }
        else
        {
          notifications.add(notification);
        }
      }
      else
      {
        doRemove(index);
      }
    }
    return notifications;
  }

  /**
   * Clears the list of all objects.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseRemove inverseRemove} as {@link #hasInverse required}.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseRemove
   */
  @Override
  public void clear()
  {
    if (isNotificationRequired())
    {
      int size = size();
      boolean oldIsSet = isSet();
      if (size > 0)
      {
        BasicEList<E> collection = new BasicEList<E>(basicList());
        int collectionSize = size;

        NotificationChain notifications = createNotificationChain(collectionSize);
        if (hasShadow())
        {
          for (int i = 0; i < size; ++i)
          {
            notifications = shadowRemove(collection.get(i), notifications);
          }
        }

        doClear(collectionSize, collection.data());
        Notification notification =
          (collectionSize == 1 ?
            createNotification(Notification.REMOVE, collection.get(0), null, 0, oldIsSet) :
            createNotification(Notification.REMOVE_MANY, collection, null, Notification.NO_INDEX, oldIsSet));

        if (hasInverse())
        {
          for (Iterator<E> i = collection.iterator(); i.hasNext(); )
          {
            notifications = inverseRemove(i.next(), notifications);
          }
          if (notifications == null)
          {
            dispatchNotification(notification);
          }
          else
          {
            notifications.add(notification);
            notifications.dispatch();
          }
        }
        else
        {
          if (notifications == null)
          {
            dispatchNotification(notification);
          }
          else
          {
            notifications.add(notification);
            notifications.dispatch();
          }
        }
      }
      else
      {
        doClear();
        dispatchNotification(createNotification(Notification.REMOVE_MANY, Collections.EMPTY_LIST, null, Notification.NO_INDEX, oldIsSet));
      }
    }
    else if (hasInverse())
    {
      int size = size();
      if (size > 0)
      {
        Object [] oldData = delegateToArray();
        int oldSize = size;
        doClear(size, oldData);
        NotificationChain notifications = createNotificationChain(oldSize);
        for (int i = 0; i < oldSize; ++i)
        {
          @SuppressWarnings("unchecked") E object = (E)oldData[i];
          notifications = inverseRemove(object, notifications);
        }
        if (notifications != null) notifications.dispatch();
      }
      else 
      {
        doClear();
      }
    }
    else
    {
      doClear();
    }
  }

  /**
   * Clears the list of all objects;
   * it does no {@link #inverseRemove inverse} updating.
   */
  protected void doClear()
  {
    super.clear();
  }

  /**
   * Sets the object at the index
   * and returns the old object at the index;
   * it does no ranging checking or uniqueness checking.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required} 
   * and delegates to {@link #inverseAdd inverseAdd} and {@link #inverseRemove inverseRemove} as {@link #hasInverse required}.
   * @param index the position in question.
   * @param object the object to set.
   * @return the old object at the index.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseAdd
   * @see #inverseRemove
   */
  @Override
  public E setUnique(int index, E object)
  {
    if (isNotificationRequired())
    {
      NotificationChain notifications = null;
      boolean oldIsSet = isSet();
      E oldObject;
      Notification notification = createNotification(Notification.SET, oldObject = doSetUnique(index, object), object, index, oldIsSet);
      if (hasInverse() && !equalObjects(oldObject, object))
      {
        if (oldObject != null)
        {
          notifications = inverseRemove(oldObject, notifications);
        }
        notifications = inverseAdd(object, notifications);

        if (hasShadow())
        {
          notifications = shadowSet(oldObject, object, notifications);
        }

        if (notifications == null)
        {
          dispatchNotification(notification);
        }
        else
        {
          notifications.add(notification);
          notifications.dispatch();
        }
      }
      else
      {
        if (hasShadow())
        {
          notifications = shadowSet(oldObject, object, notifications);
        }

        if (notifications == null)
        {
          dispatchNotification(notification);
        }
        else
        {
          notifications.add(notification);
          notifications.dispatch();
        }
      }
      return oldObject;
    }
    else
    {
      E oldObject = doSetUnique(index, object);
      if (hasInverse() && !equalObjects(oldObject, object))
      {
        NotificationChain notifications = null;
        if (oldObject != null)
        {
          notifications = inverseRemove(oldObject, null);
        }
        notifications = inverseAdd(object, notifications);
        if (notifications != null) notifications.dispatch();
      }
      return oldObject;
    }
  }

  /**
   * Sets the object at the index
   * and returns the old object at the index;
   * it does no ranging checking, uniqueness checking, inverse updating or notification.
   * @param index the position in question.
   * @param object the object to set.
   * @return the old object at the index.
   */
  protected E doSetUnique(int index, E object)
  {
    return super.setUnique(index, object);
  }

  /**
   * Sets the object at the index
   * and returns the potentially updated notification chain;
   * it does no {@link #hasInverse inverse} updating.
   * This implementation generates notifications as {@link #isNotificationRequired required}.
   * @param index the position in question.
   * @param object the object to set.
   * @return the notification chain.
   * @see #isNotificationRequired
   * @see #hasInverse
   * @see #inverseAdd
   * @see #inverseRemove
   */
  public NotificationChain basicSet(int index, E object, NotificationChain notifications)
  {
    if (isNotificationRequired())
    {
      boolean oldIsSet = isSet();
      NotificationImpl notification = 
        createNotification(Notification.SET, doSetUnique(index, object), object, index, oldIsSet);
      if (notifications == null) 
      {
        notifications = notification;
      }
      else
      {
        notifications.add(notification);
      }
    }
    else
    {
      doSetUnique(index, object);
    }
    return notifications;
  }

  /**
   * Moves the object at the source index of the list to the target index of the list
   * and returns the moved object.
   * In addition to the normal effects, 
   * this override implementation generates notifications as {@link #isNotificationRequired required}.
   * @param targetIndex the new position for the object in the list.
   * @param sourceIndex the old position of the object in the list.
   * @return the moved object.
   * @exception IndexOutOfBoundsException if either index isn't within the size range.
   * @see #isNotificationRequired
   */
  @Override
  public E move(int targetIndex, int sourceIndex)
  {
    if (isNotificationRequired())
    {
      boolean oldIsSet = isSet();
      E object = doMove(targetIndex, sourceIndex);
      dispatchNotification
        (createNotification
           (Notification.MOVE, 
            sourceIndex,
            object, 
            targetIndex,
            oldIsSet));
      return object;
    }
    else
    {
      return doMove(targetIndex, sourceIndex);
    }
  }

  /**
   * Moves the object at the source index of the list to the target index of the list
   * and returns the moved object;
   * it does no notification.
   * @param targetIndex the new position for the object in the list.
   * @param sourceIndex the old position of the object in the list.
   * @return the moved object.
   * @exception IndexOutOfBoundsException if either index isn't within the size range.
   */
  protected E doMove(int targetIndex, int sourceIndex)
  {
    return super.move(targetIndex, sourceIndex);
  }
}
