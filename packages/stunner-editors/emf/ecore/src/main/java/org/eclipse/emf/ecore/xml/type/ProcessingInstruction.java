/**
 * Copyright (c) 2006-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Nehrer and IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.xml.type;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Processing Instruction Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.ProcessingInstruction#getData <em>Data</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.xml.type.ProcessingInstruction#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getProcessingInstruction()
 * @model extendedMetaData="name='processingInstruction_._type' kind='empty'"
 * @generated
 */
public interface ProcessingInstruction extends EObject
{
  /**
   * Returns the value of the '<em><b>Data</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents all the data that follows the target of the processing instruction.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data</em>' attribute.
   * @see #setData(String)
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getProcessingInstruction_Data()
   * @model dataType="org.eclipse.emf.ecore.xml.type.String"
   *        extendedMetaData="kind='attribute' name='data'"
   * @generated
   */
  String getData();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.type.ProcessingInstruction#getData <em>Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Data</em>' attribute.
   * @see #getData()
   * @generated
   */
  void setData(String value);

  /**
   * Returns the value of the '<em><b>Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the specific target name at the start of the processing instruction.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target</em>' attribute.
   * @see #setTarget(String)
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#getProcessingInstruction_Target()
   * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
   *        extendedMetaData="kind='attribute' name='target'"
   * @generated
   */
  String getTarget();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.xml.type.ProcessingInstruction#getTarget <em>Target</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target</em>' attribute.
   * @see #getTarget()
   * @generated
   */
  void setTarget(String value);

} // ProcessingInstructionType
