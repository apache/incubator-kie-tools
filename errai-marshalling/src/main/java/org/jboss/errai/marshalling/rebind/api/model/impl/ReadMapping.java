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

package org.jboss.errai.marshalling.rebind.api.model.impl;

import java.lang.reflect.Method;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.marshalling.rebind.api.model.MemberMapping;

/**
 * @author Mike Brock
 */
public class ReadMapping extends SimpleMapping implements MemberMapping {
  private MetaClass toMap;
  private MetaClassMember readingMember;
  private final String getterMethod;

  public ReadMapping(final String key, final Class<?> type, final String getterMethod) {
    this(key, JavaReflectionClass.newUncachedInstance(type), getterMethod);
  }

  public ReadMapping(final String key, final MetaClass type, final String getterMethod) {
    super(key, type);
    this.getterMethod = getterMethod;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public MetaClassMember getBindingMember() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MetaClassMember getReadingMember() {
    if (readingMember != null) {
      return readingMember;
    }

    final MetaMethod meth = toMap.getMethod(getterMethod, new MetaClass[0]);

    final Method method = meth.asMethod();
    if (method != null) {
      method.setAccessible(true);
    }

    readingMember = meth;

    if (readingMember == null) {
      throw new RuntimeException("no such getter method: " + toMap.getFullyQualifiedName() + "." + getterMethod);
    }

    return readingMember;
  }

  @Override
  public boolean canRead() {
    return true;
  }

  @Override
  public boolean canWrite() {
    return false;
  }

  @Override
  public void setMappingClass(final MetaClass clazz) {
    this.toMap = clazz;
  }
}
