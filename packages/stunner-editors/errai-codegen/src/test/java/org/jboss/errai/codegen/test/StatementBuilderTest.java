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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import org.jboss.errai.codegen.AssignmentOperator;
import org.jboss.errai.codegen.Cast;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.VariableReference;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.exception.OutOfScopeException;
import org.jboss.errai.codegen.exception.UndefinedFieldException;
import org.jboss.errai.codegen.literal.LiteralFactory;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.test.model.BeanWithTypeParmedMeths;
import org.jboss.errai.codegen.test.model.Foo;
import org.jboss.errai.codegen.test.model.TEnum;
import org.jboss.errai.codegen.util.Bitwise;
import org.jboss.errai.codegen.util.Bool;
import org.jboss.errai.codegen.util.Expr;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the {@link StatementBuilder} API.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class StatementBuilderTest extends AbstractCodegenTest {

  @Test
  public void testDeclareVariableWithExactTypeProvided() {
    final Context ctx = Context.create();
    final String s = StatementBuilder.create().declareVariable("n", Integer.class, 10).generate(ctx);

    assertEquals("failed to generate variable declaration with type provided",
            "Integer n = 10;", s);

    final VariableReference n = ctx.getVariable("n");
    assertEquals("Wrong variable name", "n", n.getName());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(Integer.class), n.getType());
    Assert.assertEquals("Wrong variable value", LiteralFactory.getLiteral(10), n.getValue());
  }

  @Test
  public void testDeclareVariableWithIntegerTypeInference() {
    final Context ctx = Context.create();
    final String s = StatementBuilder.create().declareVariable("n", 10).generate(ctx);

    assertEquals("failed to generate variable declaration with Integers type inference",
            "Integer n = 10;", s);

    final VariableReference n = ctx.getVariable("n");
    assertEquals("Wrong variable name", "n", n.getName());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(Integer.class), n.getType());
    Assert.assertEquals("Wrong variable value", LiteralFactory.getLiteral(10), n.getValue());
  }

  @Test
  public void testDeclareVariableWithStringTypeInference() {
    final Context ctx = Context.create();
    final String s = StatementBuilder.create().declareVariable("n", "10").generate(ctx);

    assertEquals("failed to generate variable declaration with =String type inference",
            "String n = \"10\";", s);

    final VariableReference n = ctx.getVariable("n");
    assertEquals("Wrong variable name", "n", n.getName());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(String.class), n.getType());
    Assert.assertEquals("Wrong variable value", LiteralFactory.getLiteral("10"), n.getValue());
  }

  @Test
  public void testDeclareVariableWithImplicitTypeConversion() {
    final Context ctx = Context.create();
    final String s = StatementBuilder.create().declareVariable("n", Integer.class, "10").generate(ctx);

    assertEquals("failed to generate variable declaration with implicit type conversion",
            "Integer n = 10;", s);

    final VariableReference n = ctx.getVariable("n");
    assertEquals("Wrong variable name", "n", n.getName());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(Integer.class), n.getType());
    Assert.assertEquals("Wrong variable value", LiteralFactory.getLiteral(10), n.getValue());

    try {
      StatementBuilder.create().declareVariable("n", Integer.class, "abc").toJavaString();
      fail("Expected InvalidTypeException");
    }
    catch (InvalidTypeException ive) {
      // expected
      assertTrue(ive.getCause() instanceof NumberFormatException);
    }
  }

  @Test
  public void testDeclareVariableWithObjectInitializationWithExactTypeProvided() {
    final Context ctx = Context.create();
    final String s = StatementBuilder.create().declareVariable("str", String.class,
            ObjectBuilder.newInstanceOf(String.class)).generate(ctx);

    assertEquals("failed to generate variable declaration with object initialization and type provided",
            "String str = new String();", s);

    final VariableReference str = ctx.getVariable("str");
    assertEquals("Wrong variable name", "str", str.getName());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(String.class), str.getType());
  }

  @Test
  public void testDeclareVariableWithObjectInitializationWithStringTypeInference() {
    final Context ctx = Context.create();
    final String s = StatementBuilder.create(ctx)
            .declareVariable("str", ObjectBuilder.newInstanceOf(String.class)).toJavaString();

    assertEquals("failed to generate variable declaration with object initialization and string type inference",
            "String str = new String();", s);

    final VariableReference str = ctx.getVariable("str");
    assertEquals("Wrong variable name", "str", str.getName());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(String.class), str.getType());
  }

  @Test
  public void testDeclareVariableWithStatementInitialization() {
    final Context ctx = Context.create();
    final String s = Stmt.declareVariable("str", String.class,
            Stmt.nestedCall(Stmt.newObject(Integer.class).withParameters(2)).invoke("toString"))
            .generate(ctx);

    assertEquals("failed to generate variable declaration with statement initialization",
            "String str = new Integer(2).toString();", s);

    final VariableReference str = ctx.getVariable("str");
    assertEquals("Wrong variable name", "str", str.getName());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(String.class), str.getType());
  }

  @Test
  public void testDeclareFinalVariable() {
    final Context ctx = Context.create();
    final String s = StatementBuilder.create(ctx)
            .declareVariable(String.class).asFinal().named("str").initializeWith("10").toJavaString();

    assertEquals("failed to generate final variable declaration", "final String str = \"10\";", s);

    final VariableReference str = ctx.getVariable("str");
    assertEquals("Wrong variable name", "str", str.getName());
    Assert.assertTrue("Variable should be final", ctx.getVariables().get("str").isFinal());
    Assert.assertEquals("Wrong variable type", MetaClassFactory.get(String.class), str.getType());
  }

  @Test
  public void testLoadUndefinedVariable() {
    try {
      StatementBuilder.create().loadVariable("n").toJavaString();
      fail("Expected OutOfScopeException");
    }
    catch (OutOfScopeException e) {
      // expected
    }
  }

  @Test
  public void testCreateAndInitializeArray() {
    final String s = StatementBuilder.create().newArray(String.class).initialize("1", "2").toJavaString();
    assertEquals("Failed to generate 1-dimensional String array", "new String[] { \"1\", \"2\" }", s);
  }

  @Test
  public void testCreateAndInitializeArrayWithInvalidInitialization() {
    try {
      StatementBuilder.create().newArray(Annotation.class)
              .initialize("1", "2")
              .toJavaString();
      fail("Expected InvalidTypeException");
    }
    catch (InvalidTypeException oose) {
      // expected
    }
  }

  @Test
  public void testCreateAndInitializeArrayWithMissingInitializationAndDimensions() {
    try {
      StatementBuilder.create().newArray(String.class).toJavaString();
      fail("Expected RuntimeException");
    }
    catch (Exception e) {
      // expected
      assertEquals("Wrong exception details",
              "Must provide either dimension expressions or an array initializer", e.getMessage());
    }
  }

  @Test
  public void testCreateAndInitializeAnnotationArray() {
    final Statement annotation1 = ObjectBuilder.newInstanceOf(Annotation.class)
            .extend()
            .publicOverridesMethod("annotationType")
            .append(StatementBuilder.create().load(Inject.class).returnValue())
            .finish()
            .finish();

    final Statement annotation2 = ObjectBuilder.newInstanceOf(Annotation.class)
            .extend()
            .publicOverridesMethod("annotationType")
            .append(StatementBuilder.create().load(PostConstruct.class).returnValue())
            .finish()
            .finish();

    final String s = StatementBuilder.create().newArray(Annotation.class)
            .initialize(annotation1, annotation2)
            .toJavaString();

    assertEquals("failed to generate Annotation array",
            "new java.lang.annotation.Annotation[] { " +
                    "new java.lang.annotation.Annotation() {\n" +
                    " public Class annotationType() {\n" +
                    "   return javax.inject.Inject.class;\n" +
                    " }\n" +
                    "}" +
                    ", new java.lang.annotation.Annotation() {\n" +
                    "   public Class annotationType() {\n" +
                    "     return javax.annotation.PostConstruct.class;\n" +
                    "   }\n" +
                    " }\n" +
                    "}", s);
  }

  @Test
  @SuppressWarnings(value = { "all" })
  public void testCreateAndInitializeTwoDimensionalArray() {
    final String s = StatementBuilder.create().newArray(Integer.class)
            .initialize(new Integer[][] { { 1, 2 }, { 3, 4 } })
            .toJavaString();

    assertEquals("Failed to generate two dimensional array", "new Integer[][] { { 1, 2 }, { 3, 4 } }", s);
  }

  @Test
  @SuppressWarnings(value = { "all" })
  public void testCreateAndInitializeTwoDimensionalArrayWithSingleValue() {
    final String s = StatementBuilder.create().newArray(Integer.class)
            .initialize(new Object[][] { { 1, 2 } })
            .toJavaString();

    assertEquals("Failed to generate two dimensional array", "new Integer[][] { { 1, 2 } }", s);
  }

  @Test
  @SuppressWarnings(value = { "all" })
  public void testCreateAndInitializeTwoDimensionalObjectArrayWithIntegers() {
    final String s = StatementBuilder.create().newArray(Object.class)
            .initialize(new Object[][] { { 1, 2 } })
            .toJavaString();

    assertEquals("Failed to generate two dimensional array", "new Object[][] { { 1, 2 } }", s);
  }

  @Test
  @SuppressWarnings(value = { "all" })
  public void testCreateAndInitializeTwoDimensionalArrayWithStatements() {
    final String s = StatementBuilder.create().newArray(String.class)
            .initialize(new Statement[][] {
                    { StatementBuilder.create().invokeStatic(Integer.class, "toString", 1),
                            StatementBuilder.create().invokeStatic(Integer.class, "toString", 2) },
                    { StatementBuilder.create().invokeStatic(Integer.class, "toString", 3),
                            StatementBuilder.create().invokeStatic(Integer.class, "toString", 4) } })
            .toJavaString();

    assertEquals("Failed to generate two dimensional array using statements",
            "new String[][] { { Integer.toString(1), Integer.toString(2) }, " +
                    "{ Integer.toString(3), Integer.toString(4) } }", s);
  }

  @Test
  @SuppressWarnings(value = { "all" })
  public void testCreateAndInitializeTwoDimensionalArrayWithStatementsAndLiterals() {
    final String s = StatementBuilder.create().newArray(String.class)
            .initialize(new Object[][] {
                    { StatementBuilder.create().invokeStatic(Integer.class, "toString", 1), "2" },
                    { StatementBuilder.create().invokeStatic(Integer.class, "toString", 3), "4" } })
            .toJavaString();

    assertEquals("Failed to generate two dimensional array using statements and objects",
            "new String[][] { { Integer.toString(1), \"2\" }," +
                    " { Integer.toString(3), \"4\" } }", s);
  }

  @Test
  @SuppressWarnings(value = { "all" })
  public void testCreateAndInitializeThreeDimensionalArray() {
    final String s = StatementBuilder.create().newArray(String.class)
            .initialize(new String[][][] { { { "1", "2" }, { "a", "b" } }, { { "3", "4" }, { "b", "c" } } })
            .toJavaString();

    assertEquals("Failed to generate three dimensional array",
            "new String[][][] { { { \"1\", \"2\" }, { \"a\", \"b\" } }, { { \"3\", \"4\" }, { \"b\", \"c\" } } }", s);
  }

  @Test
  public void testAssignArrayValue() {
    final String s = StatementBuilder.create()
            .declareVariable("twoDimArray", String[][].class)
            .loadVariable("twoDimArray", 1, 2)
            .assignValue("test")
            .toJavaString();

    assertEquals("Failed to generate array assignment", "twoDimArray[1][2] = \"test\";", s);
  }

  @Test
  public void testAssignArrayValueWithPreIncrementAssignment() {
    final String s = StatementBuilder.create()
            .declareVariable("twoDimArray", String[][].class)
            .loadVariable("twoDimArray", 1, 2)
            .assignValue(AssignmentOperator.PreIncrementAssign, "test")
            .toJavaString();

    assertEquals("Failed to generate array assignment", "twoDimArray[1][2] += \"test\";", s);
  }

  @Test
  public void testAssignArrayValueWithVariableIndexes() {
    final String s = StatementBuilder.create()
            .declareVariable("twoDimArray", String[][].class)
            .declareVariable("i", int.class)
            .declareVariable("j", int.class)
            .loadVariable("twoDimArray", Variable.get("i"), Variable.get("j"))
            .assignValue("test")
            .toJavaString();

    assertEquals("Failed to generate array assignment", "twoDimArray[i][j] = \"test\";", s);
  }

  @Test
  public void testAssignArrayValueWithInvalidArray() {
    try {
      StatementBuilder.create()
         .declareVariable("twoDimArray", String.class)
         .loadVariable("twoDimArray", 1, 2)
         .assignValue("test")
         .toJavaString();

      fail("Expected InvalidTypeExcpetion");
    }
    catch (InvalidTypeException e) {
      // Expected, variable is not an array.
    }
  }

  @Test
  public void testAssignArrayValueWithInvalidIndexType() {
    try {
      StatementBuilder.create()
              .declareVariable("twoDimArray", String[][].class)
              .declareVariable("i", float.class)
              .declareVariable("j", float.class)
              .loadVariable("twoDimArray", Variable.get("i"), Variable.get("j"))
              .assignValue("test")
              .toJavaString();
      fail("Expected InvalidTypeExcpetion");
    }
    catch (InvalidTypeException e) {
      // Expected, indexes are no integers
    }
  }

  @Test
  public void testObjectCreationWithLiteralParameter() {
    final String s = StatementBuilder.create().newObject(String.class).withParameters("original").toJavaString();
    assertEquals("failed to generate new object with parameters", "new String(\"original\")", s);
  }

  @Test
  public void testObjectCreationWithVariableParameter() {
    final String s = StatementBuilder.create()
            .declareVariable("original", String.class)
            .newObject(String.class).withParameters(Variable.get("original")).toJavaString();
    assertEquals("failed to generate new object with parameters", "new String(original)", s);
  }

  @Test
  public void testObjectCreationWithParameterizedType() {
    final String s = StatementBuilder.create().newObject(new TypeLiteral<ArrayList<String>>() {
        }).toJavaString();
    assertEquals("failed to generate new object with parameterized type", "new java.util.ArrayList<String>()", s);
  }

  @Test
  public void testObjectCreationWithAutoImportedParameterizedType() {
    final Context c = Context.create().autoImport();
    final String s = StatementBuilder.create(c).newObject(new TypeLiteral<ArrayList<Date>>() {
        }).toJavaString();
    assertEquals("failed to generate new object with parameterized type", "new ArrayList<Date>()", s);
  }

  @Test
  public void testObjectCreationWithParameterizedTypeAndClassImport() {
    final Context c = Context.create().addImport(MetaClassFactory.get(ArrayList.class));
    final String s = StatementBuilder.create(c).newObject(new TypeLiteral<ArrayList<String>>() {
        }).toJavaString();
    assertEquals("failed to generate new object with parameterized type", "new ArrayList<String>()", s);
  }

  @Test
  public void testObjectCreationWithFullyQualifiedParameterizedTypeAndClassImport() {
    final Context c = Context.create().addImport(MetaClassFactory.get(ArrayList.class));
    final String s = StatementBuilder.create(c).newObject(new TypeLiteral<ArrayList<Date>>() {
        }).toJavaString();
    assertEquals("failed to generate new object with parameterized type", "new ArrayList<java.util.Date>()", s);
  }

  @Test
  public void testObjectCreationWithNestedParameterizedTypeAndClassImports() {
    final Context c = Context.create()
            .addImport(MetaClassFactory.get(ArrayList.class))
            .addImport(MetaClassFactory.get(HashMap.class));

    final String s = StatementBuilder.create(c)
            .newObject(new TypeLiteral<ArrayList<ArrayList<HashMap<String, Integer>>>>() {
                }).toJavaString();
    assertEquals("failed to generate new object with parameterized type",
        "new ArrayList<ArrayList<HashMap<String, Integer>>>()", s);
  }
  
  @Test
  public void testObjectCreationOfUninstantiableType() {
    try {
      Stmt.newObject(List.class).toJavaString();
      fail("Expected InvalidTypeExcpetion");
    }
    catch (InvalidTypeException e) {
      // Expected, List is not instantiable
    }
  }

  @Test
  public void testThrowExceptionUsingNewInstance() {
    final Context c = Context.create().autoImport();
    final String s = StatementBuilder.create(c).throw_(InvalidTypeException.class).toJavaString();
    assertEquals("failed to generate throw statement using a new instance",
            "throw new InvalidTypeException()", s);
  }

  @Test
  public void testThrowExceptionUsingNewInstanceWithParameters() {
    final Context c = Context.create().autoImport();
    final String s = StatementBuilder.create(c).throw_(InvalidTypeException.class, "message").toJavaString();
    assertEquals("failed to generate throw statement using a new instance",
            "throw new InvalidTypeException(\"message\")", s);
  }

  @Test
  public void testThrowExceptionUsingVariable() {
    final String s = StatementBuilder.create().declareVariable("t", Throwable.class).throw_("t").toJavaString();
    assertEquals("failed to generate throw statement using a variable", "throw t", s);
  }

  @Test
  public void testThrowExceptionUsingInvalidVariable() {
    try {
      StatementBuilder.create()
              .declareVariable("t", Integer.class)
              .throw_("t")
              .toJavaString();
      fail("expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
    }
  }

  @Test
  public void testThrowExceptionUsingUndefinedVariable() {
    try {
      StatementBuilder.create()
              .throw_("t")
              .toJavaString();
      fail("expected OutOfScopeException");
    }
    catch (OutOfScopeException e) {
      // expected
    }
  }

  @Test
  public void testNestedCall() {
    final String s = StatementBuilder.create()
            .nestedCall(
                    StatementBuilder.create().declareVariable("n", Integer.class).loadVariable("n").invoke("toString"))
            .invoke("getBytes")
            .toJavaString();

    assertEquals("failed to generate nested call", "n.toString().getBytes()", s);
  }

  @Test
  public void testAssignField() {
    final String s = Stmt.create(Context.create().autoImport()).nestedCall(
            Stmt.newObject(Foo.class)).loadField("bar").loadField("name").assignValue("test").toJavaString();

    assertEquals("failed to generate nested field assignment",
            "new Foo().bar.name = \"test\";", s);
  }

  @Test
  public void testAssignInvalidField() {
    try {
      final String s = Stmt.create(Context.create().autoImport()).nestedCall(
              Stmt.newObject(Foo.class))
              .loadField("invalid")
              .assignValue("test")
              .toJavaString();

      fail("expected UndefinedFieldException");
    }
    catch (UndefinedFieldException e) {
      // expected
    }
  }

  @Test
  public void testCastDown() {
    final Statement stmt = Cast.to(String.class, Stmt.declareVariable("obj", Object.class).loadVariable("obj"));
    assertEquals("failed to generate cast", "(String) obj", stmt.generate(Context.create()));
  }

  @Test
  public void testCastUp() {
    final Statement stmt = Cast.to(Object.class, Stmt.declareVariable("str", String.class).loadVariable("str"));
    assertEquals("created a redundant cast", "str", stmt.generate(Context.create()));
  }


  @Test
  public void testCastWithVariableGetAPI() {
    final Context ctx = Context.create();
    ctx.addVariable(Variable.create("str", String.class));

    final Statement stmt = Cast.to(Object.class, Variable.get("str"));
    assertEquals("created a redundant cast", "str", stmt.generate(ctx));
  }


  @Test
  public void testInvalidCast() {
    try {
      final Statement stmt = Cast.to(Integer.class, Stmt.declareVariable("str", String.class).loadVariable("str"));
      stmt.generate(Context.create());
      fail("expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
      assertEquals("Wrong exception message", "java.lang.String cannot be cast to java.lang.Integer", e.getMessage());
    }
  }

  @Test
  public void testReturnVoid() {
    final Context ctx = Context.create();
    ctx.addVariable(Variable.create("foo", Object.class));

    final Statement stmt = Stmt.if_(Bool.isNull(Refs.get("foo")))
            .append(Stmt.returnVoid()).finish();

    assertEquals("failed to generate return statement", "if (foo == null) {\n" +
            "  return;\n" +
            "}", stmt.generate(ctx));
  }

  @Test
  public void testTypeInferenceWorksPropertyForParameterizedMethodTypes() {
    final String s =
        Stmt.loadStatic(BeanWithTypeParmedMeths.class, "INSTANCE")
            .invoke("setFooBarMap", Stmt.loadStatic(BeanWithTypeParmedMeths.class, "INSTANCE").invoke("getFooBarMap"))
            .toJavaString();
    
    assertEquals("org.jboss.errai.codegen.test.model.BeanWithTypeParmedMeths.INSTANCE" +
            ".setFooBarMap(org.jboss.errai.codegen.test.model.BeanWithTypeParmedMeths.INSTANCE.getFooBarMap())",
            s);
  }

  @Test
  public void testBitwiseOrExpression() {
    final String generate = Bitwise.or(Stmt.load(1), Stmt.load(2), Stmt.load(3)).generate(Context.create());

    assertEquals("1 | 2 | 3", generate);
  }

  @Test
  public void testBitwiseAndExpression() {
    final String generate = Bitwise.and(Stmt.load(1), Stmt.load(2), Stmt.load(3)).generate(Context.create());

    assertEquals("1 & 2 & 3", generate);
  }

  @Test
  public void testMixedBitwise() {
    final String generate = Bitwise.or(Stmt.load(1), Stmt.load(2),
            Expr.qualify(Bitwise.and(Stmt.load(10), Stmt.load(20)))).generate(Context.create());

    assertEquals("1 | 2 | (10 & 20)", generate);
  }

  @Test
  public void testPassBitwiseToMethodParameter() {
    final Statement bitwiseStatement = Bitwise.or(Stmt.load(1), Stmt.load(2),
            Expr.qualify(Bitwise.and(Stmt.load(10), Stmt.load(20))));

    final String generate = Stmt.newObject(Integer.class).withParameters(bitwiseStatement).generate(Context.create());

    assertEquals("new Integer(1 | 2 | (10 & 20))", generate);
  }

  @Test
  public void testEnumReference() {
    final ContextualStatementBuilder statementBuilder = Stmt.loadStatic(TEnum.class, "FIRST");

    assertEquals(TEnum.class.getName() + ".FIRST", statementBuilder.generate(Context.create()));
  }
}
