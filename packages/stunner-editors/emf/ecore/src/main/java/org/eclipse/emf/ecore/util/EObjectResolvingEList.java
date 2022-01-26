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
package org.eclipse.emf.ecore.util;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;


public class EObjectResolvingEList<E> extends EObjectEList<E>
{
  private static final long serialVersionUID = 1L;

  public static class Unsettable<E> extends EObjectEList.Unsettable<E>
  {
    private static final long serialVersionUID = 1L;

    public Unsettable(Class<?> dataClass, InternalEObject owner, int featureID)
    {
      super(dataClass, owner, featureID);
    }

    @Override
    protected boolean hasProxies()
    {
      return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected E resolve(int index, E object)
    {
      return (E)resolve(index, (EObject)object);
    }
  }

  public EObjectResolvingEList(Class<?> dataClass, InternalEObject owner, int featureID)
  {
    super(dataClass, owner, featureID);
  }

  @Override
  protected boolean hasProxies()
  {
    return true;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected E resolve(int index, E object)
  {
    return (E)resolve(index, (EObject)object);
  }
}
