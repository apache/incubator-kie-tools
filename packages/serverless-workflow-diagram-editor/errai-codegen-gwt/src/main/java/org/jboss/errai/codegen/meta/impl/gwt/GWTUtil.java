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

import java.util.Arrays;

import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.JTypeParameter;
import com.google.gwt.core.ext.typeinfo.JWildcardType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class GWTUtil {

  public static MetaTypeVariable[] fromTypeVariable(final TypeOracle oracle,
          final JTypeParameter[] typeParameters) {

    return Arrays.stream(typeParameters).map(p -> new GWTTypeVariable(oracle, p)).toArray(s -> new MetaTypeVariable[s]);
  }

  public static MetaType[] fromTypeArray(final TypeOracle oracle, final JType[] types) {
    return Arrays.stream(types).map(t -> fromType(oracle, t)).toArray(s -> new MetaType[s]);
  }

  private static JType getRootComponentType(JArrayType type) {
    JType root = null;
    while (type.getComponentType() != null) {
      if (type.getComponentType().isArray() != null) {
        type = type.getComponentType().isArray();
      }
      else {
        root = type.getComponentType();
        break;
      }

    }
    return root;
  }

  public static MetaClass eraseOrReturn(final TypeOracle oracle, final JType t) {

    if (t.isArray() != null) {
      final JType root = getRootComponentType(t.isArray());
      if (root.isTypeParameter() != null) {
        return MetaClassFactory.get(Object.class);
      }
    }

    if (t.isTypeParameter() != null) {
      final JTypeParameter tp = t.isTypeParameter();
      return MetaClassFactory.get(tp.getErasedType().getQualifiedBinaryName());
    }
    else if (t.isWildcard() != null) {
      final JWildcardType wildcard = t.isWildcard();
      return MetaClassFactory.get(wildcard.getBaseType().getQualifiedBinaryName());
    }
    else {
      return GWTClass.newInstance(oracle, t);
    }
  }

  public static MetaType fromType(final TypeOracle oracle, final JType t) {
    if (t.isTypeParameter() != null) {
      return new GWTTypeVariable(oracle, t.isTypeParameter());
    }
    else if (t.isArray() != null
        && (t.isArray().getComponentType().isTypeParameter() != null
        || t.isArray().getComponentType().isWildcard() != null)) {
      return new GWTGenericArrayType(oracle, t.isArray());
    }
    else if (t.isParameterized() != null) {
      return new GWTParameterizedType(oracle, t.isParameterized());
    }
    else if (t.isWildcard() != null) {
      return new GWTWildcardType(oracle, t.isWildcard());
    }
    else if (t.isClassOrInterface() != null
        || t.isEnum() != null
        || t.isPrimitive() != null
        || t.isRawType() != null
        || t.isArray() != null) {
      return GWTClass.newInstance(oracle, t);
    }
    else {
      throw new RuntimeException("Don't know how to make a MetaType from given JType " + t +
          " (which is a " + (t.getClass()) + ")");
    }
  }

}
