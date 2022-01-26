/**
 * Copyright (c) 2002-2011 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.resource;


import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;


/**
 * A converter to normalize a URI or to produce an input or output stream for a URI.
 * <p>
 * A resource set provides {@link ResourceSet#getURIConverter one} of these
 * for use by it's {@link ResourceSet#getResources resources}
 * when they are {@link Resource#save(java.util.Map) serialized} and {@link Resource#load(java.util.Map) deserialized}.
 * A resource set also uses this directly when it {@link ResourceSet#getResource looks up} a resource:
 * a resource is considered a match if {@link Resource#getURI it's URI}, 
 * and the URI being looked up, 
 * {@link #normalize normalize} to {@link URI#equals(Object) equal} URIs.
 * Clients must extend the default {@link org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl implementation},
 * since methods can and will be added to this API.
 * </p>
 * @see ResourceSet#getURIConverter()
 * @see URIHandler
 * @see ContentHandler
 */
public interface URIConverter
{
  /**
   * An option used to pass the calling URIConverter to the {@link URIHandler}s.
   * @since 2.4
   */
  String OPTION_URI_CONVERTER = "URI_CONVERTER";

  /**
   * An option to pass a {@link Map Map&lt;Object, Object>} to any of the URI converter's methods 
   * in order to yield results in addition to the returned value of the method.
   * @since 2.4
   */
  String OPTION_RESPONSE = "RESPONSE";

  /**
   * A property of the {@link #OPTION_RESPONSE response option} 
   * used to yield the result for the asynchronous methods.
   * @since 2.7
   */
  String RESPONSE_RESULT = "RESULT";

  /**
   * A property of the {@link #OPTION_RESPONSE response option} 
   * used to yield the {@link #ATTRIBUTE_TIME_STAMP time stamp} associated
   * with the creation of an {@link #createInputStream(URI, Map) input} or an {@link #createOutputStream(URI, Map) output} stream.
   * This is typically used by resource {@link Resource#load(Map) load} and {@link Resource#save(Map) save} 
   * in order to set the {@link Resource#getTimeStamp()}.
   * @since 2.4
   */
  String RESPONSE_TIME_STAMP_PROPERTY = "TIME_STAMP";

  /**
   * A property of the {@link #OPTION_RESPONSE response option} 
   * used to yield the newly allocated URI associated
   * with the creation of an {@link #createOutputStream(URI, Map) output} stream.
   * This is typically used by resource {@link Resource#save(Map) save} 
   * in order to {@link Resource#setURI(URI) set the resource URI}.
   * @since 2.7
   */
  String RESPONSE_URI = "URI";

  /**
   * A {@link #createOutputStream(URI, Map) createOutputStream},
   * {@link #store(URI, byte[], Map, Callback) store},
   * or {@link #delete(URI, Map, Callback) delete} option 
   * that specifies a long {@link #RESPONSE_TIME_STAMP_PROPERTY timestamp} which must match the underlying resource's timestamp for the update to succeed.
   * @since 2.7
   */
  String OPTION_UPDATE_ONLY_IF_TIME_STAMP_MATCHES = "UPDATE_ONLY_IF_TIME_STAMP_MATCHES";

  /**
   * Returns the normalized form of the URI.
   * <p>
   * This may, in theory, do absolutely anything.
   * Default behaviour includes 
   * applying URI {@link URIConverter#getURIMap mapping},
   * assuming <code>"file:"</code> protocol 
   * for a {@link URI#isRelative relative} URI with a {@link URI#hasRelativePath relative path}:
   *<pre>
   *  ./WhateverDirectory/Whatever.file 
   *    -> 
   *  file:./WhateverDirectory/Whatever.file
   *</pre>
   * and assuming <code>"platform:/resource"</code> protocol 
   * for a relative URI with an {@link URI#hasAbsolutePath absolute path}:
   *<pre>
   *  /WhateverRelocatableProject/Whatever.file 
   *    -> 
   *  platform:/resource/WhateverRelocatableProject/Whatever.file
   *</pre>
   * </p>
   * <p>
   * It is important to emphasize that normalization can result in loss of information.
   * The normalized URI should generally be used only for comparison and for access to input or output streams.
   * </p>
   * @param uri the URI to normalize.
   * @return the normalized form.
   * @see org.eclipse.emf.ecore.plugin.EcorePlugin#getPlatformResourceMap
   */
  URI normalize(URI uri);

  /**
   * Returns the map used for remapping a logical URI to a physical URI when {@link #normalize normalizing}.
   * <p>
   * An implementation will typically also delegate to the {@link URIConverter#URI_MAP global} map,
   * so registrations made in this map are <em>local</em> to this URI converter,
   * i.e., they augment or override those of the global map.
   * </p>
   * <p>
   * The map generally specifies instance to instance mapping,
   * except for the case that both the key URI and the value URI end with "/", 
   * which specifies a folder to folder mapping.
   * A folder mapping will remap any URI that has the key as its {@link URI#replacePrefix prefix}, 
   * e.g., if the map contains:
   *<pre>
   *  http://www.example.com/ -> platform:/resource/example/
   *</pre>
   * then the URI
   *<pre>
   *  http://www.example.com/a/b/c.d
   *</pre>
   * will map to 
   *<pre>
   *  platform:/resource/example/a/b/c.d
   *</pre>
   * A matching instance mapping is considered first.
   * If there isn't one, the folder mappings are considered starting with the {@link URI#segmentCount() longest} prefix. 
   * </p>
   * @see #normalize(URI)
   * @see #URI_MAP
   * @return the map used for remapping a logical URI to a physical URI.
   */
  Map<URI, URI> getURIMap();

  /**
   * The global static URI map.
   * Registrations made in this instance will (typically) be available
   * for {@link URIConverter#normalize use} by any URI converter.
   * It is populated by URI mappings registered via
   * {@link org.eclipse.emf.ecore.plugin.EcorePlugin.Implementation#startup() plugin registration}.
   * @see #normalize(URI)
   */
  Map<URI, URI> URI_MAP = org.eclipse.emf.ecore.resource.impl.URIMappingRegistryImpl.INSTANCE.map();

  /**
   * Returns the list of {@link URIHandler}s.
   * @return the list of {@link URIHandler}s.
   * @since 2.4
   */
  EList<URIHandler> getURIHandlers();

  /**
   * Returns the first URI handler in the {@link #getURIHandler(URI) list} of URI handlers which {@link URIHandler#canHandle(URI) can handle} the given URI.
   * @param uri the URI for which to find a handler.
   * @return the first URI handler in the list of URI handlers which can handle the given URI.
   * @throws RuntimeException if no matching handler is found.
   * @since 2.4 
   */
  URIHandler getURIHandler(URI uri);

  /**
   * Returns the list of {@link ContentHandler}s.
   * @return the list of {@link ContentHandler}s.
   * @since 2.4
   */
  EList<ContentHandler> getContentHandlers();

  /**
   * Creates an input stream for the URI and returns it;
   * it has the same effect as calling {@link #createInputStream(URI, Map) createInputStream(uri, null)}.
   * @param uri the URI for which to create the input stream.
   * @return an open input stream.
   * @exception IOException if there is a problem obtaining an open input stream.
   * @see #createInputStream(URI, Map)
   */
  InputStream createInputStream(URI uri) throws IOException;

  /**
   * Creates an input stream for the URI and returns it.
   * <p>
   * It {@link #normalize normalizes} the URI and uses that as the basis for further processing.
   * Special requirements, such as an Eclipse file refresh, 
   * are handled by the {@link org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl default implementation}.
   * </p>
   * @param uri the URI for which to create the input stream.
   * @param options a map of options to influence the kind of stream that is returned; unrecognized options are ignored and <code>null</code> is permitted.
   * @return an open input stream.
   * @exception IOException if there is a problem obtaining an open input stream.
   * @since 2.4
   */
  InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException;

  /**
   * TODO
   * @param uri
   * @param options
   * @param callback
   */
  void createInputStream(URI uri, Map<?, ?> options, Callback<Map<?, ?>> callback);

  /**
   * An interface that is optionally implemented by the input streams returned from 
   * {@link URIConverter#createInputStream(URI)} and {@link URIConverter#createInputStream(URI, Map)}.
   * An input stream implementing this interface is highly unlikely to support {@link InputStream#read() read}.
   * Instead {@link #loadResource(Resource) loadResource} should be called.
   * @since 2.7
   */
  interface Loadable
  {
    /**
     * Load the contents of the resource directly from the backing store for which the stream implementing this interface is a facade.
     * @param resource the resource to load.
     * @throws IOException if there are any problems load the resource from the backing store.
     */
    void loadResource(Resource resource) throws IOException;
  }

  /**
   * Creates an output stream for the URI and returns it;
   * it has the same effect as calling {@link #createOutputStream(URI, Map) createOutputStream(uri, null)}.
   * @return an open output stream.
   * @exception IOException if there is a problem obtaining an open output stream.
   * @see #createOutputStream(URI, Map)
   */
  OutputStream createOutputStream(URI uri) throws IOException;
  
  /**
   * Creates an output stream for the URI and returns it.
   * <p>
   * It {@link #normalize normalizes} the URI and uses that as the basis for further processing.
   * Special requirements, such as an Eclipse file refresh, 
   * are handled by the {@link org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl default implementation}.
   * </p>
   * @param uri the URI for which to create the output stream.
   * @param options a map of options to influence the kind of stream that is returned; unrecognized options are ignored and <code>null</code> is permitted.
   * @return an open output stream.
   * @exception IOException if there is a problem obtaining an open output stream.
   * @since 2.4
   */
  OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException;

  /**
   * TODO
   * @param uri
   * @param bytes
   * @param options
   * @param callback
   */
  void store(URI uri, byte[] bytes, Map<?, ?> options, Callback<Map<?, ?>> callback);

  /**
   * An interface that is optionally implemented by the output streams returned from 
   * {@link URIConverter#createOutputStream(URI)} and {@link URIConverter#createOutputStream(URI, Map)}.
   * An output stream implementing this interface is highly unlikely to support {@link OutputStream#write(int) write}.
   * Instead {@link #saveResource(Resource) saveResource} should be called.
   * @since 2.7
   */
  interface Saveable
  {
    /**
     * Save the contents of the resource directly to the backing store for which the stream implementing this interface is a facade.
     * @param resource the resource to save.
     * @throws IOException if there are any problems saving the resource to the backing store.
     */
    void saveResource(Resource resource) throws IOException;
  }

  /**
   * An interface to be implemented by encryption service providers.
   * @since 2.2.0
   */
  interface Cipher
  {
    /**
     * Encrypts the specified output stream.
     * @param outputStream
     * @return an encrypted output stream
     */
    OutputStream encrypt(OutputStream outputStream) throws Exception;
  
    /**
     * This method is invoked after the encrypted output stream is used
     * allowing the Cipher implementation to do any maintenance work required,
     * such as flushing an internal cache.
     * @param outputStream the encrypted stream returned by {@link #encrypt(OutputStream)}.
     */
    void finish(OutputStream outputStream) throws Exception;
  
    /**
     * Decrypts the specified input stream.
     * @param inputStream
     * @return a decrypted input stream
     */
    InputStream decrypt(InputStream inputStream) throws Exception;
    
    /**
     * This method is invoked after the decrypted input stream is used
     * allowing the Cipher implementation to do any maintenance work required,
     * such as flushing internal cache.
     * @param inputStream the stream returned by {@link #decrypt(InputStream)}.
     */
    void finish(InputStream inputStream) throws Exception;
  }

  /**
   * Deletes the contents of the given URI. 
   * @param uri the URI to consider.
   * @param options options to influence how the contents are deleted, or <code>null</code> if there are no options.
   * @throws IOException if there is a problem deleting the contents.
   * @since 2.4
   */
  void delete(URI uri, Map<?, ?> options) throws IOException;

  /**
   * TODO
   * @param uri
   * @param options
   * @param callback
   */
  void delete(URI uri, Map<?, ?> options, Callback<Map<?, ?>> callback);
  
  /**
   * Returns a map from String properties to their corresponding values representing a description the given URI's contents.
   * See the {@link ContentHandler#contentDescription(URI, InputStream, Map, Map) content handler} for more details.
   * @param uri the URI to consider.
   * @param options options to influence how the content description is determined, or <code>null</code> if there are no options.
   * @return a map from String properties to their corresponding values representing a description the given URI's contents.
   * @throws IOException if there is a problem accessing the contents.
   * @see ContentHandler#contentDescription(URI, InputStream, Map, Map)
   * @since 2.4
   */
  Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException;

  /**
   * Returns whether the given URI has contents.
   * If the URI {@link #exists(URI, Map) exists}
   * it will be possible to {@link #createOutputStream(URI, Map) create} an input stream.
   * @param uri the URI to consider.
   * @param options options to influence how the existence determined, or <code>null</code> if there are no options.
   * @return whether the given URI has contents.
   * @since 2.4
   */
  boolean exists(URI uri, Map<?, ?> options);

  /**
   * TODO
   * @param uri
   * @param options
   * @param callback
   */
  void exists(URI uri, Map<?, ?> options, Callback<Boolean> callback);

  /**
   * The time stamp {@link #getAttributes(URI, Map) attribute} representing the last time the contents of a URI were modified.
   * The value is represented as Long that encodes the number of milliseconds 
   * since the epoch 00:00:00 GMT, January 1, 1970.
   * @since 2.4
   */
  String ATTRIBUTE_TIME_STAMP = "timeStamp";

  /**
   * A {@link #ATTRIBUTE_TIME_STAMP} value that indicates no time stamp is available.
   * @since 2.4
   */
  long NULL_TIME_STAMP = -1;

  /**
   * The length {@link #getAttributes(URI, Map) attribute} representing the number of bytes in the contents of a URI.
   * It is represented as a Long value.
   * @since 2.4
   */
  String ATTRIBUTE_LENGTH = "length";

  /**
   * The read only {@link #getAttributes(URI, Map) attribute} representing whether the contents of a URI can be modified.
   * It is represented as a Boolean value.
   * If the URI's contents {@link #exists(URI, Map) exist} and it is read only, 
   * it will not be possible to {@link #createOutputStream(URI, Map) create} an output stream.
   * @since 2.4
   */
  String ATTRIBUTE_READ_ONLY = "readOnly";

  /**
   * The execute {@link #getAttributes(URI, Map) attribute} representing whether the contents of a URI can be executed.
   * It is represented as a Boolean value.
   * @since 2.4
   */
  String ATTRIBUTE_EXECUTABLE = "executable";

  /**
   * The archive {@link #getAttributes(URI, Map) attribute} representing whether the contents of a URI are archived.
   * It is represented as a Boolean value.
   * @since 2.4
   */
  String ATTRIBUTE_ARCHIVE = "archive";

  /**
   * The hidden {@link #getAttributes(URI, Map) attribute} representing whether the URI is visible.
   * It is represented as a Boolean value.
   * @since 2.4
   */
  String ATTRIBUTE_HIDDEN = "hidden";

  /**
   * The directory {@link #getAttributes(URI, Map) attribute} representing whether the URI represents a directory rather than a file.
   * It is represented as a Boolean value.
   * @since 2.4
   */
  String ATTRIBUTE_DIRECTORY = "directory";

  /**
   * An option passed to a {@link Set Set<String>} to {@link #getAttributes(URI, Map)} to indicate the specific attributes to be fetched.
   */
  String OPTION_REQUESTED_ATTRIBUTES = "requestedAttributes";

  /**
   * Returns a map from String attributes to their corresponding values representing information about various aspects of the URI's state.
   * The {@link #OPTION_REQUESTED_ATTRIBUTES requested attributes option} can be used to specify which properties to fetch;
   * without that option, all supported attributes will be fetched.
   * If the URI doesn't not support any particular attribute, an entry for that attribute will not be appear in the result.
   * @param uri the URI to consider.
   * @param options options to influence how the attributes are determined, or <code>null</code> if there are no options.
   * @return a map from String attributes to their corresponding values representing information about various aspects of the URI's state.
   */
  Map<String, ?> getAttributes(URI uri, Map<?, ?> options);

  /**
   * Updates the map from String attributes to their corresponding values representing information about various aspects of the URI's state.
   * Unsupported or unchangeable attributes are ignored.
   * @param uri the URI to consider.
   * @param attributes the new values for the attributes.
   * @param options options to influence how the attributes are updated, or <code>null</code> if there are no options.
   * @throws IOException if there is a problem updating the attributes.
   */
  void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException;

  /**
   * The global static URI converter instance.
   * It's generally not a good idea to modify any aspect of this instance.
   * Instead, use a resource set's {@link ResourceSet#getURIConverter() local} instance.
   * @since 2.4
   */
  URIConverter INSTANCE = new ExtensibleURIConverterImpl();
}
