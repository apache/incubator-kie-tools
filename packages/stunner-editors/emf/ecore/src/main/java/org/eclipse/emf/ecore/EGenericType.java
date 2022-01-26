/**
 * Copyright (c) 2006-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGeneric Type</b></em>'.
 * A generic type is analogous to Java 5.0 {@link java.lang.reflect.Type Type}.
 * It can represent a reference to a type parameter, 
 * a reference to a classifier, along with optional type arguments if the classifier specify type parameters,
 * or a wildcard with an optional upper or lower bound.
 * @since 2.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EGenericType#getEUpperBound <em>EUpper Bound</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EGenericType#getETypeArguments <em>EType Arguments</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EGenericType#getERawType <em>ERaw Type</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EGenericType#getELowerBound <em>ELower Bound</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EGenericType#getETypeParameter <em>EType Parameter</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EGenericType#getEClassifier <em>EClassifier</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEGenericType()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='ConsistentType ConsistentBounds ConsistentArguments'"
 * @generated
 */
public interface EGenericType extends EObject
{
  /**
   * Returns the value of the '<em><b>EUpper Bound</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * A generic type with an upper bound T, is equivalent to "? extends T" in Java.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EUpper Bound</em>' containment reference.
   * @see #setEUpperBound(EGenericType)
   * @see org.eclipse.emf.ecore.EcorePackage#getEGenericType_EUpperBound()
   * @model containment="true"
   * @generated
   */
  EGenericType getEUpperBound();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EGenericType#getEUpperBound <em>EUpper Bound</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EUpper Bound</em>' containment reference.
   * @see #getEUpperBound()
   * @generated
   */
  void setEUpperBound(EGenericType value);

  /**
   * Returns the value of the '<em><b>EType Arguments</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EGenericType}.
   * <!-- begin-user-doc -->
   * <p>
   * These represent the template arguments applied to a classifier with type parameters, e.g., Map&lt;T>.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EType Arguments</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEGenericType_ETypeArguments()
   * @model containment="true"
   * @generated
   */
  EList<EGenericType> getETypeArguments();

  /**
   * Returns the value of the '<em><b>ERaw Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * This represents the erased or raw type of the generic type.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>ERaw Type</em>' reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEGenericType_ERawType()
   * @model required="true" transient="true" changeable="false" derived="true"
   * @generated
   */
  EClassifier getERawType();

  /**
   * Returns the value of the '<em><b>ELower Bound</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * A generic type with a lower bound T, is equivalent to "? super T" in Java.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>ELower Bound</em>' containment reference.
   * @see #setELowerBound(EGenericType)
   * @see org.eclipse.emf.ecore.EcorePackage#getEGenericType_ELowerBound()
   * @model containment="true"
   * @generated
   */
  EGenericType getELowerBound();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EGenericType#getELowerBound <em>ELower Bound</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>ELower Bound</em>' containment reference.
   * @see #getELowerBound()
   * @generated
   */
  void setELowerBound(EGenericType value);

  /**
   * Returns the value of the '<em><b>EType Parameter</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * The represents a reference to a type parameter.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EType Parameter</em>' reference.
   * @see #setETypeParameter(ETypeParameter)
   * @see org.eclipse.emf.ecore.EcorePackage#getEGenericType_ETypeParameter()
   * @model resolveProxies="false"
   * @generated
   */
  ETypeParameter getETypeParameter();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EGenericType#getETypeParameter <em>EType Parameter</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EType Parameter</em>' reference.
   * @see #getETypeParameter()
   * @generated
   */
  void setETypeParameter(ETypeParameter value);

  /**
   * Returns the value of the '<em><b>EClassifier</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * The represents a reference to a classifier.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EClassifier</em>' reference.
   * @see #setEClassifier(EClassifier)
   * @see org.eclipse.emf.ecore.EcorePackage#getEGenericType_EClassifier()
   * @model
   * @generated
   */
  EClassifier getEClassifier();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EGenericType#getEClassifier <em>EClassifier</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EClassifier</em>' reference.
   * @see #getEClassifier()
   * @generated
   */
  void setEClassifier(EClassifier value);

} // EGenericType
