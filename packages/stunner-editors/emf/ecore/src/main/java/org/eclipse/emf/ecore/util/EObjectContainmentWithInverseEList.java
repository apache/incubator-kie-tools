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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;


public class EObjectContainmentWithInverseEList<E> extends EObjectContainmentEList<E>
{
  private static final long serialVersionUID = 1L;

  public static class Unsettable<E> extends EObjectContainmentWithInverseEList<E>
  {
    private static final long serialVersionUID = 1L;

    protected boolean isSet;

    public Unsettable(Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
    {
      super(dataClass, owner, featureID, inverseFeatureID);
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
    
    public static class Resolving<E> extends EObjectContainmentWithInverseEList.Unsettable<E>
    {
      private static final long serialVersionUID = 1L;

      public Resolving(Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
      {
        super(dataClass, owner, featureID, inverseFeatureID);
      }
      
      @Override
      protected boolean hasProxies()
      {
        return true; 
      }
      
      @SuppressWarnings("unchecked")
      @Override
      protected E resolve(int index, E object)
      {
        return (E)resolve(index, (EObject)object);
      }
    }
  }
  
  public static class Resolving<E> extends EObjectContainmentWithInverseEList<E>
  {
    private static final long serialVersionUID = 1L;

    public Resolving(Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
    {
      super(dataClass, owner, featureID, inverseFeatureID);
    }
      
    @Override
    protected boolean hasProxies()
    {
      return true; 
    }
      
    @SuppressWarnings("unchecked")
    @Override
    protected E resolve(int index, E object)
    {
      return (E)resolve(index, (EObject)object);
    }
  }

  protected final int inverseFeatureID;

  public EObjectContainmentWithInverseEList
    (Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
  {
    super(dataClass, owner, featureID);
    this.inverseFeatureID = inverseFeatureID;
  }

  @Override
  protected boolean hasNavigableInverse()
  {
    return true;
  }

  @Override
  public int getInverseFeatureID()
  {
    return inverseFeatureID;
  }
  
  @Override
  public Class<?> getInverseFeatureClass()
  {
    return dataClass;
  }
}
