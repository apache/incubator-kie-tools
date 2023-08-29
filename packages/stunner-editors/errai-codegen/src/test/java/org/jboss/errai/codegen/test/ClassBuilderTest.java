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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.exception.UndefinedMethodException;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.test.model.Baz;
import org.jboss.errai.codegen.test.model.tree.Parent;
import org.jboss.errai.codegen.util.Stmt;
import org.junit.Test;

import static org.jboss.errai.codegen.test.ClassBuilderTestResult.ABSTRACT_CLASS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.ABSTRACT_CLASS_WITH_ABSTRACT_METHODS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.ABSTRACT_CLASS_WITH_ABSTRACT_METHODS_2;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_DECLARING_INNER_CLASS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_DEFINITION_BY_IMPLEMENTING_INTERFACE;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_IMPLEMENTING_INTERFACE;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_IMPLEMENTING_MULTIPLE_INTERFACES;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_ACCESSOR_METHODS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_CLASS_COMMENT;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_COLLIDING_IMPORTS_WITH_INNER_CLASS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_COLLIDING_IMPORTS_WITH_INNER_CLASS_FIRST;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_COLLIDING_IMPORTS_WITH_JAVA_LANG;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_COLLIDING_IMPORTS_WITH_JAVA_LANG_FIRST;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_CONSTRUCTORS_OF_ALL_SCOPES;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_CONSTRUCTOR_CALLING_SUPER;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_CONSTRUCTOR_CALLING_THIS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_FIELDS_OF_ALL_SCOPES;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_FIELD_INHERITANCE;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_JSNI_METHOD;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_METHODS_OF_ALL_SCOPES;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_METHOD_CALLING_METHOD_ON_SUPER;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_METHOD_CALLING_METHOD_ON_THIS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_METHOD_HAVING_THROWS_DECLARATION;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_METHOD_USING_INNER_CLASS;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_PARENT;
import static org.jboss.errai.codegen.test.ClassBuilderTestResult.CLASS_WITH_STATIC_METHOD;
import static org.junit.Assert.fail;

/**
 * Tests the {@link ClassBuilder} API.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ClassBuilderTest extends AbstractCodegenTest {

  @Test
  public void testDefineClassImplementingInterface() {
    final String cls = ClassBuilder.define("org.foo.Bar")
        .publicScope()
        .implementsInterface(Serializable.class)
        .body()
        .privateField("name", String.class)
        .finish()
        .toJavaString();
    System.out.println(cls);

    assertEquals("failed to generate class definition implementing an interface", CLASS_IMPLEMENTING_INTERFACE, cls);
  }

  @Test
  public void testDefineClassImplementingMultipleInterfaces() {
    final String cls = ClassBuilder.define("org.foo.Bar")
        .publicScope()
        .implementsInterface(Serializable.class)
        .implementsInterface(Cloneable.class)
        .body()
        .privateField("name", String.class)
        .finish()
        .toJavaString();

    assertEquals("failed to generate class definition implementing multiple interfaces",
        CLASS_IMPLEMENTING_MULTIPLE_INTERFACES, cls);
  }

  @Test
  public void testDefineClassWithInnerClass() {
    final ClassStructureBuilder<?> innerClass = ClassBuilder.define("Inner")
        .publicScope().body();

    final String cls = ClassBuilder.define("foo.bar.Baz")
        .publicScope()
        .body()
        .declaresInnerClass(new InnerClass(innerClass.getClassDefinition()))
        .toJavaString();

    assertEquals("failed to generate class with method using inner class",
        CLASS_DECLARING_INNER_CLASS, cls);
  }

  @Test
  public void testDefineInnerClassInMethod() {
    final ClassStructureBuilder<?> innerClass = ClassBuilder.define("Inner")
        .packageScope()
        .implementsInterface(Serializable.class)
        .body()
        .privateField("name", String.class)
        .finish()
        .publicMethod(void.class, "setName", Parameter.of(String.class, "name", true))
        .append(Stmt.loadClassMember("name").assignValue(Variable.get("name")))
        .finish();

    final String cls = ClassBuilder.define("foo.bar.Baz")
        .publicScope()
        .body()
        .publicMethod(void.class, "someMethod")
        .append(new InnerClass(innerClass.getClassDefinition()))
        .append(Stmt.newObject(innerClass.getClassDefinition()))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with method using inner class",
        CLASS_WITH_METHOD_USING_INNER_CLASS, cls);
  }

  @Test
  public void testDefineClassWithAccessorMethods() {
    final String cls = ClassBuilder.define("org.foo.Foo")
        .publicScope()
        .body()
        .privateField("name", String.class)
        .initializesWith(Stmt.load("default"))
        .finish()
        .publicMethod(String.class, "getName")
        .append(Stmt.loadVariable("name").returnValue())
        .finish()
        .publicMethod(void.class, "setName", Parameter.of(String.class, "name"))
        .append(Stmt.loadClassMember("name").assignValue(Variable.get("name")))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class definition with accessor methods", CLASS_WITH_ACCESSOR_METHODS, cls);
  }

  @Test
  public void testDefineClassWithAccessorMethodsUsingThisKeyword() {
    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .body()
        .privateField("name", String.class)
        .initializesWith(Stmt.load("default"))
        .finish()
        .publicMethod(String.class, "getName")
        .append(Stmt.loadVariable("name").returnValue())
        .finish()
        .publicMethod(void.class, "setName", Parameter.of(String.class, "name"))
        .append(Stmt.loadVariable("this.name").assignValue(Variable.get("name")))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class definition with accessor methods", CLASS_WITH_ACCESSOR_METHODS, cls);
  }

  @Test
  public void testDefineClassWithParent() {
    final String cls = ClassBuilder
        .define("org.foo.Foo", String.class)
        .publicScope()
        .body()
        .publicConstructor(Parameter.of(int.class, "i"))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with parent", CLASS_WITH_PARENT, cls);
  }

  @Test
  public void testDefineClassWithFieldInheritance() {
    final String cls = ClassBuilder
        .define("org.foo.Foo", Parent.class)
        .publicScope()
        .body()
        .publicConstructor()
        .append(Stmt.loadVariable("parentProtected").assignValue(0))
        .append(Stmt.loadVariable("parentPublic").assignValue(0))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with parent", CLASS_WITH_FIELD_INHERITANCE, cls);
  }

  @Test
  public void testDefineAbstractClass() {

    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .abstractClass()
        .body()
        .publicConstructor()
        .finish()
        .toJavaString();

    assertEquals("failed to generate abstract class", ABSTRACT_CLASS, cls);
  }

  @Test
  public void testDefineAbstractClassWithAbstractMethods() {

    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .abstractClass()
        .body()
        .publicConstructor()
        .finish()
        .publicAbstractMethod(void.class, "foo")
        .finish()
        .protectedAbstractMethod(void.class, "bar")
        .finish()
        .publicMethod(void.class, "baz")
        .finish()
        .toJavaString();

    assertEquals("failed to generate abstract class with abstract method", ABSTRACT_CLASS_WITH_ABSTRACT_METHODS, cls);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testDefineAbstractClassWithAbstractMethods2() {

    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .abstractClass()
        .body()
        .publicConstructor()
        .finish()
        .publicAbstractMethod(MetaClassFactory.get(String.class), "someString").finish()
        .publicAbstractMethod(Integer.class, "someInteger", Parameter.finalOf(long.class, "aLong")).finish()
        .publicAbstractMethod(void.class, "foo", String.class, Integer.class).throws_(Throwable.class)
        .protectedAbstractMethod(void.class, "bar", Long.class, Double.class).throws_(UnsupportedOperationException.class)
        .protectedAbstractMethod(Long.class, "funTimes", Parameter.finalOf(String.class, "str")).finish()
        .packageAbstractMethod(void.class, "foobaz", Map.class) .throws_(MetaClassFactory.get(ClassNotFoundException.class))
        .packageAbstractMethod(Float.class, "boringTimes", Parameter.of(byte[].class, "byteArr")).finish()
        .publicMethod(void.class, "baz")
        .finish()
        .toJavaString();

    assertEquals(ABSTRACT_CLASS_WITH_ABSTRACT_METHODS_2, cls);
  }

  @Test
  public void testDefineClassWithConstructorCallingSuper() {
    final String cls = ClassBuilder.define("org.foo.Foo")
        .publicScope()
        .body()
        .publicConstructor()
        .callSuper()
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with constructor calling super()",
        CLASS_WITH_CONSTRUCTOR_CALLING_SUPER, cls);
  }

  @Test
  public void testDefineClassWithConstructorCallingThis() {
    final String cls = ClassBuilder.define("org.foo.Foo")
        .publicScope()
        .body()
        .privateField("b", boolean.class)
        .finish()
        .publicConstructor()
        .callThis(false)
        .finish()
        .publicConstructor(Parameter.of(boolean.class, "b"))
        .append(Stmt.loadClassMember("b").assignValue(Variable.get("b")))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with constructor calling this()",
        CLASS_WITH_CONSTRUCTOR_CALLING_THIS, cls);
  }

  @Test
  public void testDefineClassWithMethodCallingMethodOnThis() {
    final String cls = ClassBuilder.define("org.foo.Foo")
        .publicScope()
        .body()
        .publicMethod(void.class, "bar")
        .append(Stmt.loadVariable("this").invoke("foo"))
        .finish()
        .publicMethod(String.class, "foo")
        .append(Stmt.load(null).returnValue())
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with method calling method on this",
        CLASS_WITH_METHOD_CALLING_METHOD_ON_THIS, cls);
  }

  @Test
  public void testDefineClassWithMethodCallingInvalidMethodOnThis() {
    try {
      ClassBuilder.define("org.foo.Foo")
          .publicScope()
          .body()
          .publicMethod(void.class, "bar")
          .append(Stmt.loadVariable("this").invoke("foo", "invalidParam"))
          .finish()
          .publicMethod(String.class, "foo")
          .append(Stmt.load(null).returnValue())
          .finish()
          .toJavaString();
      fail("exprected UndefinedMethodException");
    }
    catch (UndefinedMethodException udme) {
      // expected
      assertEquals("Wrong exception thrown", udme.getMethodName(), "foo");
    }
  }

  @Test
  public void testDefineClassWithMethodCallingMethodOnSuper() {
    final String cls = ClassBuilder.define("org.foo.Foo")
        .publicScope()
        .body()
        .publicMethod(void.class, "bar")
        .append(Stmt.loadVariable("this").invoke("foo"))
        .finish()
        .publicMethod(String.class, "foo")
        .append(Stmt.loadVariable("super").invoke("toString").returnValue())
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with method calling method on this",
        CLASS_WITH_METHOD_CALLING_METHOD_ON_SUPER, cls);
  }

  @Test
  public void testDefineClassWithMethodCallingInvalidMethodOnSuper() {
    try {
      ClassBuilder.define("org.foo.Foo")
          .publicScope()
          .body()
          .publicMethod(void.class, "bar")
          .append(Stmt.loadVariable("this").invoke("foo"))
          .finish()
          .publicMethod(String.class, "foo")
          .append(Stmt.loadVariable("super").invoke("undefinedMethod"))
          .finish()
          .toJavaString();
      fail("exprected UndefinedMethodException");
    }
    catch (UndefinedMethodException udme) {
      // expected
      assertEquals("Wrong exception thrown", udme.getMethodName(), "undefinedMethod");
    }
  }

  @Test
  public void testDefineClassWithMethodHavingThrowsDeclaration() {
    @SuppressWarnings("unchecked")
    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .body()
        .publicMethod(void.class, "initialize")
        .throws_(Exception.class, IllegalArgumentException.class)
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with method having throws declaration",
        CLASS_WITH_METHOD_HAVING_THROWS_DECLARATION, cls);
  }

  @Test
  public void testDefineClassWithMethodsOfAllScopes() {
    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .body()
        .publicMethod(void.class, "publicMethod")
        .finish()
        .protectedMethod(void.class, "protectedMethod")
        .finish()
        .packageMethod(void.class, "packagePrivateMethod")
        .finish()
        .privateMethod(void.class, "privateMethod")
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with methods of all scopes", CLASS_WITH_METHODS_OF_ALL_SCOPES, cls);
  }

  @Test
  public void testDefineClassWithFieldsOfAllScopes() {
    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .body()
        .publicField("publicField", int.class)
        .finish()
        .protectedField("protectedField", int.class)
        .finish()
        .packageField("packagePrivateField", int.class)
        .finish()
        .privateField("privateField", int.class)
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with fields of all scopes", CLASS_WITH_FIELDS_OF_ALL_SCOPES, cls);
  }

  @Test
  public void testDefineClassWithConstructorsOfAllScopes() {
    final String cls = ClassBuilder
        .define("org.foo.Foo")
        .publicScope()
        .body()
        .publicConstructor()
        .finish()
        .protectedConstructor()
        .finish()
        .packageConstructor()
        .finish()
        .privateConstructor()
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with constructors of all scopes",
        CLASS_WITH_CONSTRUCTORS_OF_ALL_SCOPES, cls);
  }

  @Test
  public void testDefineClassByImplementingInterface() {
    final String cls = ClassBuilder.implement(Baz.class)
        .publicMethod(void.class, "someMethod")
        .finish().toJavaString();

    assertEquals("failed to generate class by implementing an interface",
        CLASS_DEFINITION_BY_IMPLEMENTING_INTERFACE, cls);
  }

  @Test
  public void testDefineClassWithStaticMethod() {
    final String cls = ClassBuilder.define("my.test.Clazz")
        .publicScope().body()
        .publicMethod(void.class, "test").modifiers(Modifier.Static)
        .body()
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "Hello, World!"))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with static method", CLASS_WITH_STATIC_METHOD, cls);
  }

  @Test
  public void testDefineClassWithJSNIMethod() {
    final String cls = ClassBuilder.define("my.test.Clazz")
        .publicScope().body()
        .publicMethod(void.class, "test").modifiers(Modifier.JSNI)
        .body()
        .append(Stmt.loadStatic(System.class, "out").invoke("println", "Hello, World!"))
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with JSNI method", CLASS_WITH_JSNI_METHOD, cls);
  }

  public interface TestInterface {

  }

  @Test
  public void testCollidingImportsWithInnerClass() {
    final String cls = ClassBuilder.define("my.test.Clazz")
        .publicScope()
        .implementsInterface(org.jboss.errai.codegen.test.model.TestInterface.class)
        .implementsInterface(TestInterface.class)
        .implementsInterface(Serializable.class)
        .body()
        .privateField("name", String.class)
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with colliding imports",
        CLASS_WITH_COLLIDING_IMPORTS_WITH_INNER_CLASS, cls);
  }

  @Test
  public void testCollidingImportsWithInnerClassFirst() {
    final String cls = ClassBuilder.define("my.test.Clazz")
        .publicScope()
        .implementsInterface(TestInterface.class)
        .implementsInterface(org.jboss.errai.codegen.test.model.TestInterface.class)
        .implementsInterface(Serializable.class)
        .body()
        .privateField("name", String.class)
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with colliding imports",
        CLASS_WITH_COLLIDING_IMPORTS_WITH_INNER_CLASS_FIRST, cls);
  }

  @Test
  public void testCollidingImportsWithJavaLang() {
    final String cls = ClassBuilder.define("my.test.Clazz")
        .publicScope()
        .body()
        .privateField("i", org.jboss.errai.codegen.test.model.Integer.class)
        .finish()
        .privateField("j", Integer.class)
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with colliding imports",
        CLASS_WITH_COLLIDING_IMPORTS_WITH_JAVA_LANG, cls);
  }

  @Test
  public void testCollidingImportsWithJavaLangFirst() {
    final String cls = ClassBuilder.define("my.test.Clazz")
        .publicScope()
        .body()
        .privateField("i", Integer.class)
        .finish()
        .privateField("j", org.jboss.errai.codegen.test.model.Integer.class)
        .finish()
        .toJavaString();

    assertEquals("failed to generate class with colliding imports",
        CLASS_WITH_COLLIDING_IMPORTS_WITH_JAVA_LANG_FIRST, cls);
  }

  @Test
  public void testThisReferenceWithStmtLoadVariable() {
    final ClassStructureBuilder<? extends ClassStructureBuilder<?>> body =
        ClassBuilder.define("org.foo.Foo").publicScope().body();

    final String cls = body
        .publicMethod(body.getClassDefinition(), "getThis")
        .append(Stmt.loadVariable("this").returnValue())
        .finish()
        .toJavaString();

    assertEquals("did not properly render 'this' reference", "package org.foo;\n" +
        "\n" +
        "\n" +
        "public class Foo {\n" +
        "  public Foo getThis() {\n" +
        "    return this;\n" +
        "  }\n" +
        "}", cls);
  }

  @Test
  public void testMethodAnnotated() {

    final String cls = ClassBuilder.define("MyRunnable")
        .publicScope().implementsInterface(Runnable.class)
        .body()
        .publicMethod(void.class, "run")
        .annotatedWith(new SuppressWarnings() {
          @Override
          public String[] value() {
            return new String[]{"blah"};
          }

          @Override
          public Class<? extends Annotation> annotationType() {
            return SuppressWarnings.class;
          }
        }).body()
        .append(Stmt.returnVoid())
        .finish().toJavaString();

    assertEquals("public class MyRunnable implements Runnable {\n" +
        "  @SuppressWarnings(\"blah\") public void run() {\n" +
        "    return;\n" +
        "  }\n" +
        "}", cls);
  }

  @Test
  public void testClassComment() throws Exception {
    final String cls = ClassBuilder.define("org.foo.Bar")
        .classComment("A foo-ish bar")
        .publicScope()
        .body()
        .toJavaString();

    assertEquals(CLASS_WITH_CLASS_COMMENT, cls);
  }
}
