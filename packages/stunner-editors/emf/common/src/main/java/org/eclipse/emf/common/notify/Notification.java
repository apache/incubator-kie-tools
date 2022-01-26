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
package org.eclipse.emf.common.notify;


/**
 * A description of a feature change that has occurred for some notifier.
 * @see Adapter#notifyChanged
 * @see Notifier
 */
public interface Notification
{
  /**
   * Returns the object affected by the change.
   * @return the object affected by the change.
   */
  Object getNotifier();

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * the notifier has been created.
   * @see Notification#getEventType
   * @deprecated
   */
  @Deprecated
  int CREATE = 0;

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a feature of the notifier has been set.
   * This applies for simple features.
   * @see Notification#getEventType
   */
  int SET = 1;

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a feature of the notifier has been unset.
   * This applies for unsettable features.
   * @see Notification#getEventType
   */
  int UNSET = 2;

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a value has been inserted into a list-based feature of the notifier.
   * @see Notification#getEventType
   */
  int ADD = 3;

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a value has been removed from a list-based feature of the notifier.
   * @see Notification#getEventType
   */
  int REMOVE = 4;

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a several values have been added into a list-based feature of the notifier.
   * @see Notification#getEventType
   */
  int ADD_MANY = 5;

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a several values have been removed from a list-based feature of the notifier.
   * @see Notification#getEventType
   */
  int REMOVE_MANY = 6;

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a value has been moved within a list-based feature of the notifier.
   * @see Notification#getEventType
   */
  int MOVE = 7;    

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * an adapter is being removed from the notifier.
   * @see Notification#getEventType
   */
  int REMOVING_ADAPTER = 8; 

  /**
   * An {@link Notification#getEventType event type} indicating that 
   * a feature of the notifier has been resolved from a proxy.
   * @see Notification#getEventType
   */
  int RESOLVE = 9;

  /**
   * The number of built-in {@link Notification#getEventType event types}.
   * User defined event types should start from this value.
   * Clients are expected to ignore types they don't recognize.
   * @see Notification#getEventType
   */
  int EVENT_TYPE_COUNT = 10;

  /**
   * Returns the type of change that has occurred.
   * The valid types of events are defined by the constants in this class.
   * @return the type of change that has occurred.
   * @see Notifier
   */
  int getEventType();

  /**
   * An {@link Notification#getFeatureID ID} indicating that 
   * no feature ID information is applicable.
   * @see Notification#getFeatureID
   */
  int NO_FEATURE_ID = -1;

  /**
   * Returns the numeric ID of the feature relative to the given class, or {@link #NO_FEATURE_ID} when not applicable.
   * @param expectedClass the class to which the ID is relative.
   * @return the numeric ID of the feature.
   * @see #NO_FEATURE_ID
   */
  int getFeatureID(Class<?> expectedClass);

  /**
   * Returns the object representing the feature of the notifier that has changed.
   * @return the feature that has changed.
   */
  Object getFeature();

  /**
   * Returns the value of the notifier's feature before the change occurred.
   * For a list-based feature, this represents a value, or a list of values, removed from the list.
   * For a move, this represents the old position of the moved value.
   * @return the old value of the notifier's feature.
   */
  Object getOldValue();

  /**
   * Returns the value of the notifier's feature after the change occurred.
   * For a list-based feature, this represents a value, or a list of values, added to the list,
   * an array of <code>int</code> containing the original index of each value in the list of values removed from the list (except for the case of a clear),
   * the value moved within the list,
   * or null otherwise.
   * @return the new value of the notifier's feature.
   */
  Object getNewValue();

  /**
   * Returns whether the notifier's feature was considered set before the change occurred.
   * @return whether the notifier's feature was considered set before the change occurred.
   */
  boolean wasSet();

  /**
   * Returns true if this notification represents an event that did not change the state of the notifying object.
   * For the events {@link #ADD}, {@link #ADD_MANY}, {@link #REMOVE}, {@link #REMOVE_MANY}, {@link #MOVE}, 
   * it always returns false.
   * For the events {@link #RESOLVE} and {@link #REMOVING_ADAPTER} it always returns true.
   * For the events {@link #SET} and {@link #UNSET} it returns true if the old and the new value are equal;
   * In addition, for certain types of features there may be a distinction between 
   * being set to a default value and not being set at all, which implies that it has the default value.
   * In this situation, even in the case that the old and new values are equal, 
   * isTouch may never the less return false in order to indicate that, although the value has not changed,
   * the feature has gone from simply having a default value to being set to that same default value,
   * or has gone from being set to the default value back to being unset.
   * @return whether or not this is a state changing modification.
   */
  boolean isTouch();

  /**
   * Returns true if the notification's feature has been set to its default value.
   * @return whether or not this is a feature reset event.
   */
  boolean isReset();

  /**
   * An {@link Notification#getPosition index} indicating that 
   * no position information is applicable.
   * @see Notification#getPosition
   */
  int NO_INDEX = -1;

  /**
   * Returns the position within a list-based feature at which the change occurred.
   * It returns {@link #NO_INDEX} when not applicable.
   * @return the position at which the change occurred.
   */
  int getPosition();

  /**
   * Returns whether the notification can be and has been merged with this one.
   * @return whether the notification can be and has been merged with this one.
   */
  boolean merge(Notification notification);

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>boolean</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>boolean</code>.
   */
  boolean getOldBooleanValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>boolean</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>boolean</code>.
   */
  boolean getNewBooleanValue();

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>byte</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>byte</code>.
   */
  byte getOldByteValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>byte</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>byte</code>.
   */
  byte getNewByteValue();

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>char</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>char</code>.
   */
  char getOldCharValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>char</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>char</code>.
   */
  char getNewCharValue();

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>double</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>double</code>.
   */
  double getOldDoubleValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>double</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>double</code>.
   */
  double getNewDoubleValue();

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>float</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>float</code>.
   */
  float getOldFloatValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>float</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>float</code>.
   */
  float getNewFloatValue();

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>int</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>int</code>.
   */
  int getOldIntValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>int</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>int</code>.
   */
  int getNewIntValue();

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>long</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>long</code>.
   */
  long getOldLongValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>long</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>long</code>.
   */
  long getNewLongValue();

  /**
   * Returns the old value of the notifier's feature, if it is of type <code>short</code>. 
   * @return the old value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>short</code>.
   */
  short getOldShortValue();

  /**
   * Returns the new value of the notifier's feature, if it is of type <code>short</code>. 
   * @return the new value of the notifier's feature.
   * @exception IllegalStateException if the feature isn't <code>short</code>.
   */
  short getNewShortValue();

  /**
   * Returns the old value of the notifier's feature as a String.
   * @return the old value of the notifier's feature.
   */
  String getOldStringValue();

  /**
   * Returns the new value of the notifier's feature as a String.
   * @return the new value of the notifier's feature.
   */
  String getNewStringValue();
}
