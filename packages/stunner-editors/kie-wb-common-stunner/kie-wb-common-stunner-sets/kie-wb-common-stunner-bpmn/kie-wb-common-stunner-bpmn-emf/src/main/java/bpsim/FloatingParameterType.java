/**
 */
package bpsim;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Floating Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.FloatingParameterType#getCurrencyUnit <em>Currency Unit</em>}</li>
 *   <li>{@link bpsim.FloatingParameterType#getTimeUnit <em>Time Unit</em>}</li>
 *   <li>{@link bpsim.FloatingParameterType#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getFloatingParameterType()
 * @model extendedMetaData="name='FloatingParameter_._type' kind='empty'"
 * @generated
 */
public interface FloatingParameterType extends ConstantParameter {
	/**
	 * Returns the value of the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Currency Unit</em>' attribute.
	 * @see #setCurrencyUnit(String)
	 * @see bpsim.BpsimPackage#getFloatingParameterType_CurrencyUnit()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='currencyUnit'"
	 * @generated
	 */
	String getCurrencyUnit();

	/**
	 * Sets the value of the '{@link bpsim.FloatingParameterType#getCurrencyUnit <em>Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Currency Unit</em>' attribute.
	 * @see #getCurrencyUnit()
	 * @generated
	 */
	void setCurrencyUnit(String value);

	/**
	 * Returns the value of the '<em><b>Time Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link bpsim.TimeUnit}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time Unit</em>' attribute.
	 * @see bpsim.TimeUnit
	 * @see #isSetTimeUnit()
	 * @see #unsetTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @see bpsim.BpsimPackage#getFloatingParameterType_TimeUnit()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='timeUnit'"
	 * @generated
	 */
	TimeUnit getTimeUnit();

	/**
	 * Sets the value of the '{@link bpsim.FloatingParameterType#getTimeUnit <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time Unit</em>' attribute.
	 * @see bpsim.TimeUnit
	 * @see #isSetTimeUnit()
	 * @see #unsetTimeUnit()
	 * @see #getTimeUnit()
	 * @generated
	 */
	void setTimeUnit(TimeUnit value);

	/**
	 * Unsets the value of the '{@link bpsim.FloatingParameterType#getTimeUnit <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTimeUnit()
	 * @see #getTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @generated
	 */
	void unsetTimeUnit();

	/**
	 * Returns whether the value of the '{@link bpsim.FloatingParameterType#getTimeUnit <em>Time Unit</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Time Unit</em>' attribute is set.
	 * @see #unsetTimeUnit()
	 * @see #getTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @generated
	 */
	boolean isSetTimeUnit();

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #isSetValue()
	 * @see #unsetValue()
	 * @see #setValue(double)
	 * @see bpsim.BpsimPackage#getFloatingParameterType_Value()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='value'"
	 * @generated
	 */
	double getValue();

	/**
	 * Sets the value of the '{@link bpsim.FloatingParameterType#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #isSetValue()
	 * @see #unsetValue()
	 * @see #getValue()
	 * @generated
	 */
	void setValue(double value);

	/**
	 * Unsets the value of the '{@link bpsim.FloatingParameterType#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetValue()
	 * @see #getValue()
	 * @see #setValue(double)
	 * @generated
	 */
	void unsetValue();

	/**
	 * Returns whether the value of the '{@link bpsim.FloatingParameterType#getValue <em>Value</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Value</em>' attribute is set.
	 * @see #unsetValue()
	 * @see #getValue()
	 * @see #setValue(double)
	 * @generated
	 */
	boolean isSetValue();

} // FloatingParameterType
