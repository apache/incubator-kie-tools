/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.enterprise.client.cdi;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.util.AnnotationPropertyAccessor;
import org.jboss.errai.common.client.util.SharedAnnotationSerializer;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class EventQualifierSerializer {

  public static final String SERIALIZER_CLASS_NAME = "EventQualifierSerializerImpl";
  public static final String SERIALIZER_PACKAGE_NAME = "org.jboss.errai.cdi";

  private static EventQualifierSerializer instance;

  public static boolean isSet() {
    return instance != null;
  }

  public static void set(final EventQualifierSerializer impl) {
    if (instance == null) {
      instance = Assert.notNull(impl);
    }
    else {
      throw new RuntimeException("Cannot call set more than once.");
    }
  }

  public static EventQualifierSerializer get() {
    return Assert.notNull("Cannot call " + EventQualifierSerializer.class.getSimpleName()
            + ".get() without first setting an instance.", instance);
  }

  protected final Map<String, AnnotationPropertyAccessor> serializers = new HashMap<>();

  protected EventQualifierSerializer() {
  }

  public String serialize(final Annotation qualifier) {
    final AnnotationPropertyAccessor entry = serializers.get(qualifier.annotationType().getName());
    return SharedAnnotationSerializer.serialize(qualifier, entry);
  }

  public String qualifierName(final String serializedQualifier) {
    if (serializedQualifier.contains("(")) {
      return serializedQualifier.substring(0, serializedQualifier.indexOf('('));
    }
    else {
      return serializedQualifier;
    }
  }

}
