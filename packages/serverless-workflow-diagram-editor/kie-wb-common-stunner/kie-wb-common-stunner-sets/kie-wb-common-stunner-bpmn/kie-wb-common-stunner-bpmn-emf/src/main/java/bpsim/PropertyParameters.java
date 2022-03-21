/**
 */
package bpsim;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.PropertyParameters#getProperty <em>Property</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getPropertyParameters()
 * @model extendedMetaData="name='PropertyParameters' kind='elementOnly'"
 * @generated
 */
public interface PropertyParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Property</b></em>' containment reference list.
	 * The list contents are of type {@link bpsim.PropertyType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property</em>' containment reference list.
	 * @see bpsim.BpsimPackage#getPropertyParameters_Property()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Property' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<PropertyType> getProperty();

} // PropertyParameters
