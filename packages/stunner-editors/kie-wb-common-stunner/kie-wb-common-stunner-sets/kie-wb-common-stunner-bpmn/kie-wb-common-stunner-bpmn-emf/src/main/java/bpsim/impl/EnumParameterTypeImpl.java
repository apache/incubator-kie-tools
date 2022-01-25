/**
 */
package bpsim.impl;

import bpsim.BpsimPackage;
import bpsim.EnumParameterType;
import bpsim.ParameterValue;

import com.google.gwt.user.client.rpc.GwtTransient;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Enum Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link bpsim.impl.EnumParameterTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link bpsim.impl.EnumParameterTypeImpl#getParameterValueGroup <em>Parameter Value Group</em>}</li>
 *   <li>{@link bpsim.impl.EnumParameterTypeImpl#getParameterValue <em>Parameter Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EnumParameterTypeImpl extends ParameterValueImpl implements EnumParameterType {
	/**
	 * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected FeatureMap group;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EnumParameterTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.ENUM_PARAMETER_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, BpsimPackage.ENUM_PARAMETER_TYPE__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureMap getParameterValueGroup() {
		return (FeatureMap)getGroup().<FeatureMap.Entry>list(BpsimPackage.Literals.ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ParameterValue> getParameterValue() {
		return getParameterValueGroup().list(BpsimPackage.Literals.ENUM_PARAMETER_TYPE__PARAMETER_VALUE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BpsimPackage.ENUM_PARAMETER_TYPE__GROUP:
				return ((InternalEList<?>)getGroup()).basicRemove(otherEnd, msgs);
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP:
				return ((InternalEList<?>)getParameterValueGroup()).basicRemove(otherEnd, msgs);
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE:
				return ((InternalEList<?>)getParameterValue()).basicRemove(otherEnd, msgs);
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
			case BpsimPackage.ENUM_PARAMETER_TYPE__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP:
				if (coreType) return getParameterValueGroup();
				return ((FeatureMap.Internal)getParameterValueGroup()).getWrapper();
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE:
				return getParameterValue();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BpsimPackage.ENUM_PARAMETER_TYPE__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP:
				((FeatureMap.Internal)getParameterValueGroup()).set(newValue);
				return;
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE:
				getParameterValue().clear();
				getParameterValue().addAll((Collection<? extends ParameterValue>)newValue);
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
			case BpsimPackage.ENUM_PARAMETER_TYPE__GROUP:
				getGroup().clear();
				return;
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP:
				getParameterValueGroup().clear();
				return;
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE:
				getParameterValue().clear();
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
			case BpsimPackage.ENUM_PARAMETER_TYPE__GROUP:
				return group != null && !group.isEmpty();
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP:
				return !getParameterValueGroup().isEmpty();
			case BpsimPackage.ENUM_PARAMETER_TYPE__PARAMETER_VALUE:
				return !getParameterValue().isEmpty();
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
		result.append(" (group: ");
		result.append(group);
		result.append(')');
		return result.toString();
	}

} //EnumParameterTypeImpl
