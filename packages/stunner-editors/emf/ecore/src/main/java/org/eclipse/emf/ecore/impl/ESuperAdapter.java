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
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;


/**
 * An adapter implementation for maintaining {@link EClass}es
 */
public class ESuperAdapter extends AdapterImpl 
{
  public interface Holder
  {
    ESuperAdapter getESuperAdapter();
    boolean isFrozen();
  }

  /*
   * @deprecated
   */
  public static ESuperAdapter getESuperAdapter(EClass eClass)
  {
    return ((Holder)eClass).getESuperAdapter();
  }

  protected EList<EClass> subclasses;

  protected static final int ATTRIBUTES_MODIFIED = 0x0001;
  protected static final int REFERENCES_MODIFIED = 0x0002;
  protected static final int STRUCTURAL_FEATURES_MODIFIED = 0x0004;
  protected static final int CONTAINMENTS_MODIFIED = 0x0008;
  protected static final int OPERATIONS_MODIFIED = 0x0010;
  protected static final int SUPERS_MODIFIED = 0x0020;
  protected static final int LAST_ESUPER_ADAPTER_MODIFIED = SUPERS_MODIFIED;

  protected int modifiedState = 0x003F;

  protected final static int SUPERS = 0;
  protected final static int ATTRIBUTES = 1;
  protected final static int REFERENCES = 2;
  protected final static int OPERATIONS = 3;
  protected final static int STRUCTURAL_FEATURES = 4;

  protected static int getFeatureID(Notification notification)
  {
    int featureID = notification.getFeatureID(null);
    switch (featureID)
    {
      case EcorePackage.ECLASS__ESUPER_TYPES: return SUPERS;
      case EcorePackage.ECLASS__EATTRIBUTES: return ATTRIBUTES;
      case EcorePackage.ECLASS__EREFERENCES: return REFERENCES;
      case EcorePackage.ECLASS__EOPERATIONS: return OPERATIONS;
      case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES: return STRUCTURAL_FEATURES;
    }
    return -1;
  }

  public ESuperAdapter()
  {
    super();
  }

  public boolean isModified()
  {
    return modifiedState != 0;
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == ESuperAdapter.class;
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    int eventType = notification.getEventType();
    if (eventType != Notification.REMOVING_ADAPTER) 
    {
      int featureID = getFeatureID(notification);
      if (featureID == SUPERS)
      {
        switch (eventType)
        {
          case Notification.SET:
          {
            Object oldValue = notification.getOldValue();
            if (oldValue != null)
            {
              ESuperAdapter eSuperAdapter = ((Holder)oldValue).getESuperAdapter();
              eSuperAdapter.getSubclasses().remove(notification.getNotifier());
            }
            Object newValue = notification.getNewValue();
            if (newValue != null)
            {
              Holder holder =  (Holder)newValue;
              if (!holder.isFrozen())
              {
                ESuperAdapter eSuperAdapter = holder.getESuperAdapter();
                eSuperAdapter.getSubclasses().add((EClass)notification.getNotifier());
              }
            }
            break;
          }
          case Notification.RESOLVE:
          case Notification.ADD:
          {
            Object newValue = notification.getNewValue();
            if (newValue != null)
            {
              Holder holder = (Holder)newValue;
              if (!holder.isFrozen())
              {
                ESuperAdapter eSuperAdapter = holder.getESuperAdapter();
                eSuperAdapter.getSubclasses().add((EClass)notification.getNotifier());
              }
            }
            break;
          }
          case Notification.ADD_MANY:
          {
            Object newValue = notification.getNewValue();
            if (newValue != null)
            {
              for (Iterator<?> i = ((Collection<?>)newValue).iterator(); i.hasNext(); )
              {
                Holder holder =  (Holder)i.next();
                if (!holder.isFrozen())
                {
                  ESuperAdapter eSuperAdapter = holder.getESuperAdapter();
                  eSuperAdapter.getSubclasses().add((EClass)notification.getNotifier());
                }
              }
            }
            break;
          }
          case Notification.REMOVE:
          {
            Object oldValue = notification.getOldValue();
            if (oldValue != null)
            {
              Holder holder = (Holder)oldValue;
              if (!holder.isFrozen())
              {
                ESuperAdapter eSuperAdapter = holder.getESuperAdapter();
                eSuperAdapter.getSubclasses().remove(notification.getNotifier());
              }
            }
            break;
          }
          case Notification.REMOVE_MANY:
          {
            Object oldValue = notification.getOldValue();
            if (oldValue != null)
            {
              for (Iterator<?> i = ((Collection<?>)oldValue).iterator(); i.hasNext(); )
              {
                Holder holder = (Holder)i.next();
                if (!holder.isFrozen())
                {
                  ESuperAdapter eSuperAdapter = holder.getESuperAdapter();
                  eSuperAdapter.getSubclasses().remove(notification.getNotifier());
                }
              }
            }
            break;
          }
        }
      }

      setFlags(featureID);
    }
  }

  public boolean isAllAttributesCollectionModified()
  {
    return (modifiedState & ATTRIBUTES_MODIFIED) != 0;
  }

  public void setAllAttributesCollectionModified(boolean set)
  {
    if (set)
    {
      modifiedState |= ATTRIBUTES_MODIFIED;
    }
    else
    {
      modifiedState &= ~ATTRIBUTES_MODIFIED;
    }
  }

  public boolean isAllContainmentsCollectionModified()
  {
    return (modifiedState & CONTAINMENTS_MODIFIED) != 0;
  }

  public void setAllContainmentsCollectionModified(boolean set)
  {
    if (set)
    {
      modifiedState |= CONTAINMENTS_MODIFIED;
    }
    else
    {
      modifiedState &= ~CONTAINMENTS_MODIFIED;
    }
  }

  public boolean isAllReferencesCollectionModified()
  {
    return (modifiedState & REFERENCES_MODIFIED) != 0;
  }

  public void setAllReferencesCollectionModified(boolean set)
  {
    if (set)
    {
      modifiedState |= REFERENCES_MODIFIED;
    }
    else
    {
      modifiedState &= ~REFERENCES_MODIFIED;
    }
  }

  public boolean isAllOperationsCollectionModified()
  {
    return (modifiedState & OPERATIONS_MODIFIED) != 0;
  }

  public void setAllOperationsCollectionModified(boolean set)
  {
    if (set)
    {
      modifiedState |= OPERATIONS_MODIFIED;
    }
    else
    {
      modifiedState &= ~OPERATIONS_MODIFIED;
    }
  }

  public boolean isAllStructuralFeaturesCollectionModified()
  {
    return (modifiedState & STRUCTURAL_FEATURES_MODIFIED) != 0;
  }

  public void setAllStructuralFeaturesCollectionModified(boolean set)
  {
    if (set)
    {
      modifiedState |= STRUCTURAL_FEATURES_MODIFIED;
    }
    else
    {
      modifiedState &= ~STRUCTURAL_FEATURES_MODIFIED;
    }
  }

  public boolean isAllSuperCollectionModified()
  {
    return (modifiedState & SUPERS_MODIFIED) != 0;
  } 

  public void setAllSuperCollectionModified(boolean set)
  {
    if (set)
    {
      modifiedState |= SUPERS_MODIFIED;
    }
    else
    {
      modifiedState &= ~SUPERS_MODIFIED;
    }
  }

  public EList<EClass> getSubclasses()
  {
    if (subclasses == null)
    {
      subclasses = 
        new UniqueEList<EClass>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected Object [] newData(int capacity)
          {
            return new EClass [capacity];
          }

          @Override
          protected boolean useEquals()
          {
            return false;
          }
        };
    }
    return subclasses;
  }

  void setFlags(int featureId)
  {
    int oldModifiedState = modifiedState;

    switch (featureId)
    {
      case ATTRIBUTES:
      {
        setAllAttributesCollectionModified(true);
        setAllStructuralFeaturesCollectionModified(true);
        setAllContainmentsCollectionModified(true);
        break;
      }
      case REFERENCES:
      {
        setAllReferencesCollectionModified(true);
        setAllStructuralFeaturesCollectionModified(true);
        setAllContainmentsCollectionModified(true);
        break;
      }
      case STRUCTURAL_FEATURES:
      {
        setAllAttributesCollectionModified(true);
        setAllReferencesCollectionModified(true);
        setAllStructuralFeaturesCollectionModified(true);
        setAllContainmentsCollectionModified(true);
        break;
      }
      case OPERATIONS:
      {
        setAllOperationsCollectionModified(true);
        setAllContainmentsCollectionModified(true);
        break;
      }
      case SUPERS:
      {
        setAllSuperCollectionModified(true);
        setAllOperationsCollectionModified(true);
        setAllContainmentsCollectionModified(true);
        setAllAttributesCollectionModified(true);
        setAllReferencesCollectionModified(true);
        setAllStructuralFeaturesCollectionModified(true);
        break;
      }
    }

    if (modifiedState != oldModifiedState && subclasses != null)
    {
      for (Iterator<?> i = subclasses.iterator(); i.hasNext(); )
      {
        Holder subclass = (Holder)i.next();
        ESuperAdapter eSuperAdapter = subclass.getESuperAdapter();
        eSuperAdapter.setFlags(featureId);
      }
    }
  }
}
