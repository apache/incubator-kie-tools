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
package org.eclipse.emf.ecore;



/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EAttribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EAttribute#isID <em>ID</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EAttribute#getEAttributeType <em>EAttribute Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEAttribute()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='ConsistentTransient'"
 * @generated
 */
public interface EAttribute extends EStructuralFeature
{
  /**
   * Returns the value of the '<em><b>ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * An ID attribute explicitly models 
   * the one unique {@link org.eclipse.emf.ecore.EClass#getEIDAttribute ID} of an object.
   * </p>
   * @see org.eclipse.emf.ecore.EClass#getEIDAttribute()
   * @ignore
   * <!-- end-user-doc -->
   * @return the value of the '<em>ID</em>' attribute.
   * @see #setID(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEAttribute_ID()
   * @model
   * @generated
   */
  boolean isID();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EAttribute#isID <em>ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>ID</em>' attribute.
   * @see #isID()
   * @generated
   */
  void setID(boolean value);

  /**
   * Returns the value of the '<em><b>EAttribute Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * The {@link #getEType() type} of an attribute must always be a data type; this method provides access to it.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAttribute Type</em>' reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEAttribute_EAttributeType()
   * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EDataType getEAttributeType();

}
