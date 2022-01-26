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
package org.eclipse.emf.ecore.resource.impl;


import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;


/**
 * A highly functional and extensible URI converter implementation.
 * <p>
 * This implementation provides seamless transparent Eclipse integration
 * by supporting the <code>platform:/resource</code> mechanism both inside of Eclipse and outside of Eclipse.
 * Furthermore, although the implementation imports
 * both {@link org.eclipse.core.runtime} and {@link org.eclipse.core.resources},
 * and hence requires the Eclipse libraries at development time,
 * the implementation does <b>not</b> require them at runtime.
 * Clients of this implementation must be cautious if they wish to maintain this platform neutral behaviour.
 * </p>
 */
public class ExtensibleURIConverterImpl implements URIConverter
{
  /**
   * A map that remaps URIs.
   */
  public interface URIMap extends Map<URI, URI>
  {
    /**
     * Returns the remapped URI, or the URI itself.
     * @param uri the URI to remap.
     * @return the remapped URI, or the URI itself.
     */
    URI getURI(URI uri);
  }
 
  protected static class URIHandlerList extends BasicEList<URIHandler>
  {
    private static final long serialVersionUID = 1L;

    public URIHandlerList()
    {
      super();
    }

    @Override
    protected boolean canContainNull()
    {
      return false;
    }

    @Override
    protected Object [] newData(int capacity)
    {
      return new URIHandler [capacity];
    }

    @Override
    public URIHandler [] data()
    {
      return (URIHandler[])data;
    }
  }
 
  protected URIHandlerList uriHandlers;
 
  protected static class ContentHandlerList extends BasicEList<ContentHandler>
  {
    private static final long serialVersionUID = 1L;

    public ContentHandlerList()
    {
      super();
    }

    @Override
    protected boolean canContainNull()
    {
      return false;
    }

    @Override
    protected Object [] newData(int capacity)
    {
      return new ContentHandler [capacity];
    }

    @Override
    public ContentHandler [] data()
    {
      return (ContentHandler[])data;
    }
  }
 
  protected ContentHandlerList contentHandlers;

  /**
   * The URI map.
   */
  protected URIMap uriMap;

  /**
   * Creates an instance.
   */
  public ExtensibleURIConverterImpl()
  {
    this(URIHandler.DEFAULT_HANDLERS, ContentHandler.Registry.INSTANCE.contentHandlers());
  }

  /**
   * Creates an instance.
   */
  public ExtensibleURIConverterImpl(Collection<URIHandler> uriHandlers, Collection<ContentHandler> contentHandlers)
  {
    getURIHandlers().addAll(uriHandlers);
    getContentHandlers().addAll(contentHandlers);
  }

  public EList<URIHandler> getURIHandlers()
  {
    if (uriHandlers == null)
    {
      uriHandlers = new URIHandlerList();
    }
    return uriHandlers;
  }

  public URIHandler getURIHandler(URI uri)
  {
    int size = uriHandlers.size();
    if (size > 0)
    {
      URIHandler[] data = uriHandlers.data();
      for (int i = 0; i < size; ++i)
      {
        URIHandler uriHandler = data[i];
        if (uriHandler.canHandle(uri))
        {
          return uriHandler;
        }
      }
    }
    throw new RuntimeException("There is no URIHandler to handle " + uri);
  }

  public EList<ContentHandler> getContentHandlers()
  {
    if (contentHandlers == null)
    {
      contentHandlers = new ContentHandlerList();
    }
    return contentHandlers;
  }

  public OutputStream createOutputStream(URI uri) throws IOException
  {
    return createOutputStream(uri, null);
  }

  public void store(URI uri, byte[] bytes, Map<?, ?> options, Callback<Map<?, ?>> callback)
  {
    URI normalizedURI = normalize(uri);
    getURIHandler(normalizedURI).store(normalizedURI, bytes, new OptionsMap(OPTION_URI_CONVERTER, this, options), callback);
  }

  public void delete(URI uri, Map<?, ?> options, Callback<Map<?, ?>> callback)
  {
    URI normalizedURI = normalize(uri);
    getURIHandler(normalizedURI).delete(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options), callback);
  }

  static class OptionsMap implements Map<Object, Object>
  {
    protected Object key;
    protected Object value;
    protected Map<?, ?> options;
    protected Map<Object, Object> mergedMap;

    public OptionsMap(Object key, Object value, Map<?, ?> options)
    {
      this.options = options == null ? Collections.EMPTY_MAP : options;
      this.key = key;
      this.value = value;
    }

    protected Map<Object, Object> mergedMap()
    {
      if (mergedMap == null)
      {
        mergedMap = new LinkedHashMap<Object, Object>(options);
        mergedMap.put(key, value);
      }
      return mergedMap;
    }

    public void clear()
    {
      throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key)
    {
      return this.key == key || this.key.equals(key) || options.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
      return this.value == value || options.containsValue(value);
    }

    public Set<Map.Entry<Object, Object>> entrySet()
    {
      return mergedMap().entrySet();
    }

    public Object get(Object key)
    {
      return this.key == key || this.key.equals(key) ? value : options.get(key);
    }

    public boolean isEmpty()
    {
      return false;
    }

    public Set<Object> keySet()
    {
      return mergedMap().keySet();
    }

    public Object put(Object key, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends Object, ? extends Object> t)
    {
      throw new UnsupportedOperationException();
    }

    public Object remove(Object key)
    {
      throw new UnsupportedOperationException();
    }

    public int size()
    {
      return mergedMap().size();
    }

    public Collection<Object> values()
    {
      return mergedMap().values();
    }

    @Override
    public int hashCode()
    {
      return mergedMap().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
      return mergedMap().equals(o);
    }
  }

  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    URI normalizedURI = normalize(uri);
    return getURIHandler(normalizedURI).createOutputStream(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options));
  }

  public InputStream createInputStream(URI uri) throws IOException
  {
    return createInputStream(uri, null);
  }

  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    URI normalizedURI = normalize(uri);
    return getURIHandler(normalizedURI).createInputStream(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options));
  }

  public void createInputStream(URI uri, Map<?, ?> options, Callback<Map<?, ?>> callback)
  {
    URI normalizedURI = normalize(uri);
    getURIHandler(normalizedURI).createInputStream(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options), callback);
  }

  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    URI normalizedURI = normalize(uri);
    getURIHandler(normalizedURI).delete(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options));
  }

  public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException
  {
    URI normalizedURI = normalize(uri);
    return getURIHandler(normalizedURI).contentDescription(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options));
  }

  public boolean exists(URI uri, Map<?, ?> options)
  {
    URI normalizedURI = normalize(uri);
    return getURIHandler(normalizedURI).exists(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options));
  }

  public void exists(URI uri, Map<?, ?> options, Callback<Boolean> callback)
  {
    URI normalizedURI = normalize(uri);
    getURIHandler(normalizedURI).exists(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options), callback);
  }

  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    URI normalizedURI = normalize(uri);
    return getURIHandler(normalizedURI).getAttributes(normalizedURI, new OptionsMap(OPTION_URI_CONVERTER, this, options));
  }

  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
    URI normalizedURI = normalize(uri);
    getURIHandler(normalizedURI).setAttributes(normalizedURI, attributes, new OptionsMap(OPTION_URI_CONVERTER, this, options));
  }

  /**
   * Returns the normalized form of the URI.
   * <p>
   * This implementation does precisely and only the {@link URIConverter#normalize typical} thing.
   * It calls itself recursively so that mapped chains are followed.
   * </p>
   * @param uri the URI to normalize.
   * @return the normalized form.
   * @see org.eclipse.emf.ecore.plugin.EcorePlugin#getPlatformResourceMap
   */
  public URI normalize(URI uri)
  {
    String fragment = uri.fragment();
    String query = uri.query();
    URI result = getInternalURIMap().getURI(uri.trimFragment().trimQuery());
    String scheme = result.scheme();
    if (scheme == null)
    {
      if (result.hasAbsolutePath())
      {
        result = URI.createURI("file:" + result);
      }
      else
      {
        result = URI.createFileURI(result.toString());
      }
    }
    if (fragment != null)
    {
      result = result.appendFragment(fragment);
    }
    if (query != null)
    {
      result = result.appendQuery(query);
    }

    if (result.equals(uri))
    {
      return uri;
    }
    else
    {
      return normalize(result);
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public Map<URI, URI> getURIMap()
  {
    return getInternalURIMap();
  }

  /**
   * Returns the internal version of the URI map.
   * @return the internal version of the URI map.
   */
  protected URIMap getInternalURIMap()
  {
    if (uriMap == null)
    {
      URIMappingRegistryImpl mappingRegistryImpl =
        new URIMappingRegistryImpl()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected URI delegatedGetURI(URI uri)
          {
            return URIMappingRegistryImpl.INSTANCE.getURI(uri);
          }
        };

      uriMap = (URIMap)mappingRegistryImpl.map();
    }

    return uriMap;
  }
}
