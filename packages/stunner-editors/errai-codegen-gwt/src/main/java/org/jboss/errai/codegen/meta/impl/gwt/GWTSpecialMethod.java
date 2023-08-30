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


package org.jboss.errai.codegen.meta.impl.gwt;

import java.lang.annotation.Annotation;

import org.jboss.errai.codegen.DefModifiers;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.builder.impl.Scope;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;

/**
 * @author Mike Brock
 */
public class GWTSpecialMethod extends MetaMethod {
  private final DefModifiers modifiers;
  private final Scope scope;
  private final GWTClass declaringClass;
  private final MetaClass returnType;
  private final String methodName;
  private final MetaParameter[] parameters;

  GWTSpecialMethod(final GWTClass declaringClass,
                   final DefModifiers modifiers,
                   final Scope scope,
                   final Class returnType,
                   final String methodName,
                   final MetaParameter... parameters) {
    this(declaringClass, modifiers, scope, MetaClassFactory.get(returnType), methodName, parameters);
  }

  GWTSpecialMethod(final GWTClass declaringClass,
                   final DefModifiers modifiers,
                   final Scope scope,
                   final MetaClass returnType,
                   final String methodName,
                   final MetaParameter... parameters) {
    this.declaringClass = declaringClass;
    this.modifiers = modifiers;
    this.scope = scope;
    this.returnType = returnType;
    this.methodName = methodName;
    this.parameters = parameters;
  }

  @Override
  public String getName() {
    return methodName;
  }

  @Override
  public MetaClass getReturnType() {
    return returnType;
  }

  @Override
  public MetaParameter[] getParameters() {
    return parameters;
  }

  @Override
  public Annotation[] getAnnotations() {
    return new Annotation[0];
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
  public MetaType getGenericReturnType() {
    return null;
  }

  @Override
  public MetaType[] getGenericParameterTypes() {
    return new MetaType[0];
  }

  @Override
  public MetaClass[] getCheckedExceptions() {
    return new MetaClass[0];
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
  public boolean isVolatile() {
    return false;
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @Override
  public boolean isSynchronized() {
    return false;
  }

  @Override
  public boolean isVarArgs() {
    return false;
  }

  @Override
  public MetaTypeVariable[] getTypeParameters() {
    return new MetaTypeVariable[0];
  }
}
