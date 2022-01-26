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
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EAnnotation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EAnnotation#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EAnnotation#getDetails <em>Details</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EAnnotation#getEModelElement <em>EModel Element</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EAnnotation#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EAnnotation#getReferences <em>References</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEAnnotation()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='WellFormedSourceURI'"
 * @generated
 */
public interface EAnnotation extends EModelElement
{
  /**
   * Returns the value of the '<em><b>Source</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * This will typically be a full URI representing the type of the annotation.
   * </p>
   * @see org.eclipse.emf.ecore.EModelElement#getEAnnotation(String)
   * @ignore
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' attribute.
   * @see #setSource(String)
   * @see org.eclipse.emf.ecore.EcorePackage#getEAnnotation_Source()
   * @model
   * @generated
   */
  String getSource();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EAnnotation#getSource <em>Source</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source</em>' attribute.
   * @see #getSource()
   * @generated
   */
  void setSource(String value);

  /**
   * Returns the value of the '<em><b>Details</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * This represents tagged values.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Details</em>' map.
   * @see org.eclipse.emf.ecore.EcorePackage#getEAnnotation_Details()
   * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
   * @generated
   */
  EMap<String, String> getDetails();

  /**
   * Returns the value of the '<em><b>EModel Element</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EModelElement#getEAnnotations <em>EAnnotations</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>EModel Element</em>' container reference.
   * @see #setEModelElement(EModelElement)
   * @see org.eclipse.emf.ecore.EcorePackage#getEAnnotation_EModelElement()
   * @see org.eclipse.emf.ecore.EModelElement#getEAnnotations
   * @model opposite="eAnnotations" resolveProxies="false"
   * @generated
   */
  EModelElement getEModelElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EAnnotation#getEModelElement <em>EModel Element</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EModel Element</em>' container reference.
   * @see #getEModelElement()
   * @generated
   */
  void setEModelElement(EModelElement value);

  /**
   * Returns the value of the '<em><b>Contents</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
   * <!-- begin-user-doc -->
   * <p>
   * This represents arbitrary contained objects.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Contents</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEAnnotation_Contents()
   * @model containment="true"
   * @generated
   */
  EList<EObject> getContents();

  /**
   * Returns the value of the '<em><b>References</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
   * <!-- begin-user-doc -->
   * <p>
   * This represents arbitrary referenced objects.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>References</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEAnnotation_References()
   * @model
   * @generated
   */
  EList<EObject> getReferences();

} // EAnnotation
