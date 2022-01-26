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


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;


public class EContentsEList<E> extends AbstractSequentialInternalEList<E> implements EList<E>, InternalEList<E>
{
  public static final EContentsEList<?> EMPTY_CONTENTS_ELIST = 
    new EContentsEList<Object>(null, (EStructuralFeature [])null)
    {
      @Override
      public List<Object> basicList()
      {
        return this;
      }
    }; 
    
  @SuppressWarnings("unchecked")
  public static <T> EContentsEList<T> emptyContentsEList()
  {
    return (EContentsEList<T>)EMPTY_CONTENTS_ELIST;
  }

  public static <T> EContentsEList<T> createEContentsEList(EObject eObject)
  {
    EStructuralFeature [] eStructuralFeatures = 
      ((EClassImpl.FeatureSubsetSupplier)eObject.eClass().getEAllStructuralFeatures()).containments();
    
    return 
      eStructuralFeatures == null ?
        EContentsEList.<T>emptyContentsEList() :
        new EContentsEList<T>(eObject, eStructuralFeatures);
  }

  protected final EObject eObject; 
  protected final EStructuralFeature [] eStructuralFeatures;

  public EContentsEList(EObject eObject)
  {
    this.eObject = eObject;
    this.eStructuralFeatures = 
      ((EClassImpl.FeatureSubsetSupplier)eObject.eClass().getEAllStructuralFeatures()).containments();
  }

  public EContentsEList(EObject eObject, List<? extends EStructuralFeature> eStructuralFeatures)
  {
    this.eObject = eObject;
    this.eStructuralFeatures = new EStructuralFeature [eStructuralFeatures.size()];
    eStructuralFeatures.toArray(this.eStructuralFeatures);
  }

  public EContentsEList(EObject eObject, EStructuralFeature [] eStructuralFeatures)
  {
    this.eObject = eObject;
    this.eStructuralFeatures = eStructuralFeatures;
  }

  protected ListIterator<E> newListIterator()
  {
    return resolve() ? newResolvingListIterator() : newNonResolvingListIterator();
  }
  
  protected ListIterator<E> newResolvingListIterator()
  {
    return new ResolvingFeatureIteratorImpl<E>(eObject, eStructuralFeatures);
  }
  
  protected ListIterator<E> newNonResolvingListIterator()
  {
    return new FeatureIteratorImpl<E>(eObject, eStructuralFeatures);
  }

  protected Iterator<E> newIterator()
  {
    return newListIterator();
  }

  protected boolean useIsSet()
  {
    return true;
  }

  protected boolean resolve()
  {
    return true;
  }

  protected boolean isIncluded(EStructuralFeature eStructuralFeature)
  {
    return true;
  }

  protected boolean isIncludedEntry(EStructuralFeature eStructuralFeature)
  {
    return eStructuralFeature instanceof EReference && ((EReference)eStructuralFeature).isContainment();
  }

  @Override
  public ListIterator<E> listIterator(int index)
  {
    if (eStructuralFeatures == null)
    {
      if (index != 0)
      {
        throw new IndexOutOfBoundsException("index=" + index + ", size=0");
      }

      return FeatureIteratorImpl.emptyIterator();
    }

    ListIterator<E> result = newListIterator();
    for (int i = 0; i < index; ++i)
    {
      result.next();
    }
    return result;
  }

  @Override
  public Iterator<E> iterator()
  {
    if (eStructuralFeatures == null)
    {
      return FeatureIteratorImpl.emptyIterator();
    }

    Iterator<E> result = newIterator();
    return result;
  }

  @Override
  public int size()
  {
    int result = 0;
    if (eStructuralFeatures != null)
    {
      for (int i = 0; i < eStructuralFeatures.length; ++i)
      {
        EStructuralFeature feature = eStructuralFeatures[i];
        if (isIncluded(feature) && (!useIsSet() || eObject.eIsSet(feature)))
        {
          Object value = eObject.eGet(feature, false);
          if (FeatureMapUtil.isFeatureMap(feature))
          {
            FeatureMap featureMap = (FeatureMap)value;
            for (int j = 0, size = featureMap.size(); j < size; ++j)
            {
              if (isIncludedEntry(featureMap.getEStructuralFeature(j)) && featureMap.getValue(j) != null)
              {
                ++result;
              }
            }
          }
          else if (feature.isMany())
          {
            result += ((Collection<?>)value).size();
          } 
          else if (value != null)
          {
            ++result;
          }
        }
      }
    }
    return result;
  }

  @Override
  public boolean isEmpty()
  {
    if (eStructuralFeatures != null)
    {
      for (int i = 0; i < eStructuralFeatures.length; ++i)
      {
        EStructuralFeature feature = eStructuralFeatures[i];
        if (isIncluded(feature) && (!useIsSet() || eObject.eIsSet(feature)))
        {
          Object value = eObject.eGet(feature, false);
          if (FeatureMapUtil.isFeatureMap(feature))
          {
            FeatureMap featureMap = (FeatureMap)value;
            for (int j = 0, size = featureMap.size(); j < size; ++j)
            {
              if (isIncludedEntry(featureMap.getEStructuralFeature(j)) && featureMap.getValue(j) != null)
              {
                return false;
              }
            }
          }
          else if (feature.isMany())
          {
            if (!((Collection<?>)value).isEmpty())
            {
              return false;
            }
          } 
          else if (value != null)
          {
            return false;
          }
        }
      }
    }
    return true;
  }

  @Override
  public void move(int newPosition, Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public E move(int newPosition, int oldPosition)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public E basicGet(int index)
  {
    return basicList().get(index);
  }

  @Override
  public List<E> basicList()
  {
    return
      new EContentsEList<E>(eObject, eStructuralFeatures)
      {
        @Override
        protected boolean resolve()
        {
          return false;
        }
      };
  }

  @Override
  public Iterator<E> basicIterator()
  {
    if (eStructuralFeatures == null)
    {
      return FeatureIteratorImpl.emptyIterator();
    }

    return newNonResolvingListIterator();
  }

  @Override
  public ListIterator<E> basicListIterator()
  {
    if (eStructuralFeatures == null)
    {
      return FeatureIteratorImpl.emptyIterator();
    }

    return newNonResolvingListIterator();
  }

  @Override
  public ListIterator<E> basicListIterator(int index)
  {
    if (eStructuralFeatures == null)
    {
      if (index < 0 || index > 1)
      {
        throw new IndexOutOfBoundsException("index=" + index + ", size=0");
      }

      return FeatureIteratorImpl.emptyIterator();
    }

    ListIterator<E> result = newNonResolvingListIterator();
    for (int i = 0; i < index; ++i)
    {
      result.next();
    }
    return result;
  }

  public interface FeatureIterator<E> extends Iterator<E>
  {
    EStructuralFeature feature();
  }

  public interface FeatureListIterator<E> extends FeatureIterator<E>, ListIterator<E>
  {
    // No new methods.
  }

  public static class FeatureIteratorImpl<E> implements FeatureListIterator<E>
  {
    protected final EObject eObject; 
    protected final EStructuralFeature [] eStructuralFeatures;
    protected int featureCursor;
    protected int cursor;
    protected int prepared;
    protected E preparedResult;
    protected EStructuralFeature preparedFeature;
    protected EStructuralFeature feature;
    protected boolean isHandlingFeatureMap;
    protected ListIterator<E> values;
    protected InternalEList<E> valueInternalEList;
    protected List<E> valueList;
    protected int valueListSize;
    protected int valueListIndex;

    public FeatureIteratorImpl(EObject eObject, List<? extends EStructuralFeature> eStructuralFeatures)
    {
      this.eObject = eObject;
      this.eStructuralFeatures = new EStructuralFeature [eStructuralFeatures.size()];
      eStructuralFeatures.toArray(this.eStructuralFeatures);
    }

    public FeatureIteratorImpl(EObject eObject, EStructuralFeature [] eStructuralFeatures)
    {
      this.eObject = eObject;
      this.eStructuralFeatures = eStructuralFeatures;
    }

    protected boolean resolve()
    {
      return false;
    }

    protected boolean useIsSet()
    {
      return true;
    }

    protected boolean isIncluded(EStructuralFeature eStructuralFeature)
    {
      return true;
    }

    protected boolean isIncludedEntry(EStructuralFeature eStructuralFeature)
    {
      return eStructuralFeature instanceof EReference && ((EReference)eStructuralFeature).isContainment();
    }

    public EStructuralFeature feature()
    {
      return feature;
    }

    public boolean hasNext()
    {
      switch (prepared)
      {
        case 3:
        case 2:
        {
          return true;
        }
        case 1:
        {
          return false;
        }
        case -3:
        {
          // Undo the preparation for previous and continue.
          if (values == null)
          {
            ++valueListIndex;
          }
          else
          {
            values.next();
          }
        }
        default:
        {
          if (valueList == null || (values == null ? !scanNext() : !scanNext(values)))
          {
            while (featureCursor < eStructuralFeatures.length)
            {
              EStructuralFeature feature = eStructuralFeatures[featureCursor++];
              if (isIncluded(feature) && (!useIsSet() || eObject.eIsSet(feature)))
              {
                Object value = eObject.eGet(feature, resolve());
                isHandlingFeatureMap = FeatureMapUtil.isFeatureMap(feature);
                if (isHandlingFeatureMap || feature.isMany())
                {
                  if (resolve())
                  {
                    @SuppressWarnings("unchecked") List<E> newValueList = (List<E>)value;
                    valueList = newValueList;
                  }
                  else
                  {
                    @SuppressWarnings("unchecked") InternalEList<E> newValueList = (InternalEList<E>)value;
                    valueList = valueInternalEList = newValueList;
                  }
                  if (valueList instanceof RandomAccess)
                  {
                    values = null;
                    valueListSize = valueList.size();
                    valueListIndex = 0;
                  }
                  else
                  {
                    values = 
                      valueInternalEList == null ? 
                        valueList.listIterator() : 
                        valueInternalEList.basicListIterator();
                  }
                  if (values == null ? scanNext() : scanNext(values))
                  {
                    Object result = 
                       values == null ? 
                         valueInternalEList == null ? 
                           valueList.get(valueListIndex++) : 
                           valueInternalEList.basicGet(valueListIndex++) : 
                         values.next();
                    if (isHandlingFeatureMap)
                    {
                      FeatureMap.Entry entry = (FeatureMap.Entry)result; 
                      preparedFeature = entry.getEStructuralFeature();
                      @SuppressWarnings("unchecked") E newPreparedResult = (E)entry.getValue();
                      preparedResult = newPreparedResult;
                    }
                    else
                    {
                      @SuppressWarnings("unchecked") E newPreparedResult = (E)result;
                      preparedResult = newPreparedResult;
                      preparedFeature = feature;
                    }
                    prepared = 3;
                    return true;
                  }
                }
                else if (value != null)
                {
                  valueList = null;
                  values = null;
                  @SuppressWarnings("unchecked") E newPreparedResult = (E)value;
                  preparedResult = newPreparedResult;
                  preparedFeature = feature;
                  prepared = 2;
                  return true;
                }
              }
            }
            valueList = null;
            values = null;
            isHandlingFeatureMap = false;
            prepared = 1;
            return false;
          }
          else
          {
            Object result =
              values == null ? 
                valueInternalEList == null ? 
                  valueList.get(valueListIndex++) : 
                  valueInternalEList.basicGet(valueListIndex++) : 
                values.next();
            if (isHandlingFeatureMap)
            {
              FeatureMap.Entry entry = (FeatureMap.Entry)result; 
              preparedFeature = entry.getEStructuralFeature();
              @SuppressWarnings("unchecked") E newPreparedResult = (E)entry.getValue();
              preparedResult = newPreparedResult;
            }
            else
            {
              @SuppressWarnings("unchecked") E newPreparedResult = (E)result;
              preparedResult = newPreparedResult;
            }
            prepared = 3;
            return true;
          }
        }
      }
    }

    protected boolean scanNext(ListIterator<E> values)
    {
      if (isHandlingFeatureMap)
      {
        while (values.hasNext())
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)values.next();
          EStructuralFeature entryFeature = entry.getEStructuralFeature();
          if (isIncludedEntry(entryFeature) && entry.getValue() != null)
          {
            values.previous();
            return true;
          }
        }
        return false;
      }
      else
      {
        return values.hasNext();
      }
    }

    protected boolean scanNext()
    {
      if (isHandlingFeatureMap)
      {
        while (valueListIndex < valueListSize)
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)valueList.get(valueListIndex);
          EStructuralFeature entryFeature = entry.getEStructuralFeature();
          if (isIncludedEntry(entryFeature) && entry.getValue() != null)
          {
            return true;
          }
          else
          {
            ++valueListIndex;
          }
        }
        return false;
      }
      else
      {
        return valueListIndex < valueListSize;
      }
    }

    public E next()
    {
      if (prepared > 1 || hasNext())
      {
        ++cursor;
        prepared = 0;
        feature = preparedFeature;
        E result = preparedResult;
        hasNext();
        return result;
      }
      else
      {
        throw new NoSuchElementException();
      }
    }

    public int nextIndex()
    {
      return cursor;
    }

    public boolean hasPrevious()
    {
      switch (prepared)
      {
        case -3:
        case -2:
        {
          return true;
        }
        case -1:
        {
          return false;
        }
        case 3:
        {
          // Undo the preparation for next and continue.
          if (values == null)
          {
            --valueListIndex;
          }
          else
          {
            values.previous();
          }
        }
        default:
        {
          if (valueList == null || (values == null ? !scanPrevious() : !scanPrevious(values)))
          {
            while (featureCursor > 0)
            {
              EStructuralFeature feature = eStructuralFeatures[--featureCursor];
              if (isIncluded(feature) && (!useIsSet() || eObject.eIsSet(feature)))
              {
                Object value = eObject.eGet(feature, resolve());
                isHandlingFeatureMap = FeatureMapUtil.isFeatureMap(feature);
                if (isHandlingFeatureMap || feature.isMany())
                {
                  if (resolve())
                  {
                    @SuppressWarnings("unchecked") List<E> newValueList = (List<E>)value;
                    valueList = newValueList;
                  }
                  else
                  {
                    @SuppressWarnings("unchecked") InternalEList<E> newValueList = (InternalEList<E>)value;
                    valueList = valueInternalEList = newValueList;
                  }
                  if (valueList instanceof RandomAccess)
                  {
                    valueListSize = valueList.size();
                    valueListIndex = valueListSize;
                  }
                  else
                  {
                    values = 
                      valueInternalEList == null ? 
                        valueList.listIterator(valueList.size()) : 
                        valueInternalEList.basicListIterator(valueList.size());
                  }
                  if (values == null ? scanPrevious() : scanPrevious(values))
                  {
                    Object result =
                      values == null ? 
                        valueInternalEList == null ? 
                          valueList.get(--valueListIndex) : 
                          valueInternalEList.basicGet(--valueListIndex) : 
                        values.previous();
                    if (isHandlingFeatureMap)
                    {
                      FeatureMap.Entry entry = (FeatureMap.Entry)result; 
                      preparedFeature = entry.getEStructuralFeature();
                      @SuppressWarnings("unchecked") E newPreparedResult = (E)entry.getValue();
                      preparedResult = newPreparedResult;
                    }
                    else
                    {
                      @SuppressWarnings("unchecked") E newPreparedResult = (E)result;
                      preparedResult = newPreparedResult;
                      preparedFeature = feature;
                    }
                    prepared = -3;
                    return true;
                  }
                }
                else if (value != null)
                {
                  valueList = null;
                  values = null;
                  @SuppressWarnings("unchecked") E newPreparedResult = (E)value;
                  preparedResult = newPreparedResult;
                  preparedFeature = feature;
                  prepared = -2;
                  return true;
                }
              }
            }
            valueList = null;
            values = null;
            prepared = -1;
            return false;
          }
          else
          {
            Object result =
              values == null ? 
                valueInternalEList == null ? 
                  valueList.get(--valueListIndex) : 
                  valueInternalEList.basicGet(--valueListIndex) : 
                values.previous();
            if (isHandlingFeatureMap)
            {
              FeatureMap.Entry entry = (FeatureMap.Entry)result; 
              preparedFeature = entry.getEStructuralFeature();
              @SuppressWarnings("unchecked") E newPreparedResult = (E)entry.getValue();
              preparedResult = newPreparedResult;
            }
            else
            {
              @SuppressWarnings("unchecked") E newPreparedResult = (E)result;
              preparedResult = newPreparedResult;
            }
            prepared = -3;
            return true;
          }
        }
      }
    }

    protected boolean scanPrevious(ListIterator<E> values)
    {
      if (isHandlingFeatureMap)
      {
        while (values.hasPrevious())
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)values.previous();
          EStructuralFeature entryFeature = entry.getEStructuralFeature();
          if (isIncludedEntry(entryFeature) && entry.getValue() != null)
          {
            values.next();
            return true;
          }
        }
        return false;
      }
      else
      {
        return values.hasPrevious();
      }
    }

    protected boolean scanPrevious()
    {
      if (isHandlingFeatureMap)
      {
        while (valueListIndex > 0)
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)valueList.get(valueListIndex - 1);
          EStructuralFeature entryFeature = entry.getEStructuralFeature();
          if (isIncludedEntry(entryFeature) && entry.getValue() != null)
          {
            return true;
          }
          else
          {
            --valueListIndex;
          }
        }
        return false;
      }
      else
      {
        return valueListIndex > 0;
      }
    }

    public E previous()
    {
      if (prepared < -1 || hasPrevious())
      {
        --cursor;
        prepared = 0;
        feature = preparedFeature;
        E result = preparedResult;
        hasPrevious();
        return result;
      }
      else
      {
        throw new NoSuchElementException();
      }
    }

    public int previousIndex()
    {
      return cursor - 1;
    }

    public void add(Object o)
    {
      throw new UnsupportedOperationException();
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    public void set(Object o)
    {
      throw new UnsupportedOperationException();
    }

    public static final ListIterator<?> EMPTY_ITERATOR = 
      new FeatureIteratorImpl<Object>(null, (EStructuralFeature [] )null)
      {
        @Override
        public boolean hasNext()
        {
          return false;
        }

        @Override
        public boolean hasPrevious()
        {
          return false;
        }
      };
     
    @SuppressWarnings("unchecked")
    public static <T> ListIterator<T> emptyIterator()
    {
      return (ListIterator<T>)EMPTY_ITERATOR;
    }
  }

  public static class ResolvingFeatureIteratorImpl<E> extends FeatureIteratorImpl<E>
  {
    public ResolvingFeatureIteratorImpl(EObject eObject, List<? extends EStructuralFeature> eStructuralFeatures)
    {
      super(eObject, eStructuralFeatures);
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
}
