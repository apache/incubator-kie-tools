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


package org.jboss.errai.codegen.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.codegen.util.GenUtil;

public abstract class MetaMethod extends AbstractHasAnnotations implements MetaClassMember, MetaGenericDeclaration {
  @Override
  public abstract String getName();

  /**
   * Returns the MetaClass representing this method's return type. The returned
   * MetaClass may have had its generic information erased.
   *
   * @return
   */
  public abstract MetaClass getReturnType();

  /**
   * Returns the MetaType representing the return type of the method. In the
   * case of a plain, non-parameterized return type, this will return a
   * {@link MetaClass} equivalent to the one returned by
   * {@link MetaMethod#getReturnType()}. Other possible types could be
   * {@link MetaWildcardType}, {@link MetaParameterizedType}, and
   * {@link MetaTypeVariable}.
   * <p>
   * As of Errai 2.2, some implementations of this method are incomplete and
   * will return null if they cannot make sense of the method's return type.
   *
   * @return
   */
  public abstract MetaType getGenericReturnType();

  public abstract MetaType[] getGenericParameterTypes();

  public abstract MetaParameter[] getParameters();

  public abstract MetaClass[] getCheckedExceptions();

  public abstract boolean isVarArgs();

  private String _hashString;
  public String hashString() {
  if (_hashString != null) return _hashString;
    return _hashString = MetaMethod.class + ":"
            + getDeclaringClass().getFullyQualifiedName() + "." + getName()
            + "(" + Arrays.toString(getParameters()) + ")";
  }

  @Override
  public int hashCode() {
    return hashString().hashCode() * 31;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof MetaMethod && ((MetaMethod)o).hashString().equals(hashString());
  }

  public List<MetaParameter> getParametersAnnotatedWith(Class<? extends Annotation> annotation) {
    return Arrays.stream(getParameters())
            .filter(p -> p.isAnnotationPresent(annotation))
            .collect(Collectors.collectingAndThen(Collectors.toList(), l -> Collections.unmodifiableList(l)));
  }

  public Method asMethod() {
    try {
      final Class cls = Class.forName(getDeclaringClass().getFullyQualifiedName());
      final Class[] parms = MetaClassFactory.asClassArray(getParameters());

      for (final Method m : cls.getDeclaredMethods()) {
        if (m.getName().equals(getName()) && Arrays.equals(parms, m.getParameterTypes())) {
          return m;
        }
      }
      return null;
    }
    catch (final Throwable t) {
      return null;
    }
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    final Annotation[] annos = getAnnotations();
    if (annos != null) {
      for (final Annotation anno : annos) {
        sb.append(anno.toString()).append(" ");
      }
    }
    sb.append(" ").append(GenUtil.scopeOf(this).getCanonicalName()).append(" ")
    .append(getReturnType()).append(" ")
    .append(getName()).append("(").append(Arrays.toString(getParameters())).append(")");
    
    return sb.toString();
  }
}
