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
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ETyped Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#isOrdered <em>Ordered</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#isUnique <em>Unique</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#getLowerBound <em>Lower Bound</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#getUpperBound <em>Upper Bound</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#isMany <em>Many</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#getEType <em>EType</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypedElementImpl#getEGenericType <em>EGeneric Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class ETypedElementImpl extends ENamedElementImpl implements ETypedElement
{
  /**
   * The default value of the '{@link #isOrdered() <em>Ordered</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOrdered()
   * @generated
   * @ordered
   */
  protected static final boolean ORDERED_EDEFAULT = true;

  /**
   * The flag representing the value of the '{@link #isOrdered() <em>Ordered</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOrdered()
   * @generated
   * @ordered
   */
  protected static final int ORDERED_EFLAG = 1 << 8;

  /**
   * The default value of the '{@link #isUnique() <em>Unique</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUnique()
   * @generated
   * @ordered
   */
  protected static final boolean UNIQUE_EDEFAULT = true;

  /**
   * The flag representing the value of the '{@link #isUnique() <em>Unique</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUnique()
   * @generated
   * @ordered
   */
  protected static final int UNIQUE_EFLAG = 1 << 9;

  /**
   * The default value of the '{@link #getLowerBound() <em>Lower Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLowerBound()
   * @generated
   * @ordered
   */
  protected static final int LOWER_BOUND_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getLowerBound() <em>Lower Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLowerBound()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected int lowerBound = LOWER_BOUND_EDEFAULT;

  /**
   * The default value of the '{@link #getUpperBound() <em>Upper Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUpperBound()
   * @generated
   * @ordered
   */
  protected static final int UPPER_BOUND_EDEFAULT = 1;

  /**
   * The cached value of the '{@link #getUpperBound() <em>Upper Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUpperBound()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected int upperBound = UPPER_BOUND_EDEFAULT;

  /**
   * The default value of the '{@link #isMany() <em>Many</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isMany()
   * @generated
   * @ordered
   */
  protected static final boolean MANY_EDEFAULT = false;

  /**
   * The default value of the '{@link #isRequired() <em>Required</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isRequired()
   * @generated
   * @ordered
   */
  protected static final boolean REQUIRED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #getEType() <em>EType</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEType()
   * @generated NOT
   * @ordered
   */
  @GwtTransient
  protected EClassifier eType;

  /**
   * The cached value of the '{@link #getEGenericType() <em>EGeneric Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEGenericType()
   * @generated NOT
   * @ordered
   */
  @GwtTransient
  protected EGenericType eGenericType;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ETypedElementImpl()
  {
    super();
    eFlags |= ORDERED_EFLAG;
    eFlags |= UNIQUE_EFLAG;
  }

  @Override
  protected void freeze()
  {
    getEType();
    super.freeze();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.ETYPED_ELEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isOrdered()
  {
    return (eFlags & ORDERED_EFLAG) != 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOrdered(boolean newOrdered)
  {
    boolean oldOrdered = (eFlags & ORDERED_EFLAG) != 0;
    if (newOrdered) eFlags |= ORDERED_EFLAG; else eFlags &= ~ORDERED_EFLAG;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ETYPED_ELEMENT__ORDERED, oldOrdered, newOrdered));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isUnique()
  {
    return (eFlags & UNIQUE_EFLAG) != 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUnique(boolean newUnique)
  {
    boolean oldUnique = (eFlags & UNIQUE_EFLAG) != 0;
    if (newUnique) eFlags |= UNIQUE_EFLAG; else eFlags &= ~UNIQUE_EFLAG;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ETYPED_ELEMENT__UNIQUE, oldUnique, newUnique));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getLowerBound()
  {
    return lowerBound;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLowerBound(int newLowerBound)
  {
    int oldLowerBound = lowerBound;
    lowerBound = newLowerBound;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ETYPED_ELEMENT__LOWER_BOUND, oldLowerBound, lowerBound));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getUpperBound()
  {
    return upperBound;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUpperBound(int newUpperBound)
  {
    int oldUpperBound = upperBound;
    upperBound = newUpperBound;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ETYPED_ELEMENT__UPPER_BOUND, oldUpperBound, upperBound));
  }

  public boolean isMany()
  {
    int upper = getUpperBound();
    return upper > 1 || upper == UNBOUNDED_MULTIPLICITY;
  }

  public boolean isRequired()
  {
    int lower = getLowerBound();
    return lower >= 1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EClassifier getEType()
  {
    if (!isFrozen() && eType != null && eType.eIsProxy())
    {
      InternalEObject oldEType = (InternalEObject)eType;
      eType = (EClassifier)eResolveProxy(oldEType);
      if (eType != oldEType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, EcorePackage.ETYPED_ELEMENT__ETYPE, oldEType, eType));
      }
    }
    return eType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EClassifier basicGetEType()
  {
    return eType;
  }

  public NotificationChain setEType(EClassifier newEType, NotificationChain msgs)
  {
    EClassifier oldEType = eType;
    eType = newEType;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EcorePackage.ETYPED_ELEMENT__ETYPE, oldEType, eType);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
   return msgs;
  }

  public void setEType(EClassifier newEType)
  {
    NotificationChain msgs = setEType(newEType, null);
    EGenericType newEGenericType = null;
    if (newEType != null)
    {
      newEGenericType = EcoreFactory.eINSTANCE.createEGenericType();
      newEGenericType.setEClassifier(eType);
    }
    msgs = setEGenericType(newEGenericType, msgs);
    if (msgs != null)
    {
      msgs.dispatch();
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void unsetEType()
  {
    setEType(null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isSetEType()
  {
    return 
      eType != null &&
        eGenericType.getETypeParameter() == null &&
        eGenericType.getETypeArguments().isEmpty();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EGenericType getEGenericType()
  {
    return eGenericType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public NotificationChain basicSetEGenericType(EGenericType newEGenericType, NotificationChain msgs)
  {
    EGenericType oldEGenericType = eGenericType;
    eGenericType = newEGenericType;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE, oldEGenericType, newEGenericType);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    if (newEGenericType == null)
    {
      if (eType != null)
      {
        msgs = setEType(null, msgs);
      }
    }
    else
    {
      EClassifier newEType = ((EGenericTypeImpl)newEGenericType).basicGetERawType();
      if (newEType != eType)
      {
        msgs = setEType(newEType, msgs);
      }
    }
    return msgs;
  }

  public NotificationChain setEGenericType(EGenericType newEGenericType, NotificationChain msgs)
  {
    if (newEGenericType != eGenericType)
    {
      if (eGenericType != null)
        msgs = ((InternalEObject)eGenericType).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE, null, msgs);
      if (newEGenericType != null)
        msgs = ((InternalEObject)newEGenericType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE, null, msgs);
      msgs = basicSetEGenericType(newEGenericType, msgs);
    }
    else if (eNotificationRequired())
    {
      ENotificationImpl notification = 
        new ENotificationImpl(this, Notification.SET, EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE, newEGenericType, newEGenericType);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setEGenericType(EGenericType newEGenericType)
  {
    NotificationChain msgs = setEGenericType(newEGenericType, null);
    if (msgs != null)
    {
      msgs.dispatch();
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public NotificationChain basicUnsetEGenericType(NotificationChain msgs)
  {
    msgs = setEType(null, msgs);
    return basicSetEGenericType(null, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void unsetEGenericType()
  {
    setEGenericType(null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isSetEGenericType()
  {
    return eGenericType != null && !isSetEType();
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
      case EcorePackage.ETYPED_ELEMENT__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE:
        return basicUnsetEGenericType(msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
      case EcorePackage.ETYPED_ELEMENT__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.ETYPED_ELEMENT__NAME:
        return getName();
      case EcorePackage.ETYPED_ELEMENT__ORDERED:
        return isOrdered();
      case EcorePackage.ETYPED_ELEMENT__UNIQUE:
        return isUnique();
      case EcorePackage.ETYPED_ELEMENT__LOWER_BOUND:
        return getLowerBound();
      case EcorePackage.ETYPED_ELEMENT__UPPER_BOUND:
        return getUpperBound();
      case EcorePackage.ETYPED_ELEMENT__MANY:
        return isMany();
      case EcorePackage.ETYPED_ELEMENT__REQUIRED:
        return isRequired();
      case EcorePackage.ETYPED_ELEMENT__ETYPE:
        if (resolve) return getEType();
        return basicGetEType();
      case EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE:
        return getEGenericType();
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
      case EcorePackage.ETYPED_ELEMENT__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.ETYPED_ELEMENT__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.ETYPED_ELEMENT__ORDERED:
        setOrdered((Boolean)newValue);
        return;
      case EcorePackage.ETYPED_ELEMENT__UNIQUE:
        setUnique((Boolean)newValue);
        return;
      case EcorePackage.ETYPED_ELEMENT__LOWER_BOUND:
        setLowerBound((Integer)newValue);
        return;
      case EcorePackage.ETYPED_ELEMENT__UPPER_BOUND:
        setUpperBound((Integer)newValue);
        return;
      case EcorePackage.ETYPED_ELEMENT__ETYPE:
        setEType((EClassifier)newValue);
        return;
      case EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE:
        setEGenericType((EGenericType)newValue);
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
      case EcorePackage.ETYPED_ELEMENT__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.ETYPED_ELEMENT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.ETYPED_ELEMENT__ORDERED:
        setOrdered(ORDERED_EDEFAULT);
        return;
      case EcorePackage.ETYPED_ELEMENT__UNIQUE:
        setUnique(UNIQUE_EDEFAULT);
        return;
      case EcorePackage.ETYPED_ELEMENT__LOWER_BOUND:
        setLowerBound(LOWER_BOUND_EDEFAULT);
        return;
      case EcorePackage.ETYPED_ELEMENT__UPPER_BOUND:
        setUpperBound(UPPER_BOUND_EDEFAULT);
        return;
      case EcorePackage.ETYPED_ELEMENT__ETYPE:
        unsetEType();
        return;
      case EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE:
        unsetEGenericType();
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
      case EcorePackage.ETYPED_ELEMENT__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.ETYPED_ELEMENT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.ETYPED_ELEMENT__ORDERED:
        return ((eFlags & ORDERED_EFLAG) != 0) != ORDERED_EDEFAULT;
      case EcorePackage.ETYPED_ELEMENT__UNIQUE:
        return ((eFlags & UNIQUE_EFLAG) != 0) != UNIQUE_EDEFAULT;
      case EcorePackage.ETYPED_ELEMENT__LOWER_BOUND:
        return lowerBound != LOWER_BOUND_EDEFAULT;
      case EcorePackage.ETYPED_ELEMENT__UPPER_BOUND:
        return upperBound != UPPER_BOUND_EDEFAULT;
      case EcorePackage.ETYPED_ELEMENT__MANY:
        return isMany() != MANY_EDEFAULT;
      case EcorePackage.ETYPED_ELEMENT__REQUIRED:
        return isRequired() != REQUIRED_EDEFAULT;
      case EcorePackage.ETYPED_ELEMENT__ETYPE:
        return isSetEType();
      case EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE:
        return isSetEGenericType();
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
    result.append(" (ordered: ");
    result.append((eFlags & ORDERED_EFLAG) != 0);
    result.append(", unique: ");
    result.append((eFlags & UNIQUE_EFLAG) != 0);
    result.append(", lowerBound: ");
    result.append(lowerBound);
    result.append(", upperBound: ");
    result.append(upperBound);
    result.append(')');
    return result.toString();
  }

} //ETypedElementImpl
