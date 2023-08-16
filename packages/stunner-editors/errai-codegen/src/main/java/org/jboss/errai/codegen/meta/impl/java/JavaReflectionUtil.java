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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.common.rebind.CacheStore;
import org.jboss.errai.common.rebind.CacheUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class JavaReflectionUtil {
  public static class CacheHolder implements CacheStore {
    final Map<Type, MetaType> FROM_TYPE_CLASS = new ConcurrentHashMap<Type, MetaType>();

    @Override
    public void clear() {
      FROM_TYPE_CLASS.clear();
    }
  }

  public static MetaTypeVariable[] fromTypeVariable(final TypeVariable<?>[] typeVariables) {
    return Arrays.stream(typeVariables)
            .map(t -> new JavaReflectionTypeVariable(t)).toArray(s -> new MetaTypeVariable[s]);
  }

  public static MetaType[] fromTypeArray(final Type[] types) {
    return Arrays.stream(types).map(t -> fromType(t)).toArray(s -> new MetaType[s]);
  }

  /**
   * Returns an instance of the appropriate MetaType that wraps the given Java
   * Reflection Type.
   *
   * @param t
   *          the Type to wrap in a MetaType
   * @return A (possibly cached) MetaType instance that represents the same
   *         thing as the given Type. Never null.
   */
  public static MetaType fromType(final Type t) {
    MetaType type = CacheUtil.getCache(CacheHolder.class).FROM_TYPE_CLASS.get(t);
    if (type == null) {
      if (t instanceof Class) {
        type = (MetaClassFactory.get((Class<?>) t));
      }
      else if (t instanceof TypeVariable) {
        type = new JavaReflectionTypeVariable((TypeVariable<?>) t);
      }
      else if (t instanceof ParameterizedType) {
        type = new JavaReflectionParameterizedType((ParameterizedType) t);
      }
      else if (t instanceof GenericArrayType) {
        type = new JavaReflectionGenericArrayType((GenericArrayType) t);
      }
      else if (t instanceof WildcardType) {
        type = new JavaReflectionWildcardType((WildcardType) t);
      }
      else {
        throw new RuntimeException("Don't know how to make a MetaType from Type " + t +
                " (which is a " + (t == null ? null : t.getClass()) + ")");
      }
      CacheUtil.getCache(CacheHolder.class).FROM_TYPE_CLASS.put(t, type);
    }

    return type;
  }
}
