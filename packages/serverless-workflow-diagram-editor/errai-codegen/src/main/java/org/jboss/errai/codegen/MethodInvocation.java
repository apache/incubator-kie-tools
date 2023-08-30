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


package org.jboss.errai.codegen;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.codegen.builder.callstack.CallWriter;
import org.jboss.errai.codegen.literal.TypeLiteral;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.meta.MetaWildcardType;
import org.mvel2.util.NullType;

/**
 * Represents a method invocation statement.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock
 */
public class MethodInvocation extends AbstractStatement {
  private final MetaClass inputType;
  private final MetaMethod method;
  private final CallParameters callParameters;
  private Map<String, MetaClass> typeVariables;
  private final CallWriter writer;

  public MethodInvocation(final CallWriter writer,
                          final MetaClass inputType,
                          final MetaMethod method,
                          final CallParameters callParameters) {

    this.inputType = inputType;
    this.method = method;
    this.callParameters = callParameters;
    this.writer = writer;
  }

  String generatedCache;

  @Override
  public String generate(final Context context) {
    if (generatedCache != null) return generatedCache;
    return generatedCache = method.getName().concat(callParameters.generate(context));
  }

  @Override
  public MetaClass getType() {
    MetaClass returnType = method.getReturnType();

    if (method.getGenericReturnType() != null && method.getGenericReturnType() instanceof MetaTypeVariable) {
      typeVariables = new HashMap<String, MetaClass>();
      resolveTypeVariables();

      final MetaTypeVariable typeVar = (MetaTypeVariable) method.getGenericReturnType();
      if (typeVariables.containsKey(typeVar.getName())) {
        returnType = typeVariables.get(typeVar.getName());
      }
      else if (writer.getTypeParm(typeVar.getName()) != null) {
        returnType = writer.getTypeParm(typeVar.getName());
      }
      else {
        // returning NullType as a stand-in for an unbounded wildcard type since this is a parameterized method
        // and there is not RHS qualification for the parameter.
        //
        // ie when calling GWT.create() and assigning it to a concrete type.
        //
        // TODO: might be worth flushing this out for clarify in the future.
        return MetaClassFactory.get(NullType.class);
      }
    }

    assert returnType != null;

    return returnType;
  }

  // Resolves type variables by inspecting call parameters
  private void resolveTypeVariables() {
    final MetaParameterizedType gSuperClass = inputType.getGenericSuperClass();
    final MetaClass superClass = inputType.getSuperClass();

    if (superClass != null && superClass.getTypeParameters() != null && superClass.getTypeParameters().length > 0
            && gSuperClass != null && gSuperClass.getTypeParameters().length > 0) {
      for (int i = 0; i < superClass.getTypeParameters().length; i++) {
        final String varName = superClass.getTypeParameters()[i].getName();
        if (gSuperClass.getTypeParameters()[i] instanceof MetaClass) {
          typeVariables.put(varName, (MetaClass) gSuperClass.getTypeParameters()[i]);
        }
        else if (gSuperClass.getTypeParameters()[i] instanceof MetaWildcardType) {
          typeVariables.put(varName, MetaClassFactory.get(Object.class));
        }
        else {
          final MetaClass clazz = writer.getTypeParm(varName);
          if (clazz != null) {
            typeVariables.put(varName, clazz);
          }
        }
      }
    }

    int methodParmIndex = 0;
    for (final MetaType methodParmType : method.getGenericParameterTypes()) {
      final Statement parm = callParameters.getParameters().get(methodParmIndex);

      final MetaType callParmType;
      if (parm instanceof TypeLiteral) {
        callParmType = ((TypeLiteral) parm).getActualType();
      }
      else {
        callParmType = parm.getType();
      }

      resolveTypeVariable(methodParmType, callParmType);
      methodParmIndex++;
    }
  }

  private void resolveTypeVariable(final MetaType methodParmType, final MetaType callParmType) {
    if (methodParmType instanceof MetaTypeVariable) {
      final MetaTypeVariable typeVar = (MetaTypeVariable) methodParmType;
      final MetaClass resolvedType;
      if (callParmType instanceof MetaClass) {
        resolvedType = (MetaClass) callParmType;
      }
      else if (callParmType instanceof MetaWildcardType) {
        final MetaType[] upperBounds = ((MetaWildcardType) callParmType).getUpperBounds();
        if (upperBounds != null && upperBounds.length == 1 && upperBounds[0] instanceof MetaClass) {
          resolvedType = (MetaClass) upperBounds[0];
        }
        else {
          // it's either unbounded (? or ? super X) or has fancy bounds like ? extends X & Y
          // so we'll fall back on good old java.lang.Object
          resolvedType = MetaClassFactory.get(Object.class);
        }
      }
      else {
        throw new IllegalArgumentException("Call parameter \"" + callParmType + "\" is of unexpected metatype " + callParmType.getClass());
      }
      typeVariables.put(typeVar.getName(), resolvedType);
    }
    else if (methodParmType instanceof MetaParameterizedType) {
      final MetaType parameterizedCallParmType;
      if (callParmType instanceof MetaParameterizedType) {
        parameterizedCallParmType = callParmType;
      }
      else {
        parameterizedCallParmType = ((MetaClass) callParmType).getParameterizedType();
      }

      final MetaParameterizedType parameterizedMethodParmType = (MetaParameterizedType) methodParmType;
      int typeParmIndex = 0;
      for (final MetaType typeParm : parameterizedMethodParmType.getTypeParameters()) {
        if (parameterizedCallParmType != null) {
          resolveTypeVariable(typeParm,
                  ((MetaParameterizedType) parameterizedCallParmType).getTypeParameters()[typeParmIndex++]);
        }
      }
    }
  }
}
