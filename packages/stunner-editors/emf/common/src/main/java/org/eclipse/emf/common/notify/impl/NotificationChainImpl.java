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


import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;


/**
 * A list that acts as a notification chain.
 */
public class NotificationChainImpl extends BasicEList<Notification> implements NotificationChain
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates an empty instance.
   */
  public NotificationChainImpl()
  {
    super();
  }

  /**
   * Creates an empty instance with a given capacity.
   * @param initialCapacity the initial capacity of the list before it must grow.
   */
  public NotificationChainImpl(int initialCapacity)
  {
    super(initialCapacity);
  }

  /**
   * Returns new data storage of type {@link Notification}[].
   * @return new data storage.
   */
  @Override
  protected Object [] newData(int capacity)
  {
    return new Notification [capacity];
  }

  /**
   * Adds or merges a new notification.
   * @param newNotification a notification.
   * @return <code>true</code> when the notification is added and <code>false</code> when it is merged.
   */
  @Override
  public boolean add(Notification newNotification)
  {
    if (newNotification == null)
    {
      return false;
    }
    else 
    {
      for (int i = 0; i < size; ++i)
      {
        Notification notification = (Notification)data[i];
        if (notification.merge(newNotification))
        {
          return false;
        }
      }

      return super.add(newNotification);
    }
  }

  public void dispatch()
  {
    for (int i = 0; i < size; ++i)
    {
      Notification notification = (Notification)data[i];
      dispatch(notification);
    }
  }

  /**
   * Dispatches the notification to its notifier.
   */
  protected void dispatch(Notification notification)
  {
    Object notifier = notification.getNotifier();
    if (notifier != null && notification.getEventType() != -1)
    {
      ((Notifier)notifier).eNotify(notification);
    }
  }
}
