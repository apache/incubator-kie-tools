/**
 * Copyright (c) 2008-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.util;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.DynamicValueHolder;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * A basic implementation of a stateful setting delegate.
 * At least the first two of these methods need to be overridden.
 * <ul>
 *   <li>{@link #dynamicGet(InternalEObject, DynamicValueHolder, int, boolean, boolean)}</li>
 *   <li>{@link #dynamicIsSet(InternalEObject, DynamicValueHolder, int)}</li>
 *   <li>{@link #dynamicSet(InternalEObject, DynamicValueHolder, int, Object)}</li>
 *   <li>{@link #dynamicUnset(InternalEObject, DynamicValueHolder, int)}</li>
 *   <li>{@link #dynamicInverseAdd(InternalEObject, DynamicValueHolder, int, InternalEObject, NotificationChain)}</li>
 *   <li>{@link #dynamicInverseRemove(InternalEObject, DynamicValueHolder, int, InternalEObject, NotificationChain)}</li>
 * </ul>
 * 
 * @since 2.6
 */
public abstract class BasicSettingDelegate implements EStructuralFeature.Internal.SettingDelegate
{
  protected EStructuralFeature eStructuralFeature;

  public BasicSettingDelegate(EStructuralFeature eStructuralFeature)
  {
    this.eStructuralFeature = eStructuralFeature;
  }

  public Setting dynamicSetting(final InternalEObject owner, final DynamicValueHolder settings, final int dynamicFeatureID)
  {
    return 
      new EStructuralFeature.Setting()
      {
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
          return BasicSettingDelegate.this.dynamicGet(owner, settings, dynamicFeatureID, resolve, true);
        }

        public boolean isSet()
        {
          return BasicSettingDelegate.this.dynamicIsSet(owner, settings, dynamicFeatureID);
        }

        public void set(Object newValue)
        {
          BasicSettingDelegate.this.dynamicSet(owner, settings, dynamicFeatureID, newValue);
        }

        public void unset()
        {
          BasicSettingDelegate.this.dynamicUnset(owner, settings, dynamicFeatureID);
        }
      };
  }

  public abstract Object dynamicGet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, boolean resolve, boolean coreType);

  public abstract boolean dynamicIsSet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID);
  
  public void dynamicSet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, Object newValue)
  {
    throw new UnsupportedOperationException();
  }

  public void dynamicUnset(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID)
  {
    throw new UnsupportedOperationException();
  }

  public NotificationChain dynamicInverseAdd
   (InternalEObject owner,
    DynamicValueHolder settings,
    int dynamicFeatureID,
    InternalEObject otherEnd,
    NotificationChain notifications)
  {
    throw new UnsupportedOperationException();
  }

  public NotificationChain dynamicInverseRemove
   (InternalEObject owner,
    DynamicValueHolder settings,
    int dynamicFeatureID,
    InternalEObject otherEnd,
    NotificationChain notifications)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * A basic implementation of a stateless setting delegate.
   * At least the first two of these methods should be overridden.
   * <ul>
   *   <li>{@link #setting(InternalEObject)}</li>
   *   <li>{@link #get(InternalEObject, boolean, boolean)}</li>
   *   <li>{@link #set(InternalEObject, Object)}</li>
   *   <li>{@link #isSet(InternalEObject)}</li>
   *   <li>{@link #unset(InternalEObject)}</li>
   *   <li>{@link #inverseAdd(InternalEObject, InternalEObject, NotificationChain)}</li>
   *   <li>{@link #inverseRemove(InternalEObject, InternalEObject, NotificationChain)}</li>
   * </ul>
   */
  public static abstract class Stateless extends BasicSettingDelegate
  {
    public Stateless(EStructuralFeature eStructuralFeature)
    {
      super(eStructuralFeature);
    }

    @Override
    public final Setting dynamicSetting(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID)
    {
      return setting(owner);
    }

    /**
     * Creates a setting for the owner and this delegate's feature.
     * @param owner the owner for the setting.
     * @return a new setting.
     */
    protected Setting setting(final InternalEObject owner)
    {
      return 
        new EStructuralFeature.Setting()
        {
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
            return Stateless.this.get(owner, resolve, true);
          }
  
          public boolean isSet()
          {
            return Stateless.this.isSet(owner);
          }
  
          public void set(Object newValue)
          {
            Stateless.this.set(owner, newValue);
          }
  
          public void unset()
          {
            Stateless.this.unset(owner);
          }
        };
    }
  
    @Override
    public final Object dynamicGet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, boolean resolve, boolean coreType)
    {
      return get(owner, resolve, coreType);
    }
  
    /**
     * Returns the value of this delegate's feature for the owner.
     * @param owner the object for with to fetch the value.
     * @param resolve whether the returned object should be resolved it if is a proxy.
     * @param coreType whether to return the core type value or the API type value.
     * @return the value of this delegate's feature for the owner.
     * @see InternalEObject#eGet(EStructuralFeature, boolean, boolean)
     */
    protected abstract Object get(InternalEObject owner, boolean resolve, boolean coreType);
  
    @Override
    public final boolean dynamicIsSet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID)
    {
      return isSet(owner);
    }
  
    /**
     * Returns whether the value of this delegate's feature is considered set for the owner.
     * @param owner the object for with to test is set.
     * @return whether the value of this delegate's feature is considered set for the owner.
     * @see EObject#eIsSet(EStructuralFeature)
     */
    protected abstract boolean isSet(InternalEObject owner);
  
    @Override
    public final void dynamicSet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, Object newValue)
    {
      set(owner, newValue);
    }
  
    /**
     * Sets this new value of this delegate's feature for the owner.
     * @param owner the owner for which to set the value
     * @param newValue the new value for the feature.
     * @see EObject#eSet(EStructuralFeature, Object)
     */
    protected void set(InternalEObject owner, Object newValue)
    {
      throw new UnsupportedOperationException();
    }
  
    @Override
    public final void dynamicUnset(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID)
    {
      unset(owner);
    }
  
    /**
     * Unsets the values of this delegate's feature for the owner.
     * @param owner the owner for which to unset the value.
     * @see EObject#eUnset(EStructuralFeature)
     */
    protected void unset(InternalEObject owner)
    {
      throw new UnsupportedOperationException();
    }
  
    @Override
    public final NotificationChain dynamicInverseAdd
     (InternalEObject owner,
      DynamicValueHolder settings,
      int dynamicFeatureID,
      InternalEObject otherEnd,
      NotificationChain notifications)
    {
      return inverseAdd(owner, otherEnd, notifications);
    }
  
    /**
     * Adds the object at the other end of a bidirectional reference to this delegate's feature
     * and returns accumulated notifications.
     * @param owner the owner for which to do the inverse add.
     * @param otherEnd the object to inverse add.
     * @param notifications the notifications accumulated so far.
     * @return the accumulated notifications.
     */
    protected NotificationChain inverseAdd
     (InternalEObject owner,
      InternalEObject otherEnd,
      NotificationChain notifications)
    {
      throw new UnsupportedOperationException();
    }
  
    @Override
    public final NotificationChain dynamicInverseRemove
     (InternalEObject owner,
      DynamicValueHolder settings,
      int dynamicFeatureID,
      InternalEObject otherEnd,
      NotificationChain notifications)
    {
      return inverseRemove(owner, otherEnd, notifications);
    }
  
    /**
     * Remove the object at the other end of a bidirectional reference from this delegate's feature
     * and returns accumulated notifications.
     * @param owner the owner for which to do the inverse remove.
     * @param otherEnd the object to inverse remove.
     * @param notifications the notifications accumulated so far.
     * @return the accumulated notifications.
     */
    protected NotificationChain inverseRemove
     (InternalEObject owner,
      InternalEObject otherEnd,
      NotificationChain notifications)
    {
      throw new UnsupportedOperationException();
    }
  }
}
