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


import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;


/**
 * A virtual list of all the cross references of an EObject.
 */
public class ECrossReferenceEList<E> extends EContentsEList<E>
{
  public static final ECrossReferenceEList<?> EMPTY_CROSS_REFERENCE_ELIST = 
    new ECrossReferenceEList<Object>(null, (EStructuralFeature [])null)
    {
      @Override
      public List<Object> basicList()
      {
        return this;
      }
    };
  
  @SuppressWarnings("unchecked")
  public static <T> ECrossReferenceEList<T> emptyCrossReferenceEList()
  {
    return (ECrossReferenceEList<T>)EMPTY_CROSS_REFERENCE_ELIST;
  }

  public static <T> ECrossReferenceEList<T> createECrossReferenceEList(EObject eObject)
  {
    EStructuralFeature [] eStructuralFeatures = 
      ((EClassImpl.FeatureSubsetSupplier)eObject.eClass().getEAllStructuralFeatures()).crossReferences();
    
    return 
      eStructuralFeatures == null ?
        ECrossReferenceEList.<T>emptyCrossReferenceEList() :
        new ECrossReferenceEList<T>(eObject, eStructuralFeatures);
  }

  public ECrossReferenceEList(EObject eObject)
  {
    super
      (eObject, 
       ((EClassImpl.FeatureSubsetSupplier)eObject.eClass().getEAllStructuralFeatures()).crossReferences());
  }

  protected ECrossReferenceEList(EObject eObject, EStructuralFeature [] eStructuralFeatures)
  {
    super(eObject, eStructuralFeatures);
  }

  public static class FeatureIteratorImpl<E> extends EContentsEList.FeatureIteratorImpl<E>
  {
    protected static final EStructuralFeature[] NO_FEATURES = new EStructuralFeature [0];
    
    public FeatureIteratorImpl(EObject eObject)
    {
      this(eObject, ((EClassImpl.FeatureSubsetSupplier)eObject.eClass().getEAllStructuralFeatures()).crossReferences());
    }

    public FeatureIteratorImpl(EObject eObject, EStructuralFeature [] eStructuralFeatures)
    {
      super(eObject, eStructuralFeatures == null ? NO_FEATURES : eStructuralFeatures);
    }

    @Override
    protected boolean isIncludedEntry(EStructuralFeature eStructuralFeature)
    {
      if (eStructuralFeature instanceof EReference)
      {
        EReference eReference = (EReference)eStructuralFeature;
        return !eReference.isContainment() && !eReference.isContainer();
      }
      else
      {
        return false;
      }
    }
  }

  public static class ResolvingFeatureIteratorImpl<E> extends FeatureIteratorImpl<E>
  {
    public ResolvingFeatureIteratorImpl(EObject eObject)
    {
      super(eObject);
    }

    public ResolvingFeatureIteratorImpl(EObject eObject, EStructuralFeature [] eStructuralFeatures)
    {
      super(eObject, eStructuralFeatures);
    }

    @Override
    protected boolean resolve()
    {
      return true;
    }
  }

  @Override
  protected boolean isIncluded(EStructuralFeature eStructuralFeature)
  {
    if (FeatureMapUtil.isFeatureMap(eStructuralFeature))
    {
      return true;
    }
    else
    {
      EReference eReference = (EReference)eStructuralFeature;
      return !eReference.isContainment() && !eReference.isContainer();
    }
  }

  @Override
  protected boolean isIncludedEntry(EStructuralFeature eStructuralFeature)
  {
    if (eStructuralFeature instanceof EReference)
    {
      EReference eReference = (EReference)eStructuralFeature;
      return !eReference.isContainment() && !eReference.isContainer();
    }
    else
    {
      return false;
    }
  }

  @Override
  protected ListIterator<E> newResolvingListIterator()
  {
    return new ResolvingFeatureIteratorImpl<E>(eObject, eStructuralFeatures);
  }
  
  @Override
  protected ListIterator<E> newNonResolvingListIterator()
  {
    return new FeatureIteratorImpl<E>(eObject, eStructuralFeatures);
  }

  @Override
  public List<E> basicList()
  {
    return
      new ECrossReferenceEList<E>(eObject, eStructuralFeatures)
      {
        @Override
        protected boolean resolve()
        {
          return false;
        }
      };
  }
}
