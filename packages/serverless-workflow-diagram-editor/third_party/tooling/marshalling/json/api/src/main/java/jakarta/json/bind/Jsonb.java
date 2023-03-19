/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jakarta.json.bind;

import java.lang.reflect.Type;

/**
 * {@code Jsonb} provides an abstraction over the JSON Binding framework operations:
 *
 * <ul>
 *   <li>{@code fromJson}: read JSON input, deserialize to Java objects content tree
 *   <li>{@code toJson}: serialize Java objects content tree to JSON input
 * </ul>
 *
 * <p>Instance of this class is created using {@link jakarta.json.bind.JsonbBuilder JsonbBuilder}
 * builder methods:
 *
 * <pre>{@code
 * // Example 1 - Creating Jsonb using default JsonbBuilder instance provided by default JsonbProvider
 * Jsonb jsonb = JsonbBuilder.create();
 *
 * // Example 2 - Creating Jsonb instance for a specific provider specified by a class name
 * Jsonb jsonb = JsonbBuilder.newBuilder("foo.bar.ProviderImpl).build();
 *
 * // Example 3 - Creating Jsonb instance from a custom provider implementation
 * Jsonb jsonb = new CustomJsonbBuilder().build();
 * }</pre>
 *
 * <b>Deserializing (reading) JSON</b><br>
 *
 * <blockquote>
 *
 * Can de-serialize JSON data that represents either an entire JSON document or a subtree of a JSON
 * document.
 *
 * </blockquote>
 *
 * <blockquote>
 *
 * Reading (deserializing) object content tree from a File:<br>
 * <br>
 *
 * <pre>
 *     Jsonb jsonb = JsonbBuilder.create();
 *     Book book = jsonb.fromJson(new FileReader("jsonfile.json"), Book.class);</pre>
 *
 * If the deserialization process is unable to deserialize the JSON content to an object content
 * tree, fatal error is reported that terminates processing by throwing JsonbException.
 *
 * </blockquote>
 *
 * <p><b>Serializing (writing) to JSON</b>
 *
 * <blockquote>
 *
 * Serialization writes the representation of a Java object content tree into JSON data.
 *
 * </blockquote>
 *
 * <blockquote>
 *
 * Writing (serializing) object content tree to a File:<br>
 * <br>
 *
 * <pre>
 *     jsonb.toJson(object, new FileWriter("foo.json"));</pre>
 *
 * Writing (serializing) to a Writer:<br>
 * <br>
 *
 * <pre>
 *     jsonb.toJson(object, new PrintWriter(System.out));
 *    </pre>
 *
 * </blockquote>
 *
 * <p><b>Encoding</b>
 *
 * <blockquote>
 *
 * In deserialization operations ({@code fromJson}), encoding of JSON data is detected
 * automatically. Use the {@link jakarta.json.bind.JsonbConfig JsonbConfig} API to configure
 * expected input encoding used within deserialization operations. Client applications are expected
 * to supply a valid character encoding as defined in the <a
 * href="http://tools.ietf.org/html/rfc7159">RFC 7159</a> and supported by Java Platform.
 *
 * <p>In serialization operations ({@code toJson}), UTF-8 encoding is used by default for writing
 * JSON data. Use the {@link jakarta.json.bind.JsonbConfig JsonbConfig} API to configure the output
 * encoding used within serialization operations. Client applications are expected to supply a valid
 * character encoding as defined in the <a href="http://tools.ietf.org/html/rfc7159">RFC 7159</a>
 * and supported by Java Platform.
 *
 * </blockquote>
 *
 * <p>For optimal use, {@code JsonbBuilder} and {@code Jsonb} instances should be reused - for a
 * typical use-case, only one {@code Jsonb} instance is required by an application.
 *
 * <p>All the methods in this class are safe for use by multiple concurrent threads.
 *
 * <p>Calling {@code Closable.close()} method will cleanup all CDI managed components (such as
 * adapters with CDI dependencies) created during interaction with Jsonb. Calling {@code close()}
 * must be done after all threads has finished interaction with Jsonb. If there are remaining
 * threads working with Jsonb and {@code close()} is called, behaviour is undefined.
 *
 * @see Jsonb
 * @see JsonbBuilder
 * @see java.util.ServiceLoader
 * @since JSON Binding 1.0
 */
public interface Jsonb extends AutoCloseable {

  /**
   * Reads in a JSON data from the specified string and return the resulting content tree.
   *
   * @param str The string to deserialize JSON data from.
   * @param type Type of the content tree's root object.
   * @param <T> Type of the content tree's root object.
   * @return the newly created root object of the java content tree
   * @throws JsonbException If any unexpected error(s) occur(s) during deserialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   */
  <T> T fromJson(String str, Class<T> type) throws JsonbException;

  /**
   * Reads in a JSON data from the specified string and return the resulting content tree.
   *
   * @param str The string to deserialize JSON data from.
   * @param runtimeType Runtime type of the content tree's root object.
   * @param <T> Type of the content tree's root object.
   * @return the newly created root object of the java content tree
   * @throws JsonbException If any unexpected error(s) occur(s) during deserialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   */
  <T> T fromJson(String str, Type runtimeType) throws JsonbException;

  /**
   * Reads in a JSON data from the specified Reader and return the resulting content tree.
   *
   * @param reader The character stream is read as a JSON data.
   * @param type Type of the content tree's root object.
   * @param <T> Type of the content tree's root object.
   * @return the newly created root object of the java content tree
   * @throws JsonbException If any unexpected error(s) occur(s) during deserialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   */
  @GwtIncompatible
  <T> T fromJson(java.io.Reader reader, Class<T> type) throws JsonbException;

  /**
   * Reads in a JSON data from the specified Reader and return the resulting content tree.
   *
   * @param reader The character stream is read as a JSON data.
   * @param runtimeType Runtime type of the content tree's root object.
   * @param <T> Type of the content tree's root object.
   * @return the newly created root object of the java content tree
   * @throws JsonbException If any unexpected error(s) occur(s) during deserialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   */
  @GwtIncompatible
  <T> T fromJson(java.io.Reader reader, Type runtimeType) throws JsonbException;

  /**
   * Reads in a JSON data from the specified InputStream and return the resulting content tree.
   *
   * @param stream The stream is read as a JSON data. Upon a successful completion, the stream will
   *     be closed by this method.
   * @param type Type of the content tree's root object.
   * @param <T> Type of the content tree's root object.
   * @return the newly created root object of the java content tree
   * @throws JsonbException If any unexpected error(s) occur(s) during deserialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   */
  @GwtIncompatible
  <T> T fromJson(java.io.InputStream stream, Class<T> type) throws JsonbException;

  /**
   * Reads in a JSON data from the specified InputStream and return the resulting content tree.
   *
   * @param stream The stream is read as a JSON data. Upon a successful completion, the stream will
   *     be closed by this method.
   * @param runtimeType Runtime type of the content tree's root object.
   * @param <T> Type of the content tree's root object.
   * @return the newly created root object of the java content tree
   * @throws JsonbException If any unexpected error(s) occur(s) during deserialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   */
  @GwtIncompatible
  <T> T fromJson(java.io.InputStream stream, Type runtimeType) throws JsonbException;

  /**
   * Writes the Java object tree with root object {@code object} to a String instance as JSON.
   *
   * @param object The root object of the object content tree to be serialized. Must not be null.
   * @return String instance with serialized JSON data.
   * @throws JsonbException If any unexpected problem occurs during the serialization, such as I/O
   *     error.
   * @throws NullPointerException If any of the parameters is {@code null}.
   * @since JSON Binding 1.0
   */
  String toJson(Object object) throws JsonbException;

  /**
   * Writes the Java object tree with root object {@code object} to a String instance as JSON.
   *
   * @param object The root object of the object content tree to be serialized. Must not be null.
   * @param runtimeType Runtime type of the content tree's root object. Provided type needs to be
   *     related to the type of the instance.
   * @return String instance with serialized JSON data.
   * @throws JsonbException If any unexpected problem occurs during the serialization, such as I/O
   *     error.
   * @throws NullPointerException If any of the parameters is {@code null}.
   * @since JSON Binding 1.0
   */
  String toJson(Object object, Type runtimeType) throws JsonbException;

  /**
   * Writes the object content tree into a Writer character stream.
   *
   * @param object The object content tree to be serialized.
   * @param writer The JSON will be sent as a character stream to the given {@link Writer}.
   * @throws JsonbException If any unexpected problem occurs during the serialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   * @since JSON Binding 1.0
   */
  @GwtIncompatible
  void toJson(Object object, java.io.Writer writer) throws JsonbException;

  /**
   * Writes the object content tree into a Writer character stream.
   *
   * @param object The object content tree to be serialized.
   * @param runtimeType Runtime type of the content tree's root object. Provided type needs to be
   *     related to the type of the instance.
   * @param writer The JSON will be sent as a character stream to the given {@link Writer}.
   * @throws JsonbException If any unexpected problem occurs during the serialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   * @since JSON Binding 1.0
   */
  @GwtIncompatible
  void toJson(Object object, Type runtimeType, java.io.Writer writer) throws JsonbException;

  /**
   * Writes the object content tree into output stream.
   *
   * @param object The object content tree to be serialized.
   * @param stream The JSON will be sent as a byte stream to the given {@link OutputStream}. Upon a
   *     successful completion, the stream will be closed by this method.
   * @throws JsonbException If any unexpected problem occurs during the serialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   * @since JSON Binding 1.0
   */
  @GwtIncompatible
  void toJson(Object object, java.io.OutputStream stream) throws JsonbException;

  /**
   * Writes the object content tree into output stream.
   *
   * @param object The object content tree to be serialized.
   * @param runtimeType Runtime type of the content tree's root object. Provided type needs to be
   *     related to the type of the instance.
   * @param stream The JSON will be sent as a byte stream to the given {@link OutputStream}. Upon a
   *     successful completion, the stream will be closed by this method.
   * @throws JsonbException If any unexpected problem occurs during the serialization.
   * @throws NullPointerException If any of the parameters is {@code null}.
   * @since JSON Binding 1.0
   */
  @GwtIncompatible
  void toJson(Object object, Type runtimeType, java.io.OutputStream stream) throws JsonbException;
}
