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


import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;


/**
 * An extensible adapter factory implementation.
 */
public class AdapterFactoryImpl implements AdapterFactory
{
  /**
   * Creates an instance.
   */
  public AdapterFactoryImpl()
  {
    super();
  }

  /**
   * Returns <code>false</code>.
   * @param type the key indicating the type of adapter in question.
   * @return <code>false</code>.
   */
  public boolean isFactoryForType(Object type)
  {
    return false;
  }

  /**
   * Returns either 
   * the result of calling {@link #adapt(Notifier,Object) adapt(Notifier, Object)} 
   * or the result of calling {@link #resolve resolve(Object, Object)},
   * depending on whether the target is a notifier.
   * @param target arbitrary object to adapt.
   * @param type the key indicating the type of adapter required.
   * @return either an associated adapter or the object itself.
   * @see #adapt(Notifier,Object)
   * @see #resolve(Object, Object)
   */
  public Object adapt(Object target, Object type)
  {
    if (target instanceof Notifier)
    {
      return adapt((Notifier)target, type);
    }
    else
    {
      return resolve(target, type);
    }
  }

  /**
   * Returns the object itself.
   * This is called by {@link #adapt(Object,Object) adapt(Object, Object)} for objects that aren't notifiers.
   * @param object arbitrary object to adapt.
   * @param type the key indicating the type of adapter required.
   * @return the object itself.
   * @see #adapt(Object,Object)
   */
  protected Object resolve(Object object, Object type)
  {
    return object;
  }

  public Adapter adapt(Notifier target, Object type)
  {
    for (Adapter adapter : target.eAdapters())
    {
      if (adapter.isAdapterForType(type))
      {
        return adapter;
      }
    }
    return adaptNew(target, type);
  }

  /**
   * Creates an adapter by calling {@link #createAdapter(Notifier, Object) createAdapter(Notifier, Object)}
   * and associates it by calling {@link #associate(Adapter, Notifier) associate}.
   * @param target the notifier to adapt.
   * @param type the key indicating the type of adapter required.
   * @return a new associated adapter.
   * @see #createAdapter(Notifier, Object)
   * @see #associate(Adapter, Notifier)
   */
  public Adapter adaptNew(Notifier target, Object type)
  {
    Adapter adapter = createAdapter(target, type);
    associate(adapter, target);
    return adapter;
  }

  /**
   * Creates an adapter by calling {@link #createAdapter(Notifier) createAdapter(Notifier)}
   * and associates it by calling {@link #associate(Adapter, Notifier) associate}.
   * @param target notifier to adapt.
   * @see #createAdapter(Notifier)
   */
  public void adaptAllNew(Notifier target)
  {
    Adapter adapter = createAdapter(target);
    associate(adapter, target);
  }

  /**
   * Creates an adapter by calling {@link #createAdapter(Notifier) createAdapter(Notifier)}.
   * @param target the notifier to adapt.
   * @param type the key indicating the type of adapter required.
   * @return a new adapter.
   * @see #createAdapter(Notifier)
   */
  protected Adapter createAdapter(Notifier target, Object type)
  {
    return createAdapter(target);
  }

  /**
   * Creates an {@link org.eclipse.emf.common.notify.impl.AdapterImpl}.
   * @param target the notifier to adapt.
   * @return a new adapter.
   * @see #createAdapter(Notifier)
   */
  protected Adapter createAdapter(Notifier target)
  {
    return new AdapterImpl();
  }

  /** 
   * Associates an adapter with a notifier by adding it to the target's {@link org.eclipse.emf.common.notify.Notifier#eAdapters adapters}.
   * @param adapter the new adapter to associate.
   * @param target the notifier being adapted.
   */
  protected void associate(Adapter adapter, Notifier target)
  {
    if (adapter != null)
    {
      target.eAdapters().add(adapter);
    }
  }
}
