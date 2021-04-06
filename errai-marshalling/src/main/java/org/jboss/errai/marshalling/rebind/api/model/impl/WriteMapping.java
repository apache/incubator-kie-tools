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

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.marshalling.rebind.api.model.MemberMapping;

import java.lang.reflect.Method;

/**
 * @author Mike Brock
 */
public class WriteMapping extends SimpleMapping implements MemberMapping {
  private MetaClassMember writingMember;

  private String getterMethod;

  public WriteMapping(String key, Class<?> type, String getterMethod) {
    this(key, JavaReflectionClass.newUncachedInstance(type), getterMethod);
  }

  public WriteMapping(String key, MetaClass type, String getterMethod) {
    super(key, type);
    this.getterMethod = getterMethod;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public MetaClassMember getBindingMember() {
    if (writingMember != null) {
      return writingMember;
    }

    MetaMethod meth = toMap.getMethod(getterMethod, targetType);

    final Method method = meth.asMethod();

    if (method != null) {
      method.setAccessible(true);
    }

    writingMember = meth;

    if (writingMember == null) {
      throw new RuntimeException("no such setter method: " + toMap.getFullyQualifiedName() + "." + getterMethod);
    }

    return writingMember;
  }

  @Override
  public MetaClassMember getReadingMember() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean canRead() {
    return false;
  }

  @Override
  public boolean canWrite() {
    return true;
  }

  @Override
  public void setMappingClass(MetaClass clazz) {
    this.toMap = clazz;
  }
}
