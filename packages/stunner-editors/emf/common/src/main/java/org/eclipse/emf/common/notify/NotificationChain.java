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


/**
 * An accumulator of notifications.
 * As notifications are produced, 
 * they are {@link #add accumulated} in a chain, 
 * and possibly even merged, 
 * before finally being {@link #dispatch dispatched} to the notifier.
 */
public interface NotificationChain 
{
  /**
   * Adds a notification to the chain.
   * @return whether the notification was added.
   */
  public boolean add(Notification notification);

  /**
   * Dispatches each notification to the appropriate notifier via 
   * {@link org.eclipse.emf.common.notify.Notifier#eNotify Notifier.eNotify}.
   */
  public void dispatch();
}
