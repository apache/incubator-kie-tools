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


package org.jboss.errai.codegen.builder;

import java.util.Collection;
import java.util.List;

import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Statement;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface BlockBuilder<T> extends Finishable<T>, Iterable<Statement> {
  public void insertBefore(Statement stmt);
  public void insertBefore(InnerClass innerClass);

  public BlockBuilder<T> append(Statement stmt);
  public BlockBuilder<T> appendAll(Collection<Statement> stmt);

  public BlockBuilder<T> append(InnerClass innerClass);


  /**
   * Alias for {@link #append(org.jboss.errai.codegen.Statement)}
   * @param stmt the statement to add to the block
   * @return
   */
  public BlockBuilder<T> _(Statement stmt);

  /**
   * Alias for {@link #append(org.jboss.errai.codegen.InnerClass)} )}
   * @param innerClass the statement to add to the block
   * @return
   */
  public BlockBuilder<T> _(InnerClass innerClass);

  /**
   * Return a list of statements from the specified statement (inclusive), and remove all of the returned statements
   * from the underlying builder.
   *
   * @param statement the statement to split from.
   * @return a list of statements from the specified statement
   */
  public List<Statement> splitFrom(Statement statement);

  /**
   * Show the last statement in the block.
   *
   * @return the last statement in the block.
   */
  public Statement peek();

  public void clear();
}

