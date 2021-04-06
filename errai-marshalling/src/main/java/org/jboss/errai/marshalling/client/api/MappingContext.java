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

package org.jboss.errai.marshalling.client.api;

/**
 * @author Mike Brock
 */
public interface MappingContext {

  /**
   * Returns the Marshaller instance that can handle the given type.
   *
   * @param clazz
   *          fully qualified class name of the type to be marshalled, in the
   *          format returned by {@link java.lang.Class#getName()} and
   *          {@link org.jboss.errai.codegen.meta.MetaClass#getFullyQualifiedName()}.
   *          Null is permitted, and yields a marshaller that can only marshal
   *          and demarshal null references.
   * @return a marshaller instance that can handle the given type, or null if
   *         the type can't be handled in this mapping context.
   */
  public Marshaller<Object> getMarshaller(String clazz);

  public boolean hasMarshaller(String clazzName);

  /**
   * Indicates whether or not the specified class can be marshalled, whether or not a definition exists.
   *
   * @return boolean true if marshallable.
   */
  public boolean canMarshal(String cls);

}
