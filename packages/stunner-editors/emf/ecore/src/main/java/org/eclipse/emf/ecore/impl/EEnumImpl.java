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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EEnum</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EEnumImpl#getELiterals <em>ELiterals</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EEnumImpl extends EDataTypeImpl implements EEnum
{
  /**
   * The cached value of the '{@link #getELiterals() <em>ELiterals</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getELiterals()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EEnumLiteral> eLiterals;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EEnumImpl()
  {
    super();
  }

  @Override
  protected void freeze()
  {
    if (eLiterals != null)
    {
      for (int i = 0, size = eLiterals.size(); i < size; ++i)
      {
        freeze(eLiterals.get(i));
      }
    }
    super.freeze();
  }

  @Override
  protected void setDataTypeGeneratedInstanceClass(boolean isGenerated)
  {
    // Do nothing.
  }

  @Override
  public Object getDefaultValue()
  {
    EList<EEnumLiteral> eLiterals = getELiterals();
    if (!eLiterals.isEmpty())
    {
      return eLiterals.get(0).getInstance();
    }
    return null;
  }

  /**
   * Determines if the specified Object is an instance of this.
   */
  @Override
  public boolean isInstance(Object object)
  {
    if (object != null)
    {
      // TODO
      return true;
      /*
      Class<?> instanceClass = getInstanceClass();
      if (instanceClass != null)
      {
        return instanceClass.isInstance(object);
      }
      else
      {
        return getELiterals().contains(object);
      }
      */
    }
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.EENUM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EEnumLiteral> getELiterals()
  {
    if (eLiterals == null)
    {
      eLiterals = new EObjectContainmentWithInverseEList<EEnumLiteral>(EEnumLiteral.class, this, EcorePackage.EENUM__ELITERALS, EcorePackage.EENUM_LITERAL__EENUM);
    }
    return eLiterals;
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
      case EcorePackage.EENUM__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EENUM__NAME:
        return getName();
      case EcorePackage.EENUM__INSTANCE_CLASS_NAME:
        return getInstanceClassName();
      case EcorePackage.EENUM__INSTANCE_CLASS:
        return getInstanceClass();
      case EcorePackage.EENUM__DEFAULT_VALUE:
        return getDefaultValue();
      case EcorePackage.EENUM__INSTANCE_TYPE_NAME:
        return getInstanceTypeName();
      case EcorePackage.EENUM__EPACKAGE:
        if (resolve) return getEPackage();
        return basicGetEPackage();
      case EcorePackage.EENUM__ETYPE_PARAMETERS:
        return getETypeParameters();
      case EcorePackage.EENUM__SERIALIZABLE:
        return isSerializable();
      case EcorePackage.EENUM__ELITERALS:
        return getELiterals();
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
      case EcorePackage.EENUM__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.EENUM__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.EENUM__INSTANCE_CLASS_NAME:
        setInstanceClassName((String)newValue);
        return;
      case EcorePackage.EENUM__INSTANCE_TYPE_NAME:
        setInstanceTypeName((String)newValue);
        return;
      case EcorePackage.EENUM__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        getETypeParameters().addAll((Collection<? extends ETypeParameter>)newValue);
        return;
      case EcorePackage.EENUM__SERIALIZABLE:
        setSerializable((Boolean)newValue);
        return;
      case EcorePackage.EENUM__ELITERALS:
        getELiterals().clear();
        getELiterals().addAll((Collection<? extends EEnumLiteral>)newValue);
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
      case EcorePackage.EENUM__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.EENUM__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.EENUM__INSTANCE_CLASS_NAME:
        unsetInstanceClassName();
        return;
      case EcorePackage.EENUM__INSTANCE_TYPE_NAME:
        unsetInstanceTypeName();
        return;
      case EcorePackage.EENUM__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        return;
      case EcorePackage.EENUM__SERIALIZABLE:
        setSerializable(SERIALIZABLE_EDEFAULT);
        return;
      case EcorePackage.EENUM__ELITERALS:
        getELiterals().clear();
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
      case EcorePackage.EENUM__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EENUM__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.EENUM__INSTANCE_CLASS_NAME:
        return isSetInstanceClassName();
      case EcorePackage.EENUM__INSTANCE_CLASS:
        return getInstanceClass() != null;
      case EcorePackage.EENUM__DEFAULT_VALUE:
        return DEFAULT_VALUE_EDEFAULT == null ? getDefaultValue() != null : !DEFAULT_VALUE_EDEFAULT.equals(getDefaultValue());
      case EcorePackage.EENUM__INSTANCE_TYPE_NAME:
        return isSetInstanceTypeName();
      case EcorePackage.EENUM__EPACKAGE:
        return basicGetEPackage() != null;
      case EcorePackage.EENUM__ETYPE_PARAMETERS:
        return eTypeParameters != null && !eTypeParameters.isEmpty();
      case EcorePackage.EENUM__SERIALIZABLE:
        return ((eFlags & SERIALIZABLE_EFLAG) != 0) != SERIALIZABLE_EDEFAULT;
      case EcorePackage.EENUM__ELITERALS:
        return eLiterals != null && !eLiterals.isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
      case EcorePackage.EENUM___GET_EANNOTATION__STRING:
        return getEAnnotation((String)arguments.get(0));
      case EcorePackage.EENUM___IS_INSTANCE__OBJECT:
        return isInstance(arguments.get(0));
      case EcorePackage.EENUM___GET_CLASSIFIER_ID:
        return getClassifierID();
      case EcorePackage.EENUM___GET_EENUM_LITERAL__STRING:
        return getEEnumLiteral((String)arguments.get(0));
      case EcorePackage.EENUM___GET_EENUM_LITERAL__INT:
        return getEEnumLiteral((Integer)arguments.get(0));
      case EcorePackage.EENUM___GET_EENUM_LITERAL_BY_LITERAL__STRING:
        return getEEnumLiteralByLiteral((String)arguments.get(0));
    }
    return eDynamicInvoke(operationID, arguments);
  }

  /**
   * @generated NOT
   */
  public EEnumLiteral getEEnumLiteral(String name)
  {
    if (name == null)
    {
      for (EEnumLiteral eEnumLiteral : getELiterals())
      {
        if (eEnumLiteral.getName() == null)
        {
          return eEnumLiteral;
        }
      }
    }
    else
    {
      for (EEnumLiteral eEnumLiteral : getELiterals())
      {
        if (name.equals(eEnumLiteral.getName()))
        {
          return eEnumLiteral;
        }
      }
    }
    return null;
  }

  /**
   * @generated NOT
   */
  public EEnumLiteral getEEnumLiteral(int intValue)
  {
    for (EEnumLiteral eEnumLiteral : getELiterals())
    {
      if (eEnumLiteral.getValue() == intValue)
      {
        return eEnumLiteral;
      }
    }
    return null;
  }

  /**
   * @generated NOT
   */
  public EEnumLiteral getEEnumLiteralByLiteral(String literal)
  {
    if (literal == null)
    {
      for (EEnumLiteral eEnumLiteral : getELiterals())
      {
        if (eEnumLiteral.getLiteral() == null)
        {
          return eEnumLiteral;
        }
      }
    }
    else
    {
      for (EEnumLiteral eEnumLiteral : getELiterals())
      {
        if (literal.equals(eEnumLiteral.getLiteral()))
        {
          return eEnumLiteral;
        }
      }
    }
    return null;
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
      case EcorePackage.EENUM__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.EENUM__EPACKAGE:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, EcorePackage.EENUM__EPACKAGE, msgs);
      case EcorePackage.EENUM__ELITERALS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getELiterals()).basicAdd(otherEnd, msgs);
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
      case EcorePackage.EENUM__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.EENUM__EPACKAGE:
        return eBasicSetContainer(null, EcorePackage.EENUM__EPACKAGE, msgs);
      case EcorePackage.EENUM__ETYPE_PARAMETERS:
        return ((InternalEList<?>)getETypeParameters()).basicRemove(otherEnd, msgs);
      case EcorePackage.EENUM__ELITERALS:
        return ((InternalEList<?>)getELiterals()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

}
