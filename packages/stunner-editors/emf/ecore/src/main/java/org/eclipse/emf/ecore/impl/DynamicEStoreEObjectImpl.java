/**
 * Copyright (c) 2004-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.impl;


import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;


/**
 * An implementation of '<em><b>EObject</b></em>' that delegates to a {@link org.eclipse.emf.ecore.InternalEObject.EStore store}.
 */
public class DynamicEStoreEObjectImpl extends DynamicEObjectImpl
{
  protected static final InternalEObject EUNINITIALIZED_CONTAINER = new EObjectImpl();

  protected InternalEObject.EStore eStore;

  /**
   * Creates a store-based EObject.
   */
  public DynamicEStoreEObjectImpl()
  {
    super();
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  /**
   * Creates a store-based EObject.
   */
  public DynamicEStoreEObjectImpl(InternalEObject.EStore eStore)
  {
    super();
    eSetStore(eStore);
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  /**
   * Creates a store-based EObject.
   */
  public DynamicEStoreEObjectImpl(EClass eClass)
  {
    super(eClass);
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  /**
   * Creates a store-based EObject.
   */
  public DynamicEStoreEObjectImpl(EClass eClass, InternalEObject.EStore eStore)
  {
    super(eClass);
    eSetStore(eStore);
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  protected boolean eIsCaching()
  {
    return true;
  }

  @Override
  public Object dynamicGet(int dynamicFeatureID)
  {
    Object result = eSettings[dynamicFeatureID];
    if (result == null)
    {
      EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
      if (!eStructuralFeature.isTransient())
      {
        if (FeatureMapUtil.isFeatureMap(eStructuralFeature))
        {
          eSettings[dynamicFeatureID] = result = createFeatureMap(eStructuralFeature);
        }
        else if (eStructuralFeature.isMany())
        {
          eSettings[dynamicFeatureID] = result = createList(eStructuralFeature);
        }
        else
        {
          result = eStore().get(this, eStructuralFeature, InternalEObject.EStore.NO_INDEX);
          if (eIsCaching())
          {
            eSettings[dynamicFeatureID] = result;
          }
        }
      }
    }
    return result;
  }

  @Override
  public void dynamicSet(int dynamicFeatureID, Object value)
  {
    EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
    if (eStructuralFeature.isTransient())
    {
      eSettings[dynamicFeatureID] = value;
    }
    else
    {
      eStore().set(this, eStructuralFeature, InternalEObject.EStore.NO_INDEX, value == NIL ? null : value);
      if (eIsCaching())
      {
        eSettings[dynamicFeatureID] = value;
      }
    }
  }

  @Override
  public void dynamicUnset(int dynamicFeatureID)
  {
    eStore().unset(this, eDynamicFeature(dynamicFeatureID));
    eSettings[dynamicFeatureID] = null;
  }

  @Override
  public boolean eDynamicIsSet(EStructuralFeature eStructuralFeature)
  {
    return 
      eStructuralFeature.isTransient() ?
        super.eDynamicIsSet(eStructuralFeature) :
        eStore().isSet(this, eStructuralFeature);
  }

  protected <T> EList<T> createList(EStructuralFeature eStructuralFeature)
  {
    return new EStoreEObjectImpl.EStoreEList<T>(this, eStructuralFeature, eStore());
  }

  protected FeatureMap createFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return new EStoreEObjectImpl.EStoreFeatureMap(this, eStructuralFeature, eStore());
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    if (eContainer == EUNINITIALIZED_CONTAINER)
    {
      eInitializeContainer();
    }

    return eContainer;
  }

  @Override
  public int eContainerFeatureID()
  {
    if (eContainer == EUNINITIALIZED_CONTAINER)
    {
      eInitializeContainer();
    }

    return eContainerFeatureID;
  }

  protected void eInitializeContainer()
  {
    eContainer = eStore().getContainer(this);
    if (eContainer != null)
    {
      EStructuralFeature eContainingFeature = eStore().getContainingFeature(this);
      if (eContainingFeature instanceof EReference)
      {
        EReference eContainingReference = (EReference)eContainingFeature;
        EReference eOpposite = eContainingReference.getEOpposite();
        if (eOpposite != null)
        {
          eContainerFeatureID = eClass().getFeatureID(eOpposite);
          return;
        }
      }

      eContainerFeatureID = EOPPOSITE_FEATURE_BASE - eContainer.eClass().getFeatureID(eContainingFeature);
    }
  }

  @Override
  public InternalEObject.EStore eStore()
  {
    return eStore;
  }

  @Override
  public void eSetStore(InternalEObject.EStore store)
  {
    this.eStore = store;
  }

/*
  public String toString()
  {
    String result = super.toString();
    int index = result.indexOf("DynamicEStoreEObjectImpl");
    return index == -1 ? result : result.substring(0, index) + result.substring(index + 13);
  }
*/
}
