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


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;
import org.eclipse.emf.ecore.util.DelegatingFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;


/**
 * An implementation of '<em><b>EObject</b></em>' that delegates to a {@link org.eclipse.emf.ecore.InternalEObject.EStore store}.
 */
public class EStoreEObjectImpl extends EObjectImpl implements EStructuralFeature.Internal.DynamicValueHolder
{
  /**
   * An internal class for holding less frequently members variables.
   */
  protected static class EStoreEPropertiesHolderImpl implements BasicEObjectImpl.EPropertiesHolder
  {
    protected EClass eClass;
    protected URI eProxyURI;
    protected Resource.Internal eResource;
    protected EList<EObject> eContents;
    protected EList<EObject> eCrossReferences;

    public EClass getEClass()
    {
      return eClass;
    }
    
    public void setEClass(EClass eClass)
    {
      this.eClass = eClass;
    }

    public URI getEProxyURI()
    {
      return eProxyURI;
    }

    public void setEProxyURI(URI eProxyURI)
    {
      this.eProxyURI = eProxyURI;
    }

    public Resource.Internal getEResource()
    {
      return eResource;
    }

    public void setEResource(Resource.Internal eResource)
    {
      this.eResource = eResource;
    }

    public EList<EObject> getEContents()
    {
      return eContents;
    }

    public void setEContents(EList<EObject> eContents)
    {
      this.eContents = eContents;
    }

    public EList<EObject> getECrossReferences()
    {
      return eCrossReferences;
    }

    public void setECrossReferences(EList<EObject> eCrossReferences)
    {
      this.eCrossReferences = eCrossReferences;
    }

    public boolean hasSettings()
    {
      throw new UnsupportedOperationException();
    }

    public void allocateSettings(int maximumDynamicFeatureID)
    {
      throw new UnsupportedOperationException();
    }

    public Object dynamicGet(int dynamicFeatureID)
    {
      throw new UnsupportedOperationException();
    }

    public void dynamicSet(int dynamicFeatureID, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public void dynamicUnset(int dynamicFeatureID)
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * A list that delegates to a store.
   * @since 2.4
   */
  public static class BasicEStoreEList<E> extends DelegatingEcoreEList.Dynamic<E>
  {
    private static final long serialVersionUID = 1L;

    public BasicEStoreEList(InternalEObject owner, EStructuralFeature eStructuralFeature)
    {
      super(owner, eStructuralFeature);
    }
    
    protected EStore eStore()
    {
      return owner.eStore();
    }

    @Override
    public boolean isSet()
    {
      return eStore().isSet(owner, eStructuralFeature);
    }

    @Override
    public void unset()
    {
      if (isUnsettable() && isNotificationRequired())
      {
        boolean oldIsSet = isSet();
        eStore().unset(owner, eStructuralFeature);
        dispatchNotification(createNotification(Notification.UNSET, oldIsSet, false));
      }
      else
      {
        eStore().unset(owner, eStructuralFeature);
      }
    }

    @Override
    protected List<E> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Object object)
    {
      eStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Object object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<E> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }
      else
      {
        Object[] data = eStore().toArray(owner, eStructuralFeature);
        return new EcoreEList.UnmodifiableEList<E>(owner, eStructuralFeature, data.length, data);
      }
    }

    @Override
    protected void delegateClear()
    {
      eStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return eStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }
      return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateGet(int index)
    {
      return (E)eStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return eStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return eStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return eStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<E> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return eStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<E> delegateListIterator()
    {
      return listIterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateRemove(int index)
    {
      return (E)eStore().remove(owner, eStructuralFeature, index);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateSet(int index, E object)
    {
      return (E)eStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return eStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return eStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return eStore().toArray(owner, eStructuralFeature, array);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateMove(int targetIndex, int sourceIndex)
    {
      return (E)eStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected boolean delegateEquals(Object object)
    {
      if (object == this)
      {
        return true;
      }

      if (!(object instanceof List<?>))
      {
        return false;
      }

      List<?> list = (List<?>)object;
      if (list.size() != delegateSize())
      {
        return false;
      }


      for (ListIterator<?> i = list.listIterator(); i.hasNext(); )
      {
        Object element= i.next();
        if (element == null ? get(i.previousIndex()) != null : !element.equals(get(i.previousIndex())))
        {
          return false;
        }
      }

      return true;
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (int i = 0, size = size(); i < size; )
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", ");
        }
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }

  /**
   * A list that delegates to a store.
   */
  public static class EStoreEList<E> extends BasicEStoreEList<E>
  {
    private static final long serialVersionUID = 1L;

    protected InternalEObject.EStore store; 

    public EStoreEList(InternalEObject owner, EStructuralFeature eStructuralFeature, InternalEObject.EStore store)
    {
      super(owner, eStructuralFeature);
      this.store = store;
    }
    
    @Override
    protected final EStore eStore()
    {
      return store;
    }
  }

  /**
   * A feature map that delegates to a store.
   * @since 2.4
   */
  public static class BasicEStoreFeatureMap extends DelegatingFeatureMap
  {
    private static final long serialVersionUID = 1L;

    public BasicEStoreFeatureMap(InternalEObject owner, EStructuralFeature eStructuralFeature)
    {
      super(owner, eStructuralFeature);
    }
    
    protected EStore eStore()
    {
      return owner.eStore();
    }
    
    @Override
    protected List<FeatureMap.Entry> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Entry object)
    {
      eStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Entry object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<FeatureMap.Entry> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }
      else
      {
        Object[] data = eStore().toArray(owner, eStructuralFeature);
        return new EcoreEList.UnmodifiableEList<FeatureMap.Entry>(owner, eStructuralFeature, data.length, data);
      }
    }

    @Override
    protected void delegateClear()
    {
      eStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return eStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }
      return true;
    }

    @Override
    protected Entry delegateGet(int index)
    {
      return (Entry)eStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return eStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return eStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return eStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<FeatureMap.Entry> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return eStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<FeatureMap.Entry> delegateListIterator()
    {
      return listIterator();
    }

    @Override
    protected Entry delegateRemove(int index)
    {
      return (Entry)eStore().remove(owner, eStructuralFeature, index);
    }

    @Override
    protected Entry delegateSet(int index, Entry object)
    {
      return (Entry)eStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return eStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return eStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return eStore().toArray(owner, eStructuralFeature, array);
    }

    @Override
    protected Entry delegateMove(int targetIndex, int sourceIndex)
    {
      return (Entry)eStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (int i = 0, size = size(); i < size; )
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", ");
        }
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }

  /**
   * A feature map that delegates to a store.
   */
  public static class EStoreFeatureMap extends BasicEStoreFeatureMap
  {
    private static final long serialVersionUID = 1L;

    protected final InternalEObject.EStore store; 

    public EStoreFeatureMap(InternalEObject owner, EStructuralFeature eStructuralFeature, InternalEObject.EStore store)
    {
      super(owner, eStructuralFeature);
      this.store = store;
    }
    
    @Override
    protected EStore eStore()
    {
      return store;
    }
  }

  protected static final Object [] ENO_SETTINGS = new Object [0];
  protected static final InternalEObject EUNINITIALIZED_CONTAINER = new EObjectImpl();

  protected Object [] eSettings;
  protected InternalEObject.EStore eStore;

  /**
   * Creates a store-based EObject.
   */
  public EStoreEObjectImpl()
  {
    super();
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  /**
   * Creates a store-based EObject.
   */
  public EStoreEObjectImpl(InternalEObject.EStore eStore) 
  {
    super();
    eSetStore(eStore);
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  /**
   * Creates a store-based EObject.
   */
  public EStoreEObjectImpl(EClass eClass)
  {
    super();
    eSetClass(eClass);
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  /**
   * Creates a store-based EObject.
   */
  public EStoreEObjectImpl(EClass eClass, InternalEObject.EStore eStore) 
  {
    super();
    eSetClass(eClass);
    eSetStore(eStore);
    eContainer = EUNINITIALIZED_CONTAINER;
  }

  protected boolean eIsCaching()
  {
    return true;
  }

  public Object dynamicGet(int dynamicFeatureID)
  {
    Object result = eSettings[dynamicFeatureID];
    if (result == null)
    {
      EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
      if (!eStructuralFeature.isTransient())
      {
        if (FeatureMapUtil.isFeatureMap(eStructuralFeature))
        {
          eSettings[dynamicFeatureID] = result = createFeatureMap(eStructuralFeature);
        }
        else if (eStructuralFeature.isMany())
        {
          eSettings[dynamicFeatureID] = result = createList(eStructuralFeature);
        }
        else
        {
          result = eStore().get(this, eStructuralFeature, InternalEObject.EStore.NO_INDEX);
          if (eIsCaching())
          {
            eSettings[dynamicFeatureID] = result;
          }
        }
      }
    }
    return result;
  }

  public void dynamicSet(int dynamicFeatureID, Object value)
  {
    EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
    if (eStructuralFeature.isTransient())
    {
      eSettings[dynamicFeatureID] = value;
    }
    else
    {
      eStore().set(this, eStructuralFeature, InternalEObject.EStore.NO_INDEX, value);
      if (eIsCaching())
      {
        eSettings[dynamicFeatureID] = value;
      }
    }
  }

  public void dynamicUnset(int dynamicFeatureID)
  {
    EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
    if (eStructuralFeature.isTransient())
    {
      eSettings[dynamicFeatureID] = null;
    }
    else
    {
      eStore().unset(this, eStructuralFeature);
      eSettings[dynamicFeatureID] = null;
    }
  }

  @Override
  protected boolean eDynamicIsSet(int dynamicFeatureID, EStructuralFeature eFeature)
  {
    return
      dynamicFeatureID < 0 ?
        eOpenIsSet(eFeature) :
        eFeature.isTransient() ?
          eSettingDelegate(eFeature).dynamicIsSet(this, eSettings(), dynamicFeatureID) :
          eStore().isSet(this, eFeature);
  }

  protected EList<?> createList(final EStructuralFeature eStructuralFeature)
  {
    final EClassifier eType = eStructuralFeature.getEType();
    if (eType.getInstanceClassName() == "java.util.Map$Entry")
    {
      class EStoreEcoreEMap extends EcoreEMap<Object, Object>
      {
        private static final long serialVersionUID = 1L;

        public EStoreEcoreEMap()
        {
          super
            ((EClass)eType, 
             BasicEMap.Entry.class,
             null);
          delegateEList =
             new BasicEStoreEList<BasicEMap.Entry<Object, Object>>(EStoreEObjectImpl.this, eStructuralFeature)
             {
                private static final long serialVersionUID = 1L;

                @Override
                protected void didAdd(int index, BasicEMap.Entry<Object, Object> newObject)
                {
                  EStoreEcoreEMap.this.doPut(newObject);
                }

                @Override
                protected void didSet(int index, BasicEMap.Entry<Object, Object> newObject, BasicEMap.Entry<Object, Object> oldObject)
                {
                  didRemove(index, oldObject);
                  didAdd(index, newObject);
                }

                @Override
                protected void didRemove(int index, BasicEMap.Entry<Object, Object> oldObject)
                {
                  EStoreEcoreEMap.this.doRemove(oldObject);
                }

                @Override
                protected void didClear(int size, Object [] oldObjects)
                {
                  EStoreEcoreEMap.this.doClear();
                }

                @Override
                protected void didMove(int index, BasicEMap.Entry<Object, Object> movedObject, int oldIndex)
                {
                  EStoreEcoreEMap.this.doMove(movedObject);
                }
             };
          size = delegateEList.size();
        }
      }
      return new EStoreEcoreEMap();
    }
    else
    {
      return new BasicEStoreEList<Object>(this, eStructuralFeature);
    }
  }

  protected FeatureMap createFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return new EStoreFeatureMap(this, eStructuralFeature, eStore());
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    if (eContainer == EUNINITIALIZED_CONTAINER)
    {
      eInitializeContainer();
    }

    return eContainer;
  }

  @Override
  public int eContainerFeatureID()
  {
    if (eContainer == EUNINITIALIZED_CONTAINER)
    {
      eInitializeContainer();
    }

    return eContainerFeatureID;
  }

  protected void eInitializeContainer()
  {
    eContainer = eStore().getContainer(this);
    if (eContainer != null)
    {
      EStructuralFeature eContainingFeature = eStore().getContainingFeature(this);
      if (eContainingFeature instanceof EReference)
      {
        EReference eContainingReference = (EReference)eContainingFeature;
        EReference eOpposite = eContainingReference.getEOpposite();
        if (eOpposite != null)
        {
          eContainerFeatureID = eClass().getFeatureID(eOpposite);
          return;
        }
      }

      eContainerFeatureID = EOPPOSITE_FEATURE_BASE - eContainer.eClass().getFeatureID(eContainingFeature);
    }
  }

  @Override
  public InternalEObject.EStore eStore()
  {
    return eStore;
  }

  @Override
  public void eSetStore(InternalEObject.EStore store)
  {
    this.eStore = store;
  }

  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  @Override
  public int eDerivedStructuralFeatureID(EStructuralFeature eStructuralFeature)
  {
    return eClass().getFeatureID(eStructuralFeature);
  }

  @Override
  protected BasicEObjectImpl.EPropertiesHolder eProperties()
  {
    if (eProperties == null)
    {
      eProperties = new EStoreEPropertiesHolderImpl();
    }
    return eProperties;
  }

  @Override
  protected boolean eHasSettings()
  {
    return eSettings != null;
  }

  @Override
  protected EStructuralFeature.Internal.DynamicValueHolder eSettings()
  {
    if (eSettings == null)
    {
      int size = eClass().getFeatureCount() - eStaticFeatureCount();
      eSettings = size == 0 ? ENO_SETTINGS : new Object [size];
    }

    return this;
  }

/*
  public String toString()
  {
    String result = super.toString();
    int index = result.indexOf("EStoreEObjectImpl");
    return index == -1 ? result : result.substring(0, index) + result.substring(index + 6);
  }
*/


  /**
   *  This class is for testing purposes only and will be removed.
   */
  public static class EStoreImpl implements InternalEObject.EStore
  {
    // protected static final EStructuralFeature CONTAINING_FEATURE = new EReferenceImpl();
    // protected static final EStructuralFeature CONTAINER = new EReferenceImpl();

    protected Map<Entry, Object> map = new HashMap<Entry, Object>();

    public static class Entry
    {
      protected EObject eObject;
      protected EStructuralFeature eStructuralFeature;

      public Entry(InternalEObject eObject, EStructuralFeature eStructuralFeature)
      {
        this.eObject = eObject;
        this.eStructuralFeature = eStructuralFeature;
      }

      @Override
      public boolean equals(Object that)
      {
        if (that instanceof Entry)
        {
          Entry entry = (Entry)that;
          return eObject == entry.eObject && eStructuralFeature == entry.eStructuralFeature;
        }
        else
        {
          return false;
        }
      }

      @Override
      public int hashCode()
      {
        return eObject.hashCode() ^ eStructuralFeature.hashCode();
      }
    }

    protected EList<Object> getList(Entry entry)
    {
      @SuppressWarnings("unchecked")
      EList<Object> result = (EList<Object>)map.get(entry);
      if (result == null)
      {
        result = new BasicEList<Object>();
        map.put(entry, result);
      }
      return result;
    }

    public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      Entry entry = new Entry(eObject, feature);
      if (index == NO_INDEX)
      {
        return map.get(entry);
      }
      else
      {
        return getList(entry).get(index);
      }
    }

    public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      Entry entry = new Entry(eObject, feature);
      if (index == NO_INDEX)
      {
        return map.put(entry, value);
      }
      else
      {
        List<Object> list = getList(entry);
        return list.set(index, value);
      }
    }

    public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      Entry entry = new Entry(eObject, feature);
      getList(entry).add(index, value);
    }

    public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).remove(index);
    }

    public Object move(InternalEObject eObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).move(targetIndex, sourceIndex);
    }

    public void clear(InternalEObject eObject, EStructuralFeature feature)
    {
      Entry entry = new Entry(eObject, feature);
      map.remove(entry);
      //getList(entry).clear();
    }

    public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
    {
      Entry entry = new Entry(eObject, feature);
      return map.containsKey(entry);
    }

    public void unset(InternalEObject eObject, EStructuralFeature feature)
    {
      Entry entry = new Entry(eObject, feature);
      map.remove(entry);
    }

    public int size(InternalEObject eObject, EStructuralFeature feature)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).size();
    }

    public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).indexOf(value);
    }

    public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).lastIndexOf(value);
    }

    public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).toArray();
    }

    public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] array)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).toArray(array);
    }

    public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).isEmpty();
    }

    public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).contains(value);
    } 

    public int hashCode(InternalEObject eObject, EStructuralFeature feature)
    {
      Entry entry = new Entry(eObject, feature);
      return getList(entry).hashCode();
    }

    public InternalEObject getContainer(InternalEObject eObject)
    {
      return null;

      // Entry entry = new Entry(eObject, CONTAINER);
      // return (InternalEObject)map.get(entry);
    }

    public EStructuralFeature getContainingFeature(InternalEObject eObject)
    {
      // This should never be called.
      //
      throw new UnsupportedOperationException();
      // Entry entry = new Entry(eObject, CONTAINING_FEATURE);
      // return (EStructuralFeature)map.get(entry);
    }

    public EObject create(EClass eClass)
    {
      InternalEObject result = new EStoreEObjectImpl(eClass, this);
      return result;
    }
  }
}
