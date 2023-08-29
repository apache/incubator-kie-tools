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

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.BuildCallback;
import org.jboss.errai.codegen.builder.CatchBlockBuilder;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.TryBlockBuilder;
import org.jboss.errai.codegen.builder.callstack.DeferredCallElement;
import org.jboss.errai.codegen.control.TryBlock;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * StatementBuilder to generate try/catch/finally blocks.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class TryBlockBuilderImpl extends AbstractStatementBuilder implements TryBlockBuilder, CatchBlockBuilder {
  private TryBlock tryBlock;

  protected TryBlockBuilderImpl(Context context, CallElementBuilder callElementBuilder) {
    super(context, callElementBuilder);
  }

  @Override
  public BlockBuilder<CatchBlockBuilder> try_() {
    tryBlock = new TryBlock();

    appendCallElement(new DeferredCallElement((writer, context, statement) -> {
      writer.reset();
      writer.append(tryBlock.generate(Context.create(context)));
    }));

    return new BlockBuilderImpl<CatchBlockBuilder>(tryBlock.getBlock(), new BuildCallback<CatchBlockBuilder>() {
      @Override
      public CatchBlockBuilder callback(Statement statement) {
        return TryBlockBuilderImpl.this;
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }

  @Override
  public BlockBuilder<CatchBlockBuilder> catch_(Class<? extends Throwable> exceptionType, String variableName) {
    return catch_(MetaClassFactory.get(exceptionType), variableName);
  }

  @Override
  public BlockBuilder<CatchBlockBuilder> catch_(MetaClass exceptionType, String variableName) {
    final Variable exceptionVar = Variable.create(variableName, exceptionType);
    tryBlock.addCatchBlock(exceptionVar);

    return new BlockBuilderImpl<CatchBlockBuilder>(tryBlock.getCatchBlock(exceptionVar),
        new BuildCallback<CatchBlockBuilder>() {
          @Override
          public CatchBlockBuilder callback(Statement statement) {
            return TryBlockBuilderImpl.this;
          }

          @Override
          public Context getParentContext() {
            return context;
          }
        });
  }

  @Override
  public BlockBuilder<StatementEnd> finally_() {
    return new BlockBuilderImpl<StatementEnd>(tryBlock.getFinallyBlock(), new BuildCallback<StatementEnd>() {
      @Override
      public StatementEnd callback(Statement statement) {
        return TryBlockBuilderImpl.this;
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }
}
