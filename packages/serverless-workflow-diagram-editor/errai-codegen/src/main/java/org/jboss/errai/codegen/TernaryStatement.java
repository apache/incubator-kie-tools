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


package org.jboss.errai.codegen;

import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock
 */
public class TernaryStatement extends AbstractStatement {
  private final BooleanExpression condition;
  private final Statement trueStatement;
  private final Statement falseStatement;
  private MetaClass returnType;

  public TernaryStatement(final BooleanExpression condition,
                          final Statement trueStatement,
                          final Statement falseStatement) {
    this.condition = condition;
    this.trueStatement = trueStatement;
    this.falseStatement = falseStatement;
  }

  @Override
  public String generate(final Context context) {
    final String conditionString = condition.generate(context);
    final String trueString = trueStatement.generate(context);
    final String falseString = falseStatement.generate(context);

    returnType = trueStatement.getType();
    if (falseStatement.getType().isAssignableFrom(returnType)) {
      returnType = falseStatement.getType();
    }

    return conditionString.concat(" ? ").concat(trueString).concat(" : ").concat(falseString);
  }

  @Override
  public MetaClass getType() {
    return returnType;
  }
}
