/**
 */
package org.jboss.drools.impl;

import com.google.gwt.user.client.rpc.GwtTransient;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>On Entry Script Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.jboss.drools.impl.OnEntryScriptTypeImpl#getScript <em>Script</em>}</li>
 *   <li>{@link org.jboss.drools.impl.OnEntryScriptTypeImpl#getScriptFormat <em>Script Format</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OnEntryScriptTypeImpl extends EObjectImpl implements OnEntryScriptType {
	/**
	 * The default value of the '{@link #getScript() <em>Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScript()
	 * @generated
	 * @ordered
	 */
	protected static final String SCRIPT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScript() <em>Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScript()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected String script = SCRIPT_EDEFAULT;

	/**
	 * The default value of the '{@link #getScriptFormat() <em>Script Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScriptFormat()
	 * @generated
	 * @ordered
	 */
	protected static final String SCRIPT_FORMAT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScriptFormat() <em>Script Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScriptFormat()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected String scriptFormat = SCRIPT_FORMAT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OnEntryScriptTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DroolsPackage.Literals.ON_ENTRY_SCRIPT_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getScript() {
		return script;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScript(String newScript) {
		String oldScript = script;
		script = newScript;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT, oldScript, script));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getScriptFormat() {
		return scriptFormat;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScriptFormat(String newScriptFormat) {
		String oldScriptFormat = scriptFormat;
		scriptFormat = newScriptFormat;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT, oldScriptFormat, scriptFormat));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT:
				return getScript();
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT:
				return getScriptFormat();
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
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT:
				setScript((String)newValue);
				return;
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT:
				setScriptFormat((String)newValue);
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
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT:
				setScript(SCRIPT_EDEFAULT);
				return;
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT:
				setScriptFormat(SCRIPT_FORMAT_EDEFAULT);
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
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT:
				return SCRIPT_EDEFAULT == null ? script != null : !SCRIPT_EDEFAULT.equals(script);
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT:
				return SCRIPT_FORMAT_EDEFAULT == null ? scriptFormat != null : !SCRIPT_FORMAT_EDEFAULT.equals(scriptFormat);
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
		result.append(" (script: ");
		result.append(script);
		result.append(", scriptFormat: ");
		result.append(scriptFormat);
		result.append(')');
		return result.toString();
	}

} //OnEntryScriptTypeImpl
