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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.DefModifiers;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.ThrowsDeclaration;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.MethodBlockBuilder;
import org.jboss.errai.codegen.builder.MethodBuildCallback;
import org.jboss.errai.codegen.builder.MethodCommentBuilder;
import org.jboss.errai.codegen.literal.LiteralFactory;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock <cbrock@redhat.com>
 */
public class MethodBlockBuilderImpl<T> extends BlockBuilderImpl<T>
        implements MethodCommentBuilder<T> {

  protected String methodComment;
  protected ThrowsDeclaration throwsDeclaration = ThrowsDeclaration.none();
  protected final MethodBuildCallback<T> callback;
  protected DefParameters defParameters;
  protected final DefModifiers modifiers = new DefModifiers();
  protected final List<Annotation> annotations = new ArrayList<Annotation>();

  public MethodBlockBuilderImpl(final MethodBuildCallback<T> callback) {
    super(null);
    this.callback = callback;
  }

  @Override
  public MethodBlockBuilder<T> methodComment(final String comment) {
    methodComment = comment;
    return this;
  }

  @Override
  public MethodBlockBuilder<T> annotatedWith(final Annotation... annotations) {
    Arrays.stream(annotations).forEach(a -> this.annotations.add(a));
    return this;
  }

  @Override
  public MethodBlockBuilder<T> throws_(final Class<? extends Throwable>... exceptionTypes) {
    throwsDeclaration = ThrowsDeclaration.of(exceptionTypes);
    return this;
  }

  @Override
  public MethodBlockBuilder<T> throws_(final MetaClass... exceptions) {
    throwsDeclaration = ThrowsDeclaration.of(exceptions);
    return this;
  }


  @Override
  public MethodBlockBuilder<T> modifiers(final Modifier... modifiers) {
    for (final Modifier m : modifiers) {
      switch (m) {
        case Transient:
        case Volatile:
          throw new RuntimeException("illegal modifier for method: " + m);

        default:
          this.modifiers.addModifiers(m);
      }
    }

    return this;
  }

  @Override
  public MethodBlockBuilder<T> parameters(final DefParameters parms) {
    defParameters = parms;
    return this;
  }

  @Override
  public MethodBlockBuilder<T> parameters(final Class<?>... parms) {
    defParameters = DefParameters.fromTypeArray(MetaClassFactory.fromClassArray(parms)) ;
    return this;
  }

  @Override
  public MethodBlockBuilder<T> parameters(final MetaClass... parms) {
    defParameters = DefParameters.fromTypeArray(parms);
    return this;
  }

  @Override
  public MethodBlockBuilder<T> parameters(final Object... parms) {
    final List<MetaClass> p = new ArrayList<MetaClass>();
    for (final Object o : parms) {
      LiteralFactory.getLiteral(o);

      if (o instanceof MetaClass) {
        p.add((MetaClass) o);
      }
      else if (o instanceof Class<?>) {
        p.add(MetaClassFactory.get((Class<?>) o));
      }
      else if (o instanceof TypeLiteral) {
        p.add(MetaClassFactory.get((TypeLiteral) o));
      }
    }

    defParameters = DefParameters.fromTypeArray(p.toArray(new MetaClass[p.size()]));

    return this;
  }

  @Override
  public BlockBuilder<T> body() {
    return this;
  }

  @Override
  public T finish() {
    if (callback != null) {
      return callback.callback(blockStatement, defParameters, modifiers, throwsDeclaration, annotations, methodComment);
    }
    return null;
  }
}
