/**
 * Copyright (c) 2008-2010 Zeligsoft Inc. and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Zeligsoft - Initial API and implementation
 */

package org.eclipse.emf.ecore.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;


/**
 * A basic implementation of the dynamic operation-invocation delegate API.  In
 * fact, it is so basic that it isn't much an implementation at all, but merely
 * throws {@link UnsupportedOperationException} on every invocation, except for
 * the operations defined for the {@link EObject} class.
 * Subclasses should override the {@link #dynamicInvoke(InternalEObject, EList)}
 * method to not do that.
 * 
 * @since 2.6
 */
public class BasicInvocationDelegate implements EOperation.Internal.InvocationDelegate
{
  protected EOperation eOperation;
  
  /**
   * Initializes me with the operation that delegates to me.
   * 
   * @param operation my operation
   */
  public BasicInvocationDelegate(EOperation operation)
  {
    this.eOperation = operation;
  }

  public Object dynamicInvoke(InternalEObject target, EList<?> arguments) throws InvocationTargetException
  {
    if (eOperation.getEContainingClass() == EcorePackage.Literals.EOBJECT)
    {

      switch (eOperation.getEContainingClass().getEAllOperations().indexOf(eOperation))
      {
        case EcorePackage.EOBJECT___ECLASS:
          return target.eClass();
        case EcorePackage.EOBJECT___EIS_PROXY:
          return target.eIsProxy();
        case EcorePackage.EOBJECT___ERESOURCE:
          return target.eResource();
        case EcorePackage.EOBJECT___ECONTAINER:
          return target.eContainer();
        case EcorePackage.EOBJECT___ECONTAINING_FEATURE:
          return target.eContainingFeature();
        case EcorePackage.EOBJECT___ECONTAINMENT_FEATURE:
          return target.eContainmentFeature();
        case EcorePackage.EOBJECT___ECONTENTS:
          return target.eContents();
        case EcorePackage.EOBJECT___EALL_CONTENTS:
          return target.eAllContents();
        case EcorePackage.EOBJECT___ECROSS_REFERENCES:
          return target.eCrossReferences();
        case EcorePackage.EOBJECT___EGET__ESTRUCTURALFEATURE:
          return target.eGet((EStructuralFeature)arguments.get(0));
        case EcorePackage.EOBJECT___EGET__ESTRUCTURALFEATURE_BOOLEAN:
          return target.eGet((EStructuralFeature)arguments.get(0), (Boolean)arguments.get(1));
        case EcorePackage.EOBJECT___ESET__ESTRUCTURALFEATURE_OBJECT:
          target.eSet((EStructuralFeature)arguments.get(0), arguments.get(1));
          return null;
        case EcorePackage.EOBJECT___EIS_SET__ESTRUCTURALFEATURE:
          return target.eIsSet((EStructuralFeature)arguments.get(0));
        case EcorePackage.EOBJECT___EUNSET__ESTRUCTURALFEATURE:
          target.eUnset((EStructuralFeature)arguments.get(0));
          return null;
        case EcorePackage.EOBJECT___EINVOKE__EOPERATION_ELIST:
          return target.eInvoke((EOperation)arguments.get(0), (EList<?>)arguments.get(1));
      }
    }

    throw new UnsupportedOperationException("eInvoke not implemented for " + eOperation.getName());
  }

}
