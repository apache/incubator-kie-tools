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
package org.eclipse.emf.ecore.xml.type;


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
 *   <li>{@link org.eclipse.emf.ecore.xml.type.XMLTypeDocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.XMLTypeDocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.XMLTypeDocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getXMLTypeDocumentRoot()
 * @model features="cDATA comment processingInstruction text" 
 *        cDATAUnique="false" cDATADataType="org.eclipse.emf.ecore.xml.type.String" cDATAUpper="-2" cDATATransient="true" cDATAVolatile="true" cDATADerived="true" cDATASuppressedGetVisibility="true"
 *        cDATAExtendedMetaData="kind='element' name='cDATA' namespace='##targetNamespace'"
 *        commentUnique="false" commentDataType="org.eclipse.emf.ecore.xml.type.String" commentUpper="-2" commentTransient="true" commentVolatile="true" commentDerived="true" commentSuppressedGetVisibility="true"
 *        commentExtendedMetaData="kind='element' name='comment' namespace='##targetNamespace'"
 *        processingInstructionType="org.eclipse.emf.ecore.xml.type.ProcessingInstruction" processingInstructionContainment="true" processingInstructionUpper="-2" processingInstructionTransient="true" processingInstructionVolatile="true" processingInstructionDerived="true" processingInstructionSuppressedGetVisibility="true"
 *        processingInstructionExtendedMetaData="kind='element' name='processingInstruction' namespace='##targetNamespace'"
 *        textUnique="false" textDataType="org.eclipse.emf.ecore.xml.type.String" textUpper="-2" textTransient="true" textVolatile="true" textDerived="true" textSuppressedGetVisibility="true"
 *        textExtendedMetaData="kind='element' name='text' namespace='##targetNamespace'"
 *        extendedMetaData="name='' kind='mixed'"
 * @generated
 */
public interface XMLTypeDocumentRoot extends EObject
{
  /**
   * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Mixed</em>' attribute list.
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getXMLTypeDocumentRoot_Mixed()
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
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getXMLTypeDocumentRoot_XMLNSPrefixMap()
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
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getXMLTypeDocumentRoot_XSISchemaLocation()
   * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
   *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
   * @generated
   */
  EMap<String, String> getXSISchemaLocation();

} // DocumentRoot
