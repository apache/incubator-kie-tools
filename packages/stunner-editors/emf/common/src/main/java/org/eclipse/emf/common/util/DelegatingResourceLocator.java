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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ResourceLocator;


/**
 * An abstract resource locator implementation
 * comprising a {@link #getPrimaryResourceLocator() primary locator}
 * and a series {@link #getDelegateResourceLocators() delegate locators}.
 */
public abstract class DelegatingResourceLocator implements ResourceLocator
{
  /**
   * A cache of the translated strings.
   */
  protected Map<String, String> strings = new HashMap<String, String>();

  /**
   * A cache of the untranslated strings.
   */
  protected Map<String, String> untranslatedStrings = new HashMap<String, String>();

  /**
   * A cache of the image descriptions.
   */
  protected Map<String, Object> images = new HashMap<String, Object>();

  /**
   * Whether to translate strings by default.
   */
  protected boolean shouldTranslate = true;

  /**
   * Creates an instance.
   */
  public DelegatingResourceLocator()
  {
    super();
  }

  /**
   * Returns the primary resource locator.
   * @return the primary resource locator.
   */
  protected abstract ResourceLocator getPrimaryResourceLocator();

  /**
   * Returns the delegate resource locators.
   * @return the delegate resource locators.
   */
  protected abstract ResourceLocator [] getDelegateResourceLocators();

  /*
   * Javadoc copied from interface.
   */
  public Object getImage(String key)
  {
    Object result = images.get(key);
    if (result == null)
    {
      ResourceLocator pluginResourceLocator = getPrimaryResourceLocator();
      if (pluginResourceLocator == null)
      {
        try
        {
          result = doGetImage(key);
        }
        catch (IOException exception)
        {
          result = delegatedGetImage(key);
        }
      }
      else
      {
        try
        {
          result = pluginResourceLocator.getImage(key);
        }
        catch (MissingResourceException exception)
        {
          result = delegatedGetImage(key);
        }
      }

      images.put(key, result);
    }

    return result;
  }

  /**
   * Does the work of fetching the image associated with the key.
   * It ensures that the image exists.
   * @param key the key of the image to fetch.
   * @exception IOException if an image doesn't exist.
   * @return the description of the image associated with the key.
   */
  protected Object doGetImage(String key) throws IOException
  {
    // TODO
    return null;
  }

  /**
   * Computes the file extension to be used with the key to specify an image resource.
   * @param key the key for the imagine.
   * @return the file extension to be used with the key to specify an image resource.
   */
  protected static String extensionFor(String key)
  {    String result = ".gif";
    int index = key.lastIndexOf('.');
    if (index != -1)
    {
      String extension = key.substring(index + 1);
      if ("png".equalsIgnoreCase(extension) || 
            "gif".equalsIgnoreCase(extension) ||
            "bmp".equalsIgnoreCase(extension) ||
            "ico".equalsIgnoreCase(extension) ||
            "jpg".equalsIgnoreCase(extension) ||
            "jpeg".equalsIgnoreCase(extension) ||
            "tif".equalsIgnoreCase(extension) ||
            "tiff".equalsIgnoreCase(extension))
      {
        result = "";
      }
    }
    return result;
  }

  /**
   * Does the work of fetching the image associated with the key,
   * when the image resource is not available locally.
   * @param key the key of the image to fetch.
   * @exception MissingResourceException if the image resource doesn't exist anywhere.
   * @see #getDelegateResourceLocators()
   */
  protected Object delegatedGetImage(String key) throws MissingResourceException
  {
    ResourceLocator[] delegateResourceLocators = getDelegateResourceLocators();
    for (int i = 0; i < delegateResourceLocators.length; ++i)
    {
      try
      {
        return delegateResourceLocators[i].getImage(key);
      }
      catch (MissingResourceException exception)
      {
        // Ignore the exception since we will throw one when all else fails.
      }
    }

    throw
      new MissingResourceException
        (CommonPlugin.INSTANCE.getString("_UI_ImageResourceNotFound_exception", new Object [] { key }),
         getClass().getName(),
         key);
  }

  /**
   * Indicates whether strings should be translated by default.
   *
   * @return <code>true</code> if strings should be translated by default; <code>false</code> otherwise.
   */
  public boolean shouldTranslate()
  {
    return shouldTranslate;
  }

  /**
   * Sets whether strings should be translated by default.
   *
   * @param shouldTranslate whether strings should be translated by default.
   */
  public void setShouldTranslate(boolean shouldTranslate)
  {
    this.shouldTranslate = shouldTranslate;
  }

  /*
   * Javadoc copied from interface.
   */
  public String getString(String key)
  {
    return getString(key, shouldTranslate());
  }

  /*
   * Javadoc copied from interface.
   */
  public String getString(String key, boolean translate)
  {
    Map<String, String> stringMap = translate ? strings : untranslatedStrings;
    String result = stringMap.get(key);
    if (result == null)
    {
      try
      {
        ResourceLocator pluginResourceLocator = getPrimaryResourceLocator();
        if (pluginResourceLocator == null)
        {
          result = doGetString(key, translate);
        }
        else
        {
          result = pluginResourceLocator.getString(key, translate);
        }
      }
      catch (MissingResourceException exception)
      {
        result = delegatedGetString(key, translate);
      }

      stringMap.put(key, result);
    }

    return result;
  }

  /**
   * Does the work of fetching the string associated with the key.
   * It ensures that the string exists.
   * @param key the key of the string to fetch.
   * @exception MissingResourceException if a string doesn't exist.
   * @return the string associated with the key.
   */
  protected String doGetString(String key, boolean translate) throws MissingResourceException
  {
    // TODO
    return key;
  }

  /**
   * Does the work of fetching the string associated with the key,
   * when the string resource is not available locally.
   * @param key the key of the string to fetch.
   * @exception MissingResourceException if the string resource doesn't exist anywhere.
   * @see #getDelegateResourceLocators()
   */
  protected String delegatedGetString(String key, boolean translate)
  {
    ResourceLocator[] delegateResourceLocators = getDelegateResourceLocators();
    for (int i = 0; i < delegateResourceLocators.length; ++i)
    {
      try
      {
        return delegateResourceLocators[i].getString(key, translate);
      }
      catch (MissingResourceException exception)
      {
        // Ignore this since we will throw an exception when all else fails.
      }
    }

    throw
      new MissingResourceException
        ("The string resource ''" + key + "'' could not be located",
         getClass().getName(),
         key);
  }

  /*
   * Javadoc copied from interface.
   */
  public String getString(String key, Object [] substitutions)
  {
    return getString(key, substitutions, shouldTranslate());
  }

  /*
   * Javadoc copied from interface.
   */
  public String getString(String key, Object [] substitutions, boolean translate)
  {
    // TODO
    // return MessageFormat.format(getString(key, translate), substitutions);
    return getString(key, translate);
  }
}
