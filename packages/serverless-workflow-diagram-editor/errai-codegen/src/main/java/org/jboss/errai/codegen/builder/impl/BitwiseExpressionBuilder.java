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

import org.jboss.errai.codegen.BitwiseOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Expression;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BitwiseExpressionBuilder extends ExpressionBuilder<BitwiseOperator> {
  public BitwiseExpressionBuilder(final Statement lhs, final Statement rhs, final BitwiseOperator operator) {
    super(lhs, rhs, operator);
  }

  public BitwiseExpressionBuilder(final Object lhs, final Object rhs, final BitwiseOperator operator) {
    super(lhs, rhs, operator);
  }

  public static Expression<BitwiseOperator> create(final Statement lhs) {
    return new BitwiseExpressionBuilder(lhs, null, null);
  }

  public static Expression<BitwiseOperator> create(final BitwiseOperator operator, final Object rhs) {
    return create(null, operator, rhs);
  }

  public static Expression<BitwiseOperator> create(final Object lhs, final BitwiseOperator operator, final Object rhs) {
    return new BitwiseExpressionBuilder(lhs, rhs, operator);
  }

  public static Expression<BitwiseOperator> createUnqualifying(final Object lhs, final BitwiseOperator operator, final Object rhs) {
    final BitwiseExpressionBuilder bitwiseExpressionBuilder = new BitwiseExpressionBuilder(lhs, rhs, operator);
    bitwiseExpressionBuilder.qualifyingBrackets = false;
    return bitwiseExpressionBuilder;
  }

  @Override
  public String generate(final Context context) {
    if (operator == null) {
      lhs = GenUtil.generate(context, lhs);
      lhs = GenUtil.convert(context, lhs, MetaClassFactory.get(Boolean.class));
    }

    return super.generate(context);
  }

  @Override
  public MetaClass getType() {
    return lhs.getType();
  }
}
