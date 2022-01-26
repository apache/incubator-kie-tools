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
package org.eclipse.emf.common.notify;


/**
 * A notification that wraps another notification.
 * All the {@link Notification} methods are delegated to the wrapped notification.
 */
public class NotificationWrapper implements Notification
{
  /**
   * The notification that is being wrapped.
   */
  protected Notification notification;

  /**
   * An optional override value for the notification's notifier.
   */
  protected Object notifier;

  /**
   * Create an instance with the specified notification.
   */
  public NotificationWrapper(Notification notification)
  {
    this.notification = notification;
  }
  
  /**
   * Create an instance with the specified notifier and notification.
   */
  public NotificationWrapper(Object notifier, Notification notification)
  {
    this.notifier = notifier;
    this.notification = notification;
  }
  
  /**
   * Returns the local notifier if set; otherwise delegates to the getNotifier method of the notification.
   * @return the notifier.
   */
  public Object getNotifier()
  {
    return notifier == null ? notification.getNotifier() : notifier;
  }

  /**
   * Delegates to the getEventType method of the notification.
   * @return the eventType.
   */
  public int getEventType()
  {
    return notification.getEventType();
  }

  /**
   * Delegates to the getFeatureID method of the notification.
   * @return the featureID.
   */
  public int getFeatureID(Class<?> expectedClass)
  {
    return notification.getFeatureID(expectedClass);
  }

  /**
   * Delegates to the getFeature method of the notification.
   * @return the feature.
   */
  public Object getFeature()
  {
    return notification.getFeature();
  }

  /**
   * Delegates to the getOldValue method of the notification.
   * @return the oldValue.
   */
  public Object getOldValue()
  {
    return notification.getOldValue();
  }

  /**
   * Delegates to the getNewValue method of the notification.
   * @return the newValue.
   */
  public Object getNewValue()
  {
    return notification.getNewValue();
  }

  /**
   * Delegates to the wasSet method of the notification.
   * @return the wasSet result.
   */
  public boolean wasSet()
  {
    return notification.wasSet();
  }

  /**
   * Delegates to the isTouch method of the notification.
   * @return the isTouch result.
   */
  public boolean isTouch()
  {
    return notification.isTouch();
  }

  /**
   * Delegates to the isReset method of the notification.
   * @return the isReset result.
   */
  public boolean isReset()
  {
    return notification.isReset();
  }

  /**
   * Delegates to the getPosition method of the notification.
   * @return the position.
   */
  public int getPosition()
  {
    return notification.getPosition();
  }

  /**
   * Delegates to the merge method of the notification.
   * @return the merge result.
   */
  public boolean merge(Notification notification)
  {
    return false;
  }

  /**
   * Delegates to the getOldBooleanValue method of the notification.
   * @return the oldBooleanValue.
   */
  public boolean getOldBooleanValue()
  {
    return notification.getOldBooleanValue();
  }

  /**
   * Delegates to the getNewBooleanValue method of the notification.
   * @return the newBooleanValue.
   */
  public boolean getNewBooleanValue()
  {
    return notification.getNewBooleanValue();
  }

  /**
   * Delegates to the getOldByteValue method of the notification.
   * @return the oldByteValue.
   */
  public byte getOldByteValue()
  {
    return notification.getOldByteValue();
  }

  /**
   * Delegates to the getNewByteValue method of the notification.
   * @return the newByteValue.
   */
  public byte getNewByteValue()
  {
    return notification.getNewByteValue();
  }

  /**
   * Delegates to the getOldCharValue method of the notification.
   * @return the oldCharValue.
   */
  public char getOldCharValue()
  {
    return notification.getOldCharValue();
  }

  /**
   * Delegates to the getNewCharValue method of the notification.
   * @return the newCharValue.
   */
  public char getNewCharValue()
  {
    return notification.getNewCharValue();
  }

  /**
   * Delegates to the getOldDoubleValue method of the notification.
   * @return the oldDoubleValue.
   */
  public double getOldDoubleValue()
  {
    return notification.getOldDoubleValue();
  }

  /**
   * Delegates to the getNewDoubleValue method of the notification.
   * @return the newDoubleValue.
   */
  public double getNewDoubleValue()
  {
    return notification.getNewDoubleValue();
  }

  /**
   * Delegates to the getOldFloatValue method of the notification.
   * @return the oldFloatValue.
   */
  public float getOldFloatValue()
  {
    return notification.getOldFloatValue();
  }

  /**
   * Delegates to the getNewFloatValue method of the notification.
   * @return the newFloatValue.
   */
  public float getNewFloatValue()
  {
    return notification.getNewFloatValue();
  }

  /**
   * Delegates to the getOldIntValue method of the notification.
   * @return the oldIntValue.
   */
  public int getOldIntValue()
  {
    return notification.getOldIntValue();
  }

  /**
   * Delegates to the getNewIntValue method of the notification.
   * @return the newIntValue.
   */
  public int getNewIntValue()
  {
    return notification.getNewIntValue();
  }

  /**
   * Delegates to the getOldLongValue method of the notification.
   * @return the oldLongValue.
   */
  public long getOldLongValue()
  {
    return notification.getOldLongValue();
  }

  /**
   * Delegates to the getNewLongValue method of the notification.
   * @return the newLongValue.
   */
  public long getNewLongValue()
  {
    return notification.getNewLongValue();
  }

  /**
   * Delegates to the getOldShortValue method of the notification.
   * @return the oldShortValue.
   */
  public short getOldShortValue()
  {
    return notification.getOldShortValue();
  }

  /**
   * Delegates to the getNewShortValue method of the notification.
   * @return the newShortValue.
   */
  public short getNewShortValue()
  {
    return notification.getNewShortValue();
  }

  /**
   * Delegates to the getOldStringValue method of the notification.
   * @return the oldStringValue.
   */
  public String getOldStringValue()
  {
    return notification.getOldStringValue();
  }

  /**
   * Delegates to the getNewStringValue method of the notification.
   * @return the newStringValue.
   */
  public String getNewStringValue()
  {
    return notification.getNewStringValue();
  }
}
