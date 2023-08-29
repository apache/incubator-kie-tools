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

import java.util.Arrays;

import org.jboss.errai.codegen.exception.InvalidExpressionException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class OperatorImpl implements Operator {

  private final String canonicalString;
  private final int operatorPrecedence;
  private final MetaClass[] constraints;

  OperatorImpl(String canonicalString, int operatorPrecedence, Class<?>... constraints) {
    this.canonicalString = canonicalString;
    this.operatorPrecedence = operatorPrecedence;
    this.constraints = MetaClassFactory.fromClassArray(constraints);
  }

  @Override
  public String getCanonicalString() {
    return canonicalString;
  }

  @Override
  public int getOperatorPrecedence() {
    return operatorPrecedence;
  }

  @Override
  public boolean isHigherPrecedenceThan(Operator operator) {
    return operator.getOperatorPrecedence() < getOperatorPrecedence();
  }

  @Override
  public boolean isEqualOrHigherPrecedenceThan(Operator operator) {
    return operator.getOperatorPrecedence() <= getOperatorPrecedence();
  }

  @Override
  public void assertCanBeApplied(MetaClass clazz) {
    if (!canBeApplied(clazz)) {
      throw new InvalidExpressionException("Not a valid type for operator '" +
          canonicalString + "': " + clazz.getFullyQualifiedName());
    }
  }

  @Override
  public boolean canBeApplied(MetaClass clazz) {
    if (constraints.length == 0) return true;
    return Arrays.stream(constraints).anyMatch(mc -> mc.asBoxed().isAssignableFrom(clazz.asBoxed()));
  }
}
