/**
 * Copyright (c) 2010-2011 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.ecore.resource.impl;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

public class LocalURIHandlerImpl extends URIHandlerImpl
{
  protected Map<URI, byte[]> localBytes = new HashMap<URI, byte[]>();
  
  public LocalURIHandlerImpl()
  {
    super();
  }
  
  @Override
  public boolean canHandle(URI uri)
  {
    return "local".equals(uri.scheme());
  }

  @Override
  public OutputStream createOutputStream(final URI uri, Map<?, ?> options) throws IOException
  {
    return 
      new ByteArrayOutputStream()
      {
        @Override
        public void close() throws IOException
        {
          localBytes.put(uri, toByteArray());
        }
      };
  }

  @Override
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    byte[] bytes = localBytes.get(uri);
    if (bytes == null)
    {
      throw new IOException("Resource not found: " + uri);
    }
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    // TODO Auto-generated method stub
    super.delete(uri, options);
  }

  @Override
  public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException
  {
    // TODO Auto-generated method stub
    return super.contentDescription(uri, options);
  }

  @Override
  public boolean exists(URI uri, Map<?, ?> options)
  {
    // TODO Auto-generated method stub
    return super.exists(uri, options);
  }

  @Override
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    // TODO Auto-generated method stub
    return super.getAttributes(uri, options);
  }

  @Override
  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
    // TODO Auto-generated method stub
    super.setAttributes(uri, attributes, options);
  }
}
