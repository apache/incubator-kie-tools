/**
 */
package bpsim;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>BP Sim Data Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.BPSimDataType#getGroup <em>Group</em>}</li>
 *   <li>{@link bpsim.BPSimDataType#getScenario <em>Scenario</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getBPSimDataType()
 * @model extendedMetaData="name='BPSimData_._type' kind='elementOnly'"
 * @generated
 */
public interface BPSimDataType extends EObject {
	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' attribute list.
	 * @see bpsim.BpsimPackage#getBPSimDataType_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:0'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Scenario</b></em>' containment reference list.
	 * The list contents are of type {@link bpsim.Scenario}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scenario</em>' containment reference list.
	 * @see bpsim.BpsimPackage#getBPSimDataType_Scenario()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='Scenario' namespace='##targetNamespace' group='#group:0'"
	 * @generated
	 */
	EList<Scenario> getScenario();

} // BPSimDataType
