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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the annotated method should be called by the framework once
 * the local ErraiBus has completed initialization and has federated with the
 * remote server bus.
 * <p>
 * Methods annotated with {@code @AfterInitialization} must observe the
 * following contract:
 * <ol>
 * <li>The annotated method may be public, protected or private.</li>
 * <li>The method must NOT be static.</li>
 * <li>The method must NOT accept any parameters.</li>
 * <li>The return type MUST be void.</li>
 * <li>Execution order of <tt>AfterInitialization</tt> annotated methods
 * relative to each other is NOT guaranteed in any way.</tt></li>
 * </ol>
 *
 * @author Mike Brock
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterInitialization {
}
