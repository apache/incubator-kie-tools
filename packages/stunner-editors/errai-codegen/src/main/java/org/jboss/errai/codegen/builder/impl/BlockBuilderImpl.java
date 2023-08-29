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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.BuildCallback;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BlockBuilderImpl<T> implements BlockBuilder<T> {
  protected final BlockStatement blockStatement;
  protected final BuildCallback<T> callback;

  public BlockBuilderImpl() {
    this(null);
  }

  public BlockBuilderImpl(final BuildCallback<T> callback) {
    this(new BlockStatement(), callback);
  }

  public BlockBuilderImpl(final BlockStatement blockStatement, final BuildCallback<T> callback) {
    this.blockStatement = blockStatement;
    this.callback = callback;
  }

  @Override
  public void insertBefore(final Statement stmt) {
    this.blockStatement.insertBefore(stmt);
  }

  @Override
  public void insertBefore(final InnerClass innerClass) {
    insertBefore(new Statement() {

      @Override
      public String generate(final Context context) {
        return innerClass.generate(context);
      }

      @Override
      public MetaClass getType() {
        return innerClass.getType();
      }
    });
  }

  @Override
  public BlockBuilder<T> append(final Statement statement) {
    blockStatement.addStatement(statement);
    return this;
  }

  @Override
  public BlockBuilder<T> appendAll(Collection<Statement> stmt) {
    blockStatement.addAllStatements(stmt);
    return this;
  }

  @Override
  public BlockBuilder<T> append(final InnerClass innerClass) {
    blockStatement.addStatement(new Statement() {

      @Override
      public String generate(final Context context) {
        return innerClass.generate(context);
      }

      @Override
      public MetaClass getType() {
        return innerClass.getType();
      }
    });
    return this;
  }

  @Override
  public BlockBuilder<T> _(final Statement stmt) {
    return append(stmt);
  }

  @Override
  public BlockBuilder<T> _(final InnerClass innerClass) {
    return append(innerClass);
  }

  @Override
  public List<Statement> splitFrom(final Statement statement) {
    final List<Statement> statements = blockStatement.getStatements();
    final int index = statements.indexOf(statement);

    if (index > 0) {
      final List<Statement> split = new ArrayList<Statement>(statements.subList(index, statements.size()));
      statements.removeAll(split);
      return split;
    }
    else {
      return Collections.emptyList();
    }
  }

  @Override
  public Statement peek() {
    final List<Statement> statements = blockStatement.getStatements();
    return statements.isEmpty() ? null : statements.get(statements.size() - 1);
  }

  @Override
  public Iterator<Statement> iterator() {
    return blockStatement.getStatements().iterator();
  }

  @Override
  public T finish() {
    if (callback != null) {
      return callback.callback(blockStatement);
    }
    return null;
  }

  @Override
  public void clear() {
    blockStatement.getStatements().clear();
  }
}
