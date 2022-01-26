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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;


/**
 * An adapter that maintains itself as an adapter for all contained objects 
 * as they come and go.
 * It can be installed for an {@link EObject}, a {@link Resource}, or a {@link ResourceSet}.
 */
public class EContentAdapter extends AdapterImpl
{
  /**
   * Handles a notification by calling {@link #selfAdapt selfAdapter}.
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    selfAdapt(notification);

    super.notifyChanged(notification);
  }

  /**
   * Handles a notification by calling {@link #handleContainment handleContainment}
   * for any containment-based notification.
   */
  protected void selfAdapt(Notification notification)
  {
    Object notifier = notification.getNotifier();
    if (notifier instanceof ResourceSet)
    {
      if (notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES)
      {
        handleContainment(notification);
      }
    }
    else if (notifier instanceof Resource)
    {
      if (notification.getFeatureID(Resource.class) == Resource.RESOURCE__CONTENTS)
      {
        handleContainment(notification);
      }
    }
    else if (notifier instanceof EObject)
    {
      Object feature = notification.getFeature();
      if (feature instanceof EReference)
      {
        EReference eReference = (EReference)feature;
        if (eReference.isContainment())
        {
          handleContainment(notification);
        }
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
        // We need to be careful that the proxy may be resolved while we are attaching this adapter.
        // We need to avoid attaching the adapter during the resolve 
        // and also attaching it again as we walk the eContents() later.
        // Checking here avoids having to check during addAdapter.
        //
        Notifier oldValue = (Notifier)notification.getOldValue();
        if (oldValue.eAdapters().contains(this))
        {
          removeAdapter(oldValue);
          Notifier newValue = (Notifier)notification.getNewValue();
          addAdapter(newValue);
        }
        break;
      }
      case Notification.UNSET:
      {
        Object oldValue = notification.getOldValue();
        if (oldValue != Boolean.TRUE && oldValue != Boolean.FALSE)
        {
          if (oldValue != null)
          {
            removeAdapter((Notifier)oldValue);
          }
          Notifier newValue = (Notifier)notification.getNewValue();
          if (newValue != null)
          {
            addAdapter(newValue);
          }
        }
        break;
      }
      case Notification.SET:
      {
        Notifier oldValue = (Notifier)notification.getOldValue();
        if (oldValue != null)
        {
          removeAdapter(oldValue);
        }
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
        @SuppressWarnings("unchecked") Collection<Notifier> newValues = (Collection<Notifier>)notification.getNewValue();
        for (Notifier newValue : newValues)
        {
          addAdapter(newValue);
        }
        break;
      }
      case Notification.REMOVE:
      {
        Notifier oldValue = (Notifier)notification.getOldValue();
        if (oldValue != null)
        {
          removeAdapter(oldValue);
        }
        break;
      }
      case Notification.REMOVE_MANY:
      {
        @SuppressWarnings("unchecked") Collection<Notifier> oldValues = (Collection<Notifier>)notification.getOldValue();
        for ( Notifier oldContentValue : oldValues)
        {
          removeAdapter(oldContentValue);
        }
        break;
      }
    }
  }

  /**
   * Handles installation of the adapter
   * by adding the adapter to each of the directly contained objects.
   */
  @Override
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
    else
    {
      basicSetTarget(target);
    }
  }
  
  /**
   * Actually sets the target by calling super.
   */
  protected void basicSetTarget(Notifier target)
  {
    super.setTarget(target);
  }

  /**
   * Handles installation of the adapter on an EObject
   * by adding the adapter to each of the directly contained objects.
   */
  protected void setTarget(EObject target)
  {
    basicSetTarget(target);
    for (Iterator<? extends Notifier> i = resolve() ? 
           target.eContents().iterator() : 
           ((InternalEList<? extends Notifier>)target.eContents()).basicIterator();
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
    basicSetTarget(target);
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
    basicSetTarget(target);
    List<Resource> resources =  target.getResources();
    for (int i = 0; i < resources.size(); ++i)
    {
      Notifier notifier = resources.get(i);
      if (!notifier.eAdapters().contains(this))
      {
        addAdapter(notifier);
      }
    }
  }

  /**
   * Handles undoing the installation of the adapter
   * by removing the adapter from each of the directly contained objects.
   */
  @Override
  public void unsetTarget(Notifier target)
  {
    unsetTarget((Object)target);
  }

  /**
   * Actually unsets the target by calling super.
   */
  protected void basicUnsetTarget(Notifier target)
  {
    super.unsetTarget(target);
  }
  
  /**
   * Handles undoing the installation of the adapter
   * by removing the adapter from each of the directly contained objects.
   * @deprecated Use or override {@link #unsetTarget(Notifier)} instead.
   */
  @Deprecated
  protected void unsetTarget(Object target)
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
    else
    {
      basicUnsetTarget((Notifier)target);
    }
  }

  /**
   * Handles undoing the installation of the adapter from an EObject
   * by removing the adapter from each of the directly contained objects.
   */
  protected void unsetTarget(EObject target)
  {
    basicUnsetTarget(target);
    for (Iterator<? extends Notifier> i = resolve() ? 
           target.eContents().iterator() : 
           ((InternalEList<EObject>)target.eContents()).basicIterator(); 
         i.hasNext(); )
    {
      Notifier notifier = i.next();
      removeAdapter(notifier);
    }
  }

  /**
   * Handles undoing the installation of the adapter from a Resource
   * by removing the adapter from each of the directly contained objects.
   */
  protected void unsetTarget(Resource target)
  {
    basicUnsetTarget(target);
    List<EObject> contents = target.getContents();
    for (int i = 0, size = contents.size(); i < size; ++i)
    {
      Notifier notifier = contents.get(i);
      removeAdapter(notifier);
    }
  }

  /**
   * Handles undoing the installation of the adapter from a ResourceSet
   * by removing the adapter from each of the directly contained objects.
   */
  protected void unsetTarget(ResourceSet target)
  {
    basicUnsetTarget(target);
    List<Resource> resources =  target.getResources();
    for (int i = 0; i < resources.size(); ++i)
    {
      Notifier notifier = resources.get(i);
      removeAdapter(notifier);
    }
  }
  
  protected void addAdapter(Notifier notifier)
  {
    notifier.eAdapters().add(this); 
  }
  
  protected void removeAdapter(Notifier notifier)
  {
    notifier.eAdapters().remove(this); 
  }
  
  protected boolean resolve()
  {
    return true;
  }
}
