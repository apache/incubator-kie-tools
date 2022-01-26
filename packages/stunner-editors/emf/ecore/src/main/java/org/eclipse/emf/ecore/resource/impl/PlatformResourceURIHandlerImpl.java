/**
 * Copyright (c) 2007-2011 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.resource.impl;


import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.URIConverter;


public class PlatformResourceURIHandlerImpl extends URIHandlerImpl
{
  /**
   * Creates an instance.
   */
  public PlatformResourceURIHandlerImpl()
  {
    super();
  }

  @Override
  public boolean canHandle(URI uri)
  {
    return uri.isPlatformResource();
  }

  /**
   * Creates an output stream for the platform resource path and returns it.
   * <p>
   * This implementation does one of two things, depending on the runtime environment.
   * If there is an Eclipse workspace, it delegates to
   * {@link WorkbenchHelper#createPlatformResourceOutputStream WorkbenchHelper.createPlatformResourceOutputStream},
   * which gives the expected Eclipse behaviour.
   * Otherwise, the {@link EcorePlugin#resolvePlatformResourcePath resolved} URI
   * is delegated to {@link #createOutputStream createOutputStream}
   * for recursive processing.
   * @return an open output stream.
   * @exception IOException if there is a problem obtaining an open output stream or a valid interpretation of the path.
   * @see EcorePlugin#resolvePlatformResourcePath(String)
   */
  @Override
  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    String platformResourcePath = uri.toPlatformString(true);
    URI resolvedLocation = EcorePlugin.resolvePlatformResourcePath(platformResourcePath);
    if (resolvedLocation != null)
    {
      return ((URIConverter)options.get(URIConverter.OPTION_URI_CONVERTER)).createOutputStream(resolvedLocation, options);
    }

    throw new IOException("The path '" + platformResourcePath + "' is unmapped");
  }

  /**
   * Creates an input stream for the platform resource path and returns it.
   * <p>
   * This implementation does one of two things, depending on the runtime environment.
   * If there is an Eclipse workspace, it delegates to
   * {@link WorkbenchHelper#createPlatformResourceInputStream WorkbenchHelper.createPlatformResourceInputStream},
   * which gives the expected Eclipse behaviour.
   * Otherwise, the {@link EcorePlugin#resolvePlatformResourcePath resolved} URI
   * is delegated to {@link #createInputStream createInputStream}
   * for recursive processing.
   * @return an open input stream.
   * @exception IOException if there is a problem obtaining an open input stream or a valid interpretation of the path.
   * @see EcorePlugin#resolvePlatformResourcePath(String)
   */
  @Override
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    String platformResourcePath = uri.toPlatformString(true);
    URI resolvedLocation = EcorePlugin.resolvePlatformResourcePath(platformResourcePath);
    if (resolvedLocation != null)
    {
      return getURIConverter(options).createInputStream(resolvedLocation, options);
    }

    throw new IOException("The path '" + platformResourcePath + "' is unmapped");
  }

  @Override
  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    String platformResourcePath = uri.toPlatformString(true);
    URI resolvedLocation = EcorePlugin.resolvePlatformResourcePath(platformResourcePath);
    if (resolvedLocation != null)
    {
      getURIConverter(options).delete(resolvedLocation, options);
    }
    else
    {
      throw new IOException("The path '" + platformResourcePath + "' is unmapped");
    }
  }

  @Override
  public boolean exists(URI uri, Map<?, ?> options)
  {
    String platformResourcePath = uri.toPlatformString(true);
    URI resolvedLocation = EcorePlugin.resolvePlatformResourcePath(platformResourcePath);
    return resolvedLocation != null && getURIConverter(options).exists(resolvedLocation, options);
  }

  @Override
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    String platformResourcePath = uri.toPlatformString(true);
    URI resolvedLocation = EcorePlugin.resolvePlatformResourcePath(platformResourcePath);
    return resolvedLocation == null ? Collections.<String, Object>emptyMap() : getURIConverter(options).getAttributes(resolvedLocation, options);
  }

  @Override
  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
    String platformResourcePath = uri.toPlatformString(true);
    URI resolvedLocation = EcorePlugin.resolvePlatformResourcePath(platformResourcePath);
    if (resolvedLocation != null)
    {
      getURIConverter(options).setAttributes(resolvedLocation, attributes, options);
    }
    else
    {
      throw new IOException("The platform resource path '" + platformResourcePath + "' does not resolve");
    }
  }
}
