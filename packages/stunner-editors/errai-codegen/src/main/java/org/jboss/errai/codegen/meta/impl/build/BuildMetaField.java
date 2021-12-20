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
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.Builder;
import org.jboss.errai.codegen.builder.impl.Scope;
import org.jboss.errai.codegen.literal.AnnotationLiteral;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaType;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class BuildMetaField extends MetaField implements Builder {
  private BuildMetaClass declaringClass;
  private Statement statement;

  private Scope scope;

  private MetaClass type;
  private MetaType genericType;
  private String name;

  private boolean isFinal;
  private boolean isStatic;
  private boolean isTransient;
  private boolean isVolatile;

  private String fieldComment;

  private final List<Annotation> annotations = new ArrayList<Annotation>();


  public BuildMetaField(BuildMetaClass declaringClass, Statement statement, Scope scope, MetaClass type, String name) {
    this.declaringClass = declaringClass;
    this.statement = statement;
    this.scope = scope;
    this.type = type;
    this.name = name;
  }

  @Override
  public MetaClass getType() {
    return type;
  }

  @Override
  public MetaType getGenericType() {
    return genericType;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Annotation[] getAnnotations() {
    return annotations.toArray(new Annotation[annotations.size()]);
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
    return false;
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
    return isFinal;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public boolean isTransient() {
    return isTransient;
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @Override
  public boolean isVolatile() {
    return isVolatile;
  }

  @Override
  public boolean isSynchronized() {
    return false;
  }

  public void setDeclaringClass(BuildMetaClass declaringClass) {
    this.declaringClass = declaringClass;
  }

  public void setScope(Scope scope) {
    this.scope = scope;
  }

  public void setType(MetaClass type) {
    this.type = type;
  }

  public void setGenericType(MetaType genericType) {
    this.genericType = genericType;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFinal(boolean aFinal) {
    isFinal = aFinal;
  }

  public void setStatic(boolean aStatic) {
    isStatic = aStatic;
  }

  public void setTransient(boolean aTransient) {
    isTransient = aTransient;
  }

  public void setVolatile(boolean aVolatile) {
    isVolatile = aVolatile;
  }

  public void addAnnotation(Annotation annotation) {
    annotations.add(annotation);
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
  }

  public void setFieldComment(String fieldComment) {
    this.fieldComment = fieldComment;
  }

  @Override
  public String toJavaString() {
    final StringBuilder builder = new StringBuilder(25);
    if (fieldComment != null) {
      builder.append(new Comment(fieldComment).generate(null)).append('\n');
    }

    if (!annotations.isEmpty()) {
      for (final Annotation a : getAnnotations()) {
        builder.append(new AnnotationLiteral(a).getCanonicalString(Context.create())).append(" ");
      }
    }

    declaringClass.getContext().addVariable(Variable.create(name, type));

    builder.append(statement.generate(declaringClass.getContext()));

    return builder.toString();
  }
}
