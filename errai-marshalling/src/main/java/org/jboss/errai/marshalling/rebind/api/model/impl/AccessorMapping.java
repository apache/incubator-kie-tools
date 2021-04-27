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
public class AccessorMapping extends SimpleMapping implements MemberMapping {
  private MetaClassMember bindingMember;
  private MetaClassMember readingMember;

  private String setterMethod;
  private String getterMethod;

  public AccessorMapping(String key, Class<?> type, String setterMethod, String getterMethod) {
    this(key, JavaReflectionClass.newUncachedInstance(type), setterMethod, getterMethod);
  }

  public AccessorMapping(String key, MetaClass type, String setterMethod, String getterMethod) {
    super(key, type);

    this.setterMethod = setterMethod;
    this.getterMethod = getterMethod;
  }


  @Override
  public MetaClassMember getBindingMember() {
    if (bindingMember != null) {
      return bindingMember;
    }

    MetaMethod meth = toMap.getMethod(setterMethod, targetType);

    final Method method = meth.asMethod();

    if (method != null) {
      method.setAccessible(true);
    }

    bindingMember = meth;

    if (bindingMember == null) {
      throw new RuntimeException("no such setter method: " + toMap.getFullyQualifiedName() + "." + setterMethod);
    }

    return bindingMember;
  }

  @Override
  public MetaClassMember getReadingMember() {
    if (readingMember != null) {
      return readingMember;
    }

    MetaMethod meth = toMap.getMethod(getterMethod, new MetaClass[0]);

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
    return true;
  }
}
