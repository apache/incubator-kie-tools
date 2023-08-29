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

import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.BooleanOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.BuildCallback;
import org.jboss.errai.codegen.builder.ContextualIfBlockBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.IfBlockBuilder;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.callstack.ConditionalBlockCallElement;
import org.jboss.errai.codegen.control.IfBlock;
import org.jboss.errai.codegen.literal.NullLiteral;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * StatementBuilder to generate if blocks.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class IfBlockBuilderImpl extends AbstractStatementBuilder implements ContextualIfBlockBuilder, IfBlockBuilder,
    ElseBlockBuilder {
  
  private IfBlock ifBlock;

  protected IfBlockBuilderImpl(Context context, CallElementBuilder callElementBuilder) {
    super(context, callElementBuilder);
  }

  protected IfBlockBuilderImpl(Context context, CallElementBuilder callElementBuilder, IfBlock ifBlock) {
    super(context, callElementBuilder);
    this.ifBlock = ifBlock;
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> if_() {
    return if_(new BooleanExpressionBuilder());
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> ifNot() {
    return if_(new BooleanExpressionBuilder().negate());
  }
  
  @Override
  public BlockBuilder<ElseBlockBuilder> if_(BooleanOperator op, Statement rhs) {
    if (rhs == null)
      rhs = NullLiteral.INSTANCE;
    return if_(new BooleanExpressionBuilder(rhs, op));
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> if_(BooleanOperator op, Object rhs) {
    final Statement rhsStatement = GenUtil.generate(context, rhs);
    return if_(new BooleanExpressionBuilder(rhsStatement, op));
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> if_(final BooleanExpression condition) {
    ifBlock = new IfBlock(condition);
    appendCallElement(new ConditionalBlockCallElement(ifBlock));

    return new BlockBuilderImpl<ElseBlockBuilder>(ifBlock.getBlock(), new BuildCallback<ElseBlockBuilder>() {
      @Override
      public ElseBlockBuilder callback(Statement statement) {
        return IfBlockBuilderImpl.this;
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }

  @Override
  public BlockBuilder<StatementEnd> else_() {
    return new BlockBuilderImpl<StatementEnd>(ifBlock.getElseBlock(), new BuildCallback<StatementEnd>() {
      @Override
      public StatementEnd callback(Statement statement) {
        return IfBlockBuilderImpl.this;
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> elseif_(Statement lhs) {
    return elseif_(lhs, null, null);
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> elseif_(Statement lhs, BooleanOperator op, Statement rhs) {
    final IfBlock elseIfBlock = new IfBlock(new BooleanExpressionBuilder(lhs, rhs, op));
    ifBlock.setElseIfBlock(elseIfBlock);
    return elseif_(elseIfBlock);
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> elseif_(Statement lhs, BooleanOperator op, Object rhs) {
    return elseif_(lhs, op, GenUtil.generate(context, rhs));
  }

  private BlockBuilder<ElseBlockBuilder> elseif_(final IfBlock elseIfBlock) {
    return new BlockBuilderImpl<ElseBlockBuilder>(elseIfBlock.getBlock(), new BuildCallback<ElseBlockBuilder>() {
      @Override
      public ElseBlockBuilder callback(Statement statement) {
        return new IfBlockBuilderImpl(context, callElementBuilder, elseIfBlock);
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }
}
