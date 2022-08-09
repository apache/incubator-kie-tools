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

package jakarta.json.bind.config;

/**
 * Allows to define custom property naming strategy. Specifies predefined property naming
 * strategies. Does not override JsonbProperty value.
 *
 * <p>This strategy can be set via {@link jakarta.json.bind.JsonbConfig}.
 *
 * @see jakarta.json.bind.JsonbConfig
 * @since JSON Binding 1.0
 */
public interface PropertyNamingStrategy {
  /** Using this strategy, the property name is unchanged. */
  String IDENTITY = "IDENTITY";

  /**
   * Using this strategy, the property name is transformed to lower case with dashes. The dashes are
   * on the positions of different case boundaries in the original field name (camel case).
   */
  String LOWER_CASE_WITH_DASHES = "LOWER_CASE_WITH_DASHES";

  /**
   * Using this strategy, the property name is transformed to lower case with underscores. The
   * underscores are on the positions of different case boundaries in the original field name (camel
   * case).
   */
  String LOWER_CASE_WITH_UNDERSCORES = "LOWER_CASE_WITH_UNDERSCORES";

  /** Using this strategy, the first character will be capitalized. */
  String UPPER_CAMEL_CASE = "UPPER_CAMEL_CASE";

  /**
   * Using this strategy, the first character will be capitalized and the words will be separated by
   * spaces.
   */
  String UPPER_CAMEL_CASE_WITH_SPACES = "UPPER_CAMEL_CASE_WITH_SPACES";

  /**
   * Using this strategy, the serialization will be same as identity. Deserialization will be case
   * insensitive. E.g. property in JSON with name PropertyNAME, will be mapped to field
   * propertyName.
   */
  String CASE_INSENSITIVE = "CASE_INSENSITIVE";

  /**
   * Translates the property name into its JSON field name representation.
   *
   * @param propertyName Name of the property to translate.
   * @return Translated JSON field name.
   */
  String translateName(String propertyName);
}
