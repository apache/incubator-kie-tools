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
package org.eclipse.emf.ecore.xml.type;


import org.eclipse.emf.ecore.EDataType;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Simple Any Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.SimpleAnyType#getRawValue <em>Raw Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.SimpleAnyType#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.SimpleAnyType#getInstanceType <em>Instance Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getSimpleAnyType()
 * @model extendedMetaData="name='simpleAnyType' kind='simple'"
 * @generated
 */
public interface SimpleAnyType extends AnyType
{
  /**
   * Returns the value of the '<em><b>Raw Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Raw Value</em>' attribute.
   * @see #setRawValue(String)
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getSimpleAnyType_RawValue()
   * @model dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
   *        extendedMetaData="name=':3' kind='simple'"
   * @generated
   */
  String getRawValue();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.type.SimpleAnyType#getRawValue <em>Raw Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Raw Value</em>' attribute.
   * @see #getRawValue()
   * @generated
   */
  void setRawValue(String value);

  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(Object)
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getSimpleAnyType_Value()
   * @model dataType="org.eclipse.emf.ecore.xml.type.AnySimpleType" transient="true" volatile="true" derived="true"
   *        extendedMetaData="name=':4' kind='simple'"
   * @generated
   */
  Object getValue();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.type.SimpleAnyType#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(Object value);

  /**
   * Returns the value of the '<em><b>Instance Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Instance Type</em>' reference.
   * @see #setInstanceType(EDataType)
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getSimpleAnyType_InstanceType()
   * @model resolveProxies="false" required="true"
   *        extendedMetaData="name=':5' kind='simple'"
   * @generated
   */
  EDataType getInstanceType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.type.SimpleAnyType#getInstanceType <em>Instance Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Instance Type</em>' reference.
   * @see #getInstanceType()
   * @generated
   */
  void setInstanceType(EDataType value);

} // SimpleAnyType
