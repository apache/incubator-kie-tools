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

import java.util.List;
import java.util.Map;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.AssignmentOperator;
import org.jboss.errai.codegen.BooleanOperator;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.Builder;
import org.jboss.errai.codegen.builder.impl.ContextBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.exception.InvalidExpressionException;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.exception.OutOfScopeException;
import org.jboss.errai.codegen.exception.TypeNotIterableException;
import org.jboss.errai.codegen.util.Bool;
import org.jboss.errai.codegen.util.Stmt;
import org.junit.Test;

import static org.jboss.errai.codegen.test.LoopBuilderTestResult.DOWHILE_NESTED_EXPRESSION;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.DOWHILE_SIMPLE_EXPRESSION;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.DOWHILE_SIMPLE_EXPRESSION_NO_OP;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_KEYSET_LOOP;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_LITERAL_STRING_ARRAY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_NESTED_STRING_IN_LIST;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_OBJECT_IN_LIST;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_OBJECT_IN_LIST_NOT_NULL;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_OBJECT_IN_LIST_TWO_STATEMENTS;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_STRING_IN_ARRAY_ONE_STATEMENT;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_STRING_IN_LIST;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOREACH_STRING_IN_LIST_NOT_NULL;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOR_CHAINED_INITIALIZER_COUNTING_EXP_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOR_CHAINED_INITIALIZER_NO_COUNTING_EXP_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOR_DECLARE_INITIALIZER_COUNTING_EXP;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOR_INITIALIZER_COUNTING_EXP_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOR_INITIALIZER_NO_COUNTING_EXP_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.FOR_NO_INITIALIZER_NO_COUNTING_EXP_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.LOOP_WITH_BREAK;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.LOOP_WITH_BREAK_AND_LABEL;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.LOOP_WITH_CONTINUE;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.LOOP_WITH_CONTINUE_AND_LABEL;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.WHILE_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.WHILE_NESTED_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.WHILE_NESTED_LOOPS;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.WHILE_RHS_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.WHILE_RHS_NULL_EMPTY;
import static org.jboss.errai.codegen.test.LoopBuilderTestResult.WHILE_WITH_BODY;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the generation of loops using the {@link StatementBuilder} API.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@SuppressWarnings("serial")
public class LoopBuilderTest extends AbstractCodegenTest {

  @Test
  public void testForeachLoopWithStringInParameterizedList() {
    String foreachWithListOfStrings = StatementBuilder.create()
        .declareVariable("list", new TypeLiteral<List<String>>() {})
        .loadVariable("list")
        .foreach("element")
        .finish().toJavaString();

    assertEquals("Failed to generate foreach loop using a List<String>",
        FOREACH_STRING_IN_LIST, foreachWithListOfStrings);
  }

  @Test
  public void testForeachLoopWithStringInArray() {
    Statement createObject = StatementBuilder.create().newObject(String.class);

    String foreachWithStringArray = StatementBuilder.create(Context.create().addVariable("list", String[].class))
        .loadVariable("list")
        .foreach("element")
        .append(createObject)
        .finish()
        .toJavaString();

    assertEquals("Failed to generate foreach loop using a String[]",
        FOREACH_STRING_IN_ARRAY_ONE_STATEMENT, foreachWithStringArray);
  }

  @Test
  public void testForeachLoopWithStringInList() {
    Statement createObject = StatementBuilder.create().newObject(String.class);
    Statement createAnotherObject = StatementBuilder.create().newObject(Object.class);

    String foreachWithList = StatementBuilder.create()
        .declareVariable("list", List.class)
        .loadVariable("list")
        .foreach("element")
        .append(createObject)
        .append(createAnotherObject)
        .finish().toJavaString();

    assertEquals("Failed to generate foreach loop using a List<?>",
        FOREACH_OBJECT_IN_LIST_TWO_STATEMENTS, foreachWithList);
  }

  @Test
  public void testForeachLoopWithUndefinedCollection() {
    try {
      StatementBuilder.create()
          .loadVariable("list")
          .foreach("element", Integer.class)
          .finish().toJavaString();

      fail("Expected OutOfScopeException");
    }
    catch (OutOfScopeException e) {
      // expected
    }
  }

  @Test
  public void testForeachLoopWithProvidedLoopVarType() {
    Builder builder = StatementBuilder.create()
        .declareVariable("list", new TypeLiteral<List<String>>() {})
        .loadVariable("list")
        .foreach("element", Object.class)
        .finish();

    assertEquals("Failed to generate foreach loop with provided loop var type",
        FOREACH_OBJECT_IN_LIST, builder.toJavaString());

    try {
      StatementBuilder.create()
          .declareVariable("list", new TypeLiteral<List<String>>() {})
          .loadVariable("list")
          .foreach("element", Integer.class)
          .finish().toJavaString();

      fail("Expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
    }
  }

  @Test
  public void testForeachLoopsNested() {
    Statement createObject = StatementBuilder.create().newObject(String.class);

    Builder outerLoop = StatementBuilder.create()
        .declareVariable("list", new TypeLiteral<List<String>>() {})
        .loadVariable("list")
        .foreach("element")
        .append(StatementBuilder.create(
            ContextBuilder.create().addVariable(Variable.create("anotherList",
                new TypeLiteral<List<String>>() {})).getContext())
            .loadVariable("anotherList")
            .foreach("anotherElement")
            .append(createObject)
            .finish()
        ).finish();

    assertEquals("Failed to generate nested foreach loops",
        FOREACH_NESTED_STRING_IN_LIST, outerLoop.toJavaString());
  }

  @Test
  public void testForeachLoopWithInvalidCollectionType() {

    try {
      StatementBuilder.create()
          .declareVariable("list", String.class)
          .loadVariable("list")
          .foreach("element")
          .finish().toJavaString();

      fail("Expected TypeNotIterableException");
    }
    catch (TypeNotIterableException e) {
      // expected
    }
  }

  @Test
  public void testForeachLoopWithInvoke() {
    Builder loop = StatementBuilder.create()
        .declareVariable("map", Map.class)
        .loadVariable("map")
        .invoke("keySet")
        .foreach("key")
        .append(Stmt.loadStatic(System.class, "out").invoke("println", Variable.get("key")))
        .finish();

    assertEquals("Failed to generate foreach loop using invoke()",
        FOREACH_KEYSET_LOOP, loop.toJavaString());
  }

  @Test
  public void testForeachLoopWithLiterals() {
    String s = StatementBuilder.create()
        .loadLiteral(new String[] { "s1", "s2" })
        .foreach("s")
        .append(StatementBuilder.create().loadVariable("s").invoke("getBytes"))
        .finish().toJavaString();

    assertEquals("Failed to generate foreach loop using a literal String array",
        FOREACH_LITERAL_STRING_ARRAY, s);
  }

  @Test
  public void testForeachLoopWithProvidedContext() {
    Context c = ContextBuilder.create().addVariable(Variable.create("s", String.class)).getContext();
    String s = StatementBuilder.create(c)
        .loadLiteral(new String[] { "s1", "s2" })
        .foreach("s")
        .append(StatementBuilder.create().loadVariable("s").invoke("getBytes"))
        .finish().toJavaString();

    assertEquals("Failed to generate foreach loop using a literal String array",
        FOREACH_LITERAL_STRING_ARRAY, s);
  }
  
  @Test
  public void testForeachLoopWithNullCheck() {
    String foreachWithListOfStrings = StatementBuilder.create()
            .declareVariable("list", new TypeLiteral<List<String>>() {})
            .loadVariable("list")
            .foreachIfNotNull("element")
            .finish().toJavaString();

        assertEquals("Failed to generate foreach loop using a List<String> and null check",
            FOREACH_STRING_IN_LIST_NOT_NULL, foreachWithListOfStrings);
  }
  
  @Test
  public void testForeachLoopWithNullCheckAndProviderVarType() {
    String foreachWithListOfStrings = StatementBuilder.create()
            .declareVariable("list", new TypeLiteral<List<String>>() {})
            .loadVariable("list")
            .foreachIfNotNull("element", Object.class)
            .finish().toJavaString();

        assertEquals("Failed to generate foreach loop using a List<String> and null check",
            FOREACH_OBJECT_IN_LIST_NOT_NULL, foreachWithListOfStrings);
  }

  @Test
  public void testWhileLoopWithInvalidExpression() {
    try {
      StatementBuilder.create()
          .declareVariable("n", Integer.class)
          .loadVariable("n")
          .while_().finish().toJavaString();
      fail("Expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
    }

    try {
      StatementBuilder.create()
          .declareVariable("str", String.class)
          .declareVariable("str2", String.class)
          .loadVariable("str")
          .while_(BooleanOperator.GreaterThan, Variable.get("str2")).finish()
          .toJavaString();

      fail("Expected InvalidExpressionException");
    }
    catch (InvalidExpressionException iee) {
      // expected
      assertTrue("Wrong exception thrown", iee.getMessage().contains(String.class.getName()));
    }
  }

  @Test
  public void testWhileLoopChainedWithEmptyExpressionWithoutBody() {
    String s = StatementBuilder.create()
        .declareVariable("b", Boolean.class)
        .loadVariable("b")
        .while_().finish().toJavaString();

    assertEquals("Failed to generate empty while loop with chained lhs", WHILE_EMPTY, s);
  }

  @Test
  public void testWhileLoopChainedWithEmptyExpressionWithBody() {
    String s = StatementBuilder.create()
        .declareVariable("b", Boolean.class)
        .loadVariable("b")
        .while_()
          .append(StatementBuilder.create().loadVariable("b").assignValue(false))
        .finish().toJavaString();

    assertEquals("Failed to generate while loop with chained lhs and body", WHILE_WITH_BODY, s);
  }

  @Test
  public void testWhileLoopChainedWithNullCheck() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .while_(BooleanOperator.NotEquals, null)
        .finish().toJavaString();

    assertEquals("Failed to generate while loop with chained lhs, rhs (null check) and no body",
        WHILE_RHS_NULL_EMPTY, s);
  }

  @Test
  public void testWhileLoopChainedWithExpression() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("length")
        .while_(BooleanOperator.GreaterThanOrEqual, 2)
        .finish().toJavaString();

    assertEquals("Failed to generate while loop with chained lhs, rhs and no body", WHILE_RHS_EMPTY, s);
  }

  @Test
  public void testWhileLoopUnchainedWithExpression() {
    Context ctx = Context.create().addVariable("str", String.class);
    String s = StatementBuilder.create(ctx)
        .while_(Bool.expr(Stmt.loadVariable("str").invoke("length"), BooleanOperator.GreaterThanOrEqual, 2))
        .finish().toJavaString();

    assertEquals("Failed to generate while loop with rhs and no body", WHILE_RHS_EMPTY, s);
  }

  @Test
  public void testWhileLoopUnchainedWithNestedExpressions() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .while_(Bool.expr(
            Bool.expr(Variable.get("str"), BooleanOperator.NotEquals, null),
            BooleanOperator.And,
            Bool.expr(Stmt.loadVariable("str").invoke("length"), BooleanOperator.GreaterThan, 0)))
        .finish().toJavaString();

    assertEquals("Failed to generate while loop with nested expressions and no body", WHILE_NESTED_EMPTY, s);
  }

  @Test
  public void testWhileLoopsNested() {
    Context c = Context.create().addVariable("str", String.class).addVariable("str2", String.class);

    String s = StatementBuilder.create(c)
            .loadVariable("str")
            .while_(BooleanOperator.NotEquals, null)
            .append(
                StatementBuilder.create(c)
                    .while_(Bool.expr(Variable.get("str2"), BooleanOperator.NotEquals, null))
                    .finish())
            .finish().toJavaString();

    assertEquals("Failed to generate nested while loops", WHILE_NESTED_LOOPS, s);
  }

  @Test
  public void testForLoopUnchainedWithoutInitializerAndCountingExpression() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class, 0)
        .for_(Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100))
        .finish().toJavaString();

    assertEquals("Failed to generate for loop without initializer",
        FOR_NO_INITIALIZER_NO_COUNTING_EXP_EMPTY, s);
  }

  @Test
  public void testForLoopChainedWithoutCountingExpression() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class, 0)
        .loadVariable("i")
        .for_(Stmt.loadVariable("i").assignValue(0), Bool.expr(BooleanOperator.LessThan, 100))
        .finish().toJavaString();

    assertEquals("Failed to generate for loop with initializer and chained lhs",
        FOR_CHAINED_INITIALIZER_NO_COUNTING_EXP_EMPTY, s);
  }

  @Test
  public void testForLoopUnchainedWithInitializer() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class)
        .for_(StatementBuilder.create().loadVariable("i").assignValue(0),
            Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100))
        .finish().toJavaString();

    assertEquals("Failed to generate for loop with initializer",
        FOR_INITIALIZER_NO_COUNTING_EXP_EMPTY, s);
  }

  @Test
  public void testForLoopChainedWithCountingExpression() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class, 0)
        .loadVariable("i")
        .for_(Stmt.loadVariable("i").assignValue(0), Bool.expr(BooleanOperator.LessThan, 100),
            StatementBuilder.create().loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
        .finish().toJavaString();

    assertEquals("Failed to generate for loop with initializer and counting expression and chained lhs",
        FOR_CHAINED_INITIALIZER_COUNTING_EXP_EMPTY, s);
  }

  @Test
  public void testForLoopUnchainedWithInitializerAndCountingExpression() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class)
        .for_(StatementBuilder.create().loadVariable("i").assignValue(0),
            Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100),
            StatementBuilder.create().loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
        .finish().toJavaString();

    assertEquals("Failed to generate for loop with initializer and counting expression",
        FOR_INITIALIZER_COUNTING_EXP_EMPTY, s);
  }

  @Test
  public void testForLoopUnchainedWithDeclaringInitializerAndCountingExpression() {
    String s = StatementBuilder.create()
        .for_(Stmt.declareVariable(int.class).named("i").initializeWith(0),
            Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100),
            StatementBuilder.create().loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
        .append(StatementBuilder.create().loadStatic(System.class, "out").invoke("println", Variable.get("i")))
        .finish().toJavaString();

    assertEquals("Failed to generate for loop with declaring initializer and counting expression",
        FOR_DECLARE_INITIALIZER_COUNTING_EXP, s);
  }

  @Test
  public void testDoWhileLoopUnchainedWithoutRhs() {
    String s = StatementBuilder.create()
        .declareVariable("b", Boolean.class)
        .do_()
          .append(StatementBuilder.create().loadVariable("b").assignValue(false))
        .finish()
        .while_(Bool.expr(Variable.get("b")))
        .toJavaString();

    assertEquals("Failed to generate do while loop with simple expression (no operator and rhs)",
        DOWHILE_SIMPLE_EXPRESSION_NO_OP, s);
  }

  @Test
  public void testDoWhileLoopChainedWithoutRhs() {
    String s = StatementBuilder.create()
        .declareVariable("b", Boolean.class)
        .loadVariable("b")
        .do_()
          .append(StatementBuilder.create().loadVariable("b").assignValue(false))
        .finish()
        .while_()
        .toJavaString();

    assertEquals("Failed to generate for do while loop with simple expression (no operator and rhs) and chained lhs",
        DOWHILE_SIMPLE_EXPRESSION_NO_OP, s);
  }

  @Test
  public void testDoWhileLoopChainedWithRhs() {
    String s = StatementBuilder.create()
        .declareVariable("n", Integer.class)
        .loadVariable("n")
        .do_()
          .append(StatementBuilder.create().loadVariable("n").assignValue(1))
        .finish()
        .while_(BooleanOperator.GreaterThanOrEqual, 1)
        .toJavaString();

    assertEquals("Failed to generate for do while loop with simple expression (no operator and rhs) and chained lhs",
        DOWHILE_SIMPLE_EXPRESSION, s);
  }

  @Test
  public void testDoWhileLoopUnchainedWithNestedExpressions() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .do_()
          .append(StatementBuilder.create().loadStatic(System.class, "out").invoke("println", Variable.get("str")))
        .finish()
        .while_(Bool.expr(
            Bool.expr(Variable.get("str"), BooleanOperator.NotEquals, null),
            BooleanOperator.And,
            Bool.expr(Stmt.loadVariable("str").invoke("length"), BooleanOperator.GreaterThan, 0)))
        .toJavaString();

    assertEquals("Failed to generate do while loop with nested expression", DOWHILE_NESTED_EXPRESSION, s);
  }

  @Test
  public void testLoopWithContinue() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class, 0)
        .loadVariable("i")
        .if_(BooleanOperator.GreaterThan, 100)
        .append(Stmt
            .for_(Stmt.loadVariable("i").assignValue(0),
                Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100),
                Stmt.loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
            .append(
                Stmt.if_(Bool.expr(Variable.get("i"), BooleanOperator.Equals, 50))
                    .append(Stmt.continue_())
                    .finish())
            .finish())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate loop with continue", LOOP_WITH_CONTINUE, s);
  }

  @Test
  public void testLoopWithContinueAndLabel() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class, 0)
        .loadVariable("i")
        .if_(BooleanOperator.GreaterThan, 100)
        .append(Stmt.label("label"))
        .append(Stmt
            .for_(Stmt.loadVariable("i").assignValue(0),
                Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100),
                Stmt.loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
            .append(Stmt
                .if_(Bool.expr(Variable.get("i"), BooleanOperator.Equals, 50))
                .append(Stmt.continue_("label"))
                .finish())
            .finish())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate loop with continue and label", LOOP_WITH_CONTINUE_AND_LABEL, s);
  }

  @Test
  public void testLoopWithBreak() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class, 0)
        .loadVariable("i")
        .if_(BooleanOperator.GreaterThan, 100)
        .append(Stmt
            .for_(Stmt.loadVariable("i").assignValue(0),
                Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100),
                Stmt.loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
            .append(Stmt
                .if_(Bool.expr(Variable.get("i"), BooleanOperator.Equals, 50))
                .append(Stmt.break_())
                .finish())
            .finish())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate loop with continue", LOOP_WITH_BREAK, s);
  }

  @Test
  public void testLoopWithBreakAndLabel() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class, 0)
        .loadVariable("i")
        .if_(BooleanOperator.GreaterThan, 100)
        .append(Stmt.label("label"))
        .append(Stmt
            .for_(Stmt.loadVariable("i").assignValue(0),
                Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100),
                Stmt.loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
            .append(Stmt
                .if_(Bool.expr(Variable.get("i"), BooleanOperator.Equals, 50))
                .append(Stmt.break_("label"))
                .finish())
            .finish())
        .finish()
        .toJavaString();

    assertEquals("Failed to generate loop with continue and label", LOOP_WITH_BREAK_AND_LABEL, s);
  }

  @Test
  public void testLoopWithInvalidLabel() {
    try {
      StatementBuilder.create()
          .declareVariable("i", Integer.class, 0)
          .loadVariable("i")
          .if_(BooleanOperator.GreaterThan, 100)
          .append(Stmt.label("label"))
          .append(Stmt
              .for_(Stmt.loadVariable("i").assignValue(0),
                  Bool.expr(Variable.get("i"), BooleanOperator.LessThan, 100),
                  Stmt.loadVariable("i").assignValue(AssignmentOperator.PreIncrementAssign, 1))
              .append(Stmt.continue_("undefinedlabel"))
              .finish())
          .finish()
          .toJavaString();
      fail("expected OutOfScopeException");
    }
    catch (OutOfScopeException e) {
      // expected
    }
  }
}
