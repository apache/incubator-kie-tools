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
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

/**
 * An extensible notifier implementation.
 */
public class NotifierImpl extends BasicNotifierImpl
{
  /**
   * The bit of {@link #eFlags} that is used to represent {@link #eDeliver}.
   */
  protected static final int EDELIVER = 0x0001;

  /**
   * The last bit used by this class; derived classes may use bit values higher than this.
   */
  protected static final int ELAST_NOTIFIER_FLAG = EDELIVER;

  /**
   * An extensible set of bit flags;
   * the first bit is used for {@link #EDELIVER} to implement {@link #eDeliver}.
   */
  protected int eFlags = EDELIVER;

  /**
   * The list of {@link org.eclipse.emf.common.notify.Adapter}s associated with the notifier.
   */
  protected BasicEList<Adapter> eAdapters;

  /**
   * Creates a blank new instance.
   */
  public NotifierImpl()
  {
    super();
  }

  @Override
  public EList<Adapter> eAdapters()
  {
    if (eAdapters == null)
    {
      eAdapters =  new EAdapterList<Adapter>(this);
    }
    return eAdapters;
  }

  @Override
  protected BasicEList<Adapter> eBasicAdapters()
  {
    return eAdapters;
  }

  @Override
  public boolean eDeliver()
  {
    return (eFlags & EDELIVER) != 0;
  }

  @Override
  public void eSetDeliver(boolean deliver)
  {
    if (deliver)
    {
      this.eFlags |= EDELIVER;
    }
    else
    {
      this.eFlags &= ~EDELIVER;
    }
  }
}
