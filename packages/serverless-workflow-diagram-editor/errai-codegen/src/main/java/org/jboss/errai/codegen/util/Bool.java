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
import org.jboss.errai.codegen.BooleanOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.impl.BooleanExpressionBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class Bool {

  public static BooleanExpression expr(final Object lhs, final BooleanOperator operator, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, operator, rhs);
  }

  public static BooleanExpression expr(final BooleanOperator operator, final Object rhs) {
    return BooleanExpressionBuilder.create(operator, rhs);
  }

  public static BooleanExpression expr(final Statement lhs) {
    return BooleanExpressionBuilder.create(lhs);
  }

  public static BooleanExpression notExpr(final Statement lhs) {
    return BooleanExpressionBuilder.create(new Statement() {
      @Override
      public String generate(final Context context) {
        return "!" + lhs.generate(context);
      }

      @Override
      public MetaClass getType() {
        return MetaClassFactory.get(Boolean.class);
      }
    });
  }

  public static BooleanExpression equals(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.Equals, rhs);
  }

  public static BooleanExpression notEquals(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.NotEquals, rhs);
  }

  public static BooleanExpression or(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.Or, rhs);
  }

  public static BooleanExpression and(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.And, rhs);
  }

  public static BooleanExpression instanceOf(final Object lhs, final Class<?> type) {
    return instanceOf(lhs, MetaClassFactory.get(type));
  }

  public static BooleanExpression instanceOf(final Object lhs, final MetaClass type) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.InstanceOf, MetaClassFactory.getAsStatement(type));
  }

  public static BooleanExpression greaterThanOrEqual(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.GreaterThanOrEqual, rhs);
  }

  public static BooleanExpression greaterThan(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.GreaterThan, rhs);
  }

  public static BooleanExpression lessThanOrEqual(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.LessThanOrEqual, rhs);
  }

  public static BooleanExpression lessThan(final Object lhs, final Object rhs) {
    return BooleanExpressionBuilder.create(lhs, BooleanOperator.LessThan, rhs);
  }

  public static BooleanExpression isNotNull(final Object test) {
    return Bool.notEquals(test, null);
  }

  public static BooleanExpression isNull(final Object test) {
    return Bool.equals(test, null);
  }
}
