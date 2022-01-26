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
package org.eclipse.emf.ecore.util;


import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;


public class EObjectEList<E> extends EcoreEList<E>
{
  private static final long serialVersionUID = 1L;

  public static class Unsettable<E> extends EObjectEList<E>
  {
    private static final long serialVersionUID = 1L;

    protected boolean isSet;

    public Unsettable(Class<?> dataClass, InternalEObject owner, int featureID)
    {
      super(dataClass, owner, featureID);
    }

    @Override
    protected void didChange()
    {
      isSet = true;
    }

    @Override
    public boolean isSet()
    {
      return isSet;
    }

    @Override
    public void unset()
    {
      super.unset();
      if (isNotificationRequired())
      {
        boolean oldIsSet = isSet;
        isSet = false;
        owner.eNotify(createNotification(Notification.UNSET, oldIsSet, false));
      }
      else
      {
        isSet = false;
      }
    }
  }

  protected final int featureID;

  public EObjectEList(Class<?> dataClass, InternalEObject owner, int featureID)
  {
    super(dataClass, owner);
    this.featureID = featureID;
  }

  @Override
  public int getFeatureID()
  {
    return featureID;
  }

  @Override
  protected boolean useEquals()
  {
    return false;
  }

  @Override
  protected boolean isUnique()
  {
    return true;
  }

  @Override
  protected boolean hasInverse()
  {
    return false;
  }

  @Override
  protected boolean isEObject()
  {
    return true;
  }

  @Override
  protected boolean canContainNull()
  {
    return false;
  }

  @Override
  protected E resolve(int index, E object)
  {
    return object;
  }
}
