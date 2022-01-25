/**
 */
package org.omg.spec.bpmn.non.normative.color;

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
 * </p>
 * <ul>
 *   <li>{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getBackgroundColor <em>Background Color</em>}</li>
 *   <li>{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getBorderColor <em>Border Color</em>}</li>
 *   <li>{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getColor <em>Color</em>}</li>
 * </ul>
 *
 * @see org.omg.spec.bpmn.non.normative.color.ColorPackage#getDocumentRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
public interface DocumentRoot extends EObject {
	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.omg.spec.bpmn.non.normative.color.ColorPackage#getDocumentRoot_Mixed()
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
	 * @see org.omg.spec.bpmn.non.normative.color.ColorPackage#getDocumentRoot_XMLNSPrefixMap()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;" transient="true"
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
	 * @see org.omg.spec.bpmn.non.normative.color.ColorPackage#getDocumentRoot_XSISchemaLocation()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;" transient="true"
	 *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
	 * @generated
	 */
	EMap<String, String> getXSISchemaLocation();

	/**
	 * Returns the value of the '<em><b>Background Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Background Color</em>' attribute.
	 * @see #setBackgroundColor(String)
	 * @see org.omg.spec.bpmn.non.normative.color.ColorPackage#getDocumentRoot_BackgroundColor()
	 * @model dataType="org.omg.spec.bpmn.non.normative.color.HexColor"
	 *        extendedMetaData="kind='attribute' name='background-color' namespace='##targetNamespace'"
	 * @generated
	 */
	String getBackgroundColor();

	/**
	 * Sets the value of the '{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getBackgroundColor <em>Background Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Background Color</em>' attribute.
	 * @see #getBackgroundColor()
	 * @generated
	 */
	void setBackgroundColor(String value);

	/**
	 * Returns the value of the '<em><b>Border Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Border Color</em>' attribute.
	 * @see #setBorderColor(String)
	 * @see org.omg.spec.bpmn.non.normative.color.ColorPackage#getDocumentRoot_BorderColor()
	 * @model dataType="org.omg.spec.bpmn.non.normative.color.HexColor"
	 *        extendedMetaData="kind='attribute' name='border-color' namespace='##targetNamespace'"
	 * @generated
	 */
	String getBorderColor();

	/**
	 * Sets the value of the '{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getBorderColor <em>Border Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Border Color</em>' attribute.
	 * @see #getBorderColor()
	 * @generated
	 */
	void setBorderColor(String value);

	/**
	 * Returns the value of the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color</em>' attribute.
	 * @see #setColor(String)
	 * @see org.omg.spec.bpmn.non.normative.color.ColorPackage#getDocumentRoot_Color()
	 * @model dataType="org.omg.spec.bpmn.non.normative.color.HexColor"
	 *        extendedMetaData="kind='attribute' name='color' namespace='##targetNamespace'"
	 * @generated
	 */
	String getColor();

	/**
	 * Sets the value of the '{@link org.omg.spec.bpmn.non.normative.color.DocumentRoot#getColor <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Color</em>' attribute.
	 * @see #getColor()
	 * @generated
	 */
	void setColor(String value);

} // DocumentRoot
