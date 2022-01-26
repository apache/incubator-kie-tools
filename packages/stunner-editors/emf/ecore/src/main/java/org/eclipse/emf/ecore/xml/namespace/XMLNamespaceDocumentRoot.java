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
package org.eclipse.emf.ecore.xml.namespace;


import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getLang <em>Lang</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getSpace <em>Space</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
public interface XMLNamespaceDocumentRoot extends EObject
{
  /**
   * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Mixed</em>' attribute list.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot_Mixed()
   * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
   *        extendedMetaData="kind='elementWildcard' name=':mixed'"
   * @generated
   */
  FeatureMap getMixed();

  /**
   * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>XMLNS Prefix Map</em>' map.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot_XMLNSPrefixMap()
   * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
   *        extendedMetaData="kind='attribute' name='xmlns:prefix'"
   * @generated
   */
  EMap<String, String> getXMLNSPrefixMap();

  /**
   * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>XSI Schema Location</em>' map.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot_XSISchemaLocation()
   * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
   *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
   * @generated
   */
  EMap<String, String> getXSISchemaLocation();

  /**
   * Returns the value of the '<em><b>Base</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Base</em>' attribute.
   * @see #setBase(String)
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot_Base()
   * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
   *        extendedMetaData="kind='attribute' name='base' namespace='##targetNamespace'"
   * @generated
   */
  String getBase();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getBase <em>Base</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Base</em>' attribute.
   * @see #getBase()
   * @generated
   */
  void setBase(String value);

  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(String)
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot_Id()
   * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID"
   *        extendedMetaData="kind='attribute' name='id' namespace='##targetNamespace'"
   * @generated
   */
  String getId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(String value);

  /**
   * Returns the value of the '<em><b>Lang</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lang</em>' attribute.
   * @see #setLang(String)
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot_Lang()
   * @model dataType="org.eclipse.emf.ecore.xml.namespace.LangType"
   *        extendedMetaData="kind='attribute' name='lang' namespace='##targetNamespace'"
   * @generated
   */
  String getLang();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getLang <em>Lang</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Lang</em>' attribute.
   * @see #getLang()
   * @generated
   */
  void setLang(String value);

  /**
   * Returns the value of the '<em><b>Space</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.ecore.xml.namespace.SpaceType}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Space</em>' attribute.
   * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
   * @see #isSetSpace()
   * @see #unsetSpace()
   * @see #setSpace(SpaceType)
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage#getXMLNamespaceDocumentRoot_Space()
   * @model unsettable="true"
   *        extendedMetaData="kind='attribute' name='space' namespace='##targetNamespace'"
   * @generated
   */
  SpaceType getSpace();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getSpace <em>Space</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Space</em>' attribute.
   * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
   * @see #isSetSpace()
   * @see #unsetSpace()
   * @see #getSpace()
   * @generated
   */
  void setSpace(SpaceType value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getSpace <em>Space</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetSpace()
   * @see #getSpace()
   * @see #setSpace(SpaceType)
   * @generated
   */
  void unsetSpace();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getSpace <em>Space</em>}' attribute is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Space</em>' attribute is set.
   * @see #unsetSpace()
   * @see #getSpace()
   * @see #setSpace(SpaceType)
   * @generated
   */
  boolean isSetSpace();

} // DocumentRoot
