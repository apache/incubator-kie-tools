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


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;


/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceFactory
 * @model kind="package"
 *        annotation="http://www.w3.org/XML/1998/namespace lang='en'"
 * @generated
 */
public interface XMLNamespacePackage extends EPackage{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "namespace";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.w3.org/XML/1998/namespace";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "xml";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  XMLNamespacePackage eINSTANCE = org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespaceDocumentRootImpl <em>Document Root</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespaceDocumentRootImpl
   * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getXMLNamespaceDocumentRoot()
   * @generated
   */
  int XML_NAMESPACE_DOCUMENT_ROOT = 0;

  /**
   * The feature id for the '<em><b>Mixed</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT__MIXED = 0;

  /**
   * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

  /**
   * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

  /**
   * The feature id for the '<em><b>Base</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT__BASE = 3;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT__ID = 4;

  /**
   * The feature id for the '<em><b>Lang</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT__LANG = 5;

  /**
   * The feature id for the '<em><b>Space</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT__SPACE = 6;

  /**
   * The number of structural features of the '<em>Document Root</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int XML_NAMESPACE_DOCUMENT_ROOT_FEATURE_COUNT = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.ecore.xml.namespace.SpaceType <em>Space Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
   * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getSpaceType()
   * @generated
   */
  int SPACE_TYPE = 1;

  /**
   * The meta object id for the '<em>Lang Type</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.lang.String
   * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getLangType()
   * @generated
   */
  int LANG_TYPE = 2;

  /**
   * The meta object id for the '<em>Lang Type Null</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.lang.String
   * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getLangTypeNull()
   * @generated
   */
  int LANG_TYPE_NULL = 3;

  /**
   * The meta object id for the '<em>Space Type Object</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
   * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getSpaceTypeObject()
   * @generated
   */
  int SPACE_TYPE_OBJECT = 4;


  /**
   * Returns the meta object for class '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot <em>Document Root</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Document Root</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot
   * @generated
   */
  EClass getXMLNamespaceDocumentRoot();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getMixed <em>Mixed</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Mixed</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getMixed()
   * @see #getXMLNamespaceDocumentRoot()
   * @generated
   */
  EAttribute getXMLNamespaceDocumentRoot_Mixed();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getXMLNSPrefixMap()
   * @see #getXMLNamespaceDocumentRoot()
   * @generated
   */
  EReference getXMLNamespaceDocumentRoot_XMLNSPrefixMap();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>XSI Schema Location</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getXSISchemaLocation()
   * @see #getXMLNamespaceDocumentRoot()
   * @generated
   */
  EReference getXMLNamespaceDocumentRoot_XSISchemaLocation();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getBase <em>Base</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Base</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getBase()
   * @see #getXMLNamespaceDocumentRoot()
   * @generated
   */
  EAttribute getXMLNamespaceDocumentRoot_Base();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getId()
   * @see #getXMLNamespaceDocumentRoot()
   * @generated
   */
  EAttribute getXMLNamespaceDocumentRoot_Id();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getLang <em>Lang</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Lang</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getLang()
   * @see #getXMLNamespaceDocumentRoot()
   * @generated
   */
  EAttribute getXMLNamespaceDocumentRoot_Lang();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getSpace <em>Space</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Space</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot#getSpace()
   * @see #getXMLNamespaceDocumentRoot()
   * @generated
   */
  EAttribute getXMLNamespaceDocumentRoot_Space();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.ecore.xml.namespace.SpaceType <em>Space Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Space Type</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
   * @generated
   */
  EEnum getSpaceType();

  /**
   * Returns the meta object for data type '{@link java.lang.String <em>Lang Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Lang Type</em>'.
   * @see java.lang.String
   * @model instanceClass="java.lang.String"
   *        extendedMetaData="name='lang_._type' memberTypes='http://www.eclipse.org/emf/2003/XMLType#language lang_._type_._member_._1'"
   * @generated
   */
  EDataType getLangType();

  /**
   * Returns the meta object for data type '{@link java.lang.String <em>Lang Type Null</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Lang Type Null</em>'.
   * @see java.lang.String
   * @model instanceClass="java.lang.String"
   *        extendedMetaData="name='lang_._type_._member_._1' baseType='http://www.eclipse.org/emf/2003/XMLType#string' enumeration=''"
   * @generated
   */
  EDataType getLangTypeNull();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.ecore.xml.namespace.SpaceType <em>Space Type Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Space Type Object</em>'.
   * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
   * @model instanceClass="org.eclipse.emf.ecore.xml.namespace.SpaceType"
   *        extendedMetaData="name='space_._type:Object' baseType='space_._type'"
   * @generated
   */
  EDataType getSpaceTypeObject();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  XMLNamespaceFactory getXMLNamespaceFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespaceDocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespaceDocumentRootImpl
     * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getXMLNamespaceDocumentRoot()
     * @generated
     */
    EClass XML_NAMESPACE_DOCUMENT_ROOT = eINSTANCE.getXMLNamespaceDocumentRoot();

    /**
     * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XML_NAMESPACE_DOCUMENT_ROOT__MIXED = eINSTANCE.getXMLNamespaceDocumentRoot_Mixed();

    /**
     * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XML_NAMESPACE_DOCUMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getXMLNamespaceDocumentRoot_XMLNSPrefixMap();

    /**
     * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference XML_NAMESPACE_DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getXMLNamespaceDocumentRoot_XSISchemaLocation();

    /**
     * The meta object literal for the '<em><b>Base</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XML_NAMESPACE_DOCUMENT_ROOT__BASE = eINSTANCE.getXMLNamespaceDocumentRoot_Base();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XML_NAMESPACE_DOCUMENT_ROOT__ID = eINSTANCE.getXMLNamespaceDocumentRoot_Id();

    /**
     * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XML_NAMESPACE_DOCUMENT_ROOT__LANG = eINSTANCE.getXMLNamespaceDocumentRoot_Lang();

    /**
     * The meta object literal for the '<em><b>Space</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute XML_NAMESPACE_DOCUMENT_ROOT__SPACE = eINSTANCE.getXMLNamespaceDocumentRoot_Space();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.ecore.xml.namespace.SpaceType <em>Space Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
     * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getSpaceType()
     * @generated
     */
    EEnum SPACE_TYPE = eINSTANCE.getSpaceType();

    /**
     * The meta object literal for the '<em>Lang Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getLangType()
     * @generated
     */
    EDataType LANG_TYPE = eINSTANCE.getLangType();

    /**
     * The meta object literal for the '<em>Lang Type Null</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getLangTypeNull()
     * @generated
     */
    EDataType LANG_TYPE_NULL = eINSTANCE.getLangTypeNull();

    /**
     * The meta object literal for the '<em>Space Type Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.xml.namespace.SpaceType
     * @see org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespacePackageImpl#getSpaceTypeObject()
     * @generated
     */
    EDataType SPACE_TYPE_OBJECT = eINSTANCE.getSpaceTypeObject();

  }

} //XMLNamespacePackage
