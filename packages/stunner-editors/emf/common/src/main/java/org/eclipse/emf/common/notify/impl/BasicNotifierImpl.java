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


import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;


/**
 * An extensible notifier implementation.
 */
public class BasicNotifierImpl implements Notifier
{
  /**
   * Creates a blank new instance.
   */
  public BasicNotifierImpl()
  {
    super();
  }

  public static class EAdapterList<E extends Object & Adapter> extends BasicEList<E>
  {
    private static final long serialVersionUID = 1L;

    protected Notifier notifier;

    public EAdapterList(Notifier notifier)
    {
      this.notifier = notifier;
    }

    protected boolean safe;

    @Override
    protected boolean canContainNull()
    {
      return false;
    }

    @Override
    protected boolean useEquals()
    {
      return false;
    }

    @Override
    protected Object [] newData(int capacity)
    {
      return new Adapter [capacity];
    }

    @Override
    protected void didAdd(int index, E newObject)
    {
      newObject.setTarget(notifier);
    }

    @Override
    protected void didRemove(int index, E oldObject)
    {
      E adapter = oldObject;
      if (notifier.eDeliver())
      {
        Notification notification = 
          new NotificationImpl(Notification.REMOVING_ADAPTER, oldObject, null, index)
          {
            @Override
            public Object getNotifier()
            {
              return notifier;
            }
          };
        adapter.notifyChanged(notification);
      }
      if (adapter instanceof Adapter.Internal)
      {
        ((Adapter.Internal)adapter).unsetTarget(notifier);
      }
      else if (adapter.getTarget() == notifier) 
      {
        adapter.setTarget(null);
      }
    }

    @Override
    public Object [] data()
    {
      safe = true;
      if (data != null && data.length != size)
      {
        if (size == 0)
        {
          data = null;
        }
        else
        {
          Object [] oldData = data;
          data = newData(size);
          System.arraycopy(oldData, 0, data, 0, size);
        }
      }
      return data;
    }

    protected void ensureSafety()
    {
      if (safe && data != null)
      {
        Object [] oldData = data;
        data = newData(data.length);
        System.arraycopy(oldData, 0, data, 0, size);
        safe = false;
      }
    }

    @Override
    public boolean add(E object)
    {
      ensureSafety();
      return super.add(object);
    }

    @Override
    public void add(int index, E object)
    {
      ensureSafety();
      super.add(index, object);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection)
    {
      ensureSafety();
      return super.addAll(collection);
    }

    @Override
    public boolean remove(Object object)
    {
      ensureSafety();
      return super.remove(object);
    }

    @Override
    public E remove(int index)
    {
      ensureSafety();
      return super.remove(index);
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
      ensureSafety();
      return super.removeAll(collection);
    }

    @Override
    public void clear()
    {
      ensureSafety();
      super.clear();
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
      ensureSafety();
      return super.retainAll(collection);
    }

    @Override
    public E set(int index, E object)
    {
      ensureSafety();
      return super.set(index, object);
    }

    @Override
    public void move(int newPosition, E object)
    {
      ensureSafety();
      super.move(newPosition, object);
    }

    @Override
    public E move(int newPosition, int oldPosition)
    {
      ensureSafety();
      return super.move(newPosition, oldPosition);
    }
  }

  public EList<Adapter> eAdapters()
  {
    return ECollections.emptyEList();
  }

  /**
   * Returns the adapter list, even if it is <code>null</code>.
   * @return the adapter list, even if it is <code>null</code>.
   */
  protected BasicEList<Adapter> eBasicAdapters()
  {
    return null;
  }

  /**
   * Returns the underlying array of adapters.
   * The length of this array reflects exactly the number of adapters
   * where <code>null</code> represents the lack of any adapters.
   * This array may not be modified by the caller 
   * and must be guaranteed not to be modified even if the {@link #eAdapters() list of adapters} is modified.
   * @return the underlying array of adapters.
   */
  protected Adapter[] eBasicAdapterArray()
  {
    BasicEList<Adapter> eBasicAdapters = eBasicAdapters();
    return eBasicAdapters == null ? null : (Adapter[])eBasicAdapters.data();
  }

  /**
   * Returns whether there are any adapters.
   * @return whether there are any adapters.
   */
  protected boolean eBasicHasAdapters()
  {
    BasicEList<Adapter> eBasicAdapters = eBasicAdapters();
    return eBasicAdapters != null && eBasicAdapters.size() != 0;
  }

  /*
   * Javadoc copied from interface.
   */
  public boolean eDeliver()
  {
    return false;
  }

  /*
   * Javadoc copied from interface.
   */
  public void eSetDeliver(boolean deliver)
  {
    throw new UnsupportedOperationException();
  }

  /*
   * Javadoc copied from interface.
   */
  public void eNotify(Notification notification)
  {
    Adapter[] eAdapters = eBasicAdapterArray();
    if (eAdapters != null && eDeliver())
    {
      for (int i = 0, size = eAdapters.length; i < size; ++i)
      {
        eAdapters[i].notifyChanged(notification);
      }
    }
  }

  /**
   * Returns whether {@link #eNotify eNotify} needs to be called.
   * This may return <code>true</code> even when {@link #eDeliver eDeliver} is <code>false</code>
   * or when {@link #eAdapters eAdapters} is empty.
   * @return whether {@link #eNotify eNotify} needs to be called.
   */
  public boolean eNotificationRequired()
  {
    return eBasicHasAdapters() && eDeliver();
  }
}
