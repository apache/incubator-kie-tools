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


package org.jboss.errai.common.client.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that instances of the annotated class are eligible to be serialized
 * and sent over the wire between server and clients.
 * <p>
 *
 * @since Errai 2.0
 * @author Mike Brock <cbrock@redhat.com>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Portable {
  /**
   * Indicate that the annotated class should be treated as an alias of an existing marshalling mapping, and should
   * not be directly mapped itself.
   * @return
   */
  Class<?> aliasOf() default Object.class;

  /*
   * Indicate that concrete supertypes of the annotated class should have marshalling mappings generated as if marked
   * portable.
   */
  boolean mapSuperTypes() default false;
}
