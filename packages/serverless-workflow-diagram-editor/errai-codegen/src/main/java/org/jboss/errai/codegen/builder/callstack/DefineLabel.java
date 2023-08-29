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
import org.jboss.errai.codegen.control.branch.Label;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class DefineLabel extends AbstractCallElement {
  private final String name;
  
  public DefineLabel(final String name) {
    this.name = name;
  }
  
  @Override
  public void handleCall(final CallWriter writer,
                         final Context context,
                         final Statement statement) {

    final Label label = Label.create(name);
    context.addLabel(label);
    
    writer.reset();
    writer.append(label.generate(context));
  }

  @Override
  public String toString() {
    return "[[Label<" + name + ">]" + next + "]";
  }
}
