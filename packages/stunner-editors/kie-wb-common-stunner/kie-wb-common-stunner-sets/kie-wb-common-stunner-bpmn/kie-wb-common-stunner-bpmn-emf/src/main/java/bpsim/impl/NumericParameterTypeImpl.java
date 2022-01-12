/**
 */
package bpsim.impl;

import bpsim.BpsimPackage;
import bpsim.NumericParameterType;
import bpsim.TimeUnit;

import com.google.gwt.user.client.rpc.GwtTransient;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Numeric Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link bpsim.impl.NumericParameterTypeImpl#getCurrencyUnit <em>Currency Unit</em>}</li>
 *   <li>{@link bpsim.impl.NumericParameterTypeImpl#getTimeUnit <em>Time Unit</em>}</li>
 *   <li>{@link bpsim.impl.NumericParameterTypeImpl#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NumericParameterTypeImpl extends ConstantParameterImpl implements NumericParameterType {
	/**
	 * The default value of the '{@link #getCurrencyUnit() <em>Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrencyUnit()
	 * @generated
	 * @ordered
	 */
	protected static final String CURRENCY_UNIT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCurrencyUnit() <em>Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrencyUnit()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected String currencyUnit = CURRENCY_UNIT_EDEFAULT;

	/**
	 * The default value of the '{@link #getTimeUnit() <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeUnit()
	 * @generated
	 * @ordered
	 */
	protected static final TimeUnit TIME_UNIT_EDEFAULT = TimeUnit.MS;

	/**
	 * The cached value of the '{@link #getTimeUnit() <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeUnit()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected TimeUnit timeUnit = TIME_UNIT_EDEFAULT;

	/**
	 * This is true if the Time Unit attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected boolean timeUnitESet;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final long VALUE_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected long value = VALUE_EDEFAULT;

	/**
	 * This is true if the Value attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected boolean valueESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NumericParameterTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.NUMERIC_PARAMETER_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCurrencyUnit() {
		return currencyUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCurrencyUnit(String newCurrencyUnit) {
		String oldCurrencyUnit = currencyUnit;
		currencyUnit = newCurrencyUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.NUMERIC_PARAMETER_TYPE__CURRENCY_UNIT, oldCurrencyUnit, currencyUnit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTimeUnit(TimeUnit newTimeUnit) {
		TimeUnit oldTimeUnit = timeUnit;
		timeUnit = newTimeUnit == null ? TIME_UNIT_EDEFAULT : newTimeUnit;
		boolean oldTimeUnitESet = timeUnitESet;
		timeUnitESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.NUMERIC_PARAMETER_TYPE__TIME_UNIT, oldTimeUnit, timeUnit, !oldTimeUnitESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetTimeUnit() {
		TimeUnit oldTimeUnit = timeUnit;
		boolean oldTimeUnitESet = timeUnitESet;
		timeUnit = TIME_UNIT_EDEFAULT;
		timeUnitESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.NUMERIC_PARAMETER_TYPE__TIME_UNIT, oldTimeUnit, TIME_UNIT_EDEFAULT, oldTimeUnitESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetTimeUnit() {
		return timeUnitESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValue(long newValue) {
		long oldValue = value;
		value = newValue;
		boolean oldValueESet = valueESet;
		valueESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.NUMERIC_PARAMETER_TYPE__VALUE, oldValue, value, !oldValueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetValue() {
		long oldValue = value;
		boolean oldValueESet = valueESet;
		value = VALUE_EDEFAULT;
		valueESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.NUMERIC_PARAMETER_TYPE__VALUE, oldValue, VALUE_EDEFAULT, oldValueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetValue() {
		return valueESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__CURRENCY_UNIT:
				return getCurrencyUnit();
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__TIME_UNIT:
				return getTimeUnit();
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__VALUE:
				return getValue();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__CURRENCY_UNIT:
				setCurrencyUnit((String)newValue);
				return;
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__TIME_UNIT:
				setTimeUnit((TimeUnit)newValue);
				return;
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__VALUE:
				setValue((Long)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__CURRENCY_UNIT:
				setCurrencyUnit(CURRENCY_UNIT_EDEFAULT);
				return;
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__TIME_UNIT:
				unsetTimeUnit();
				return;
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__VALUE:
				unsetValue();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__CURRENCY_UNIT:
				return CURRENCY_UNIT_EDEFAULT == null ? currencyUnit != null : !CURRENCY_UNIT_EDEFAULT.equals(currencyUnit);
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__TIME_UNIT:
				return isSetTimeUnit();
			case BpsimPackage.NUMERIC_PARAMETER_TYPE__VALUE:
				return isSetValue();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (currencyUnit: ");
		result.append(currencyUnit);
		result.append(", timeUnit: ");
		if (timeUnitESet) result.append(timeUnit); else result.append("<unset>");
		result.append(", value: ");
		if (valueESet) result.append(value); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //NumericParameterTypeImpl
