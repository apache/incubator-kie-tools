/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore;



/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EParameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EParameter#getEOperation <em>EOperation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEParameter()
 * @model
 * @generated
 */
public interface EParameter extends ETypedElement
{
  /**
   * Returns the value of the '<em><b>EOperation</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EOperation#getEParameters <em>EParameters</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the containing operation.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EOperation</em>' container reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEParameter_EOperation()
   * @see org.eclipse.emf.ecore.EOperation#getEParameters
   * @model opposite="eParameters" resolveProxies="false" changeable="false"
   * @generated
   */
  EOperation getEOperation();

} //EParameter
