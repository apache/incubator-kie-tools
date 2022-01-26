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
 * A representation of the model object '<em><b>ETyped Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#isOrdered <em>Ordered</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#isUnique <em>Unique</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#getLowerBound <em>Lower Bound</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#getUpperBound <em>Upper Bound</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#isMany <em>Many</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#getEType <em>EType</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.ETypedElement#getEGenericType <em>EGeneric Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement()
 * @model abstract="true"
 *        annotation="http://www.eclipse.org/emf/2002/Ecore constraints='ValidLowerBound ValidUpperBound ConsistentBounds ValidType'"
 * @generated
 */
public interface ETypedElement extends ENamedElement
{
  /**
   * A value indicating that there is no {@link #getUpperBound upper bound}.
   * @see #getUpperBound()
   */
  int UNBOUNDED_MULTIPLICITY = -1;

  /**
   * A value indicating that there is an unspecified {@link #getUpperBound upper bound}.
   * @see #getUpperBound()
   */
  int UNSPECIFIED_MULTIPLICITY = -2;

  /**
   * Returns the value of the '<em><b>Ordered</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * It represents whether order is meaningful.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ordered</em>' attribute.
   * @see #setOrdered(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_Ordered()
   * @model default="true"
   * @generated
   */
  boolean isOrdered();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.ETypedElement#isOrdered <em>Ordered</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ordered</em>' attribute.
   * @see #isOrdered()
   * @generated
   */
  void setOrdered(boolean value);

  /**
   * Returns the value of the '<em><b>Unique</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * It represents whether values must be unique.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unique</em>' attribute.
   * @see #setUnique(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_Unique()
   * @model default="true"
   * @generated
   */
  boolean isUnique();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.ETypedElement#isUnique <em>Unique</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Unique</em>' attribute.
   * @see #isUnique()
   * @generated
   */
  void setUnique(boolean value);

  /**
   * Returns the value of the '<em><b>Lower Bound</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the minimum number of values that must appear in a valid instance.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lower Bound</em>' attribute.
   * @see #setLowerBound(int)
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_LowerBound()
   * @model
   * @generated
   */
  int getLowerBound();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.ETypedElement#getLowerBound <em>Lower Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Lower Bound</em>' attribute.
   * @see #getLowerBound()
   * @generated
   */
  void setLowerBound(int value);

  /**
   * Returns the value of the '<em><b>Upper Bound</b></em>' attribute.
   * The default value is <code>"1"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the maximum number of values that must appear in a valid instance;
   * the value <code>-1</code>, i.e., (@link #UNBOUNDED_MULTIPLICITY}}, represents unbounded,
   * and the value <code>-2</code>, i.e., (@link #UNSPECIFIED_MULTIPLICITY}}, represents unspecified.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Upper Bound</em>' attribute.
   * @see #setUpperBound(int)
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_UpperBound()
   * @model default="1"
   * @generated
   */
  int getUpperBound();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.ETypedElement#getUpperBound <em>Upper Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Upper Bound</em>' attribute.
   * @see #getUpperBound()
   * @generated
   */
  void setUpperBound(int value);

  /**
   * Returns the value of the '<em><b>Many</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents whether more than one value may appear in a valid instance.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Many</em>' attribute.
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_Many()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  boolean isMany();

  /**
   * Returns the value of the '<em><b>Required</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents whether at least one value is required for a valid instance.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Required</em>' attribute.
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_Required()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  boolean isRequired();

  /**
   * Returns the value of the '<em><b>EType</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the type of the element
   * and is derived from the {@link #getEType() generic type}.
   * </p>
   * @see #getEGenericType()
   * <!-- end-user-doc -->
   * @return the value of the '<em>EType</em>' reference.
   * @see #setEType(EClassifier)
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_EType()
   * @model unsettable="true" volatile="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  EClassifier getEType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.ETypedElement#getEType <em>EType</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EType</em>' reference.
   * @see #getEType()
   * @generated
   */
  void setEType(EClassifier value);

  /**
   * Returns the value of the '<em><b>EGeneric Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the generic type of the element.
   * The {@link #getEType() type} is derived from this, i.e., it represents the {@link EGenericType#getERawType() erasure} of the generic type.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EGeneric Type</em>' containment reference.
   * @see #setEGenericType(EGenericType)
   * @see org.eclipse.emf.ecore.EcorePackage#getETypedElement_EGenericType()
   * @model containment="true" unsettable="true" volatile="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  EGenericType getEGenericType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.ETypedElement#getEGenericType <em>EGeneric Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EGeneric Type</em>' containment reference.
   * @see #getEGenericType()
   * @generated
   */
  void setEGenericType(EGenericType value);

} //ETypedElement
