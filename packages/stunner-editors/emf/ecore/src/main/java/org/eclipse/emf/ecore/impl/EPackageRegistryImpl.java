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
package org.eclipse.emf.ecore.impl;


import java.util.HashMap;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;



/**
 * An implementation of a package registry that can delegate failed lookup to another registry.
 */
public class EPackageRegistryImpl extends HashMap<String, Object> implements EPackage.Registry
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates the {@link EPackage.Registry#INSTANCE instance} of the global registry.
   * If a {@link System#getSecurityManager() security manager} is active,
   * and <code>"classLoader"</code> {@link RuntimePermission permission} is not granted,
   * a secure delegator instance is created,
   * i.e., a private registry implementation that securely accesses class loaders 
   * and keeps them private, will be used.
   */
  public static EPackage.Registry createGlobalRegistry()
  {
    return new EPackageRegistryImpl();
  }

  /** 
   * The delegate registry.
   */
  protected EPackage.Registry delegateRegistry;

  /**
   * Creates a non-delegating instance.
   */
  public EPackageRegistryImpl()
  {
    super();
  }

  /**
   * Creates a delegating instance.
   */
  public EPackageRegistryImpl(EPackage.Registry delegateRegistry)
  {
    this.delegateRegistry = delegateRegistry;
  }

  /*
   * Javadoc copied from interface.
   */
  public EPackage getEPackage(String nsURI)
  {
    Object ePackage = get(nsURI);
    if (ePackage instanceof EPackage)
    {
      EPackage result = (EPackage)ePackage;
      if (result.getNsURI() == null)
      {
        initialize(result);
      }
      return result;
    }
    else if (ePackage instanceof EPackage.Descriptor)
    {
      EPackage.Descriptor ePackageDescriptor = (EPackage.Descriptor)ePackage;
      EPackage result = ePackageDescriptor.getEPackage();
      if (result != null)
      {
        if (result.getNsURI() == null)
        {
          initialize(result);
        }
        else
        {
          put(nsURI, result);
        }
      }
      return result;
    }
    else
    {
      return delegatedGetEPackage(nsURI);
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public EFactory getEFactory(String nsURI)
  {
    Object ePackage = get(nsURI);
    if (ePackage instanceof EPackage)
    {
      EPackage result = (EPackage)ePackage;
      if (result.getNsURI() == null)
      {
        initialize(result);
      }
      return result.getEFactoryInstance();
    }
    else if (ePackage instanceof EPackage.Descriptor)
    {
      EPackage.Descriptor ePackageDescriptor = (EPackage.Descriptor)ePackage;
      EFactory result = ePackageDescriptor.getEFactory();
      return result;
    }
    else
    {
      return delegatedGetEFactory(nsURI);
    }
  }

  /**
   * Creates a delegating instance.
   */
  protected void initialize(EPackage ePackage)
  {
    // Do nothing.
  }

  /**
   * Returns the package from the delegate registry, if there is one.
   * @return the package from the delegate registry.
   */
  protected EPackage delegatedGetEPackage(String nsURI)
  {
    if (delegateRegistry != null)
    {
      return delegateRegistry.getEPackage(nsURI);
    }

    return null;
  }

  /**
   * Returns the factory from the delegate registry, if there is one.
   * @return the factory from the delegate registry.
   */
  protected EFactory delegatedGetEFactory(String nsURI)
  {
    if (delegateRegistry != null)
    {
      return delegateRegistry.getEFactory(nsURI);
    }

    return null;
  }

  /**
   * Returns whether this map or the delegate map contains this key. Note that
   * if there is a delegate map, the result of this method may
   * <em><b>not</b></em> be the same as <code>keySet().contains(key)</code>.
   * @param key the key whose presence in this map is to be tested.
   * @return whether this map or the delegate map contains this key.
   */
  @Override
  public boolean containsKey(Object key)
  {
    return super.containsKey(key) || delegateRegistry != null && delegateRegistry.containsKey(key);
  }
}
