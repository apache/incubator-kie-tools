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


package org.jboss.errai.codegen.test;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.util.Stmt;
import org.junit.Test;

import static org.jboss.errai.codegen.test.SwitchBlockBuilderTestResult.SWITCH_BLOCK_CHAINED_INVOCATION;
import static org.jboss.errai.codegen.test.SwitchBlockBuilderTestResult.SWITCH_BLOCK_CHAR_CHAINED;
import static org.jboss.errai.codegen.test.SwitchBlockBuilderTestResult.SWITCH_BLOCK_EMPTY;
import static org.jboss.errai.codegen.test.SwitchBlockBuilderTestResult.SWITCH_BLOCK_ENUM;
import static org.jboss.errai.codegen.test.SwitchBlockBuilderTestResult.SWITCH_BLOCK_INT;
import static org.jboss.errai.codegen.test.SwitchBlockBuilderTestResult.SWITCH_BLOCK_INTEGER_NO_DEFAULT;
import static org.jboss.errai.codegen.test.SwitchBlockBuilderTestResult.SWITCH_BLOCK_INT_FALLTHROUGH;
import static org.junit.Assert.fail;

/**
 * Tests the generation of switch blocks using the {@link StatementBuilder} API.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class SwitchBlockBuilderTest extends AbstractCodegenTest {
  public enum TestEnum {
    A, B;
  }

  @Test
  public void testSwitchBlockWithInvalidStatement() {
    try {
      StatementBuilder.create()
          .switch_(Stmt.loadStatic(System.class, "out"))
          .toJavaString();
      fail("expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
       // expected
    }
  }

  @Test
  public void testSwitchBlockWithInvalidCaseValueUsingEnumForInt() {
    try {
      Context c = Context.create().addVariable("n", int.class);
      StatementBuilder.create(c)
          .switch_(c.getVariable("n"))
          .case_(TestEnum.A)
          .finish()
          .toJavaString();
      fail("expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
    }
  }

  @Test
  public void testSwitchBlockWithInvalidCaseValueUsingIntForEnum() {
    try {
      Context c = Context.create().addVariable("t", TestEnum.class);
      StatementBuilder.create(c)
          .switch_(Stmt.loadVariable("t"))
          .case_(1)
          .finish()
          .toJavaString();
      fail("expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
       // expected 
    }
  }

  @Test
  public void testSwitchBlockOnIntEmpty() {
    String s = StatementBuilder.create()
        .declareVariable("n", int.class)
        .switch_(Stmt.loadVariable("n"))
        .toJavaString();

    assertEquals("Failed to generate empty switch block", SWITCH_BLOCK_EMPTY, s);
  }

  @Test
  public void testSwitchBlockOnInt() {
    String s = StatementBuilder.create()
        .declareVariable("n", int.class)
        .switch_(Stmt.loadVariable("n"))
        .case_(0)
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "0"))
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "break"))
        .append(Stmt.break_())
        .finish()
        .case_(1)
        .append(Stmt.break_())
        .finish()
        .default_()
        .append(Stmt.break_())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate int switch block", SWITCH_BLOCK_INT, s);
  }

  @Test
  public void testSwitchBlockOnEnum() {
    Context c = Context.create().autoImport();
    String s = StatementBuilder.create(c)
        .declareVariable("t", TestEnum.class)
        .switch_(Stmt.loadVariable("t"))
        .case_(TestEnum.A)
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "A"))
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "break"))
        .append(Stmt.break_())
        .finish()
        .case_(TestEnum.B)
        .append(Stmt.break_())
        .finish()
        .default_()
        .append(Stmt.break_())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate enum switch block", SWITCH_BLOCK_ENUM, s);
  }
  
  @Test
  public void testSwitchBlockWithoutDefaultBlock() {
    String s = StatementBuilder.create()
        .declareVariable("n", Integer.class)
        .switch_(Stmt.loadVariable("n"))
        .case_(0)
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "0"))
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "break"))
        .append(Stmt.break_())
        .finish()
        .case_(1)
        .append(Stmt.break_())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate Integer switch block without default", SWITCH_BLOCK_INTEGER_NO_DEFAULT, s);
  }

  @Test
  public void testSwitchBlockWithFallThrough() {
    String s = StatementBuilder.create()
        .declareVariable("n", int.class)
        .switch_(Stmt.loadVariable("n"))
        .case_(0)
        .finish()
        .case_(1)
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "1"))
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "break"))
        .append(Stmt.break_())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate int switch block with fallthrough", SWITCH_BLOCK_INT_FALLTHROUGH, s);
  }
  
  @Test
  public void testSwitchBlockChained() {
    String s = StatementBuilder.create()
        .declareVariable("n", int.class)
        .loadVariable("n")
        .switch_()
        .case_(0)
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "0"))
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "break"))
        .append(Stmt.break_())
        .finish()
        .case_(1)
        .append(Stmt.break_())
        .finish()
        .default_()
        .append(Stmt.break_())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate chained switch block", SWITCH_BLOCK_INT, s);
  }
  
  @Test
  public void testSwitchBlockChainedOnChar() {
    String s = StatementBuilder.create()
        .declareVariable("c", char.class)
        .loadVariable("c")
        .switch_()
        .case_('a')
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "a"))
        .append(Stmt.break_())
        .finish()
        .case_('b')
        .append(Stmt.break_())
        .finish()
        .default_()
        .append(Stmt.break_())
        .finish()
        .toJavaString();
    
    assertEquals("Failed to generate char switch block", SWITCH_BLOCK_CHAR_CHAINED, s);
  }
  
  @Test
  public void testSwitchBlockChainedOnInvocation() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("length")
        .switch_()
        .case_(0)
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "0"))
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "break"))
        .append(Stmt.break_())
        .finish()
        .case_(1)
        .append(Stmt.break_())
        .finish()
        .default_()
        .append(Stmt.break_())
        .finish()
        .toJavaString();
    
    assertEquals("Failed to generate switch block chained on invocation", SWITCH_BLOCK_CHAINED_INVOCATION, s);
  }
}
