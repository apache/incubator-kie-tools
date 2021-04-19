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
 */
public interface MarshallingSession {
  public MappingContext getMappingContext();

  /**
   * Returns a marshaller for the provided type.
   *
   * @param fqcn
   *          fully qualified class name of the type to be marshalled, in the
   *          format returned by {@link java.lang.Class#getName()} and
   *          {@link org.jboss.errai.codegen.meta.MetaClass#getFullyQualifiedName()}.
   *          Null is permitted, and yields a marshaller that can only marshal
   *          and demarshal null references.
   *
   * @return marshaller instance, or null if no marshaller was found for the
   *         given type.
   */
  public Marshaller<Object> getMarshallerInstance(String fqcn);

  public String determineTypeFor(String formatType, Object o);

  /**
   * Records a new object to the session with the specified <tt>hashCode</tt> identifier.
   *
   * @param hashCode
   *          a unique identifier
   * @param instance
   *          the instance of the entity.
   */
  public <T> T recordObject(String hashCode, T instance);

  /**
   * Checks if the object is already in the context based on the object reference.
   *
   * @param reference
   *          the entity reference
   * @return true if the session contains the object reference.
   */
  public boolean hasObject(Object reference);

  /**
   * Checks if the object is already in the context based on the hash code.
   *
   * @param hashCode
   *          the hash code
   * @return true if the session contains the object reference.
   */
  public boolean hasObject(String hashCode);

  /**
   * Returns a unique identifier for the specified object reference. Returns a new identifier if the object is unknown
   * to the session, or returns the existing one if it is known.
   *
   * @param reference
   *          the entity reference
   * @return a new or existing identifier within this session
   */
  public String getObject(Object reference);

  /**
   * Looks up the object based on the specified <tt>hashCode</tt> identifier. Returns null if the specified identifier
   * does not exist.
   *
   * @param type
   *          the type of entity being looked up
   * @param hashCode
   *          the identifier of the entity within the session
   * @param <T>
   *          the type of entity being looked up
   * @return the instance of the entity or null if not present
   */
  public <T> T getObject(Class<T> type, String hashCode);

  public String getAssumedElementType();

  public void setAssumedElementType(String assumedElementType);

  public String getAssumedMapKeyType();

  public void setAssumedMapKeyType(String assumedMapKeyType);

  public String getAssumedMapValueType();

  public void setAssumedMapValueType(String assumedMapValueType);

  public void resetAssumedTypes();
}
