/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.tests;

import junit.framework.Assert;
import org.jboss.errai.marshalling.client.MarshallingSessionProviderFactory;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.ParserFactory;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.jboss.errai.marshalling.tests.res.AImpl1;
import org.jboss.errai.marshalling.tests.res.AImpl2;
import org.jboss.errai.marshalling.tests.res.EntityWithInheritedTypeVariable;
import org.jboss.errai.marshalling.tests.res.EntityWithInterfaceArray;
import org.jboss.errai.marshalling.tests.res.EnumContainer;
import org.jboss.errai.marshalling.tests.res.EnumContainerContainer;
import org.jboss.errai.marshalling.tests.res.EnumTestA;
import org.jboss.errai.marshalling.tests.res.EnumWithAbstractMethod;
import org.jboss.errai.marshalling.tests.res.EnumWithState;
import org.jboss.errai.marshalling.tests.res.ImmutableEnumContainer;
import org.jboss.errai.marshalling.tests.res.InterfaceA;
import org.jboss.errai.marshalling.tests.res.Outer;
import org.jboss.errai.marshalling.tests.res.Outer2;
import org.jboss.errai.marshalling.tests.res.SType;
import org.jboss.errai.marshalling.tests.res.shared.Role;
import org.jboss.errai.marshalling.tests.res.shared.User;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class ServerMarshallingTest {

  static {
    System.setProperty("errai.devel.nocache", "true");
    System.out.println("Working Dir: " + new File("").getAbsoluteFile().getAbsolutePath());
  }

  @SuppressWarnings("unchecked")
  private void testEncodeDecodeDynamic(Object value) {
    if (value == null) return;
    testEncodeDecode((Class<Object>) value.getClass(), value);
  }

  private <T> void testEncodeDecode(Class<T> type, T value) {
    Marshaller<Object> marshaller = MappingContextSingleton.get().getMarshaller(type.getName());
    Assert.assertNotNull("did not find " + type.getName() + " marshaller", marshaller);

    MarshallingSession encSession = MarshallingSessionProviderFactory.getEncoding();
    String enc = "[" + marshaller.marshall(value, encSession) + "]";

    MarshallingSession decSession = MarshallingSessionProviderFactory.getDecoding();
    EJValue parsedJson = ParserFactory.get().parse(enc);
    Assert.assertTrue("expected outer JSON to be array", parsedJson.isArray() != null);

    EJValue encodedNode = parsedJson.isArray().get(0);

    Object dec = marshaller.demarshall(encodedNode, decSession);
    Assert.assertTrue("decoded type not an instance of " + value.getClass(), type.isAssignableFrom(value.getClass()));
    assertEquals(value, dec);
  }

  private static void assertEquals(Object a1, Object a2) {
    if (a1 != null && a2 != null) {
      if (a1.getClass().isArray()) {
        if (a2.getClass().isArray()) {
          assertArrayEquals(a1, a2);
          return;
        }

      }
    }

    Assert.assertEquals(a1, a2);
  }

  private static void assertArrayEquals(Object array1, Object array2) {
    int len1 = Array.getLength(array1);
    int len2 = Array.getLength(array2);

    if (len1 != len2) Assert.failNotEquals("different length arrays!", array1, array2);

    Object el1, el2;

    for (int i = 0; i < len1; i++) {
      el1 = Array.get(array1, i);
      el2 = Array.get(array2, i);

      if ((el1 == null || el2 == null) && el1 != null) {
        Assert.failNotEquals("different values", array1, array2);
      }
      else if (el1 != null) {
        assertEquals(el1, el2);
      }
    }
  }

  /**
   * Tests that the MappingContext.getMarshaller() returns null when asked for a
   * marshaller it doesn't have. In particular, it should not throw an exception.
   */
  @Test
  public void testMissingTypeGetsNullMarshaller() {
    Marshaller<Object> marshaller = MappingContextSingleton.get().getMarshaller("does.not.Exist");
    Assert.assertNull(marshaller);
  }

  @Test
  public void testString() {
    testEncodeDecode(String.class, "ThisIsOurTestString");
  }

  @Test
  public void testStringEncodeWithHighLevelAPI() {
    final String val = "Seventeen-oh-one";
    String json = ServerMarshalling.toJSON(val);
    assertEquals("\"Seventeen-oh-one\"", json);
  }

  /**
   * This method tests for string round-trip encode/decode using the
   * ServerMarshalling.toJSON() and ServerMarshalling.fromJSON() methods
   * specifically. The success or failure of this method is not predicted by the
   * success or failure of {@link #testString()}, which uses lower-level APIs.
   */
  @Test
  public void testStringRoundTripWithHighLevelAPI() {
    final String val = "Seventeen-oh-one";
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to round-trip the string", val, ServerMarshalling.fromJSON(json));
  }

  /**
   * Tests that strings containing non-ASCII characters survive the
   * encode/decode process.
   */
  @Test
  public void testNonAsciiString() {
    testEncodeDecode(String.class, "S\u00ebvent\u00e9\u00ebn-\u00f8h-\u00f3\u00f1e");
  }

  @Test
  public void testEscapesInString() {
    testEncodeDecode(String.class, "\n\t\r\n{}{}{}\\}\\{\\]\\[");
  }

  @Test
  public void testStringArray() {
    testEncodeDecode(String[].class, new String[]{"foo", "bar", "superfoobar", "ultrafoobar"});
  }

  @Test
  public void testIntegerArray() {
    testEncodeDecode(Integer[].class, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
  }

  @Test
  public void testPrimIntegerArray() {
    testEncodeDecode(int[].class, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
  }

  @Test
  public void testLongArray() {
    testEncodeDecode(Long[].class, new Long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l});
  }

  @Test
  public void testPrimLongArray() {
    testEncodeDecode(long[].class, new long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l});
  }


  @Test
  public void testIntegerMaxValue() {
    testEncodeDecode(Integer.class, Integer.MAX_VALUE);
  }

  @Test
  public void testIntegerMinValue() {
    testEncodeDecode(Integer.class, Integer.MIN_VALUE);
  }

  @Test
  public void testIntegerRandomValue() {
    testEncodeDecode(Integer.class, new Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
  }

  @Test
  public void testShortMinValue() {
    testEncodeDecode(Short.class, Short.MAX_VALUE);
  }

  @Test
  public void testShortMaxValue() {
    testEncodeDecode(Short.class, Short.MIN_VALUE);
  }

  @Test
  public void testShortRandomValue() {
    testEncodeDecode(Short.class, (short) new Random(System.currentTimeMillis()).nextInt(Short.MAX_VALUE));
  }

  @Test
  public void testLongMaxValue() {
    testEncodeDecode(Long.class, Long.MAX_VALUE);
  }

  @Test
  public void testLongMinValue() {
    testEncodeDecode(Long.class, Long.MIN_VALUE);
  }

  @Test
  public void testLong6536000376648360988() {
    testEncodeDecode(Long.class, 6536000376648360988L);
  }

  @Test
  public void testLongRandomValue() {
    testEncodeDecode(Long.class, new Random(System.currentTimeMillis()).nextLong());
  }

  @Test
  public void testDoubleMaxValue() {
    testEncodeDecode(Double.class, Double.MAX_VALUE);
  }

  @Test
  public void testDoubleMinValue() {
    testEncodeDecode(Double.class, Double.MIN_VALUE);
  }

  @Test
  public void testDoubleRandomValue() {
    testEncodeDecode(Double.class, new Random(System.currentTimeMillis()).nextDouble());
  }

  @Test
  public void testDouble0dot9635950160419999() {
    testEncodeDecode(Double.class, 0.9635950160419999d);
  }

  @Test
  public void testDoubleNan() {
    testEncodeDecode(Double.class, Double.NaN);
  }

  @Test
  public void testDoublePosInf() {
    testEncodeDecode(Double.class, Double.POSITIVE_INFINITY);
  }

  @Test
  public void testDoubleNegInf() {
    testEncodeDecode(Double.class, Double.NEGATIVE_INFINITY);
  }

  @Test
  public void testFloatMaxValue() {
    testEncodeDecode(Float.class, Float.MAX_VALUE);
  }

  @Test
  public void testFloatMinValue() {
    testEncodeDecode(Float.class, Float.MIN_VALUE);
  }

  @Test
  public void testFloatRandomValue() {
    testEncodeDecode(Float.class, new Random(System.currentTimeMillis()).nextFloat());
  }

  @Test
  public void testFloatNan() {
    testEncodeDecode(Float.class, Float.NaN);
  }

  @Test
  public void testFloatPosInf() {
    testEncodeDecode(Float.class, Float.POSITIVE_INFINITY);
  }

  @Test
  public void testFloatNegInf() {
    testEncodeDecode(Float.class, Float.NEGATIVE_INFINITY);
  }

  @Test
  public void testByteMaxValue() {
    testEncodeDecode(Byte.class, Byte.MAX_VALUE);
  }

  @Test
  public void testByteMinValue() {
    testEncodeDecode(Byte.class, Byte.MIN_VALUE);
  }

  @Test
  public void testByteRandomValue() {
    testEncodeDecode(Byte.class, (byte) new Random(System.currentTimeMillis()).nextInt());
  }

  @Test
  public void testBooleanTrue() {
    testEncodeDecode(Boolean.class, Boolean.TRUE);
  }

  @Test
  public void testBooleanFalse() {
    testEncodeDecode(Boolean.class, Boolean.FALSE);
  }

  @Test
  public void testCharMaxValue() {
    testEncodeDecode(Character.class, Character.MAX_VALUE);
  }

  @Test
  public void testCharMinValue() {
    testEncodeDecode(Character.class, Character.MIN_VALUE);
  }

  @Test
  public void testListMarshall() {
    testEncodeDecodeDynamic(Arrays.asList("foo", "bar", "sillyhat"));
  }

  @Test
  public void testUnmodifiableListMarshall() {
    testEncodeDecodeDynamic(Collections.unmodifiableList(Arrays.asList("foo", "bar", "sillyhat")));
  }

  @Test
  public void testUnmodifiableSetMarshall() {
    testEncodeDecodeDynamic(Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("foo", "bar", "sillyhat"))));
  }

  @Test
  public void testUnmodifiableSortedSetMarshall() {
    testEncodeDecodeDynamic(Collections.unmodifiableSortedSet(new TreeSet<String>(Arrays.asList("foo", "bar", "sillyhat"))));
  }

  @Test
  public void testSingletonListMarshall() {
    testEncodeDecodeDynamic(Collections.singletonList("foobie"));
  }

  @Test
  public void testSetMarshall() {
    testEncodeDecodeDynamic(new HashSet<String>(Arrays.asList("foo", "bar", "sillyhat")));
  }

  @Test
  public void testEmptyList() {
    testEncodeDecodeDynamic(Collections.emptyList());
  }

  @Test
  public void testEmptySet() {
    testEncodeDecodeDynamic(Collections.emptySet());
  }

  @Test
  public void testEmptyMap() {
    testEncodeDecodeDynamic(Collections.emptyMap());
  }

  @Test
  public void testMapWithBigIntegerAsKey() {
	  Map<BigInteger, String> map = new HashMap<BigInteger, String>();
	  map.put(new BigInteger("10"), "10 value");
	  testEncodeDecodeDynamic(map);
  }

  @Test
  public void testMapWithBigDecimalAsKey() {
	  Map<BigDecimal, String> map = new HashMap<BigDecimal, String>();
	  map.put(new BigDecimal("10"), "10 value");
	  testEncodeDecodeDynamic(map);
  }

  @Test
  public void testSynchronizedSortedMap() {
    TreeMap<String, String> map = new TreeMap<String, String>();
    map.put("a", "a");
    map.put("b", "b");
    map.put("c", "c");
    testEncodeDecodeDynamic(Collections.synchronizedSortedMap(map));
  }

  @Test
  public void testSynchronizedMap() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("a", "a");
    map.put("b", "b");
    map.put("c", "c");
    testEncodeDecodeDynamic(Collections.synchronizedMap(map));
  }

  @Test
  public void testSynchronizedSortedSet() {
    TreeSet<String> set = new TreeSet<String>();
    set.add("a");
    set.add("b");
    set.add("c");
    testEncodeDecodeDynamic(Collections.synchronizedSortedSet(set));
  }

  @Test
  public void testSynchronizedSet() {
    HashSet<String> set = new HashSet<String>();
    set.add("a");
    set.add("b");
    set.add("c");
    testEncodeDecodeDynamic(Collections.synchronizedSet(set));
  }

  @Test
  public void testUserEntity() {
    User user = new User();
    user.setUserName("foo");
    user.setPassword("bar");

    Set<Role> roles = new HashSet<Role>();
    roles.add(new Role("admin"));
    roles.add(new Role("users"));

    user.setRoles(roles);

    testEncodeDecodeDynamic(user);
  }

  @Test
  public void testEmptyOptional() {
    testEncodeDecodeDynamic(Optional.empty());
  }

  @Test
  public void testNonEmptyOptionalString() {
    testEncodeDecodeDynamic(Optional.of("foo"));
  }

  @Test
  public void testNonEmptyOptionalPrimitive() {
    testEncodeDecodeDynamic(Optional.of(1));
  }

  @Test
  public void testOptionalEmptyList() {
    testEncodeDecodeDynamic(Optional.of(Collections.emptyList()));
  }

  @Test
  public void testOptionalList() {
    testEncodeDecodeDynamic(Optional.of(Collections.singletonList("foo")));
  }

  @Test
  public void testOptionalEntityWithInterfaceArrayEmpty() {
    final EntityWithInterfaceArray entity = new EntityWithInterfaceArray();
    entity.setA(new InterfaceA[0]);

    testEncodeDecodeDynamic(Optional.of(entity));
  }

  @Test
  public void testOptionalEntityWithInterfaceArrayNotEmpty() {
    final EntityWithInterfaceArray entity = new EntityWithInterfaceArray();
    entity.setA(new InterfaceA[] { new AImpl1(1), new AImpl2("foo") });

    testEncodeDecodeDynamic(Optional.of(entity));
  }

  class ServerRandomProvider implements RandomProvider {
    private final char[] CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    private final Random random = new Random(System.nanoTime());

    @Override
    public boolean nextBoolean() {
      return random.nextBoolean();
    }

    @Override
    public int nextInt(int upper) {
      return random.nextInt(upper);
    }

    @Override
    public double nextDouble() {
      return new BigDecimal(random.nextDouble(), MathContext.DECIMAL32).doubleValue();
    }

    @Override
    public char nextChar() {
      return CHARS[nextInt(1000) % CHARS.length];
    }

    @Override
    public String randString() {
      StringBuilder builder = new StringBuilder();
      int len = nextInt(25) + 5;
      for (int i = 0; i < len; i++) {
        builder.append(nextChar());
      }
      return builder.toString();
    }
  }

  public interface RandomProvider {
    public boolean nextBoolean();

    public int nextInt(int upper);

    public double nextDouble();

    public char nextChar();

    public String randString();
  }

  @Test
  public void testSTypeEntity() {
    SType sType = SType.create(new ServerRandomProvider());

//    long st = System.currentTimeMillis();
//    for (int i = 0; i < 10000; i++) {
    testEncodeDecodeDynamic(sType);

    MappingContextSingleton.get();
    String json = ServerMarshalling.toJSON(sType);

    System.out.println(json);

    //  }
//    System.out.println(System.currentTimeMillis() - st);
  }

  @Test
  public void testPrimitiveIntRoundTrip() {
    final int val = 1701;
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall int", val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testPrimitiveLongRoundTrip() {
    final long val = 1701l;
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall long", val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testPrimitiveDoubleRoundTrip() {
    final double val = 17.01;
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall double", val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testPrimitiveFloatRoundTrip() {
    final float val = 1701f;
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall float", val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testSimpleEnumRoundTrip() {
    EnumTestA val = EnumTestA.FIRST;
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall enum", val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testEnumContainerWithNullRefs() {
    EnumContainer val = new EnumContainer();
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall enum container with nulls",
            val.toString(), ServerMarshalling.fromJSON(json).toString());
  }

  @Test
  public void testEnumContainerWithDistinctRefs() {
    EnumContainer val = new EnumContainer();
    val.setEnumA1(EnumTestA.FIRST);
    val.setEnumA2(EnumTestA.SECOND);
    val.setEnumA3(EnumTestA.THIRD);
    val.setStatefulEnum1(EnumWithState.THING1);
    val.setStatefulEnum2(EnumWithState.THING2);

    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall enum container with distinct refs",
            val.toString(), ServerMarshalling.fromJSON(json).toString());
  }

  @Test
  public void testEnumContainerWithRepeatedRefs() {
    EnumContainer val = new EnumContainer();
    val.setEnumA1(EnumTestA.FIRST);
    val.setEnumA2(EnumTestA.FIRST);
    val.setEnumA3(EnumTestA.FIRST);
    val.setStatefulEnum1(EnumWithState.THING1);
    val.setStatefulEnum2(EnumWithState.THING1);
    val.setStatefulEnum3(EnumWithState.THING1);

    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall enum container with repeated refs",
            val.toString(), ServerMarshalling.fromJSON(json).toString());
  }

  @Test
  public void testEnumContainerContainer() {
    EnumContainerContainer val = new EnumContainerContainer();

    EnumContainer enumContainer = new EnumContainer();
    enumContainer.setEnumA1(EnumTestA.FIRST);

    val.setEnumContainer(enumContainer);
    val.setEnumA(EnumTestA.FIRST);

    String json = ServerMarshalling.toJSON(val);
    System.out.println(json);
    Assert.assertEquals("Failed to marshall/demarshall enum container container",
            val.toString(), ServerMarshalling.fromJSON(json).toString());
  }

  @Test
  // This tests guards against regressions of https://issues.jboss.org/browse/ERRAI-370
  public void testEnumWithAbstractMethod() {
    EnumWithAbstractMethod val = EnumWithAbstractMethod.THING2;
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall enum with abstract method", val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testImmutableEnumContainer() {
    ImmutableEnumContainer val = new ImmutableEnumContainer(EnumTestA.FIRST);
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall enum container",
            val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testImmutableEnumContainerWithNullRefs() {
    ImmutableEnumContainer val = new ImmutableEnumContainer(null);
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall immutable enum container with nulls",
            val, ServerMarshalling.fromJSON(json));
  }

  @Test
  public void testListWithInheritedTypeVariable() {
    EntityWithInheritedTypeVariable<String> val = new EntityWithInheritedTypeVariable<String>();
    val.setList(Arrays.asList("one", "gwt", null));
    val.addToFieldAccessedList("this is an entry");
    String json = ServerMarshalling.toJSON(val);
    Assert.assertEquals("Failed to marshall/demarshall immutable enum container with nulls",
            val, ServerMarshalling.fromJSON(json));
  }
  
  // This is a regression test for ERRAI-794
  @Test
  public void testBackReferenceOrderingWithMapsTo() {
    Outer.Nested key = new Outer.Nested("exp");
    Outer outer = new Outer (Arrays.asList(key), key);
    testEncodeDecode(Outer.class, outer);
    
    Outer2.Nested key2 = new Outer2.Nested("exp");
    Outer2 outer2 = new Outer2 (key2, Arrays.asList(key2));
    testEncodeDecode(Outer2.class, outer2);
  }
}
