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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Mike Brock
 */
public class AnnotationLiteral extends LiteralValue<Annotation> {

  public AnnotationLiteral(final Annotation value) {
    super(value);
  }

  @Override
  public String getCanonicalString(final Context context) {
    final Class<? extends Annotation> annotationClass = getValue().annotationType();
    final StringBuilder builder = new StringBuilder();

    builder.append("@").append(LoadClassReference.getClassReference(MetaClassFactory.get(annotationClass), context));

    final List<Method> sortedMethods = Arrays.asList(annotationClass.getDeclaredMethods());
    Collections.sort(sortedMethods, new Comparator<Method>() {
      @Override
      public int compare(final Method m1, final Method m2) {
        return m1.getName().compareTo(m2.getName());
      }

    });

    final List<String> elements = new ArrayList<String>();

    String lastMethodRendered = "";
    String lastValueRendered;
    Set<String> enumTypes = new HashSet<String>();

    for (final Method method : sortedMethods) {
      if (((method.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED)) == 0)
              && (!"equals".equals(method.getName()) && !"hashCode".equals(method.getName()))) {
        method.setAccessible(true);
        lastMethodRendered = method.getName();
        lastValueRendered = getLiteral(context, method);
        elements.add(lastMethodRendered + " = " + lastValueRendered);
        if (method.getReturnType().isEnum()) {
          enumTypes.add(method.getReturnType().getSimpleName());
        }
      }
    }

    final Iterator<String> els = elements.iterator();
    if (els.hasNext()) {
      builder.append("(");
    }
    while (els.hasNext()) {
      builder.append(els.next());
      if (els.hasNext()) builder.append(", ");
      else builder.append(")");
    }

    String toReturn = builder.toString().replaceAll("new (String|int|long|float|double|boolean|byte|short|char|Class)\\[\\]", "");
    for (String enumType : enumTypes) {
      toReturn = toReturn.replaceAll("new " + enumType + "\\[\\]", "");
    }

    if (elements.size() == 1 && "value".endsWith(lastMethodRendered)) {
      toReturn = toReturn.replaceFirst("(\\s)*value =(\\s)+", "");
    }

    return toReturn;
  }

  private String getLiteral(Context context, Method method) {
    try {
      Class<?> methodType = method.getReturnType();
      Object methodValue = method.invoke(getValue());

      if (method.getReturnType().isArray()) {
        return getArrayLiteral(context, methodType, methodValue);
      } else {
        return getNonArrayLiteral(context, methodType, methodValue);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("error generation annotation wrapper", e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("error generation annotation wrapper", e);
    }
  }

  private String getArrayLiteral(Context context, Class<?> type, Object obj) {
    if (Array.getLength(obj) == 1) {
      return getNonArrayLiteral(context, type.getComponentType(), Array.get(obj, 0));
    } else if (type.getComponentType().isAnnotation()) {
      String result = "{ ";
      Annotation[] annotations = (Annotation[]) obj;
      for (int i = 0; i < annotations.length; i++) {
        result += getNonArrayLiteral(context, type.getComponentType(), annotations[i]);
        if ((i + 1) != annotations.length) {
          result += ", ";
        }
      }
      result += " }";
      return result;
    } else {
      return LiteralFactory.getLiteral(obj).getCanonicalString(context);
    }
  }

  private String getNonArrayLiteral(Context context, Class<?> type, Object obj) {
    if (type.isAnnotation()) {
      return new AnnotationLiteral((Annotation) obj).getCanonicalString(context);
    } else {
      return LiteralFactory.getLiteral(obj).getCanonicalString(context);
    }
  }
}
