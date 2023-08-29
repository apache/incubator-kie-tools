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


package org.jboss.errai.ioc.rebind.ioc.graph.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.junit.Test;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class GraphUtilUnitTest {

  public interface ThrowableCollection<T extends Throwable> extends Collection<T> {}

  public interface ThrowableList<T extends Throwable> extends List<T>, ThrowableCollection<T> {}

  public interface IntCollection extends Collection<Integer> {}

  public interface WildcardMethod {
    Collection<?> method();
  }

  @SuppressWarnings("serial")
  public class ThrowableArrayList<T extends Throwable> extends ArrayList<T> implements ThrowableList<T> {}

  @Test
  public void rawInterfaceIsAssignableToParameterizedSameInterface() throws Exception {
    final MetaClass parameterized = parameterizedAs(Collection.class, typeParametersOf(Integer.class));
    final MetaClass raw = MetaClassFactory.get(Collection.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void rawInterfaceIsAssignableToParameterizedSameInterfaceWithBaseParameterType() throws Exception {
    final MetaClass parameterized = parameterizedAs(ThrowableList.class, typeParametersOf(Throwable.class));
    final MetaClass raw = MetaClassFactory.get(ThrowableList.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void rawClassIsAssignableToParameterizedSameClass() throws Exception {
    final MetaClass parameterized = parameterizedAs(ArrayList.class, typeParametersOf(Integer.class));
    final MetaClass raw = MetaClassFactory.get(ArrayList.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void rawClassIsAssignableToParameterizedSameClassWithBaseParameterType() throws Exception {
    final MetaClass parameterized = parameterizedAs(ThrowableArrayList.class, typeParametersOf(Throwable.class));
    final MetaClass raw = MetaClassFactory.get(ThrowableArrayList.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void rawClassIsAssignableToParameterizedInterface() throws Exception {
    final MetaClass parameterized = parameterizedAs(List.class, typeParametersOf(Integer.class));
    final MetaClass raw = MetaClassFactory.get(ArrayList.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void rawClassIsAssignableToParameterizedInterfaceWithBaseParameterType() throws Exception {
    final MetaClass parameterized = parameterizedAs(ThrowableList.class, typeParametersOf(Throwable.class));
    final MetaClass raw = MetaClassFactory.get(ThrowableArrayList.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void rawInterfaceIsAssignableToParameterizedSuperInterface() throws Exception {
    final MetaClass parameterized = parameterizedAs(Collection.class, typeParametersOf(Integer.class));
    final MetaClass raw = MetaClassFactory.get(List.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void rawInterfaceIsAssignableToParameterizedSuperInterfaceWithBaseParameterType() throws Exception {
    final MetaClass parameterized = parameterizedAs(ThrowableCollection.class, typeParametersOf(Throwable.class));
    final MetaClass raw = MetaClassFactory.get(ThrowableList.class);
    assertNull(raw.getParameterizedType());

    assertTrue(GraphUtil.hasAssignableTypeParameters(raw, parameterized));
  }

  @Test
  public void parameterizedInterfaceIsAssignableToSelf() throws Exception {
    final MetaClass parameterized = parameterizedAs(Collection.class, typeParametersOf(Integer.class));

    assertTrue(GraphUtil.hasAssignableTypeParameters(parameterized, parameterized));
  }

  @Test
  public void parameterizedInterfaceIsAssignableToParameterizedSuperType() throws Exception {
    final MetaClass parameterized = parameterizedAs(List.class, typeParametersOf(Integer.class));
    final MetaClass superParameterized = parameterizedAs(Collection.class, typeParametersOf(Integer.class));

    assertTrue(GraphUtil.hasAssignableTypeParameters(parameterized, superParameterized));
  }

  @Test
  public void interfaceWithConcreteTypeParameterSupertypeIsAssignableToParameterizedSuperType() throws Exception {
    final MetaClass parameterized = MetaClassFactory.get(IntCollection.class);
    final MetaClass superParameterized = parameterizedAs(Collection.class, typeParametersOf(Integer.class));

    assertTrue(GraphUtil.hasAssignableTypeParameters(parameterized, superParameterized));
  }

  @Test
  public void interfaceWithConcreteTypeParameterSupertypeIsAssignableToRawSuperType() throws Exception {
    final MetaClass parameterized = MetaClassFactory.get(IntCollection.class);
    final MetaClass raw = MetaClassFactory.get(Collection.class);

    assertTrue(GraphUtil.hasAssignableTypeParameters(parameterized, raw));
  }

  @Test
  public void parameterizedTypeIsAssignableToSameTypeWithWildcard() throws Exception {
    final MetaClass parameterized = parameterizedAs(Collection.class, typeParametersOf(Integer.class));
    final MetaClass wildcard = MetaClassFactory.get(WildcardMethod.class).getMethod("method", new Class[0]).getReturnType();

    assertTrue(GraphUtil.hasAssignableTypeParameters(parameterized, wildcard));
  }

}
