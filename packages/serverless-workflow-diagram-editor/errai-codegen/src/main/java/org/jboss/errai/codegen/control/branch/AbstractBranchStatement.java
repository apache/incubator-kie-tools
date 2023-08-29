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


package org.jboss.errai.codegen.control.branch;

import org.jboss.errai.codegen.AbstractStatement;
import org.jboss.errai.codegen.Context;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class AbstractBranchStatement extends AbstractStatement {
  protected String label;

  public AbstractBranchStatement() {}

  public AbstractBranchStatement(String label) {
    this.label = label;
  }

  public String generateLabelReference(Context context) {
    if (label != null)
      return " " + context.getLabel(label).generate(context);

    return "";
  }
}
