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

import com.google.gwt.user.client.rpc.GwtTransient;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ItemKind;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Item Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.bpmn2.impl.ItemDefinitionImpl#isIsCollection <em>Is Collection</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.impl.ItemDefinitionImpl#getImport <em>Import</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.impl.ItemDefinitionImpl#getItemKind <em>Item Kind</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.impl.ItemDefinitionImpl#getStructureRef <em>Structure Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ItemDefinitionImpl extends RootElementImpl implements ItemDefinition {
	/**
	 * The default value of the '{@link #isIsCollection() <em>Is Collection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsCollection()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_COLLECTION_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsCollection() <em>Is Collection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsCollection()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected boolean isCollection = IS_COLLECTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getImport() <em>Import</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImport()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected Import import_;

	/**
	 * The default value of the '{@link #getItemKind() <em>Item Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItemKind()
	 * @generated
	 * @ordered
	 */
	protected static final ItemKind ITEM_KIND_EDEFAULT = ItemKind.PHYSICAL;

	/**
	 * The cached value of the '{@link #getItemKind() <em>Item Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItemKind()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected ItemKind itemKind = ITEM_KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getStructureRef() <em>Structure Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureRef()
	 * @generated
	 * @ordered
	 */
	protected static final String STRUCTURE_REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStructureRef() <em>Structure Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureRef()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected String structureRef = STRUCTURE_REF_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ItemDefinitionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Bpmn2Package.Literals.ITEM_DEFINITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIsCollection() {
		return isCollection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIsCollection(boolean newIsCollection) {
		boolean oldIsCollection = isCollection;
		isCollection = newIsCollection;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.ITEM_DEFINITION__IS_COLLECTION,
					oldIsCollection, isCollection));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Import getImport() {
		if (import_ != null && import_.eIsProxy()) {
			InternalEObject oldImport = (InternalEObject) import_;
			import_ = (Import) eResolveProxy(oldImport);
			if (import_ != oldImport) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, Bpmn2Package.ITEM_DEFINITION__IMPORT,
							oldImport, import_));
			}
		}
		return import_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Import basicGetImport() {
		return import_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setImport(Import newImport) {
		Import oldImport = import_;
		import_ = newImport;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.ITEM_DEFINITION__IMPORT, oldImport,
					import_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ItemKind getItemKind() {
		return itemKind;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setItemKind(ItemKind newItemKind) {
		ItemKind oldItemKind = itemKind;
		itemKind = newItemKind == null ? ITEM_KIND_EDEFAULT : newItemKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.ITEM_DEFINITION__ITEM_KIND, oldItemKind,
					itemKind));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getStructureRef() {
		return structureRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStructureRef(String newStructureRef) {
		String oldStructureRef = structureRef;
		structureRef = newStructureRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bpmn2Package.ITEM_DEFINITION__STRUCTURE_REF,
					oldStructureRef, structureRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case Bpmn2Package.ITEM_DEFINITION__IS_COLLECTION:
			return isIsCollection();
		case Bpmn2Package.ITEM_DEFINITION__IMPORT:
			if (resolve)
				return getImport();
			return basicGetImport();
		case Bpmn2Package.ITEM_DEFINITION__ITEM_KIND:
			return getItemKind();
		case Bpmn2Package.ITEM_DEFINITION__STRUCTURE_REF:
			return getStructureRef();
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
		case Bpmn2Package.ITEM_DEFINITION__IS_COLLECTION:
			setIsCollection((Boolean) newValue);
			return;
		case Bpmn2Package.ITEM_DEFINITION__IMPORT:
			setImport((Import) newValue);
			return;
		case Bpmn2Package.ITEM_DEFINITION__ITEM_KIND:
			setItemKind((ItemKind) newValue);
			return;
		case Bpmn2Package.ITEM_DEFINITION__STRUCTURE_REF:
			setStructureRef((String) newValue);
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
		case Bpmn2Package.ITEM_DEFINITION__IS_COLLECTION:
			setIsCollection(IS_COLLECTION_EDEFAULT);
			return;
		case Bpmn2Package.ITEM_DEFINITION__IMPORT:
			setImport((Import) null);
			return;
		case Bpmn2Package.ITEM_DEFINITION__ITEM_KIND:
			setItemKind(ITEM_KIND_EDEFAULT);
			return;
		case Bpmn2Package.ITEM_DEFINITION__STRUCTURE_REF:
			setStructureRef(STRUCTURE_REF_EDEFAULT);
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
		case Bpmn2Package.ITEM_DEFINITION__IS_COLLECTION:
			return isCollection != IS_COLLECTION_EDEFAULT;
		case Bpmn2Package.ITEM_DEFINITION__IMPORT:
			return import_ != null;
		case Bpmn2Package.ITEM_DEFINITION__ITEM_KIND:
			return itemKind != ITEM_KIND_EDEFAULT;
		case Bpmn2Package.ITEM_DEFINITION__STRUCTURE_REF:
			return STRUCTURE_REF_EDEFAULT == null ? structureRef != null : !STRUCTURE_REF_EDEFAULT.equals(structureRef);
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
		result.append(" (isCollection: ");
		result.append(isCollection);
		result.append(", itemKind: ");
		result.append(itemKind);
		result.append(", structureRef: ");
		result.append(structureRef);
		result.append(')');
		return result.toString();
	}

} //ItemDefinitionImpl
