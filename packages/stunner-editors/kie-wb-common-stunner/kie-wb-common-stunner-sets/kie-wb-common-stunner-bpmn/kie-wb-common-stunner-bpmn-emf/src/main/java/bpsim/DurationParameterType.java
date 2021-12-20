/**
 */
package bpsim;

import javax.xml.datatype.Duration;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Duration Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.DurationParameterType#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getDurationParameterType()
 * @model extendedMetaData="name='DurationParameter_._type' kind='empty'"
 * @generated
 */
public interface DurationParameterType extends ConstantParameter {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(Duration)
	 * @see bpsim.BpsimPackage#getDurationParameterType_Value()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.Duration"
	 *        extendedMetaData="kind='attribute' name='value'"
	 * @generated
	 */
	Duration getValue();

	/**
	 * Sets the value of the '{@link bpsim.DurationParameterType#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(Duration value);

} // DurationParameterType
