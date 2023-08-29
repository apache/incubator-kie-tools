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


package org.jboss.errai.enterprise.client.cdi.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Identifies an annotated event type is a conversational type. Meaning, that the annotated event must not result
 * in a propagation to other event observation methods outside the current session context.
 * <p>
 * The annotated class must also be annotated with {@link org.jboss.errai.common.client.api.annotations.Portable} in
 * addition to being marked conversational, in order for the event to be transmittable across buses.
 * <p>
 * Events marked conversational are <em>always</em> conversational and may not never be used for broadcasting.
 * <p>
 * Unlike {@code @Portable}, this annotation is not inherited by subclasses. Each conversational event type must be
 * explicitly marked with this annotation.
 *
 * @author Mike Brock
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Documented
public @interface Conversational {}
