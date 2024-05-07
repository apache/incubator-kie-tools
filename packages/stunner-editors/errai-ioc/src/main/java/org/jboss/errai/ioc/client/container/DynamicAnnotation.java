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

package org.jboss.errai.ioc.client.container;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * A representation of an {@link Annotation} that was not present at
 * compile-time of this script.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface DynamicAnnotation extends Annotation {

  static DynamicAnnotation create(final String serialized) {
    return DynamicAnnotationImpl.create(serialized);
  }

  /**
   * Returns the fully qualified name of the annotation.
   *
   * @return fqcn of annotation, never null.
   */
  String getName();

  /**
   * Returns a map of member names to values for this annotation instance.
   *
   * @return map of members if present, otherwise an empty map.
   */
  Map<String, String> getMembers();
  
  /**
   * Returns the annotation member with the given name. 
   * 
   * @param name of the member, must not be null.
   * @return String representation of the member value, null if member doesn't exist.
   */
  default String getMember(final String name) {
    return getMembers().get(name);
  }
}
