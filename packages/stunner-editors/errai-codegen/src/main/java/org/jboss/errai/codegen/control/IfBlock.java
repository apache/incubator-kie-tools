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


package org.jboss.errai.codegen.control;

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.builder.impl.BooleanExpressionBuilder;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class IfBlock extends AbstractConditionalBlock {
  private final BlockStatement elseBlock = new BlockStatement();
  private IfBlock elseIfBlock;

  public IfBlock(BooleanExpression condition) {
    super(condition);
  }

  @Override
  public BooleanExpressionBuilder getCondition() {
    return (BooleanExpressionBuilder) super.getCondition();
  }

  public BlockStatement getElseBlock() {
    return elseBlock;
  }

  public IfBlock getElseIfBlock() {
    return elseIfBlock;
  }

  public void setElseIfBlock(IfBlock elseIfBlock) {
    this.elseIfBlock = elseIfBlock;
  }
  
  String generatedCache;

  @Override
  public String generate(Context context) {
    if (generatedCache != null) return generatedCache;
    
    final StringBuilder builder = new StringBuilder("if ");
    builder.append("(").append(getCondition().generate(context)).append(") ");

    builder.append("{\n");

    if (getBlock() != null) {
      builder.append(getBlock().generate(Context.create(context)));
    }

    builder.append("\n} ");

    if (elseIfBlock != null) {
      builder.append("else ").append(elseIfBlock.generate(Context.create(context)));
      return builder.toString();
    }

    if (elseBlock != null && !elseBlock.isEmpty()) {
      builder.append("else {\n").append(elseBlock.generate(Context.create(context))).append("\n} ");
      return builder.toString();
    }

    return generatedCache = builder.toString();
  }
}
