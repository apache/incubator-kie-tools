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

/**
 * Service Provider Interface (SPI) to plug in implementations of JSON Binding API {@link
 * jakarta.json.bind.JsonbBuilder JsonbBuilder} objects.
 *
 * <p>{@link jakarta.json.bind.spi.JsonbProvider JsonbProvider} is an abstract class that provides a
 * service for creating JSON Binding builder implementation instances. A <i>service provider</i> for
 * {@link jakarta.json.bind.spi.JsonbProvider JsonbProvider} provides an specific implementation by
 * subclassing and implementing the {@link jakarta.json.bind.JsonbBuilder JsonbBuilder} creation
 * method(s) in {@link jakarta.json.bind.spi.JsonbProvider JsonbProvider}.
 *
 * <p>The API locates and loads providers using {@link java.util.ServiceLoader ServiceLoader}.
 *
 * @since JSON Binding 1.0
 */
package jakarta.json.bind.spi;
