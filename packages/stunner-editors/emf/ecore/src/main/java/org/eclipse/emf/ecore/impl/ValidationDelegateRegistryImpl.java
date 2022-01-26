/**
 * Copyright (c) 2009-2010 Kenn Hussey and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Kenn Hussey - Initial API and implementation
 */
package org.eclipse.emf.ecore.impl;

import java.util.HashMap;

import org.eclipse.emf.ecore.EValidator;


/**
 * An implementation of a validation delegate registry.
 */
public class ValidationDelegateRegistryImpl extends HashMap<String, Object> implements EValidator.ValidationDelegate.Registry
{
  private static final long serialVersionUID = 1L;

  protected EValidator.ValidationDelegate.Registry delegateRegistry;

  public ValidationDelegateRegistryImpl()
  {
    super();
  }

  public ValidationDelegateRegistryImpl(EValidator.ValidationDelegate.Registry delegateRegistry)
  {
    this.delegateRegistry = delegateRegistry;
  }

  @Override
  public Object get(Object key)
  {
    Object validationDelegate = super.get(key);
    if (validationDelegate instanceof EValidator.ValidationDelegate.Descriptor)
    {
      EValidator.ValidationDelegate.Descriptor validationDelegateDescriptor = (EValidator.ValidationDelegate.Descriptor)validationDelegate;
      validationDelegate = validationDelegateDescriptor.getValidationDelegate();
      put((String)key, validationDelegate);
      return validationDelegate;
    }
    else if (validationDelegate != null)
    {
      return validationDelegate;
    }
    else
    {
      return delegatedGet(key);
    }
  }

  public EValidator.ValidationDelegate getValidationDelegate(String uri)
  {
    return (EValidator.ValidationDelegate)get(uri);
  }

  protected Object delegatedGet(Object key)
  {
    if (delegateRegistry != null)
    {
      return delegateRegistry.get(key);
    }

    return null;
  }

  @Override
  public boolean containsKey(Object key)
  {
    return super.containsKey(key) || delegateRegistry != null && delegateRegistry.containsKey(key);
  }
}
