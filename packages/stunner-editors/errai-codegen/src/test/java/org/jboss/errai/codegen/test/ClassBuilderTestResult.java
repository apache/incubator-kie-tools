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

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface ClassBuilderTestResult {

  public static final String CLASS_IMPLEMENTING_INTERFACE =
      "     package org.foo;" +
          "\n" +
          " import java.io.Serializable;\n" +
          "\n" +
          " public class Bar implements Serializable {\n" +
          "   private String name;\n" +
          " }";

  public static final String CLASS_IMPLEMENTING_MULTIPLE_INTERFACES =
      "     package org.foo;\n" +
          " import java.io.Serializable;\n" +
          "\n" +
          " public class Bar implements Serializable, Cloneable {\n" +
          "   private String name;\n" +
          " }";

  public static final String CLASS_DECLARING_INNER_CLASS =
      "     package foo.bar;" +
          "\n" +
          " public class Baz {\n" +
          "    public class Inner {\n" +
          "   }" +
          " }";

  public static final String CLASS_WITH_METHOD_USING_INNER_CLASS =
      "     package foo.bar;" +
          "\n" +
          " import java.io.Serializable;\n" +
          "\n" +
          " public class Baz {\n" +
          "   public void someMethod() {\n" +
          "      class Inner implements Serializable {\n" +
          "        private String name;" +
          "        public void setName(final String name) {\n" +
          "          this.name = name;\n" +
          "        }\n" +
          "      }" +
          "      new Inner();" +
          "   }" +
          " }";

  public static final String CLASS_WITH_ACCESSOR_METHODS =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   private String name = \"default\";\n" +
          "   public String getName() {\n" +
          "     return name;\n" +
          "   }\n" +
          "   public void setName(String name) {\n" +
          "     this.name = name;\n" +
          "   }\n" +
          " }";

  public static final String CLASS_WITH_PARENT =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo extends String {\n" +
          "   public Foo(int i) {" +
          "   }" +
          " }";

  public static final String CLASS_WITH_FIELD_INHERITANCE =
      "     package org.foo;" +
          "" +
          " import org.jboss.errai.codegen.test.model.tree.Parent;" +
          "" +
          " public class Foo extends Parent {" +
          "   public Foo() {" +
          "     parentProtected = 0;" +
          "     parentPublic = 0;" +
          "   }" +
          " }";

  public static final String ABSTRACT_CLASS =
      "     package org.foo;\n" +
          "\n" +
          " public abstract class Foo {\n" +
          "   public Foo() {" +
          "   }" +
          " }";

  public static final String ABSTRACT_CLASS_WITH_ABSTRACT_METHODS =
      "     package org.foo;\n" +
          "\n" +
          " public abstract class Foo {\n" +
          "   public Foo() {" +
          "   }" +
          "   public abstract void foo();" +
          "   protected abstract void bar();" +
          "   public void baz() {" +
          "   }" +
          " }";

  public static final String ABSTRACT_CLASS_WITH_ABSTRACT_METHODS_2 =
      "package org.foo;\n" +
          "\n" +
          "import java.util.Map;\n" +
          "\n" +
          "public abstract class Foo {\n" +
          "  public Foo() {\n" +
          "\n" +
          "  }\n" +
          "\n" +
          "  public abstract String someString();\n" +
          "  public abstract Integer someInteger(final long aLong);\n" +
          "  public abstract void foo(String a0, Integer a1) throws Throwable;\n" +
          "  protected abstract void bar(Long a0, Double a1) throws UnsupportedOperationException;\n" +
          "  protected abstract Long funTimes(final String str);\n" +
          "  abstract void foobaz(Map a0) throws ClassNotFoundException;\n" +
          "  abstract Float boringTimes(byte[] byteArr);\n" +
          "  public void baz() {\n" +
          "\n" +
          "  }\n" +
          "}";

  public static final String CLASS_WITH_CONSTRUCTOR_CALLING_SUPER =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   public Foo() {" +
          "     super();" +
          "   }" +
          " }";

  public static final String CLASS_WITH_CONSTRUCTOR_CALLING_THIS =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   private boolean b;" +
          "   public Foo() {" +
          "     this(false);" +
          "   }" +
          "   public Foo(boolean b) {" +
          "     this.b = b;" +
          "   }" +
          " }";

  public static final String CLASS_WITH_METHOD_CALLING_METHOD_ON_THIS =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   public void bar() {" +
          "     foo();" +
          "   }" +
          "   public String foo() {" +
          "     return null;" +
          "   }" +
          " }";

  public static final String CLASS_WITH_METHOD_CALLING_METHOD_ON_SUPER =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   public void bar() {" +
          "     foo();" +
          "   }" +
          "   public String foo() {" +
          "     return super.toString();" +
          "   }" +
          " }";

  public static final String CLASS_WITH_METHOD_HAVING_THROWS_DECLARATION =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   public void initialize() throws Exception, IllegalArgumentException {" +
          "   }" +
          " }";

  public static final String CLASS_WITH_METHODS_OF_ALL_SCOPES =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   public void publicMethod() {" +
          "   }" +
          "   protected void protectedMethod() {" +
          "   }" +
          "   void packagePrivateMethod() {" +
          "   }" +
          "   private void privateMethod() {" +
          "   }" +
          " }";

  public static final String CLASS_WITH_FIELDS_OF_ALL_SCOPES =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   public int publicField;\n" +
          "   protected int protectedField;\n" +
          "   int packagePrivateField;\n" +
          "   private int privateField;\n" +
          " }";

  public static final String CLASS_WITH_CONSTRUCTORS_OF_ALL_SCOPES =
      "     package org.foo;\n" +
          "\n" +
          " public class Foo {\n" +
          "   public Foo() {" +
          "   }" +
          "   protected Foo() {" +
          "   }" +
          "   Foo() {" +
          "   }" +
          "   private Foo() {" +
          "   }" +
          " }";

  public static final String CLASS_WITH_STATIC_METHOD =
      "     package my.test;\n" +
          "\n" +
          " public class Clazz {\n" +
          "    public static void test() {\n" +
          "        System.out.println(\"Hello, World!\");\n" +
          "    }\n" +
          " }";

  public static final String CLASS_WITH_JSNI_METHOD =
      "     package my.test;\n" +
          "\n" +
          " public class Clazz {\n" +
          "    public native void test() /*-{\n" +
          "        System.out.println(\"Hello, World!\");\n" +
          "    }-*/;\n" +
          " }";

  public static final String CLASS_DEFINITION_BY_IMPLEMENTING_INTERFACE =
      "     package org.jboss.errai.codegen.test.model;\n" +
          "\n" +
          " public class BazImpl implements Baz {\n" +
          "     public void someMethod() {\n" +
          "     }\n" +
          " }";

  public static final String CLASS_WITH_COLLIDING_IMPORTS_WITH_INNER_CLASS =
      "     package my.test;" +
          "\n" +
          " import java.io.Serializable;\n" +
          " import org.jboss.errai.codegen.test.model.TestInterface;\n" +
          "\n" +
          " public class Clazz implements TestInterface, " +
          "   org.jboss.errai.codegen.test.ClassBuilderTest.TestInterface, Serializable {\n" +
          "   private String name;\n" +
          " }";

  public static final String CLASS_WITH_COLLIDING_IMPORTS_WITH_INNER_CLASS_FIRST =
      "     package my.test;" +
          "\n" +
          " import java.io.Serializable;\n" +
          " import org.jboss.errai.codegen.test.ClassBuilderTest.TestInterface;\n" +
          "\n" +
          " public class Clazz implements TestInterface, " +
          "   org.jboss.errai.codegen.test.model.TestInterface, Serializable {\n" +
          "   private String name;\n" +
          " }";

  public static final String CLASS_WITH_COLLIDING_IMPORTS_WITH_JAVA_LANG =
      "     package my.test;" +
          " import org.jboss.errai.codegen.test.model.Integer;\n" +
          "\n" +
          " public class Clazz {\n " +
          "   private Integer i;\n" +
          "   private java.lang.Integer j;\n" +
          " }";

  public static final String CLASS_WITH_COLLIDING_IMPORTS_WITH_JAVA_LANG_FIRST =
      "     package my.test;" +
          "\n" +
          " public class Clazz {\n " +
          "   private Integer i;\n" +
          "   private org.jboss.errai.codegen.test.model.Integer j;\n" +
          " }";

  public static final String CLASS_WITH_CLASS_COMMENT =
      "     package org.foo;" +
          "" +
          " // A foo-ish bar" +
          " public class Bar {}";
}
