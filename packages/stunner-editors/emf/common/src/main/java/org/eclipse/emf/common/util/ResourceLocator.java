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
package org.eclipse.emf.common.util;


/**
 * A locator of Java resources.
 */
public interface ResourceLocator
{
  /**
   * Returns the description that can be used to create the image resource associated with the key.
   * The description will typically be in the form of a URL to the image data.
   * Creation of an actual image depends on the GUI environment;
   * within Eclipse, org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry can be used.
   * @param key the key of the image resource.
   * @return the description on the image resource.
   */
  Object getImage(String key);

  /**
   * Returns the string resource associated with the key.
   * @param key the key of the string resource.
   * @return the string resource associated with the key.
   */
  String getString(String key);

  /**
   * Returns the string resource associated with the key.
   * @param key the key of the string resource.
   * @param translate whether the result is to be translated to the current locale.
   * @return the string resource associated with the key.
   */
  String getString(String key, boolean translate);

  /**
   * Returns a string resource associated with the key, and performs substitutions.
   * @param key the key of the string.
   * @param substitutions the message substitutions.
   * @return a string resource associated with the key.
   * @see #getString(String)
   * @see java.text.MessageFormat#format(String, Object[])
   */
  String getString(String key, Object [] substitutions);

  /**
   * Returns a string resource associated with the key, and performs substitutions.
   * @param key the key of the string.
   * @param substitutions the message substitutions.
   * @param translate whether the result is to be translated to the current locale.
   * @return a string resource associated with the key.
   * @see #getString(String)
   * @see java.text.MessageFormat#format(String, Object[])
   */
  String getString(String key, Object [] substitutions, boolean translate);
}
