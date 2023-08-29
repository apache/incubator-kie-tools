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


package org.jboss.errai.codegen.meta.impl.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class JavaReflectionConstructor extends MetaConstructor {
  private final Constructor constructor;
  private MetaParameter[] parameters;
  private MetaClass declaringClass;
  private volatile Annotation[] annotationsCache;

  JavaReflectionConstructor(final Constructor c) {
    constructor = c;
  }

  @Override
  public MetaParameter[] getParameters() {
    if (parameters == null) {

      final Class<?>[] parmTypes = constructor.getParameterTypes();
      final Type[] genParmTypes = constructor.getGenericParameterTypes();
      final Annotation[][] parmAnnos = constructor.getParameterAnnotations();
      final List<MetaParameter> parmList = new ArrayList<>(parmTypes.length);

      for (int i = 0; i < parmTypes.length; i++) {
        final MetaClass mcParm = MetaClassFactory.get(parmTypes[i], genParmTypes[i]);
        parmList.add(new JavaReflectionParameter(mcParm, parmAnnos[i], this));
      }

      parameters = parmList.toArray(new MetaParameter[parmList.size()]);
    }

    return parameters;
  }

  @Override
  public MetaClass getDeclaringClass() {
    if (declaringClass == null) {
      declaringClass = MetaClassFactory.get(constructor.getDeclaringClass());
    }
    return declaringClass;
  }

  @Override
  public String getDeclaringClassName() {
    return getDeclaringClass().getName();
  }

  private MetaType[] _genericParameterTypes;

  @Override
  public MetaType[] getGenericParameterTypes() {
    if (_genericParameterTypes != null) return _genericParameterTypes;
    return _genericParameterTypes = JavaReflectionUtil.fromTypeArray(constructor.getGenericParameterTypes());
  }

  private MetaTypeVariable[] _typeParameters;

  @Override
  public MetaTypeVariable[] getTypeParameters() {
    if (_typeParameters != null) return _typeParameters;
    return _typeParameters = JavaReflectionUtil.fromTypeVariable(constructor.getTypeParameters());
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public MetaClass getReturnType() {
    return getDeclaringClass();
  }

  @Override
  public MetaType getGenericReturnType() {
    return getDeclaringClass();
  }

  @Override
  public synchronized Annotation[] getAnnotations() {
    if (annotationsCache == null) {
      annotationsCache = constructor.getAnnotations();
    }
    return annotationsCache;
  }

  @Override
  public MetaClass[] getCheckedExceptions() {
    return new MetaClass[0];
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  @Override
  public boolean isPublic() {
    return (constructor.getModifiers() & Modifier.PUBLIC) != 0;
  }

  @Override
  public boolean isPrivate() {
    return (constructor.getModifiers() & Modifier.PRIVATE) != 0;
  }

  @Override
  public boolean isProtected() {
    return (constructor.getModifiers() & Modifier.PROTECTED) != 0;
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
  public boolean isVolatile() {
    return false;
  }

  @Override
  public boolean isSynchronized() {
    return (constructor.getModifiers() & Modifier.SYNCHRONIZED) != 0;
  }

  @Override
  public boolean isSynthetic() {
    return constructor.isSynthetic();
  }

  @Override
  public boolean isVarArgs() {
    return constructor.isVarArgs();
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof MetaConstructor && GenUtil.equals(this, (MetaConstructor) o);
  }

  @Override
  public Constructor asConstructor() {
    return constructor;
  }
}
