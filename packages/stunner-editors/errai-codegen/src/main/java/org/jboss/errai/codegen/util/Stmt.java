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


package org.jboss.errai.codegen.util;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.VariableReference;
import org.jboss.errai.codegen.builder.ArrayInitializationBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.CaseBlockBuilder;
import org.jboss.errai.codegen.builder.CatchBlockBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.StatementBegin;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.VariableDeclarationStart;
import org.jboss.errai.codegen.builder.VariableReferenceContextualStatementBuilder;
import org.jboss.errai.codegen.builder.WhileBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class Stmt {

  /**
   * Create a new statement builder.
   * @return a new statement builder instance.
   */
  public static StatementBegin create() {
    return StatementBuilder.create();
  }
  
  public static StatementBegin create(final Context ctx) {
    return StatementBuilder.create(ctx);
  }

  public static ArrayInitializationBuilder newArray(final MetaClass componentType) {
    return StatementBuilder.create().newArray(componentType);
  }

  public static ArrayInitializationBuilder newArray(final MetaClass componentType,
                                                    final Object... dimensions) {
    return StatementBuilder.create().newArray(componentType, dimensions);
  }

  public static ArrayInitializationBuilder newArray(final Class<?> componentType) {
    return StatementBuilder.create().newArray(componentType);
  }

  public static ArrayInitializationBuilder newArray(final Class<?> componentType,
                                                    final Object... dimensions) {
    return StatementBuilder.create().newArray(componentType, dimensions);
  }

  public static BlockBuilder<WhileBuilder> do_() {
    return StatementBuilder.create().do_();
  }

  public static BlockBuilder<StatementEnd> while_(final BooleanExpression condition) {
    return StatementBuilder.create().while_(condition);
  }

  public static BlockBuilder<StatementEnd> for_(final BooleanExpression condition) {
    return StatementBuilder.create().for_(condition);
  }

  public static BlockBuilder<StatementEnd> for_(final Statement initializer,
                                                final BooleanExpression condition) {
    return StatementBuilder.create().for_(initializer, condition);
  }

  public static BlockBuilder<StatementEnd> for_(final Statement initializer,
                                                final BooleanExpression condition,
      final Statement countingExpression) {
    return StatementBuilder.create().for_(initializer, condition, countingExpression);
  }

  public static BlockBuilder<ElseBlockBuilder> if_(final BooleanExpression condition) {
    return StatementBuilder.create().if_(condition);
  }

  public static CaseBlockBuilder switch_(final Statement statement) {
    return StatementBuilder.create().switch_(statement);
  }

  public static BlockBuilder<CatchBlockBuilder> try_() {
    return StatementBuilder.create().try_();
  }

  public static VariableDeclarationStart<StatementBegin> declareVariable(final Class<?> type) {
    return StatementBuilder.create().declareVariable(type);
  }

  public static VariableDeclarationStart<StatementBegin> declareVariable(final MetaClass type) {
    return StatementBuilder.create().declareVariable(type);
  }

  public static StatementBuilder declareVariable(final VariableReference reference) {
    return StatementBuilder.create().declareVariable(reference.getName(), reference.getType());
  }

  public static StatementBuilder declareVariable(final String name,
                                                 final Class<?> type) {
    return StatementBuilder.create().declareVariable(name, type);
  }

  public static StatementBuilder declareVariable(final String name,
                                                 final TypeLiteral<?> type) {
    return StatementBuilder.create().declareVariable(name, type);
  }

  public static StatementBuilder declareVariable(final String name,
                                                 final Object initialization) {
    return StatementBuilder.create().declareVariable(name, initialization);
  }

  public static StatementBuilder declareVariable(final VariableReference ref,
                                                 final Object initialization) {
    return StatementBuilder.create().declareVariable(ref.getName(), ref.getType(), initialization);
  }

  public static StatementBuilder declareVariable(final String name,
                                                 final Class<?> type,
                                                 final Object initialization) {
    return StatementBuilder.create().declareVariable(name, type, initialization);
  }

  public static StatementBuilder declareVariable(final String name,
                                                 final MetaClass type,
                                                 final Object initialization) {
    return StatementBuilder.create().declareVariable(name, type, initialization);
  }


  public static StatementBuilder declareVariable(final String name,
                                                 final TypeLiteral<?> type,
                                                 final Object initialization) {
    return StatementBuilder.create().declareVariable(name, type, initialization);
  }


  public static StatementBuilder declareFinalVariable(final String name,
                                                 final Class<?> type) {
    return StatementBuilder.create().declareFinalVariable(name, type);
  }

  public static StatementBuilder declareFinalVariable(final String name,
                                                 final TypeLiteral<?> type) {
    return StatementBuilder.create().declareFinalVariable(name, type);
  }

  public static StatementBuilder declareFinalVariable(final VariableReference ref,
                                                 final Object initialization) {
    return StatementBuilder.create().declareFinalVariable(ref.getName(), ref.getType(), initialization);
  }

  public static StatementBuilder declareFinalVariable(final String name,
                                                 final Class<?> type,
                                                 final Object initialization) {
    return StatementBuilder.create().declareFinalVariable(name, type, initialization);
  }

  public static StatementBuilder declareFinalVariable(final String name,
                                                 final MetaClass type,
                                                 final Object initialization) {
    return StatementBuilder.create().declareFinalVariable(name, type, initialization);
  }

  public static StatementBuilder declareFinalVariable(final String name,
                                                 final TypeLiteral<?> type,
                                                 final Object initialization) {
    return StatementBuilder.create().declareFinalVariable(name, type, initialization);
  }
  
  public static VariableReferenceContextualStatementBuilder loadVariable(final VariableReference reference) {
    return loadVariable(reference.getName(), reference.getIndexes());
  }

  public static VariableReferenceContextualStatementBuilder loadVariable(final String name,
                                                                         final Object... indexes) {
    return StatementBuilder.create().loadVariable(name, indexes);
  }

  public static VariableReferenceContextualStatementBuilder loadClassMember(final String name,
                                                                            final Object... indexes) {
    return StatementBuilder.create().loadClassMember(name, indexes);
  }

  public static ContextualStatementBuilder loadLiteral(final Object o) {
    return StatementBuilder.create().loadLiteral(o);
  }

  public static ContextualStatementBuilder load(final Object o) {
    return StatementBuilder.create().load(o);
  }

  public static ContextualStatementBuilder loadClassReference(final Object o) {
    return StatementBuilder.create().loadClassReference(o);
  }

  public static ContextualStatementBuilder invokeStatic(final MetaClass clazz,
                                                        final String methodName,
                                                        final Object... parameters) {
    return StatementBuilder.create().invokeStatic(clazz, methodName, parameters);
  }

  public static ContextualStatementBuilder invokeStatic(final Class<?> clazz,
                                                        final String methodName,
                                                        final Object... parameters) {
    return StatementBuilder.create().invokeStatic(clazz, methodName, parameters);
  }

  public static ContextualStatementBuilder loadStatic(final Class<?> clazz,
                                                      final String fieldName) {
    return StatementBuilder.create().loadStatic(clazz, fieldName);
  }

  public static ContextualStatementBuilder loadStatic(final MetaClass clazz,
                                                      final String fieldName) {
    return StatementBuilder.create().loadStatic(clazz, fieldName);
  }

  public static ContextualStatementBuilder castTo(final Class<?> clazz,
                                                  final Statement stmt) {
    return StatementBuilder.create().castTo(clazz, stmt);
  }

  public static ContextualStatementBuilder castTo(final MetaClass clazz,
                                                  final Statement stmt) {
    return StatementBuilder.create().castTo(clazz, stmt);
  }

  public static ContextualStatementBuilder nestedCall(final Statement statement) {
    return StatementBuilder.create().nestedCall(statement);
  }

  public static ObjectBuilder newObject(final Class<?> type) {
    return StatementBuilder.create().newObject(type);
  }

  public static ObjectBuilder newObject(final MetaClass type) {
    return StatementBuilder.create().newObject(type);
  }

  public static ObjectBuilder newObject(final TypeLiteral<?> type) {
    return StatementBuilder.create().newObject(type);
  }

  public static Statement newObject(final Class<?> type, Object... parms) {
    return StatementBuilder.create().newObject(type, parms);
  }

  public static Statement newObject(final MetaClass type, Object... parms) {
    return StatementBuilder.create().newObject(type, parms);
  }

  public static Statement newObject(final TypeLiteral<?> type, Object... parms) {
    return StatementBuilder.create().newObject(type, parms);
  }

  public static StatementEnd throw_(final Class<? extends Throwable> throwableType,
                                    final Object... parameters) {
    return StatementBuilder.create().throw_(throwableType, parameters);
  }

  public static StatementEnd throw_(final String exceptionVarName) {
    return StatementBuilder.create().throw_(exceptionVarName);
  }

  public static StatementEnd label(final String label) {
    return StatementBuilder.create().label(label);
  }

  public static StatementEnd break_() {
    return StatementBuilder.create().break_();
  }

  public static StatementEnd break_(final String label) {
    return StatementBuilder.create().break_(label);
  }

  public static StatementEnd continue_() {
    return StatementBuilder.create().continue_();
  }

  public static StatementEnd continue_(final String label) {
    return StatementBuilder.create().continue_(label);
  }

  public static StatementEnd returnVoid() {
    return StatementBuilder.create().returnVoid();
  }

  public static Statement codeComment(final String comment) {
    return StatementBuilder.create().codeComment(comment);
  }
}
