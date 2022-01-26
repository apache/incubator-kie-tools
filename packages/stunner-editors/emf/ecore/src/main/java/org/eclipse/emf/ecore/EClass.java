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
 * A representation of the model object '<em><b>EClass</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EClass#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#isInterface <em>Interface</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getESuperTypes <em>ESuper Types</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEOperations <em>EOperations</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAllAttributes <em>EAll Attributes</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAllReferences <em>EAll References</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEReferences <em>EReferences</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAttributes <em>EAttributes</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAllContainments <em>EAll Containments</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAllOperations <em>EAll Operations</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAllStructuralFeatures <em>EAll Structural Features</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAllSuperTypes <em>EAll Super Types</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEIDAttribute <em>EID Attribute</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEStructuralFeatures <em>EStructural Features</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEGenericSuperTypes <em>EGeneric Super Types</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EClass#getEAllGenericSuperTypes <em>EAll Generic Super Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEClass()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='InterfaceIsAbstract AtMostOneID UniqueFeatureNames UniqueOperationSignatures NoCircularSuperTypes WellFormedMapEntryClass ConsistentSuperTypes DisjointFeatureAndOperationSignatures'"
 * @generated
 */
public interface EClass extends EClassifier
{
  /**
   * Returns the value of the '<em><b>Abstract</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents whether the class is abstract in the Java sense.
   * For an abstract class,
   * the generated implementation class will be abstract,
   * and the generated factory will not provide support for creating an instance.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Abstract</em>' attribute.
   * @see #setAbstract(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_Abstract()
   * @model
   * @generated
   */
  boolean isAbstract();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EClass#isAbstract <em>Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Abstract</em>' attribute.
   * @see #isAbstract()
   * @generated
   */
  void setAbstract(boolean value);

  /**
   * Returns the value of the '<em><b>Interface</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents whether the class is an interface in the Java sense.
   * For an interface class,
   * there will be no generated implementation class.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Interface</em>' attribute.
   * @see #setInterface(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_Interface()
   * @model
   * @generated
   */
  boolean isInterface();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EClass#isInterface <em>Interface</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Interface</em>' attribute.
   * @see #isInterface()
   * @generated
   */
  void setInterface(boolean value);

  /**
   * Returns the value of the '<em><b>ESuper Types</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EClass}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the super types in the Java sense, i.e., the super class and the direct implemented interfaces,
   * and is derived from the {@link #getEGenericSuperTypes() generic super types}.
   * </p>
   * @see #getEGenericSuperTypes()
   * @see #getEAllSuperTypes()
   * @ignore
   * <!-- end-user-doc -->
   * @return the value of the '<em>ESuper Types</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_ESuperTypes()
   * @model unsettable="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  EList<EClass> getESuperTypes();

  /**
   * Returns the value of the '<em><b>EAll Super Types</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EClass}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the closure of all inherited {@link #getESuperTypes() super types}
   * and is derived from the {@link #getEAllGenericSuperTypes() generic super types}.
   * </p>
   * @see #getESuperTypes()
   * @see #getEAllGenericSuperTypes()
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAll Super Types</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAllSuperTypes()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EClass> getEAllSuperTypes();

  /**
   * Returns the value of the '<em><b>EID Attribute</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the attribute that will be used as the ID of an instance.
   * @see org.eclipse.emf.ecore.EAttribute#isID()
   * @ignore
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EID Attribute</em>' reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EIDAttribute()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EAttribute getEIDAttribute();

  /**
   * Returns the value of the '<em><b>EStructural Features</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EStructuralFeature#getEContainingClass <em>EContaining Class</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the modeled features local to this class.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EStructural Features</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EStructuralFeatures()
   * @see org.eclipse.emf.ecore.EStructuralFeature#getEContainingClass
   * @model opposite="eContainingClass" containment="true"
   * @generated
   */
  EList<EStructuralFeature> getEStructuralFeatures();

  /**
   * Returns the value of the '<em><b>EGeneric Super Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EGenericType}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the generic super types in the Java sense, i.e., the super class and the direct implemented interfaces.
   * The {@link #getESuperTypes() super types} list is derived from this one, i.e., it represents the {@link EGenericType#getERawType() erasure} of each generic super type.
   * </p>
   * @see #getESuperTypes()
   * @since 2.3
   * <!-- end-user-doc -->
   * @return the value of the '<em>EGeneric Super Types</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EGenericSuperTypes()
   * @model containment="true" unsettable="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  EList<EGenericType> getEGenericSuperTypes();

  /**
   * Returns the value of the '<em><b>EAll Generic Super Types</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EGenericType}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the closure of all inherited {@link #getEGenericSuperTypes() generic super types}.
   * The {@link #getEAllSuperTypes() all super types} list is derived from this one, i.e., it represents the {@link EGenericType#getERawType() erasure} of each generic super type.
   * </p>
   * @see #getEGenericSuperTypes()
   * @see #getEAllSuperTypes()
   * @since 2.3
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAll Generic Super Types</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAllGenericSuperTypes()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EGenericType> getEAllGenericSuperTypes();

  /**
   * Returns the value of the '<em><b>EAttributes</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EAttribute}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the modeled attributes local to this class.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAttributes</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAttributes()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EAttribute> getEAttributes();

  /**
   * Returns the value of the '<em><b>EAll Attributes</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EAttribute}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the closure of all attributes, inherited and local.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAll Attributes</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAllAttributes()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EAttribute> getEAllAttributes();

  /**
   * Returns the value of the '<em><b>EReferences</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EReference}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the modeled references local to this class.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EReferences</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EReferences()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EReference> getEReferences();

  /**
   * Returns the value of the '<em><b>EAll References</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EReference}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the closure of all references, inherited and local.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAll References</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAllReferences()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EReference> getEAllReferences();

  /**
   * Returns the value of the '<em><b>EAll Containments</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EReference}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the closure of all references, inherited and local, 
   * that are {@link EReference#isContainment containments}.
   * These features will determine the {@link EObject#eContents} of an instance.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAll Containments</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAllContainments()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EReference> getEAllContainments();

  /**
   * Returns the value of the '<em><b>EAll Structural Features</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the closure of all attributes and references, inherited and local.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAll Structural Features</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAllStructuralFeatures()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EStructuralFeature> getEAllStructuralFeatures();

  /**
   * Returns the value of the '<em><b>EOperations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EOperation}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EOperation#getEContainingClass <em>EContaining Class</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the modeled operations local to this class.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EOperations</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EOperations()
   * @see org.eclipse.emf.ecore.EOperation#getEContainingClass
   * @model opposite="eContainingClass" containment="true"
   * @generated
   */
  EList<EOperation> getEOperations();

  /**
   * Returns the value of the '<em><b>EAll Operations</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EOperation}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the closure of all operations, inherited and local.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAll Operations</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEClass_EAllOperations()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EOperation> getEAllOperations();

  /**
   * <!-- begin-user-doc -->
   * Returns whether this class is the same as, or a super type of, some other class.
   * @param someClass some other class.
   * @return whether this class is the same as, or a super type of, some other class.
   * @see Class#isAssignableFrom
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean isSuperTypeOf(EClass someClass);

  /**
   * <!-- begin-user-doc -->
   * Returns the number of features.
   * @return the number of features.
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  int getFeatureCount();

  /**
   * <!-- begin-user-doc -->
   * Returns the feature with this ID.
   * @return the feature with this ID.
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EStructuralFeature getEStructuralFeature(int featureID);

  /**
   * <!-- begin-user-doc -->
   * Returns the feature with this name.
   * @return the feature with this name.
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EStructuralFeature getEStructuralFeature(String featureName);

  /**
   * <!-- begin-user-doc -->
   * Returns the number of operations.
   * @return the number of operations.
   * @since 2.6
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  int getOperationCount();

  /**
   * <!-- begin-user-doc -->
   * Returns the operation with this ID.
   * @return the operation with this ID.
   * @since 2.6
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EOperation getEOperation(int operationID);

  /**
   * <!-- begin-user-doc -->
   * Returns the ID of the operation relative to this class, or -1 if the operation is not in this class.
   * @return the ID of the operation relative to this class, or -1 if the operation is not in this class.
   * @since 2.6
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  int getOperationID(EOperation operation);

  /**
   * <!-- begin-user-doc -->
   * Returns the operation that overrides this operation.
   * @return the operation that overrides this operation.
   * @since 2.6
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EOperation getOverride(EOperation operation);

  /**
   * <!-- begin-user-doc -->
   * Returns the ID of the feature relative to this class, or -1 if the feature is not in this class.
   * @return the ID of the feature relative to this class, or -1 if the feature is not in this class.
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  int getFeatureID(EStructuralFeature feature);

}
