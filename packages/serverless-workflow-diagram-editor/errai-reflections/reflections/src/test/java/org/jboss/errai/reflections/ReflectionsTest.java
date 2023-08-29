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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jboss.errai.reflections.scanners.ConvertersScanner;
import org.jboss.errai.reflections.scanners.FieldAnnotationsScanner;
import org.jboss.errai.reflections.scanners.MethodAnnotationsScanner;
import org.jboss.errai.reflections.scanners.ResourcesScanner;
import org.jboss.errai.reflections.scanners.SubTypesScanner;
import org.jboss.errai.reflections.scanners.TypeAnnotationsScanner;
import org.jboss.errai.reflections.util.ClasspathHelper;
import org.jboss.errai.reflections.util.ConfigurationBuilder;
import org.jboss.errai.reflections.util.FilterBuilder;
import org.jboss.errai.reflections.vfs.Vfs;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.jboss.errai.reflections.TestModel.AC1;
import static org.jboss.errai.reflections.TestModel.AC2;
import static org.jboss.errai.reflections.TestModel.AF1;
import static org.jboss.errai.reflections.TestModel.AI1;
import static org.jboss.errai.reflections.TestModel.AI2;
import static org.jboss.errai.reflections.TestModel.AM1;
import static org.jboss.errai.reflections.TestModel.C1;
import static org.jboss.errai.reflections.TestModel.C2;
import static org.jboss.errai.reflections.TestModel.C3;
import static org.jboss.errai.reflections.TestModel.C4;
import static org.jboss.errai.reflections.TestModel.C5;
import static org.jboss.errai.reflections.TestModel.C6;
import static org.jboss.errai.reflections.TestModel.I1;
import static org.jboss.errai.reflections.TestModel.I2;
import static org.jboss.errai.reflections.TestModel.I3;
import static org.jboss.errai.reflections.TestModel.MAI1;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 */
public class ReflectionsTest {
  static Reflections reflections;
  //todo add tests for annotations on constructors
  //todo add tests for package annotations

  @BeforeClass
  public static void init() {
    Predicate<String> filter = new FilterBuilder().include("org.jboss.errai.reflections.TestModel\\$.*");
    reflections = new Reflections(new ConfigurationBuilder()
        .filterInputsBy(filter)
        .setScanners(
            new SubTypesScanner().filterResultsBy(filter),
            new TypeAnnotationsScanner().filterResultsBy(filter),
            new FieldAnnotationsScanner().filterResultsBy(filter),
            new MethodAnnotationsScanner().filterResultsBy(filter),
            new ConvertersScanner().filterResultsBy(filter))
        .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

    reflections.scan();
  }

  @Test
  public void testAll() {
    testSubTypesOf();
    testTypesAnnotatedWith();
    testMethodsAnnotatedWith();
    testFieldsAnnotatedWith();
    testConverters();
  }

  @Test
  public void testSubTypesOf() {
    assertThat(reflections.getSubTypesOf(I1.class), are(I2.class, C1.class, C2.class, C3.class, C5.class));
    assertThat(reflections.getSubTypesOf(I2.class), are(C1.class, C2.class, C3.class, C5.class));
  }

  @Test
  public void testTypesAnnotatedWith() {
    //@Inherited
    assertThat("when honoring @Inherited, meta-annotation should only effect annotated super classes and it's sub types",
        reflections.getTypesAnnotatedWith(MAI1.class), are(AI1.class));

    assertThat("when honoring @Inherited, meta-annotation should only effect annotated super classes and it's sub types",
        reflections.getTypesAnnotatedWith(AI2.class), are(I2.class));

    assertThat("when honoring @Inherited, meta-annotation should only effect annotated super classes and it's sub types",
        reflections.getTypesAnnotatedWith(AC1.class), are(C1.class, C2.class, C3.class, C5.class));

    assertThat("when not honoring @Inherited, meta annotation effects all subtypes, including annotations interfaces and classes",
        reflections.getTypesAnnotatedWith(AI1.class, false), are(I1.class, I2.class, C1.class, C2.class, C3.class, C5.class));

    assertThat("when not honoring @Inherited, meta annotation effects all subtypes, including annotations interfaces and classes",
        reflections.getTypesAnnotatedWith(AI2.class, false), are(I2.class, C1.class, C2.class, C3.class, C5.class));

    assertThat(reflections.getTypesAnnotatedWith(AM1.class), isEmpty);

    //annotation member value matching
    AC2 ac2 = new AC2() {
      public String value() {
        return "ugh?!";
      }

      public Class<? extends Annotation> annotationType() {
        return AC2.class;
      }
    };

    assertThat("when honoring @Inherited, meta-annotation should only effect annotated super classes and it's sub types",
        reflections.getTypesAnnotatedWith(ac2), are(C3.class, I3.class));

    assertThat("when not honoring @Inherited, meta annotation effects all subtypes, including annotations interfaces and classes",
        reflections.getTypesAnnotatedWith(ac2, false), are(C3.class, C5.class, I3.class, C6.class));
  }

  @Test
  public void testMethodsAnnotatedWith() {
    try {
      assertThat(reflections.getMethodsAnnotatedWith(AM1.class),
          are(C4.class.getDeclaredMethod("m1"),
              C4.class.getDeclaredMethod("m1", int.class, String[].class),
              C4.class.getDeclaredMethod("m1", int[][].class, String[][].class),
              C4.class.getDeclaredMethod("m3")));

      AM1 am1 = new AM1() {
        public String value() {
          return "1";
        }

        public Class<? extends Annotation> annotationType() {
          return AM1.class;
        }
      };
      assertThat(reflections.getMethodsAnnotatedWith(am1),
          are(C4.class.getDeclaredMethod("m1"),
              C4.class.getDeclaredMethod("m1", int.class, String[].class),
              C4.class.getDeclaredMethod("m1", int[][].class, String[][].class)));
    }
    catch (NoSuchMethodException e) {
      fail();
    }
  }

  @Test
  public void testFieldsAnnotatedWith() {
    try {
      assertThat(reflections.getFieldsAnnotatedWith(AF1.class),
          are(C4.class.getDeclaredField("f1"),
              C4.class.getDeclaredField("f2")
          ));

      assertThat(reflections.getFieldsAnnotatedWith(new AF1() {
        public String value() {
          return "2";
        }

        public Class<? extends Annotation> annotationType() {
          return AF1.class;
        }
      }),
          are(C4.class.getDeclaredField("f2")));
    }
    catch (NoSuchFieldException e) {
      fail();
    }
  }

  @Test
  public void testConverters() {
    try {
      assertThat(reflections.getConverters(C2.class, C3.class),
          are(C4.class.getDeclaredMethod("c2toC3", C2.class)));
    }
    catch (NoSuchMethodException e) {
      fail();
    }
  }

  @Test
  public void collect() {
    String path = getUserDir() + "/target/test-classes" + "/META-INF/reflections/testModel-reflections.xml";
    try {
      Predicate<String> filter = new FilterBuilder().include("org.jboss.errai.reflections.TestModel\\$.*");
      Reflections testModelReflections = new Reflections(new ConfigurationBuilder()
          .filterInputsBy(filter)
          .setScanners(
              new SubTypesScanner().filterResultsBy(filter),
              new TypeAnnotationsScanner().filterResultsBy(filter))
          .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));
  
      testModelReflections.scan();
      testModelReflections.save(path);
      reflections = Reflections.collect();
      reflections.scan();
      testAll();
    } 
    finally {
      new File(path).delete();
    }
  }

  @Test
  public void collectInputStream() {
    
    String path = getUserDir() + "/target/test-classes" + "/META-INF/reflections/testModel-reflections.xml";
    try {
      Predicate<String> filter = new FilterBuilder().include("org.jboss.errai.reflections.TestModel\\$.*");
      Reflections testModelReflections = new Reflections(new ConfigurationBuilder()
              .filterInputsBy(filter)
              .setScanners(new SubTypesScanner().filterResultsBy(filter),
                      new TypeAnnotationsScanner().filterResultsBy(filter))
              .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

      testModelReflections.scan();
      testModelReflections.save(path);

      final Iterable<Vfs.File> xmls = Vfs.findFiles(Arrays.asList(ClasspathHelper.forClass(ReflectionsTest.class)),
              new Predicate<Vfs.File>() {
                public boolean apply(Vfs.File input) {
                  return input.getName().endsWith(".xml");
                }
              });

      reflections = new Reflections(new ConfigurationBuilder());
      for (Vfs.File xml : xmls) {
        try {
          reflections.collect(xml.openInputStream());
          reflections.scan();
        } catch (IOException e) {
          throw new RuntimeException("", e);
        }
      }

      testAll();
    } finally {
      new File(path).delete();
    }
  }

//  @Test @Ignore
//  public void jsonCollect() {
//    Predicate<String> filter = new FilterBuilder().include("org.jboss.errai.reflections.TestModel\\$.*");
//    Reflections testModelReflections = new Reflections(new ConfigurationBuilder()
//        .filterInputsBy(filter)
//        .setScanners(
//            new SubTypesScanner().filterResultsBy(filter),
//            new TypeAnnotationsScanner().filterResultsBy(filter))
//        .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));
//
//    testModelReflections.scan();
//
//    String path = getUserDir() + "/target/test-classes" + "/META-INF/reflections/testModel-reflections.json";
//
//    final JsonSerializer serializer = new JsonSerializer();
//    testModelReflections.save(path, serializer);
//
//    reflections = new Reflections(new ConfigurationBuilder()).collect("META-INF/reflections",
//        new FilterBuilder().include(".*-reflections.json"),
//        serializer);
//
//    reflections.scan();
//
//
//    reflections.collect("META-INF/reflections",
//        new FilterBuilder().include(".*-reflections.xml").exclude("testModel-reflections.xml"),
//        new XmlSerializer());
//
//
//    testAll();
//  }

  public static String getUserDir() {
    File file = new File(System.getProperty("user.dir"));
    //a hack to fix user.dir issue(?) in surfire
    if (Lists.newArrayList(file.list()).contains("reflections")) {
      file = new File(file, "reflections");
    }
    return file.getAbsolutePath();
  }

  @Test
  public void testResourcesScanner() {
    Predicate<String> filter = new FilterBuilder().include(".*\\.xml");
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .filterInputsBy(filter)
        .setScanners(new ResourcesScanner())
        .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

    reflections.scan();

    Set<String> resolved = reflections.getResources(Pattern.compile(".*resource1-reflections\\.xml"));
    assertThat(resolved, are("META-INF/reflections/resource1-reflections.xml"));

    Set<String> resources = reflections.getStore().get(ResourcesScanner.class).keySet();
    assertThat(resources, are(
        "resource1-reflections.xml", "resource2-reflections.xml"));
  }

  //
  private final BaseMatcher<Set<Class<?>>> isEmpty = new BaseMatcher<Set<Class<?>>>() {
    public boolean matches(Object o) {
      return ((Collection<?>) o).isEmpty();
    }

    public void describeTo(Description description) {
      description.appendText("empty collection");
    }
  };

  public <T> Matcher<Set<? super T>> are(final T... ts) {
    final Collection<?> c1 = Arrays.asList(ts);
    return new BaseMatcher<Set<? super T>>() {
      public boolean matches(Object o) {
        Collection<?> c2 = (Collection<?>) o;
        return c1.containsAll(c2) && c2.containsAll(c1);
      }

      public void describeTo(Description description) {
        description.appendText("elements: ");
        description.appendValueList("(", ",", ")", ts);
      }
    };
  }
}
