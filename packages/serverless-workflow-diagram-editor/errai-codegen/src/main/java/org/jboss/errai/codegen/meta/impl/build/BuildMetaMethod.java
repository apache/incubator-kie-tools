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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Comment;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.DefModifiers;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.ThrowsDeclaration;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.Builder;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.builder.impl.Scope;
import org.jboss.errai.codegen.literal.AnnotationLiteral;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BuildMetaMethod extends MetaMethod implements Builder {
  private BuildMetaClass declaringClass;
  private BlockStatement body;

  private Scope scope;
  private final DefModifiers modifiers;

  private String name;
  private MetaClass returnType;
  private DefParameters defParameters;

  private List<MetaType> genericParameterTypes;

  private ThrowsDeclaration throwsDeclaration;

  private MetaMethod reifiedFormOf;

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  private String methodComment;

  public BuildMetaMethod(final BuildMetaClass declaringClass,
                         final BlockStatement body,
                         final Scope scope,
                         final DefModifiers modifiers,
                         final String name,
                         final MetaClass returnType,
                         final DefParameters defParameters,
                         final ThrowsDeclaration throwsDeclaration
  ) {

    this.declaringClass = declaringClass;
    this.body = body;
    this.modifiers = modifiers;
    this.scope = scope;
    this.name = name;
    this.returnType = returnType;
    this.defParameters = defParameters;
    this.throwsDeclaration = throwsDeclaration;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public MetaClass getReturnType() {
    return returnType;
  }

  // don't try to support this for now.
  @Override
  public MetaType getGenericReturnType() {
    return null;
  }

  @Override
  public MetaType[] getGenericParameterTypes() {
    if (genericParameterTypes == null)
      return new MetaType[0];

    return genericParameterTypes.toArray(new MetaType[genericParameterTypes.size()]);
  }

  @Override
  public MetaParameter[] getParameters() {
    final List<Parameter> parameters = defParameters.getParameters();
    if (parameters != null) {
      final List<MetaParameter> metaParameterList = new ArrayList<MetaParameter>();
      for (final Parameter p : parameters) {
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
            return BuildMetaMethod.this;
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
    else {
      return new MetaParameter[0];
    }
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
    return modifiers.hasModifier(Modifier.Abstract);
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
    return modifiers.hasModifier(Modifier.Final);
  }

  @Override
  public boolean isStatic() {
    return modifiers.hasModifier(Modifier.Static);
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
    return modifiers.hasModifier(Modifier.Synchronized);
  }

  public void addAnnotations(final Annotation... annotations) {
    addAnnotations(Arrays.asList(annotations));
  }

  public void addAnnotations(final Collection<Annotation> annotations) {
    annotations.forEach(a -> this.annotations.add(a));
  }

  @Override
  public Annotation[] getAnnotations() {
    return annotations.toArray(new Annotation[annotations.size()]);
  }

  @Override
  public MetaTypeVariable[] getTypeParameters() {
    return new MetaTypeVariable[0];
  }

  @Override
  public MetaClass[] getCheckedExceptions() {
    return throwsDeclaration.getExceptionTypes();
  }

  public void setDeclaringClass(final BuildMetaClass declaringClass) {
    this.declaringClass = declaringClass;
  }

  public void setScope(final Scope scope) {
    this.scope = scope;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setReturnType(final MetaClass returnType) {
    this.returnType = returnType;
  }

  public void setGenericReturnType(final MetaType genericReturnType) {
    //   this.genericReturnType = genericReturnType;
  }

  public void setGenericParameterTypes(final List<MetaType> genericParameterTypes) {
    this.genericParameterTypes = genericParameterTypes;
  }

  public void setBody(final BlockStatement body) {
    this.body = body;
  }

  public void setDefParameters(final DefParameters defParameters) {
    this.defParameters = defParameters;
  }

  public void setThrowsDeclaration(final ThrowsDeclaration throwsDeclaration) {
    this.throwsDeclaration = throwsDeclaration;
  }

  public BlockStatement getBody() {
    return body;
  }

  @Override
  public boolean isVarArgs() {
    return false;
  }

  public boolean isReifiedForm() {
    return reifiedFormOf != null;
  }

  public MetaMethod getReifiedFormOf() {
    return reifiedFormOf;
  }

  public void setReifiedFormOf(final MetaMethod reifiedFormOf) {
    this.reifiedFormOf = reifiedFormOf;
  }

  public void setMethodComment(final String methodComment) {
    this.methodComment = methodComment;
  }

  @Override
  public String toJavaString() {
    final Context context = Context.create(declaringClass.getContext());

    for (final Parameter p : defParameters.getParameters()) {
      context.addVariable(Variable.create(p.getName(), p.getType()));
    }
    final StringBuilder buf = new StringBuilder(256);

    if (!annotations.isEmpty()) {
      for (final Annotation a : getAnnotations()) {
        buf.append(new AnnotationLiteral(a).getCanonicalString(context)).append(" ");
      }
      buf.append("\n");
    }

    if (methodComment != null) {
      buf.append(new Comment(methodComment).generate(null)).append("\n");
    }

    buf.append(scope.getCanonicalName()).append(" ")
            .append(modifiers.toJavaString()).append(" ");

    buf.append(LoadClassReference.getClassReference(returnType, context, false))
            .append(" ")
            .append(name)
            .append(defParameters.generate(context));

    if (!throwsDeclaration.isEmpty()) {
      buf.append(" ")
              .append(throwsDeclaration.generate(context));
    }

    if (modifiers.hasModifier(Modifier.Abstract) || getDeclaringClass().isInterface()) {
      buf.append(";");
    }
    else if (modifiers.hasModifier(Modifier.JSNI)) {
      buf.append(" /*-{\n").append(body.generate(context)).append("\n}-*/;\n");
    }
    else {
      buf.append(" {\n").append(body.generate(context)).append("\n}\n");
    }

    return buf.toString();
  }

  @Override
  public String toString() {
    return name + defParameters;
  }
}
