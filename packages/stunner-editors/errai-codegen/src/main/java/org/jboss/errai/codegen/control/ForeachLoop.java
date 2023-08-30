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


package org.jboss.errai.codegen.control;

import org.jboss.errai.codegen.AbstractStatement;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;

import static org.jboss.errai.codegen.builder.callstack.LoadClassReference.getClassReference;

/**
 * Foreach statement (enhanced for loop).
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ForeachLoop extends AbstractStatement {

  private final Variable loopVar;
  private final String collectionExpr;
  private final Statement body;
  private final boolean nullSafe;

  public ForeachLoop(Variable loopVar, String collectionExpr, Statement body, boolean nullSafe) {
    this.loopVar = loopVar;
    this.collectionExpr = collectionExpr;
    this.body = body;
    this.nullSafe = nullSafe;
  }

  String generatedCache;

  @Override
  public String generate(Context context) {
    if (generatedCache != null) return generatedCache;

    return generatedCache
            = ((nullSafe) ? "if (" + collectionExpr + " != null) {\n" : "")
            + "for (" + getClassReference(loopVar.getType(), context)
            + " " + loopVar.getName() + " : " + collectionExpr + ") {"
            + "\n\t" + body.generate(context).replaceAll("\n", "\n\t")
            + "\n}"
            + ((nullSafe) ? "\n}" : "");
  }
}
