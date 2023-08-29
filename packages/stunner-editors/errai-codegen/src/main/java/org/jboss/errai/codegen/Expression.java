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

/**
 * Represents an expression. The LHS can either be a {@link Statement} 
 * or the generated {@link String} thereof.
 * 
 * @param <T> the type of operator to be used for the expression.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface Expression<T extends Operator> extends Statement {
  
  public Statement getLhs();
  public void setLhs(Statement lhs);
  
  public String getLhsExpr();
  public void setLhsExpr(String lhsExpr);
  
  public Statement getRhs();
  public void setRhs(Statement rhs);
  
  public T getOperator();
  public void setOperator(T operator);
}
