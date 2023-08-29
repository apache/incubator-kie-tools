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


package org.jboss.errai.codegen.builder.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.errai.codegen.AbstractStatement;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.BuildCallback;
import org.jboss.errai.codegen.exception.UndefinedMethodException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.GenUtil;

import static org.jboss.errai.codegen.builder.callstack.LoadClassReference.getClassReference;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class AnonymousClassStructureBuilderImpl
    extends ClassBuilder<AnonymousClassStructureBuilder>
    implements AnonymousClassStructureBuilder {
  private final BuildCallback<ObjectBuilder> callback;
  private final List<DeferredGenerateCallback> deferredGenerateCallbacks;
  private final Context context;

  AnonymousClassStructureBuilderImpl(final MetaClass clazz, final BuildCallback<ObjectBuilder> builderBuildCallback) {
    super(clazz.getFullyQualifiedName(), clazz, builderBuildCallback.getParentContext());
    this.callback = builderBuildCallback;
    this.context = builderBuildCallback.getParentContext();
    deferredGenerateCallbacks = new ArrayList<DeferredGenerateCallback>();
  }

  @Override
  public BlockBuilder<AnonymousClassStructureBuilder> initialize() {
    return new BlockBuilderImpl<AnonymousClassStructureBuilder>(
        new BuildCallback<AnonymousClassStructureBuilder>() {
          @Override
          public AnonymousClassStructureBuilderImpl callback(final Statement statement) {

            addCallable(context -> {
              final StringBuilder buf = new StringBuilder(256);
              buf.append("{\n");
              if (statement != null) {
                buf.append(statement.generate(Context.create(context))).append("\n");
              }
              buf.append("}\n");

              return buf.toString();
            });

            return AnonymousClassStructureBuilderImpl.this;
          }

          @Override
          public Context getParentContext() {
            return context;
          }
        });
  }

  private BlockBuilder<AnonymousClassStructureBuilder> publicOverridesMethod(final MetaMethod method,
                                                                             final DefParameters parameters) {
    return new BlockBuilderImpl<AnonymousClassStructureBuilder>(
        new BuildCallback<AnonymousClassStructureBuilder>() {
          @Override
          public AnonymousClassStructureBuilder callback(final Statement statement) {

            addCallable(context -> {
              final Context subContext = Context.create(context);
              parameters.getParameters()
                .forEach(p -> subContext.addVariable(Variable.create(p.getName(), p.getType())));

              final StringBuilder buf = new StringBuilder(256);
              final String returnType = getClassReference(method.getReturnType(), context);

              buf.append("public ").append(returnType)
                  .append(" ")
                  .append(method.getName())
                  .append(parameters.generate(context)).append(" {\n");
              if (statement != null) {
                buf.append(statement.generate(subContext)).append("\n");
              }
              buf.append("}\n");

              return buf.toString();
            });

            return AnonymousClassStructureBuilderImpl.this;
          }

          @Override
          public Context getParentContext() {
            return context;
          }
        });
  }

  @Override
  public BlockBuilder<AnonymousClassStructureBuilder> publicOverridesMethod(final String name, final Parameter... args) {
    final List<MetaClass> types = new ArrayList<MetaClass>();
    Arrays.stream(args).forEach(a -> types.add(a.getType()));
    final MetaMethod method = classDefinition.getSuperClass()
        .getBestMatchingMethod(name, types.toArray(new MetaClass[args.length]));
    
    if (method == null)
      throw new UndefinedMethodException("Can't override (inherited method not found):"
          + classDefinition.getFullyQualifiedNameWithTypeParms() + "." + name + "(" + types + ")");

    return publicOverridesMethod(method, DefParameters.fromParameters(args));
  }


  @Override
  public ObjectBuilder finish() {
    if (callback != null) {
      return callback.callback(new AbstractStatement() {
        @Override
        public String generate(final Context context) {
          context.attachClass(getClassDefinition());
          return doGenerate(context);
        }
      });
    }

    return null;
  }

  private void addCallable(final DeferredGenerateCallback callable) {
    deferredGenerateCallbacks.add(callable);
  }

  String generatedCache;

  private String doGenerate(final Context context) {
    if (generatedCache != null) return generatedCache;
    try {
      if (deferredGenerateCallbacks == null)
        return null;

      final Context subContext = Context.create(context);

      classDefinition.getContext().getDeclaredVariables().forEach((v) -> subContext.addVariable(v));
      subContext.addVariable(Variable.create("this", getClassDefinition()));
      classDefinition.setContext(subContext);

      final StringBuilder buf = new StringBuilder(256);
      buf.append(classDefinition.membersToString().trim()).append("\n");
      deferredGenerateCallbacks.forEach(c -> buf.append(c.doGenerate(subContext).trim()).append('\n'));
      return generatedCache = buf.toString().trim();
    }
    catch (final Exception e) {
      GenUtil.throwIfUnhandled("while generating: " + classDefinition.getFullyQualifiedName(), e);
      return null;
    }
  }

  public static interface DeferredGenerateCallback {
    public String doGenerate(Context context);
  }


  @Override
  public BuildMetaClass getClassDefinition() {
    return classDefinition;
  }
}
