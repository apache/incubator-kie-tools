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
import java.util.Arrays;

import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class GWTConstructor extends MetaConstructor {
  private final JConstructor constructor;
  private final MetaClass declaringClass;
  private final Annotation[] annotations;
  private final TypeOracle oracle;

  public GWTConstructor(final TypeOracle oracle, final JConstructor c) {
    this.constructor = c;
    this.annotations = c.getAnnotations();

    this.declaringClass = GWTClass.newInstance(oracle, c.getEnclosingType());
    this.oracle = oracle;
  }

  @Override
  public MetaParameter[] getParameters() {
    return Arrays.stream(constructor.getParameters())
            .map(p -> new GWTParameter(oracle, p, this))
            .toArray(s -> new MetaParameter[s]);
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public MetaClass getReturnType() {
    return declaringClass;
  }

  @Override
  public MetaType getGenericReturnType() {
    return declaringClass;
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
  public Annotation[] getAnnotations() {
    return annotations;
  }

  @Override
  public MetaType[] getGenericParameterTypes() {
    return null;
  }

  @Override
  public MetaClass[] getCheckedExceptions() {
    return new MetaClass[0];
  }

  @Override
  public boolean isVarArgs() {
    return constructor.isVarArgs();
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  @Override
  public boolean isPublic() {
    return constructor.isPublic();
  }

  @Override
  public boolean isPrivate() {
    return constructor.isPrivate();
  }

  @Override
  public boolean isProtected() {
    return constructor.isProtected();
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

  @Override
  public MetaTypeVariable[] getTypeParameters() {
    return GWTUtil.fromTypeVariable(oracle, constructor.getTypeParameters());
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof MetaConstructor && GenUtil.equals(this, (MetaConstructor) o);
  }
}
