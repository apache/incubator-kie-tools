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

package org.jboss.errai.codegen.meta.impl.build;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.codegen.Comment;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.Builder;
import org.jboss.errai.codegen.builder.impl.Scope;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BuildMetaConstructor extends MetaConstructor implements Builder {
  private final BuildMetaClass declaringClass;
  private Statement body;

  private boolean isVarArgs;
  private boolean isAbstract;
  private Scope scope;

  private DefParameters defParameters;
  private final List<MetaTypeVariable> typeVariables = new ArrayList<MetaTypeVariable>();

  private MetaConstructor reifiedFormOf;

  private String constructorComment;

  public BuildMetaConstructor(final BuildMetaClass declaringClass) {
    this.declaringClass = declaringClass;
  }

  public BuildMetaConstructor(final BuildMetaClass declaringClass,
                              final Statement body) {
    this.declaringClass = declaringClass;
    this.body = body;
  }

  public BuildMetaConstructor(final BuildMetaClass declaringClass,
                              final Statement body,
                              final DefParameters defParameters) {
    this(declaringClass, body);
    this.defParameters = defParameters;
  }

  public BuildMetaConstructor(final BuildMetaClass declaringClass,
                              final Statement body,
                              final Scope scope,
                              final DefParameters defParameters) {
    this(declaringClass, body, defParameters);
    this.scope = scope;
  }

  @Override
  public MetaParameter[] getParameters() {
    if (defParameters == null) {
      return new MetaParameter[0];
    }
    else {
      final List<MetaParameter> metaParameterList = new ArrayList<MetaParameter>();
      for (final Parameter p : defParameters.getParameters()) {

        metaParameterList.add(new MetaParameter() {
          @Override
          public String getName() {
            return p.getName();
          }

          @Override
          public MetaClass getType() {
            return p.getType();
          }

          @Override
          public MetaClassMember getDeclaringMember() {
            return BuildMetaConstructor.this;
          }

          @Override
          public Annotation[] getAnnotations() {
            return new Annotation[0];
          }

          @Override
          public boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
            return false;
          }

          @Override
          public <A extends Annotation> A getAnnotation(final Class<A> annotation) {
            return null;
          }
        });
      }
      return metaParameterList.toArray(new MetaParameter[metaParameterList.size()]);
    }
  }

  @Override
  public MetaType[] getGenericParameterTypes() {
    return typeVariables.toArray(new MetaTypeVariable[typeVariables.size()]);
  }

  @Override
  public boolean isVarArgs() {
    return isVarArgs;
  }

  @Override
  public MetaClass getDeclaringClass() {
    return declaringClass;
  }
  
  @Override
  public String getDeclaringClassName() {
    return declaringClass.getName();
  }

  @Override
  public boolean isAbstract() {
    return isAbstract;
  }

  @Override
  public boolean isPublic() {
    return scope == Scope.Public;
  }

  @Override
  public boolean isPrivate() {
    return scope == Scope.Private;
  }

  @Override
  public boolean isProtected() {
    return scope == Scope.Protected;
  }

  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public boolean isStatic() {
    return true;
  }

  @Override
  public boolean isTransient() {
    return false;
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @Override
  public boolean isVolatile() {
    return false;
  }

  @Override
  public boolean isSynchronized() {
    return false;
  }

  public void setScope(final Scope scope) {
    this.scope = scope;
  }

  @Override
  public Annotation[] getAnnotations() {
    return new Annotation[0];
  }

  @Override
  public MetaTypeVariable[] getTypeParameters() {
    return typeVariables.toArray(new MetaTypeVariable[typeVariables.size()]);
  }

  public Statement getBody() {
    return body;
  }

  public void setBody(final Statement body) {
    this.body = body;
  }

  public void setDefParameters(final DefParameters defParameters) {
    this.defParameters = defParameters;
  }


  @Override
  public MetaClass[] getCheckedExceptions() {
    return new MetaClass[0];
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public MetaClass getReturnType() {
    return this.declaringClass;
  }

  @Override
  public MetaType getGenericReturnType() {
    return this.declaringClass;
  }

  public boolean isReifiedForm() {
    return reifiedFormOf != null;
  }

  public MetaConstructor getReifiedFormOf() {
    return reifiedFormOf;
  }

  public void setReifiedFormOf(final MetaConstructor reifiedFormOf) {
    this.reifiedFormOf = reifiedFormOf;
  }

  public void setConstructorComment(final String constructorComment) {
    this.constructorComment = constructorComment;
  }

  String generatedCache;

  @Override
  public String toJavaString() {
    if (generatedCache != null) return generatedCache;

    final Context context = Context.create(declaringClass.getContext());
    defParameters.getParameters().stream().forEach(p -> context.addVariable(Variable.create(p.getName(), p.getType())));

    final StringBuilder build = new StringBuilder(512);
    if (constructorComment != null)  {
      build.append(new Comment(constructorComment).generate(null)).append('\n');
    }

    return generatedCache =
            build.append(scope.getCanonicalName())
            .append(" ")
            .append(declaringClass.getName())
            .append(defParameters.generate(context))
            .append(" {\n").append(body.generate(context)).append("\n}\n")
            .toString();
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof MetaConstructor && GenUtil.equals(this, (MetaConstructor) o);
  }
}
