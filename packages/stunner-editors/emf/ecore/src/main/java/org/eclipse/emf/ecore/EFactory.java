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
 * A representation of the model object '<em><b>EFactory</b></em>'.
 * <p>
 * A factory is responsible for {@link #create creating} class instances,
 * and for converting data type instances {@link #convertToString to} and {@link #createFromString from} <code>String</code>.
 * <p>
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EFactory#getEPackage <em>EPackage</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEFactory()
 * @model
 * @generated
 */
public interface EFactory extends EModelElement
{
  /**
   * Returns the value of the '<em><b>EPackage</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EPackage#getEFactoryInstance <em>EFactory Instance</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represent the package of this factory.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EPackage</em>' reference.
   * @see #setEPackage(EPackage)
   * @see org.eclipse.emf.ecore.EcorePackage#getEFactory_EPackage()
   * @see org.eclipse.emf.ecore.EPackage#getEFactoryInstance
   * @model opposite="eFactoryInstance" resolveProxies="false" required="true" transient="true"
   * @generated
   */
  EPackage getEPackage();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EFactory#getEPackage <em>EPackage</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EPackage</em>' reference.
   * @see #getEPackage()
   * @generated
   */
  void setEPackage(EPackage value);

  /**
   * <!-- begin-user-doc -->
   * Creates a new instance of the class and returns it.
   * @param eClass the class of the new instance.
   * @return a new instance of the class.
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EObject create(EClass eClass);

  /**
   * <!-- begin-user-doc -->
   * Creates an instance of the data type from the literal value.
   * @param literalValue literal value of the instance.
   * @return an instance of the data type.
   * @see #convertToString(EDataType, Object)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  Object createFromString(EDataType eDataType, String literalValue);

  /**
   * <!-- begin-user-doc -->
   * Returns the literal value of the instance.
   * @param eDataType the data type of the value.
   * @param instanceValue the value.
   * @return the literal value of the instance.
   * @see #createFromString(EDataType, String)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  String convertToString(EDataType eDataType, Object instanceValue);

}
