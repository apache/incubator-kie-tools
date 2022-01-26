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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;


/**
 * An implementation of a content handler.
 */
public class ContentHandlerImpl implements ContentHandler
{
  /**
   * Creates a map with a single entry from {@link ContentHandler#VALIDITY_PROPERTY} to the given validity value.
   * @param validity the value of the validity property.
   * @return a map with a single entry from {@link ContentHandler#VALIDITY_PROPERTY} to the given validity value.
   */
  public static Map<String, Object> createContentDescription(Validity validity)
  {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put(VALIDITY_PROPERTY, validity);
    return result;
  }

  /**
   * Creates an instance.
   */
  public ContentHandlerImpl()
  {
    super();
  }

  /**
   * Returns the value of {@link ContentHandler#OPTION_REQUESTED_PROPERTIES} in the options map.
   * @param options the options in which to look up the property.
   * @return value of {@link ContentHandler#OPTION_REQUESTED_PROPERTIES} in the options map.
   */
  @SuppressWarnings("unchecked")
  protected Set<String> getRequestedProperties(Map<?, ?> options)
  {
    return (Set<String>)options.get(OPTION_REQUESTED_PROPERTIES);
  }

  /**
   * Returns whether the named property is one requested in the options.
   * @param property the property in question.
   * @param options the options in which to look for the requested property.
   * @return whether the named property is one requested in the options.
   * @see #getRequestedProperties(Map)
   */
  protected boolean isRequestedProperty(String property, Map<?, ?> options)
  {
    if (ContentHandler.VALIDITY_PROPERTY.equals(property) || ContentHandler.CONTENT_TYPE_PROPERTY.equals(property))
    {
      return  true;
    }
    else
    {
      Set<String> requestedProperties = getRequestedProperties(options);
      if (requestedProperties == null)
      {
        return true;
      }
      else
      {
        return requestedProperties.contains(property);
      }
    }
  }

  /**
   * This implementations always return true; clients are generally expected to override this.
   * @param uri the URI in questions.
   * @return true;
   */
  public boolean canHandle(URI uri)
  {
    return true;
  }

  /**
   * This base implementation handles looking up the {@link ContentHandler#BYTE_ORDER_MARK_PROPERTY} if that's a {@link #isRequestedProperty(String, Map) requested property}.
   */
  public Map<String, Object> contentDescription(URI uri, InputStream inputStream, Map<?, ?> options, Map<Object, Object> context) throws IOException
  {
    Map<String, Object> result = createContentDescription(ContentHandler.Validity.INDETERMINATE);
    if (isRequestedProperty(ContentHandler.BYTE_ORDER_MARK_PROPERTY, options))
    {
      result.put(ContentHandler.BYTE_ORDER_MARK_PROPERTY, getByteOrderMark(uri, inputStream, options, context));
    }
    return result;
  }

  /**
   * Returns the byte order marker at the start of the input stream.
   * @param uri the URI of the input stream.
   * @param inputStream the input stream to scan.
   * @param options any options to influence the behavior; this base implementation ignores this.
   * @param context the cache for fetching and storing a previous computation of the byte order marker; this base implementation caches {@link ContentHandler#BYTE_ORDER_MARK_PROPERTY}.
   * @return the byte order marker at the start of the input stream.
   * @throws IOException
   */
  protected ByteOrderMark getByteOrderMark(URI uri, InputStream inputStream, Map<?, ?> options, Map<Object, Object> context) throws IOException
  {
    ByteOrderMark result = (ByteOrderMark)context.get(ContentHandler.BYTE_ORDER_MARK_PROPERTY);
    if (result == null)
    {
      result = ByteOrderMark.read(inputStream);
      inputStream.reset();
      context.put(ContentHandler.BYTE_ORDER_MARK_PROPERTY, result);
    }
    return result;
  }
}
