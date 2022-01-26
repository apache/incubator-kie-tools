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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EReference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EReferenceImpl#isContainment <em>Containment</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EReferenceImpl#isContainer <em>Container</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EReferenceImpl#isResolveProxies <em>Resolve Proxies</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EReferenceImpl#getEOpposite <em>EOpposite</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EReferenceImpl#getEReferenceType <em>EReference Type</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EReferenceImpl#getEKeys <em>EKeys</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EReferenceImpl extends EStructuralFeatureImpl implements EReference
{
  /**
   * The default value of the '{@link #isContainment() <em>Containment</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isContainment()
   * @generated
   * @ordered
   */
  protected static final boolean CONTAINMENT_EDEFAULT = false;

  /**
   * The flag representing the value of the '{@link #isContainment() <em>Containment</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isContainment()
   * @generated
   * @ordered
   */
  protected static final int CONTAINMENT_EFLAG = 1 << 15;

  /**
   * The default value of the '{@link #isContainer() <em>Container</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isContainer()
   * @generated
   * @ordered
   */
  protected static final boolean CONTAINER_EDEFAULT = false;

  /**
   * The default value of the '{@link #isResolveProxies() <em>Resolve Proxies</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isResolveProxies()
   * @generated
   * @ordered
   */
  protected static final boolean RESOLVE_PROXIES_EDEFAULT = true;

  /**
   * The flag representing the value of the '{@link #isResolveProxies() <em>Resolve Proxies</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isResolveProxies()
   * @generated
   * @ordered
   */
  protected static final int RESOLVE_PROXIES_EFLAG = 1 << 16;

  /**
   * The cached value of the '{@link #getEOpposite() <em>EOpposite</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEOpposite()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EReference eOpposite;

  /**
   * The cached value of the '{@link #getEKeys() <em>EKeys</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEKeys()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EAttribute> eKeys;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EReferenceImpl()
  {
    super();
    eFlags |= RESOLVE_PROXIES_EFLAG;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.EREFERENCE;
  }

  public boolean isBidirectional()
  {
    return getEOpposite() != null;
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isContainment()
  {
    return (eFlags & CONTAINMENT_EFLAG) != 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContainmentGen(boolean newContainment)
  {
    boolean oldContainment = (eFlags & CONTAINMENT_EFLAG) != 0;
    if (newContainment) eFlags |= CONTAINMENT_EFLAG; else eFlags &= ~CONTAINMENT_EFLAG;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EREFERENCE__CONTAINMENT, oldContainment, newContainment));
  }

  public void setContainment(boolean value)
  {
    setContainmentGen(value);
    if (eContainer instanceof EClassImpl)
    {
      ((EClassImpl)eContainer).getESuperAdapter().setFlags(ESuperAdapter.REFERENCES);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated modifiable
   */
  @Override
  public boolean isContainer()
  {
    EReference theOpposite = getEOpposite();
    return theOpposite != null && theOpposite.isContainment();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isResolveProxies()
  {
    return (eFlags & RESOLVE_PROXIES_EFLAG) != 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setResolveProxies(boolean newResolveProxies)
  {
    boolean oldResolveProxies = (eFlags & RESOLVE_PROXIES_EFLAG) != 0;
    if (newResolveProxies) eFlags |= RESOLVE_PROXIES_EFLAG; else eFlags &= ~RESOLVE_PROXIES_EFLAG;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EREFERENCE__RESOLVE_PROXIES, oldResolveProxies, newResolveProxies));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEOpposite()
  {
    if (eOpposite != null && eOpposite.eIsProxy())
    {
      InternalEObject oldEOpposite = (InternalEObject)eOpposite;
      eOpposite = (EReference)eResolveProxy(oldEOpposite);
      if (eOpposite != oldEOpposite)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, EcorePackage.EREFERENCE__EOPPOSITE, oldEOpposite, eOpposite));
      }
    }
    return eOpposite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference basicGetEOpposite()
  {
    return eOpposite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEOpposite(EReference newEOpposite)
  {
    EReference oldEOpposite = eOpposite;
    eOpposite = newEOpposite;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EREFERENCE__EOPPOSITE, oldEOpposite, eOpposite));
  }

  @Override
  public NotificationChain setEType(EClassifier newEType, NotificationChain msgs)
  {
    eReferenceType = null;
    return super.setEType(newEType, msgs);
  }

  @GwtTransient
  protected EClass eReferenceType;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EClass getEReferenceType()
  {
    if (eReferenceType == null || !isFrozen() && eReferenceType.eIsProxy())
    {
      EClassifier eType = getEType();
      if (eType instanceof EClass)
      {
        eReferenceType =(EClass)eType;
      }
    }
    return eReferenceType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EClass basicGetEReferenceType()
  {
    if (eReferenceType == null)
    {
      EClassifier eType = basicGetEType();
      if (eType instanceof EClass)
      {
        eReferenceType = (EClass)eType;
      }
    }
    return eReferenceType;
  }

  @Override
  protected void freeze()
  {
    getEReferenceType();
    super.freeze();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EAttribute> getEKeys()
  {
    if (eKeys == null)
    {
      eKeys = new EObjectResolvingEList<EAttribute>(EAttribute.class, this, EcorePackage.EREFERENCE__EKEYS);
    }
    return eKeys;
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
      case EcorePackage.EREFERENCE__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EREFERENCE__NAME:
        return getName();
      case EcorePackage.EREFERENCE__ORDERED:
        return isOrdered();
      case EcorePackage.EREFERENCE__UNIQUE:
        return isUnique();
      case EcorePackage.EREFERENCE__LOWER_BOUND:
        return getLowerBound();
      case EcorePackage.EREFERENCE__UPPER_BOUND:
        return getUpperBound();
      case EcorePackage.EREFERENCE__MANY:
        return isMany();
      case EcorePackage.EREFERENCE__REQUIRED:
        return isRequired();
      case EcorePackage.EREFERENCE__ETYPE:
        if (resolve) return getEType();
        return basicGetEType();
      case EcorePackage.EREFERENCE__EGENERIC_TYPE:
        return getEGenericType();
      case EcorePackage.EREFERENCE__CHANGEABLE:
        return isChangeable();
      case EcorePackage.EREFERENCE__VOLATILE:
        return isVolatile();
      case EcorePackage.EREFERENCE__TRANSIENT:
        return isTransient();
      case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
        return getDefaultValueLiteral();
      case EcorePackage.EREFERENCE__DEFAULT_VALUE:
        return getDefaultValue();
      case EcorePackage.EREFERENCE__UNSETTABLE:
        return isUnsettable();
      case EcorePackage.EREFERENCE__DERIVED:
        return isDerived();
      case EcorePackage.EREFERENCE__ECONTAINING_CLASS:
        return getEContainingClass();
      case EcorePackage.EREFERENCE__CONTAINMENT:
        return isContainment();
      case EcorePackage.EREFERENCE__CONTAINER:
        return isContainer();
      case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
        return isResolveProxies();
      case EcorePackage.EREFERENCE__EOPPOSITE:
        if (resolve) return getEOpposite();
        return basicGetEOpposite();
      case EcorePackage.EREFERENCE__EREFERENCE_TYPE:
        if (resolve) return getEReferenceType();
        return basicGetEReferenceType();
      case EcorePackage.EREFERENCE__EKEYS:
        return getEKeys();
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
      case EcorePackage.EREFERENCE__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.EREFERENCE__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.EREFERENCE__ORDERED:
        setOrdered((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__UNIQUE:
        setUnique((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__LOWER_BOUND:
        setLowerBound((Integer)newValue);
        return;
      case EcorePackage.EREFERENCE__UPPER_BOUND:
        setUpperBound((Integer)newValue);
        return;
      case EcorePackage.EREFERENCE__ETYPE:
        setEType((EClassifier)newValue);
        return;
      case EcorePackage.EREFERENCE__EGENERIC_TYPE:
        setEGenericType((EGenericType)newValue);
        return;
      case EcorePackage.EREFERENCE__CHANGEABLE:
        setChangeable((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__VOLATILE:
        setVolatile((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__TRANSIENT:
        setTransient((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
        setDefaultValueLiteral((String)newValue);
        return;
      case EcorePackage.EREFERENCE__UNSETTABLE:
        setUnsettable((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__DERIVED:
        setDerived((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__CONTAINMENT:
        setContainment((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
        setResolveProxies((Boolean)newValue);
        return;
      case EcorePackage.EREFERENCE__EOPPOSITE:
        setEOpposite((EReference)newValue);
        return;
      case EcorePackage.EREFERENCE__EKEYS:
        getEKeys().clear();
        getEKeys().addAll((Collection<? extends EAttribute>)newValue);
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
      case EcorePackage.EREFERENCE__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.EREFERENCE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__ORDERED:
        setOrdered(ORDERED_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__UNIQUE:
        setUnique(UNIQUE_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__LOWER_BOUND:
        setLowerBound(LOWER_BOUND_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__UPPER_BOUND:
        setUpperBound(UPPER_BOUND_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__ETYPE:
        unsetEType();
        return;
      case EcorePackage.EREFERENCE__EGENERIC_TYPE:
        unsetEGenericType();
        return;
      case EcorePackage.EREFERENCE__CHANGEABLE:
        setChangeable(CHANGEABLE_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__VOLATILE:
        setVolatile(VOLATILE_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__TRANSIENT:
        setTransient(TRANSIENT_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
        setDefaultValueLiteral(DEFAULT_VALUE_LITERAL_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__UNSETTABLE:
        setUnsettable(UNSETTABLE_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__DERIVED:
        setDerived(DERIVED_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__CONTAINMENT:
        setContainment(CONTAINMENT_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
        setResolveProxies(RESOLVE_PROXIES_EDEFAULT);
        return;
      case EcorePackage.EREFERENCE__EOPPOSITE:
        setEOpposite((EReference)null);
        return;
      case EcorePackage.EREFERENCE__EKEYS:
        getEKeys().clear();
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
      case EcorePackage.EREFERENCE__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EREFERENCE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.EREFERENCE__ORDERED:
        return ((eFlags & ORDERED_EFLAG) != 0) != ORDERED_EDEFAULT;
      case EcorePackage.EREFERENCE__UNIQUE:
        return ((eFlags & UNIQUE_EFLAG) != 0) != UNIQUE_EDEFAULT;
      case EcorePackage.EREFERENCE__LOWER_BOUND:
        return lowerBound != LOWER_BOUND_EDEFAULT;
      case EcorePackage.EREFERENCE__UPPER_BOUND:
        return upperBound != UPPER_BOUND_EDEFAULT;
      case EcorePackage.EREFERENCE__MANY:
        return isMany() != MANY_EDEFAULT;
      case EcorePackage.EREFERENCE__REQUIRED:
        return isRequired() != REQUIRED_EDEFAULT;
      case EcorePackage.EREFERENCE__ETYPE:
        return isSetEType();
      case EcorePackage.EREFERENCE__EGENERIC_TYPE:
        return isSetEGenericType();
      case EcorePackage.EREFERENCE__CHANGEABLE:
        return ((eFlags & CHANGEABLE_EFLAG) != 0) != CHANGEABLE_EDEFAULT;
      case EcorePackage.EREFERENCE__VOLATILE:
        return ((eFlags & VOLATILE_EFLAG) != 0) != VOLATILE_EDEFAULT;
      case EcorePackage.EREFERENCE__TRANSIENT:
        return ((eFlags & TRANSIENT_EFLAG) != 0) != TRANSIENT_EDEFAULT;
      case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
        return DEFAULT_VALUE_LITERAL_EDEFAULT == null ? defaultValueLiteral != null : !DEFAULT_VALUE_LITERAL_EDEFAULT.equals(defaultValueLiteral);
      case EcorePackage.EREFERENCE__DEFAULT_VALUE:
        return DEFAULT_VALUE_EDEFAULT == null ? getDefaultValue() != null : !DEFAULT_VALUE_EDEFAULT.equals(getDefaultValue());
      case EcorePackage.EREFERENCE__UNSETTABLE:
        return ((eFlags & UNSETTABLE_EFLAG) != 0) != UNSETTABLE_EDEFAULT;
      case EcorePackage.EREFERENCE__DERIVED:
        return ((eFlags & DERIVED_EFLAG) != 0) != DERIVED_EDEFAULT;
      case EcorePackage.EREFERENCE__ECONTAINING_CLASS:
        return getEContainingClass() != null;
      case EcorePackage.EREFERENCE__CONTAINMENT:
        return ((eFlags & CONTAINMENT_EFLAG) != 0) != CONTAINMENT_EDEFAULT;
      case EcorePackage.EREFERENCE__CONTAINER:
        return isContainer() != CONTAINER_EDEFAULT;
      case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
        return ((eFlags & RESOLVE_PROXIES_EFLAG) != 0) != RESOLVE_PROXIES_EDEFAULT;
      case EcorePackage.EREFERENCE__EOPPOSITE:
        return eOpposite != null;
      case EcorePackage.EREFERENCE__EREFERENCE_TYPE:
        return basicGetEReferenceType() != null;
      case EcorePackage.EREFERENCE__EKEYS:
        return eKeys != null && !eKeys.isEmpty();
    }
    return eDynamicIsSet(featureID);
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
    result.append(" (containment: ");
    result.append((eFlags & CONTAINMENT_EFLAG) != 0);
    result.append(", resolveProxies: ");
    result.append((eFlags & RESOLVE_PROXIES_EFLAG) != 0);
    result.append(')');
    return result.toString();
  }

}
