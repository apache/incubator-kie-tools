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

package jakarta.json.bind.config;

/**
 * Specifies predefined binary data handling strategies. This strategy can be set via {@link
 * jakarta.json.bind.JsonbConfig#withBinaryDataStrategy(String)}.
 *
 * @see jakarta.json.bind.JsonbConfig
 * @since JSON Binding 1.0
 */
public final class BinaryDataStrategy {

  /** Private constructor to disallow instantiation. */
  private BinaryDataStrategy() {};

  /** Using this strategy, binary data is encoded as a byte array. Default encoding strategy. */
  public static final String BYTE = "BYTE";

  /**
   * Using this strategy, binary data is encoded using the Base64 encoding scheme as specified in
   * RFC 4648 and RFC 2045.
   */
  public static final String BASE_64 = "BASE_64";

  /**
   * Using this strategy, binary data is encoded using the "URL and Filename safe Base64 Alphabet"
   * as specified in Table 2 of RFC 4648.
   */
  public static final String BASE_64_URL = "BASE_64_URL";
}
