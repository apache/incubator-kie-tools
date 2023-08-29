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
import org.jboss.errai.codegen.builder.Builder;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.callstack.CallElement;
import org.jboss.errai.codegen.builder.callstack.CallWriter;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.util.GenUtil;

import static org.jboss.errai.codegen.util.PrettyPrinter.prettyPrintJava;

/**
 * Base class of all {@link StatementBuilder}s
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class AbstractStatementBuilder implements Statement, Builder, StatementEnd {
  protected final Context context;
  protected final CallElementBuilder callElementBuilder;
  protected boolean generated;

  protected AbstractStatementBuilder(final Context context) {
    this(context, new CallElementBuilder());
  }

  protected AbstractStatementBuilder(Context context, final CallElementBuilder callElementBuilder) {
    if (context == null) {
      context = Context.create();
    }

    this.context = context;
    this.callElementBuilder = callElementBuilder;
  }

  String generatorCache;

  @Override
  public String generate(final Context context) {
    if (generatorCache != null) return generatorCache;

    final CallWriter writer = new CallWriter();
    try {
      callElementBuilder.getRootElement().handleCall(writer, context, null);
    }
    catch (Exception e) {
      GenUtil.throwIfUnhandled("generation failure at: " + writer.getCallString(), e);
    }
    generated = true;

    return generatorCache = prettyPrintJava(writer.getCallString());
  }

  public void appendCallElement(final CallElement element) {
    callElementBuilder.appendCallElement(element);
  }

  @Override
  public MetaClass getType() {
    if (callElementBuilder.getCallElement() == null)
      return null;

    return callElementBuilder.getCallElement().getResultType();
  }

  @Override
  public String toJavaString() {
    return generate(context);
  }
}
