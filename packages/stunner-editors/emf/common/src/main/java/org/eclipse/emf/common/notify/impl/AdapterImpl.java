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
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;



/**
 * An extensible adapter implementation.
 */
public class AdapterImpl implements Adapter.Internal
{
  /**
   * The last notifier set to this adapter.
   */
  protected Notifier target = null;

  /**
   * Creates an instance.
   */
  public AdapterImpl()
  {
    super();
  }

  /**
   * Returns <code>false</code>
   * @param type the type.
   * @return <code>false</code>
   */
  public boolean isAdapterForType(Object type)
  {
    return false;
  }

  /**
   * Does nothing; clients may override so that it does something.
   */
  public void notifyChanged(Notification msg)
  {
    // Do nothing.
  }

  /*
   * Javadoc copied from interface.
   */
  public Notifier getTarget()
  {
    return target;
  }

  /*
   * Javadoc copied from interface.
   */
  public void setTarget(Notifier newTarget)
  {
    target = newTarget;
  }

  /*
   * Javadoc copied from interface.
   */
  public void unsetTarget(Notifier oldTarget)
  {
    if (target == oldTarget)
    {
      setTarget(null);
    }
  }
}
