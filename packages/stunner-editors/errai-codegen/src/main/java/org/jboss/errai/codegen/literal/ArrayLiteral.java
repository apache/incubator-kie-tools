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


package org.jboss.errai.codegen.literal;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * Renders an array back to it's canonical Java-based literal representation, assuming the contents
 * of the array can be represented as such.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ArrayLiteral extends LiteralValue<Object> {
  private final int dimensions;
  private final Class<?> arrayType;
  private final MetaClass mArrayType;
  
  public ArrayLiteral(final Object value) {
    super(value);

    Class<?> type = value.getClass();
    int dim = 0;
    while (type.isArray()) {
      dim++;
      type = type.getComponentType();
    }

    this.dimensions = dim;
    
    if (MetaClass.class.isAssignableFrom(type)) {
      type = Class.class;
    }
    else if (MetaType.class.isAssignableFrom(type)) {
      type = Type.class;
    }
    
    this.arrayType = type;

    mArrayType = MetaClassFactory.get(arrayType).asArrayOf(dim);
  }

  @Override
  public String getCanonicalString(final Context context) {
    final StringBuilder buf = new StringBuilder("new " +
            LoadClassReference.getClassReference(MetaClassFactory.get(arrayType), context));

    final Object val = getValue();

    for (int i = 0; i < dimensions; i++) {
      buf.append("[]");
    }
    buf.append(" ");
    buf.append(renderInlineArrayLiteral(context, val));

    return buf.toString();
  }

  private static String renderInlineArrayLiteral(final Context context, final Object arrayInstance) {
    final StringBuilder builder = new StringBuilder("{ ");

    final int length = Array.getLength(arrayInstance);

    Object element;

    for (int i = 0; i < length; i++) {
      element = Array.get(arrayInstance, i);

      if (element == null) {
        builder.append("null");
      }
      else if (element.getClass().isArray()) {
        builder.append(renderInlineArrayLiteral(context, element));

      }
      else {
        builder.append(GenUtil.generate(context, element).generate(context));
      }

      if (i + 1 < length) {
        builder.append(", ");
      }
    }

    return builder.append(" }").toString();
  }

  @Override
  public MetaClass getType() {
    return mArrayType;
  }
}
