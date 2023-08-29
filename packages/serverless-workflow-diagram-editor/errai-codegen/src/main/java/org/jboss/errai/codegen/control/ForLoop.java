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
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ForLoop extends AbstractConditionalBlock {
  private Statement initializer;
  private Statement afterBlock;

  public ForLoop(BooleanExpression condition, BlockStatement block) {
    super(condition, block);
  }

  public ForLoop(BooleanExpression condition, BlockStatement block, Statement initializer, Statement afterBlock) {
    super(condition, block);
    this.initializer = initializer;
    this.afterBlock = afterBlock;
  }
  
  String generatedCache;
  @Override
  public String generate(Context context) {
    if (generatedCache != null) return generatedCache;
    final StringBuilder builder = new StringBuilder("for (");

    if (initializer != null) {
      builder.append(initializer.generate(context));
      if (initializer instanceof Variable) {
        context.addVariable((Variable) initializer);
      }
    }
    
    if (!builder.toString().endsWith(";"))
      builder.append(";");
    
    builder.append(" ").append(getCondition().generate(context)).append("; ");

    if (afterBlock != null) {
      String afterOutput = afterBlock.generate(context).trim();

      if (afterOutput.endsWith(";")) {
        afterOutput = afterOutput.substring(0, afterOutput.length() - 1);
      }

      builder.append(afterOutput);
    }

    builder.append(") {\n")
        .append(getBlock().generate(Context.create(context)))
        .append("\n}\n");

    return generatedCache = builder.toString();
  }
}
