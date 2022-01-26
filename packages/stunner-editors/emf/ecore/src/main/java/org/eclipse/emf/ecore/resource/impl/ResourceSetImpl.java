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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.NotifyingInternalEListImpl;


/**
 * An extensible resource set implementation.
 * <p>
 * The following configuration and control mechanisms are provided:
 * <ul>
 *   <li><b>Resolve</b></li>
 *   <ul>
 *     <li>{@link #delegatedGetResource(URI, boolean)}</li>
 *     <li>{@link #getEObject(URI, boolean)}</li>
 *   </ul>
 *   <li><b>Demand</b></li>
 *   <ul>
 *     <li>{@link #demandCreateResource(URI)}</li>
 *     <li>{@link #demandLoad(Resource)}</li>
 *     <li>{@link #demandLoadHelper(Resource)}</li>
 *   </ul>
 * </ul>
 * </p>
 */
public class ResourceSetImpl extends NotifierImpl implements ResourceSet
{
  /**
   * The contained resources.
   * @see #getResources
   */
  protected EList<Resource> resources;

  /**
   * The registered adapter factories.
   * @see #getAdapterFactories
   */
  protected EList<AdapterFactory> adapterFactories;

  /**
   * The load options.
   * @see #getLoadOptions
   */
  protected Map<Object, Object> loadOptions;

  /**
   * The local resource factory registry.
   * @see #getResourceFactoryRegistry
   */
  protected Resource.Factory.Registry resourceFactoryRegistry;

  /**
   * The URI converter.
   * @see #getURIConverter
   */
  protected URIConverter uriConverter;

  /**
   * The local package registry.
   * @see #getPackageRegistry
   */
  protected EPackage.Registry packageRegistry;

  /**
   * A map to cache the resource associated with a specific URI.
   * @see #setURIResourceMap(Map)
   */
  protected Map<URI, Resource> uriResourceMap;

  /**
   * Creates an empty instance.
   */
  public ResourceSetImpl()
  {
    super();
  }

  /**
   * Returns the map used to cache the resource {@link #getResource(URI, boolean) associated} with a specific URI.
   * @return the map used to cache the resource associated with a specific URI.
   * @see #setURIResourceMap
   */
  public Map<URI, Resource> getURIResourceMap()
  {
    return uriResourceMap;
  }

  /**
   * Sets the map used to cache the resource associated with a specific URI.
   * This cache is only activated if the map is not <code>null</code>.
   * The map will be lazily loaded by the {@link #getResource(URI, boolean) getResource} method.
   * It is up to the client to clear the cache when it becomes invalid,
   * e.g., when the URI of a previously mapped resource is changed.
   * @param uriResourceMap the new map or <code>null</code>.
   * @see #getURIResourceMap
   */
  public void setURIResourceMap(Map<URI, Resource> uriResourceMap)
  {
    this.uriResourceMap = uriResourceMap;
  }

  /*
   * Javadoc copied from interface.
   */
  public EList<Resource> getResources()
  {
    if (resources == null)
    {
      resources = new ResourcesEList<Resource>();
    }
    return resources;
  }

  /*
   * Javadoc copied from interface.
   */
  public TreeIterator<Notifier> getAllContents()
  {
    TreeIterator<Notifier> result = EcoreUtil.getAllContents(Collections.singleton(this));
    result.next();
    return result;
  }

  /*
   * Javadoc copied from interface.
   */
  public EList<AdapterFactory> getAdapterFactories()
  {
    if (adapterFactories == null)
    {
      adapterFactories =
        new BasicEList<AdapterFactory>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected boolean useEquals()
          {
            return false;
          }

          @Override
          protected boolean isUnique()
          {
            return true;
          }

          @Override
          protected Object [] newData(int capacity)
          {
            return new AdapterFactory [capacity];
          }
        };
    }
    return adapterFactories;
  }

  /*
   * Javadoc copied from interface.
   */
  public Map<Object, Object> getLoadOptions()
  {
    if (loadOptions == null)
    {
      loadOptions = new HashMap<Object, Object>();
    }

    return loadOptions;
  }

  /*
   * Javadoc copied from interface.
   */
  public EObject getEObject(URI uri, boolean loadOnDemand)
  {
    return getEObject(uri, loadOnDemand, null);
  }

  /*
   * Javadoc copied from interface.
   */
  public void getEObject(URI uri, Callback<EObject> callback)
  {
    getEObject(uri, true, callback);
  }

  /*
   * Javadoc copied from interface.
   */
  protected EObject getEObject(final URI uri, boolean loadOnDemand, final Callback<EObject> callback)
  {
    Resource resource = getResource
      (uri.trimFragment(), 
       loadOnDemand, 
       callback == null ?
         null :
         new Callback<Resource>()
         {
           public void onSuccess(Resource resource)
           {
             callback.onSuccess(resource.getEObject(uri.fragment()));
           }
            
           public void onFailure(Throwable caught)
           {
             callback.onFailure(caught);
           }
         });
    if (resource != null)
    {
      return resource.getEObject(uri.fragment());
    }
    else
    {
      return null;
    }
  }

  /**
   * Creates a new resource appropriate for the URI.
   * It is called by {@link #getResource(URI, boolean) getResource(URI, boolean)}
   * when a URI that doesn't exist as a resource is demand loaded.
   * This implementation simply calls {@link #createResource(URI, String) createResource(URI)}.
   * Clients may extend this as appropriate.
   * @param uri the URI of the resource to create.
   * @return a new resource.
   * @see #getResource(URI, boolean)
   */
  protected Resource demandCreateResource(URI uri)
  {
    return createResource(uri, ContentHandler.UNSPECIFIED_CONTENT_TYPE);
  }

  /**
   * Loads the given resource.
   * It is called by {@link #demandLoadHelper(Resource) demandLoadHelper(Resource)}
   * to perform a demand load.
   * This implementation simply calls <code>resource.</code>{@link Resource#load(Map) load}({@link #getLoadOptions() getLoadOptions}()).
   * Clients may extend this as appropriate.
   * @param resource  a resource that isn't loaded.
   * @exception IOException if there are serious problems loading the resource.
   * @see #getResource(URI, boolean)
   * @see #demandLoadHelper(Resource)
   */
  protected void demandLoad(Resource resource, Callback<Resource> callback) throws IOException
  {
    resource.load(getLoadOptions(), callback);
  }

  /**
   * Demand loads the given resource using {@link #demandLoad(Resource)}
   * and {@link WrappedException wraps} any {@link IOException} as a runtime exception.
   * It is called by {@link #getResource(URI, boolean) getResource(URI, boolean)}
   * to perform a demand load.
   * @param resource a resource that isn't loaded.
   * @see #demandLoad(Resource)
   */
  protected void demandLoadHelper(Resource resource, Callback<Resource> callback)
  {
    try
    {
      demandLoad(resource, callback);
    }
    catch (IOException exception)
    {
      handleDemandLoadException(resource, exception);
    }
  }

  /**
   * Handles the exception thrown during demand load
   * by recording it as an error diagnostic
   * and throwing a wrapping runtime exception.
   * @param resource the resource that threw an exception while loading.
   * @param exception the exception thrown from the resource while loading.
   * @see #demandLoadHelper(Resource)
   */
  protected void handleDemandLoadException(Resource resource, IOException exception) throws RuntimeException
  {
    final String location = resource.getURI() == null ? null : resource.getURI().toString();
    class DiagnosticWrappedException extends WrappedException implements Resource.Diagnostic
    {
      private static final long serialVersionUID = 1L;

      public DiagnosticWrappedException(Exception exception)
      {
        super(exception);
      }

      public String getLocation()
      {
        return location;
      }

      public int getColumn()
      {
        return 0;
      }

      public int getLine()
      {
        return 0;
      }
    }

    Exception cause = exception instanceof Resource.IOWrappedException ? (Exception)exception.getCause() : exception;
    DiagnosticWrappedException wrappedException = new DiagnosticWrappedException(cause);

    if (resource.getErrors().isEmpty())
    {
      resource.getErrors().add(exception instanceof Resource.Diagnostic ? (Resource.Diagnostic)exception : wrappedException);
    }

    throw wrappedException;
  }

  /**
   * Returns a resolved resource available outside of the resource set.
   * It is called by {@link #getResource(URI, boolean) getResource(URI, boolean)}
   * after it has determined that the URI cannot be resolved
   * based on the existing contents of the resource set.
   * This implementation looks up the URI in the {#getPackageRegistry() local} package registry.
   * Clients may extend this as appropriate.
   * @param uri the URI
   * @param loadOnDemand whether demand loading is required.
   */
  protected Resource delegatedGetResource(URI uri, boolean loadOnDemand, Callback<Resource> callback)
  {
    EPackage ePackage = getPackageRegistry().getEPackage(uri.toString());
    return ePackage == null ? null : ePackage.eResource();
  }

  /*
   * Javadoc copied from interface.
   */
  public Resource getResource(URI uri, boolean loadOnDemand)
  {
    return getResource(uri, loadOnDemand, null);
    
  }

  /*
   * Javadoc copied from interface.
   */
  public Resource getResource(URI uri, Callback<Resource> callback)
  {
    return getResource(uri, true, callback);
  }

  protected Resource getResource(URI uri, boolean loadOnDemand, Callback<Resource> callback)
  {
    Map<URI, Resource> map = getURIResourceMap();
    if (map != null)
    {
      Resource resource = map.get(uri);
      if (resource != null)
      {
        if (loadOnDemand && !resource.isLoaded())
        {
          demandLoadHelper(resource, callback);
        }
        else if (callback != null)
        {
          callback.onSuccess(resource);
        }
        return resource;
      }
    }

    URIConverter theURIConverter = getURIConverter();
    URI normalizedURI = theURIConverter.normalize(uri);
    for (Resource resource : getResources())
    {
      if (theURIConverter.normalize(resource.getURI()).equals(normalizedURI))
      {
        if (map != null)
        {
          map.put(uri, resource);
        }
        if (loadOnDemand && !resource.isLoaded())
        {
          demandLoadHelper(resource, callback);
        }
        else if (callback != null)
        {
          callback.onSuccess(resource);
        }
        return resource;
      }
    }

    Resource delegatedResource = delegatedGetResource(uri, loadOnDemand, callback);
    if (delegatedResource != null)
    {
      if (map != null)
      {
        map.put(uri, delegatedResource);
      }
      if (callback != null && delegatedResource.isLoaded())
      {
        callback.onSuccess(delegatedResource);
      }
      return delegatedResource;
    }

    if (loadOnDemand)
    {
      Resource resource = demandCreateResource(uri);
      if (resource == null)
      {
        throw new RuntimeException("Cannot create a resource for '" + uri + "'; a registered resource factory is needed");
      }

      demandLoadHelper(resource, callback);

      if (map != null)
      {
        map.put(uri, resource);
      }
      return resource;
    }

    return null;
  }

  /*
   * Javadoc copied from interface.
   */
  public Resource createResource(URI uri)
  {
    return createResource(uri, null);
  }

  /*
   * Javadoc copied from interface.
   */
  public Resource createResource(URI uri, String contentType)
  {
    Resource.Factory resourceFactory = getResourceFactoryRegistry().getFactory(uri, contentType);
    if (resourceFactory != null)
    {
      Resource result = resourceFactory.createResource(uri);
      getResources().add(result);
      return result;
    }
    else
    {
      return null;
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public Resource.Factory.Registry getResourceFactoryRegistry()
  {
    if (resourceFactoryRegistry == null)
    {
      resourceFactoryRegistry =
        new ResourceFactoryRegistryImpl()
        {
          @Override
          protected Resource.Factory delegatedGetFactory(URI uri, String contentTypeIdentifier)
          {
            return
              convert
                (getFactory
                  (uri,
                   Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap(),
                   Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap(),
                   Resource.Factory.Registry.INSTANCE.getContentTypeToFactoryMap(),
                   contentTypeIdentifier,
                   false));
          }

          @Override
          protected URIConverter getURIConverter()
          {
            return ResourceSetImpl.this.getURIConverter();
          }

          @Override
          protected Map<?, ?> getContentDescriptionOptions()
          {
            return getLoadOptions();
          }
        };
    }
    return resourceFactoryRegistry;
  }

  /*
   * Javadoc copied from interface.
   */
  public void setResourceFactoryRegistry(Resource.Factory.Registry resourceFactoryRegistry)
  {
    this.resourceFactoryRegistry = resourceFactoryRegistry;
  }

  /*
   * Javadoc copied from interface.
   */
  public URIConverter getURIConverter()
  {
    if (uriConverter == null)
    {
      uriConverter = new ExtensibleURIConverterImpl();
    }
    return uriConverter;
  }

  /*
   * Javadoc copied from interface.
   */
  public void setURIConverter(URIConverter uriConverter)
  {
    this.uriConverter = uriConverter;
  }

  /*
   * Javadoc copied from interface.
   */
  public EPackage.Registry getPackageRegistry()
  {
    if (packageRegistry == null)
    {
      packageRegistry = new EPackageRegistryImpl(EPackage.Registry.INSTANCE);
    }
    return packageRegistry;
  }

  /*
   * Javadoc copied from interface.
   */
  public void setPackageRegistry(EPackage.Registry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }


  /**
   * A notifying list implementation for supporting {@link ResourceSet#getResources}.
   */
  protected class ResourcesEList<E extends Object & Resource> extends NotifyingInternalEListImpl<E> implements InternalEList<E>
  {
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean isNotificationRequired()
    {
      return ResourceSetImpl.this.eNotificationRequired();
    }

    @Override
    protected Object [] newData(int capacity)
    {
      return new Resource [capacity];
    }

    @Override
    public Object getNotifier()
    {
      return ResourceSetImpl.this;
    }

    @Override
    public int getFeatureID()
    {
      return RESOURCE_SET__RESOURCES;
    }

    @Override
    protected boolean useEquals()
    {
      return false;
    }

    @Override
    protected boolean hasInverse()
    {
      return true;
    }

    @Override
    protected boolean isUnique()
    {
      return true;
    }

    @Override
    protected NotificationChain inverseAdd(E object, NotificationChain notifications)
    {
      Resource.Internal resource = (Resource.Internal)object;
      return resource.basicSetResourceSet(ResourceSetImpl.this, notifications);
    }

    @Override
    protected NotificationChain inverseRemove(E object, NotificationChain notifications)
    {
      Resource.Internal resource = (Resource.Internal)object;
      Map<URI, Resource> map = getURIResourceMap();
      if (map != null)
      {
        for (Iterator<Resource> i = map.values().iterator(); i.hasNext();)
        {
          if (resource == i.next())
          {
            i.remove();
          }
        }
      }
      return resource.basicSetResourceSet(null, notifications);
    }

    @Override
    public boolean contains(Object object)
    {
      return size <= 4 ? super.contains(object) : object instanceof Resource && ((Resource)object).getResourceSet() == ResourceSetImpl.this;
    }
  }

  /**
   * Returns a standard label with the list of resources.
   * @return the string form.
   */
  @Override
  public String toString()
  {
    return
      getClass().getName() +  '@' + Integer.toHexString(hashCode()) +
        " resources=" + (resources == null ? "[]" : resources.toString());
  }
}
