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


package org.jboss.errai.codegen;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.Stmt;

import static org.jboss.errai.codegen.util.PrettyPrinter.prettyPrintJava;

public class AnnotationEncoder {
  public static Statement encode(final Annotation annotation) {
    final Class<? extends Annotation> annotationClass = annotation.annotationType();

    return new Statement() {
      String generatedCache;

      @Override
      public String generate(final Context context) {
        if (generatedCache != null) return generatedCache;

        final AnonymousClassStructureBuilder builder
                = ObjectBuilder.newInstanceOf(annotationClass, context)
                .extend();

        final List<Method> sortedMethods = Arrays.asList(annotation.getClass().getDeclaredMethods());
        Collections.sort(sortedMethods, new Comparator<Method>() {
                    @Override
                    public int compare(final Method m1, final Method m2) {
                        return m1.getName().compareTo(m2.getName());
                    }
            
        });
        
        for (final Method method : sortedMethods) {
          if (((method.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED)) == 0)
                  && (!"equals".equals(method.getName()) && !"hashCode".equals(method.getName()))) {
            try {
              method.setAccessible(true);
              builder.publicOverridesMethod(method.getName())
                      .append(Stmt.load(method.invoke(annotation)).returnValue()).finish();
            }
            catch (IllegalAccessException e) {
              throw new RuntimeException("error generation annotation wrapper", e);
            }
            catch (InvocationTargetException e) {
              throw new RuntimeException("error generation annotation wrapper", e);
            }
          }
        }

        return generatedCache = prettyPrintJava(builder.finish().toJavaString());
      }

      @Override
      public MetaClass getType() {
        return MetaClassFactory.get(annotationClass);
      }
    };
  }
}
