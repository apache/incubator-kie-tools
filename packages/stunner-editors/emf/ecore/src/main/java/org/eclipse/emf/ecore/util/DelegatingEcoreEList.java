/**
 * Copyright (c) 2002-2012 IBM Corporation and others.
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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.DelegatingEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;


/**
 * A {@link DelegatingNotifyingInternalEListImpl delegating notifying internal EList}
 * that implements {@link InternalEList.Unsettable} and {@link EStructuralFeature.Setting}.
 * At least one of {@link #getEStructuralFeature()} or {@link #getFeatureID()} must be specialized
 * since each delegates to the other and without specialization this will lead to stack overflow.
 */
public abstract class DelegatingEcoreEList<E>
  extends DelegatingNotifyingInternalEListImpl<E>
  implements InternalEList.Unsettable<E>, EStructuralFeature.Setting
{
  private static final long serialVersionUID = 1L;

  /**
   * A {@link DelegatingEcoreEList delegating Ecore EList} with an implementation for tracking the unset state.
   * At least one of {@link #getEStructuralFeature()} or {@link #getFeatureID()} must be specialized
   * since each delegates to the other and without specialization this will lead to stack overflow.
   */
  public static abstract class Unsettable<E> extends DelegatingEcoreEList<E>
  {
    private static final long serialVersionUID = 1L;

    protected boolean isSet;

    public Unsettable(InternalEObject owner)
    {
      super(owner);
    }

    @Override
    protected void didChange()
    {
      isSet = true;
    }

    @Override
    public boolean isSet()
    {
      return isSet;
    }

    @Override
    public void unset()
    {
      super.unset();
      if (isNotificationRequired())
      {
        boolean oldIsSet = isSet;
        isSet = false;
        dispatchNotification(createNotification(Notification.UNSET, oldIsSet, false));
      }
      else
      {
        isSet = false;
      }
    }
  }

  protected final InternalEObject owner;

  public DelegatingEcoreEList(InternalEObject owner)
  {
    super();
    this.owner = owner;
  }

  @Override
  protected boolean canContainNull()
  {
    EClassifier eClassifier = getFeatureType();
    if (eClassifier instanceof EDataType)
    {
      if (eClassifier instanceof EEnum)
      {
        return false;
      }
      else
      {
        return !eClassifier.getInstanceClass().isPrimitive();
      }
    }
    else
    {
      return false;
    }
  }

  @Override
  protected boolean isUnique()
  {
    return getEStructuralFeature().isUnique();
  }

  @Override
  protected boolean hasInverse()
  {
    EStructuralFeature eStructuralFeature = getEStructuralFeature();
    if (eStructuralFeature instanceof EReference)
    {
      EReference eReference = (EReference)eStructuralFeature;
      return eReference.isContainment() || ((EReference)eStructuralFeature).getEOpposite() != null;
    }
    else
    {
      return false;
    }
  }

  @Override
  protected E validate(int index, E object)
  {
    super.validate(index, object);
    if (object != null && !isInstance(object))
    {
      throw new ArrayStoreException();
    }
    return object;
  }

  protected boolean isInstance(Object object)
  {
    return getFeatureType().isInstance(object);
  }

  @Override
  public Object getNotifier()
  {
    return owner;
  }

  @Override
  public Object getFeature()
  {
    return getEStructuralFeature();
  }

  @Override
  public int getFeatureID()
  {
    return owner.eClass().getFeatureID(getEStructuralFeature());
  }

  public EStructuralFeature getEStructuralFeature()
  {
    return owner.eClass().getEStructuralFeature(getFeatureID());
  }

  protected EClassifier getFeatureType()
  {
    return getEStructuralFeature().getEType();
  }

  protected EReference getInverseEReference()
  {
    return ((EReference)getEStructuralFeature()).getEOpposite();
  }

  protected int getInverseFeatureID()
  {
    return getInverseEReference().getFeatureID();
  }

  protected Class<?> getInverseFeatureClass()
  {
    return ((EClass)getEStructuralFeature().getEType()).getInstanceClass();
  }

  protected boolean hasManyInverse()
  {
    EStructuralFeature eStructuralFeature = getEStructuralFeature();
    if (eStructuralFeature instanceof EReference)
    {
      EReference eReference = (EReference)eStructuralFeature;
      EReference oppositeEReference = eReference.getEOpposite();
      return oppositeEReference != null && oppositeEReference.isMany();
    }
    else
    {
      return false;
    }
  }

  protected boolean hasNavigableInverse()
  {
    EStructuralFeature eStructuralFeature = getEStructuralFeature();
    if (eStructuralFeature instanceof EReference)
    {
      EReference eReference = (EReference)eStructuralFeature;
      EReference oppositeEReference = eReference.getEOpposite();
      return oppositeEReference != null;
    }
    else
    {
      return false;
    }
  }

  protected boolean isEObject()
  {
    return getFeatureType() instanceof EClass;
  }

  protected boolean isContainment()
  {
    EStructuralFeature eStructuralFeature = getEStructuralFeature();
    if (eStructuralFeature instanceof EReference)
    {
      EReference eReference = (EReference)eStructuralFeature;
      return eReference.isContainment();
    }
    else
    {
      return false;
    }
  }

  protected boolean hasProxies()
  {
    EStructuralFeature eStructuralFeature = getEStructuralFeature();
    if (eStructuralFeature instanceof EReference)
    {
      EReference eReference = (EReference)eStructuralFeature;
      return eReference.isResolveProxies();
    }
    else
    {
      return false;
    }
  }

  protected boolean hasInstanceClass()
  {
    return getFeatureType().getInstanceClass() != null;
  }

  @Override
  protected E resolve(int index, E object)
  {
    if (isEObject() && hasProxies())
    {
      @SuppressWarnings("unchecked") E resolved = (E)resolveProxy((EObject)object);
      if (resolved != object)
      {
        E oldObject = delegateGet(index);
        delegateSet(index, validate(index, resolved));
        didSet(index, resolved, oldObject);

        if (isContainment())
        {
          NotificationChain notificationChain = inverseRemove(object, null);
          if (((InternalEObject)resolved).eInternalContainer() == null)
          {
            notificationChain = inverseAdd(resolved, notificationChain);
          }
          if (notificationChain != null)
          {
            notificationChain.dispatch();
          }
        }
        if (isNotificationRequired())
        {
          dispatchNotification(createNotification(Notification.RESOLVE, object, resolved, index, false));
        }

        return resolved;
      }
    }
    return object;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected E resolve(E object)
  {
    return isEObject() ? (E)resolveProxy((EObject)object) : object;
  }

  protected EObject resolveProxy(EObject eObject)
  {
    return eObject.eIsProxy() ? owner.eResolveProxy((InternalEObject)eObject) : eObject;
  }

  @Override
  public Object[] toArray()
  {
    if (hasProxies())
    {
      for (int i = size() - 1; i >= 0; --i)
      {
        get(i);
      }
    }
    return super.toArray();
  }

  @Override
  public <T> T[] toArray(T [] array)
  {
    if (hasProxies())
    {
      for (int i = size() - 1; i >= 0; --i)
      {
        get(i);
      }
    }
    return super.toArray(array);
  }

  @Override
  protected NotificationImpl createNotification(int eventType, Object oldObject, Object newObject, int index, boolean wasSet)
  {
    return new ENotificationImpl(owner, eventType, getFeatureID(), oldObject, newObject, index, wasSet);
  }

  protected NotificationImpl createNotification(int eventType, boolean oldValue, boolean newValue)
  {
    return new ENotificationImpl(owner, eventType, getFeatureID(), oldValue, newValue);
  }

  @Override
  protected void dispatchNotification(Notification notification)
  {
    owner.eNotify(notification);
  }

  @Override
  protected boolean isNotificationRequired()
  {
    return owner.eNotificationRequired();
  }

  @Override
  public NotificationChain inverseAdd(E object, NotificationChain notifications)
  {
    InternalEObject internalEObject = (InternalEObject) object;
    if (hasNavigableInverse())
    {
      if (!hasInstanceClass())
      {
        return 
          internalEObject.eInverseAdd
            (owner, 
             internalEObject.eClass().getFeatureID(getInverseEReference()),
             null,
             notifications);
      }
      else
      {
        return 
          internalEObject.eInverseAdd
            (owner, 
             getInverseFeatureID(),
             getInverseFeatureClass(),
             notifications);
      }
    }
    else
    {
      return 
        internalEObject.eInverseAdd
          (owner, 
           InternalEObject.EOPPOSITE_FEATURE_BASE - getFeatureID(),
           null,
           notifications);
    }
  }

  @Override
  public NotificationChain inverseRemove(E object, NotificationChain notifications)
  {
    InternalEObject internalEObject = (InternalEObject) object;
    if (hasNavigableInverse())
    {
      if (!hasInstanceClass())
      {
        return 
          internalEObject.eInverseRemove
            (owner, 
             internalEObject.eClass().getFeatureID(getInverseEReference()),
             null,
             notifications);
      }
      else
      {
        return 
          internalEObject.eInverseRemove
            (owner, 
             getInverseFeatureID(),
             getInverseFeatureClass(),
             notifications);
      }
    }
    else
    {
      return 
        internalEObject.eInverseRemove
          (owner, 
           InternalEObject.EOPPOSITE_FEATURE_BASE - getFeatureID(),
           null,
           notifications);
    }
  }

  /**
   * Resolve to compare objects but do not modify list
   */
  @Override
  public boolean contains(Object object)
  {
    if (isEObject())
    {
      int size = size();
      if (size > 4)
      {
        if (!isInstance(object)) 
        {
          return false;
        }
        else if (isContainment())
        {
          InternalEObject eObject = (InternalEObject)object;
          EObject eContainer = eObject.eContainer();
          boolean result =
            eContainer == owner && 
              (hasNavigableInverse() ? 
                 eObject.eBaseStructuralFeatureID(eObject.eContainerFeatureID(), getInverseFeatureClass()) == getInverseFeatureID() :
                 InternalEObject.EOPPOSITE_FEATURE_BASE - eObject.eContainerFeatureID() == getFeatureID());
          if (hasProxies() && !result && eContainer == null && eObject.eDirectResource() != null)
          {
            for (int i = 0; i < size; ++i)
            {
              EObject containedEObject = resolveProxy((EObject)delegateGet(i));
              if (containedEObject == object)
              {
                return true;
              }
            }
          }
          return result;
        }
        // We can also optimize single valued reverse. 
        //
        else if (hasNavigableInverse() && !hasManyInverse())
        {
          Object opposite = ((EObject)object).eGet(getInverseEReference());
          if (opposite == owner)
          {
            return true;
          }
          else if (opposite == null || !((EObject)opposite).eIsProxy())
          {
            return false;
          }
        }
      }

      boolean result = super.contains(object);
      if (hasProxies() && !result)
      {
        for (int i = 0; i < size; ++i)
        {
          EObject eObject = resolveProxy((EObject)delegateGet(i));
          if (eObject == object)
          {
            return true;
          }
        }
      }
      return result;
    }
    else
    {
      return super.contains(object);
    }
  }

  @Override
  public int indexOf(Object object)
  {
    int index = super.indexOf(object);
    if (index >= 0)
      return index;

    // EATM This might be better written as a single loop for the EObject case?
    //
    if (isEObject())
    {
      for (int i = 0, size = size(); i < size; ++i)
      {
        EObject eObject = resolveProxy((EObject)delegateGet(i));
        if (eObject == object)
        {
          return i;
        }
      }
    }

    return -1;
  }

  @Override
  public int lastIndexOf(Object object)
  {
    int result = super.lastIndexOf(object);
    if (isEObject () && result == -1)
    {
      for (int i = size() - 1; i >= 0; --i)
      {
        EObject eObject = resolveProxy((EObject)delegateGet(i));
        if (eObject == object)
        {
          return i;
        }
      }
    }

    return result;
  }

  public EObject getEObject()
  {
    return owner;
  }

  public Object get(boolean resolve)
  {
    return this;
  }

  @SuppressWarnings("unchecked")
  public void set(Object newValue)
  {
    clear();
    addAll((List<? extends E>)newValue);
  }

  @Override
  public boolean isSet()
  {
    return !isEmpty();
  }

  public void unset()
  {
    clear();
  }

  public static class UnmodifiableEList<E>
    extends DelegatingEList.UnmodifiableEList<E>
    implements InternalEList.Unsettable<E>, EStructuralFeature.Setting
  {
    private static final long serialVersionUID = 1L;

    protected final InternalEObject owner;
    protected final EStructuralFeature eStructuralFeature;

    public UnmodifiableEList(InternalEObject owner, EStructuralFeature eStructuralFeature, List<E> underlyingList)
    {
      super(underlyingList);
      this.owner = owner;
      this.eStructuralFeature = eStructuralFeature;
    }

    @Override
    public E basicGet(int index)
    {
      return super.basicGet(index);
    }

    @Override
    public List<E> basicList()
    {
      return super.basicList();
    }

    @Override
    public Iterator<E> basicIterator()
    {
      return super.basicIterator();
    }

    @Override
    public ListIterator<E> basicListIterator()
    {
      return super.basicListIterator();
    }

    @Override
    public ListIterator<E> basicListIterator(int index)
    {
      return super.basicListIterator(index);
    }

    public Object[] basicToArray()
    {
      return super.toArray();
    }

    public <T> T[] basicToArray(T[] array)
    {
      return super.toArray(array);
    }

    public boolean basicContains(Object object)
    {
      return super.contains(object);
    }

    public boolean basicContainsAll(Collection<?> collection)
    {
      return super.containsAll(collection);
    }

    public int basicIndexOf(Object object)
    {
      return super.indexOf(object);
    }

    public int basicLastIndexOf(Object object)
    {
      return super.lastIndexOf(object);
    }

    public EObject getEObject()
    {
      return owner;
    }

    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    public Object get(boolean resolve)
    {
      return this;
    }

    public void set(Object newValue)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isSet()
    {
      return !isEmpty();
    }

    public void unset()
    {
      throw new UnsupportedOperationException();
    }

    public NotificationChain basicRemove(Object object, NotificationChain notifications)
    {
      throw new UnsupportedOperationException();
    }

    public NotificationChain basicAdd(E object, NotificationChain notifications)
    {
      throw new UnsupportedOperationException();
    }
  }

  public static abstract class Generic<E> extends DelegatingEcoreEList<E>
  {
    private static final long serialVersionUID = 1L;

    public static final int IS_SET = EcoreEList.Generic.IS_SET;
    public static final int IS_UNSETTABLE = EcoreEList.Generic.IS_UNSETTABLE;
    public static final int HAS_INSTANCE_CLASS = EcoreEList.Generic.HAS_INSTANCE_CLASS;
    public static final int HAS_NAVIGABLE_INVERSE = EcoreEList.Generic.HAS_NAVIGABLE_INVERSE;
    public static final int HAS_MANY_INVERSE = EcoreEList.Generic.HAS_MANY_INVERSE;
    public static final int IS_CONTAINMENT = EcoreEList.Generic.IS_CONTAINMENT;
    public static final int IS_CONTAINER = EcoreEList.Generic.IS_CONTAINER;
    public static final int IS_UNIQUE = EcoreEList.Generic.IS_UNIQUE;
    public static final int IS_PRIMITIVE = EcoreEList.Generic.IS_PRIMITIVE;
    public static final int IS_ENUM = EcoreEList.Generic.IS_ENUM;
    public static final int IS_EOBJECT = EcoreEList.Generic.IS_EOBJECT;
    public static final int HAS_PROXIES = EcoreEList.Generic.HAS_PROXIES;

    public static int kind(EStructuralFeature eStructuralFeature)
    {
      return EcoreEList.Generic.kind(eStructuralFeature);
    }

    protected int kind;

    public Generic(int kind, InternalEObject owner)
    {
      super(owner);
      this.kind = kind;
    }

    @Override
    protected boolean useEquals()
    {
      // We can use == for EObjects and EnumLiterals.
      //
      return (kind & (IS_EOBJECT | IS_ENUM)) == 0;
    }

    @Override
    protected boolean canContainNull()
    {
      return (kind & (IS_EOBJECT | IS_PRIMITIVE | IS_ENUM)) == 0;
    }

    @Override
    protected boolean isUnique()
    {
      return (kind & IS_UNIQUE) != 0;
    }

    @Override
    protected boolean hasInverse()
    {
      return (kind & (HAS_NAVIGABLE_INVERSE | IS_CONTAINMENT)) != 0;
    }

    @Override
    protected boolean hasManyInverse()
    {
      return (kind & HAS_MANY_INVERSE) != 0;
    }

    @Override
    protected boolean hasNavigableInverse()
    {
      return (kind & HAS_NAVIGABLE_INVERSE) != 0; 
    }

    @Override
    protected boolean isEObject()
    {
      return (kind & IS_EOBJECT) != 0;
    }

    @Override
    protected boolean isContainment()
    {
      return (kind & IS_CONTAINMENT) != 0;
    }

    @Override
    protected boolean hasProxies()
    {
      return (kind & HAS_PROXIES) != 0;
    }

    @Override
    protected boolean hasInstanceClass()
    {
      return (kind & HAS_INSTANCE_CLASS) != 0;
    }

    protected boolean isContainer()
    {
      return (kind & IS_CONTAINER) != 0;
    }

    protected boolean isUnsettable()
    {
      return (kind & IS_UNSETTABLE) != 0;
    }

    @Override
    public boolean isSet()
    {
      return isUnsettable() ? (kind & IS_SET) != 0 : !isEmpty();
    }

    @Override
    public void unset()
    {
      super.unset();
      if (isUnsettable())
      {
        if (isNotificationRequired())
        {
          boolean oldIsSet = (kind & IS_SET) != 0;
          kind &= ~IS_SET;
          dispatchNotification(createNotification(Notification.UNSET, oldIsSet, false));
        }
        else
        {
          kind &= ~IS_SET;
        }
      }
    }

    @Override
    protected void didChange()
    {
      kind |= IS_SET;
    }
  }

  public static abstract class Dynamic<E> extends Generic<E>
  {
    private static final long serialVersionUID = 1L;

    protected EStructuralFeature eStructuralFeature;

    public Dynamic(InternalEObject owner, EStructuralFeature eStructuralFeature)
    {
      super(kind(eStructuralFeature), owner);
      this.eStructuralFeature = eStructuralFeature;
    }

    public Dynamic(int kind, InternalEObject owner, EStructuralFeature eStructuralFeature)
    {
      super(kind, owner);
      this.eStructuralFeature = eStructuralFeature;
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }
  }
}
