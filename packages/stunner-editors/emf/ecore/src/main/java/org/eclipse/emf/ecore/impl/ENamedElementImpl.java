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


import com.google.gwt.user.client.rpc.GwtTransient;
import java.util.Collection;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ENamed Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.ENamedElementImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class ENamedElementImpl extends EModelElementImpl implements ENamedElement
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected String name = NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ENamedElementImpl()
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
    return EcorePackage.Literals.ENAMED_ELEMENT;
  }

  /**
   * Default is ID if name is null       
   */
  public String getName()
  {
    return getNameGen();
/*
    // if no default has been specified, use the id as the default.
    String defaultName = this.getNameGen();

    if (defaultName != null)
      return defaultName;
    else
      return eID();
*/
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getNameGen()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ENAMED_ELEMENT__NAME, oldName, name));
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
      case EcorePackage.ENAMED_ELEMENT__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.ENAMED_ELEMENT__NAME:
        return getName();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case EcorePackage.ENAMED_ELEMENT__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.ENAMED_ELEMENT__NAME:
        setName((String)newValue);
        return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case EcorePackage.ENAMED_ELEMENT__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.ENAMED_ELEMENT__NAME:
        setName(NAME_EDEFAULT);
        return;
    }
    eDynamicUnset(featureID);
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
      case EcorePackage.ENAMED_ELEMENT__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.ENAMED_ELEMENT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    }
    return eDynamicIsSet(featureID);
  }

  @Override
  public EList<EObject> eContents()
  {
    EStructuralFeature [] eStructuralFeatures = 
      ((EClassImpl.FeatureSubsetSupplier)eClass().getEAllStructuralFeatures()).containments();
      
    return
      eStructuralFeatures == null ?
        EContentsEList.<EObject>emptyContentsEList() :
        new EContentsEList<EObject>(this, eStructuralFeatures)
        {
          @Override
          protected boolean useIsSet()
          {
            return false;
          }

          @Override
          protected ListIterator<EObject> newResolvingListIterator()
          {
            return 
              new ResolvingFeatureIteratorImpl<EObject>(eObject, eStructuralFeatures)
              {
                @Override
                protected boolean useIsSet()
                {
                  return false;
                }
              };
          }

          @Override
          protected ListIterator<EObject> newNonResolvingListIterator()
          {
            return 
              new FeatureIteratorImpl<EObject>(eObject, eStructuralFeatures)
              {
                @Override
                protected boolean useIsSet()
                {
                  return false;
                }
              };
          }

        };
  }

  @Override
  public EList<EObject> eCrossReferences()
  {
    EStructuralFeature [] eStructuralFeatures = 
      ((EClassImpl.FeatureSubsetSupplier)eClass().getEAllStructuralFeatures()).crossReferences();

    return
      eStructuralFeatures == null ?
        ECrossReferenceEList.<EObject>emptyCrossReferenceEList() :
        new ECrossReferenceEList<EObject>(this, eStructuralFeatures)
        {
          @Override
          protected boolean useIsSet()
          {
            return false;
          }

          @Override
          protected ListIterator<EObject> newResolvingListIterator()
          {
            return 
              new ResolvingFeatureIteratorImpl<EObject>(eObject, eStructuralFeatures)
              {
                @Override
                protected boolean useIsSet()
                {
                  return false;
                }
              };
          }

          @Override
          protected ListIterator<EObject> newNonResolvingListIterator()
          {
            return 
              new FeatureIteratorImpl<EObject>(eObject, eStructuralFeatures)
              {
                @Override
                protected boolean useIsSet()
                {
                  return false;
                }
              };
          }
        };
  }

}
