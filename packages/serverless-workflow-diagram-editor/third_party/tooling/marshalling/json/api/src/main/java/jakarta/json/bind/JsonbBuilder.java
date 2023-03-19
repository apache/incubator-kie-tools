/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.spi.JsonbProvider;

/**
 * JsonbBuilder class provides the client's entry point to the JSON Binding API. It builds {@link
 * jakarta.json.bind.Jsonb Jsonb} instances based on all parameters and configuration provided
 * before calling {@code build()} method.
 *
 * <p>For most use-cases, only one instance of JsonbBuilder is required within the application.
 *
 * @see Jsonb
 * @see java.util.ServiceLoader
 * @since JSON Binding 1.0
 */
public interface JsonbBuilder {

  /**
   * Set configuration which will be set to the newly created {@link jakarta.json.bind.Jsonb Jsonb}
   * instance.
   *
   * @param config Configuration for {@link jakarta.json.bind.Jsonb Jsonb} instance.
   * @return This {@code JsonbBuilder} instance.
   */
  JsonbBuilder withConfig(JsonbConfig config);

  /**
   * Provides a <a href="https://projects.eclipse.org/projects/ee4j.jsonp/">JSON-P</a> provider to
   * be used for all <a href="https://projects.eclipse.org/projects/ee4j.jsonp/">JSON-P</a> related
   * operations.
   *
   * @param jsonpProvider {@link jakarta.json.spi.JsonProvider JsonProvider} instance to be used by
   *     Jsonb to lookup JSON-P implementation.
   * @return This {@code JsonbBuilder} instance.
   */
  JsonbBuilder withProvider(JsonProvider jsonpProvider);

  /**
   * Returns a new instance of {@link jakarta.json.bind.Jsonb Jsonb} based on the parameters and
   * configuration specified previously in this builder.
   *
   * @return Jsonb A new instance of {@link jakarta.json.bind.Jsonb Jsonb} class. Always a non-null
   *     valid object.
   * @throws jakarta.json.bind.JsonbException If an error was encountered while creating the Jsonb
   *     instance, such as (but not limited to) no JSON Binding provider found, or classes provide
   *     conflicting annotations.
   * @throws IllegalArgumentException If there's an error processing the set parameters, such as the
   *     non-null parameter is assigned null value, or unrecognized property is set in {@link
   *     jakarta.json.bind.JsonbConfig JsonbConfig}.
   */
  Jsonb build();

  /**
   * Create a new {@link jakarta.json.bind.Jsonb} instance using the default {@code JsonbBuilder}
   * implementation provided as returned from {@link jakarta.json.bind.spi.JsonbProvider#provider()}
   * method.
   *
   * @return new {@link jakarta.json.bind.Jsonb Jsonb} instance.
   */
  static Jsonb create() {
    return JsonbProvider.provider().create().build();
  }

  /**
   * Create a new {@link jakarta.json.bind.Jsonb} instance using the default {@code JsonbBuilder}
   * implementation provided as returned from {@link jakarta.json.bind.spi.JsonbProvider#provider()}
   * method, configured with provided configuration.
   *
   * @param config Provided configuration for {@link jakarta.json.bind.Jsonb} instance.
   * @return new {@link jakarta.json.bind.Jsonb Jsonb} instance.
   */
  static Jsonb create(JsonbConfig config) {
    return JsonbProvider.provider().create().withConfig(config).build();
  }

  /**
   * Create a new {@code JsonbBuilder} instance as returned by the default {@link
   * jakarta.json.bind.spi.JsonbProvider#provider()} method.
   *
   * @return new {@code JsonbBuilder} instance.
   */
  static JsonbBuilder newBuilder() {
    return JsonbProvider.provider().create();
  }

  /**
   * Create a new {@code JsonbBuilder} instance as returned by {@link
   * jakarta.json.bind.spi.JsonbProvider#provider(String)} method.
   *
   * @param providerName Provider class name to be looked up by {@link java.util.ServiceLoader
   *     ServiceLoader}.
   * @return new {@code JsonbBuilder} instance.
   */
  static JsonbBuilder newBuilder(final String providerName) {
    return JsonbProvider.provider(providerName).create();
  }

  /**
   * Create a new {@code JsonbBuilder} instance as returned by {@code provider#create} call.
   *
   * @param provider {@link jakarta.json.spi.JsonProvider JsonProvider} instance used for creating
   *     {@code JsonBuilder instances}.
   * @return new {@code JsonbBuilder} instance.
   */
  static JsonbBuilder newBuilder(final JsonbProvider provider) {
    return provider.create();
  }
}
