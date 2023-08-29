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
 * Thrown when a LiteralValue is requested for an object that cannot be
 * represented as a LiteralValue.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public class NotLiteralizableException extends GenerationException {

  private final Object nonLiteralizableObject;

  /**
   * Creates a new NotLiteralizableException that arose as a result of
   * requesting a LiteralValue of the given object.
   *
   * @param nonLiteralizableObject
   *          the object that is not literalizable.
   */
  public NotLiteralizableException(Object nonLiteralizableObject) {
    super("Not literalizable: " +
          (nonLiteralizableObject == null ? "null" : nonLiteralizableObject.toString()) +
          " (of type " + (nonLiteralizableObject == null ? "void" : nonLiteralizableObject.getClass().getName()) + ")");
    this.nonLiteralizableObject = nonLiteralizableObject;
  }

  /**
   * Returns the object that could not be literalized.
   *
   * @return The object that could not be literalized.
   */
  public Object getNonLiteralizableObject() {
    return nonLiteralizableObject;
  }
}
