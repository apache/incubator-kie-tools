/**
 */
package bpsim;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Resource Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.ResourceParameters#getSelection <em>Selection</em>}</li>
 *   <li>{@link bpsim.ResourceParameters#getAvailability <em>Availability</em>}</li>
 *   <li>{@link bpsim.ResourceParameters#getQuantity <em>Quantity</em>}</li>
 *   <li>{@link bpsim.ResourceParameters#getRole <em>Role</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getResourceParameters()
 * @model extendedMetaData="name='ResourceParameters' kind='elementOnly'"
 * @generated
 */
public interface ResourceParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Selection</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Selection</em>' containment reference.
	 * @see #setSelection(Parameter)
	 * @see bpsim.BpsimPackage#getResourceParameters_Selection()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Selection' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getSelection();

	/**
	 * Sets the value of the '{@link bpsim.ResourceParameters#getSelection <em>Selection</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Selection</em>' containment reference.
	 * @see #getSelection()
	 * @generated
	 */
	void setSelection(Parameter value);

	/**
	 * Returns the value of the '<em><b>Availability</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Availability</em>' containment reference.
	 * @see #setAvailability(Parameter)
	 * @see bpsim.BpsimPackage#getResourceParameters_Availability()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Availability' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getAvailability();

	/**
	 * Sets the value of the '{@link bpsim.ResourceParameters#getAvailability <em>Availability</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Availability</em>' containment reference.
	 * @see #getAvailability()
	 * @generated
	 */
	void setAvailability(Parameter value);

	/**
	 * Returns the value of the '<em><b>Quantity</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Quantity</em>' containment reference.
	 * @see #setQuantity(Parameter)
	 * @see bpsim.BpsimPackage#getResourceParameters_Quantity()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Quantity' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getQuantity();

	/**
	 * Sets the value of the '{@link bpsim.ResourceParameters#getQuantity <em>Quantity</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Quantity</em>' containment reference.
	 * @see #getQuantity()
	 * @generated
	 */
	void setQuantity(Parameter value);

	/**
	 * Returns the value of the '<em><b>Role</b></em>' containment reference list.
	 * The list contents are of type {@link bpsim.Parameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Role</em>' containment reference list.
	 * @see bpsim.BpsimPackage#getResourceParameters_Role()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Role' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Parameter> getRole();

} // ResourceParameters
