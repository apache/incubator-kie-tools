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
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.junit.Test;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Tests the generation of annotations.
 *
 * @author Johannes Barop <jb@barop.de>
 */
public class AnnotationTest extends AbstractCodegenTest {

  @Test
  public void testSingleBoolean() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.BooleanAnnotation;\n" +
            "@BooleanAnnotation(arrayValue = false, value = false)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructBooleanAnnotation(false));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiBoolean() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.BooleanAnnotation;\n" +
            "@BooleanAnnotation(arrayValue = {true, false, true}, value = true)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructBooleanAnnotation(true, false, true));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleByte() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.ByteAnnotation;\n" +
            "@ByteAnnotation(arrayValue = 1, value = 1)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructByteAnnotation(new byte[]{1}));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiByte() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.ByteAnnotation;\n" +
            "@ByteAnnotation(arrayValue = {1, 2, 3}, value = 1)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructByteAnnotation(new byte[]{1, 2, 3}));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleChar() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.CharAnnotation;\n" +
            "@CharAnnotation(arrayValue = 'a', value = 'a')\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructCharAnnotation('a'));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiChar() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.CharAnnotation;\n" +
            "@CharAnnotation(arrayValue = {'a', 'b', 'c'}, value = 'a')\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructCharAnnotation('a', 'b', 'c'));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleShort() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.ShortAnnotation;\n" +
            "@ShortAnnotation(arrayValue = 1, value = 1)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructShortAnnotation(new short[]{1}));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiShort() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.ShortAnnotation;\n" +
            "@ShortAnnotation(arrayValue = {1, 2, 3}, value = 1)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructShortAnnotation(new short[]{1, 2, 3}));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleInt() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.IntAnnotation;\n" +
            "@IntAnnotation(arrayValue = 1, value = 1)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructIntAnnotation(1));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiInt() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.IntAnnotation;\n" +
            "@IntAnnotation(arrayValue = {1, 2, 3}, value = 1)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructIntAnnotation(1, 2, 3));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleLong() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.LongAnnotation;\n" +
            "@LongAnnotation(arrayValue = 1L, value = 1L)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructLongAnnotation(1l));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiLong() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.LongAnnotation;\n" +
            "@LongAnnotation(arrayValue = {1L, 2L, 3L}, value = 1L)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructLongAnnotation(1l, 2l, 3l));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleFloat() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.FloatAnnotation;\n" +
            "@FloatAnnotation(arrayValue = 1.1f, value = 1.1f)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructFloatAnnotation(1.1f));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiFloat() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.FloatAnnotation;\n" +
            "@FloatAnnotation(arrayValue = {1.1f, 2.22f, 3.333f}, value = 1.1f)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructFloatAnnotation(1.1f, 2.22f, 3.333f));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleDouble() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.DoubleAnnotation;\n" +
            "@DoubleAnnotation(arrayValue = 1.1d, value = 1.1d)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructDoubleAnnotation(1.1));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiDouble() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.DoubleAnnotation;\n" +
            "@DoubleAnnotation(arrayValue = {1.1d, 2.22d, 3.333d}, value = 1.1d)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructDoubleAnnotation(1.1, 2.22, 3.333));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleString() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.StringAnnotation;\n" +
            "@StringAnnotation(arrayValue = \"Hello World\", value = \"Hello World\")\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructStringAnnotation("Hello World"));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiString() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.StringAnnotation;\n" +
            "@StringAnnotation(arrayValue = {\"Hang\", \"to\", \"your\", \"helmet\"}, value = \"Hang\")\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructStringAnnotation("Hang", "to", "your", "helmet"));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleClass() {
    String expected = "" +
            "import java.util.Set;\n" +
            "import org.jboss.errai.codegen.test.AnnotationTest.ClassAnnotation;\n" +
            "@ClassAnnotation(arrayValue = Set.class, value = Set.class)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructClassAnnotation(Set.class));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiClass() {
    String expected = "" +
            "import java.util.List;\n" +
            "import java.util.Set;\n" +
            "import org.jboss.errai.codegen.test.AnnotationTest.ClassAnnotation;\n" +
            "@ClassAnnotation(arrayValue = {Set.class, List.class}, value = Set.class)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructClassAnnotation(Set.class, List.class));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleEnum() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.EnumAnnotation;\n" +
            "import org.jboss.errai.codegen.test.AnnotationTest.SimpleEnum;\n" +
            "@EnumAnnotation(arrayValue = SimpleEnum.YES, value = SimpleEnum.YES)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructEnumAnnotation(SimpleEnum.YES));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiEnum() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.EnumAnnotation;\n" +
            "import org.jboss.errai.codegen.test.AnnotationTest.SimpleEnum;\n" +
            "@EnumAnnotation(arrayValue = {SimpleEnum.YES, SimpleEnum.NO, SimpleEnum.YES}, value = SimpleEnum.YES)\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructEnumAnnotation(SimpleEnum.YES, SimpleEnum.NO, SimpleEnum.YES));
    assertEquals(expected, cls);
  }

  @Test
  public void testSingleAnnotation() {
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.AnnotationAnnotation;\n" +
            "import org.jboss.errai.codegen.test.AnnotationTest.BooleanAnnotation;\n" +
            "@AnnotationAnnotation(arrayValue = \n" +
            "               @BooleanAnnotation(arrayValue = {true, false}, value = true),\n" +
            "       value = @BooleanAnnotation(arrayValue = {true, false}, value = true))\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructAnnotationAnnotation(constructBooleanAnnotation(true, false)));
    assertEquals(expected, cls);
  }

  @Test
  public void testMultiAnnotation() {
    // XXX:
    // FIXME: The PrettyPrinter output in nested annotations is different
    String expected = "" +
            "import org.jboss.errai.codegen.test.AnnotationTest.AnnotationAnnotation;\n" +
            "import org.jboss.errai.codegen.test.AnnotationTest.BooleanAnnotation;\n" +
            "@AnnotationAnnotation(arrayValue = {\n" +
            "               @BooleanAnnotation(arrayValue = { true, false }, value = true),\n" + /* XXX */
            "               @BooleanAnnotation(arrayValue = { true, true}, value = true)},\n" + /* XXX */
            "       value = @BooleanAnnotation(arrayValue = {true, false}, value = true))\n" +
            "public class FooBar {}";
    String cls = constructTypeWithAnnotation(constructAnnotationAnnotation(constructBooleanAnnotation(true, false), constructBooleanAnnotation(true, true)));
    assertEquals(expected, cls);
  }

  private static String constructTypeWithAnnotation(Annotation... annotations) {
    ClassStructureBuilder<?> builder = ClassBuilder.define("FooBar").publicScope().body();
    for (Annotation annotation : annotations) {
      builder.getClassDefinition().addAnnotation(annotation);
    }
    return builder.toJavaString();
  }

  private enum SimpleEnum {
    YES, NO
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface BooleanAnnotation {
    boolean value();

    boolean[] arrayValue();
  }

  private static BooleanAnnotation constructBooleanAnnotation(final boolean... values) {
    return new BooleanAnnotation() {
      @Override
      public boolean value() {
        return values[0];
      }

      @Override
      public boolean[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return BooleanAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface ByteAnnotation {
    byte value();

    byte[] arrayValue();
  }

  private static ByteAnnotation constructByteAnnotation(final byte... values) {
    return new ByteAnnotation() {
      @Override
      public byte value() {
        return values[0];
      }

      @Override
      public byte[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return ByteAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface CharAnnotation {
    char value();

    char[] arrayValue();
  }

  private static CharAnnotation constructCharAnnotation(final char... values) {
    return new CharAnnotation() {
      @Override
      public char value() {
        return values[0];
      }

      @Override
      public char[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return CharAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface ShortAnnotation {
    short value();

    short[] arrayValue();
  }

  private static ShortAnnotation constructShortAnnotation(final short... values) {
    return new ShortAnnotation() {
      @Override
      public short value() {
        return values[0];
      }

      @Override
      public short[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return ShortAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface IntAnnotation {
    int value();

    int[] arrayValue();
  }

  private static IntAnnotation constructIntAnnotation(final int... values) {
    return new IntAnnotation() {
      @Override
      public int value() {
        return values[0];
      }

      @Override
      public int[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return IntAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface LongAnnotation {
    long value();

    long[] arrayValue();
  }

  private static LongAnnotation constructLongAnnotation(final long... values) {
    return new LongAnnotation() {
      @Override
      public long value() {
        return values[0];
      }

      @Override
      public long[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return LongAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface FloatAnnotation {
    float value();

    float[] arrayValue();
  }

  private static FloatAnnotation constructFloatAnnotation(final float... values) {
    return new FloatAnnotation() {
      @Override
      public float value() {
        return values[0];
      }

      @Override
      public float[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return FloatAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface DoubleAnnotation {
    double value();

    double[] arrayValue();
  }

  private static DoubleAnnotation constructDoubleAnnotation(final double... values) {
    return new DoubleAnnotation() {
      @Override
      public double value() {
        return values[0];
      }

      @Override
      public double[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return DoubleAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface StringAnnotation {
    String value();

    String[] arrayValue();
  }

  private static StringAnnotation constructStringAnnotation(final String... values) {
    return new StringAnnotation() {
      @Override
      public String value() {
        return values[0];
      }

      @Override
      public String[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return StringAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface ClassAnnotation {
    Class<?> value();

    Class<?>[] arrayValue();
  }

  private static ClassAnnotation constructClassAnnotation(final Class<?>... values) {
    return new ClassAnnotation() {
      @Override
      public Class<?> value() {
        return values[0];
      }

      @Override
      public Class<?>[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return ClassAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface EnumAnnotation {
    SimpleEnum value();

    SimpleEnum[] arrayValue();
  }

  private static EnumAnnotation constructEnumAnnotation(final SimpleEnum... values) {
    return new EnumAnnotation() {
      @Override
      public SimpleEnum value() {
        return values[0];
      }

      @Override
      public SimpleEnum[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return EnumAnnotation.class;
      }
    };
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  private @interface AnnotationAnnotation {
    BooleanAnnotation value();

    BooleanAnnotation[] arrayValue();
  }

  private static AnnotationAnnotation constructAnnotationAnnotation(final BooleanAnnotation... values) {
    return new AnnotationAnnotation() {
      @Override
      public BooleanAnnotation value() {
        return values[0];
      }

      @Override
      public BooleanAnnotation[] arrayValue() {
        return values;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return AnnotationAnnotation.class;
      }
    };
  }

}
