/**
 */
package bpsim.impl;

import bpsim.BpsimPackage;
import bpsim.ParameterValue;
import bpsim.UserDistributionDataPointType;

import com.google.gwt.user.client.rpc.GwtTransient;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User Distribution Data Point Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link bpsim.impl.UserDistributionDataPointTypeImpl#getParameterValueGroup <em>Parameter Value Group</em>}</li>
 *   <li>{@link bpsim.impl.UserDistributionDataPointTypeImpl#getParameterValue <em>Parameter Value</em>}</li>
 *   <li>{@link bpsim.impl.UserDistributionDataPointTypeImpl#getProbability <em>Probability</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UserDistributionDataPointTypeImpl extends EObjectImpl implements UserDistributionDataPointType {
	/**
	 * The cached value of the '{@link #getParameterValueGroup() <em>Parameter Value Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterValueGroup()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected FeatureMap parameterValueGroup;

	/**
	 * The default value of the '{@link #getProbability() <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbability()
	 * @generated
	 * @ordered
	 */
	protected static final float PROBABILITY_EDEFAULT = 0.0F;

	/**
	 * The cached value of the '{@link #getProbability() <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbability()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected float probability = PROBABILITY_EDEFAULT;

	/**
	 * This is true if the Probability attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected boolean probabilityESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UserDistributionDataPointTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.USER_DISTRIBUTION_DATA_POINT_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureMap getParameterValueGroup() {
		if (parameterValueGroup == null) {
			parameterValueGroup = new BasicFeatureMap(this, BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP);
		}
		return parameterValueGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ParameterValue getParameterValue() {
		return (ParameterValue)getParameterValueGroup().get(BpsimPackage.Literals.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParameterValue(ParameterValue newParameterValue, NotificationChain msgs) {
		return ((FeatureMap.Internal)getParameterValueGroup()).basicAdd(BpsimPackage.Literals.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE, newParameterValue, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParameterValue(ParameterValue newParameterValue) {
		((FeatureMap.Internal)getParameterValueGroup()).set(BpsimPackage.Literals.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE, newParameterValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public float getProbability() {
		return probability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProbability(float newProbability) {
		float oldProbability = probability;
		probability = newProbability;
		boolean oldProbabilityESet = probabilityESet;
		probabilityESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY, oldProbability, probability, !oldProbabilityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetProbability() {
		float oldProbability = probability;
		boolean oldProbabilityESet = probabilityESet;
		probability = PROBABILITY_EDEFAULT;
		probabilityESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY, oldProbability, PROBABILITY_EDEFAULT, oldProbabilityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetProbability() {
		return probabilityESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP:
				return ((InternalEList<?>)getParameterValueGroup()).basicRemove(otherEnd, msgs);
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE:
				return basicSetParameterValue(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP:
				if (coreType) return getParameterValueGroup();
				return ((FeatureMap.Internal)getParameterValueGroup()).getWrapper();
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE:
				return getParameterValue();
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY:
				return getProbability();
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
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP:
				((FeatureMap.Internal)getParameterValueGroup()).set(newValue);
				return;
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE:
				setParameterValue((ParameterValue)newValue);
				return;
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY:
				setProbability((Float)newValue);
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
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP:
				getParameterValueGroup().clear();
				return;
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE:
				setParameterValue((ParameterValue)null);
				return;
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY:
				unsetProbability();
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
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP:
				return parameterValueGroup != null && !parameterValueGroup.isEmpty();
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE:
				return getParameterValue() != null;
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY:
				return isSetProbability();
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
		result.append(" (parameterValueGroup: ");
		result.append(parameterValueGroup);
		result.append(", probability: ");
		if (probabilityESet) result.append(probability); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //UserDistributionDataPointTypeImpl
