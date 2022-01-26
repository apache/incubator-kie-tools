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
package org.eclipse.emf.ecore.util;


//import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * A validity checker for basic EObject constraints.
 */
public class Diagnostician implements EValidator.SubstitutionLabelProvider, EValidator
{
  public static final Diagnostician INSTANCE = new Diagnostician();

  protected EValidator.Registry eValidatorRegistry;

  public Diagnostician(EValidator.Registry eValidatorRegistry)
  {
    this.eValidatorRegistry = eValidatorRegistry;
  }

  public Diagnostician()
  {
    this(EValidator.Registry.INSTANCE);
  }

  public String getObjectLabel(EObject eObject)
  {
    return EcoreUtil.getIdentification(eObject);
  }

  public String getFeatureLabel(EStructuralFeature eStructuralFeature)
  {
    return eStructuralFeature.getName();
  }

  public String getValueLabel(EDataType eDataType, Object value)
  {
    return EcoreUtil.convertToString(eDataType, value);
  }

  /**
   * @since 2.4
   */
  public Map<Object, Object> createDefaultContext()
  {
    Map<Object, Object> context = new HashMap<Object, Object>();
    context.put(EValidator.SubstitutionLabelProvider.class, this);
    context.put(EValidator.class, this);
    return context;
  }

  /**
   * @since 2.4
   */
  public BasicDiagnostic createDefaultDiagnostic(EObject eObject)
  {
    return
      new BasicDiagnostic
        (EObjectValidator.DIAGNOSTIC_SOURCE,
         0,
         EcorePlugin.INSTANCE.getString("_UI_DiagnosticRoot_diagnostic", new Object[] { getObjectLabel(eObject) }),
         new Object [] { eObject });    
  }

  /**
   * @since 2.4
   */
  public BasicDiagnostic createDefaultDiagnostic(EDataType eDataType, Object value)
  {
    return
      new BasicDiagnostic
        (EObjectValidator.DIAGNOSTIC_SOURCE,
         0,
         EcorePlugin.INSTANCE.getString("_UI_DiagnosticRoot_diagnostic", new Object [] { getValueLabel(eDataType, value) }),
         new Object [] { value, eDataType });    
  }

  public Diagnostic validate(EObject eObject)
  {
    BasicDiagnostic diagnostics = createDefaultDiagnostic(eObject);
    validate(eObject, diagnostics, createDefaultContext());
    return diagnostics;
  }

  /**
   * @since 2.4
   */
  public Diagnostic validate(EObject eObject, Map<?, ?> contextEntries)
  {
    BasicDiagnostic diagnostics = createDefaultDiagnostic(eObject);
    Map<Object, Object> context = createDefaultContext();
    context.putAll(contextEntries);
    validate(eObject, diagnostics, context);
    return diagnostics;    
  }

  /**
   * Validates the object in the given context, optionally producing diagnostics.
   * @param eObject the object to validate.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @return whether the object is valid.
   */
  public boolean validate(EObject eObject, DiagnosticChain diagnostics)
  {
    return validate(eObject, diagnostics, createDefaultContext());
  }

  public boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate(eObject.eClass(), eObject, diagnostics, context); 
  }

  public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    Object eValidator;
    EClass eType = eClass;
    while ((eValidator = eValidatorRegistry.get(eType.eContainer())) == null)
    {
      List<EClass> eSuperTypes = eType.getESuperTypes();
      if (eSuperTypes.isEmpty())
      {
        eValidator = eValidatorRegistry.get(null);
        break;
      }
      else
      {
        eType = eSuperTypes.get(0);
      }
    }
    boolean circular = context.get(EObjectValidator.ROOT_OBJECT) == eObject;
    @SuppressWarnings("null")
    boolean result = ((EValidator)eValidator).validate(eClass, eObject, diagnostics, context);
    if ((result || diagnostics != null) && !circular)
    {
      result &= doValidateContents(eObject, diagnostics, context);
    }
    return result;
  }

  protected boolean doValidateContents(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    List<EObject> eContents = eObject.eContents();
    if (!eContents.isEmpty())
    {
      Iterator<EObject> i = eContents.iterator(); 
      EObject child = i.next();
      boolean result = validate(child, diagnostics, context);
      while (i.hasNext() && (result || diagnostics != null))
      {
        child = i.next();
        result &= validate(child, diagnostics, context);
      }
      return result;
    }
    else
    {
      return true;
    }
  }

  public Diagnostic validate(EDataType eDataType, Object value)
  {
    BasicDiagnostic diagnostics = createDefaultDiagnostic(eDataType, value);
    validate(eDataType, value, diagnostics, createDefaultContext());
    return diagnostics;
  }

  public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    Object eValidator = eValidatorRegistry.get(eDataType.eContainer());
    if (eValidator == null)
    {
      eValidator = eValidatorRegistry.get(null);
    }

    return ((EValidator)eValidator).validate(eDataType, value, diagnostics, context);
  }
}
