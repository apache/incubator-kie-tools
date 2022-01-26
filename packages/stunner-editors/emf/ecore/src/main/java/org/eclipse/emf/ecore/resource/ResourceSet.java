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
package org.eclipse.emf.ecore.resource;


import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;


/**
 * A collection of related persistent documents.
 * <p>
 * A resource set manages a collection of related {@link #getResources resources}
 * and produces notification for changes to that collection.
 * It provides a {@link #getAllContents tree} of contents.
 * A collection of {@link #getAdapterFactories adapter factories} 
 * supports {@link org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(EObject, Object) adapter lookup} 
 * via registered adapter factory.
 * </p>
 * <p>
 * A resource can be {@link #createResource(URI) created} 
 * or {@link #getResource(URI, boolean) demand loaded}
 * into the collection.
 * The {@link #getResourceFactoryRegistry registry} of resource factories can be configured
 * to create resources of the appropriate type.
 * A proxy can be {@link #getEObject resolved} by the resource set, 
 * and may cause the demand load of a resource.
 * Default {@link #getLoadOptions load options} are used during demand load.
 * A {@link #getURIConverter URI converter} can be configured to 
 * normalize URIs for comparison and to monitor access to the backing store.
 * Clients must extend the default {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl implementation},
 * since methods can and will be added to this API.
 * </p>
 * @see Resource
 * @see Resource.Factory
 * @see URIConverter
 * @see org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(EObject, Object)
 * @see org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(Resource, Object)
 */
public interface ResourceSet extends Notifier
{
  /**
   * The {@link #getResources} feature {@link org.eclipse.emf.common.notify.Notification#getFeatureID ID}.
   */
  int RESOURCE_SET__RESOURCES = 0;

  /**
   * Returns the direct {@link Resource}s being managed.
   * <p>
   * A resource added to this list 
   * will be {@link Resource#getResourceSet contained} by this resource set.
   * If it was previously contained by a resource set, it will have been removed.
   * </p>
   * @return the resources.
   * @see Resource#getResourceSet
   */
  EList<Resource> getResources();

  /**
   * Returns a tree iterator that iterates over all the {@link #getResources direct resources} 
   * and over the content {@link Resource#getAllContents tree} of each. 
   * @return a tree iterator that iterates over all contents.
   * @see EObject#eAllContents
   * @see Resource#getAllContents
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getAllContents(ResourceSet, boolean)
   */
  TreeIterator<Notifier> getAllContents();

  /**
   * Returns the list of registered {@link org.eclipse.emf.common.notify.AdapterFactory} instances.
   * <p>
   * One style of adapter {@link org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(EObject, Object) lookup} supported by EMF 
   * is via registered adapter factories.
   * Since these factories are accessible to any fully contained object via
   *<pre>
   *  eObject.eResource().getResourceSet().getAdapterFactories()
   *</pre>
   * they can be used to create adapters on demand, 
   * without going to the factory first.
   * </p>
   * @return the list of adapter factories.
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(EObject, Object)
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(Resource, Object)
   * @see org.eclipse.emf.common.notify.AdapterFactory#adapt(Notifier, Object)
   */
  EList<AdapterFactory> getAdapterFactories();

  /**
   * Returns the options used during demand load.
   * <p>
   * Options are handled generically as feature-to-setting entries.
   * They are passed to the resource when it is {@link Resource#load(Map) deserialized}.
   * A resource will ignore options it doesn't recognize.
   * The options could even include things like an Eclipse progress monitor...
   * </p>
   * @return the options used during demand load.
   * @see Resource#load(Map)
   */
  Map<Object, Object> getLoadOptions();

  /**
   * Returns the object resolved by the URI.
   * <p>
   * Every object {@link EObject#eResource contained} by a resource (or that is a {@link EObject#eIsProxy proxy})
   * has a {@link org.eclipse.emf.ecore.util.EcoreUtil#getURI corresponding URI} 
   * that resolves to the object.
   * So for any object contained by a resource, the following is <code>true</code>.
   *<pre>
   *   eObject == eObject.eResource().getResourceSet().getEObject(EcoreUtil.getURI(eObject), false)
   *</pre>
   * </p>
   * <p>
   * The URI {@link URI#trimFragment without} the fragment,
   * is used to {@link #getResource resolve} a resource.
   * If the resource resolves,
   * the {@link URI#fragment fragment} is used to {@link Resource#getEObject resolve} the object.
   * </p>
   * @param uri the URI to resolve.
   * @param loadOnDemand whether to create and load the resource, if it doesn't already exists.
   * @return the object resolved by the URI, or <code>null</code> if there isn't one.
   * @see Resource#getEObject(String)
   * @see #getResource(URI, boolean)
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getURI(EObject)
   * @throws RuntimeException if a resource can't be demand created.
   * @throws org.eclipse.emf.common.util.WrappedException if a problem occurs during demand load.
   */
  EObject getEObject(URI uri, boolean loadOnDemand);

  /**
   * TODO
   * @param uri
   * @param callback
   */
  void getEObject(URI uri, Callback<EObject> callback);

  /**
   * Returns the resource resolved by the URI.
   * <p>
   * A resource set is expected to implement the following strategy 
   * in order to resolve the given URI to a resource.
   * First it uses it's {@link #getURIConverter URI converter} to {@link URIConverter#normalize normalize} the URI 
   * and then to compare it with the normalized URI of each resource;
   * if it finds a match, 
   * that resource becomes the result.
   * Failing that,
   * it {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#delegatedGetResource delegates} 
   * to allow the URI to be resolved elsewhere.
   * For example, 
   * the {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE package registry}
   * is used to {@link org.eclipse.emf.ecore.EPackage.Registry#getEPackage resolve} 
   * the {@link org.eclipse.emf.ecore.EPackage namespace URI} of a package
   * to the static instance of that package.
   * So the important point is that an arbitrary implementation may resolve the URI to any resource,
   * not necessarily to one contained by this particular resource set.
   * If the delegation step fails to provide a result,
   * and if <code>loadOnDemand</code> is <code>true</code>,
   * a resource is {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandCreateResource created} 
   * and that resource becomes the result.
   * If <code>loadOnDemand</code> is <code>true</code>
   * and the result resource is not {@link Resource#isLoaded loaded}, 
   * it will be {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandLoad loaded} before it is returned.
   * </p>
   * @param uri the URI to resolve.
   * @param loadOnDemand whether to create and load the resource, if it doesn't already exists.
   * @return the resource resolved by the URI, or <code>null</code> if there isn't one and it's not being demand loaded.
   * @throws RuntimeException if a resource can't be demand created.
   * @throws org.eclipse.emf.common.util.WrappedException if a problem occurs during demand load.
   */
  Resource getResource(URI uri, boolean loadOnDemand);

  /**
   * TODO
   * @param uri
   * @param callback
   */
  Resource getResource(URI uri, Callback<Resource> callback);

  /**
   * Creates a new resource, of the appropriate type, and returns it.
   * <p>
   * It delegates to the resource factory {@link #getResourceFactoryRegistry registry} 
   * to determine the {@link Resource.Factory.Registry#getFactory(URI) correct} factory,
   * and then it uses that factory to {@link Resource.Factory#createResource create} the resource
   * and adds it to the {@link #getResources contents}.
   * If there is no registered factory, <code>null</code> will be returned;
   * when running within Eclipse,
   * a default XMI factory will be registered,
   * and this will never return <code>null</code>.
   * </p>
   * @param uri the URI of the resource to create.
   * @return a new resource, or <code>null</code> if no factory is registered.
   */
  Resource createResource(URI uri);

  /**
   * Creates a new resource, of the appropriate type, and returns it.
   * <p>
   * It delegates to the resource factory {@link #getResourceFactoryRegistry registry} 
   * to determine the {@link Resource.Factory.Registry#getFactory(URI, String) correct} factory,
   * and then it uses that factory to {@link Resource.Factory#createResource create} the resource
   * and adds it to the {@link #getResources contents}.
   * If there is no registered factory, <code>null</code> will be returned;
   * when running within Eclipse,
   * a default XMI factory will be registered,
   * and this will never return <code>null</code>.
   * </p>
   * @param uri the URI of the resource to create.
   * @param contentType the {@link ContentHandler#CONTENT_TYPE_PROPERTY content type identifier} of the URI, 
   * or <code>null</code> if no content type should be used during lookup.
   * @return a new resource, or <code>null</code> if no factory is registered.
   * @since 2.4
   */
  Resource createResource(URI uri, String contentType);

  /**
   * Returns the registry used for creating a resource of the appropriate type.
   * <p>
   * An implementation will typically provide a registry that delegates to
   * the {@link Resource.Factory.Registry#INSTANCE global} resource factory registry.
   * As a result, registrations made in this registry are <em>local</em> to this resource set,
   * i.e., they augment or override those of the global registry.
   * </p>
   * @return the registry used for creating a resource of the appropriate type.
   */
  Resource.Factory.Registry getResourceFactoryRegistry();

  /**
   * Sets the registry used for creating resource of the appropriate type.
   * @param resourceFactoryRegistry the new registry.
   */
  void setResourceFactoryRegistry(Resource.Factory.Registry resourceFactoryRegistry);

  /**
   * Returns the converter used to normalize URIs and to open streams.
   * @return the URI converter.
   * @see URIConverter
   * @see URI
   */
  URIConverter getURIConverter();

  /**
   * Sets the converter used to normalize URIs and to open streams.
   * @param converter the new converter.
   * @see URIConverter
   * @see URI
   */
  void setURIConverter(URIConverter converter);

  /**
   * Returns the registry used for looking up a package based namespace.
   * <p>
   * An implementation will typically provide a registry that delegates to
   * the {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE global} package registry.
   * As a result, registrations made in this registry are <em>local</em> to this resource set,
   * i.e., they augment or override those of the global registry.
   * </p>
   * @return the registry used for looking up a package based namespace.
   */
  EPackage.Registry getPackageRegistry();

  /**
   * Set the registry used for looking up a package based namespace.
   * @param packageRegistry the new registry.
   */
  void setPackageRegistry(EPackage.Registry packageRegistry);
}
