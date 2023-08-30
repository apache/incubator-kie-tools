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
 * {@link Statement} thats represents a reference to a {@link Variable}.
 * 
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class VariableReference extends AbstractStatement {
  public abstract String getName();
  public abstract Statement getValue();

  protected Statement[] indexes;

  @Override
  public String generate(final Context context) {
    return getName();
  }

  public Statement[] getIndexes() {
    return indexes;
  }
  
  public void setIndexes(final Statement[] indexes) {
    this.indexes = indexes;
  }
}
