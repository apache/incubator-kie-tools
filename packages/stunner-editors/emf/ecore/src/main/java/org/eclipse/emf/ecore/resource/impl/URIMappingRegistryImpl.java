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


import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.URI;


/**
 * An extensible implementation of a URI mapping registry.
 */
public class URIMappingRegistryImpl extends BasicEMap<URI, URI>
{
  private static final long serialVersionUID = 1L;

  /**
   * The implementation of the global mapping registry.
   * @see org.eclipse.emf.ecore.resource.URIConverter#URI_MAP
   */
  public static final URIMappingRegistryImpl INSTANCE = new URIMappingRegistryImpl();

  /**
   * A list of lists of prefix URIs; 
   * it is indexed by segment count to yield a list of prefixes of that length.
   */
  protected BasicEList<List<Entry<URI, URI>>> prefixMaps = new BasicEList<List<Entry<URI, URI>>>();

  /**
   * Creates an instance.
   */
  public URIMappingRegistryImpl()
  {
    super();
  }

  /**
   * Creates an {@link MappingEntryImpl}.
   */
  @Override
  protected Entry<URI, URI> newEntry(int hash, URI key, URI value)
  {
    validateKey(key);
    validateValue(value);
    return new MappingEntryImpl(hash, key, value);
  }

  /**
   * An extended implementation that maintains a bit 
   * indicating if the entry represents a folder to folder mapping.
   */
  protected class MappingEntryImpl extends EntryImpl
  {
    /**
     * The indicator whether this entry represents a folder to folder mapping.
     */
    public boolean isPrefixMapEntry;

    /**
     * Creates an instance.
     */
    public MappingEntryImpl(int hash, URI key, URI value)
    {
      super(hash, key, value);
      determineEntryType();
    }

    /**
     * Computes whether this entry represents a folder to folder mapping.
     */
    public void determineEntryType()
    {
      isPrefixMapEntry = (key).isPrefix() && (value).isPrefix();
    }
  }

  /**
   * Returns the remapped URI, or the URI itself.
   * This implementation uses the map to find an exact match.
   * Failing that, it matches the {@link #prefixMaps} prefixes in order.
   * And failing that, it delegates to {@link #delegatedGetURI(URI) delegatedGetURI}.
   * @param uri the URI to remap.
   * @return the remapped URI, or the URI itself.
   */
  public URI getURI(URI uri)
  {
    URI result = get(uri);
    if (result == null)
    {
      if (prefixMaps != null)
      {
        for (int i = Math.min(prefixMaps.size() - 1, uri.segmentCount()); i >= 0; --i)
        {
          List<Entry<URI, URI>> prefixes = prefixMaps.get(i);
          for (int j = prefixes.size() - 1; j >= 0; --j)
          {
            Entry<URI, URI> entry = prefixes.get(j);
            result = uri.replacePrefix(entry.getKey(), entry.getValue());

            if (result != null)
            {
              return result;
            }
          }
        }
      }

      result = delegatedGetURI(uri);
    }

    return result;
  }

  /**
   * Returns the mapped URI for the given URI, when standard alternatives fail.
   * <p>
   * This implementation returns <code>uri</code>.
   * </p>
   * @param uri the URI.
   * @return the mapped URI.
   * @see #getURI(URI)
   */
  protected URI delegatedGetURI(URI uri)
  {
    return uri;
  }

  /**
   * A map that is a {@link ExtensibleURIConverterImpl.URIMap}.
   */
  protected class URIMapImpl extends DelegatingMap implements ExtensibleURIConverterImpl.URIMap
  {
    /**
     * Creates an instance.
     */
    public URIMapImpl()
    {
      super();
    }

    /**
     * Returns the remapped URI, or the URI itself.
     * This implementation delegates to the containing {@link URIMappingRegistryImpl}.
     * @param uri the URI to remap.
     * @return the remapped URI, or the URI itself.
     */
    public URI getURI(URI uri)
    {
      return URIMappingRegistryImpl.this.getURI(uri);
    }
  }

  /** 
   * Returns a map view that implements {@link ExtensibleURIConverterImpl.URIMap}.
   */
  @Override
  public Map<URI, URI> map()
  {
    if (view == null)
    {
      view = new View<URI, URI>();
    }
    if (view.map == null)
    {
      view.map = new URIMapImpl();
    }

    return view.map;
  }

  /**
   * Validates that the key is a URI.
   */
  @Override
  protected void validateKey(URI key)
  {
    // Do nothing.
  }

  /**
   * Validates that the value is a URI.
   */
  @Override
  protected void validateValue(URI value)
  {
    // Do nothing.
  }

  /**
   * Checks for folder mappings to populate the {@link #prefixMaps prefix maps}.
   */
  @Override
  protected void didAdd(Entry<URI, URI> entry)
  {
    if (((MappingEntryImpl)entry).isPrefixMapEntry)
    {
      int length = entry.getKey().segmentCount();
      if (prefixMaps == null)
      {
        prefixMaps = new BasicEList<List<Entry<URI, URI>>>();
      }

      for (int i = prefixMaps.size() - 1; i <= length; ++i)
      {
        prefixMaps.add(new BasicEList<Entry<URI, URI>>());
      }

      prefixMaps.get(length).add(entry);
    }
  }

  /**
   * Checks for folder mappings to update the {@link #prefixMaps prefix maps}.
   */
  @Override
  protected void didModify(Entry<URI, URI> entry, URI oldValue)
  {
    didRemove(entry);
    ((MappingEntryImpl)entry).determineEntryType();
    didAdd(entry);
  }

  /**
   * Checks for folder mappings to cleanup the {@link #prefixMaps prefix maps}.
   */
  @Override
  protected void didRemove(Entry<URI, URI> entry)
  {
    if (((MappingEntryImpl)entry).isPrefixMapEntry)
    {
      int length = entry.getKey().segmentCount();
      prefixMaps.get(length).remove(entry);
    }
  }

  /**
   * Discards all the {@link #prefixMaps prefix maps}.
   */
  @Override
  protected void didClear(BasicEList<Entry<URI, URI>> [] oldEntryData)
  {
    prefixMaps = null;
  }
}
