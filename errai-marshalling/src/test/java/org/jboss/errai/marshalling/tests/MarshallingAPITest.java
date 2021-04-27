/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.jboss.errai.marshalling.tests.res.AImpl1;
import org.jboss.errai.marshalling.tests.res.AImpl2;
import org.jboss.errai.marshalling.tests.res.ASubImpl1;
import org.jboss.errai.marshalling.tests.res.BImpl1;
import org.jboss.errai.marshalling.tests.res.BImpl2;
import org.jboss.errai.marshalling.tests.res.EntityWithAbstractFieldType;
import org.jboss.errai.marshalling.tests.res.EntityWithInheritedPublicFields;
import org.jboss.errai.marshalling.tests.res.EntityWithInterface;
import org.jboss.errai.marshalling.tests.res.EntityWithInterfaceArray;
import org.jboss.errai.marshalling.tests.res.EntityWithInterfaceArrayInPublicField;
import org.jboss.errai.marshalling.tests.res.EntityWithMapUsingArrayValues;
import org.jboss.errai.marshalling.tests.res.EntityWithPortableSubtypesInArray;
import org.jboss.errai.marshalling.tests.res.EntityWithPublicFields;
import org.jboss.errai.marshalling.tests.res.InterfaceA;
import org.jboss.errai.marshalling.tests.res.Outer;
import org.jboss.errai.marshalling.tests.res.Outer2;
import org.jboss.errai.marshalling.tests.res.SomeInterface;
import org.jboss.errai.marshalling.tests.res.shared.ItemWithEnum;
import org.jboss.errai.marshalling.tests.res.shared.NullBoxedNatives;
import org.jboss.errai.marshalling.tests.res.shared.Role;
import org.jboss.errai.marshalling.tests.res.shared.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class MarshallingAPITest {

  @Before
  public void ensureMarshallingSystemInitialized() {
    MappingContextSingleton.get();
  }

  private void testEncodeDecode(final Object value) {
    if (value == null) return;
    final String json = ServerMarshalling.toJSON(value);
    final Object result = ServerMarshalling.fromJSON(json);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testQualifiedInt() {
    testEncodeDecode(212);
  }

  @Test
  public void testQualifiedLong() {
    testEncodeDecode(3293L);
  }

  @Test
  public void testNullLong() {
    testEncodeDecode((Long) null);
  }

  @Test
  public void testDoubleNan() {
    testEncodeDecode(Double.NaN);
  }

  @Test
  public void testDoublePosInf() {
    testEncodeDecode(Double.POSITIVE_INFINITY);
  }

  @Test
  public void testDoubleNegInf() {
    testEncodeDecode(Double.NEGATIVE_INFINITY);
  }

  @Test
  public void testFloatNan() {
    testEncodeDecode(Float.NaN);
  }

  @Test
  public void testFloatPosInf() {
    testEncodeDecode(Float.POSITIVE_INFINITY);
  }

  @Test
  public void testFloatNegInf() {
    testEncodeDecode(Float.NEGATIVE_INFINITY);
  }

  @Test
  public void testQualifiedShort() {
    testEncodeDecode((short) 123);
  }

  @Test
  public void testQualifiedDouble() {
    testEncodeDecode(3232938.323d);
  }

  @Test
  public void testQualifiedFloat() {
    testEncodeDecode(32013910.32f);
  }

  @Test
  public void testQualifiedBoolean() {
    testEncodeDecode(true);
  }

  @Test
  public void testQualifiedByte() {
    testEncodeDecode((byte) 120);
  }

  @Test
  public void testQualifiedCharacter() {
    testEncodeDecode('a');
  }

  @Test
  public void testUserEntity() {
    final User user = new User();
    user.setUserName("foo");
    user.setPassword("bar");

    final Set<Role> roles = new HashSet<>();
    roles.add(new Role("admin"));
    roles.add(new Role("users"));

    user.setRoles(roles);

    testEncodeDecode(user);
  }

  @Test
  public void testEntityWithNullBoxedNatives() {
    final NullBoxedNatives entity = new NullBoxedNatives();
    testEncodeDecode(entity);
  }

  @Test
  public void testNullEnumInEntity() {
    final ItemWithEnum itemWithEnum = new ItemWithEnum();
    testEncodeDecode(itemWithEnum);
  }


  @Test
  public void testEntityWithInterface() {
    EntityWithInterface ewi = new EntityWithInterface();
    ewi.setA(new AImpl1(4711));
    testEncodeDecode(ewi);

    ewi = new EntityWithInterface();
    ewi.setA(new AImpl2("admin"));
    testEncodeDecode(ewi);
  }

  @Test
  public void testEntityWithInterfaceArray() {
    final EntityWithInterfaceArray ewia = new EntityWithInterfaceArray();

    final InterfaceA[] a = new InterfaceA[4];
    a[0] = new AImpl1(4711);
    a[1] = null;
    a[2] = new AImpl2("admin");
    a[3] = new ASubImpl1(11f);
    ewia.setA(a);

    testEncodeDecode(ewia);
  }

  @Test
  public void testEntityWithInterfaceArrayInPublicField() {
    final EntityWithInterfaceArrayInPublicField ewiaipf = new EntityWithInterfaceArrayInPublicField();

    final InterfaceA[] a = new InterfaceA[4];
    a[0] = new AImpl1(4711);
    a[1] = null;
    a[2] = new AImpl2("admin");
    a[3] = new ASubImpl1(11f);
    ewiaipf.a = a;

    testEncodeDecode(ewiaipf);
  }

  @Test
  public void testEntityWithPortableSubtypesInArray() {
    final EntityWithPortableSubtypesInArray ewpsia = new EntityWithPortableSubtypesInArray();

    final AImpl1[] a = new AImpl1[3];
    a[0] = new AImpl1(4711);
    a[1] = null;
    a[2] = new ASubImpl1(11f);
    ewpsia.setA(a);

    testEncodeDecode(ewpsia);
  }

  @Test
  public void testEntityWithAbstractFieldType() {
    EntityWithAbstractFieldType ewaft = new EntityWithAbstractFieldType();
    ewaft.setB(new BImpl1(4711));
    testEncodeDecode(ewaft);

    ewaft = new EntityWithAbstractFieldType();
    ewaft.setB(new BImpl2("admin"));
    testEncodeDecode(ewaft);
  }

  @Test
  public void testEntityWithPublicFields() {
    final EntityWithPublicFields ewpf = new EntityWithPublicFields();

    final ArrayList<String> values = new ArrayList<>();
    values.add("1");
    values.add("2");
    values.add("3");

    ewpf.value = 17;
    ewpf.values = values;

    testEncodeDecode(ewpf);
  }

  @Test
  public void testEntityWithInheritedPublicFields() {
    final EntityWithInheritedPublicFields ewipf = new EntityWithInheritedPublicFields();

    final ArrayList<String> values = new ArrayList<>();
    values.add("1");
    values.add("2");
    values.add("3");

    ewipf.value = 17;
    ewipf.values = values;

    testEncodeDecode(ewipf);
  }

  @Test
  public void testEntityWithMapUsingArrayValues() {
    final EntityWithMapUsingArrayValues ewmuav = new EntityWithMapUsingArrayValues();

    final Map<String, String[]> data = new HashMap<>();
    data.put("1", new String[]{"2", "3", "4"});
    data.put("5", new String[]{"6", "7", "8"});

    ewmuav.setData(data);
    testEncodeDecode(ewmuav);
  }

  // This is a regression test for ERRAI-794
  @Test
  public void testBackReferenceOrderingWithMapsTo() {
    final Outer.Nested key = new Outer.Nested("exp");
    final Outer outer = new Outer (Arrays.asList(key), key);
    testEncodeDecode(outer);

    final Outer2.Nested key2 = new Outer2.Nested("exp");
    final Outer2 outer2 = new Outer2 (key2, Arrays.asList(key2));
    testEncodeDecode(outer2);
  }

  // This is a regression test for ERRAI-811
  @Test
  public void testEntityWithMapUsingNullKey() {
    final Map<String, String> data = new HashMap<>();
    data.put("key1", "value1");
    data.put(null, "value2");

    testEncodeDecode(data);
  }

  @Test
  public void testCanHandleReturnsTrueForInterface() throws Exception {
    assertTrue(Marshalling.canHandle(SomeInterface.class));
  }

  @Test
  public void testEmptyOptional() {
    testEncodeDecode(Optional.empty());
  }

  @Test
  public void testNonEmptyOptionalString() {
    testEncodeDecode(Optional.of("foo"));
  }

  @Test
  public void testNonEmptyOptionalPrimitive() {
    testEncodeDecode(Optional.of(1));
  }

  @Test
  public void testOptionalEmptyList() {
    testEncodeDecode(Optional.of(Collections.emptyList()));
  }

  @Test
  public void testOptionalList() {
    testEncodeDecode(Optional.of(Collections.singletonList("foo")));
  }

  @Test
  public void testOptionalEntityWithInterfaceArrayEmpty() {
    final EntityWithInterfaceArray entity = new EntityWithInterfaceArray();
    entity.setA(new InterfaceA[0]);

    testEncodeDecode(Optional.of(entity));
  }

  @Test
  public void testOptionalEntityWithInterfaceArrayNotEmpty() {
    final EntityWithInterfaceArray entity = new EntityWithInterfaceArray();
    entity.setA(new InterfaceA[] { new AImpl1(1), new AImpl2("foo") });

    testEncodeDecode(Optional.of(entity));
  }

  @Test
  public void testOptionalUserEntity() {
    final User user = new User();
    user.setUserName("foo");
    user.setPassword("bar");

    final Set<Role> roles = new HashSet<>();
    roles.add(new Role("admin"));
    roles.add(new Role("users"));

    user.setRoles(roles);
    testEncodeDecode(Optional.of(user));
  }
}
