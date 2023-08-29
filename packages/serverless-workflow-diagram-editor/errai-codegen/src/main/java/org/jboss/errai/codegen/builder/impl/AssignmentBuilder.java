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

import java.util.Arrays;

import org.jboss.errai.codegen.AssignmentOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.VariableReference;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * {@link StatementBuilder} that generates {@link VariableReference} assignments.
 * 
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class AssignmentBuilder implements Statement {
  protected boolean isFinal;

  protected final AssignmentOperator operator;
  protected final VariableReference reference;
  protected final Statement statement;

  public AssignmentBuilder(final boolean isFinal,
                           final AssignmentOperator operator,
                           final VariableReference reference,
                           final Statement statement) {
    this.isFinal = isFinal;
    this.operator = operator;
    this.reference = reference;
    this.statement = statement;
  }

  @Override
  public String generate(final Context context) {
    MetaClass referenceType = reference.getType();
    final Statement[] indexes = reference.getIndexes();
    if (indexes!=null) {
      for (final Statement index : indexes) {
        if (!referenceType.isArray())
          throw new InvalidTypeException("Variable is not a " + indexes.length + "-dimensional array!");
        referenceType = referenceType.getComponentType();
      } 
    }
    operator.assertCanBeApplied(referenceType);
    operator.assertCanBeApplied(statement.getType());

    return  reference.generate(context) + generateIndexes(context, indexes) +
        " " + operator.getCanonicalString() + " " + statement.generate(context);
  }

  private String generateIndexes(final Context context, final Statement[] indexes) {
    if (indexes==null || indexes.length == 0) return "";
   
    final StringBuilder buf = new StringBuilder(128);
    Arrays.stream(indexes).forEach(index -> buf.append("[").append(index.generate(context)).append("]"));
    return buf.toString();
  }

  @Override
  public MetaClass getType() {
    return reference.getType();
  }
}
