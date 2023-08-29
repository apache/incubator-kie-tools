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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.literal.LiteralFactory;
import org.jboss.errai.codegen.test.model.Person;
import org.jboss.errai.codegen.test.model.PersonImpl;
import org.jboss.errai.codegen.util.Stmt;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class LiteralTest extends AbstractCodegenTest {

  @Test
  public void testIntegerLiteral() {
    assertEquals("1234", LiteralFactory.getLiteral(1234).generate(Context.create()));
  }

  @Test
  public void testShortLiteral() {
    assertEquals("1234", LiteralFactory.getLiteral((short) 1234).generate(Context.create()));
  }

  @Test
  public void testLongLiteral() {
    assertEquals("1234L", LiteralFactory.getLiteral(1234L).generate(Context.create()));
  }

  @Test
  public void testDoubleLiteral() {
    assertEquals("1234.567d", LiteralFactory.getLiteral(1234.567d).generate(Context.create()));
  }

  @Test
  public void testFloatLiteral() {
    assertEquals("1234.567f", LiteralFactory.getLiteral(1234.567f).generate(Context.create()));
  }

  @Test
  public void testByteLiteral() {
    assertEquals("72", LiteralFactory.getLiteral((byte) 72).generate(Context.create()));
  }

  @Test
  public void testBooleanLiteral() {
    assertEquals("false", LiteralFactory.getLiteral(false).generate(Context.create()));
  }

  @Test
  public void testStringLiteral() {
    final String expected = "\"The quick brown fox said \\\"how do you do?\\\"\\nNew line.\\rCarriage Return!"
        + "\\t and a tab\"";

    final String input = "The quick brown fox said \"how do you do?\"\nNew line.\rCarriage Return!"
        + "\t and a tab";

    assertEquals(expected, LiteralFactory.getLiteral(input).generate(Context.create()));
  }

  @Test
  public void testStringArrayCreation() {
    final String[][] input = new String[][]{{"Hello1", "Hello2"}, {"Hello3", "Hello4"}};
    final String expected = "new String[][] { { \"Hello1\", \"Hello2\" }, { \"Hello3\", \"Hello4\" } }";

    assertEquals(expected, LiteralFactory.getLiteral(input).generate(Context.create()));
  }

  /**
   * This tests that any caching in LiteralFactory does not affect literal
   * values generated from mutated arrays.
   */
  @Test
  public void testGenerateIntArrayThenModifyThenGenerateAgain() {
    final int[] a = new int[] { 1, 2, 3, 4 };

    assertEquals("new int[] { 1, 2, 3, 4 }", LiteralFactory.getLiteral(a).generate(Context.create()));

    a[0] = 10;

    assertEquals("new int[] { 10, 2, 3, 4 }", LiteralFactory.getLiteral(a).generate(Context.create()));
  }

  /**
   * This tests that any caching in LiteralFactory does not affect literal
   * values generated from mutated arrays of identity-equality values.
   */
  @Test
  public void testGenerateObjectArrayThenModifyThenGenerateAgain() {
    PersonImpl p = new PersonImpl("person", 1, null);

    {
      // pre-flight check: this test is only effective if PersonImpl is equal-by-identity
      // and not equal-by-value (that is, PersonImpl inherits Object.equals behaviour)
      PersonImpl p2 = new PersonImpl("person", 1, null);
      assertNotSame(p, p2);
      assertFalse(p.equals(p2));
    }

    // now the test itself
    final Person[] a = new Person[] { p };

    Context ctx = Context.create();
    ctx.addLiteralizableClass(Person.class);

    assertEquals("new org.jboss.errai.codegen.test.model.Person[] { " +
        "new org.jboss.errai.codegen.test.model.Person() { " +
        "public int getAge() { return 1; } " +
        "public org.jboss.errai.codegen.test.model.Person getMother() { return null; } " +
        "public String getName() { return \"person\"; } } }",
        LiteralFactory.getLiteral(a).generate(ctx));

    p.setAge(10);

    assertEquals("new org.jboss.errai.codegen.test.model.Person[] { " +
        "new org.jboss.errai.codegen.test.model.Person() { " +
        "public int getAge() { return 10; } " +
        "public org.jboss.errai.codegen.test.model.Person getMother() { return null; } " +
        "public String getName() { return \"person\"; } } }",
        LiteralFactory.getLiteral(a).generate(ctx));
  }


  @Test
  public void testClassLiteral() {
    assertEquals("String.class", LiteralFactory.getLiteral(String.class).generate(Context.create()));
  }

  @Test
  public void testSetEncoding() {
    Set<String> s = new LinkedHashSet<String>();
    s.add("foo");
    s.add("bar");

    assertEquals("new java.util.HashSet() {\n" +
            "  {\n" +
            "    add(\"foo\");\n" +
            "    add(\"bar\");\n" +
            "  }\n" +
            "}", Stmt.load(s).generate(Context.create()));

  }

  @Test
  public void testListEncoding() {
    List<String> s = new ArrayList<String>();
    s.add("foo");
    s.add("bar");

    assertEquals("new java.util.ArrayList() {\n" +
            "  {\n" +
            "    add(\"foo\");\n" +
            "    add(\"bar\");\n" +
            "  }\n" +
            "}", Stmt.load(s).generate(Context.create()));

  }

  @Test
  public void testMapEncoding() {
    Map<String, String> s = new LinkedHashMap<String, String>();
    s.put("foo", "fooz");
    s.put("bar", "barz");

    assertEquals("new java.util.HashMap() {\n" +
            "  {\n" +
            "    put(\"foo\", \"fooz\");\n" +
            "    put(\"bar\", \"barz\");\n" +
            "  }\n" +
            "}", Stmt.load(s).generate(Context.create()));

  }
}
