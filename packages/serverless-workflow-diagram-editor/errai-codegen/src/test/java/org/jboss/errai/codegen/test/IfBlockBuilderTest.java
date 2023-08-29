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

import org.jboss.errai.codegen.ArithmeticOperator;
import org.jboss.errai.codegen.BooleanOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.impl.ContextBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.exception.InvalidExpressionException;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.Arith;
import org.jboss.errai.codegen.util.Bool;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the generation of if blocks using the {@link StatementBuilder} API.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class IfBlockBuilderTest extends AbstractCodegenTest implements IfBlockBuilderTestResult {

  @Test
  public void testEmptyIfBlockUsingNoRhs() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("endsWith", "abc")
        .if_()
        .finish().toJavaString();

    assertEquals("Failed to generate empty if block using no rhs", EMPTY_IF_BLOCK_NO_RHS, s);
  }

  @Test
  public void testEmptyIfBlockUsingNoRhsAndNegation() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("endsWith", "abc")
        .ifNot()
        .finish().toJavaString();

    assertEquals("Failed to generate empty if block using no rhs", EMPTY_IF_BLOCK_NO_RHS_AND_NEGATION, s);
  }

  @Test
  public void testEmptyIfBlockUsingLiteralRhs() {
    String s = StatementBuilder.create()
        .declareVariable("n", int.class)
        .loadVariable("n")
        .if_(BooleanOperator.Equals, 1)
        .finish().toJavaString();

    assertEquals("Failed to generate empty if block using a literal rhs", EMPTY_IF_BLOCK_LITERAL_RHS, s);
  }

  @Test
  public void testIfElseBlockUsingNoRhs() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("endsWith", "abc")
        .if_()
          ._(Stmt.declareVariable(Integer.class).named("n").initializeWith(0))
        .finish()
        .else_()
          ._(Stmt.declareVariable(Integer.class).named("n").initializeWith(1))
        .finish().toJavaString();

    assertEquals("Failed to generate empty if block using no rhs", IF_ELSE_BLOCK_NO_RHS, s);
  }

  @Test
  public void testIfElseBlockUsingRhs() {
    String s = StatementBuilder.create()
        .declareVariable("n", Integer.class)
        .declareVariable("m", Integer.class)
        .loadVariable("n")
        .if_(BooleanOperator.GreaterThan, Variable.get("m"))
          ._(Stmt.declareVariable(Integer.class).named("n").initializeWith(0))
        .finish()
        .else_()
          ._(Stmt.declareVariable(Integer.class).named("n").initializeWith(1))
        .finish().toJavaString();

    assertEquals("Failed to generate empty if block using a rhs", IF_ELSE_BLOCK_RHS, s);
  }

  @Test
  public void testNestedIfElseIfBlockUsingNoRhs() {
    Context c = ContextBuilder.create().addVariable("s", String.class).addVariable("n", Integer.class).getContext();

    String s = StatementBuilder.create(c)
        .loadVariable("s")
        .invoke("endsWith", "abc")
        .if_()
          ._(StatementBuilder.create(c).loadVariable("n").assignValue(0))
        .finish()
        .else_()
          ._(StatementBuilder.create(c).loadVariable("s")
              .invoke("startsWith", "def")
              .if_()
                ._(StatementBuilder.create(c).loadVariable("n").assignValue(1))
              .finish()
        )
        .finish().toJavaString();

    assertEquals("Failed to generate if-else-if-block using no rhs", IF_ELSEIF_BLOCK_NO_RHS_NESTED, s);
  }

  @Test
  public void testNestedIfElseIfElseBlockUsingNoRhs() {
    Context c = ContextBuilder.create().addVariable("s", String.class).addVariable("n", Integer.class).getContext();

    String s = StatementBuilder.create(c)
        .loadVariable("s")
        .invoke("endsWith", "abc")
        .if_()
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(0))
        .finish()
        .else_()
          .append(StatementBuilder.create(c).loadVariable("s")
              .invoke("startsWith", "def")
              .if_()
                .append(StatementBuilder.create(c).loadVariable("n").assignValue(1))
              .finish()
              .else_()
                .append(StatementBuilder.create(c).loadVariable("n").assignValue(2))
              .finish()
        )
        .finish().toJavaString();

    assertEquals("Failed to generate if-else-if-else block using no rhs",
        IF_ELSEIF_ELSE_BLOCK_NO_RHS_NESTED, s);
  }

  @Test
  public void testNestedIfElseIfBlockUsingRhs() {
    Context c = ContextBuilder.create().addVariable("n", Integer.class).addVariable("m", Integer.class).getContext();

    String s = StatementBuilder.create(c)
        .loadVariable("n")
        .if_(BooleanOperator.GreaterThan, Variable.get("m"))
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(0))
        .finish()
        .else_()
          .append(StatementBuilder.create(c).loadVariable("m")
              .if_(BooleanOperator.GreaterThan, Variable.get("n"))
                .append(StatementBuilder.create(c).loadVariable("n").assignValue(1))
              .finish()
              .else_()
                .append(StatementBuilder.create(c).loadVariable("n").assignValue(2))
              .finish()
        )
        .finish().toJavaString();

    assertEquals("Failed to generate if-else-if-else block using rhs", IF_ELSEIF_ELSE_BLOCK_RHS_NESTED, s);
  }

  @Test
  public void testIfElseIfBlockUsingNoRhsAndElseifKeyword() {
    Context c = ContextBuilder.create().addVariable("s", String.class).addVariable("n", Integer.class).getContext();

    String s = StatementBuilder.create(c)
        .loadVariable("s")
        .invoke("endsWith", "abc")
        .if_()
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(0))
        .finish()
        .elseif_(StatementBuilder.create(c).loadVariable("s").invoke("startsWith", "def"))
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(1))
        .finish().toJavaString();

    assertEquals("Failed to generate if-elseif block using no rhs", IF_ELSEIF_BLOCK_NO_RHS, s);
  }

  @Test
  public void testIfElseIfElseBlockUsingNoRhsAndElseifKeyword() {
    Context c = ContextBuilder.create().addVariable("s", String.class).addVariable("n", Integer.class).getContext();

    String s = StatementBuilder.create(c)
        .loadVariable("s")
        .invoke("endsWith", "abc")
        .if_()
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(0))
        .finish()
        .elseif_(StatementBuilder.create(c).loadVariable("s").invoke("startsWith", "def"))
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(1))
        .finish()
        .else_()
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(2))
        .finish().toJavaString();

    assertEquals("Failed to generate if - elseif - else block using no rhs", IF_ELSEIF_ELSE_BLOCK_NO_RHS, s);
  }

  @Test
  public void testIfElseIfBlockUsingRhsAndElseifKeyword() {
    Context c = ContextBuilder.create().addVariable("n", Integer.class).addVariable("m", Integer.class).getContext();

    String s = StatementBuilder.create(c)
        .loadVariable("n")
        .if_(BooleanOperator.GreaterThan, Variable.get("m"))
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(0))
        .finish()
        .elseif_(StatementBuilder.create(c).loadVariable("m"), BooleanOperator.GreaterThan, Variable.get("n"))
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(1))
        .finish()
        .elseif_(StatementBuilder.create(c).loadVariable("m"), BooleanOperator.Equals, Variable.get("n"))
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(2))
        .finish()
        .else_()
          .append(StatementBuilder.create(c).loadVariable("n").assignValue(3))
        .finish().toJavaString();

    assertEquals("Failed to generate if - else if - else block using rhs", IF_ELSEIF_ELSE_BLOCK_RHS, s);
  }

  @Test
  public void testIfBlockWithInvalidNonBooleanExpression() {
    try {
      StatementBuilder.create()
          .declareVariable("str", String.class)
          .loadVariable("str")
          .invoke("compareTo", "asd")
          .if_().finish()
          .toJavaString();

      fail("Expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
    }

    try {
      StatementBuilder.create()
          .declareVariable("str", String.class)
          .loadVariable("str")
          .ifNot()
          .finish()
          .toJavaString();

      fail("Expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
    }
  }

  @Test
  public void testIfBlockWithInvalidExpression() {
    try {
      StatementBuilder.create()
          .declareVariable("str", String.class)
          .declareVariable("str2", String.class)
          .loadVariable("str")
          .if_(BooleanOperator.GreaterThan, Variable.get("str2")).finish()
          .toJavaString();

      fail("Expected InvalidExpressionException");
    }
    catch (InvalidExpressionException e) {
      // expected
      assertTrue("Wrong exception thrown", e.getMessage().contains(String.class.getName()));
    }
  }

  @Test
  public void testIfBlockWithInstanceOfExpression() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .if_(BooleanOperator.InstanceOf, MetaClassFactory.getAsStatement(String.class)).finish()
        .toJavaString();

    assertEquals("Failed to generate empty if block using an instance of expression",
        EMPTY_IF_BLOCK_INSTANCE_OF_RHS, s);
  }

  @Test
  public void testIfBlockWithNullCheck() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .if_(BooleanOperator.NotEquals, null)
        .finish()
        .toJavaString();

    assertEquals("Failed to generate if block using a null rhs", EMPTY_IF_BLOCK_NULL_RHS, s);
  }

  @Test
  public void testIfBlockUnchainedWithNestedExpressions() {
    Context ctx = Context.create().addVariable("a", boolean.class)
        .addVariable("b", boolean.class);

    String s = Stmt.create(ctx)
        .if_(Bool.expr(
              Bool.expr("foo", BooleanOperator.Equals, "bar"), 
              BooleanOperator.Or,
              Bool.expr(
                  Bool.expr("cat", BooleanOperator.Equals, "dog"), 
                  BooleanOperator.And, 
                  Bool.expr("girl", BooleanOperator.NotEquals, "boy"))))
        .finish()
        .elseif_(Bool.expr(Stmt.loadVariable("a"), BooleanOperator.And, Stmt.loadVariable("b")))
          .append(Stmt.loadStatic(System.class, "out").invoke("println", Refs.get("a")))
        .finish()
        .toJavaString();

    assertEquals("Failed to generate if block using nested boolean expressions",
        IF_ELSEIF_BLOCK_UNCHAINED_NESTED_EXPRESSIONS, s);
  }

  @Test
  public void testIfBlockUnchainedWithExpressionUsingNegation() {
    Context ctx = Context.create().addVariable("a", boolean.class)
        .addVariable("b", boolean.class);

    String s =
        Stmt.create(ctx)
            .if_(Bool.expr(Stmt.loadVariable("a"), BooleanOperator.And, Bool.expr(Stmt.loadVariable("b")).negate()))
            .append(Stmt.loadStatic(System.class, "out").invoke("println", Refs.get("a")))
            .finish()
            .toJavaString();

    assertEquals("Failed to generate if block using nested boolean expressions",
        IF_BLOCK_UNCHAINED_WITH_EXPRESSION_USING_NEGATION, s);
  }

  @Test
  public void testIfBlockUnchainedWithExpressionUsingArithmetics() {
    Context ctx = Context.create()
        .addVariable("a", Integer.class)
        .addVariable("b", Integer.class)
        .addVariable("c", Float.class);

    String s =
        Stmt.create(ctx)
            .if_(Bool.expr(
                Arith.expr(
                    Arith.expr(Stmt.loadVariable("a"), ArithmeticOperator.Addition, Stmt.loadVariable("b")),
                    ArithmeticOperator.Division,
                    Stmt.loadVariable("c")),
                 BooleanOperator.GreaterThan, 1))
            .append(Stmt.loadStatic(System.class, "out").invoke("println", Refs.get("a")))
            .finish()
            .toJavaString();

    assertEquals("Failed to generate if block using arithmetic expressions",
        IF_BLOCK_UNCHAINED_WITH_EXPRESSION_USING_ARITHMETICS, s);
  }
}
