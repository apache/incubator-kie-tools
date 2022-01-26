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
package org.eclipse.emf.ecore.impl;


import java.util.HashMap;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EObjectValidator;


/**
 * An implementation of a validator registry.
 */
public class EValidatorRegistryImpl extends HashMap<EPackage, Object> implements EValidator.Registry
{
  private static final long serialVersionUID = 1L;

  protected EValidator.Registry delegateRegistry;

  public EValidatorRegistryImpl()
  {
    super();
  }

  public EValidatorRegistryImpl(EValidator.Registry delegateRegistry)
  {
    this.delegateRegistry = delegateRegistry;
  }

  @Override
  public Object get(Object key)
  {
    Object eValidator = super.get(key);
    if (eValidator instanceof EValidator.Descriptor)
    {
      EValidator.Descriptor eValidatorDescriptor = (EValidator.Descriptor)eValidator;
      eValidator = eValidatorDescriptor.getEValidator();
      put((EPackage)key, eValidator);
      return eValidator;
    }
    else if (eValidator != null)
    {
      return eValidator;
    }
    else
    {
      return delegatedGet(key);
    }
  }

  public EValidator getEValidator(EPackage ePackage)
  {
    return (EValidator)get(ePackage);
  }

  protected Object delegatedGet(Object key)
  {
    if (delegateRegistry != null)
    {
      return delegateRegistry.get(key);
    }

    return key == null ? EObjectValidator.INSTANCE : null;
  }

  @Override
  public boolean containsKey(Object key)
  {
    return super.containsKey(key) || delegateRegistry != null && delegateRegistry.containsKey(key);
  }
}
