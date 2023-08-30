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


package org.jboss.errai.codegen.builder.callstack;

import java.util.Arrays;

import org.jboss.errai.codegen.CallParameters;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.MethodInvocation;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.exception.UndefinedMethodException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.util.GenUtil;

import static org.jboss.errai.codegen.CallParameters.fromStatements;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class MethodCall extends AbstractCallElement {
  private final String methodName;
  private final Object[] parameters;
  private final boolean staticMethod;

  public MethodCall(final String methodName, final Object[] parameters) {
    this.methodName = methodName;
    this.parameters = parameters;
    this.staticMethod = false;
  }

  public MethodCall(final String methodName, final Object[] parameters, final boolean staticMethod) {
    this.methodName = methodName;
    this.parameters = parameters;
    this.staticMethod = staticMethod;
  }

  @Override
  public void handleCall(final CallWriter writer, final Context context, Statement statement) {
    try {
      CallParameters callParams = fromStatements(GenUtil.generateCallParameters(context, parameters));

      statement.generate(context);

      final MetaClass callType = statement.getType();

      final MetaClass[] parameterTypes = callParams.getParameterTypes();
      final MetaMethod method = (staticMethod) ? callType.getBestMatchingStaticMethod(methodName, parameterTypes)
              : callType.getBestMatchingMethod(methodName, parameterTypes);

      if (method == null) {
        if (context.isPermissiveMode()) {
          try {
            blameAndRethrow( new UndefinedMethodException(statement.getType(), methodName, parameterTypes) );
          }
          catch (final Exception e) {
            e.printStackTrace();
          }
          
          dummyReturn(writer, context);
          return;
        }
        else {
          throw new UndefinedMethodException(statement.getType(), methodName, parameterTypes);
        }
      }

      if (method.getGenericParameterTypes() != null) {
        final MetaType[] genTypes = method.getGenericParameterTypes();
        for (int i = 0; i < genTypes.length; i++) {
          if (genTypes[i] instanceof MetaParameterizedType) {
            if (parameters[i] instanceof MetaClass) {
              final MetaType type = ((MetaParameterizedType) genTypes[i]).getTypeParameters()[0];
              if (type instanceof MetaTypeVariable) {
                writer.recordTypeParm(((MetaTypeVariable) type).getName(), (MetaClass) parameters[i]);
              }
            }
          }
        }
      }

      /**
       * If the method is within the calling scope, we can strip the qualifying reference.
       */
      if (statement instanceof LoadClassReference.ClassReference && context.isInScope(method)) {
        writer.reset();
      }

      callParams = fromStatements(GenUtil.generateCallParameters(method, context, parameters));
      statement = new MethodInvocation(writer, callType, method, callParams);

      resultType = statement.getType();

      nextOrReturn(writer, context, statement);
    }
    catch (final GenerationException ge) {
      blameAndRethrow(ge);
    }
    catch (final Exception e) {
      throw new RuntimeException("error generating method call for: " + methodName
              + "(" + Arrays.toString(parameters) + ")", e);
    }
  }

  @Override
  public String toString() {
    return "[[MethodCall<" + methodName + "(" + Arrays.toString(parameters) + ")>]" + next + "]";
  }

  private void dummyReturn(final CallWriter writer, final Context context) {
    nextOrReturn(writer, context, new Statement() {
      @Override
      public String generate(final Context context) {
        final StringBuilder parms = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
          parms.append(GenUtil.generate(context, parameters[i]));
          if (i + 1 < parameters.length) parms.append(", ");
        }
        return methodName + "(" + parms.toString() + ")";
      }

      @Override
      public MetaClass getType() {
        return MetaClassFactory.get(Object.class);
      }
    });
  }
}
