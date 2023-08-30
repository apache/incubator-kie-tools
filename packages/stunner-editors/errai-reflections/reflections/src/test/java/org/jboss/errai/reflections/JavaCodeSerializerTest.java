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


package org.jboss.errai.reflections;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.base.Predicate;
import org.jboss.errai.reflections.scanners.TypeElementsScanner;
import org.jboss.errai.reflections.scanners.TypesScanner;
import org.jboss.errai.reflections.serializers.JavaCodeSerializer;
import org.jboss.errai.reflections.util.ClasspathHelper;
import org.jboss.errai.reflections.util.ConfigurationBuilder;
import org.jboss.errai.reflections.util.FilterBuilder;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;

/** */
public class JavaCodeSerializerTest {

  @Test
  public void generateAndSave() {
    //generate
    Predicate<String> filter = new FilterBuilder().include("org.jboss.errai.reflections.TestModel\\$.*").include("org.jboss.errai.reflections.MyTestModelStore.*");

    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .filterInputsBy(filter)
        .setScanners(new TypesScanner(), new TypeElementsScanner())
        .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

    reflections.scan();

    //no re-serializing
    Set<String> stringSet = reflections.getStore().get(TypesScanner.class).keySet();
    for (String s : stringSet) {
      if (stringSet.contains("MyTestModelStore")) {
        Assert.fail(s + " has been re-serialized");
      }
    }

    //save
    String filename = ReflectionsTest.getUserDir() + "/src/test/java/org.jboss.errai.reflections.MyTestModelStore";
    reflections.save(filename, new JavaCodeSerializer());
  }

  @Test
  public void classCanBeFetchedFromJavaCodeSerializer() {
    //class
    Class<? extends JavaCodeSerializer.IClass> aClass1 = MyTestModelStore.org.jboss.errai.reflections.TestModel$C1.class;
    Class aClass = JavaCodeSerializer.resolveClass(aClass1);
    Assert.assertEquals(TestModel.C1.class, aClass);
  }

  @Test
  public void methodCanBeFetchedFromJavaCodeSerializer() throws NoSuchMethodException {
    //method
    Class<? extends JavaCodeSerializer.IMethod> method1 = MyTestModelStore.org.jboss.errai.reflections.TestModel$C4.m1.class;
    Method m1 = JavaCodeSerializer.resolveMethod(method1);
    Assert.assertEquals(TestModel.C4.class.getDeclaredMethod("m1"), m1);

    //overloaded method with parameters
    Class<? extends JavaCodeSerializer.IMethod> method2 = MyTestModelStore.org.jboss.errai.reflections.TestModel$C4.m1_int_java$lang$String$$.class;
    Method m2 = JavaCodeSerializer.resolveMethod(method2);
    Assert.assertEquals(TestModel.C4.class.getDeclaredMethod("m1", int.class, String[].class), m2);

    //overloaded method with parameters and multi dimensional array
    Class<? extends JavaCodeSerializer.IMethod> method3 = MyTestModelStore.org.jboss.errai.reflections.TestModel$C4.m1_int$$$$_java$lang$String$$$$.class;
    Method m3 = JavaCodeSerializer.resolveMethod(method3);
    Assert.assertEquals(TestModel.C4.class.getDeclaredMethod("m1", int[][].class, String[][].class), m3);
  }

  @Test
  public void fieldCanBeFetchedFromJavaCodeSerializer() throws NoSuchMethodException, NoSuchFieldException {
    //field
    Class<? extends JavaCodeSerializer.IField> field1 = MyTestModelStore.org.jboss.errai.reflections.TestModel$C4.f1.class;
    Field f1 = JavaCodeSerializer.resolveField(field1);
    Assert.assertEquals(TestModel.C4.class.getDeclaredField("f1"), f1);
  }

  @Test
  public void fieldCanBeFetchedFromAnnotationUsingJavaCodeSerializer() throws NoSuchFieldException {
    //field can be fetched from annotation
    Mark mark = SomeClassToMark.class.getAnnotation(Mark.class);
    Class<? extends JavaCodeSerializer.IField> markedElement = mark.value();
    Field markedField = JavaCodeSerializer.resolveField(markedElement);
    Assert.assertEquals(TestModel.C4.class.getDeclaredField("f1"), markedField);
  }

  //test case - use the store in annotations
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Mark {
    Class<? extends JavaCodeSerializer.IField> value();
  }

  @Mark(MyTestModelStore.org.jboss.errai.reflections.TestModel$C4.f1.class)
  //annotation can use Store to specify class/field/method in a semi strong typed manner
  @SuppressWarnings({"UnusedDeclaration"})
  public static class SomeClassToMark {

  }
}
