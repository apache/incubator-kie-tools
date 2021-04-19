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

package org.jboss.errai.marshalling.server.marshallers;

import java.lang.reflect.Array;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJArray;
import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * @author Mike Brock
 */
public class DefaultArrayMarshaller implements Marshaller<Object> {
  private final MetaClass arrayType;
  private final Marshaller<Object> outerMarshaller;
  private final int dimensions;

  public DefaultArrayMarshaller(final MetaClass arrayType, final Marshaller<Object> outerMarshaller) {
    this.arrayType = Assert.notNull(arrayType);
    this.outerMarshaller = Assert.notNull("no outer marshaller specified for: " +
            arrayType.getOuterComponentType().getFullyQualifiedName(),
            outerMarshaller);

    Class<?> type = arrayType.asClass();
    int dim = 0;
    while (type.isArray()) {
      dim++;
      type = type.getComponentType();
    }

    this.dimensions = dim;
  }

  @SuppressWarnings("unchecked")
  public Class<Object> getTypeHandled() {
    return (Class<Object>) arrayType.asClass();
  }

  public Object demarshall(final EJValue a0, final MarshallingSession a1) {
    if (a0.isNull()) {
      return null;
    }
    else {
      final EJArray arr = a0.isArray();

      final int[] dims = new int[dimensions];
      dims[0] = arr.size();

      final Object arrayInstance = Array.newInstance(arrayType.getOuterComponentType().asClass(), dims);
      _demarshall(dimensions - 1, arrayInstance, arr, a1);
      return arrayInstance;
    }
  }

  @Override  
  public String marshall(final Object a0, final MarshallingSession a1) {
    if (a0 == null) {
      return null;
    }
    else {
      return _marshall(a0, a1);
    }
  }

  private Object _demarshall(final int dim, final Object arrayInstance, final EJArray a0, final MarshallingSession a1) {
    if (dim == 0) {
      for (int i = 0; i < a0.size(); i++) {
        Array.set(arrayInstance, i, outerMarshaller.demarshall(a0.get(i), a1));
      }
    }
    else {
      for (int i = 0; i < a0.size(); i++) {
        Array.set(arrayInstance, i, _demarshall(dim - 1,
                Array.newInstance(arrayType.getOuterComponentType().asClass(), a0.get(i).isArray().size()),
                a0.get(i).isArray(),
                a1));
      }
    }
    return arrayInstance;
  }

  private String _marshall(final Object a0, final MarshallingSession a1) {
    final StringBuilder builder = new StringBuilder("[");

    final int length = Array.getLength(a0);

    Object element;

    for (int i = 0; i < length; i++) {
      element = Array.get(a0, i);
      if (element != null && element.getClass().isArray()) {
        builder.append(_marshall(element, a1));
      }
      else {
        builder.append(outerMarshaller.marshall(element, a1));
      }

      if (i + 1 < length) {
        builder.append(',');
      }
    }

    return builder.append(']').toString();
  }

  @Override
  public Object[] getEmptyArray() {
    return (Object[]) Array.newInstance(arrayType.getOuterComponentType().asClass(), 0);
  }
}
