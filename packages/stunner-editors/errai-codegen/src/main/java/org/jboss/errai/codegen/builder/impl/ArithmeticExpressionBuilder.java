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

import org.jboss.errai.codegen.ArithmeticExpression;
import org.jboss.errai.codegen.ArithmeticOperator;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ArithmeticExpressionBuilder extends ExpressionBuilder<ArithmeticOperator> implements ArithmeticExpression {

  public ArithmeticExpressionBuilder() {}

  public ArithmeticExpressionBuilder(Statement rhs, ArithmeticOperator operator) {
    super(rhs, operator);
  }

  public ArithmeticExpressionBuilder(Object rhs, ArithmeticOperator operator) {
    super(rhs, operator);
  }

  public ArithmeticExpressionBuilder(Statement lhs, Statement rhs, ArithmeticOperator operator) {
    super(lhs, rhs, operator);
  }

  public ArithmeticExpressionBuilder(Object lhs, Object rhs, ArithmeticOperator operator) {
    super(lhs, rhs, operator);
  }

  public static ArithmeticExpression create(Statement lhs) {
    return new ArithmeticExpressionBuilder(lhs, null, null);
  }

  public static ArithmeticExpression create(ArithmeticOperator operator, Object rhs) {
    return new ArithmeticExpressionBuilder(rhs, operator);
  }

  public static ArithmeticExpression create(Object lhs, ArithmeticOperator operator, Object rhs) {
    return new ArithmeticExpressionBuilder(lhs, rhs, operator);
  }

  @Override
  public MetaClass getType() {
    Class<?> lhsType = null;
    if (lhs != null) {
      lhsType = lhs.getType().asUnboxed().asClass();
    }
    Class<?> rhsType = null;
    if (rhs != null) {
      rhsType = rhs.getType().asUnboxed().asClass();
    }
    return MetaClassFactory.get(promote(lhsType, rhsType));
  }

  /**
   * Implements unary or binary numeric promotion <a href=
   * "http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.6.2"
   * >according to the JLS</a>.
   *
   * @param lhs
   *          the left-hand-side type as an unboxed numeric type. Can be null if
   *          there is no left-hand-side operand.
   * @param lhs
   *          the right-hand-side type as an unboxed numeric type. Can be null
   *          if there is no right-hand-side operand.
   * @return The unboxed numeric class type that the expression results in.
   */
  private Class<?> promote(Class<?> lhs, Class<?> rhs) {
    if (lhs == double.class || rhs == double.class) {
      return double.class;
    }
    if (lhs == float.class || rhs == float.class) {
      return float.class;
    }
    if (lhs == long.class || rhs == long.class) {
      return long.class;
    }
    return int.class;
  }
}
