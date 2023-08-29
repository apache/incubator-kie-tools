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


package org.jboss.errai.codegen.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.UnsafeNativeLong;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.StringStatement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.MethodBlockBuilder;
import org.jboss.errai.codegen.builder.MethodCommentBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;

/**
 * @author Mike Brock
 */
public class GWTPrivateMemberAccessor implements PrivateMemberAccessor {

  /**
   * Annotation instance that can be passed to the code generator when generating long accessors.
   */
  private static final UnsafeNativeLong UNSAFE_NATIVE_LONG_ANNOTATION = new UnsafeNativeLong() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return UnsafeNativeLong.class;
    }
  };

  /**
   * A reusable empty annotation array.
   */
  private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

  @Override
  public void createWritableField(final MetaClass type,
                                  final ClassStructureBuilder<?> classBuilder,
                                  final MetaField field,
                                  final Modifier[] modifiers)  {

    final MethodCommentBuilder<? extends ClassStructureBuilder<?>> methodBuilder =
            classBuilder.packageMethod(void.class, PrivateAccessUtil.getPrivateFieldAccessorName(field));

    if (type.getCanonicalName().equals("long")) {
      methodBuilder.annotatedWith(UNSAFE_NATIVE_LONG_ANNOTATION);
    }

    if (!field.isStatic()) {
      methodBuilder
              .parameters(DefParameters.fromParameters(Parameter.of(field.getDeclaringClass().getErased(), "instance"),
                      Parameter.of(type, "value")));
    } else {
      methodBuilder
              .parameters(DefParameters.fromParameters(Parameter.of(type, "value")));
    }

    methodBuilder.modifiers(appendJsni(modifiers))
            .body()
            ._(StringStatement.of(JSNIUtil.fieldAccess(field) + " = value"))
            .finish();
  }

  @Override
  public void createReadableField(final MetaClass type,
                                  final ClassStructureBuilder<?> classBuilder,
                                  final MetaField field,
                                  final Modifier[] modifiers) {

    final MethodBlockBuilder<? extends ClassStructureBuilder<?>> instance =
            classBuilder.packageMethod(type, PrivateAccessUtil.getPrivateFieldAccessorName(field));

    if (!field.isStatic()) {
      instance.parameters(DefParameters.fromParameters(Parameter.of(field.getDeclaringClass().getErased(), "instance")));
    }

    if (type.getCanonicalName().equals("long")) {
      instance.annotatedWith(UNSAFE_NATIVE_LONG_ANNOTATION);
    }

    instance.modifiers(appendJsni(modifiers))
            .body()
            ._(StringStatement.of("return " + JSNIUtil.fieldAccess(field)))
            .finish();
  }

  @Override
  public void makeMethodAccessible(final ClassStructureBuilder<?> classBuilder,
                                   final MetaMethod method,
                                   final Modifier[] modifiers) {

    final MetaMethod erasedMethod = method.getDeclaringClass().getErased().getDeclaredMethod(method.getName(),
            getErasedParamterTypes(method));

    final List<Parameter> wrapperDefParms = new ArrayList<Parameter>();

    if (!erasedMethod.isStatic()) {
      wrapperDefParms.add(Parameter.of(erasedMethod.getDeclaringClass().getErased(), "instance"));
    }

    final List<Parameter> methodDefParms = DefParameters.from(erasedMethod).getParameters();
    wrapperDefParms.addAll(methodDefParms);

    Annotation[] annotations = NO_ANNOTATIONS;
    for (MetaParameter p : erasedMethod.getParameters()) {
      if (p.getType().getCanonicalName().equals("long")) {
        annotations = new Annotation[] { UNSAFE_NATIVE_LONG_ANNOTATION };
      }
    }
    if (erasedMethod.getReturnType().getCanonicalName().equals("long")) {
      annotations = new Annotation[] { UNSAFE_NATIVE_LONG_ANNOTATION };
    }

    classBuilder.publicMethod(erasedMethod.getReturnType(), PrivateAccessUtil.getPrivateMethodName(method))
            .annotatedWith(annotations)
            .parameters(DefParameters.fromParameters(wrapperDefParms))
            .modifiers(appendJsni(modifiers))
            .body()
            ._(StringStatement.of(JSNIUtil.methodAccess(erasedMethod)))
            .finish();
  }

  private MetaClass[] getErasedParamterTypes(final MetaMethod method) {
    final MetaClass[] paramTypes = new MetaClass[method.getParameters().length];
    for (int i = 0; i < paramTypes.length; i++) {
      paramTypes[i] = method.getParameters()[i].getType().getErased();
    }

    return paramTypes;
  }

  @Override
  public void makeConstructorAccessible(final ClassStructureBuilder<?> classBuilder,
                                        final MetaConstructor constructor) {

    final DefParameters methodDefParms = DefParameters.from(constructor);

    Annotation[] annotations = NO_ANNOTATIONS;
    for (MetaParameter p : constructor.getParameters()) {
      if (p.getType().getCanonicalName().equals("long")) {
        annotations = new Annotation[] { UNSAFE_NATIVE_LONG_ANNOTATION };
      }
    }

    classBuilder.publicMethod(constructor.getReturnType(), PrivateAccessUtil.getPrivateMethodName(constructor))
            .annotatedWith(annotations)
            .parameters(methodDefParms)
            .modifiers(Modifier.Static, Modifier.JSNI)
            .body()
            ._(StringStatement.of(JSNIUtil.methodAccess(constructor)))
            .finish();
  }

  /**
   * Returns a new array consisting of a copy of the given array, plus
   * Modifiers.JSNI as the last element.
   *
   * @param modifiers
   *         The array to copy. May be empty, but must not be null.
   *
   * @return An array of length {@code n + 1}, where {@code n} is the length of
   *         the given array. Positions 0..n-1 correspond with the respective
   *         entries in the given array, and position n contains Modifiers.JSNI.
   */
  public static Modifier[] appendJsni(Modifier[] modifiers) {
    final Modifier[] origModifiers = modifiers;
    modifiers = new Modifier[origModifiers.length + 1];
    System.arraycopy(origModifiers, 0, modifiers, 0, origModifiers.length);
    modifiers[modifiers.length - 1] = Modifier.JSNI;
    return modifiers;
  }
}
