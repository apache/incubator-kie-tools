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

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EClassifier</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EClassifier#getInstanceClassName <em>Instance Class Name</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClassifier#getInstanceClass <em>Instance Class</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClassifier#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClassifier#getInstanceTypeName <em>Instance Type Name</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClassifier#getEPackage <em>EPackage</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClassifier#getETypeParameters <em>EType Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEClassifier()
 * @model abstract="true"
 *        annotation="http://www.eclipse.org/emf/2002/Ecore constraints='WellFormedInstanceTypeName UniqueTypeParameterNames'"
 * @generated
 */
public interface EClassifier extends ENamedElement
{
  /**
   * Returns the value of the '<em><b>Instance Class Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the name of the Java instance class that this meta object represents.
   * </p>
   * @see #getInstanceClass()
   * @ignore
   * <!-- end-user-doc -->
   * @return the value of the '<em>Instance Class Name</em>' attribute.
   * @see #setInstanceClassName(String)
   * @see org.eclipse.emf.ecore.EcorePackage#getEClassifier_InstanceClassName()
   * @model unsettable="true" volatile="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  String getInstanceClassName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EClassifier#getInstanceClassName <em>Instance Class Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Instance Class Name</em>' attribute.
   * @see #getInstanceClassName()
   * @generated
   */
  void setInstanceClassName(String value);

  /**
   * Returns the value of the '<em><b>Instance Class</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the actual Java instance class that this meta object represents.
   * </p>
   * @see #setInstanceClass(Class)
   * @ignore
   * <!-- end-user-doc -->
   * @return the value of the '<em>Instance Class</em>' attribute.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClassifier_InstanceClass()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Class<?> getInstanceClass();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EClassifier#getInstanceClass <em>Instance Class</em>}' attribute.
   * @param value the new value of the '<em>Instance Class</em>' attribute.
   * @see #getInstanceClass()
   */
  void setInstanceClass(Class<?> value);

  /**
   * Returns the value of the '<em><b>Default Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * The default value for the type, in the Java sense.
   * For primitive types, it will be the Java primitive default wrapped appropriately.
   * For {@link EEnum enums}, it will be the first enumerator.
   * And for all other types derived from <code>java.lang.Object</code>, it will be null.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Value</em>' attribute.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClassifier_DefaultValue()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Object getDefaultValue();

  /**
   * Returns the value of the '<em><b>Instance Type Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the parameterized Java type that this meta object represents.
   * </p>
   * @since 2.3
   * <!-- end-user-doc -->
   * @return the value of the '<em>Instance Type Name</em>' attribute.
   * @see #setInstanceTypeName(String)
   * @see org.eclipse.emf.ecore.EcorePackage#getEClassifier_InstanceTypeName()
   * @model unsettable="true" volatile="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  String getInstanceTypeName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EClassifier#getInstanceTypeName <em>Instance Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Instance Type Name</em>' attribute.
   * @see #getInstanceTypeName()
   * @generated
   */
  void setInstanceTypeName(String value);

  /**
   * Returns the value of the '<em><b>EPackage</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EPackage#getEClassifiers <em>EClassifiers</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>EPackage</em>' container reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClassifier_EPackage()
   * @see org.eclipse.emf.ecore.EPackage#getEClassifiers
   * @model opposite="eClassifiers" changeable="false"
   * @generated
   */
  EPackage getEPackage();

  /**
   * Returns the value of the '<em><b>EType Parameters</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.ETypeParameter}.
   * <!-- begin-user-doc -->
   * <p>
   * A classifier can optionally introduce type parameters.
   * </p>
   * @since 2.3
   * <!-- end-user-doc -->
   * @return the value of the '<em>EType Parameters</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClassifier_ETypeParameters()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<ETypeParameter> getETypeParameters();

  /**
   * <!-- begin-user-doc -->
   * Returns whether the object is an instance of this classifier.
   * @param object the object in question.
   * @return whether the object is an instance.
   * @see Class#isInstance
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean isInstance(Object object);

  /**
   * <!-- begin-user-doc -->
   * Returns the ID relative to the {@link #getEPackage containing} package.
   * @return  the relative ID.
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  int getClassifierID();

}
