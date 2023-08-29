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


package org.jboss.errai.codegen.builder;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface StatementBegin extends ArrayBuilder, LoopBuilder, IfBlockBuilder, SwitchBlockBuilder, TryBlockBuilder {
  public VariableDeclarationStart<StatementBegin> declareVariable(Class<?> type);
  public VariableDeclarationStart<StatementBegin> declareVariable(MetaClass type);

  public StatementBuilder declareVariable(String name, Class<?> type);
  public StatementBuilder declareVariable(String name, TypeLiteral<?> type);
  public StatementBuilder declareVariable(String name, Object initialization);
  public StatementBuilder declareVariable(String name, MetaClass type, Object initialization);
  public StatementBuilder declareVariable(String name, Class<?> type, Object initialization);
  public StatementBuilder declareVariable(String name, TypeLiteral<?> type, Object initialization);

  public StatementBuilder declareFinalVariable(String name, Class<?> type);
  public StatementBuilder declareFinalVariable(String name, TypeLiteral<?> type);
  public StatementBuilder declareFinalVariable(String name, MetaClass type, Object initialization);
  public StatementBuilder declareFinalVariable(String name, Class<?> type, Object initialization);
  public StatementBuilder declareFinalVariable(String name, TypeLiteral<?> type, Object initialization);

  public VariableReferenceContextualStatementBuilder loadVariable(String name, Object... indexes);
  public VariableReferenceContextualStatementBuilder loadClassMember(String name, Object... indexes);
  public ContextualStatementBuilder loadLiteral(Object o);
  public ContextualStatementBuilder load(Object o);
  public ContextualStatementBuilder loadClassReference(Object o);

  public ContextualStatementBuilder invokeStatic(MetaClass clazz, String methodName, Object... parameters);
  public ContextualStatementBuilder invokeStatic(Class<?> clazz, String methodName, Object... parameters);

  public ContextualStatementBuilder loadStatic(Class<?> clazz, String fieldName);
  public ContextualStatementBuilder loadStatic(MetaClass clazz, String fieldName);

  public ContextualStatementBuilder nestedCall(Statement statement);

  public ContextualStatementBuilder castTo(Class<?> type, Statement statement);
  public ContextualStatementBuilder castTo(MetaClass type, Statement statement);

  public ObjectBuilder newObject(Class<?> type);
  public ObjectBuilder newObject(MetaClass type);
  public ObjectBuilder newObject(TypeLiteral<?> type);

  public Statement newObject(Class<?> type, Object... parameters);
  public Statement newObject(MetaClass type, Object... parameters);
  public Statement newObject(TypeLiteral<?> type, Object... parameters);

  public StatementEnd throw_(Class<? extends Throwable> throwableType, Object... parameters);
  public StatementEnd throw_(String exceptionVarName);
  
  public StatementEnd label(String label);
  
  public StatementEnd break_();
  public StatementEnd break_(String label);
  
  public StatementEnd continue_();
  public StatementEnd continue_(String label);

  public StatementEnd returnVoid();

  public Statement codeComment(String comment);
}
