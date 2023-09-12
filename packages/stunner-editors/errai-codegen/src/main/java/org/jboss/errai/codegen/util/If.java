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

import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Mike Brock
 */
public class If {
  public static BlockBuilder<ElseBlockBuilder> cond(final Statement condition) {
    return StatementBuilder.create().if_(Bool.expr(condition));
  }

  public static BlockBuilder<ElseBlockBuilder> cond(final BooleanExpression condition) {
    return StatementBuilder.create().if_(condition);
  }

  public static BlockBuilder<ElseBlockBuilder> isNull(final Object statement) {
    return StatementBuilder.create().if_(Bool.isNull(statement));
  }

  public static BlockBuilder<ElseBlockBuilder> isNotNull(final Object statement) {
    return StatementBuilder.create().if_(Bool.isNotNull(statement));
  }

  public static BlockBuilder<ElseBlockBuilder> not(final Statement statement) {
    return StatementBuilder.create().if_(Bool.notExpr(statement));
  }

  public static BlockBuilder<ElseBlockBuilder> isEqual(final Object lhs,
                                                       final Object rhs) {
    return StatementBuilder.create().if_(Bool.equals(lhs, rhs));
  }

  public static BlockBuilder<ElseBlockBuilder> notEquals(final Object lhs,
                                                         final Object rhs) {
    return StatementBuilder.create().if_(Bool.notEquals(lhs, rhs));
  }

  public static BlockBuilder<ElseBlockBuilder> idEquals(final Object lhs,
                                                         final Object rhs) {
    return StatementBuilder.create().if_(Bool.equals(lhs, rhs));
  }

  public static BlockBuilder<ElseBlockBuilder> idNotEquals(final Object lhs,
                                                         final Object rhs) {
    return StatementBuilder.create().if_(Bool.notEquals(lhs, rhs));
  }

  public static BlockBuilder<ElseBlockBuilder> objEquals(final Object lhs,
                                                         final Object rhs) {
    return StatementBuilder.create().if_(Bool.expr(Stmt.load(lhs).invoke("equals", rhs)));
  }

  public static BlockBuilder<ElseBlockBuilder> safeObjEquals(final Object lhs,
                                                             final Object rhs) {
    return StatementBuilder.create().if_(Bool.expr(Bool.and(Bool.isNotNull(lhs),
            Bool.expr(Stmt.load(lhs).invoke("equals", rhs)))));
  }

  public static BlockBuilder<ElseBlockBuilder> objNotEquals(final Object lhs,
                                                         final Object rhs) {
    return StatementBuilder.create().if_(Bool.notExpr(Bool.expr(Stmt.load(lhs).invoke("equals", rhs))));
  }

  public static BlockBuilder<ElseBlockBuilder> safeObjNotEquals(final Object lhs,
                                                             final Object rhs) {
    return StatementBuilder.create().if_(
        Bool.expr(Bool.and(Bool.isNotNull(lhs),
            Bool.expr(Bool.notExpr(Stmt.load(lhs).invoke("equals", rhs))))));
  }

  public static BlockBuilder<ElseBlockBuilder> instanceOf(final Object lhs,
                                                          final Class<?> type) {

    return instanceOf(lhs, MetaClassFactory.get(type));
  }

  public static BlockBuilder<ElseBlockBuilder> instanceOf(final Object lhs,
                                                          final MetaClass type) {
    return StatementBuilder.create().if_(Bool.instanceOf(lhs, type));
  }

}
