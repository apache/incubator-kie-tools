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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.impl.AbstractMetaParameterizedType;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class GWTParameterizedType extends AbstractMetaParameterizedType {
  private final JParameterizedType parameterizedType;
  private final TypeOracle oracle;

  public GWTParameterizedType(final TypeOracle oracle, final JParameterizedType parameterizedType) {
    this.parameterizedType = parameterizedType;
    this.oracle = oracle;
  }

  @Override
  public MetaType[] getTypeParameters() {
    final List<MetaType> types = new ArrayList<>();
    for (final JClassType parm : parameterizedType.getTypeArgs()) {
      if (parm.isWildcard() != null) {
        types.add(new GWTWildcardType(oracle, parm.isWildcard()));
      }
      else if (parm.isTypeParameter() != null) {
        types.add(new GWTTypeVariable(oracle, parm.isTypeParameter()));
      }
      else if (parm.isArray() != null
              && isInnerMostComponentTypeParameter(parm.isArray())) {
        // is generic array. Erase to Object[]
        types.add(GWTClass.newInstance(oracle, parm.isArray().getErasedType()));
      }
      else if (parm.isClassOrInterface() != null
              || parm.isEnum() != null
              || parm.isPrimitive() != null
              || parm.isRawType() != null
              || parm.isArray() != null
              || parm.isAnnotation() != null) {
        types.add(GWTClass.newInstance(oracle, parm));
      }
      else {
        throw new IllegalArgumentException("Unsupported kind of type parameter " + parm + " in type " + parameterizedType.getName());
      }
    }
    return types.toArray(new MetaType[types.size()]);
  }

  private boolean isInnerMostComponentTypeParameter(final JArrayType parm) {
    final JType componentType = parm.isArray().getComponentType();
    if (componentType.isArray() != null) {
      return isInnerMostComponentTypeParameter(componentType.isArray());
    }
    else {
      return componentType.isTypeParameter() != null;
    }
  }

  @Override
  public MetaType getOwnerType() {
    return GWTClass.newInstance(oracle, parameterizedType.getEnclosingType());
  }

  @Override
  public MetaType getRawType() {
    return GWTClass.newInstance(oracle, parameterizedType.getRawType());
  }

  @Override
  public String getName() {
    return parameterizedType.getParameterizedQualifiedSourceName();
  }
}
