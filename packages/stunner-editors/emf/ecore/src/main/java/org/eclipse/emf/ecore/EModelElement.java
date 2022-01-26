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


import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EModel Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EModelElement#getEAnnotations <em>EAnnotations</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEModelElement()
 * @model abstract="true"
 * @generated
 */
public interface EModelElement extends EObject
{
  /**
   * Returns the value of the '<em><b>EAnnotations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EAnnotation}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EAnnotation#getEModelElement <em>EModel Element</em>}'.
   * <!-- begin-user-doc -->
   * It represents additional associated information
   * @see #getEAnnotation(String)
   * @ignore
   * <!-- end-user-doc -->
   * @return the value of the '<em>EAnnotations</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEModelElement_EAnnotations()
   * @see org.eclipse.emf.ecore.EAnnotation#getEModelElement
   * @model opposite="eModelElement" containment="true"
   * @generated
   */
  EList<EAnnotation> getEAnnotations();

  /**
   * <!-- begin-user-doc -->
   * Return the annotation with a matching {@link org.eclipse.emf.ecore.EAnnotation#getSource() source} attribute.
   * @return The annotation with a matching source attribute.
   * @see #getEAnnotations()
   * @see org.eclipse.emf.ecore.EAnnotation#getSource()
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EAnnotation getEAnnotation(String source);

}
