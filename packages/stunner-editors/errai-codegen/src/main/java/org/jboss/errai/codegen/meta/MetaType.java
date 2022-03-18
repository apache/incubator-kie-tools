/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.codegen.meta;

/**
 * Umbrella type for anything that could be used as a type in Java code:
 * MetaClass, MetaParameterizedType, MetaTypeVariable, MetaWildcardType, and
 * MetaGenericArrayType all implement this interface.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public interface MetaType {

  /**
   * Returns the name of this type as it was written in the original source code.
   *
   * @return The name of this type as it was written in the original source code.
   */
  public String getName();
}
