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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.codegen.builder.ClosedBlock;
import org.jboss.errai.codegen.util.EmptyStatement;

/**
 * Represents a code block (e.g. a loop body).
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BlockStatement extends AbstractStatement {
  public static final BlockStatement EMPTY_BLOCK = new BlockStatement() {
    @Override
    public BlockStatement addStatement(final Statement statement) {
      throw new UnsupportedOperationException("this is an immutable object");
    }

    @Override
    public void insertBefore(final Statement statement) {
      throw new UnsupportedOperationException("this is an immutable object");
    }
  };

  private final List<Statement> statements = new ArrayList<Statement>(20);
  
  public BlockStatement(final Statement... statements) {
    if (statements != null) {
      for (final Statement statement : statements) {
        if (statement != null)
          this.statements.add(statement);
      }
    }
  }

  public BlockStatement addStatement(final Statement statement) {
    if (statement != null)
      statements.add(statement);

    return this;
  }

  public BlockStatement addAllStatements(final Collection<Statement> stmts) {
    if (statements != null)
      statements.addAll(stmts);

    return this;
  }

  public void insertBefore(final Statement statement) {
    if (statement != null) {
      final int pos = statements.size() == 0 ? 0 : statements.size() - 1;
      statements.add(pos, statement);
    }

  }
  
  String generatedCache;
  @Override
  public String generate(final Context context) {
    if (generatedCache != null) return generatedCache;
    
    final StringBuilder buf = new StringBuilder(512);

    boolean isLastBlock = false;
    for (final Statement statement : statements) {
      if (buf.length() != 0 && !(statement instanceof EmptyStatement)) {
        buf.append("\n");
      }

      buf.append(statement.generate(context));

      if (!(statement instanceof Comment) && !(statement instanceof EmptyStatement)
              && !buf.toString().endsWith(";") && !buf.toString().endsWith(":") && !buf.toString().endsWith("}")) {

        buf.append(";");
      }

      isLastBlock = statement instanceof ClosedBlock;
    }

    if (buf.length() != 0 && buf.charAt(buf.length() - 1) != ';' && !isLastBlock) {
      buf.append(';');
    }

    return generatedCache = buf.toString();
  }

  /**
   * Returns a <b>mutable</b> representation of the statements in this block. Changes to the List returned by this
   * method <b>will</b> be reflected in the state of this method when the code is generated.
   *
   * @return a list representing the underlying set of statements in this block.
   */
  public List<Statement> getStatements() {
    return statements;
  }

  public boolean isEmpty() {
    return statements.isEmpty();
  }
}
