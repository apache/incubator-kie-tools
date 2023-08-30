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


package org.jboss.errai.ioc.client.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import jsinterop.annotations.JsType;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A qualifier indicating that the injected type should be satisfied by a
 * {@link JsType} instance provided by a a separately compiled script if
 * available.
 *
 * This is useful when you have a locally defined type that implements a
 * {@link JsType} interface. In this case adding {@link Shared} to an injection
 * site ensures that an external implementation is used (if one exists) rather
 * than the local non-JsType one.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
public @interface Shared {

}
