/**
 * Copyright (c) 2004-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore;


import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;


/**
 * A validity checker.
 */
public interface EValidator
{
  /**
   * This is the ID used for Eclipse markers which are based on diagnostics.
   */
  String MARKER = "org.eclipse.emf.ecore.diagnostic";

  /**
   * This is the name of the marker attribute to hold the String representation of the 
   * {@link org.eclipse.emf.ecore.util.EcoreUtil#getURI URI} of the object that is the target of the marker.
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getURI
   */
  String URI_ATTRIBUTE = "uri";

  /**
   * This is the name of the marker attribute to hold a space separated sequence 
   * of {@link org.eclipse.emf.common.util.URI#encodeFragment(String, boolean) encoded} Strings 
   * where each string is the {@link org.eclipse.emf.ecore.util.EcoreUtil#getURI URI} of an object related to the target of the marker.
   * The vale of this attribute should be processed as follows:
   *<pre>
   *  for (String relatedURI : relatedURIs.split(" "))
   *  {
   *    URI uri = URI.createURI(URI.decode(relatedURI));
   *    // ...
   *  }
   *</pre>
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getURI
   * @see org.eclipse.emf.common.util.URI#decode(String)
   */
  String RELATED_URIS_ATTRIBUTE = "relatedURIs";

  /**
   * An <code>EValidator</code> wrapper that is used by the {@link EValidator.Registry}.
   */
  public interface Descriptor
  {
    /**
     * Returns the validator.
     * @return the validator.
     */
    EValidator getEValidator();
  }

  /**
   * A map from {@link org.eclipse.emf.ecore.EPackage EPackage} to {@link EValidator}.
   */
  interface Registry extends Map<EPackage, Object>
  {
    /**
     * Looks up the package in the map.
     */
    EValidator getEValidator(EPackage ePackage);

    /**
     * The global instance of a validator registry.
     */
    Registry INSTANCE = new org.eclipse.emf.ecore.impl.EValidatorRegistryImpl();
  }

  /**
   * An interface for providing labels used within message substitutions.
   */
  interface SubstitutionLabelProvider
  {
    /**
     * Returns the label to identify an object.
     */
    String getObjectLabel(EObject eObject);

    /**
     * Returns the label used to identify a feature.
     */
    String getFeatureLabel(EStructuralFeature eStructuralFeature);

    /**
     * Returns the label to identify a value of some data type.
     */
    String getValueLabel(EDataType eDataType, Object value);
  }

  /**
   * An common interface for pattern-based constraints.
   */
  interface PatternMatcher
  {
    /**
     * Returns whether the string value matches the pattern.
     */
    boolean matches(String value);
  }

  /**
   * An interface for delegating validation expression evaluation.
   * @since 2.6
   */
  interface ValidationDelegate
  {
    /**
     * A <code>ValidationDelegate</code> wrapper that is used by the {@link ValidationDelegate.Registry}.
     */
    public interface Descriptor
    {
      /**
       * Returns the validation delegate.
       * @return the validation delegate.
       */
      ValidationDelegate getValidationDelegate();
    }

    /**
     * A map from {@link java.lang.String String} to {@link ValidationDelegate}.
     */
    interface Registry extends Map<String, Object>
    {
      /**
       * Looks up the validation delegate in the map.
       */
      ValidationDelegate getValidationDelegate(String uri);

      /**
       * The global instance of a validation delegate registry.
       */
      Registry INSTANCE = new org.eclipse.emf.ecore.impl.ValidationDelegateRegistryImpl();
    }

    /**
     * Evaluates the given invariant expression against the object in the given context.
     * @return the result of the expression evaluation.
     */
    boolean validate(EClass eClass, EObject eObject, Map<Object, Object> context, EOperation invariant, String expression);

    /**
     * Evaluates the given constraint expression against the object in the given context.
     * @return the result of the expression evaluation.
     */
    boolean validate(EClass eClass, EObject eObject, Map<Object, Object> context, String constraint, String expression);

    /**
     * Evaluates the given constraint expression against the value in the given context.
     * @return the result of the expression evaluation.
     */
    boolean validate(EDataType eDataType, Object value, Map<Object, Object> context, String constraint, String expression);
  }

  /**
   * Validates the object in the given context, optionally producing diagnostics.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether the object is valid.
   */
  boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context);

  boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context);

  boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context);
}
