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
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EEnum Literal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EEnumLiteralImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EEnumLiteralImpl#getInstance <em>Instance</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EEnumLiteralImpl#getLiteral <em>Literal</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EEnumLiteralImpl#getEEnum <em>EEnum</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EEnumLiteralImpl extends ENamedElementImpl implements EEnumLiteral
{
  /**
   * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected static final int VALUE_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected int value = VALUE_EDEFAULT;

  /**
   * The default value of the '{@link #getInstance() <em>Instance</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstance()
   * @generated
   * @ordered
   */
  protected static final Enumerator INSTANCE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getInstance() <em>Instance</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstance()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected Enumerator instance = INSTANCE_EDEFAULT;

  /**
   * The default value of the '{@link #getLiteral() <em>Literal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLiteral()
   * @generated
   * @ordered
   */
  protected static final String LITERAL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLiteral() <em>Literal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLiteral()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected String literal = LITERAL_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected EEnumLiteralImpl()
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
    return EcorePackage.Literals.EENUM_LITERAL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setValue(int newValue)
  {
    int oldValue = value;
    value = newValue;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EENUM_LITERAL__VALUE, oldValue, value));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Enumerator getInstance()
  {
    return instance != null ? instance : generatedInstance;
  }

  /**
   * This stores the default or generated instance value.
   */
  @GwtTransient
  protected Enumerator generatedInstance = this;

  public void setGeneratedInstance(boolean isGenerated)
  {
    if (isGenerated)
    {
      if (generatedInstance == this)
      {
        generatedInstance = instance;
        instance = null;
      }
    }
    else if (generatedInstance != this)
    {
      instance = generatedInstance;
      generatedInstance = this;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInstanceGen(Enumerator newInstance)
  {
    Enumerator oldInstance = instance;
    instance = newInstance;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EENUM_LITERAL__INSTANCE, oldInstance, instance));
  }

  public void setInstance(Enumerator newInstance)
  {
    setInstanceGen(newInstance);
    if (newInstance == null)
    {
      setName(null);
      setValue(0);
      setLiteral(null);
    }
    else if (newInstance != this)
    {
      setName(newInstance.getName());
      setValue(newInstance.getValue());
      String literal = newInstance.getLiteral();
      setLiteral(literal == null || literal.equals(newInstance.getName()) ? null : literal);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLiteralGen()
  {
    return literal;
  }

  public String getLiteral()
  {
    String result = getLiteralGen();
    return result == null ? getName() : result;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLiteral(String newLiteral)
  {
    String oldLiteral = literal;
    literal = newLiteral;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EENUM_LITERAL__LITERAL, oldLiteral, literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EEnum getEEnum()
  {
    return (eContainerFeatureID() == EcorePackage.EENUM_LITERAL__EENUM) ? (EEnum)eContainer : null;
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
      case EcorePackage.EENUM_LITERAL__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.EENUM_LITERAL__EENUM:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, EcorePackage.EENUM_LITERAL__EENUM, msgs);
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
      case EcorePackage.EENUM_LITERAL__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.EENUM_LITERAL__EENUM:
        return eBasicSetContainer(null, EcorePackage.EENUM_LITERAL__EENUM, msgs);
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
      case EcorePackage.EENUM_LITERAL__EENUM:
        return eInternalContainer().eInverseRemove(this, EcorePackage.EENUM__ELITERALS, EEnum.class, msgs);
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
      case EcorePackage.EENUM_LITERAL__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EENUM_LITERAL__NAME:
        return getName();
      case EcorePackage.EENUM_LITERAL__VALUE:
        return getValue();
      case EcorePackage.EENUM_LITERAL__INSTANCE:
        return getInstance();
      case EcorePackage.EENUM_LITERAL__LITERAL:
        return getLiteral();
      case EcorePackage.EENUM_LITERAL__EENUM:
        return getEEnum();
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
      case EcorePackage.EENUM_LITERAL__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.EENUM_LITERAL__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.EENUM_LITERAL__VALUE:
        setValue((Integer)newValue);
        return;
      case EcorePackage.EENUM_LITERAL__INSTANCE:
        setInstance((Enumerator)newValue);
        return;
      case EcorePackage.EENUM_LITERAL__LITERAL:
        setLiteral((String)newValue);
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
      case EcorePackage.EENUM_LITERAL__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.EENUM_LITERAL__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.EENUM_LITERAL__VALUE:
        setValue(VALUE_EDEFAULT);
        return;
      case EcorePackage.EENUM_LITERAL__INSTANCE:
        setInstance(INSTANCE_EDEFAULT);
        return;
      case EcorePackage.EENUM_LITERAL__LITERAL:
        setLiteral(LITERAL_EDEFAULT);
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
      case EcorePackage.EENUM_LITERAL__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EENUM_LITERAL__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.EENUM_LITERAL__VALUE:
        return value != VALUE_EDEFAULT;
      case EcorePackage.EENUM_LITERAL__INSTANCE:
        return INSTANCE_EDEFAULT == null ? instance != null : !INSTANCE_EDEFAULT.equals(instance);
      case EcorePackage.EENUM_LITERAL__LITERAL:
        return LITERAL_EDEFAULT == null ? literal != null : !LITERAL_EDEFAULT.equals(literal);
      case EcorePackage.EENUM_LITERAL__EENUM:
        return getEEnum() != null;
    }
    return eDynamicIsSet(featureID);
  }

  @Override
  public String toString()
  {
    return getLiteral();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toStringGen()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (value: ");
    result.append(value);
    result.append(", instance: ");
    result.append(instance);
    result.append(", literal: ");
    result.append(literal);
    result.append(')');
    return result.toString();
  }

}
