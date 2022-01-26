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


import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EParameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EParameterImpl#getEOperation <em>EOperation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EParameterImpl extends ETypedElementImpl implements EParameter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EParameterImpl()
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
    return EcorePackage.Literals.EPARAMETER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated modifiable
   */
  public EOperation getEOperation()
  {
    return (eContainerFeatureID() == EcorePackage.EPARAMETER__EOPERATION) ? (EOperation)eContainer : null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case EcorePackage.EPARAMETER__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.EPARAMETER__EOPERATION:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, EcorePackage.EPARAMETER__EOPERATION, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case EcorePackage.EPARAMETER__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.EPARAMETER__EGENERIC_TYPE:
        return basicUnsetEGenericType(msgs);
      case EcorePackage.EPARAMETER__EOPERATION:
        return eBasicSetContainer(null, EcorePackage.EPARAMETER__EOPERATION, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
      case EcorePackage.EPARAMETER__EOPERATION:
        return eInternalContainer().eInverseRemove(this, EcorePackage.EOPERATION__EPARAMETERS, EOperation.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case EcorePackage.EPARAMETER__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EPARAMETER__NAME:
        return getName();
      case EcorePackage.EPARAMETER__ORDERED:
        return isOrdered();
      case EcorePackage.EPARAMETER__UNIQUE:
        return isUnique();
      case EcorePackage.EPARAMETER__LOWER_BOUND:
        return getLowerBound();
      case EcorePackage.EPARAMETER__UPPER_BOUND:
        return getUpperBound();
      case EcorePackage.EPARAMETER__MANY:
        return isMany();
      case EcorePackage.EPARAMETER__REQUIRED:
        return isRequired();
      case EcorePackage.EPARAMETER__ETYPE:
        if (resolve) return getEType();
        return basicGetEType();
      case EcorePackage.EPARAMETER__EGENERIC_TYPE:
        return getEGenericType();
      case EcorePackage.EPARAMETER__EOPERATION:
        return getEOperation();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case EcorePackage.EPARAMETER__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EPARAMETER__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.EPARAMETER__ORDERED:
        return ((eFlags & ORDERED_EFLAG) != 0) != ORDERED_EDEFAULT;
      case EcorePackage.EPARAMETER__UNIQUE:
        return ((eFlags & UNIQUE_EFLAG) != 0) != UNIQUE_EDEFAULT;
      case EcorePackage.EPARAMETER__LOWER_BOUND:
        return lowerBound != LOWER_BOUND_EDEFAULT;
      case EcorePackage.EPARAMETER__UPPER_BOUND:
        return upperBound != UPPER_BOUND_EDEFAULT;
      case EcorePackage.EPARAMETER__MANY:
        return isMany() != MANY_EDEFAULT;
      case EcorePackage.EPARAMETER__REQUIRED:
        return isRequired() != REQUIRED_EDEFAULT;
      case EcorePackage.EPARAMETER__ETYPE:
        return isSetEType();
      case EcorePackage.EPARAMETER__EGENERIC_TYPE:
        return isSetEGenericType();
      case EcorePackage.EPARAMETER__EOPERATION:
        return getEOperation() != null;
    }
    return eDynamicIsSet(featureID);
  }

} //EParameterImpl
