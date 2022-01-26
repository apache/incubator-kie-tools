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


public class EObjectWithInverseResolvingEList<E> extends EObjectWithInverseEList<E>
{
  private static final long serialVersionUID = 1L;

  public static class Unsettable<E> extends EObjectWithInverseEList.Unsettable<E>
  {
    private static final long serialVersionUID = 1L;

    public static class ManyInverse<E> extends EObjectWithInverseResolvingEList.Unsettable<E>
    {
      private static final long serialVersionUID = 1L;

      public ManyInverse(Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
      {
        super(dataClass, owner, featureID, inverseFeatureID);
      }

      @Override
      protected boolean hasManyInverse()
      {
        return true;
      }
    }

    public Unsettable(Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
    {
      super(dataClass, owner, featureID, inverseFeatureID);
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

  public static class ManyInverse<E> extends EObjectWithInverseResolvingEList<E>
  {
    private static final long serialVersionUID = 1L;

    public ManyInverse(Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
    {
      super(dataClass, owner, featureID, inverseFeatureID);
    }

    @Override
    protected boolean hasManyInverse()
    {
      return true;
    }
  }

  public EObjectWithInverseResolvingEList
    (Class<?> dataClass, InternalEObject owner, int featureID, int inverseFeatureID)
  {
    super(dataClass, owner, featureID, inverseFeatureID);
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
