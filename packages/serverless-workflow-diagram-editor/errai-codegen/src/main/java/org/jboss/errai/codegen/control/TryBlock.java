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

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.errai.codegen.AbstractStatement;
import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Variable;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class TryBlock extends AbstractStatement {
  private final BlockStatement block = new BlockStatement();
  private final Map<Variable, BlockStatement> catchBlocks = new LinkedHashMap<Variable, BlockStatement>();
  private BlockStatement finallyBlock = null;

  public BlockStatement getBlock() {
    return block;
  }

  public void addCatchBlock(Variable exception) {
    catchBlocks.put(exception, new BlockStatement());
  }
  
  public BlockStatement getCatchBlock(Variable exceptionVar) {
    return catchBlocks.get(exceptionVar);
  }

  public BlockStatement getFinallyBlock() {
    if (finallyBlock == null)
      finallyBlock = new BlockStatement();
    
    return finallyBlock;
  }

  String generatedCache;
  @Override
  public String generate(Context context) {
    if (generatedCache != null) return generatedCache;
    final StringBuilder buf = new StringBuilder("try {\n");
    buf.append(block.generate(context)).append("\n} ");

    if (!catchBlocks.isEmpty()) {
      for (final Variable exception : catchBlocks.keySet()) {
        final Context ctx = Context.create(context).addVariable(exception);
        buf.append("catch (").append(exception.generate(ctx)).append(") ")
            .append("{\n")
            .append(catchBlocks.get(exception).generate(ctx))
            .append("\n} ");
      }
    }
    else if (finallyBlock == null) {
      finallyBlock = new BlockStatement();
    }

    if (finallyBlock != null) {
      final Context ctx = Context.create(context);
      buf.append(" finally {\n").append(finallyBlock.generate(ctx)).append("\n}\n");
    }

    return generatedCache = buf.toString();
  }
}
