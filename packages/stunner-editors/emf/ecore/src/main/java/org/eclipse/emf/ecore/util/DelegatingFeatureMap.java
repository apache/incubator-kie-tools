/**
 * Copyright (c) 2003-2010 IBM Corporation and others.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.AbstractEList;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;


public abstract class DelegatingFeatureMap extends DelegatingEcoreEList<FeatureMap.Entry> implements FeatureMap.Internal, FeatureMap.Internal.Wrapper
{
  private static final long serialVersionUID = 1L;

  protected FeatureMap.Internal.Wrapper wrapper = this;
  protected final FeatureMapUtil.Validator featureMapValidator;
  protected final EStructuralFeature eStructuralFeature;

  public DelegatingFeatureMap(InternalEObject owner, int featureID)
  {
    super(owner);
    this.eStructuralFeature = owner.eClass().getEStructuralFeature(featureID);
    featureMapValidator = FeatureMapUtil.getValidator(owner.eClass(), getEStructuralFeature());
  }

  public DelegatingFeatureMap(InternalEObject owner, EStructuralFeature eStructuralFeature)
  {
    super(owner);
    this.eStructuralFeature = eStructuralFeature;
    featureMapValidator = FeatureMapUtil.getValidator(owner.eClass(), getEStructuralFeature());
  }

/*
  List theList = new java.util.ArrayList();
  protected List delegateList()
  {
    return theList;
  }
*/
  
  public Wrapper getWrapper()
  {
    return wrapper;
  }

  public void setWrapper(Wrapper wrapper)
  {
    this.wrapper = wrapper;
  }

  public FeatureMap featureMap()
  {
    return this;
  }
  
  @Override
  protected Entry validate(int index, Entry object)
  {
    Entry result = super.validate(index, object);
    EStructuralFeature eStructuralFeature = object.getEStructuralFeature();
    if (!eStructuralFeature.isChangeable() || !featureMapValidator.isValid(eStructuralFeature))
    {
      throw
        new RuntimeException
          ("Invalid entry feature '" + eStructuralFeature.getEContainingClass().getName() + "." + eStructuralFeature.getName() + "'");
    }
    return result;
  }

  @Override
  protected boolean isEObject()
  {
    return false;
  }

  @Override
  protected boolean isUnique()
  {
    return false;
  }

  @Override
  protected boolean canContainNull()
  {
    return false;
  }

  @Override
  protected EClassifier getFeatureType()
  {
    return org.eclipse.emf.ecore.EcorePackage.Literals.EJAVA_OBJECT;
  }

  @Override
  public EStructuralFeature getEStructuralFeature()
  {
    return eStructuralFeature;
  }

  protected FeatureMap.Entry createEntry(EStructuralFeature eStructuralFeature, Object value)
  {
    return FeatureMapUtil.createEntry(eStructuralFeature, value);
  }

  protected FeatureMap.Entry.Internal createRawEntry(EStructuralFeature eStructuralFeature, Object value)
  {
    return FeatureMapUtil.createRawEntry(eStructuralFeature, value);
  }

  protected NotificationImpl createNotification
    (int eventType, EStructuralFeature feature, Object oldObject, Object newObject, int index, boolean wasSet)
  {
    return new FeatureMapUtil.FeatureENotificationImpl(owner, eventType, feature, oldObject, newObject, index, wasSet);
  }

  protected boolean isMany(EStructuralFeature feature)
  {
    return FeatureMapUtil.isMany(owner, feature);
  }

  @Override
  protected boolean hasInverse()
  {
    return true;
  }

  @Override
  protected boolean hasShadow()
  {
    return true;
  }

  protected int entryIndex(EStructuralFeature feature, int index)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    int count = 0;
    int size = delegateSize();
    int result = size;
    for (int i = 0; i < size; ++i)
    {
      Entry entry = delegateGet(i);
      if (validator.isValid(entry.getEStructuralFeature()))
      {
        if (index == count)
        {
          return i;
        }
        ++count;
        result = i + 1;
      }
    }

    if (index == count)
    {
      return result;
    }
    else
    {
      throw new IndexOutOfBoundsException("index=" + index + ", size=" + count);
    }
  }

  protected boolean isResolveProxies(EStructuralFeature feature)
  {
    return feature instanceof EReference && ((EReference)feature).isResolveProxies();
  }

  public Object resolveProxy(EStructuralFeature feature, int entryIndex, int index, Object object)
  {
    EObject resolved = resolveProxy((EObject)object);
    if (resolved != object)
    {
      Entry oldObject = delegateGet(entryIndex);
      Entry entry = createEntry(feature, resolved);
      delegateSet(entryIndex, validate(entryIndex, entry));
      didSet(entryIndex, entry, oldObject);

      if (isNotificationRequired())
      {
        NotificationImpl notifications = 
          createNotification
            (Notification.RESOLVE, 
             entry.getEStructuralFeature(), 
             object,
             resolved,
             index,
             false);

        notifications.add(createNotification(Notification.RESOLVE, oldObject, entry, index, false));
        notifications.dispatch();
      }

      return resolved;
    }

    return object;
  }

  @Override
  protected EObject resolveProxy(EObject eObject)
  {
    return owner.eResolveProxy((InternalEObject)eObject);
  }

  public int getModCount()
  {
    return 0;
  }

  public EStructuralFeature getEStructuralFeature(int index)
  {
    return get(index).getEStructuralFeature();
  }

  public Object getValue(int index)
  {
    return get(index).getValue();
  }

  public Object setValue(int index, Object value)
  {
    return set(index, createEntry(getEStructuralFeature(index), value)).getValue();
  }

  @Override
  public NotificationChain shadowAdd(Entry object, NotificationChain notifications)
  {
    if (isNotificationRequired())
    {
      EStructuralFeature feature = object.getEStructuralFeature();
      Object value = object.getValue();
      // EATM must fix isSet bits.
      NotificationImpl notification = 
        feature.isMany() ?
          createNotification
            (Notification.ADD,
             feature,
             null, 
             value,
             indexOf(feature, value),
             true) :
          createNotification
            (Notification.SET, 
             feature,
             feature.getDefaultValue(), 
             value,
             Notification.NO_INDEX,
             true);
  
      if (notifications != null)
      {
        notifications.add(notification);
      }
      else
      {
        notifications = notification;
      }
    }
    return notifications;
  }

  @Override
  public NotificationChain inverseAdd(Entry object, NotificationChain notifications)
  {
    FeatureMap.Entry.Internal entry = (FeatureMap.Entry.Internal)object;
    return entry.inverseAdd(owner, getFeatureID(), notifications);
  }

  @Override
  public NotificationChain shadowRemove(Entry object, NotificationChain notifications)
  {
    if (isNotificationRequired())
    {
      EStructuralFeature feature = object.getEStructuralFeature();
      Object value = object.getValue();
      NotificationImpl notification = 
        feature.isMany() ?
          createNotification
            (Notification.REMOVE,
             feature,
             value,
             null, 
             indexOf(feature, value),
             true) :
          createNotification
            (feature.isUnsettable() ? Notification.UNSET : Notification.SET, 
             feature,
             value,
             feature.getDefaultValue(), 
             Notification.NO_INDEX,
             true);

      if (notifications != null)
      {
        notifications.add(notification);
      }
      else
      {
        notifications = notification;
      }
    }
    return notifications;
  }

  @Override
  public NotificationChain inverseRemove(Entry object, NotificationChain notifications)
  {
    FeatureMap.Entry.Internal entry = (FeatureMap.Entry.Internal)object;
    return entry.inverseRemove(owner, getFeatureID(), notifications);
  }

  @Override
  public NotificationChain shadowSet(Entry oldObject, Entry newObject, NotificationChain notifications)
  {
    if (isNotificationRequired())
    {
      EStructuralFeature feature = oldObject.getEStructuralFeature();
      Object oldValue = oldObject.getValue();
      Object newValue = newObject.getValue();
      NotificationImpl notification = 
        createNotification
          (Notification.SET,
           feature,
           oldValue,
           newValue,
           feature.isMany() ? indexOf(feature, newValue) : Notification.NO_INDEX,
           true);

      if (notifications != null)
      {
        notifications.add(notification);
      }
      else
      {
        notifications = notification;
      }
    }
    return notifications;
  }

  public NotificationChain inverseTouch(Object object, NotificationChain notifications)
  {
    if (isNotificationRequired())
    {
      Entry entry = (Entry)object;
      EStructuralFeature feature = entry.getEStructuralFeature();
      Object value = entry.getValue();
      NotificationImpl notification = 
        createNotification
          (Notification.SET,
           feature,
           value, 
           value,
           feature.isMany() ? indexOf(feature, value) : Notification.NO_INDEX,
           true);
  
      if (notifications != null)
      {
        notifications.add(notification);
      }
      else
      {
        notifications = notification;
      }
    }

    return notifications;
  }

  @Override
  public Entry move(int targetIndex, int sourceIndex)
  {
    if (!isNotificationRequired())
    {
      return doMove(targetIndex, sourceIndex);
    }
    else if (targetIndex != sourceIndex)
    {
      Entry sourceEntry =  delegateGet(sourceIndex);
      EStructuralFeature feature = sourceEntry.getEStructuralFeature();
      if (isMany(feature))
      {
        FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
        int featureTargetIndex = -1;
        int featureSourceIndex = -1;
        int count = 0;
        for (int i = 0, maxIndex= targetIndex > sourceIndex ? targetIndex : sourceIndex; i <= maxIndex; ++i)
        {
          if (i == sourceIndex)
          {
            featureSourceIndex = count++;
          }
          else
          {
            Entry entry = delegateGet(i);
            boolean isValid = validator.isValid(entry.getEStructuralFeature());
            if (i == targetIndex)
            {
              featureTargetIndex = i == maxIndex && !isValid ? count-1 : count;
            }
            
            if (isValid)
            {
                ++count;
            }
          }
        }

        Entry result = super.move(targetIndex, sourceIndex);
        
        if (featureSourceIndex != featureTargetIndex)
        {
          dispatchNotification
            (new ENotificationImpl
               (owner, 
                Notification.MOVE, 
                feature,
                featureSourceIndex, 
                sourceEntry.getValue(),
                featureTargetIndex));
        }
        return result;
      }
    }
    return super.move(targetIndex, sourceIndex);
  }

  @Override
  public Entry set(int index, Entry object)
  {
    EStructuralFeature entryFeature = object.getEStructuralFeature();
    if (isMany(entryFeature))
    {
      if (entryFeature.isUnique())
      {
        for (int i = 0, size = delegateSize(); i < size; ++i)
        {
          Entry otherEntry = delegateGet(i);
          if (otherEntry.equals(object) && i != index)
          {
            throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
          }
        }
      }
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), entryFeature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry otherEntry = delegateGet(i);
        if (validator.isValid(otherEntry.getEStructuralFeature()) && i != index)
        {
          throw new IllegalArgumentException("The multiplicity constraint is violated");
        }
      }
    }

    return doSet(index, object);
  }

  public Entry doSet(int index, Entry object)
  {
    return super.set(index, object);
  }

  @Override
  public boolean add(Entry object)
  {
    EStructuralFeature entryFeature = object.getEStructuralFeature();
    if (isMany(entryFeature))
    {
      if (entryFeature.isUnique() && contains(entryFeature, object.getValue()))
      {
        return false;
      }
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), entryFeature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry otherEntry = delegateGet(i);
        if (validator.isValid(otherEntry.getEStructuralFeature()))
        {
          if (otherEntry.equals(object))
          {
            return false;
          }
          else
          {
            doSet(i, object);
            return true;
          }
        }
      }
    }

    return doAdd(object);
  }

  protected boolean doAdd(Entry object)
  {
    return super.add(object);
  }

  @Override
  public void add(int index, Entry object)
  {
    EStructuralFeature entryFeature = object.getEStructuralFeature();
    if (isMany(entryFeature))
    {
      if (entryFeature.isUnique())
      {
        for (int i = 0, size = delegateSize(); i < size; ++i)
        {
          Entry otherEntry = delegateGet(i);
          if (otherEntry.equals(object) && i != index)
          {
            throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
          }
        }
      }
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), entryFeature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry otherEntry = delegateGet(i);
        if (validator.isValid(otherEntry.getEStructuralFeature()))
        {
          throw new IllegalArgumentException("The multiplicity constraint is violated");
        }
      }
    }

    doAdd(index, object);
  }

  public void doAdd(int index, Entry object)
  {
    super.add(index, object);
  }

  @Override
  public boolean addAll(Collection<? extends Entry> collection)
  {
    Collection<Entry> uniqueCollection = new BasicEList<Entry>(collection.size());
    for (Entry entry : collection)
    {
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      if (isMany(entryFeature))
      {
        if (!entryFeature.isUnique() || !contains(entryFeature, entry.getValue()) && !uniqueCollection.contains(entry))
        {
          uniqueCollection.add(entry);
        }
      }
      else
      {
        FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), entryFeature);
        boolean include = true;
        for (int j = 0, size = delegateSize(); j < size; ++j)
        {
          Entry otherEntry = delegateGet(j);
          if (validator.isValid(otherEntry.getEStructuralFeature()))
          {
            doSet(j, entry);
            include = false;
            break;
          }
        }
        if (include)
        {
          uniqueCollection.add(entry);
        }
      }
    }

    return doAddAll(uniqueCollection);
  }

  public boolean doAddAll(Collection<? extends Entry> collection)
  {
    return super.addAll(collection);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Entry> collection)
  {
    Collection<Entry> uniqueCollection = new BasicEList<Entry>(collection.size());
    for (Entry entry : collection)
    {
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      if (isMany(entryFeature))
      {
        if (!entryFeature.isUnique() || !contains(entryFeature, entry.getValue()) && !uniqueCollection.contains(entry))
        {
          uniqueCollection.add(entry);
        }
      }
      else
      {
        FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), entryFeature);
        boolean include = true;
        for (int j = 0, size = delegateSize(); j < size; ++j)
        {
          Entry otherEntry = delegateGet(j);
          if (validator.isValid(otherEntry.getEStructuralFeature()))
          {
            doSet(j, entry);
            include = false;
            break;
          }
        }
        if (include)
        {
          uniqueCollection.add(entry);
        }
      }
    }

    return doAddAll(index, uniqueCollection);
  }

  public boolean doAddAll(int index, Collection<? extends Entry> collection)
  {
    return super.addAll(index, collection);
  }


  public int size(EStructuralFeature feature)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    int result = 0;
    for (int i = 0, size = delegateSize(); i < size; ++i)
    {
      Entry entry = delegateGet(i);
      if (validator.isValid(entry.getEStructuralFeature()))
      {
        ++result;
      }
    }
    return result;
  }

  public boolean isEmpty(EStructuralFeature feature)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    for (int i = 0, size = delegateSize(); i < size; ++i)
    {
      Entry entry = delegateGet(i);
      if (validator.isValid(entry.getEStructuralFeature()))
      {
        return false;
      }
    }
    return true;
  }

  public boolean contains(EStructuralFeature feature, Object object)
  {
    return contains(feature, object, isResolveProxies(feature));
  }

  public boolean basicContains(EStructuralFeature feature, Object object)
  {
    return contains(feature, object, false);
  }

  protected boolean contains(EStructuralFeature feature, Object object, boolean resolve)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()) && entry.equals(object))
        {
          return true;
        }
      }
    }
    else if (object != null)
    {
      int size = delegateSize();
      for (int i = 0; i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()) && object.equals(entry.getValue()))
        {
          return true;
        }
      }
      if (resolve)
      {
        for (int i = 0; i < size; ++i)
        {
          Entry entry = delegateGet(i);
          if (validator.isValid(entry.getEStructuralFeature()) && object == resolveProxy((EObject)entry.getValue()))
          {
            return true;
          }
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()) && entry.getValue() == null)
        {
          return false;
        }
      }
    }

    return false;
  }

  public boolean containsAll(EStructuralFeature feature, Collection<?> collection)
  {
    for (Object object : collection)
    {
      if (!contains(feature, object))
      {
        return false;
      }
    }

    return true;
  }

  public boolean basicContainsAll(EStructuralFeature feature, Collection<?> collection)
  {
    for (Object object : collection)
    {
      if (!basicContains(feature, object))
      {
        return false;
      }
    }

    return true;
  }

  public int indexOf(EStructuralFeature feature, Object object)
  {
    return indexOf(feature, object, isResolveProxies(feature));
  }

  public int basicIndexOf(EStructuralFeature feature, Object object)
  {
    return indexOf(feature, object, false);
  }

  protected int indexOf(EStructuralFeature feature, Object object, boolean resolve)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    int result = 0;
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.equals(object))
          {
            return result;
          }
          ++result;
        }
      }
    }
    else if (object != null)
    {
      int size = delegateSize();
      for (int i = 0; i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (object.equals(entry.getValue()))
          {
            return result;
          }
          ++result;
        }
      }
      if (resolve)
      {
        result = 0;
        for (int i = 0; i < size; ++i)
        {
          Entry entry = delegateGet(i);
          if (validator.isValid(entry.getEStructuralFeature()))
          {
            if (object == resolveProxy((EObject)entry.getValue()))
            {
              return result;
            }
            ++result;
          }
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.getValue() == null)
          {
            return result;
          }
          ++result;
        }
      }
    }

    return -1;
  }

  public int lastIndexOf(EStructuralFeature feature, Object object)
  {
    return lastIndexOf(feature, object, isResolveProxies(feature));
  }

  public int basicLastIndexOf(EStructuralFeature feature, Object object)
  {
    return lastIndexOf(feature, object, false);
  }

  protected int lastIndexOf(EStructuralFeature feature, Object object, boolean resolve)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    int result = -1;
    int count = 0;
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.equals(object))
          {
            result = count;
          }
          ++count;
        }
      }
    }
    else if (object != null)
    {
      int size = delegateSize();
      for (int i = 0; i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (object.equals(entry.getValue()))
          {
            result = count;
          }
          ++count;
        }
      }
      if (resolve)
      {
        result = -1;
        count = 0;
        for (int i = 0; i < size; ++i)
        {
          Entry entry = delegateGet(i);
          if (validator.isValid(entry.getEStructuralFeature()))
          {
            if (object == resolveProxy((EObject)entry.getValue()))
            {
              result = count;
            }
            ++count;
          }
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.getValue() == null)
          {
            result = count;
          }
          ++count;
        }
      }
    }

    return result;
  }

  public Iterator<Object> iterator(EStructuralFeature feature)
  {
    return 
      feature instanceof EReference && ((EReference)feature).isResolveProxies() ?
        new ResolvingFeatureEIterator<Object>(feature, this) :
        new FeatureEIterator<Object>(feature, this);
  }

  public ListIterator<Object> listIterator(EStructuralFeature feature)
  {
    return 
      feature instanceof EReference && ((EReference)feature).isResolveProxies() ?
        new ResolvingFeatureEIterator<Object>(feature, this) :
        new FeatureEIterator<Object>(feature, this);
  }

  public ListIterator<Object> listIterator(EStructuralFeature feature, int index)
  {
    ListIterator<Object> result =
      feature instanceof EReference && ((EReference)feature).isResolveProxies() ?
        new ResolvingFeatureEIterator<Object>(feature, this) :
        new FeatureEIterator<Object>(feature, this);
    for (int i = 0; i < index; ++i)
    {
      result.next();
    }
    return result;
  }

  public ValueListIterator<Object> valueListIterator()
  {
    return new ValueListIteratorImpl<Object>();
  }
  
  public ValueListIterator<Object> valueListIterator(int index)
  {
    return new ValueListIteratorImpl<Object>(index);
  }
  
  protected class ValueListIteratorImpl<E> extends AbstractEList<FeatureMap.Entry>.EListIterator<E> implements ValueListIterator<E>
  {
    public ValueListIteratorImpl()
    {
      super();
    }
    
    public ValueListIteratorImpl(int index)
    {
      super(index);
    }
    
    public EStructuralFeature feature()
    {
      if (lastCursor == -1)
      {
        throw new IllegalStateException();
      }
      return getEStructuralFeature(lastCursor);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E next()
    {
      return (E)doNext().getValue();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E previous()
    {
      return (E)doPrevious().getValue();
    }

    @Override
    public void add(E value)
    {
      doAdd(FeatureMapUtil.createEntry(feature(), value));
    }
    
    public void add(EStructuralFeature eStructuralFeature, Object value)
    {
      doAdd(FeatureMapUtil.createEntry(eStructuralFeature, value));
    }
  }

/*
  public List subList(EStructuralFeature feature, int from, int to)
  {
    return null;
  }
*/

  @SuppressWarnings("unchecked")
  public <T> EList<T> list(EStructuralFeature feature)
  {
    return
      FeatureMapUtil.isFeatureMap(feature) ?
        (EList<T>)new FeatureMapUtil.FeatureFeatureMap(feature, this) :
        new FeatureMapUtil.FeatureEList<T>(feature, this);
  }

  public EStructuralFeature.Setting setting(EStructuralFeature feature)
  {
    return 
      isMany(feature) ?
        (EStructuralFeature.Setting)list(feature) :
        (EStructuralFeature.Setting)new FeatureMapUtil.FeatureValue(feature, this);
  }

  public List<Object> basicList(EStructuralFeature feature)
  {
    return new FeatureMapUtil.FeatureEList.Basic<Object>(feature, this);
  }

  public Iterator<Object> basicIterator(EStructuralFeature feature)
  {
    return new FeatureEIterator<Object>(feature, this);
  }

  public ListIterator<Object> basicListIterator(EStructuralFeature feature)
  {
    return new FeatureEIterator<Object>(feature, this);
  }

  public ListIterator<Object> basicListIterator(EStructuralFeature feature, int index)
  {
    ListIterator<Object> result = new FeatureEIterator<Object>(feature, this);
    for (int i = 0; i < index; ++i)
    {
      result.next();
    }
    return result;
  }

  public Object[] toArray(EStructuralFeature feature)
  {
    return toArray(feature, isResolveProxies(feature));
  }

  public Object[] basicToArray(EStructuralFeature feature)
  {
    return toArray(feature, false);
  }

  protected Object[] toArray(EStructuralFeature feature, boolean resolve)
  {
    List<Object> result = new BasicEList<Object>();
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          result.add(entry);
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          Object value = entry.getValue();
          result.add(resolve ? resolveProxy(feature, i, result.size(), value) : value);
        }
      }
    }
    return result.toArray();
  }

  public <T> T[] toArray(EStructuralFeature feature, T [] array)
  {
    return toArray(feature, array, isResolveProxies(feature));
  }

  public <T> T[] basicToArray(EStructuralFeature feature, T [] array)
  {
    return toArray(feature, array, false);
  }

  protected <T> T[] toArray(EStructuralFeature feature, T [] array, boolean resolve)
  {
    List<Object> result = new BasicEList<Object>();
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          result.add(entry);
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          Object value = entry.getValue();
          result.add(resolve ? resolveProxy(feature, i, result.size(), value) : value);
        }
      }
    }
    return result.toArray(array);
  }


  public void set(EStructuralFeature feature, Object object)
  {
    if (isMany(feature))
    {
      List<Object> list = list(feature);
      list.clear();
      list.addAll((Collection<?>)object);
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (shouldUnset(feature, object))
          {
            remove(i);
          }
          else
          {
            doSet(i, FeatureMapUtil.isFeatureMap(feature) ? (Entry)object : createEntry(feature, object));
          }
          return;
        }
      }
  
      if (!shouldUnset(feature, object))
      {
        doAdd(FeatureMapUtil.isFeatureMap(feature) ? (Entry)object : createEntry(feature, object));
      }
    }
  }

  protected boolean shouldUnset(EStructuralFeature feature, Object value)
  {
    if (feature.getUpperBound() != ETypedElement.UNSPECIFIED_MULTIPLICITY && !feature.isUnsettable())
    {
      Object defaultValue = feature.getDefaultValue();
      return defaultValue == null ? value == null : defaultValue.equals(value);
    }
    else
    {
      return false;
    }
  }

  public void add(int index, EStructuralFeature feature, Object object)
  {
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    if (isMany(feature))
    {
      if (feature.isUnique() && contains(feature, object))
      {
        throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
      }
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (isFeatureMap ? entry.equals(object) : object == null ? entry.getValue() == null : object.equals(entry.getValue()))
          {
            throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
          }
        }
      }
    }

    doAdd(index, isFeatureMap ? (Entry)object : createEntry(feature, object));
  }

  public boolean add(EStructuralFeature feature, Object object)
  {
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    if (isMany(feature))
    {
      if (feature.isUnique() && contains(feature, object))
      {
        return false;
      }
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (isFeatureMap ? entry.equals(object) : object == null ? entry.getValue() == null : object.equals(entry.getValue()))
          {
            return false;
          }
          else
          {
            doSet(i, isFeatureMap ? (Entry)object : createEntry(feature, object));
            return true;
          }
        }
      }
    }

    return doAdd(isFeatureMap ? (Entry)object : createEntry(feature, object));
  }

  public void add(EStructuralFeature feature, int index, Object object)
  {
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    if (isMany(feature))
    {
      if (feature.isUnique() && contains(feature, object))
      {
        throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
      }
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          throw new IllegalArgumentException("The multiplicity constraint is violated");
        }
      }
    }

    doAdd(entryIndex(feature, index), isFeatureMap ? (Entry)object : createEntry(feature, object));
  }

  public boolean addAll(int index, EStructuralFeature feature, Collection<?> collection)
  {
    if (collection.size() == 0)
    {
      return false;
    }
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    @SuppressWarnings("unchecked") Collection<Entry> entryCollection = 
      isFeatureMap ? (Collection<Entry>)collection : new BasicEList<Entry>(collection.size());
    if (isMany(feature))
    {
      if (feature.isUnique())
      {
        for (Object object : collection)
        {
          if (!contains(feature, object))
          {
            Entry entry = createEntry(feature, object);
            if (!entryCollection.contains(entry))
            {
              entryCollection.add(entry);
            }
          }
        }
      }
      else if (!isFeatureMap)
      {
        for (Object object : collection)
        {
          Entry entry = createEntry(feature, object);
          entryCollection.add(entry);
        }
      }
    }
    else
    {
      if (collection.size() > 1)
      {
        throw new IllegalArgumentException("The multiplicity constraint is violated");
      }

      if (isFeatureMap)
      {
        if (contains(feature, collection.iterator().next()))
        {
          return false;
        }
      }
      else
      {
        FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
        for (int i = 0, size = delegateSize(); i < size; ++i)
        {
          Entry entry = delegateGet(i);
          if (validator.isValid(entry.getEStructuralFeature()))
          {
            if (collection.contains(entry.getValue()))
            {
              return false;
            }
            else
            {
              throw new IllegalArgumentException("The multiplicity constraint is violated");
            }
          }
        }
        Entry entry = createEntry(feature, collection.iterator().next());
        entryCollection.add(entry);
      }
    }

    return doAddAll(index, entryCollection);
  }

  public boolean addAll(EStructuralFeature feature, Collection<?> collection)
  {
    if (collection.size() == 0)
    {
      return false;
    }
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    @SuppressWarnings("unchecked") Collection<Entry> entryCollection = 
      isFeatureMap ? (Collection<Entry>)collection : new BasicEList<Entry>(collection.size());
    if (isMany(feature))
    {
      if (feature.isUnique())
      {
        for (Object object : collection)
        {
          if (!contains(feature, object))
          {
            Entry entry = createEntry(feature, object);
            if (!entryCollection.contains(entry))
            {
              entryCollection.add(entry);
            }
          }
        }
      }
      else if (!isFeatureMap)
      {
        for (Object object : collection)
        {
          Entry entry = createEntry(feature, object);
          entryCollection.add(entry);
        }
      }
    }
    else
    {
      if (collection.size() > 1)
      {
        throw new IllegalArgumentException("The multiplicity constraint is violated");
      }
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (collection.contains(isFeatureMap ? entry : entry.getValue()))
          {
            return false;
          }
          else
          {
            for (Object object : collection)
            {
              doSet(i, isFeatureMap ? (Entry)object : createEntry(feature, object));
            }
            return true;
          }
        }
      }
      if (!isFeatureMap)
      {
        Entry entry = createEntry(feature, collection.iterator().next());
        entryCollection.add(entry);
      }
    }

    return doAddAll(entryCollection);
  }

  public boolean addAll(EStructuralFeature feature, int index, Collection<?> collection)
  {
    if (collection.size() == 0)
    {
      return false;
    }
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    @SuppressWarnings("unchecked") Collection<Entry> entryCollection = 
      isFeatureMap ? (Collection<Entry>)collection : new BasicEList<Entry>(collection.size());
    if (isMany(feature))
    {
      if (feature.isUnique())
      {
        for (Object object : collection)
        {
          if (!contains(feature, object))
          {
            Entry entry = createEntry(feature, object);
            entryCollection.add(entry);
          }
        }
      }
      else if (!isFeatureMap)
      {
        for (Object object : collection)
        {
          Entry entry = createEntry(feature, object);
          entryCollection.add(entry);
        }
      }
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          throw new IllegalArgumentException("The multiplicity constraint is violated");
        }
      }

      if (collection.size() > 1)
      {
        throw new IllegalArgumentException("The multiplicity constraint is violated");
      }

      if (!isFeatureMap)
      {
        Entry entry = createEntry(feature, collection.iterator().next());
        entryCollection.add(entry);
      }
    }

    return doAddAll(entryIndex(feature, index), entryCollection);
  }

  public void addUnique(EStructuralFeature feature, Object object)
  {
    addUnique(createRawEntry(feature, object));
  }

  public void addUnique(EStructuralFeature feature, int index, Object object)
  {
    addUnique(entryIndex(feature, index), createRawEntry(feature, object));
  }

  @Override
  public void addUnique(Entry object)
  {
    // Validate now since the call we make after will skip validating.
    validate(delegateSize(), object);

    super.addUnique(object);
  }

  public void addUnique(Entry.Internal entry)
  {
    super.addUnique(entry);
  }

  @Override
  public boolean addAllUnique(Collection<? extends Entry> collection)
  {
    return super.addAllUnique(collection);
  }

  public boolean addAllUnique(Entry.Internal [] entries, int start, int end)
  {
    return super.addAllUnique(size(), entries, start, end);
  }

  public boolean addAllUnique(int index, Entry.Internal [] entries, int start, int end)
  {
    return super.addAllUnique(index, entries, start, end);
  }

  public NotificationChain basicAdd(EStructuralFeature feature, Object object, NotificationChain notifications)
  {
    if (object == null)
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (entry.getEStructuralFeature() == feature)
        {
          return super.basicRemove(entry, notifications);
        }
      }
    }

    Entry entry = FeatureMapUtil.isFeatureMap(feature) ? (Entry)object : createEntry(feature, object);

    notifications = basicAdd(entry, notifications);
    if (isNotificationRequired())
    {
      boolean oldIsSet = !isEmpty(feature);
      NotificationImpl notification = 
        feature.isMany() ?
          createNotification
            (Notification.ADD,
             feature,
             null, 
             object,
             indexOf(feature, object),
             oldIsSet) :
          createNotification
            (Notification.SET, 
             feature,
             feature.getDefaultValue(), 
             object,
             Notification.NO_INDEX,
             oldIsSet);

      if (notifications != null)
      {
        notifications.add(notification);
      }
      else
      {
        notifications = notification;
      }
    }
    return notifications;
  }

  public boolean remove(EStructuralFeature feature, Object object)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.equals(object))
          {
            remove(i);
            return true;
          }
        }
      }
    }
    else if (object != null)
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (object.equals(entry.getValue()))
          {
            remove(i);
            return true;
          }
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.getValue() == null)
          {
            remove(i);
            return true;
          }
        }
      }
    }

    return false;
  }

  public Object remove(EStructuralFeature feature, int index)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    int count = 0;
    for (int i = 0, size = delegateSize(); i < size; ++i)
    {
      Entry entry = delegateGet(i);
      if (validator.isValid(entry.getEStructuralFeature()))
      {
        if (count == index)
        {
          remove(i);
          return FeatureMapUtil.isFeatureMap(feature) ? entry : entry.getValue();
        }
        ++count;
      }
    }

    throw new IndexOutOfBoundsException("index=" + index + ", size=" + count);
  }

  public boolean removeAll(EStructuralFeature feature, Collection<?> collection)
  {
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      return removeAll(collection);
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      List<Entry> entryCollection = new BasicEList<Entry>(collection.size());
      for (int i = delegateSize(); --i >= 0; )
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (collection.contains(entry.getValue()))
          {
            entryCollection.add(entry);
          }
        }
      }

      return removeAll(entryCollection);
    }
  }

  public NotificationChain basicRemove(EStructuralFeature feature, Object object, NotificationChain notifications)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    int count = 0;
    Entry match = null;
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.equals(object))
          {
            match = entry;
            break;
          }
          ++count;
        }
      }
    }
    if (object != null)
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (object.equals(entry.getValue()))
          {
            match = entry;
            break;
          }
          ++count;
        }
      }
    }
    else
    {
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (entry.getValue() == null)
          {
            match = entry;
            break;
          }
          ++count;
        }
      }
    }

    if (match != null)
    {
      if (isNotificationRequired())
      {
        NotificationImpl notification = 
          feature.isMany() ?
            createNotification
              (Notification.REMOVE,
               feature,
               object,
               null, 
               count,
               true) :
            createNotification
              (feature.isUnsettable() ? Notification.UNSET : Notification.SET, 
               feature,
               object,
               feature.getDefaultValue(), 
               Notification.NO_INDEX,
               true);
  
        if (notifications != null)
        {
          notifications.add(notification);
        }
        else
        {
          notifications = notification;
        }
      }
      notifications = basicRemove(match, notifications);
    }

    return notifications;
  }

  public boolean retainAll(EStructuralFeature feature, Collection<?> collection)
  {
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    List<Entry> entryCollection = new BasicEList<Entry>(collection.size());
    for (int i = delegateSize(); --i >= 0; )
    {
      Entry entry = delegateGet(i);
      if (validator.isValid(entry.getEStructuralFeature()))
      {
        if (!collection.contains(isFeatureMap ? entry : entry.getValue()))
        {
          entryCollection.add(entry);
        }
      }
    }

    return removeAll(entryCollection);
  }

  public void clear(EStructuralFeature feature)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    List<Entry> entryCollection = new BasicEList<Entry>();
    for (int i = delegateSize(); --i >= 0; )
    {
      Entry entry = delegateGet(i);
      if (validator.isValid(entry.getEStructuralFeature()))
      {
        entryCollection.add(entry);
      }
    }

    if (!removeAll(entryCollection))
    {
      dispatchNotification
        (feature.isMany() ?
           createNotification
             (Notification.REMOVE_MANY,
              feature,
              Collections.EMPTY_LIST,
              null, 
              Notification.NO_INDEX,
              false) :
           createNotification
             (feature.isUnsettable() ? Notification.UNSET : Notification.SET, 
              feature,
              null,
              null, 
              Notification.NO_INDEX,
              false));
    }
  }

  public void move(EStructuralFeature feature, int index, Object object)
  {
    move(feature, index, indexOf(feature, object));
  }

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    if (isMany(feature))
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      Object result = null;
      int entryTargetIndex = -1;
      int entrySourceIndex = -1;
      int count = 0;
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (count == targetIndex)
          {
            entryTargetIndex = i;
          }
          if (count == sourceIndex)
          {
            entrySourceIndex = i;
            result = entry.getValue();
          }
          ++count;
        }
      }
      if (entryTargetIndex == -1)
      {
        throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + count);
      }
      if (entrySourceIndex == -1)
      {
        throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + count);
      }

      super.move(entryTargetIndex, entrySourceIndex);

      if (isNotificationRequired())
      {
        dispatchNotification
          (createNotification
             (Notification.MOVE, 
              feature,
              sourceIndex, 
              result,
              targetIndex,
              true));
      }

      return result;
    }
    else
    {
      throw new IllegalArgumentException("The feature must be many-valued to support move");
    }
  }

  public Object get(EStructuralFeature feature, boolean resolve)
  {
    if (isMany(feature))
    {
      return list(feature);
    }
    else
    {
      FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
      int count = 0;
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (FeatureMapUtil.isFeatureMap(feature))
          {
            return entry;
          }
          else
          {
            Object value = entry.getValue();
            if (value != null && resolve && isResolveProxies(feature))
            {
              value = resolveProxy(feature, i, count, value);
            }
            return value;
          }
        }
        ++count;
      }

      return feature.getDefaultValue();
    }
  }

  public Object get(EStructuralFeature feature, int index, boolean resolve)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    if (isMany(feature))
    {
      int count = 0;
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (count == index)
          {
            if (FeatureMapUtil.isFeatureMap(feature))
            {
              return entry;
            }
            else
            {
              Object value = entry.getValue();
              if (value != null && resolve && isResolveProxies(feature))
              {
                value = resolveProxy(feature, i, count, entry.getValue());
              }
              return value;
            }
          }
          ++count;
        }
      }
      throw new IndexOutOfBoundsException("index=" + index + ", size=" + count);
    }
    else
    {
      int count = 0;
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (FeatureMapUtil.isFeatureMap(feature))
          {
            return entry;
          }
          else
          {
            Object value = entry.getValue();
            if (value != null && resolve && isResolveProxies(feature))
            {
              value = resolveProxy(feature, i, count, value);
            }
            return value;
          }
        }
        ++count;
      }

      return feature.getDefaultValue();
    }
  }

  public Object set(EStructuralFeature feature, int index, Object object)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    if (isMany(feature))
    {
      if (feature.isUnique())
      {
        int currentIndex = indexOf(feature, object);
        if (currentIndex >=0 && currentIndex != index)
        {
          throw new IllegalArgumentException("The 'no duplicates' constraint is violated");
        }
      }

      int count = 0;
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (count == index)
          {
            return doSet(i, FeatureMapUtil.isFeatureMap(feature) ? (Entry)object : createEntry(feature, object));
          }
          ++count;
        }
      }
      throw new IndexOutOfBoundsException("index=" + index + ", size=" + count);
    }
    else
    {
      // Index should be -1.

      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          return FeatureMapUtil.isFeatureMap(feature) ? entry : entry.getValue();
        }
      }

      return null;
    }
  }

  public Object setUnique(EStructuralFeature feature, int index, Object object)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    if (isMany(feature))
    {
      int count = 0;
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          if (count == index)
          {
            return setUnique(i, FeatureMapUtil.isFeatureMap(feature) ? (Entry)object : createEntry(feature, object));
          }
          ++count;
        }
      }
      throw new IndexOutOfBoundsException("index=" + index + ", size=" + count);
    }
    else
    {
      // Index should be -1.

      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          return setUnique(i, FeatureMapUtil.isFeatureMap(feature) ? (Entry)object : createEntry(feature, object));
        }
      }

      return feature.getDefaultValue();
    }
  }

  public boolean isSet(EStructuralFeature feature)
  {
    return !isEmpty(feature);
  }

  public void unset(EStructuralFeature feature)
  {
    FeatureMapUtil.Validator validator = FeatureMapUtil.getValidator(owner.eClass(), feature);
    List<Entry> removals = null;
    for (int i = 0, size = delegateSize(); i < size; ++i)
    {
      Entry entry = delegateGet(i);
      if (validator.isValid(entry.getEStructuralFeature()))
      {
        if (removals == null)
        {
          removals = new BasicEList<Entry>();
        }
        removals.add(entry);
      }
    }

    if (removals != null)
    {
      removeAll(removals);
    }
  }

  @Override
  public NotificationChain basicRemove(Object object, NotificationChain notifications)
  {
    // This may be called directly on an EObject for the case of a containment.
    //
    if (object instanceof FeatureMap.Entry)
    {
      return super.basicRemove(object, notifications);
    }
    else
    {
      Entry match = null;
      EStructuralFeature feature = null;
      for (int i = 0, size = delegateSize(); i < size; ++i)
      {
        Entry entry = delegateGet(i); 
        if (object.equals(entry.getValue()))
        {
          feature = entry.getEStructuralFeature();
          if (feature instanceof EReference && ((EReference)feature).isContainment())
          {
            match = entry;
            break;
          }
        }
      }

      if (match != null)
      {
        if (isNotificationRequired())
        {
          @SuppressWarnings("null")
          NotificationImpl notification =
            feature.isMany() ?
              createNotification
                (Notification.REMOVE,
                 feature,
                 object,
                 null,
                 indexOf(feature, object),
                 true) :
              createNotification
                (feature.isUnsettable() ? Notification.UNSET : Notification.SET,
                 feature,
                 object,
                 feature.getDefaultValue(),
                 Notification.NO_INDEX,
                 true);

          if (notifications != null)
          {
            notifications.add(notification);
          }
          else
          {
            notifications = notification;
          }
        }
        notifications = basicRemove(match, notifications);
      }

      return notifications;
    }
  }

  /**
   * -------------------------------------------
   */
  public static class FeatureEIterator<E> extends FeatureMapUtil.BasicFeatureEIterator<E>
  {
    public FeatureEIterator(EStructuralFeature eStructuralFeature, FeatureMap.Internal featureMap)
    {
      super(eStructuralFeature, featureMap);
    }

    @Override
    protected boolean scanNext()
    {
      int size = featureMap.size();
      while (entryCursor < size)
      {
        Entry entry = featureMap.get(entryCursor);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          preparedResult = extractValue(entry);
          prepared = 2;
          return true;
        }
        ++entryCursor;
      }

      prepared = 1;
      lastCursor = -1;
      return false;
    }

    @Override
    protected boolean scanPrevious()
    {
      while (--entryCursor >= 0)
      {
        Entry entry = featureMap.get(entryCursor);
        if (validator.isValid(entry.getEStructuralFeature()))
        {
          preparedResult = extractValue(entry);
          prepared = -2;
          return true;
        }
      }

      prepared = -1;
      lastCursor = -1;
      return false;
    }
  }

  /**
   * -------------------------------------------
   */
  public static class ResolvingFeatureEIterator<E> extends FeatureEIterator<E>
  {
    public ResolvingFeatureEIterator(EStructuralFeature eStructuralFeature, FeatureMap.Internal featureMap)
    {
      super(eStructuralFeature, featureMap);
    }

    @Override
    protected boolean resolve()
    {
      return true;
    }
  }

  /**
   * Temporary for testing purposes only.
   */
  public static class FeatureMapEObjectImpl extends org.eclipse.emf.ecore.impl.EObjectImpl
  {
    protected DelegatingFeatureMap featureMap = 
      new DelegatingFeatureMap(this, -1)
      {
        private static final long serialVersionUID = 1L;

        protected List<Entry> theList = new java.util.ArrayList<Entry>();
        @Override
        protected List<Entry> delegateList()
        {
          return theList;
        }
      };


    public FeatureMapEObjectImpl()
    {
      super();
    }

    @Override
    public Object eDynamicGet(EStructuralFeature eFeature, boolean resolve)
    {
      if (eFeature instanceof EReference && ((EReference)eFeature).isContainer())
      {
        return eSettingDelegate(eFeature).dynamicGet(this, null, -1, true, true);
      }
      else
      {
        return featureMap.setting(eFeature).get(resolve);
      }
    }

    @Override
    public void eDynamicSet(EStructuralFeature eFeature, Object newValue)
    {
      if (eFeature instanceof EReference && ((EReference)eFeature).isContainer())
      {
        eSettingDelegate(eFeature).dynamicSet(this, null, -1, newValue);
      }
      else
      {
        if (!eFeature.isUnsettable())
        {
          Object defaultValue = eFeature.getDefaultValue();
          if (defaultValue == null ? newValue == null : defaultValue.equals(newValue))
          {
            featureMap.setting(eFeature).unset();
            return;
          }
        }
        featureMap.setting(eFeature).set(newValue);
      }
    }

    @Override
    public void eDynamicUnset(EStructuralFeature eFeature)
    {
      if (eFeature instanceof EReference && ((EReference)eFeature).isContainer())
      {
        eSettingDelegate(eFeature).dynamicUnset(this, null, -1);
      }
      else
      {
        featureMap.setting(eFeature).unset();
      }
    }

    @Override
    public boolean eDynamicIsSet(EStructuralFeature eFeature)
    {
      if (eFeature instanceof EReference && ((EReference)eFeature).isContainer())
      {
        return eSettingDelegate(eFeature).dynamicIsSet(this, null, -1);
      }
      else
      {
        return featureMap.setting(eFeature).isSet();
      }
    }

    @Override
    public NotificationChain eDynamicInverseAdd(InternalEObject otherEnd, int featureID, Class<?> inverseClass, NotificationChain notifications)
    {
      EStructuralFeature.Internal feature = (EStructuralFeature.Internal)eClass().getEStructuralFeature(featureID);
      if (feature.isMany())
      {
        return featureMap.basicAdd(feature, otherEnd, notifications);
      }
      else if (feature instanceof EReference && ((EReference)feature).isContainer())
      {
        return eSettingDelegate(feature).dynamicInverseAdd(this, null, -1, otherEnd, notifications);
      }
      else
      {
        InternalEObject oldValue = (InternalEObject)eDynamicGet(feature, false);
        if (oldValue != null)
        {
          notifications = oldValue.eInverseRemove
            (this, oldValue.eClass().getFeatureID(((EReference)feature).getEOpposite()), null, notifications);
          notifications = featureMap.basicRemove(feature, oldValue, notifications);
        }

        return featureMap.basicAdd(feature, otherEnd, notifications);
      }
    }

    @Override
    public NotificationChain eDynamicInverseRemove(InternalEObject otherEnd, int featureID, Class<?> inverseClass, NotificationChain notifications)
    {
      EStructuralFeature.Internal feature = (EStructuralFeature.Internal)eClass().getEStructuralFeature(featureID);
      if (feature instanceof EReference && ((EReference)feature).isContainer())
      {
        return eSettingDelegate(feature).dynamicInverseRemove(this, null, -1, otherEnd, notifications);
      }
      else
      {
        return featureMap.basicRemove(feature, otherEnd, notifications);
      }
    }

    public FeatureMap featureMap()
    {
      return featureMap;
    }

    @Override
    public void eNotify(Notification notification)
    {
      if (notification.getFeatureID(null) != -1)
      {
        super.eNotify(notification);
      }
    }

    @Override
    public String toString()
    {
      String result = super.toString();
      result = "org.eclipse.emf.ecore.impl.EObjectImpl" + result.substring(result.indexOf("@"));
      return result;
    }
  }

    @Override
   public void set(Object newValue)
   {
     super.set(newValue instanceof FeatureMap ? newValue : ((FeatureMap.Internal.Wrapper)newValue).featureMap());
   }
}
