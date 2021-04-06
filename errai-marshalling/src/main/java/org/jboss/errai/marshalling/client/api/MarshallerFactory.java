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
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface MarshallerFactory {
  /**
   * Returns a {@link Marshaller} capable of handling the specified encodedType
   * 
   * @param encodedType
   *          The fully-qualified Java class name of the encoded type
   * 
   * @return a marshaller instance.
   */
  Marshaller<Object> getMarshaller(String encodedType);

  /**
   * Registers a {@link Marshaller} for the type with the provided fully qualified class name.
   * 
   * @param fqcn
   *          the fully qualified type name of the marshallable type.
   * @param marshaller
   *          the marshaller instance
   */
  void registerMarshaller(String fqcn, Marshaller<Object> marshaller);
}
