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
package org.eclipse.emf.ecore.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ContentHandlerRegistryImpl;

/**
 * A handler for describing the contents of URIs.
 * <p>
 * A content handler is used primarily by a {@link URIConverter URI converter}
 * which provides support for {@link URIConverter#contentDescription(URI, Map) describing} the contents of a URI
 * by virtue of having a {@link URIConverter#getContentHandlers() list} of content handlers
 * that it consults to determine whether the  handler {@link #canHandle(URI) can handle} the given URI and if so
 * that it uses as a delegate for computing the {@link #contentDescription(URI, InputStream, Map, Map) content description}.
 * </p>
 * @see URIHandler
 * @see URIConverter
 * @since 2.4
 */
public interface ContentHandler
{
  /**
   * A registry of content handlers based on a priority order; lower values indicate have higher priority.
   */
  interface Registry extends SortedMap<Integer, List<ContentHandler>>
  {
    /**
     * A constant indicating a very high priority content handler.
     */
    int VERY_HIGH_PRIORITY = -10000;

    /**
     * A constant indicating a high priority content handler.
     */
    int HIGH_PRIORITY = -1000;

    /**
     * A constant indicating a normal priority content handler.
     */
    int NORMAL_PRIORITY = 0;

    /**
     * A constant indicating a low priority content handler.
     */
    int LOW_PRIORITY = 1000;

    /**
     * A constant indicating a very low priority content handler.
     */
    int VERY_LOW_PRIORITY = 10000;

    /**
     * Adds an additional content handler with the given priority to the map.
     * If there is already a list for the given priority in the map, the handler is added to that list.
     * Otherwise, a new list containing the handler is created and put into the map.
     * @param priority the priority of the handler.
     * @param contentHandler the new handler to add.
     */
    void put(int priority, ContentHandler contentHandler);

    /**
     * Returns a read only list of all the content handlers in the map in priority order.
     * @return a read only list of all the content handlers in the map in priority order.
     */
    List<ContentHandler> contentHandlers();

    /**
     * The global static content handler registry instance.
     */
    Registry INSTANCE = new ContentHandlerRegistryImpl();
  }

  /**
   * Returns whether this handler can describe the contents for the given URI.
   * @param uri the URI to consider.
   * @return whether this handler can describe the contents for the given URI.
   */
  boolean canHandle(URI uri);

  /**
   * An option used to specify the {@link Set} of properties being requested when computing a {@link #contentDescription(URI, InputStream, Map, Map) content description}.
   * @see #VALIDITY_PROPERTY
   * @see #CONTENT_TYPE_PROPERTY
   * @see #BYTE_ORDER_MARK_PROPERTY
   * @see #CHARSET_PROPERTY
   */
  String OPTION_REQUESTED_PROPERTIES = "REQUESTED_PROPERTIES";

  /**
   * A {@link #contentDescription(URI, InputStream, Map, Map) content description} property indicating the {@link Validity validity} of the content.
   * The value will be one of {@link Validity#INVALID}, {@link Validity#INDETERMINATE}, or {@link Validity#VALID}.
   * This property will always be present in a {@link #contentDescription(URI, InputStream, Map, Map) content description}.
   * @see Validity
   * @see IContentDescriber#INVALID
   * @see IContentDescriber#INDETERMINATE
   * @see IContentDescriber#VALID
   */
  String VALIDITY_PROPERTY = "org.eclipse.emf.ecore:validity";

  /**
   * A value specifying the validity of a {@link ContentHandler#contentDescription(URI, InputStream, Map, Map) content description}.
   * The {@link ContentHandler#VALIDITY_PROPERTY validity property} will have a value of this type,
   * i.., either {@link Validity#INVALID}, {@link Validity#INDETERMINATE}, or {@link Validity#VALID}.
   * @see IContentDescriber#INVALID
   * @see IContentDescriber#INDETERMINATE
   * @see IContentDescriber#VALID
   */
  enum Validity
  {
    /**
     * @see IContentDescriber#INVALID
     */
    INVALID,
    /**
     * @see IContentDescriber#INDETERMINATE
     */
    INDETERMINATE,
    /**
     * @see IContentDescriber#VALID
     */
    VALID
  }

  /**
   *  A {@link #contentDescription(URI, InputStream, Map, Map) content description} property describing the content's type identity.
   *  The value will be an arbitrary string.
   *  The content type is often used to {@link Resource.Factory.Registry#getContentTypeToFactoryMap() determine} an appropriate resource factory
   *  for processing the contents of a URI.
   *  @see IContentType#getId()
   *  @see Resource.Factory.Registry#getContentTypeToFactoryMap()
   *  @see Resource.Factory.Registry#getFactory(URI, String)
   */
  String CONTENT_TYPE_PROPERTY = "org.eclipse.emf.ecore:contentType";

  /**
   * A constant used to indicate that a {@link #CONTENT_TYPE_PROPERTY content type} needs to be computed.
   * @see ResourceSet#createResource(URI, String)
   * @see Resource.Factory.Registry#getFactory(URI, String)
   */
  String UNSPECIFIED_CONTENT_TYPE = "";

  /**
   * A {@link #contentDescription(URI, InputStream, Map, Map) content description} property describing the character set encoding used by the bytes of the content.
   * The value will be a string denoting a character set.
   * @see IContentDescription#CHARSET
   */
  String CHARSET_PROPERTY = "org.eclipse.core.runtime:charset";

  /**
   * A {@link #contentDescription(URI, InputStream, Map, Map) content description} property describing the byte order mark at the beginning of the contents.
   * The value will be of type {@link ByteOrderMark}.
   * @see IContentDescription#BYTE_ORDER_MARK
   */
  String BYTE_ORDER_MARK_PROPERTY = "org.eclipse.core.runtime:bom";

  /**
   * A value specifying the byte order mark of a {@link ContentHandler#contentDescription(URI, InputStream, Map, Map) content description}.
   * The {@link ContentHandler#BYTE_ORDER_MARK_PROPERTY byte order mark property} will have a value of this type,
   * i.., either {@link #UTF_8}, {@link #UTF_16BE}, or {@link #UTF_16LE}.
   * @see IContentDescription#BYTE_ORDER_MARK
   * @see IContentDescription#BOM_UTF_8
   * @see IContentDescription#BOM_UTF_16BE
   * @see IContentDescription#BOM_UTF_16LE
   */
  enum ByteOrderMark
  {
    /**
     * A byte order mark indicating a UTF-8 encoding.
     * @see IContentDescription#BOM_UTF_8
     */
    UTF_8
      {
        @Override
        public byte[] bytes()
        {
          return UTF_8_BYTES;
        }
      },
    /**
     * A byte order mark indicating a UTF-16 big endian encoding.
     * @see IContentDescription#BOM_UTF_16BE
     */
    UTF_16BE
      {
        @Override
        public byte[] bytes()
        {
          return UTF_16BE_BYTES;
        }
      },
    /**
     * A byte order mark indicating a UTF-16 little endian encoding.
     * @see IContentDescription#BOM_UTF_16LE
     */
    UTF_16LE
      {
        @Override
        public byte[] bytes()
        {
          return UTF_16LE_BYTES;
        }
      };

    private static final byte [] UTF_8_BYTES;
    private static final byte [] UTF_16BE_BYTES;
    private static final byte [] UTF_16LE_BYTES;

    static
    {
      byte [] utf8Bytes;
      byte [] utf16BEBytes;
      byte [] utf16LEBytes;
      try
      {
        utf8Bytes = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        utf16BEBytes = new byte[] {(byte) 0xFE, (byte) 0xFF};
        utf16LEBytes = new byte[] {(byte) 0xFF, (byte) 0xFE};
      }
      catch (Throwable throwable)
      {
        utf8Bytes = new byte [] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        utf16BEBytes = new byte [] {(byte) 0xFE, (byte) 0xFF};
        utf16LEBytes =  new byte [] {(byte) 0xFF, (byte) 0xFE};
      }
      UTF_8_BYTES = utf8Bytes;
      UTF_16BE_BYTES = utf16BEBytes;
      UTF_16LE_BYTES = utf16LEBytes;
    }

    /**
     * Returns the bytes associated with this byte order mark.
     * This value will be identical to the corresponding constant for {@link IContentDescription#BYTE_ORDER_MARK}.
     * @see IContentDescription#BYTE_ORDER_MARK
     * @see IContentDescription#BOM_UTF_8
     * @see IContentDescription#BOM_UTF_16BE
     * @see IContentDescription#BOM_UTF_16LE
     * @return the bytes associated with this byte order mark.
     */
    public abstract byte [] bytes();

    /**
     * Returns the byte order mark at the start of the input stream, or <code>null</code> if there isn't one.
     * @param inputStream the input stream to scan.
     * @return the byte order mark at the start of the input stream, or <code>null</code> if there isn't one.
     * @throws IOException if there is a problem reading from the input stream.
     */
    public static ByteOrderMark read(InputStream inputStream) throws IOException
    {
      int first = inputStream.read();
      if (first == 0xEF)
      {
        if (inputStream.read() == 0xBB && inputStream.read() == 0xBF)
        {
          return UTF_8;
        }
      }
      else if (first == 0xFE)
      {
        if (inputStream.read() == 0xFF)
        {
          return UTF_16BE;
        }
      }
      else if (first == 0xFF)
      {
        if (inputStream.read() == 0xFE)
        {
          return UTF_16LE;
        }
      }

      return null;
    }
  }

  /**
   * An unmodifiable {@link #contentDescription(URI, InputStream, Map, Map) content description} indicating that the content is invalid.
   */
  Map<String, Object> INVALID_CONTENT_DESCRIPTION =
    Collections.unmodifiableMap(org.eclipse.emf.ecore.resource.impl.ContentHandlerImpl.createContentDescription(Validity.INVALID));

  /**
   * Returns a map of properties that describe the content of the given URI's corresponding input stream.
   * The {@link #VALIDITY_PROPERTY validity property} will always be present to indicate the status.
   * The {@link #CONTENT_TYPE_PROPERTY content type property} too will always be present,
   * except when the validity property is {@link Validity#INVALID}.
   * The option {@link #OPTION_REQUESTED_PROPERTIES} can be used to specify the set of additional properties that should appear in the result.
   * If this option is not present, all properties this handler can compute will be returned.
   * The context map is used to cache results that can be shared between content handler implementations.
   * For example, once a content handler has computed the {@link #BYTE_ORDER_MARK_PROPERTY byte order mark property},
   * the result can be cached so that it is not recomputed repeatedly.
   * Similarly, content handlers for XML content might cache the {@link #CONTENT_TYPE_PROPERTY character set property},
   * and might even share a parsed XML representation so that each handle can analyze to determine whether that XML is of the expected form for the content type.
   *
   * @param uri the URI for which to determine the content description.
   * @param inputStream the input stream associated with the given URI.
   * @param options a map of options to direct what kind of description is needed.
   * @param context a map of contextual information that content handlers use to store partially computed results.
   * @return a map of properties that describe the content of the given URI's corresponding input stream.
   * @throws IOException if there is a problem reading the stream.
   */
  Map<String, ?> contentDescription(URI uri, InputStream inputStream, Map<?, ?> options, Map<Object, Object> context) throws IOException;
}
