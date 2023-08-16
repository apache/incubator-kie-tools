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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaType;

public class JavaReflectionField extends MetaField {
  private final Field field;

  public JavaReflectionField(final Field field) {
    this.field = field;
  }

  @Override
  public String getName() {
    return field.getName();
  }

  @Override
  public MetaClass getType() {
    return MetaClassFactory.get(field.getType(), field.getGenericType());
  }

  private volatile Annotation[] _annotationsCache;

  @Override
  public synchronized Annotation[] getAnnotations() {
    if (_annotationsCache != null) {
      return _annotationsCache;
    }
    return _annotationsCache = field.getAnnotations();
  }

  @Override
  public MetaType getGenericType() {
    return JavaReflectionUtil.fromType(field.getGenericType());
  }

  @Override
  public MetaClass getDeclaringClass() {
    return MetaClassFactory.get(field.getDeclaringClass());
  }
  
  @Override
  public String getDeclaringClassName() {
    return field.getDeclaringClass().getName();
  }

  @Override
  public boolean isAbstract() {
    return (field.getModifiers() & Modifier.ABSTRACT) != 0;
  }

  @Override
  public boolean isPublic() {
    return (field.getModifiers() & Modifier.PUBLIC) != 0;
  }

  @Override
  public boolean isPrivate() {
    return (field.getModifiers() & Modifier.PRIVATE) != 0;
  }

  @Override
  public boolean isProtected() {
    return (field.getModifiers() & Modifier.PROTECTED) != 0;
  }

  @Override
  public boolean isFinal() {
    return (field.getModifiers() & Modifier.FINAL) != 0;
  }

  @Override
  public boolean isStatic() {
    return (field.getModifiers() & Modifier.STATIC) != 0;
  }

  @Override
  public boolean isTransient() {
    return (field.getModifiers() & Modifier.TRANSIENT) != 0;
  }

  @Override
  public boolean isVolatile() {
    return (field.getModifiers() & Modifier.VOLATILE) != 0;
  }

  @Override
  public boolean isSynthetic() {
    return field.isSynthetic();
  }

  @Override
  public boolean isSynchronized() {
    return (field.getModifiers() & Modifier.SYNCHRONIZED) != 0;
  }

  @Override
  public Field asField() {
    return field;
  }
}
