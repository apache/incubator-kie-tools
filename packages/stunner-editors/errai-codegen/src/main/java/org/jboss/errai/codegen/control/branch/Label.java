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
public class Label extends AbstractStatement {
  private final String name;

  public static Label create(String name) {
    return new Label(name);
  }
  
  private Label(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  @Override
  public String generate(Context context) {
    return name+":";
  }
  
  public LabelReference getReference() {
    return new LabelReference(name);
  }
  
  @Override
  public String toString() {
    return "Label [name=" + name + "]";
  }
}
