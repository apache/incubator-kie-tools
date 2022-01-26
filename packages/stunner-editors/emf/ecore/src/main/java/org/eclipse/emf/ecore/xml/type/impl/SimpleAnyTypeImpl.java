/**
 * Copyright (c) 2003-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.xml.type.impl;

import com.google.gwt.user.client.rpc.GwtTransient;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xml.type.SimpleAnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Simple Any Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.impl.SimpleAnyTypeImpl#getRawValue <em>Raw Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.impl.SimpleAnyTypeImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.impl.SimpleAnyTypeImpl#getInstanceType <em>Instance Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SimpleAnyTypeImpl extends AnyTypeImpl implements SimpleAnyType
{
  /**
   * The default value of the '{@link #getRawValue() <em>Raw Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRawValue()
   * @generated
   * @ordered
   */
  protected static final String RAW_VALUE_EDEFAULT = null;

  /**
   * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected static final Object VALUE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getInstanceType() <em>Instance Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstanceType()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EDataType instanceType;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SimpleAnyTypeImpl()
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
    return XMLTypePackage.Literals.SIMPLE_ANY_TYPE;
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRawValue()
  {
    return (String)getMixed().get(XMLTypePackage.Literals.SIMPLE_ANY_TYPE__RAW_VALUE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRawValue(String newRawValue)
  {
    ((FeatureMap.Internal)getMixed()).set(XMLTypePackage.Literals.SIMPLE_ANY_TYPE__RAW_VALUE, newRawValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object getValue()
  {
    return EcoreUtil.createFromString(instanceType, getRawValue());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setValue(Object newValue)
  {
    setRawValue(EcoreUtil.convertToString(instanceType, newValue));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getInstanceType()
  {
    return instanceType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInstanceType(EDataType newInstanceType)
  {
    EDataType oldInstanceType = instanceType;
    instanceType = newInstanceType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, XMLTypePackage.SIMPLE_ANY_TYPE__INSTANCE_TYPE, oldInstanceType, instanceType));
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
      case XMLTypePackage.SIMPLE_ANY_TYPE__MIXED:
        if (coreType) return getMixed();
        return ((FeatureMap.Internal)getMixed()).getWrapper();
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY:
        if (coreType) return getAny();
        return ((FeatureMap.Internal)getAny()).getWrapper();
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY_ATTRIBUTE:
        if (coreType) return getAnyAttribute();
        return ((FeatureMap.Internal)getAnyAttribute()).getWrapper();
      case XMLTypePackage.SIMPLE_ANY_TYPE__RAW_VALUE:
        return getRawValue();
      case XMLTypePackage.SIMPLE_ANY_TYPE__VALUE:
        return getValue();
      case XMLTypePackage.SIMPLE_ANY_TYPE__INSTANCE_TYPE:
        return getInstanceType();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case XMLTypePackage.SIMPLE_ANY_TYPE__MIXED:
        ((FeatureMap.Internal)getMixed()).set(newValue);
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY:
        ((FeatureMap.Internal)getAny()).set(newValue);
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY_ATTRIBUTE:
        ((FeatureMap.Internal)getAnyAttribute()).set(newValue);
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__RAW_VALUE:
        setRawValue((String)newValue);
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__VALUE:
        setValue(newValue);
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__INSTANCE_TYPE:
        setInstanceType((EDataType)newValue);
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
      case XMLTypePackage.SIMPLE_ANY_TYPE__MIXED:
        getMixed().clear();
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY:
        getAny().clear();
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY_ATTRIBUTE:
        getAnyAttribute().clear();
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__RAW_VALUE:
        setRawValue(RAW_VALUE_EDEFAULT);
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__VALUE:
        setValue(VALUE_EDEFAULT);
        return;
      case XMLTypePackage.SIMPLE_ANY_TYPE__INSTANCE_TYPE:
        setInstanceType((EDataType)null);
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
      case XMLTypePackage.SIMPLE_ANY_TYPE__MIXED:
        return mixed != null && !mixed.isEmpty();
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY:
        return !getAny().isEmpty();
      case XMLTypePackage.SIMPLE_ANY_TYPE__ANY_ATTRIBUTE:
        return anyAttribute != null && !anyAttribute.isEmpty();
      case XMLTypePackage.SIMPLE_ANY_TYPE__RAW_VALUE:
        return RAW_VALUE_EDEFAULT == null ? getRawValue() != null : !RAW_VALUE_EDEFAULT.equals(getRawValue());
      case XMLTypePackage.SIMPLE_ANY_TYPE__VALUE:
        return VALUE_EDEFAULT == null ? getValue() != null : !VALUE_EDEFAULT.equals(getValue());
      case XMLTypePackage.SIMPLE_ANY_TYPE__INSTANCE_TYPE:
        return instanceType != null;
    }
    return eDynamicIsSet(featureID);
  }

} //SimpleAnyTypeImpl
