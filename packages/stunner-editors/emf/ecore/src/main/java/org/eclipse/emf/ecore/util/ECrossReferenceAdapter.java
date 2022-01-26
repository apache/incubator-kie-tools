/**
 * Copyright (c) 2005-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;


/**
 * An adapter that maintains itself as an adapter for all contained objects.
 * It can be installed for an {@link EObject}, a {@link Resource}, or a {@link ResourceSet}.
 * @since 2.2
 */
public class ECrossReferenceAdapter implements Adapter.Internal
{
  /**
   * Returns the first {@link ECrossReferenceAdapter} in the notifier's {@link Notifier#eAdapters() adapter list}, 
   * or <code>null</code>, if there isn't one.
   * @param notifier the object to search.
   * @return the first ECrossReferenceAdapter in the notifier's adapter list.
   */
  public static ECrossReferenceAdapter getCrossReferenceAdapter(Notifier notifier) 
  {
    List<Adapter> adapters = notifier.eAdapters();
    for (int i = 0, size = adapters.size(); i < size; ++i)
    {
      Object adapter = adapters.get(i);
      if (adapter instanceof ECrossReferenceAdapter)
      {
        return (ECrossReferenceAdapter)adapter;
      }
    }
    return null;
  }
  
  protected Set<Resource> unloadedResources = new HashSet<Resource>();
  protected Map<EObject, Resource> unloadedEObjects = new HashMap<EObject, Resource>();
  
  protected class InverseCrossReferencer extends EcoreUtil.CrossReferencer
  {
    private static final long serialVersionUID = 1L;

    protected Map<URI, List<EObject>> proxyMap;
    
    protected InverseCrossReferencer()
    {
      super((Collection<Notifier>)null);
    }
    
    @Override
    protected EContentsEList.FeatureIterator<EObject> getCrossReferences(EObject eObject)
    {
      return
        new ECrossReferenceEList.FeatureIteratorImpl<EObject>(eObject)
        {
          @Override
          protected boolean isIncluded(EStructuralFeature eStructuralFeature)
          {
            return FeatureMapUtil.isFeatureMap(eStructuralFeature) || ECrossReferenceAdapter.this.isIncluded((EReference)eStructuralFeature);
          }

          @Override
          protected boolean resolve()
          {
            return InverseCrossReferencer.this.resolve();
          }
        };
    }

    @Override
    protected boolean crossReference(EObject eObject, EReference eReference, EObject crossReferencedEObject)
    {
      return isIncluded(eReference);
    }
    
    @Override
    protected Collection<EStructuralFeature.Setting> newCollection()
    {
      return 
        new BasicEList<EStructuralFeature.Setting>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected Object[] newData(int capacity)
          {
            return new EStructuralFeature.Setting [capacity];
          }

          @Override
          public boolean add(EStructuralFeature.Setting setting)
          {
            EObject eObject = setting.getEObject();
            EStructuralFeature eStructuralFeature = setting.getEStructuralFeature();
            EStructuralFeature.Setting [] settingData =  (EStructuralFeature.Setting[])data;
            for (int i = 0; i < size; ++i)
            {
              EStructuralFeature.Setting containedSetting = settingData[i];
              if (containedSetting.getEObject() == eObject && containedSetting.getEStructuralFeature() == eStructuralFeature)
              {
                return false;
              }
            }
            addUnique(setting);
            return true;
          }
        };
    }
    
    public void add(EObject eObject)
    {
      handleCrossReference(eObject);
      if (!resolve())
      {
        addProxy(eObject, eObject);
      }
    }
    
    @Override
    protected void add(InternalEObject eObject, EReference eReference, EObject crossReferencedEObject)
    {
      super.add(eObject, eReference, crossReferencedEObject);
      if (!resolve())
      {
        addProxy(crossReferencedEObject, eObject);
      }
    }
    
    public void add(EObject eObject, EReference eReference, EObject crossReferencedEObject)
    {
      add((InternalEObject)eObject, eReference, crossReferencedEObject);
    }
    
    protected void addProxy(EObject proxy, EObject context)
    {
      if (proxy.eIsProxy())
      {
        if (proxyMap == null)
        {
          proxyMap = new HashMap<URI, List<EObject>>();
        }
        URI uri = normalizeURI(((InternalEObject)proxy).eProxyURI(), context);
        List<EObject> proxies = proxyMap.get(uri);
        if (proxies == null)
        {
          proxyMap.put(uri, proxies = new BasicEList.FastCompare<EObject>());
        }
        proxies.add(proxy);
      }
    }

    public Object remove(EObject eObject)
    {
      if (!resolve())
      {
        removeProxy(eObject, eObject);
      }
      return super.remove(eObject);
    }

    public void remove(EObject eObject, EReference eReference, EObject crossReferencedEObject)
    {
      if (!resolve())
      {
        removeProxy(crossReferencedEObject, eObject);
      }
      BasicEList<EStructuralFeature.Setting> collection = (BasicEList<EStructuralFeature.Setting>)get(crossReferencedEObject);
      if (collection != null)
      {
        EStructuralFeature.Setting [] settingData =  (EStructuralFeature.Setting[])collection.data();
        for (int i = 0, size = collection.size(); i < size; ++i)
        {
          EStructuralFeature.Setting setting = settingData[i];
          if (setting.getEObject() == eObject && setting.getEStructuralFeature() == eReference)
          {
            if (collection.size() == 1)
            {
              super.remove(crossReferencedEObject);  
            }
            else
            {
              collection.remove(i);
            }
            break;
          }
        }
      }      
    }

    protected void removeProxy(EObject proxy, EObject context)
    {
      if (proxyMap != null && proxy.eIsProxy())
      {
        URI uri = normalizeURI(((InternalEObject)proxy).eProxyURI(), context);
        List<EObject> proxies = proxyMap.get(uri);
        if (proxies != null)
        {
          proxies.remove(proxy);
          if (proxies.isEmpty())
          {
            proxyMap.remove(uri);
          }
        }
      }
    }
    
    protected List<EObject> removeProxies(URI uri)
    {
      return proxyMap != null ? proxyMap.remove(uri) : null;
    }
    
    protected URI normalizeURI(URI uri, EObject objectContext)
    {
      // This should be the same as the logic in ResourceImpl.getEObject(String).
      //
      String fragment = uri.fragment();
      if (fragment != null)
      {
        int length = fragment.length();
        if (length > 0 && fragment.charAt(0) != '/' && fragment.charAt(length - 1) == '?')
        {
          int index = fragment.lastIndexOf('?', length - 2);
          if (index > 0)
          {
            uri = uri.trimFragment().appendFragment(fragment.substring(0, index));
          }
        }
      }
      Resource resourceContext = objectContext.eResource();
      if (resourceContext != null)
      {
        ResourceSet resourceSetContext = resourceContext.getResourceSet();
        if (resourceSetContext != null)
        {
          return resourceSetContext.getURIConverter().normalize(uri);
        }
      }
      return uri;
    }
    
    @Override
    protected boolean resolve()
    {
      return ECrossReferenceAdapter.this.resolve();
    }
  }
  
  protected InverseCrossReferencer inverseCrossReferencer;
  
  public ECrossReferenceAdapter()
  {
    inverseCrossReferencer = createInverseCrossReferencer();
  }
  
  public Collection<EStructuralFeature.Setting> getNonNavigableInverseReferences(EObject eObject)
  {
    return getNonNavigableInverseReferences(eObject, !resolve());
  }

  public Collection<EStructuralFeature.Setting> getNonNavigableInverseReferences(EObject eObject, boolean resolve)
  {
    if (resolve)
    {
      resolveAll(eObject);
    }

    Collection<EStructuralFeature.Setting> result = inverseCrossReferencer.get(eObject);
    if (result == null)
    {
      result = Collections.emptyList();
    }
    return result;
  }
  
  public Collection<EStructuralFeature.Setting> getInverseReferences(EObject eObject)
  {
    return getInverseReferences(eObject, !resolve());
  }

  public Collection<EStructuralFeature.Setting> getInverseReferences(EObject eObject, boolean resolve)
  {
    Collection<EStructuralFeature.Setting> result = new ArrayList<EStructuralFeature.Setting>();
    
    if (resolve)
    {
      resolveAll(eObject);
    }
    
    EObject eContainer = eObject.eContainer();
    if (eContainer != null)
    {
      result.add(((InternalEObject)eContainer).eSetting(eObject.eContainmentFeature()));
    }
    
    Collection<EStructuralFeature.Setting> nonNavigableInverseReferences = inverseCrossReferencer.get(eObject);
    if (nonNavigableInverseReferences != null)
    {
      result.addAll(nonNavigableInverseReferences);
    }
    
    for (EReference eReference : eObject.eClass().getEAllReferences())
    {
      EReference eOpposite = eReference.getEOpposite();
      if (eOpposite != null && !eReference.isContainer() && eObject.eIsSet(eReference))
      {
        if (eReference.isMany())
        {
          Object collection = eObject.eGet(eReference);
          for (@SuppressWarnings("unchecked") Iterator<EObject> j = 
                 resolve() ? 
                   ((Collection<EObject>)collection).iterator() : 
                   ((InternalEList<EObject>)collection).basicIterator(); 
               j.hasNext(); )
          {
            InternalEObject referencingEObject = (InternalEObject)j.next();
            result.add(referencingEObject.eSetting(eOpposite));
          }
        }
        else
        {
          result.add(((InternalEObject)eObject.eGet(eReference, resolve())).eSetting(eOpposite));
        }
      }
    }
    
    return result;
  }
  
  protected void resolveAll(EObject eObject)
  {
    if (!eObject.eIsProxy())
    {
      Resource resource = eObject.eResource();
      if (resource != null)
      {
        URI uri = resource.getURI();
        if (uri != null)
        {
          ResourceSet resourceSet = resource.getResourceSet();
          if (resourceSet != null)
          {
            uri = resourceSet.getURIConverter().normalize(uri);
          }
          uri = uri.appendFragment(resource.getURIFragment(eObject));
        }
        else
        {
          URI.createHierarchicalURI(null, null, resource.getURIFragment(eObject));
        }
        List<EObject> proxies = inverseCrossReferencer.removeProxies(uri);
        if (proxies != null)
        {
          for (int i = 0, size = proxies.size(); i < size; ++i)
          {
            EObject proxy = proxies.get(i);
            for (EStructuralFeature.Setting setting : getInverseReferences(proxy, false))
            {
              resolveProxy(resource, eObject, proxy, setting);
            }
          }
        }
      }
    }
  }

  protected void resolveProxy(Resource resource, EObject eObject, EObject proxy, EStructuralFeature.Setting setting)
  {
    Object value = setting.get(true);
    if (setting.getEStructuralFeature().isMany())
    {
      InternalEList<?> list = (InternalEList<?>)value;
      List<?> basicList = list.basicList();
      int index =  basicList.indexOf(proxy);
      if (index != -1)
      {
        list.get(index);
      }
    }
  }

  protected boolean isIncluded(EReference eReference)
  {
    return eReference.getEOpposite() == null && !eReference.isDerived();
  }
  
  protected InverseCrossReferencer createInverseCrossReferencer()
  {
    return new InverseCrossReferencer();
  }
  
  /**
   * Handles a notification by calling {@link #selfAdapt selfAdapter}.
   */
  public void notifyChanged(Notification notification)
  {
    selfAdapt(notification);
  }

  /**
   * Handles a notification by calling {@link #handleContainment handleContainment}
   * for any containment-based notification.
   */
  protected void selfAdapt(Notification notification)
  {
    Object notifier = notification.getNotifier();
    if (notifier instanceof EObject)
    {
      Object feature = notification.getFeature();
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          handleContainment(notification);
        }
        else if (isIncluded(reference))
        {
          handleCrossReference(reference, notification);
        }
      }
    }
    else if (notifier instanceof Resource)
    {
      switch (notification.getFeatureID(Resource.class))
      { 
        case Resource.RESOURCE__CONTENTS:
        {
          if (!unloadedResources.contains(notifier))
          {
            switch (notification.getEventType())
            {
              case Notification.REMOVE:
              {
                Resource resource = (Resource)notifier;
                if (!resource.isLoaded())
                {
                  EObject eObject = (EObject)notification.getOldValue();
                  unloadedEObjects.put(eObject, resource);
                  for (Iterator<EObject> i = EcoreUtil.getAllProperContents(eObject, false); i.hasNext(); )
                  {
                    unloadedEObjects.put(i.next(), resource);
                  }
                }
                break;
              }
              case Notification.REMOVE_MANY:
              {
                Resource resource = (Resource)notifier;
                if (!resource.isLoaded())
                {
                  @SuppressWarnings("unchecked")
                  List<EObject> eObjects = (List<EObject>)notification.getOldValue();
                  for (Iterator<EObject> i = EcoreUtil.getAllProperContents(eObjects, false); i.hasNext(); )
                  {
                    unloadedEObjects.put(i.next(), resource);
                  }
                }
                break;
              }
              default:
              {
                handleContainment(notification);
                break;
              }
            }
          }
          break;
        }
        case Resource.RESOURCE__IS_LOADED:
        {
          if (notification.getNewBooleanValue())
          {
            unloadedResources.remove(notifier);
            for (Notifier child : ((Resource)notifier).getContents())
            {
              addAdapter(child);
            }
          }
          else
          {
            unloadedResources.add((Resource)notifier);
            for (Iterator<Map.Entry<EObject, Resource>> i = unloadedEObjects.entrySet().iterator(); i.hasNext(); )
            {
              Map.Entry<EObject, Resource> entry = i.next();
              if (entry.getValue() == notifier)
              {
                i.remove();
                EObject eObject = entry.getKey();
                Collection<EStructuralFeature.Setting> settings = inverseCrossReferencer.get(eObject);
                if (settings != null)
                {
                  for (EStructuralFeature.Setting setting : settings)
                  {
                    inverseCrossReferencer.addProxy(eObject, setting.getEObject());
                  }
                }
              }
            }
          }
          break;
        }
      }
    }
    else if (notifier instanceof ResourceSet)
    {
      if (notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES)
      {
        handleContainment(notification);
      }
    }
  }

  /**
   * Handles a containment change by adding and removing the adapter as appropriate.
   */
  protected void handleContainment(Notification notification)
  {
    switch (notification.getEventType())
    {
      case Notification.RESOLVE:
      {
        Notifier oldValue = (Notifier)notification.getOldValue();
        removeAdapter(oldValue);
        Notifier newValue = (Notifier)notification.getNewValue();
        addAdapter(newValue);
        break;
      }
      case Notification.UNSET:
      {
        Object newValue = notification.getNewValue();
        if (newValue != null && newValue != Boolean.TRUE && newValue != Boolean.FALSE)
        {
          addAdapter((Notifier)newValue);
        }
        break;
      }
      case Notification.SET:
      {
        Notifier newValue = (Notifier)notification.getNewValue();
        if (newValue != null)
        {
          addAdapter(newValue);
        }
        break;
      }
      case Notification.ADD:
      {
        Notifier newValue = (Notifier)notification.getNewValue();
        if (newValue != null)
        {
          addAdapter(newValue);
        }
        break;
      }
      case Notification.ADD_MANY:
      {
        for (Object newValue : (Collection<?>)notification.getNewValue())
        {
          addAdapter((Notifier)newValue);
        }
        break;
      }
    }
  }
  
  /**
   * Handles a cross reference change by adding and removing the adapter as appropriate.
   */
  protected void handleCrossReference(EReference reference, Notification notification)
  {
    switch (notification.getEventType())
    {
      case Notification.RESOLVE:
      case Notification.SET:
      case Notification.UNSET:
      {
        EObject notifier = (EObject)notification.getNotifier();
        EReference feature = (EReference)notification.getFeature();
        if (!feature.isMany() || notification.getPosition() != Notification.NO_INDEX)
        {
          EObject oldValue = (EObject)notification.getOldValue();
          if (oldValue != null)
          {
            inverseCrossReferencer.remove(notifier, feature, oldValue);
          }
          EObject newValue = (EObject)notification.getNewValue();
          if (newValue != null)
          {
            inverseCrossReferencer.add(notifier, feature, newValue);
          }
        }
        break;
      }
      case Notification.ADD:
      {
        EObject newValue = (EObject)notification.getNewValue();
        if (newValue != null)
        {
          inverseCrossReferencer.add((EObject)notification.getNotifier(), (EReference)notification.getFeature(), newValue);
        }
        break;
      }
      case Notification.ADD_MANY:
      {
        EObject notifier = (EObject)notification.getNotifier();
        EReference feature = (EReference)notification.getFeature();
        for (Object newValue : (Collection<?>)notification.getNewValue())
        {
          inverseCrossReferencer.add(notifier, feature, (EObject)newValue);
        }
        break;
      }
      case Notification.REMOVE:
      {
        EObject oldValue = (EObject)notification.getOldValue();
        if (oldValue != null)
        {
          inverseCrossReferencer.remove((EObject)notification.getNotifier(), (EReference)notification.getFeature(), oldValue);
        }
        break;
      }
      case Notification.REMOVE_MANY:
      {
        EObject notifier = (EObject)notification.getNotifier();
        EReference feature = (EReference)notification.getFeature();
        for (Object oldValue : (Collection<?>)notification.getOldValue())
        {
          inverseCrossReferencer.remove(notifier, feature, (EObject)oldValue);
        }
        break;
      }
    }
  }

  /**
   * Handles installation of the adapter
   * by adding the adapter to each of the directly contained objects.
   */
  public void setTarget(Notifier target)
  {
    if (target instanceof EObject)
    {
      setTarget((EObject)target);
    }
    else if (target instanceof Resource)
    {
      setTarget((Resource)target);
    }
    else if (target instanceof ResourceSet)
    {
      setTarget((ResourceSet)target);
    }
  }

  /**
   * Handles installation of the adapter on an EObject
   * by adding the adapter to each of the directly contained objects.
   */
  protected void setTarget(EObject target)
  {
    inverseCrossReferencer.add(target);
    for (@SuppressWarnings("unchecked") Iterator<EObject> i = 
           resolve() ? 
              target.eContents().iterator() : 
              (Iterator<EObject>)((InternalEList<?>)target.eContents()).basicIterator(); 
         i.hasNext(); )
    {
      Notifier notifier = i.next();
      addAdapter(notifier);
    }
  }

  /**
   * Handles installation of the adapter on a Resource
   * by adding the adapter to each of the directly contained objects.
   */
  protected void setTarget(Resource target)
  {
    if (!target.isLoaded())
    {
      unloadedResources.add(target);
    }
    List<EObject> contents = target.getContents();
    for (int i = 0, size = contents.size(); i < size; ++i)
    {
      Notifier notifier = contents.get(i);
      addAdapter(notifier);
    }
  }

  /**
   * Handles installation of the adapter on a ResourceSet
   * by adding the adapter to each of the directly contained objects.
   */
  protected void setTarget(ResourceSet target)
  {
    List<Resource> resources =  target.getResources();
    for (int i = 0; i < resources.size(); ++i)
    {
      Notifier notifier = resources.get(i);
      addAdapter(notifier);
    }
  }

  /**
   * Handles undoing the installation of the adapter
   * by removing the adapter to each of the directly contained objects.
   */
  public void unsetTarget(Notifier target)
  {
    if (target instanceof EObject)
    {
      unsetTarget((EObject)target);
    }
    else if (target instanceof Resource)
    {
      unsetTarget((Resource)target);
    }
    else if (target instanceof ResourceSet)
    {
      unsetTarget((ResourceSet)target);
    }
  }

  /**
   * Handles undoing the installation of the adapter from an EObject
   * by removing the adapter to each of the directly contained objects.
   */
  protected void unsetTarget(EObject target)
  {
    for (EContentsEList.FeatureIterator<EObject> i = inverseCrossReferencer.getCrossReferences(target); i.hasNext(); )
    {
      EObject crossReferencedEObject = i.next();
      inverseCrossReferencer.remove(target, (EReference)i.feature(), crossReferencedEObject);     
    }

    for (@SuppressWarnings("unchecked") Iterator<InternalEObject> i = 
           resolve() ? 
             (Iterator<InternalEObject>)(Iterator<?>)target.eContents().iterator() : 
             (Iterator<InternalEObject>)((InternalEList<?>)target.eContents()).basicIterator(); 
         i.hasNext(); )
    {
      // Don't remove the adapter if the object is in a different resource 
      // and that resource (and hence all its contents) are being cross referenced.
      //
      InternalEObject internalEObject = i.next();
      Resource eDirectResource = internalEObject.eDirectResource();
      if (eDirectResource == null || !eDirectResource.eAdapters().contains(this))
      {
        removeAdapter(internalEObject);
      }
    }
  }

  /**
   * Handles undoing the installation of the adapter from a Resource
   * by removing the adapter to each of the directly contained objects.
   */
  protected void unsetTarget(Resource target)
  {
    List<EObject> contents = target.getContents();
    for (int i = 0, size = contents.size(); i < size; ++i)
    {
      Notifier notifier = contents.get(i);
      removeAdapter(notifier);
    }
    unloadedEObjects.remove(target);
  }

  /**
   * Handles undoing the installation of the adapter from a ResourceSet
   * by removing the adapter to each of the directly contained objects.
   */
  protected void unsetTarget(ResourceSet target)
  {
    List<Resource> resources =  target.getResources();
    for (int i = 0; i < resources.size(); ++i)
    {
      Notifier notifier = resources.get(i);
      removeAdapter(notifier);
    }
  }

  protected void addAdapter(Notifier notifier)
  {
    List<Adapter> eAdapters = notifier.eAdapters();
    if (!eAdapters.contains(this))
    {
      eAdapters.add(this);
    }
  }
  
  protected void removeAdapter(Notifier notifier)
  {
    notifier.eAdapters().remove(this); 
  }
  
  public void dump()
  {
    EcoreUtil.CrossReferencer.print(System.out, inverseCrossReferencer);
  }

  public Notifier getTarget()
  {
    return null;
  }

  public boolean isAdapterForType(Object type)
  {
    return false;
  }
  
  protected boolean resolve()
  {
    return true;
  }
}
