/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.yaml.mapper.api.exception;

/** Base exception for mapping process */
public class YAMLMappingException extends RuntimeException {

  /** Constructor for YAMLMappingException. */
  public YAMLMappingException() {}

  /**
   * Constructor for YAMLMappingException.
   *
   * @param message a {@link String} object.
   */
  public YAMLMappingException(String message) {
    super(message);
  }

  /**
   * Constructor for YAMLMappingException.
   *
   * @param message a {@link String} object.
   * @param cause a {@link Throwable} object.
   */
  public YAMLMappingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor for YAMLMappingException.
   *
   * @param cause a {@link Throwable} object.
   */
  public YAMLMappingException(Throwable cause) {
    super(cause);
  }
}
