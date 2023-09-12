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


package org.jboss.errai.codegen.builder.impl;

import java.lang.reflect.Array;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ArrayBuilder;
import org.jboss.errai.codegen.builder.ArrayInitializationBuilder;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * StatementBuilder to create and initialize Arrays.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock
 */
public class ArrayBuilderImpl extends AbstractStatementBuilder implements ArrayBuilder, ArrayInitializationBuilder {
  private MetaClass type;
  private MetaClass componentType;
  private Object[] dimensions;
  private Object values = null;

  protected ArrayBuilderImpl(Context context, CallElementBuilder callElementBuilder) {
    super(context, callElementBuilder);
  }

  @Override
  public ArrayInitializationBuilder newArray(MetaClass componentType, Object... dimensions) {
    this.type = componentType.asArrayOf(dimensions.length == 0 ? 1 : dimensions.length);
    this.componentType = componentType;
    this.dimensions = dimensions;
    return this;
  }

  @Override
  public ArrayInitializationBuilder newArray(Class<?> componentType, Object... dimensions) {
    return newArray(MetaClassFactory.get(componentType), dimensions);
  }

  @Override
  public AbstractStatementBuilder initialize(Object... values) {
    if (values.length == 1 && values[0].getClass().isArray()
            && values.getClass().getComponentType().equals(Object.class)) {
      // this is a workaround for the jdt compiler which is coercing a multi-dimensional array
      // into the first element of our vararg instead of flattening it out (like javac does).
      this.values = values[0];
    }
    else {
      this.values = values;
    }

    return this;
  }

  @Override
  public MetaClass getType() {
    return type;
  }

  @Override
  public String generate(Context context) {
    if (generatorCache != null) return generatorCache;

    StringBuilder buf = new StringBuilder(128);
    buf.append("new ").append(LoadClassReference.getClassReference(componentType, context));

    if (values != null) {
      generateWithInitialization(context, buf);
    }
    else {
      int i = 0;

      for (Object dimension : dimensions) {
        try {
          if (dimension == null) {
            if (i == 0) {
              i--;
              break;
            }
            else {
              buf.append("[]");
            }
          }
          else {
            buf.append("[").append(GenUtil.generate(context, dimension).generate(context)).append("]");
          }
        }
        finally {
          i++;
        }
      }

      if (i == 0) {
        throw new RuntimeException("Must provide either dimension expressions or an array initializer");
      }
    }

    return generatorCache = buf.toString();
  }

  private void generateWithInitialization(Context context, StringBuilder buf) {
    int dim = 0;
    Class<?> type = values.getClass();
    while (type.isArray()) {
      dim++;
      type = type.getComponentType();
    }

    for (int i = 0; i < dim; i++) {
      buf.append("[]");
    }
    buf.append(" ");

    generateInitialization(context, buf, values);
  }

  private void generateInitialization(Context context, StringBuilder buf, Object values) {
    buf.append("{ ");
    int length = Array.getLength(values);
    for (int i = 0; i < length; i++) {
      Object element = Array.get(values, i);
      if (element.getClass().isArray()) {
        generateInitialization(context, buf, element);
      }
      else {
        Statement statement = GenUtil.generate(context, element);
        String statementExpr = statement.generate(context);
        GenUtil.assertAssignableTypes(context, statement.getType(), componentType);
        buf.append(statementExpr);
      }
      if (i + 1 < length) {
        buf.append(", ");
      }
    }
    buf.append(" }");
  }
}
