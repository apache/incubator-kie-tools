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

import java.util.Collection;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.Builder;
import org.jboss.errai.codegen.builder.VariableDeclarationInitializer;

/**
 * Builder for the {@link Context}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ContextBuilder implements Builder {
  private final Context context;

  protected ContextBuilder(Context context) {
    this.context = context;
  }

  public static ContextBuilder create() {
    return new ContextBuilder(Context.create());
  }

  public static ContextBuilder create(Context context) {
    return new ContextBuilder(context);
  }

  public ContextBuilder addVariable(Variable variable) {
    context.addVariable(variable);
    return this;
  }

  public ContextBuilder addVariable(String name, Class<?> type) {
    context.addVariable(Variable.create(name, type));
    return this;
  }

  public ContextBuilder addVariable(String name, TypeLiteral<?> type) {
    context.addVariable(Variable.create(name, type));
    return this;
  }

  public ContextBuilder addVariable(String name, Object initialization) {
    context.addVariable(Variable.create(name, initialization));
    return this;
  }

  public ContextBuilder addVariable(String name, Class<?> type, Object initialization) {
    context.addVariable(Variable.create(name, type, initialization));
    return this;
  }

  public ContextBuilder addVariable(String name, TypeLiteral<?> type, Object initialization) {
    context.addVariable(Variable.create(name, type, initialization));
    return this;
  }

  public VariableDeclarationInitializer<ContextBuilder> declareVariable(final Variable var) {
    context.addVariable(var);
    return new VariableDeclarationInitializer<ContextBuilder>() {

      @Override
      public ContextBuilder initializeWith(Object initialization) {
        var.initialize(initialization);
        return ContextBuilder.this;
      }

      @Override
      public ContextBuilder initializeWith(Statement initialization) {
        var.initialize(initialization);
        return ContextBuilder.this;
      }

      @Override
      public ContextBuilder finish() {
        return ContextBuilder.this;
      }
    };
  }

  public VariableDeclarationInitializer<ContextBuilder> declareVariable(String name) {
    return declareVariable(Variable.create(name, (Class<?>) null));
  }

  public VariableDeclarationInitializer<ContextBuilder> declareVariable(String name, Class<?> type) {
    return declareVariable(Variable.create(name, type));
  }

  public VariableDeclarationInitializer<ContextBuilder> declareVariable(String name, TypeLiteral<?> type) {
    return declareVariable(Variable.create(name, type));
  }

  @Override
  public String toJavaString() {
    final Collection<Variable> vars = context.getDeclaredVariables();
    final StringBuilder buf = new StringBuilder(128);
    vars.forEach(var -> buf.append(var.generate(context)).append(";\n"));
    return buf.toString();
  }

  public Context getContext() {
    return context;
  }
  
}
