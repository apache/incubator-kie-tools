/**
 * <copyright>
 * 
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Reiner Hille-Doering (SAP AG) - initial API and implementation and/or initial documentation
 * 
 * </copyright>
 */
package org.eclipse.bpmn2.impl;

import java.util.Collection;

import com.google.gwt.user.client.rpc.GwtTransient;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Operation;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.bpmn2.impl.OperationImpl#getInMessageRef <em>In Message Ref</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.impl.OperationImpl#getOutMessageRef <em>Out Message Ref</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.impl.OperationImpl#getErrorRefs <em>Error Refs</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.impl.OperationImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.impl.OperationImpl#getImplementationRef <em>Implementation Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OperationImpl extends BaseElementImpl implements Operation {
	/**
	 * The cached value of the '{@link #getInMessageRef() <em>In Message Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInMessageRef()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected Message inMessageRef;

	/**
	 * The cached value of the '{@link #getOutMessageRef() <em>Out Message Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutMessageRef()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected Message outMessageRef;

	/**
	 * The cached value of the '{@link #getErrorRefs() <em>Error Refs</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getErrorRefs()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected EList<org.eclipse.bpmn2.Error> errorRefs;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getImplementationRef() <em>Implementation Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementationRef()
	 * @generated
	 * @ordered
	 */
	protected static final String IMPLEMENTATION_REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getImplementationRef() <em>Implementation Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementationRef()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected String implementationRef = IMPLEMENTATION_REF_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OperationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Bpmn2Package.Literals.OPERATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Message getInMessageRef() {
		if (inMessageRef != null && inMessageRef.eIsProxy()) {
			InternalEObject oldInMessageRef = (InternalEObject) inMessageRef;
			inMessageRef = (Message) eResolveProxy(oldInMessageRef);
			if (inMessageRef != oldInMessageRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, Bpmn2Package.OPERATION__IN_MESSAGE_REF,
							oldInMessageRef, inMessageRef));
			}
		}
		return inMessageRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Message basicGetInMessageRef() {
		return inMessageRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInMessageRef(Message newInMessageRef) {
		Message oldInMessageRef = inMessageRef;
		inMessageRef = newInMessageRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.OPERATION__IN_MESSAGE_REF,
					oldInMessageRef, inMessageRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Message getOutMessageRef() {
		if (outMessageRef != null && outMessageRef.eIsProxy()) {
			InternalEObject oldOutMessageRef = (InternalEObject) outMessageRef;
			outMessageRef = (Message) eResolveProxy(oldOutMessageRef);
			if (outMessageRef != oldOutMessageRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, Bpmn2Package.OPERATION__OUT_MESSAGE_REF,
							oldOutMessageRef, outMessageRef));
			}
		}
		return outMessageRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Message basicGetOutMessageRef() {
		return outMessageRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOutMessageRef(Message newOutMessageRef) {
		Message oldOutMessageRef = outMessageRef;
		outMessageRef = newOutMessageRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.OPERATION__OUT_MESSAGE_REF,
					oldOutMessageRef, outMessageRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<org.eclipse.bpmn2.Error> getErrorRefs() {
		if (errorRefs == null) {
			errorRefs = new EObjectResolvingEList<org.eclipse.bpmn2.Error>(org.eclipse.bpmn2.Error.class, this,
					Bpmn2Package.OPERATION__ERROR_REFS);
		}
		return errorRefs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.OPERATION__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getImplementationRef() {
		return implementationRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setImplementationRef(String newImplementationRef) {
		String oldImplementationRef = implementationRef;
		implementationRef = newImplementationRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.OPERATION__IMPLEMENTATION_REF,
					oldImplementationRef, implementationRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case Bpmn2Package.OPERATION__IN_MESSAGE_REF:
			if (resolve)
				return getInMessageRef();
			return basicGetInMessageRef();
		case Bpmn2Package.OPERATION__OUT_MESSAGE_REF:
			if (resolve)
				return getOutMessageRef();
			return basicGetOutMessageRef();
		case Bpmn2Package.OPERATION__ERROR_REFS:
			return getErrorRefs();
		case Bpmn2Package.OPERATION__NAME:
			return getName();
		case Bpmn2Package.OPERATION__IMPLEMENTATION_REF:
			return getImplementationRef();
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
		case Bpmn2Package.OPERATION__IN_MESSAGE_REF:
			setInMessageRef((Message) newValue);
			return;
		case Bpmn2Package.OPERATION__OUT_MESSAGE_REF:
			setOutMessageRef((Message) newValue);
			return;
		case Bpmn2Package.OPERATION__ERROR_REFS:
			getErrorRefs().clear();
			getErrorRefs().addAll((Collection<? extends org.eclipse.bpmn2.Error>) newValue);
			return;
		case Bpmn2Package.OPERATION__NAME:
			setName((String) newValue);
			return;
		case Bpmn2Package.OPERATION__IMPLEMENTATION_REF:
			setImplementationRef((String) newValue);
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
		case Bpmn2Package.OPERATION__IN_MESSAGE_REF:
			setInMessageRef((Message) null);
			return;
		case Bpmn2Package.OPERATION__OUT_MESSAGE_REF:
			setOutMessageRef((Message) null);
			return;
		case Bpmn2Package.OPERATION__ERROR_REFS:
			getErrorRefs().clear();
			return;
		case Bpmn2Package.OPERATION__NAME:
			setName(NAME_EDEFAULT);
			return;
		case Bpmn2Package.OPERATION__IMPLEMENTATION_REF:
			setImplementationRef(IMPLEMENTATION_REF_EDEFAULT);
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
		case Bpmn2Package.OPERATION__IN_MESSAGE_REF:
			return inMessageRef != null;
		case Bpmn2Package.OPERATION__OUT_MESSAGE_REF:
			return outMessageRef != null;
		case Bpmn2Package.OPERATION__ERROR_REFS:
			return errorRefs != null && !errorRefs.isEmpty();
		case Bpmn2Package.OPERATION__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case Bpmn2Package.OPERATION__IMPLEMENTATION_REF:
			return IMPLEMENTATION_REF_EDEFAULT == null ? implementationRef != null
					: !IMPLEMENTATION_REF_EDEFAULT.equals(implementationRef);
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
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", implementationRef: ");
		result.append(implementationRef);
		result.append(')');
		return result.toString();
	}

} //OperationImpl
