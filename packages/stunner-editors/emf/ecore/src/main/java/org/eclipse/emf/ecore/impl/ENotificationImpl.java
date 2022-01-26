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
package org.eclipse.emf.ecore.impl;


import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;


/**
 * An implementation of an Ecore-specific notification.
 */
public class ENotificationImpl extends NotificationImpl
{
  protected InternalEObject notifier;
  protected int featureID = NO_FEATURE_ID;
  protected EStructuralFeature feature;

  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue, Object newValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldValue, newValue, isSetChange ? IS_SET_CHANGE_INDEX : NO_INDEX);
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue, Object newValue)
  {
    this(notifier, eventType, feature, oldValue, newValue, NO_INDEX);
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue, Object newValue, int position)
  {
    super(eventType, oldValue, newValue, position);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue, Object newValue, int position, boolean wasSet)
  {
    super(eventType, oldValue, newValue, position, wasSet);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldValue, newValue, isSetChange ? IS_SET_CHANGE_INDEX : NO_INDEX);
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue, int position, boolean wasSet)
  {
    super(eventType, oldValue, newValue, position, wasSet);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue)
  {
    this(notifier, eventType, featureID, oldValue, newValue, NO_INDEX);
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue, int position)
  {
    super(eventType, oldValue, newValue, position);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  public ENotificationImpl
    (InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue, boolean newBooleanValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldBooleanValue, newBooleanValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue, boolean newBooleanValue)
  {
    super(eventType, oldBooleanValue, newBooleanValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl
    (InternalEObject notifier, int eventType, EStructuralFeature feature, boolean oldBooleanValue, boolean newBooleanValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldBooleanValue, newBooleanValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, boolean oldBooleanValue, boolean newBooleanValue)
  {
    super(eventType, oldBooleanValue, newBooleanValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue, byte newByteValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldByteValue, newByteValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue, byte newByteValue)
  {
    super(eventType, oldByteValue, newByteValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, byte oldByteValue, byte newByteValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldByteValue, newByteValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, byte oldByteValue, byte newByteValue)
  {
    super(eventType, oldByteValue, newByteValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue, char newCharValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldCharValue, newCharValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue, char newCharValue)
  {
    super(eventType, oldCharValue, newCharValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, char oldCharValue, char newCharValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldCharValue, newCharValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, char oldCharValue, char newCharValue)
  {
    super(eventType, oldCharValue, newCharValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue, double newDoubleValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldDoubleValue, newDoubleValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue, double newDoubleValue)
  {
    super(eventType, oldDoubleValue, newDoubleValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, double oldDoubleValue, double newDoubleValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldDoubleValue, newDoubleValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, double oldDoubleValue, double newDoubleValue)
  {
    super(eventType, oldDoubleValue, newDoubleValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue, float newFloatValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldFloatValue, newFloatValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue, float newFloatValue)
  {
    super(eventType, oldFloatValue, newFloatValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, float oldFloatValue, float newFloatValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldFloatValue, newFloatValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, float oldFloatValue, float newFloatValue)
  {
    super(eventType, oldFloatValue, newFloatValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue, int newIntValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldIntValue, newIntValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue, int newIntValue)
  {
    super(eventType, oldIntValue, newIntValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, int oldIntValue, int newIntValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldIntValue, newIntValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, int oldIntValue, int newIntValue)
  {
    super(eventType, oldIntValue, newIntValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue, long newLongValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldLongValue, newLongValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue, long newLongValue)
  {
    super(eventType, oldLongValue, newLongValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, long oldLongValue, long newLongValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldLongValue, newLongValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, long oldLongValue, long newLongValue)
  {
    super(eventType, oldLongValue, newLongValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue, short newShortValue, boolean isSetChange)
  {
    this(notifier, eventType, featureID, oldShortValue, newShortValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  public ENotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue, short newShortValue)
  {
    super(eventType, oldShortValue, newShortValue);
    this.notifier = notifier;
    this.featureID = featureID;
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, short oldShortValue, short newShortValue, boolean isSetChange)
  {
    this(notifier, eventType, feature, oldShortValue, newShortValue);
    if (isSetChange)
    {
      this.position = IS_SET_CHANGE_INDEX;
    }
  }

  /**
   * @since 2.9
   */
  public ENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, short oldShortValue, short newShortValue)
  {
    super(eventType, oldShortValue, newShortValue);
    this.notifier = notifier;
    this.feature = feature;
  }

  @Override
  public Object getNotifier()
  {
    return notifier;
  }

  @Override
  public Object getFeature()
  {
    if (feature == null && featureID != NO_FEATURE_ID)
    {
      EClass eClass = notifier.eClass();
      feature = eClass.getEStructuralFeature(featureID);
    }
    return feature;
  }

  @Override
  public int getFeatureID(Class<?> expectedClass)
  {
    if (featureID == NO_FEATURE_ID && feature != null)
    {
      featureID = notifier.eDerivedStructuralFeatureID(feature.getFeatureID(), feature.getContainerClass());
    }
    return notifier.eBaseStructuralFeatureID(featureID, expectedClass);
  }

  @Override
  protected Object getFeatureDefaultValue()
  {
    Object feature = getFeature();
    if (feature instanceof EStructuralFeature)
    {
      return ((EStructuralFeature)feature).getDefaultValue();
    }
    return null;
  }

  @Override
  protected boolean isFeatureUnsettable()
  {
    Object feature = getFeature();
    if (feature instanceof EStructuralFeature)
    {
      return ((EStructuralFeature)feature).isUnsettable();
    }
    return false;
  }
}

