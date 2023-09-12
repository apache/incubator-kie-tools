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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.codegen.AbstractStatement;
import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.literal.LiteralValue;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class SwitchBlock extends AbstractStatement {
  private static final List<Class<?>> supportedTypes; 
  static {
    supportedTypes = new ArrayList<Class<?>>(Arrays.asList(Integer.class, Character.class, Short.class, Byte.class, Enum.class, String.class));
  }

  private Statement switchExprStmt;
  private String switchExpr;
  private final Map<LiteralValue<?>, BlockStatement> caseBlocks = new LinkedHashMap<LiteralValue<?>, BlockStatement>();
  private BlockStatement defaultBlock;

  public SwitchBlock() {}

  public SwitchBlock(Statement switchExprStmt) {
    this.switchExprStmt = switchExprStmt;
  }

  public void addCase(LiteralValue<?> value) {
    caseBlocks.put(value, new BlockStatement());
  }

  public BlockStatement getCaseBlock(LiteralValue<?> value) {
    return caseBlocks.get(value);
  }

  public BlockStatement getDefaultBlock() {
    if (defaultBlock == null)
      defaultBlock = new BlockStatement();

    return defaultBlock;
  }

  public void setSwitchExpr(Statement switchExprStmt) {
    this.switchExprStmt = switchExprStmt;
  }

  public void setSwitchExpr(String expr) {
    this.switchExpr = expr;
  }

  @Override
  public String generate(Context context) {
    final StringBuilder buf = new StringBuilder("switch (");
    if (switchExpr == null) {
      buf.append(switchExprStmt.generate(context)).append(") {\n ");
    }
    else {
      buf.append(switchExpr).append(") {\n ");
    }

    checkSwitchExprType();

    if (!caseBlocks.isEmpty()) {
      final MetaClass switchExprMetaClass = switchExprStmt.getType().getErased().asBoxed();
      final boolean isStringSwitch = switchExprMetaClass.asClass().equals(String.class);
      for (final LiteralValue<?> value : caseBlocks.keySet()) {
        if (!switchExprMetaClass.isAssignableFrom(value.getType().getErased())) {
          throw new InvalidTypeException(
              value.generate(context) + " is not a valid value for " + switchExprStmt.getType().getFullyQualifiedName());
        }
        String val = value.generate(context);
        if (!isStringSwitch) {
          // case labels must be unqualified
          final int idx = val.lastIndexOf('.');
          if (idx != -1) {
            val = val.substring(idx + 1);
          }
        }
        buf.append("case ").append(val).append(": ").append(getCaseBlock(value).generate(Context.create(context)))
            .append("\n");
      }
    }

    if (defaultBlock != null) {
      buf.append("default: ").append(defaultBlock.generate(Context.create(context))).append("\n");
    }

    return buf.append("}").toString();
  }

  private void checkSwitchExprType() {
    final boolean validType = supportedTypes.stream()
            .anyMatch(cls -> MetaClassFactory.get(cls).isAssignableFrom(switchExprStmt.getType().asBoxed()));

    if (!validType)
      throw new InvalidTypeException("Type not permitted in switch statements:" + 
          switchExprStmt.getType().getFullyQualifiedName());
  }
}
