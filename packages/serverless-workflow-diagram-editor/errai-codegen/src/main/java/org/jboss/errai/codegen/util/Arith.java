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

import org.jboss.errai.codegen.ArithmeticExpression;
import org.jboss.errai.codegen.ArithmeticOperator;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.impl.ArithmeticExpressionBuilder;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class Arith {

  public static ArithmeticExpression expr(final Object lhs, final ArithmeticOperator operator, final Object rhs) {
    return ArithmeticExpressionBuilder.create(lhs, operator, rhs);
  }

  public static ArithmeticExpression expr(final ArithmeticOperator operator, final Object rhs) {
    return ArithmeticExpressionBuilder.create(operator, rhs);
  }

  public static ArithmeticExpression expr(final Statement lhs) {
    return ArithmeticExpressionBuilder.create(lhs);
  }
}
