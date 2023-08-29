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


package org.jboss.errai.codegen.exception;

/**
 * Base class for failures in the Errai code generation module.
 * <p>
 * Note to users of this class: instead of throwing an instance of this base
 * class, please consider reusing one of its subtypes, or create a new subtype
 * if none of the existing ones are suitable.
 *
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class GenerationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private String additionalFailureInfo = "";

  /**
   * Creates a GenerationException with no message and no cause.
   */
  public GenerationException() {
    super();
  }

  /**
   * Creates a GenerationException with the given message and no cause.
   */
  public GenerationException(String msg) {
    super(msg);
  }

  /**
   * Creates a GenerationException with no message and the given cause.
   */
  public GenerationException(Throwable t) {
    super(t);
  }

  /**
   * Creates a GenerationException with the given message and the given cause.
   */
  public GenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Adds the given message to this exception. All additional failure
   * information added this way is returned by {@link #getMessage()}.
   *
   * @param info The additional information to add.
   */
  public void appendFailureInfo(String info) {
    additionalFailureInfo += "\n" + info;
  }

  /**
   * Returns this exception's message (as given in the constructor) plus all of
   * the additional failure information, separated by newlines.
   *
   * @see GenerationException#appendFailureInfo(String)
   */
  @Override
  public String getMessage() {
    return super.getMessage() + additionalFailureInfo;
  }
}
