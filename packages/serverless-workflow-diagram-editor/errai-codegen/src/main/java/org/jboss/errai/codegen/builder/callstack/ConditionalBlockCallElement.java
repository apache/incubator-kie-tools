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


package org.jboss.errai.codegen.builder.callstack;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.control.AbstractConditionalBlock;
import org.jboss.errai.codegen.exception.GenerationException;

/**
 * A {@link CallElement} for conditional blocks. It can only be the last
 * element in a chain of calls.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ConditionalBlockCallElement extends AbstractCallElement {
  private final AbstractConditionalBlock conditionalBlock;
  
  public ConditionalBlockCallElement(final AbstractConditionalBlock conditionalBlock) {
    this.conditionalBlock = conditionalBlock;
  }

  @Override
  public void handleCall(final CallWriter writer, final Context context, final Statement lhs) {
    try {
      if (lhs != null) {
        // The LHS value is on the current callstack. So we grab the value from there at generation time.
        conditionalBlock.getCondition().setLhs(lhs);
        conditionalBlock.getCondition().setLhsExpr(writer.getCallString());
      }
      writer.reset();
      writer.append(conditionalBlock.generate(Context.create(context)));
    } 
    catch(GenerationException e) {
      blameAndRethrow(e);
    }
  }

  @Override
  public String toString() {
    return "[[Conditional<" + conditionalBlock + ">]" + next + "]";
  }
}
