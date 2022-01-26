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
package org.eclipse.emf.common.notify;


import org.eclipse.emf.common.util.EList;

/**
 * A source of notification delivery.
 * Since all modeled objects will be notifiers, 
 * the method names start with "e" to distinguish the EMF methods 
 * from the client's methods.
 */
public interface Notifier
{
  /**
   * Returns list of the adapters associated with this notifier.
   * @return the adapters associated with this notifier.
   */
  EList<Adapter> eAdapters();

  /**
   * Returns whether this notifier will deliver notifications to the adapters.
   * @return whether notifications will be delivered.
   * @see #eSetDeliver
   */
  boolean eDeliver();

  /**
   * Sets whether this notifier will deliver notifications to the adapters.
   * @param deliver whether or not to deliver.
   * @see #eDeliver()
   */
  void eSetDeliver(boolean deliver);

  /**
   * Notifies a change to a feature of this notifier as described by the notification.
   * The notifications will generally be {@link #eDeliver() delivered} 
   * to the {@link #eAdapters adapters}
   * via {@link Adapter#notifyChanged Adapter.notifyChanged}.
   * @param notification a description of the change.
   */
  void eNotify(Notification notification);
}
