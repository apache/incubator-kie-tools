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

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.StringExpression;
import org.jboss.errai.codegen.StringOperator;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Slava Pankov
 */
public class StringExpressionBuilder extends ExpressionBuilder<StringOperator> implements StringExpression {

  public StringExpressionBuilder() {}

  public StringExpressionBuilder(Statement rhs, StringOperator operator) {
    super(rhs, operator);
  }

  public StringExpressionBuilder(Object rhs, StringOperator operator) {
    super(rhs, operator);
  }

  public StringExpressionBuilder(Statement lhs, Statement rhs, StringOperator operator) {
    super(lhs, rhs, operator);
  }

  public StringExpressionBuilder(Object lhs, Object rhs, StringOperator operator) {
    super(lhs, rhs, operator);
  }

  public static StringExpression create(Statement lhs) {
    return new StringExpressionBuilder(lhs, null, null);
  }

  public static StringExpression create(StringOperator operator, Object rhs) {
    return new StringExpressionBuilder(rhs, operator);
  }

  public static StringExpression create(Object lhs, StringOperator operator, Object rhs) {
    return new StringExpressionBuilder(lhs, rhs, operator);
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

  private Class<?> promote(Class<?> lhs, Class<?> rhs) {
    return String.class;
  }
}
