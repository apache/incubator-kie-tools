/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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

package jakarta.json.bind.spi;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

/**
 * Service provider for JSON Binding implementations.
 *
 * <p>Provider implementors must implement all abstract methods.
 *
 * <p>API clients can obtain instance of default provider by calling:
 *
 * <pre>{@code
 * JsonbProvider provider = JsonbProvider.provider();
 * }
 * }</pre>
 *
 * Specific provider instance lookup:
 *
 * <pre>{@code
 * JsonbProvider provider;
 * try {
 *   JsonbProvider.provider("foo.bar.ProviderImpl");
 * } catch (JsonbException e) {
 *   // provider not found or could not be instantiated
 * }
 * }</pre>
 *
 * where '{@code foo.bar.ProviderImpl}' is a vendor implementation class extending {@link
 * jakarta.json.bind.spi.JsonbProvider} and identified to service loader as specified in {@link
 * java.util.ServiceLoader} documentation. <br>
 * All the methods in this class are allowed to be called by multiple concurrent threads.
 *
 * @see jakarta.json.bind.Jsonb
 * @see java.util.ServiceLoader
 * @since JSON Binding 1.0
 */
public abstract class JsonbProvider {

  /**
   * A constant representing the name of the default {@link jakarta.json.bind.spi.JsonbProvider
   * JsonbProvider} implementation class.
   */
  private static final String DEFAULT_PROVIDER = "org.eclipse.yasson.JsonBindingProvider";

  /** Protected constructor. */
  protected JsonbProvider() {}

  /**
   * Creates a JSON Binding provider object by using the {@link java.util.ServiceLoader#load(Class)}
   * method. The first provider of {@code JsonbProvider} class from list of providers returned by
   * {@code ServiceLoader.load} call is returned. If there are no available service providers, this
   * method tries to load the default service provider using {@link Class#forName(String)} method.
   *
   * @see java.util.ServiceLoader
   * @throws JsonbException if there is no provider found, or there is a problem instantiating the
   *     provider instance.
   * @return {@code JsonbProvider} instance
   */
  @SuppressWarnings("UseSpecificCatch")
  public static JsonbProvider provider() {
    throw new JsonbException("Not implemented");
  }

  /**
   * Creates a JSON Binding provider object by using the {@link java.util.ServiceLoader#load(Class)}
   * method, matching {@code providerName}. The first provider of {@code JsonbProvider} class from
   * list of providers returned by {@code ServiceLoader.load} call, matching providerName is
   * returned. If no such provider is found, JsonbException is thrown.
   *
   * @param providerName Class name ({@code class.getName()}) to be chosen from the list of
   *     providers returned by {@code ServiceLoader.load(JsonbProvider.class)} call.
   * @throws JsonbException if there is no provider found, or there is a problem instantiating the
   *     provider instance.
   * @throws NullPointerException if providerName is {@code null}.
   * @see java.util.ServiceLoader
   * @return {@code JsonbProvider} instance
   */
  @SuppressWarnings("UseSpecificCatch")
  public static JsonbProvider provider(final String providerName) {
    throw new JsonbException("Not implemented");
  }

  /**
   * Returns a new instance of {@link jakarta.json.bind.JsonbBuilder JsonbBuilder} class.
   *
   * <p>{@link jakarta.json.bind.JsonbBuilder JsonbBuilder} provides necessary getter methods to
   * access required parameters.
   *
   * @return JsonbBuilder A new instance of class implementing {@link
   *     jakarta.json.bind.JsonbBuilder}. Always a non-null valid object.
   * @see jakarta.json.bind.Jsonb
   * @see jakarta.json.bind.JsonbBuilder
   * @throws JsonbException If an error was encountered while creating the {@link JsonbBuilder}
   *     instance.
   */
  public abstract JsonbBuilder create();
}
