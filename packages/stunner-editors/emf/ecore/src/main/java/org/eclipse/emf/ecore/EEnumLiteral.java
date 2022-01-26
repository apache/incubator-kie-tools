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


import org.eclipse.emf.common.util.Enumerator;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EEnum Literal</b></em>'.
 * @extends Enumerator
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EEnumLiteral#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EEnumLiteral#getInstance <em>Instance</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EEnumLiteral#getLiteral <em>Literal</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EEnumLiteral#getEEnum <em>EEnum</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEEnumLiteral()
 * @model
 * @generated
 */
public interface EEnumLiteral extends ENamedElement, Enumerator
{
  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the <code>int</code> value of an enumerator.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(int)
   * @see org.eclipse.emf.ecore.EcorePackage#getEEnumLiteral_Value()
   * @model
   * @generated
   */
  int getValue();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EEnumLiteral#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(int value);

  /**
   * Returns the value of the '<em><b>Instance</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the actual Java instance value. 
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Instance</em>' attribute.
   * @see #setInstance(Enumerator)
   * @see org.eclipse.emf.ecore.EcorePackage#getEEnumLiteral_Instance()
   * @model transient="true"
   * @generated
   */
  Enumerator getInstance();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EEnumLiteral#getInstance <em>Instance</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Instance</em>' attribute.
   * @see #getInstance()
   * @generated
   */
  void setInstance(Enumerator value);

  /**
   * Returns the value of the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * This is the literal, string value that represents this enumerator value.
   * This is used in persisting instances of enumerated type.
   * If set to <code>null<code>, it will return the {@link ENamedElement#getName name}, instead.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Literal</em>' attribute.
   * @see #setLiteral(String)
   * @see org.eclipse.emf.ecore.EcorePackage#getEEnumLiteral_Literal()
   * @model
   * @generated
   */
  String getLiteral();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EEnumLiteral#getLiteral <em>Literal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Literal</em>' attribute.
   * @see #getLiteral()
   * @generated
   */
  void setLiteral(String value);

  /**
   * Returns the value of the '<em><b>EEnum</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EEnum#getELiterals <em>ELiterals</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the contain enumeration.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EEnum</em>' container reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEEnumLiteral_EEnum()
   * @see org.eclipse.emf.ecore.EEnum#getELiterals
   * @model opposite="eLiterals" resolveProxies="false" changeable="false"
   * @generated
   */
  EEnum getEEnum();

}
