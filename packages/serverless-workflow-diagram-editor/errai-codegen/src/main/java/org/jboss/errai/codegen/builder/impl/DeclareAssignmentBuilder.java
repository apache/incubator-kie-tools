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

import org.jboss.errai.codegen.AssignmentOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.VariableReference;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class DeclareAssignmentBuilder extends AssignmentBuilder {
  public DeclareAssignmentBuilder(final boolean isFinal, final VariableReference reference, final Statement statement) {
    super(isFinal, AssignmentOperator.Assignment, reference, statement);
  }

  @Override
  public String generate(final Context context) {
    final String type = LoadClassReference.getClassReference(reference.getType(), context, true);
    if (statement != null) {
      return (isFinal ? "final " : "") + type + " " + super.generate(context);
    }
    else {
      return (isFinal ? "final " : "") + type + " " + reference.getName();
    }
  }
}
