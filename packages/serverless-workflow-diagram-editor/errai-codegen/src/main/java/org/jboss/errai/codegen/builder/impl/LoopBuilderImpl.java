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

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.BooleanOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.BuildCallback;
import org.jboss.errai.codegen.builder.ContextualLoopBuilder;
import org.jboss.errai.codegen.builder.LoopBuilder;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.WhileBuilder;
import org.jboss.errai.codegen.builder.callstack.CallElement;
import org.jboss.errai.codegen.builder.callstack.ConditionalBlockCallElement;
import org.jboss.errai.codegen.builder.callstack.DeferredCallElement;
import org.jboss.errai.codegen.control.DoWhileLoop;
import org.jboss.errai.codegen.control.ForLoop;
import org.jboss.errai.codegen.control.ForeachLoop;
import org.jboss.errai.codegen.control.WhileLoop;
import org.jboss.errai.codegen.literal.NullLiteral;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * StatementBuilder to generate loops.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class LoopBuilderImpl extends AbstractStatementBuilder implements ContextualLoopBuilder, LoopBuilder {

  protected LoopBuilderImpl(Context context, CallElementBuilder callElementBuilder) {
    super(context, callElementBuilder);
  }

  // foreach loop
  @Override
  public BlockBuilder<StatementEnd> foreach(String loopVarName) {
    return foreach(loopVarName, (MetaClass) null);
  }

  @Override
  public BlockBuilder<StatementEnd> foreach(String loopVarName, Class<?> loopVarType) {
    return foreach(loopVarName, MetaClassFactory.get(loopVarType));
  }

  @Override
  public BlockBuilder<StatementEnd> foreach(final String loopVarName, final MetaClass loopVarType) {
    final BlockStatement body = new BlockStatement();
    appendCallElement(foreachCallElement(body, loopVarName, loopVarType, false));
    return createLoopBody(body);
  }
  
  private CallElement foreachCallElement(final BlockStatement body, final String loopVarName, final MetaClass loopVarType, final boolean nullSafe) {
    return new DeferredCallElement((writer, context, statement) -> {
        GenUtil.assertIsIterable(statement);
        final Variable loopVar = createForEachLoopVar(statement, loopVarName, loopVarType, context);
        final String collection = writer.getCallString();
        writer.reset();
        writer.append(new ForeachLoop(loopVar, collection, body, nullSafe).generate(Context.create(context)));
    });
  }
  
  @Override
  public BlockBuilder<StatementEnd> foreachIfNotNull(String loopVarName) {
    return foreachIfNotNull(loopVarName, (MetaClass) null);
  }

  @Override
  public BlockBuilder<StatementEnd> foreachIfNotNull(String loopVarName, Class<?> loopVarType) {
    return foreachIfNotNull(loopVarName, MetaClassFactory.get(loopVarType));
  }

  @Override
  public BlockBuilder<StatementEnd> foreachIfNotNull(String loopVarName, MetaClass loopVarType) {
    final BlockStatement body = new BlockStatement();
    appendCallElement(foreachCallElement(body, loopVarName, loopVarType, true));
    return createLoopBody(body);
  }

  // do while loop
  @Override
  public BlockBuilder<WhileBuilder> do_() {
    final BlockStatement body = new BlockStatement();

    return new BlockBuilderImpl<WhileBuilder>(body, new BuildCallback<WhileBuilder>() {
      @Override
      public WhileBuilder callback(Statement statement) {
        return new WhileBuilder() {

          @Override
          public StatementEnd while_(final BooleanExpression condition) {
            appendCallElement(new ConditionalBlockCallElement(new DoWhileLoop(condition, body)));
            return LoopBuilderImpl.this;
          }

          @Override
          public StatementEnd while_() {
            while_(new BooleanExpressionBuilder());
            return LoopBuilderImpl.this;
          }

          @Override
          public StatementEnd while_(BooleanOperator op, Statement rhs) {
            while_(new BooleanExpressionBuilder(rhs, op));
            return LoopBuilderImpl.this;
          }

          @Override
          public StatementEnd while_(BooleanOperator op, Object rhs) {
            return while_(op, GenUtil.generate(context, rhs));
          }
        };
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }

  // while loop
  @Override
  public BlockBuilder<StatementEnd> while_() {
    return while_(new BooleanExpressionBuilder());
  }

  @Override
  public BlockBuilder<StatementEnd> while_(BooleanOperator op, Object rhs) {
    return while_(op, GenUtil.generate(context, rhs));
  }

  @Override
  public BlockBuilder<StatementEnd> while_(BooleanOperator op, Statement rhs) {
    if (rhs == null)
      rhs = NullLiteral.INSTANCE;
    return while_(new BooleanExpressionBuilder(rhs, op));
  }

  @Override
  public BlockBuilder<StatementEnd> while_(final BooleanExpression condition) {
    final BlockStatement body = new BlockStatement();
    appendCallElement(new ConditionalBlockCallElement(new WhileLoop(condition, body)));
    return createLoopBody(body);
  }

  // for loop
  @Override
  public BlockBuilder<StatementEnd> for_(BooleanExpression condition) {
    return for_((Statement) null, condition);
  }

  @Override
  public BlockBuilder<StatementEnd> for_(Statement initializer, BooleanExpression condition) {
    return for_(initializer, condition, null);
  }

  @Override
  public BlockBuilder<StatementEnd> for_(final Statement initializer, final BooleanExpression condition,
      final Statement countingExpression) {

    final BlockStatement body = new BlockStatement();
    appendCallElement(new ConditionalBlockCallElement(new ForLoop(condition, body, initializer, countingExpression)));
    return createLoopBody(body);
  }

  private BlockBuilder<StatementEnd> createLoopBody(BlockStatement body) {
    return new BlockBuilderImpl<StatementEnd>(body, new BuildCallback<StatementEnd>() {
      @Override
      public StatementEnd callback(Statement statement) {
        return LoopBuilderImpl.this;
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }

  private Variable createForEachLoopVar(Statement collection, String loopVarName, MetaClass providedLoopVarType, Context context) {
    // infer the loop variable type
    MetaClass loopVarType = MetaClassFactory.get(Object.class);
    final MetaParameterizedType parameterizedType = collection.getType().getParameterizedType();
    if (parameterizedType != null && parameterizedType.getTypeParameters().length != 0 && 
            parameterizedType.getTypeParameters()[0] instanceof MetaClass) {
      loopVarType = (MetaClass) parameterizedType.getTypeParameters()[0];
    }
    else if (collection.getType().getComponentType() != null) {
      loopVarType = collection.getType().getComponentType();
    }

    // try to use the provided loop variable type if possible (assignable from the inferred type)
    if (providedLoopVarType != null) {
      GenUtil.assertAssignableTypes(context, loopVarType, providedLoopVarType);
      loopVarType = providedLoopVarType;
    }

    final Variable loopVar = Variable.create(loopVarName, loopVarType);
    context.addVariable(loopVar);
    return loopVar;
  }

}
