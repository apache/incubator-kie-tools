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
package org.eclipse.emf.ecore.impl;


import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;

import com.google.gwt.user.client.rpc.GwtTransient;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EObject</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated NOT
 */
public class EObjectImpl extends BasicEObjectImpl implements EObject
{
  /**
   * The bit of {@link #eFlags} that is used to represent {@link #eDeliver}.
   */
  @GwtTransient
  protected static final int EDELIVER = 0x0001;
  
  /**
   * The bit of {@link #eFlags} that is used to represent whether there is a dynamic EClass.
   */
  @GwtTransient
  protected static final int EDYNAMIC_CLASS = 0x0002;

  /**
   * The bit of {@link #eFlags} that is used to represent {@link #eIsProxy}.
   */
  @GwtTransient
  protected static final int EPROXY = 0x004;

  /**
   * The last bit used by this class; derived classes may use bit values higher than this.
   */
  @GwtTransient
  protected static final int ELAST_NOTIFIER_FLAG = EPROXY;

  /**
   * The last bit used by this class; derived classes may use bit values higher than this.
   */
  @GwtTransient
  public static final int ELAST_EOBJECT_FLAG = ELAST_NOTIFIER_FLAG;

  /**
   * An extensible set of bit flags;
   * the first bit is used for {@link #EDELIVER} to implement {@link #eDeliver}
   * and the second bit is used for {@link #EPROXY} to implement {@link #eIsProxy}.
   */
  @GwtTransient
  protected int eFlags = EDELIVER;

  /**
   * The list of {@link org.eclipse.emf.common.notify.Adapter}s associated with the notifier.
   */
  @GwtTransient
  protected BasicEList<Adapter> eAdapters;

  /**
   * The container of this object.
   */
  @GwtTransient
  protected InternalEObject eContainer;

  /**
   * The feature ID of this object's container holding feature, if there is one,
   * or {@link #EOPPOSITE_FEATURE_BASE EOPPOSITE_FEATURE_BASE} minus the feature ID of the container's feature that contains this object.
   */
  @GwtTransient
  protected int eContainerFeatureID;

  /**
   * Additional less frequently used fields.
   */
  @GwtTransient
  protected EPropertiesHolder eProperties;
  
  /**
   * <!-- begin-user-doc -->
   * Creates an EObject.
   * <!-- end-user-doc -->
   * @generated
   */
  protected EObjectImpl() 
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.eINSTANCE.getEObject();
  }

  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
      case EcorePackage.EOBJECT___ECLASS:
        return eClass();
      case EcorePackage.EOBJECT___EIS_PROXY:
        return eIsProxy();
      case EcorePackage.EOBJECT___ERESOURCE:
        return eResource();
      case EcorePackage.EOBJECT___ECONTAINER:
        return eContainer();
      case EcorePackage.EOBJECT___ECONTAINING_FEATURE:
        return eContainingFeature();
      case EcorePackage.EOBJECT___ECONTAINMENT_FEATURE:
        return eContainmentFeature();
      case EcorePackage.EOBJECT___ECONTENTS:
        return eContents();
      case EcorePackage.EOBJECT___EALL_CONTENTS:
        return eAllContents();
      case EcorePackage.EOBJECT___ECROSS_REFERENCES:
        return eCrossReferences();
      case EcorePackage.EOBJECT___EGET__ESTRUCTURALFEATURE:
        return eGet((EStructuralFeature)arguments.get(0));
      case EcorePackage.EOBJECT___EGET__ESTRUCTURALFEATURE_BOOLEAN:
        return eGet((EStructuralFeature)arguments.get(0), (Boolean)arguments.get(1));
      case EcorePackage.EOBJECT___ESET__ESTRUCTURALFEATURE_OBJECT:
        eSet((EStructuralFeature)arguments.get(0), arguments.get(1));
        return null;
      case EcorePackage.EOBJECT___EIS_SET__ESTRUCTURALFEATURE:
        return eIsSet((EStructuralFeature)arguments.get(0));
      case EcorePackage.EOBJECT___EUNSET__ESTRUCTURALFEATURE:
        eUnset((EStructuralFeature)arguments.get(0));
        return null;
      case EcorePackage.EOBJECT___EINVOKE__EOPERATION_ELIST:
        return eInvoke((EOperation)arguments.get(0), (EList<?>)arguments.get(1));    
    }
    return eDynamicInvoke(operationID, arguments);
  }

  /*
   * Javadoc copied from interface.
   */
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

  /*
   * Javadoc copied from interface.
   */
  @Override
  public boolean eDeliver()
  {
    return (eFlags & EDELIVER) != 0;
  }

  /*
   * Javadoc copied from interface.
   */
  @Override
  public void eSetDeliver(boolean deliver)
  {
    if (deliver)
    {
      eFlags |= EDELIVER;
    }
    else
    {
      eFlags &= ~EDELIVER;
    }
  }

  /* 
   * @see org.eclipse.emf.ecore.EObject#eIsProxy()
   */
  @Override
  public boolean eIsProxy()
  {
    return (eFlags & EPROXY) != 0;
  }
  
  /* 
   * @see org.eclipse.emf.ecore.InternalEObject#eSetProxyURI(org.eclipse.emf.common.util.URI)
   */
  @Override
  public void eSetProxyURI(URI uri)
  {
    super.eSetProxyURI(uri);
    if (uri != null)
    {
      eFlags |= EPROXY;
    }
    else
    {
      eFlags &= ~EPROXY;
    }
  }
  
  @Override
  protected EPropertiesHolder eProperties()
  {
    if (eProperties == null)
    {
      eProperties = new EPropertiesHolderImpl();
    }
    return eProperties;
  }

  @Override
  protected EPropertiesHolder eBasicProperties()
  {
    return eProperties;
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    return eContainer;
  }

  @Override
  public int eContainerFeatureID()
  {
    return eContainerFeatureID;
  }

  @Override
  protected void eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID)
  {
    eContainer = newContainer;
    eContainerFeatureID = newContainerFeatureID;
  }

  @Override
  public EClass eClass()
  {
    return (eFlags & EDYNAMIC_CLASS) == 0 ? eStaticClass() : eProperties().getEClass();
  }

  @Override
  public void eSetClass(EClass eClass)
  {
    super.eSetClass(eClass);
    if (eClass != null)
    {
      eFlags |= EDYNAMIC_CLASS;
    }
    else
    {
      eFlags &= ~EDYNAMIC_CLASS;
    }
  }
}
