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

import org.jboss.errai.codegen.AssignmentOperator;
import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.BooleanOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.CaseBlockBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.VariableReferenceContextualStatementBuilder;
import org.jboss.errai.codegen.builder.WhileBuilder;
import org.jboss.errai.codegen.builder.callstack.AssignVariable;
import org.jboss.errai.codegen.builder.callstack.LoadField;
import org.jboss.errai.codegen.builder.callstack.MethodCall;
import org.jboss.errai.codegen.builder.callstack.ReturnValue;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;

/**
 * Implementation of the {@link ContextualStatementBuilder}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock <cbrock@redhat.com>
 */
public class ContextualStatementBuilderImpl extends AbstractStatementBuilder implements ContextualStatementBuilder,
    VariableReferenceContextualStatementBuilder {

  protected ContextualStatementBuilderImpl(Context context, CallElementBuilder callElementBuilder) {
    super(context, callElementBuilder);
  }

  // Invocation
  @Override
  public ContextualStatementBuilder invoke(MetaMethod method, Object... parameters) {
    return invoke(method.getName(), parameters);
  }

  @Override
  public ContextualStatementBuilder invoke(String methodName, Object... parameters) {
    appendCallElement(new MethodCall(methodName, parameters));
    return this;
  }

  @Override
  public VariableReferenceContextualStatementBuilder loadField(MetaField field) {
    appendCallElement(new LoadField(field.getName()));
    return this;
  }

  @Override
  public VariableReferenceContextualStatementBuilder loadField(String fieldName) {
    appendCallElement(new LoadField(fieldName));
    return this;
  }

  // Looping
  @Override
  public BlockBuilder<StatementEnd> foreach(String loopVarName) {
    return new LoopBuilderImpl(context, callElementBuilder).foreach(loopVarName);
  }

  @Override
  public BlockBuilder<StatementEnd> foreach(String loopVarName, Class<?> loopVarType) {
    return new LoopBuilderImpl(context, callElementBuilder).foreach(loopVarName, loopVarType);
  }

  @Override
  public BlockBuilder<StatementEnd> foreach(String loopVarName, MetaClass loopVarType) {
    return new LoopBuilderImpl(context, callElementBuilder).foreach(loopVarName, loopVarType);
  }
  
  @Override
  public BlockBuilder<StatementEnd> foreachIfNotNull(String loopVarName) {
    return new LoopBuilderImpl(context, callElementBuilder).foreachIfNotNull(loopVarName);
  }

  @Override
  public BlockBuilder<StatementEnd> foreachIfNotNull(String loopVarName, Class<?> loopVarType) {
    return new LoopBuilderImpl(context, callElementBuilder).foreachIfNotNull(loopVarName, loopVarType);
  }

  @Override
  public BlockBuilder<StatementEnd> foreachIfNotNull(String loopVarName, MetaClass loopVarType) {
    return new LoopBuilderImpl(context, callElementBuilder).foreachIfNotNull(loopVarName, loopVarType);
  }

  @Override
  public BlockBuilder<StatementEnd> for_(Statement initializer, BooleanExpression condition) {
    return new LoopBuilderImpl(context, callElementBuilder).for_(initializer, condition);
  }

  @Override
  public BlockBuilder<StatementEnd> for_(Statement initializer, BooleanExpression condition, Statement afterBlock) {
    return new LoopBuilderImpl(context, callElementBuilder).for_(initializer, condition, afterBlock);
  }

  @Override
  public BlockBuilder<WhileBuilder> do_() {
    return new LoopBuilderImpl(context, callElementBuilder).do_();
  }

  @Override
  public BlockBuilder<StatementEnd> while_() {
    return new LoopBuilderImpl(context, callElementBuilder).while_();
  }

  @Override
  public BlockBuilder<StatementEnd> while_(BooleanOperator op, Statement rhs) {
    return new LoopBuilderImpl(context, callElementBuilder).while_(op, rhs);
  }

  @Override
  public BlockBuilder<StatementEnd> while_(BooleanOperator op, Object rhs) {
    return new LoopBuilderImpl(context, callElementBuilder).while_(op, rhs);
  }

  // If-Then-Else
  @Override
  public BlockBuilder<ElseBlockBuilder> if_() {
    return new IfBlockBuilderImpl(context, callElementBuilder).if_();
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> if_(BooleanOperator op, Statement rhs) {
    return new IfBlockBuilderImpl(context, callElementBuilder).if_(op, rhs);
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> if_(BooleanOperator op, Object rhs) {
    return new IfBlockBuilderImpl(context, callElementBuilder).if_(op, rhs);
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> ifNot() {
    return new IfBlockBuilderImpl(context, callElementBuilder).ifNot();
  }

  // Switch
  @Override
  public CaseBlockBuilder switch_() {
    return new SwitchBlockBuilderImpl(context, callElementBuilder).switch_();
  }

  // Value return
  @Override
  public StatementEnd returnValue() {
    appendCallElement(new ReturnValue());
    return this;
  }

  // Assignments
  @Override
  public StatementEnd assignValue(Object statement) {
    return assignValue(AssignmentOperator.Assignment, statement);
  }

  @Override
  public StatementEnd assignValue(AssignmentOperator operator, Object statement) {
    appendCallElement(new AssignVariable(operator, statement));
    return this;
  }

  @Override
  public String toString() {
    return "[Statement:" + callElementBuilder + "]";

  }

}
