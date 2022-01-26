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
package org.eclipse.emf.common;


import org.eclipse.emf.common.util.DelegatingResourceLocator;
import org.eclipse.emf.common.util.Logger;
import org.eclipse.emf.common.util.ResourceLocator;


/**
 * EMF must run 
 * within an Eclipse workbench,
 * within a headless Eclipse workspace,
 * or just stand-alone as part of some other application.
 * To support this, all resource access (e.g., NL strings, images, and so on) is directed to the resource locator methods,
 * which can redirect the service as appropriate to the runtime.
 * During Eclipse invocation, the implementation delegates to a plugin implementation.
 * During stand-alone invocation, no plugin initialization takes place,
 * so the implementation delegates to a resource JAR on the CLASSPATH.
 * The resource jar will typically <b>not</b> be on the CLASSPATH during Eclipse invocation.
 * It will contain things like the icons and the .properties,  
 * which are available in a different way during Eclipse invocation.
 * @see DelegatingResourceLocator
 * @see ResourceLocator
 * @see Logger
 */
public abstract class EMFPlugin extends DelegatingResourceLocator implements ResourceLocator, Logger
{
  public static final boolean IS_ECLIPSE_RUNNING = false;
  public static final boolean IS_RESOURCES_BUNDLE_AVAILABLE = false;

  protected ResourceLocator [] delegateResourceLocators;

  public EMFPlugin(ResourceLocator [] delegateResourceLocators)
  {
    this.delegateResourceLocators = delegateResourceLocators;
  }

  /**
   * Returns an Eclipse plugin implementation of a resource locator.
   * @return an Eclipse plugin implementation of a resource locator.
   */
  public abstract ResourceLocator getPluginResourceLocator();
  
  @Override
  final protected ResourceLocator getPrimaryResourceLocator()
  {
    return getPluginResourceLocator();
  }
  
  @Override
  protected ResourceLocator[] getDelegateResourceLocators()
  {
    return delegateResourceLocators;
  }

  /**
   * Returns an Eclipse plugin implementation of a logger.
   * @return an Eclipse plugin implementation of a logger.
   */
  public Logger getPluginLogger()
  {
    return (Logger)getPluginResourceLocator();
  }

  public String getSymbolicName()
  {
    ResourceLocator resourceLocator = getPluginResourceLocator();
    if (resourceLocator instanceof InternalEclipsePlugin)
    {
      return ((InternalEclipsePlugin)resourceLocator).getSymbolicName();
    }
    else
    {
      String result = getClass().getName();
      return result.substring(0, result.lastIndexOf('.'));
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public void log(Object logEntry)
  {
    Logger logger = getPluginLogger();
    if (logger == null)
    {
      if (logEntry instanceof Throwable)
      {
        ((Throwable)logEntry).printStackTrace(System.err);
      }
      else
      {
        System.err.println(logEntry);
      }
    }
    else
    {
      logger.log(logEntry);
    }
  }

  /**
   * This just provides a common interface for the Eclipse plugins supported by EMF.
   * It is not considered API and should not be used by clients.
   */
  public static interface InternalEclipsePlugin
  {
    String getSymbolicName();
  }
}
