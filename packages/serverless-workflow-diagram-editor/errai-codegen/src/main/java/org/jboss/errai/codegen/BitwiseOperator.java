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
public enum BitwiseOperator implements Operator {
  And("&", 6),
  Or("|", 4),
  Xor("^", 5),
  ShiftRight(">>", 9),
  UnsignedShiftRight(">>>", 9),
  ShiftLeft("<<", 9);

  private final Operator operator;

  BitwiseOperator(final String canonicalString, final int operatorPrecedence) {
    operator = new OperatorImpl(canonicalString, operatorPrecedence);
  }

  @Override
  public String getCanonicalString() {
    return operator.getCanonicalString();
  }

  @Override
  public int getOperatorPrecedence() {
    return operator.getOperatorPrecedence();
  }

  @Override
  public boolean isHigherPrecedenceThan(final Operator operator) {
    return operator.getOperatorPrecedence() < getOperatorPrecedence();
  }

  @Override
  public boolean isEqualOrHigherPrecedenceThan(final Operator operator) {
    return operator.getOperatorPrecedence() <= getOperatorPrecedence();
  }

  @Override
  public boolean canBeApplied(final MetaClass clazz) {
    return operator.canBeApplied(clazz);
  }

  @Override
  public void assertCanBeApplied(final MetaClass clazz) {
    operator.assertCanBeApplied(clazz);
  }
}
