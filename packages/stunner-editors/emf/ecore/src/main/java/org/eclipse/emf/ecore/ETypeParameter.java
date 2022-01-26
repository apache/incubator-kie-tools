/**
 * Copyright (c) 2006-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EType Parameter</b></em>'.
 * @since 2.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.ETypeParameter#getEBounds <em>EBounds</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getETypeParameter()
 * @model
 * @generated
 */
public interface ETypeParameter extends ENamedElement
{
  /**
   * Returns the value of the '<em><b>EBounds</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EGenericType}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the bounds on the type of argument that be may be used to instantiate this parameter.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EBounds</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getETypeParameter_EBounds()
   * @model containment="true"
   * @generated
   */
  EList<EGenericType> getEBounds();

} // ETypeParameter
