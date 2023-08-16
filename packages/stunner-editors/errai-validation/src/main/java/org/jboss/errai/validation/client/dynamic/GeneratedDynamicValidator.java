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


package org.jboss.errai.validation.client.dynamic;

import java.util.Map;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;

/**
 * Implemented by generated validators that validate for a single {@link ConstraintValidator}.
 *
 * @param <T>
 *          The type of value validated by this generated validator.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface GeneratedDynamicValidator<T> {

  /**
   * @param parameters
   *          A map of values for all properties for the {@link Constraint} associated with this validator.
   * @param value
   *          The value to be validated.
   * @return An empty set if the given value is valid, or as set with one or more {@link ConstraintViolation
   *         ConstraintViolations} if given value is invalid.
   */
  Set<ConstraintViolation<T>> validate(Map<String, Object> parameters, T value);
  
}
