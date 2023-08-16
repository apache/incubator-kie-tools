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

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class DoWhileLoop extends AbstractConditionalBlock {
  public DoWhileLoop(BooleanExpression condition, BlockStatement block) {
    super(condition, block);
  }

  @Override
  public String generate(Context context) {
    final StringBuilder builder = new StringBuilder("do {\n");

    if (getBlock() != null) {
      builder.append(getBlock().generate(context));
    }

    builder.append("\n} while (").append(getCondition().generate(context)).append(");\n");

    return builder.toString();
  }
}
