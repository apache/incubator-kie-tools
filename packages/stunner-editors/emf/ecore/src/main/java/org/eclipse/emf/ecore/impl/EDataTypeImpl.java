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


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;

import com.google.gwt.user.client.rpc.GwtTransient;
import org.eclipse.emf.ecore.util.EcoreUtil;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EData Type</b></em>'.
 * @extends EDataType.Internal
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EDataTypeImpl#isSerializable <em>Serializable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EDataTypeImpl extends EClassifierImpl implements EDataType, EDataType.Internal
{
  /**
   * The default value of the '{@link #isSerializable() <em>Serializable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSerializable()
   * @generated
   * @ordered
   */
  protected static final boolean SERIALIZABLE_EDEFAULT = true;

  /**
   * The flag representing the value of the '{@link #isSerializable() <em>Serializable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSerializable()
   * @generated
   * @ordered
   */
  protected static final int SERIALIZABLE_EFLAG = 1 << 8;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EDataTypeImpl()
  {
    super();
    eFlags |= SERIALIZABLE_EFLAG;
  }

  @GwtTransient
  protected Object defaultValue = null;
  @GwtTransient
  protected boolean defaultValueIsSet = false;

  @Override
  public Object getDefaultValue()
  {
    if (!defaultValueIsSet)
    {
      Class<?> instanceClass = null;
      try
      {
        instanceClass = getInstanceClass();
      }
      catch (Exception e) 
      {
        // Continue with no instanceClass. 
      }

      defaultValue = null;
      if (instanceClass != null && instanceClass.isPrimitive())
      {
        if (instanceClass == boolean.class)
          defaultValue = Boolean.FALSE;
        else if (instanceClass == int.class)
          defaultValue = 0;
        else if (instanceClass == float.class)
          defaultValue = 0.0F;
        else if (instanceClass == double.class)
          defaultValue = 0.0;
        else if (instanceClass == long.class)
          defaultValue = 0L;
        else if (instanceClass == short.class)
          defaultValue = (short)0;
        else if (instanceClass == byte.class)
          defaultValue = (byte)0;
        else // if (instanceClass == Character.TYPE)
          defaultValue = '\u0000';
      }
      defaultValueIsSet = true;
    }
    return defaultValue;
  }

  @Override
  public void setInstanceClassGen(Class<?> instanceClass)
  {
    super.setInstanceClassGen(instanceClass);
    defaultValueIsSet = false;
  }

  @Override
  public void setGeneratedInstanceClass(boolean isGenerated)
  {
    super.setGeneratedInstanceClass(isGenerated);

    setDataTypeGeneratedInstanceClass(isGenerated);

  }

  protected void setDataTypeGeneratedInstanceClass(boolean isGenerated)
  {
    // EEnumImpl overrides this to do nothing.
    // The only case that is special is when a more specific instance of AbstractEnumerator is generated/reused.
    //
    if (isGenerated)
    {
      instanceClassName = "org.eclipse.emf.common.util.AbstractEnumerator";
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.EDATA_TYPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSerializable()
  {
    return (eFlags & SERIALIZABLE_EFLAG) != 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSerializable(boolean newSerializable)
  {
    boolean oldSerializable = (eFlags & SERIALIZABLE_EFLAG) != 0;
    if (newSerializable) eFlags |= SERIALIZABLE_EFLAG; else eFlags &= ~SERIALIZABLE_EFLAG;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EDATA_TYPE__SERIALIZABLE, oldSerializable, newSerializable));
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
      case EcorePackage.EDATA_TYPE__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EDATA_TYPE__NAME:
        return getName();
      case EcorePackage.EDATA_TYPE__INSTANCE_CLASS_NAME:
        return getInstanceClassName();
      case EcorePackage.EDATA_TYPE__INSTANCE_CLASS:
        return getInstanceClass();
      case EcorePackage.EDATA_TYPE__DEFAULT_VALUE:
        return getDefaultValue();
      case EcorePackage.EDATA_TYPE__INSTANCE_TYPE_NAME:
        return getInstanceTypeName();
      case EcorePackage.EDATA_TYPE__EPACKAGE:
        if (resolve) return getEPackage();
        return basicGetEPackage();
      case EcorePackage.EDATA_TYPE__ETYPE_PARAMETERS:
        return getETypeParameters();
      case EcorePackage.EDATA_TYPE__SERIALIZABLE:
        return isSerializable();
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
      case EcorePackage.EDATA_TYPE__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.EDATA_TYPE__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.EDATA_TYPE__INSTANCE_CLASS_NAME:
        setInstanceClassName((String)newValue);
        return;
      case EcorePackage.EDATA_TYPE__INSTANCE_TYPE_NAME:
        setInstanceTypeName((String)newValue);
        return;
      case EcorePackage.EDATA_TYPE__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        getETypeParameters().addAll((Collection<? extends ETypeParameter>)newValue);
        return;
      case EcorePackage.EDATA_TYPE__SERIALIZABLE:
        setSerializable((Boolean)newValue);
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
      case EcorePackage.EDATA_TYPE__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.EDATA_TYPE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.EDATA_TYPE__INSTANCE_CLASS_NAME:
        unsetInstanceClassName();
        return;
      case EcorePackage.EDATA_TYPE__INSTANCE_TYPE_NAME:
        unsetInstanceTypeName();
        return;
      case EcorePackage.EDATA_TYPE__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        return;
      case EcorePackage.EDATA_TYPE__SERIALIZABLE:
        setSerializable(SERIALIZABLE_EDEFAULT);
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
      case EcorePackage.EDATA_TYPE__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EDATA_TYPE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.EDATA_TYPE__INSTANCE_CLASS_NAME:
        return isSetInstanceClassName();
      case EcorePackage.EDATA_TYPE__INSTANCE_CLASS:
        return getInstanceClass() != null;
      case EcorePackage.EDATA_TYPE__DEFAULT_VALUE:
        return DEFAULT_VALUE_EDEFAULT == null ? getDefaultValue() != null : !DEFAULT_VALUE_EDEFAULT.equals(getDefaultValue());
      case EcorePackage.EDATA_TYPE__INSTANCE_TYPE_NAME:
        return isSetInstanceTypeName();
      case EcorePackage.EDATA_TYPE__EPACKAGE:
        return basicGetEPackage() != null;
      case EcorePackage.EDATA_TYPE__ETYPE_PARAMETERS:
        return eTypeParameters != null && !eTypeParameters.isEmpty();
      case EcorePackage.EDATA_TYPE__SERIALIZABLE:
        return ((eFlags & SERIALIZABLE_EFLAG) != 0) != SERIALIZABLE_EDEFAULT;
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
    result.append(" (serializable: ");
    result.append((eFlags & SERIALIZABLE_EFLAG) != 0);
    result.append(')');
    return result.toString();
  }

  protected ConversionDelegate conversionDelegate;
  protected boolean conversionDelegateIsSet;

  public ConversionDelegate getConversionDelegate()
  {
    if (!conversionDelegateIsSet)
    {
      List<String> conversionDelegates = EcoreUtil.getConversionDelegates(getEPackage());
      if (!conversionDelegates.isEmpty())
      {
        for (String eDataTypeDelegateUri : conversionDelegates)
        {
          if (this.getEAnnotation(eDataTypeDelegateUri) != null)
          {
            EDataType.Internal.ConversionDelegate.Factory eDataTypeDelegateFactory = EcoreUtil.getConversionDelegateFactory(this);
            conversionDelegate = eDataTypeDelegateFactory.createConversionDelegate(this);
            conversionDelegateIsSet = true;
          }
        }
      }
    }
    return conversionDelegate;
  }

  public void setConversionDelegate(ConversionDelegate conversionDelegate)
  {
    this.conversionDelegate = conversionDelegate;
    conversionDelegateIsSet = conversionDelegate != null;
  }

}
