/**
 * Copyright (c) 2006-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.notify.impl;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;


/**
 * An alternate, extensible adapter implementation that is well suited to adapt for a number of objects
 * (typically all objects of a given type).
 * @since 2.2
 * 
 */
public class SingletonAdapterImpl implements Adapter.Internal
{
  /**
   * The list of all the targets to which this adapter is set.
   */
  protected List<Notifier> targets;

  /**
   * Creates an instance.
   */
  public SingletonAdapterImpl()
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

  public Notifier getTarget()
  {
    return targets == null || targets.isEmpty() ? null : targets.get(targets.size() - 1);
  }

  public void setTarget(Notifier target)
  {
    if (targets == null)
    {
      targets = new ArrayList<Notifier>();
    }
    targets.add(target);
  }

  public void unsetTarget(Notifier target)
  {
    if (targets != null)
    {
      targets.remove(target);
    }
  }

  /**
   * Removes the adapter from all its targets.
   */
  public void dispose()
  {
    List<Notifier> oldTargets = targets;
    targets = null;

    if (oldTargets != null)
    {
      for (Notifier notifier : oldTargets)
      {
        notifier.eAdapters().remove(this);
      }
    }
  }
}
