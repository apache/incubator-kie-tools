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
package org.eclipse.emf.ecore.resource.impl;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;


/**
 * An extensible implementation of a resource factory registry.
 */
public class ResourceFactoryRegistryImpl implements Resource.Factory.Registry
{
  /**
   * The protocol map.
   */
  protected Map<String, Object> protocolToFactoryMap = new HashMap<String, Object>();

  /**
   * The extension map.
   */
  protected Map<String, Object> extensionToFactoryMap = new HashMap<String, Object>();

  /**
   * The content type identifier map.
   */
  protected Map<String, Object> contentTypeIdentifierToFactoryMap = new HashMap<String, Object>();

  /**
   * Creates an instance.
   */
  public ResourceFactoryRegistryImpl()
  {
    super();
  }

  /**
   * Returns the resource factory appropriate for the given URI.
   * <p>
   * This implementation does the {@link org.eclipse.emf.ecore.resource.Resource.Factory.Registry#getFactory(URI) typical} thing.
   * It will delegate to {@link #delegatedGetFactory(URI, String)}
   * in the case that the typical behaviour doesn't produce a result;
   * clients are encouraged to override that method only.
   * </p>
   * @param uri the URI.
   * @return the resource factory appropriate for the given URI.
   * @see org.eclipse.emf.ecore.resource.ResourceSet#createResource(URI)
   */
  public Resource.Factory getFactory(URI uri)
  {
    return convert(getFactory(uri, protocolToFactoryMap, extensionToFactoryMap, contentTypeIdentifierToFactoryMap, ContentHandler.UNSPECIFIED_CONTENT_TYPE, true));
  }

  /**
   * Returns the resource factory appropriate for the given URI.
   * <p>
   * This implementation does the {@link org.eclipse.emf.ecore.resource.Resource.Factory.Registry#getFactory(URI, String) typical} thing.
   * It will delegate to {@link #delegatedGetFactory(URI, String)}
   * in the case that the typical behaviour doesn't produce a result;
   * clients are encouraged to override that method only.
   * </p>
   * @param uri the URI.
   * @return the resource factory appropriate for the given URI.
   * @see org.eclipse.emf.ecore.resource.ResourceSet#createResource(URI)
   */
  public Resource.Factory getFactory(URI uri, String contentType)
  {
    return convert(getFactory(uri, protocolToFactoryMap, extensionToFactoryMap, contentTypeIdentifierToFactoryMap, contentType, true));
  }

  public static Resource.Factory convert(Object resourceFactory)
  {
    return
      resourceFactory instanceof Resource.Factory.Descriptor ?
        ((Resource.Factory.Descriptor)resourceFactory).createFactory() :
        (Resource.Factory)resourceFactory;
  }

  protected Object getFactory
    (URI uri,
     Map<String, Object> protocolToFactoryMap,
     Map<String, Object> extensionToFactoryMap,
     Map<String, Object> contentTypeIdentifierToFactoryMap,
     String contentTypeIdentifier,
     boolean delegate)
  {
    Object resourceFactory = null;
    if (!protocolToFactoryMap.isEmpty())
    {
      resourceFactory = protocolToFactoryMap.get(uri.scheme());
    }
    if (resourceFactory == null)
    {
      boolean extensionToFactoryMapIsEmpty = extensionToFactoryMap.isEmpty();
      if (!extensionToFactoryMapIsEmpty)
      {
        resourceFactory = extensionToFactoryMap.get(uri.fileExtension());
      }
      if (resourceFactory == null)
      {
        boolean contentTypeIdentifierToFactoryMapIsEmpty = contentTypeIdentifierToFactoryMap.isEmpty();
        if (!contentTypeIdentifierToFactoryMapIsEmpty)
        {
          if (ContentHandler.UNSPECIFIED_CONTENT_TYPE.equals(contentTypeIdentifier))
          {
            contentTypeIdentifier = getContentTypeIdentifier(uri);
          }
          if (contentTypeIdentifier != null)
          {
            resourceFactory = contentTypeIdentifierToFactoryMap.get(contentTypeIdentifier);
          }
        }
        if (resourceFactory == null)
        {
          if (!extensionToFactoryMapIsEmpty)
          {
            resourceFactory = extensionToFactoryMap.get(Resource.Factory.Registry.DEFAULT_EXTENSION);
          }
          if (resourceFactory == null)
          {
            if (!contentTypeIdentifierToFactoryMapIsEmpty)
            {
              resourceFactory = contentTypeIdentifierToFactoryMap.get(Resource.Factory.Registry.DEFAULT_CONTENT_TYPE_IDENTIFIER);
            }
            if (resourceFactory == null && delegate)
            {
              resourceFactory = delegatedGetFactory(uri, contentTypeIdentifier);
            }
          }
        }
      }
    }
    return resourceFactory;
  }

  protected String getContentTypeIdentifier(URI uri)
  {
    try
    {
      Map<String, ?> contentDescription = getURIConverter().contentDescription(uri, getContentDescriptionOptions());
      return (String)contentDescription.get(ContentHandler.CONTENT_TYPE_PROPERTY);
    }
    catch (IOException e)
    {
      return null;
    }
  }

  /**
   * Returns the URI converter that's used to {@link URIConverter#contentDescription(URI, Map) compute} the content type identifier.
   * @return the URI converter that's used to compute the content type identifier.
   */
  protected URIConverter getURIConverter()
  {
    return URIConverter.INSTANCE;
  }

  /**
   * A constant read only map of {@link URIConverter#contentDescription(URI, Map) options} used to request just the {@link ContentHandler#CONTENT_TYPE_PROPERTY content type}.
   */
  protected static final Map<?, ?> CONTENT_DESCRIPTION_OPTIONS;
  static
  {
    Map<Object, Object> contentDescriptionOptions = new HashMap<Object, Object>();
    Set<String> requestedProperties = new HashSet<String>();
    requestedProperties.add(ContentHandler.CONTENT_TYPE_PROPERTY);
    contentDescriptionOptions.put(ContentHandler.OPTION_REQUESTED_PROPERTIES, requestedProperties);
    CONTENT_DESCRIPTION_OPTIONS = Collections.unmodifiableMap(contentDescriptionOptions);
  }

  /**
   * Returns the default options used to {@link URIConverter#contentDescription(URI, Map) compute} the content type identifier.
   * @return the default options used to compute the content type identifier.
   */
  protected Map<?, ?> getContentDescriptionOptions()
  {
    return CONTENT_DESCRIPTION_OPTIONS;
  }

  /**
   * Returns the resource factory appropriate for the given URI and {@link ContentHandler#CONTENT_TYPE_PROPERTY content type identifier}, when standard alternatives fail.
   * <p>
   * This implementation calls {@link #delegatedGetFactory(URI)};
   * clients are encouraged to override it.
   * </p>
   * @param uri the URI.
   * @param contentTypeIdentifier the {@link ContentHandler#CONTENT_TYPE_PROPERTY content type identifier}.
   * @return the resource factory appropriate for the given URI and content type identifier.
   * @see #getFactory(URI)
   */
  protected Resource.Factory delegatedGetFactory(URI uri, String contentTypeIdentifier)
  {
    return delegatedGetFactory(uri);
  }

  /**
   * Returns the resource factory appropriate for the given URI, when standard alternatives fail.
   * <p>
   * This implementation returns <code>null</code>;
   * clients are encouraged to override {@link #delegatedGetFactory(URI, String)} instead.
   * </p>
   * @param uri the URI.
   * @return the resource factory appropriate for the given URI.
   * @see #getFactory(URI)
   * @deprecated since 2.4
   */
  @Deprecated
  protected Resource.Factory delegatedGetFactory(URI uri)
  {
    return null;
  }

  /*
   * Javadoc copied from interface.
   */
  public Map<String, Object> getExtensionToFactoryMap()
  {
    return extensionToFactoryMap;
  }

  /*
   * Javadoc copied from interface.
   */
  public Map<String, Object> getProtocolToFactoryMap()
  {
    return protocolToFactoryMap;
  }

  /*
   * Javadoc copied from interface.
   */
  public Map<String, Object> getContentTypeToFactoryMap()
  {
    return contentTypeIdentifierToFactoryMap;
  }
}
