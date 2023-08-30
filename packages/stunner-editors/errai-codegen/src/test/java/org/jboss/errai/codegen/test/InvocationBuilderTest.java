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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.impl.ContextBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.exception.OutOfScopeException;
import org.jboss.errai.codegen.exception.UndefinedMethodException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.test.model.Foo;
import org.jboss.errai.codegen.util.Refs;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the generation of method invocations using the {@link StatementBuilder} API.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@SuppressWarnings("serial")
public class InvocationBuilderTest extends AbstractCodegenTest {

  @Test
  public void testSingleInvocation() {
    String s = StatementBuilder.create()
        .declareVariable("obj", Object.class)
        .loadVariable("obj")
        .invoke("toString")
        .toJavaString();

    assertEquals("Failed to generate invocation on variable", "obj.toString()", s);
  }

  @Test
  public void testChainedInvocations() {
    String s = StatementBuilder.create()
        .declareVariable("i", Integer.class)
        .declareVariable("regex", String.class)
        .declareVariable("replacement", String.class)
        .loadVariable("i")
        .invoke("toString")
        .invoke("replaceAll", Variable.get("regex"), Variable.get("replacement"))
        .toJavaString();

    assertEquals("Failed to generate chained invocations on variable",
        "i.toString().replaceAll(regex, replacement)", s);
  }

  @Test
  public void testInvokeWithLiteralParameters() {
    String result = StatementBuilder.create().declareVariable("s", String.class)
        .loadVariable("s").invoke("replaceAll", "foo", "foo\t\n").toJavaString();

    assertEquals("Failed to generate invocation using literal parameters",
        "s.replaceAll(\"foo\", \"foo\\t\\n\")", result);
  }

  @Test
  public void testInvokeOnLiteral() {
    String result = StatementBuilder.create().loadLiteral("foo").invoke("toString").toJavaString();

    assertEquals("Failed to generate invocation using literal parameters",
        "\"foo\".toString()", result);
  }

  @Test
  public void testInvokeBestMatchingMethod() {
    String s = StatementBuilder.create()
        .declareVariable("n", Integer.class)
        .loadVariable("n")
            // 1 will be inferred to LiteralValue<Integer>, equals(Integer.class)
            // should be matched equals(Object.class)
        .invoke("equals", 1)
        .toJavaString();

    assertEquals("Failed to generate invocation on matched method", "n.equals(1)", s);
  }

  @Test
  public void testInvokeUndefinedMethodOnVariable() {
    try {
      StatementBuilder.create()
          .declareVariable("obj", Object.class)
          .declareVariable("param", String.class)
          .loadVariable("obj")
          .invoke("undefinedMethod", Variable.get("param"))
          .toJavaString();
      fail("expected UndefinedMethodException");
    }
    catch (UndefinedMethodException udme) {
      // expected
      assertEquals("Wrong exception thrown", udme.getMethodName(), "undefinedMethod");
    }
  }

  @Test
  public void testInvokeChainedUndefinedMethod() {
    try {
      StatementBuilder.create()
          .declareVariable("s", String.class)
          .declareVariable("regex", String.class)
          .declareVariable("replacement", String.class)
          .loadVariable("s")
          .invoke("replaceAll", Variable.get("regex"), Variable.get("replacement"))
          .invoke("undefinedMethod", Variable.get("regex"), Variable.get("replacement"))
          .toJavaString();
      fail("expected UndefinedMethodException");
    }
    catch (UndefinedMethodException udme) {
      // expected
      assertEquals("Wrong exception thrown", udme.getMethodName(), "undefinedMethod");
    }
  }

  @Test
  public void testInvokeOnUndefinedVariable() {
    try {
      // injector undefined
      StatementBuilder.create()
          .loadVariable("injector")
          .invoke("provide", Refs.get("param"), Refs.get("param2"))
          .toJavaString();
      fail("expected OutOfScopeException");
    }
    catch (OutOfScopeException oose) {
      // expected
      assertTrue("Wrong exception thrown", oose.getMessage().contains("injector"));
    }
  }

  @Test
  public void testInvokeWithUndefinedVariable() {
    try {
      // param2 undefined
      StatementBuilder.create()
          .declareVariable("obj", Object.class)
          .declareVariable("param", String.class)
          .loadVariable("obj")
          .invoke("undefinedMethod", Variable.get("param"), Variable.get("param2"))
          .toJavaString();
      fail("expected OutOfScopeException");
    }
    catch (OutOfScopeException oose) {
      // expected
      assertTrue(oose.getMessage().contains("param2"));
    }
  }

  @Test
  public void testInvokeUsingStandardizedLoadVariableReference() {
    Context context = ContextBuilder.create()
        .addVariable("s", String.class)
        .getContext();

    String s = StatementBuilder.create(context)
        .load(Variable.get("s"))
        .invoke("toUpperCase").toJavaString();

    assertEquals("Failed using load() passing a variable reference", "s.toUpperCase()", s);
  }

  @Test
  public void testInvokeUsingStandardizedLoadVariableInstance() {
    Context context = ContextBuilder.create()
        .addVariable("s", String.class)
        .getContext();

    Variable v = Variable.create("s", String.class);
    String s = StatementBuilder.create(context)
        .load(v)
        .invoke("toUpperCase").toJavaString();

    assertEquals("Failed using load() passing a variable instance", "s.toUpperCase()", s);
  }

  @Test
  public void testInvokeUsingStandardizedLoadLiteral() {
    String s = StatementBuilder.create()
        .load("foo")
        .invoke("toUpperCase").toJavaString();

    assertEquals("Failed injecting literal with load()", "\"foo\".toUpperCase()", s);
  }

  @Test
  public void testInvokeWithParameterTypeConversionOfIntegerToString() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("endsWith", 123)
        .toJavaString();

    assertEquals("Failed to generate invocation with parameter type conversion", "str.endsWith(\"123\")", s);
  }

  @Test
  public void testInvokeWithParameterTypeConversionOfStringToInteger() {
    String s = StatementBuilder.create()
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("substring", "1", "3")
        .toJavaString();

    assertEquals("Failed to generate invocation with parameter type conversion", "str.substring(1, 3)", s);
  }

  @Test
  public void testInvokeWithParameterTypeConversionOfVariable() {
    Context c = Context.create().addVariable("n", Integer.class, 123);
    String s = StatementBuilder.create(c)
        .declareVariable("str", String.class)
        .loadVariable("str")
        .invoke("endsWith", c.getVariable("n").getValue())
        .toJavaString();

    assertEquals("Failed to generate invocation with parameter type conversion of variable",
        "str.endsWith(\"123\")", s);
  }

  @Test
  public void testInvokeStaticMethod() {
    String s = StatementBuilder.create()
        .invokeStatic(Integer.class, "getInteger", "123")
        .toJavaString();

    assertEquals("Failed to generate static method invocation", "Integer.getInteger(\"123\")", s);
  }

  @Test
  public void testInvokeUndefinedStaticMethod() {
    try {
      StatementBuilder.create()
          .invokeStatic(Integer.class, "intValue")
          .toJavaString();
      fail("expected UndefinedMethodException");
    }
    catch (UndefinedMethodException udme) {
      // expected
      assertEquals("Wrong exception details", udme.getMethodName(), "intValue");
    }
  }

  @Test
  public void testInvokeWithVariableReturnType() {
    String s =
        StatementBuilder.create(Context.create().autoImport())
            .declareVariable("s", String.class)
            .declareVariable("str", String.class,
                StatementBuilder.create().invokeStatic(Foo.class, "foo", Variable.get("s")))
            .toJavaString();

    assertEquals("Failed to generate method invocation using variable return type",
        "String str = Foo.foo(s);", s);
  }

  @Test
  public void testInvokeWithInvalidVariableReturnType() {

    try {
      StatementBuilder.create(Context.create().autoImport())
          .declareVariable("list", new TypeLiteral<List<String>>() {
          })
          .declareVariable("n", Integer.class,
              StatementBuilder.create().invokeStatic(Foo.class, "bar", Variable.get("list")))
          .toJavaString();
      fail("expected InvalidTypeException");
    }
    catch (InvalidTypeException e) {
      // expected
      assertEquals("Wrong exception message",
          "java.lang.Integer is not assignable from java.lang.String", e.getMessage());
    }
  }

  @Test
  public void testInvokeWithParameterizedListAndVariableReturnType() {
    String s =
        StatementBuilder.create(Context.create().autoImport())
            .declareVariable("list", new TypeLiteral<List<String>>() {
            })
            .declareVariable("str", String.class,
                StatementBuilder.create().invokeStatic(Foo.class, "bar", Variable.get("list")))
            .toJavaString();

    assertEquals("Failed to generate method invocation with variable return type inferred from List<T>",
        "String str = Foo.bar(list);", s);
  }

  @Test
  public void testInvokeWithNestedParameterizedListAndVariableReturnType() {
    String s =
        StatementBuilder.create(Context.create().autoImport())
            .declareVariable("n", int.class)
            .declareVariable("list", new TypeLiteral<List<List<Map<String, Integer>>>>() {
            })
            .declareVariable("str", String.class,
                StatementBuilder.create().invokeStatic(Foo.class, "bar", Variable.get("n"), Variable.get("list")))
            .toJavaString();

    assertEquals("Failed to generate method invocation with variable return type inferred from nested List<T>",
        "String str = Foo.bar(n, list);", s);
  }

  @Test
  public void testInvokeWithParameterizedMapAndVariableReturnType() {
    String s =
        StatementBuilder.create(Context.create().autoImport())
            .declareVariable("map", new TypeLiteral<Map<String, Integer>>() {
            })
            .declareVariable("val", Integer.class,
                StatementBuilder.create().invokeStatic(Foo.class, "bar", Variable.get("map")))
            .toJavaString();

    assertEquals("Failed to generate method invocation with variable return type inferred from Map<K, V>",
        "Integer val = Foo.bar(map);", s);
  }

  @Test
  public void testInvokeWithParameterizedClassAndVariableReturnType() {
    String s =
        StatementBuilder.create(Context.create().autoImport())
            .declareVariable("set", Set.class,
                StatementBuilder.create().invokeStatic(Foo.class, "baz", Set.class))
            .toJavaString();

    assertEquals("Failed to generate method invocation with variable return type inferred from Class<T>",
        "Set set = Foo.baz(Set.class);", s);
  }

  @Test
  public void testLookupOfMethodWithArrayParameters() {
    final MetaClass metaClass = MetaClassFactory.get(Arrays.class);
    final MetaMethod equals = metaClass.getBestMatchingMethod("equals", Class[].class, Class[].class);
    assertEquals("public boolean equals([[Ljava.lang.Object;, [Ljava.lang.Object;])", equals.toString());
  }
}
