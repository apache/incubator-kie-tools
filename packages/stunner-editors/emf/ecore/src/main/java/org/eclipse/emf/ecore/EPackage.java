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


import java.util.Map;

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EPackage</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EPackage#getNsURI <em>Ns URI</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EPackage#getNsPrefix <em>Ns Prefix</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EPackage#getEFactoryInstance <em>EFactory Instance</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EPackage#getEClassifiers <em>EClassifiers</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EPackage#getESubpackages <em>ESubpackages</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EPackage#getESuperPackage <em>ESuper Package</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEPackage()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='WellFormedNsURI WellFormedNsPrefix UniqueSubpackageNames UniqueClassifierNames UniqueNsURIs'"
 * @generated
 */
public interface EPackage extends ENamedElement
{
  /**
   * An <code>EPackage</code> wrapper that is used by the {@link EPackage.Registry}.
   */
  public interface Descriptor
  {
    /**
     * Returns the package.
     * @return the package.
     */
    EPackage getEPackage();

    /**
     * Returns the factory.
     * @return the factory.
     */
    EFactory getEFactory();
  }

  /**
   * A map from {@link EPackage#getNsURI() namespace URI} to {@link EPackage}.
   */
  public interface Registry extends Map<String, Object>
  {
    /**
     * Looks up the value in the map, converting <code>EPackage.Descriptor</code> objects to <code>EPackage</code> objects on demand.
     */
    EPackage getEPackage(String nsURI);

    /**
     * Looks up the value in the map, converting <code>EPackage.Descriptor</code> objects to <code>EFactory</code> objects on demand.
     */
    EFactory getEFactory(String nsURI);

    Registry INSTANCE = org.eclipse.emf.ecore.impl.EPackageRegistryImpl.createGlobalRegistry();
  }

  /**
   * Returns the value of the '<em><b>Ns URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the namespace URI, i.e., a universally unique identification of a particular package,
   * much like an XML Schema target namespace.
   * It will be used in an XMI serialization.
   * It is typically registered with the {@link EPackage.Registry#INSTANCE global} package registry.
   * </p>
   * @see EPackage.Registry#INSTANCE
   * @ignore
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ns URI</em>' attribute.
   * @see #setNsURI(String)
   * @see org.eclipse.emf.ecore.EcorePackage#getEPackage_NsURI()
   * @model
   * @generated
   */
  String getNsURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EPackage#getNsURI <em>Ns URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ns URI</em>' attribute.
   * @see #getNsURI()
   * @generated
   */
  void setNsURI(String value);

  /**
   * Returns the value of the '<em><b>Ns Prefix</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the preferred XMLNS prefix to be used for this package's the {@link #getNsURI namespace URI}.
   * It will be used in an XMI serialization.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ns Prefix</em>' attribute.
   * @see #setNsPrefix(String)
   * @see org.eclipse.emf.ecore.EcorePackage#getEPackage_NsPrefix()
   * @model
   * @generated
   */
  String getNsPrefix();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EPackage#getNsPrefix <em>Ns Prefix</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ns Prefix</em>' attribute.
   * @see #getNsPrefix()
   * @generated
   */
  void setNsPrefix(String value);

  /**
   * Returns the value of the '<em><b>EFactory Instance</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EFactory#getEPackage <em>EPackage</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It this package's factory for creating modeled object instances.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EFactory Instance</em>' reference.
   * @see #setEFactoryInstance(EFactory)
   * @see org.eclipse.emf.ecore.EcorePackage#getEPackage_EFactoryInstance()
   * @see org.eclipse.emf.ecore.EFactory#getEPackage
   * @model opposite="ePackage" resolveProxies="false" required="true" transient="true"
   * @generated
   */
  EFactory getEFactoryInstance();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EPackage#getEFactoryInstance <em>EFactory Instance</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EFactory Instance</em>' reference.
   * @see #getEFactoryInstance()
   * @generated
   */
  void setEFactoryInstance(EFactory value);

  /**
   * Returns the value of the '<em><b>EClassifiers</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EClassifier}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EClassifier#getEPackage <em>EPackage</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the meta objects, i.e., classes and data types, defined in this package.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EClassifiers</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEPackage_EClassifiers()
   * @see org.eclipse.emf.ecore.EClassifier#getEPackage
   * @model opposite="ePackage" containment="true" resolveProxies="true"
   * @generated
   */
  EList<EClassifier> getEClassifiers();

  /**
   * Returns the value of the '<em><b>ESubpackages</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EPackage}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EPackage#getESuperPackage <em>ESuper Package</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the nested packages contained by this package.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>ESubpackages</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEPackage_ESubpackages()
   * @see org.eclipse.emf.ecore.EPackage#getESuperPackage
   * @model opposite="eSuperPackage" containment="true" resolveProxies="true"
   * @generated
   */
  EList<EPackage> getESubpackages();

  /**
   * Returns the value of the '<em><b>ESuper Package</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EPackage#getESubpackages <em>ESubpackages</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the containing package of this package.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>ESuper Package</em>' container reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEPackage_ESuperPackage()
   * @see org.eclipse.emf.ecore.EPackage#getESubpackages
   * @model opposite="eSubpackages" changeable="false"
   * @generated
   */
  EPackage getESuperPackage();

  /**
   * <!-- begin-user-doc -->
   * Returns the classifier with the given name.
   * @param name the name in question.
   * @return the classifier with the given name.
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EClassifier getEClassifier(String name);

}
