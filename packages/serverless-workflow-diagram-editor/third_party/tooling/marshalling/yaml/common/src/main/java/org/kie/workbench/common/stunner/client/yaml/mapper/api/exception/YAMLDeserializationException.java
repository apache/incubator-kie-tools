/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.yaml.mapper.api.exception;

/** Base exception for deserialization process */
public class YAMLDeserializationException extends YAMLMappingException {

  /** Constructor for YAMLDeserializationException. */
  public YAMLDeserializationException() {}

  /**
   * Constructor for YAMLDeserializationException.
   *
   * @param message a {@link String} object.
   */
  public YAMLDeserializationException(String message) {
    super(message);
  }

  /**
   * Constructor for YAMLDeserializationException.
   *
   * @param message a {@link String} object.
   * @param cause a {@link Throwable} object.
   */
  public YAMLDeserializationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor for YAMLDeserializationException.
   *
   * @param cause a {@link Throwable} object.
   */
  public YAMLDeserializationException(Throwable cause) {
    super(cause);
  }
}
