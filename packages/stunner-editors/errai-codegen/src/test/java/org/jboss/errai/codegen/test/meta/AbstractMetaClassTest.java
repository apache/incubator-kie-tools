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


package org.jboss.errai.codegen.test.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.meta.MetaWildcardType;
import org.jboss.errai.codegen.test.model.ClassWithArrayGenerics;
import org.jboss.errai.codegen.test.model.ClassWithGenericCollections;
import org.jboss.errai.codegen.test.model.ClassWithGenericMethods;
import org.jboss.errai.codegen.test.model.HasManyConstructors;
import org.jboss.errai.codegen.test.model.ObjectWithNested;
import org.jboss.errai.codegen.test.model.ParameterizedClass;
import org.jboss.errai.codegen.test.model.TestInterface;
import org.jboss.errai.codegen.test.model.tree.Child;
import org.jboss.errai.codegen.test.model.tree.Grandparent;
import org.jboss.errai.codegen.test.model.tree.GrandparentInterface;
import org.jboss.errai.codegen.test.model.tree.GrandparentSuperInterface;
import org.jboss.errai.codegen.test.model.tree.IsolatedInterface;
import org.jboss.errai.codegen.test.model.tree.Parent;
import org.jboss.errai.codegen.test.model.tree.ParentInterface;
import org.jboss.errai.codegen.test.model.tree.ParentSuperInterface1;
import org.jboss.errai.codegen.test.model.tree.ParentSuperInterface2;
import org.jboss.errai.codegen.util.GenUtil;
import org.junit.Test;
import org.mvel2.util.NullType;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Subclassable container for the test cases that guarantee an implementation of
 * MetaClass conforms to the general contract. Each implementation of MetaClass
 * should have a corresponding subclass of this test. This way, every
 * implementation of MetaClass will be subjected to the same set of tests.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Mike Brock <cbrock@redhat.com>
 */
public abstract class AbstractMetaClassTest {

  /**
   * Requests the MetaClass impl from the subclass and checks that it's the
   * expected subtype of MetaClass before returning it.
   *
   * @param javaClass
   * @return
   */
  private MetaClass getMetaClass(final Class<?> javaClass) {
    final MetaClass impl = getMetaClassImpl(javaClass);
    assertEquals(getTypeOfMetaClassBeingTested(), impl.getClass());
    return impl;
  }

  /**
   * Returns a MetaClass object--of the type being tested--that represents {@code javaClass}.
   *
   * @param javaClass The Java class type being requested.
   * @return an instance of the subtype of MetaClass that's being tested.
   */
  protected abstract MetaClass getMetaClassImpl(Class<?> javaClass);

  /**
   * Returns the type of MetaClass that will be returned from {@link #getMetaClassImpl(Class)}.
   */
  protected abstract Class<? extends MetaClass> getTypeOfMetaClassBeingTested();

  @Test
  public void testInternalNameForOneDimensionalPrimitiveArray() {
   final String internalName = getMetaClass(char[].class).getInternalName();
   assertEquals("Wrong internal name generated for one-dimensional primitive array",
       "[C", internalName);
  }

  @Test
  public void testInternalNameForOneDimensionalObjectArray() {
   final String internalName = getMetaClass(String[].class).getInternalName();
   assertEquals("Wrong internal name generated for one-dimensional object array",
       "[Ljava/lang/String;", internalName);
  }

  @Test
  public void testInternalNameForMultiDimensionalPrimitiveArray() {
   final String internalName = getMetaClass(char[][].class).getInternalName();
   assertEquals("Wrong internal name generated for multidimensional primitive array",
       "[[C", internalName);
  }

  @Test
  public void testInternalNameForMultiDimensionalObjectArray() {
   final String internalName = getMetaClass(String[][].class).getInternalName();
   assertEquals("Wrong internal name generated for multidimensional object array",
       "[[Ljava/lang/String;", internalName);
  }

  @Test
  public void testObjectIsAssignableFromNull() throws Exception {
	  // This test checks the valid case:
	  // Object example = null;

	  final MetaClass metaObject = getMetaClass(Object.class);
	  final MetaClass metaNull = getMetaClass(NullType.class);

	  assertTrue(metaObject.isAssignableFrom(metaNull));
  }

  @Test
  public void testChildIsAssignableFromNull() throws Exception {
	  // This test checks the valid case:
	  // Child example = null;

	  final MetaClass metaChild = getMetaClass(Child.class);
	  final MetaClass metaNull = getMetaClass(NullType.class);

	  assertTrue(metaChild.isAssignableFrom(metaNull));
  }

  @Test
  public void testNullIsAssignableToChild() throws Exception {
	  // This test checks the valid case:
	  // Child example = null;

	  final MetaClass metaChild = getMetaClass(Child.class);
	  final MetaClass metaNull = getMetaClass(NullType.class);

	  assertTrue(metaNull.isAssignableTo(metaChild));
  }

  @Test
  public void testIsAssignableFromComparisonForNested() {
    final MetaClass interfaceClass = getMetaClass(TestInterface.class);
    final MetaClass metaHolderClass = getMetaClass(ObjectWithNested.class);

    // dig out the nested interface from the holder class and ensure it's what we were looking for
    final MetaClass nestedInterface = metaHolderClass.getDeclaredClasses()[0];
    assertEquals("MyNestedInterface", nestedInterface.getName());
    assertEquals(getTypeOfMetaClassBeingTested(), nestedInterface.getClass());

    assertTrue("MyNestedInterface should be assignable from TestInterface",
            interfaceClass.isAssignableFrom(nestedInterface));
  }

  @Test
  public void testChildIsAssignableFromChild() {
    // This test checks the valid case:
    // Child example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);

    assertTrue(metaChild.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsAssignableToChild() {
    // This test checks the valid case:
    // Child example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);

    assertTrue(metaChild.isAssignableTo(metaChild));
  }

  @Test
  public void testParentIsAssignableFromChild() {
    // This test checks the valid case:
    // Parent example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaParent = getMetaClass(Parent.class);

    assertTrue(metaParent.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromParent() {
    // This test checks the disallowed case:
    // Child child = new Parent();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaParent = getMetaClass(Parent.class);

    assertFalse(metaChild.isAssignableFrom(metaParent));
  }

  @Test
  public void testGrandParentIsAssignableFromChild() {
    // This test checks the valid case:
    // Grandparent example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaGrandparent = getMetaClass(Grandparent.class);

    assertTrue(metaGrandparent.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromGrandParent() {
    // This test checks the disallowed case:
    // Child child = new Grandparent();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaGrandparent = getMetaClass(Grandparent.class);

    assertFalse(metaChild.isAssignableFrom(metaGrandparent));
  }

  /**
   * This is a regression test for ERRAI-238.
   */
  @Test
  public void testUncleIsAssignableFromChild() {
    // This test checks the valid case:
    // ParentInterface example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaUncle = getMetaClass(ParentInterface.class);

    assertTrue(metaUncle.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromUncle() {
    // This test checks the disallowed case:
    // Child child = new ParentInterface() {};

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaUncle = getMetaClass(ParentInterface.class);

    assertFalse(metaChild.isAssignableFrom(metaUncle));
  }

  /**
   * This is a regression test for ERRAI-238.
   */
  @Test
  public void testUncleInLawIsAssignableFromChild() {
    // This test checks the valid case:
    // ParentSuperInterface1 example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaUncleInLaw = getMetaClass(ParentSuperInterface1.class);

    assertTrue(metaUncleInLaw.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromUncleInLaw() {
    // This test checks the disallowed case:
    // Child child = new ParentSuperInterface1() {};

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaUncleInLaw = getMetaClass(ParentSuperInterface1.class);

    assertFalse(metaChild.isAssignableFrom(metaUncleInLaw));
  }

  /**
   * This is a regression test for ERRAI-238.
   */
  @Test
  public void testUncleInLaw2IsAssignableFromChild() {
    // This test checks the valid case:
    // ParentSuperInterface2 example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaUncleInLaw = getMetaClass(ParentSuperInterface2.class);

    assertTrue(metaUncleInLaw.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromUncleInLaw2() {
    // This test checks the disallowed case:
    // Child child = new ParentSuperInterface2() {};

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaUncleInLaw = getMetaClass(ParentSuperInterface2.class);

    assertFalse(metaChild.isAssignableFrom(metaUncleInLaw));
  }

  /**
   * This is a regression test for ERRAI-238.
   */
  @Test
  public void testGreatUncleIsAssignableFromChild() {
    // This test checks the valid case:
    // GrandparentInterface example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaGreatUncle = getMetaClass(GrandparentInterface.class);

    assertTrue(metaGreatUncle.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromGreatUncle() {
    // This test checks the disallowed case:
    // Child child = new GrandparentInterface() {};

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaGreatUncle = getMetaClass(GrandparentInterface.class);

    assertFalse(metaChild.isAssignableFrom(metaGreatUncle));
  }

  /**
   * This is a regression test for ERRAI-238.
   */
  @Test
  public void testGreatUncleInLawIsAssignableFromChild() {
    // This test checks the valid case:
    //GrandparentSuperInterface example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaGreatUncleInLaw = getMetaClass(GrandparentSuperInterface.class);

    assertTrue(metaGreatUncleInLaw.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromGreatUncleInLaw() {
    // This test checks the disallowed case:
    // Child child = new GrandparentSuperInterface() {};

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaGreatUncleInLaw = getMetaClass(GrandparentSuperInterface.class);

    assertFalse(metaChild.isAssignableFrom(metaGreatUncleInLaw));
  }

  @Test
  public void testObjectIsAssignableFromChild() {
    // This test checks the valid case:
    //Object example = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaObject = getMetaClass(Object.class);

    assertTrue(metaObject.isAssignableFrom(metaChild));
  }

  @Test
  public void testChildIsNotAssignableFromObject() {
    // This test checks the disallowed case:
    // Child child = new Object();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaObject = getMetaClass(Object.class);

    assertFalse(metaChild.isAssignableFrom(metaObject));
  }

  @Test
  public void testObjectIsAssignableFromIsolatedInterface() {
    // This test checks the valid case:
    // Object example = new IsolatedInterface() {};

    final MetaClass metaInterface = getMetaClass(IsolatedInterface.class);
    final MetaClass metaObject = getMetaClass(Object.class);

    assertTrue(metaObject.isAssignableFrom(metaInterface));
  }

  @Test
  public void testIsolatedInterfaceIsNotAssignableFromObject() {
    // This test checks the disallowed case:
    // IsolatedInterface ii = new Object();

    final MetaClass metaInterface = getMetaClass(IsolatedInterface.class);
    final MetaClass metaObject = getMetaClass(Object.class);

    assertFalse(metaInterface.isAssignableFrom(metaObject));
  }

  @Test
  public void testUncleIsAssignableToChild() {
    // This test checks the allowed case:
	// ParentInterface pi = new Child();

    final MetaClass metaChild = getMetaClass(Child.class);
    final MetaClass metaUncle = getMetaClass(ParentInterface.class);

    assertTrue(metaChild.isAssignableTo(metaUncle));
  }

  @Test
  public void testNoDuplicateMethodsInClassHierarchy() throws NotFoundException {
    final MetaClass child = getMetaClass(Child.class);

    final List<MetaMethod> foundMethods = new ArrayList<>();
    for (final MetaMethod m : child.getMethods()) {
      if (m.getName().equals("interfaceMethodOverriddenMultipleTimes")) {
        foundMethods.add(m);
      }
    }

    assertEquals("Only one copy of the method should have been found", 1, foundMethods.size());
  }

  @Test
  public void testSuperClass() throws Exception {
    final MetaClass child = getMetaClass(Child.class);
    final MetaClass parent = child.getSuperClass();

    assertEquals(getMetaClass(Parent.class), parent);
  }

  @Test
  public void testNamingMethods() throws Exception {
    final MetaClass child = getMetaClass(Child.class);

    assertEquals(Child.class.getSimpleName(), child.getName());
    assertEquals(Child.class.getName(), child.getFullyQualifiedName());
    assertEquals(Child.class.getName(), child.getCanonicalName());
    assertEquals("L" + Child.class.getName().replace('.', '/') + ";", child.getInternalName());
  }

  @Test
  public void testAccessModifiersForPublicTopLevelClass() throws Exception {
    final MetaClass child = getMetaClass(Child.class);

    assertTrue(child.isPublic());
    assertFalse(child.isProtected());
    assertFalse(child.isPrivate());

    assertTrue(child.isDefaultInstantiable());
  }

  // TODO: add private, pkg private, protected methods to Child, Parent, Grandparent, and test getDeclaredMethods()
//    System.out.println("--gwt methods--");
//    for (MetaMethod method : Child.class.getName().getDeclaredMethods()) {
//      System.out.println(method.toString());
//    }
//    assertEquals(new HashSet<MetaMethod>(Arrays.asList(Child.class.getName().getDeclaredMethods())),
//            new HashSet<MetaMethod>(Arrays.asList(javaMC.getDeclaredMethods())));

  @Test
  public void testGetInterfaces() throws Exception {
    final MetaClass grandparent = getMetaClass(Grandparent.class);
    assertEquals(1, grandparent.getInterfaces().length);
    assertEquals(
            Arrays.asList(getMetaClass(GrandparentInterface.class)),
            Arrays.asList(grandparent.getInterfaces()));
  }

  @Test
  public void testFieldWithStringTypeParam() throws Exception {
    final MetaClass metaClass = getMetaClass(ClassWithGenericCollections.class);
    final MetaField field = metaClass.getDeclaredField("hasStringParam");
    assertNotNull(field);

    assertEquals("Collection", field.getType().getName());
    assertEquals("java.util.Collection", field.getType().getFullyQualifiedName());
    assertEquals("<java.lang.String>", field.getType().getParameterizedType().toString());
    assertEquals("java.util.Collection<java.lang.String>", field.getType().getFullyQualifiedNameWithTypeParms());
    assertEquals("java.util.Collection", field.getType().getErased().getFullyQualifiedNameWithTypeParms());
    assertEquals(
            Arrays.asList(getMetaClass(String.class)),
            Arrays.asList(field.getType().getParameterizedType().getTypeParameters()));
  }

  @Test
  public void testFieldWithStringBoundedWildcardTypeParam() throws Exception {
    final MetaClass metaClass = getMetaClass(ClassWithGenericCollections.class);
    final MetaField field = metaClass.getDeclaredField("hasWildcardExtendsStringParam");
    assertNotNull(field);

    assertEquals("Collection", field.getType().getName());
    assertEquals("java.util.Collection", field.getType().getFullyQualifiedName());
    assertEquals("<? extends java.lang.String>", field.getType().getParameterizedType().toString());
    assertEquals("java.util.Collection<? extends java.lang.String>", field.getType().getFullyQualifiedNameWithTypeParms());
    assertEquals("java.util.Collection", field.getType().getErased().getFullyQualifiedNameWithTypeParms());

    assertEquals(1, field.getType().getParameterizedType().getTypeParameters().length);
    final MetaWildcardType typeParam = (MetaWildcardType) field.getType().getParameterizedType().getTypeParameters()[0];

    assertEquals("Should have no lower bound",
            Arrays.asList(),
            Arrays.asList(typeParam.getLowerBounds()));

    assertEquals("Upper bound should be java.lang.String",
            Arrays.asList(getMetaClass(String.class)),
            Arrays.asList(typeParam.getUpperBounds()));
  }

  @Test
  public void testFieldWithUnboundedTypeVarParam() throws Exception {
    final MetaClass metaClass = getMetaClass(ClassWithGenericCollections.class);
    final MetaField field = metaClass.getDeclaredField("hasUnboundedTypeVarFromClass");
    assertNotNull(field);

    assertEquals("Collection", field.getType().getName());
    assertEquals("java.util.Collection", field.getType().getFullyQualifiedName());
    assertEquals("java.util.Collection<T>", field.getType().getParameterizedType().getName());
    assertEquals("java.util.Collection<T>", field.getType().getFullyQualifiedNameWithTypeParms());
    assertEquals("java.util.Collection", field.getType().getErased().getFullyQualifiedNameWithTypeParms());

    assertEquals(1, field.getType().getParameterizedType().getTypeParameters().length);
    final MetaTypeVariable typeVar = (MetaTypeVariable) field.getType().getParameterizedType().getTypeParameters()[0];

    assertEquals("T", typeVar.getName());
    assertEquals("Should have no upper bound",
            Arrays.asList(getMetaClass(Object.class)),
            Arrays.asList(typeVar.getBounds()));
  }

  @Test
  public void testFieldWithSingleUpperBoundedTypeVarParam() throws Exception {
    final MetaClass metaClass = getMetaClass(ClassWithGenericCollections.class);
    final MetaMethod field = metaClass.getDeclaredMethod("hasSingleBoundedTypeVarFromSelf", new Class[] {});
    assertNotNull(field);

    final MetaTypeVariable returnType = (MetaTypeVariable) field.getGenericReturnType();
    assertEquals("B", returnType.getName());

    assertEquals("Should have a single upper bound",
            Arrays.asList(getMetaClass(Serializable.class)),
            Arrays.asList(returnType.getBounds()));
  }

  @Test
  public void testFieldWithTwoUpperBoundedTypeVarParam() throws Exception {
    final MetaClass metaClass = getMetaClass(ClassWithGenericCollections.class);
    final MetaMethod field = metaClass.getDeclaredMethod("hasDoubleBoundedTypeVarFromSelf", new Class[] {});
    assertNotNull(field);

    final MetaTypeVariable returnType = (MetaTypeVariable) field.getGenericReturnType();
    assertEquals("B", returnType.getName());

    assertEquals("Should have two upper bounds",
            Arrays.asList(getMetaClass(Collection.class), getMetaClass(Serializable.class)),
            Arrays.asList(returnType.getBounds()));
  }

  @Test
  public void testEraseNonGenericType() throws Exception {
    final MetaClass child = getMetaClass(Child.class);
    assertSame(child, child.getErased());
  }

  @Test
  public void testEraseParameterizedTopLevelType() throws Exception {
    final MetaClass parameterized = getMetaClass(ParameterizedClass.class);
    assertEquals("ParameterizedClass", parameterized.getName());
    assertEquals("org.jboss.errai.codegen.test.model.ParameterizedClass", parameterized.getFullyQualifiedName());
    assertNull(parameterized.getParameterizedType());

    // I think this would be correct, but right now we get the raw type name
    //assertEquals("org.jboss.errai.codegen.test.model.ParameterizedClass<T>", parameterized.getFullyQualifiedNameWithTypeParms());

    assertEquals("org.jboss.errai.codegen.test.model.ParameterizedClass", parameterized.getErased().getFullyQualifiedNameWithTypeParms());
  }

  @Test
  public void testMethodObjectReturnType() {
    final MetaClass c = getMetaClass(ClassWithGenericMethods.class);
    final MetaMethod method = c.getMethod("methodReturningObject", new Class[] {});
    assertEquals("java.lang.Object", method.getReturnType().getFullyQualifiedNameWithTypeParms());
    assertEquals(getTypeOfMetaClassBeingTested(), method.getReturnType().getClass());

    // the generic return type should be the same: plain old Object
    assertEquals(getMetaClass(Object.class), method.getGenericReturnType());
  }

  @Test
  public void testMethodReturnTypeWithWildcardParameter() {
    final MetaClass c = getMetaClass(ClassWithGenericMethods.class);
    final MetaMethod method = c.getMethod("methodReturningUnboundedWildcardCollection", new Class[] {});

    // TODO (ERRAI-459) decide whether it's correct to have the type param present or not
    // then adjust this assertion to strict equality rather than startsWith()
    assertTrue(method.getReturnType().getFullyQualifiedNameWithTypeParms().startsWith("java.util.Collection"));

    final MetaType genericReturnType = method.getGenericReturnType();
    assertNotNull(genericReturnType);
    assertTrue("Got unexpected return type type " + genericReturnType.getClass(),
            genericReturnType instanceof MetaParameterizedType);
    final MetaParameterizedType mpReturnType = (MetaParameterizedType) genericReturnType;
    assertEquals(1, mpReturnType.getTypeParameters().length);

    // Sole type parameter should be <?>
    assertTrue(mpReturnType.getTypeParameters()[0] instanceof MetaWildcardType);
    final MetaWildcardType typeParam = (MetaWildcardType) mpReturnType.getTypeParameters()[0];
    assertArrayEquals(new MetaType[] {}, typeParam.getLowerBounds());
    assertArrayEquals(new MetaType[] { getMetaClass(Object.class) }, typeParam.getUpperBounds());
  }

  @Test
  public void testMethodReturnTypeWithUpperBoundedWildcardParameter() {
    final MetaClass c = getMetaClass(ClassWithGenericMethods.class);
    final MetaMethod method = c.getMethod("methodReturningUpperBoundedWildcardCollection", new Class[] {});

    // TODO (ERRAI-459) decide whether it's correct to have the type param present or not
    // then adjust this assertion to strict equality rather than startsWith()
    assertTrue(method.getReturnType().getFullyQualifiedNameWithTypeParms().startsWith("java.util.Collection"));

    final MetaType genericReturnType = method.getGenericReturnType();
    assertNotNull(genericReturnType);
    assertTrue("Got unexpected return type type " + genericReturnType.getClass(),
            genericReturnType instanceof MetaParameterizedType);
    final MetaParameterizedType mpReturnType = (MetaParameterizedType) genericReturnType;
    assertEquals(1, mpReturnType.getTypeParameters().length);

    // Sole type parameter should be <? extends String>
    assertTrue(mpReturnType.getTypeParameters()[0] instanceof MetaWildcardType);
    final MetaWildcardType typeParam = (MetaWildcardType) mpReturnType.getTypeParameters()[0];
    assertArrayEquals(new MetaType[] {}, typeParam.getLowerBounds());
    assertArrayEquals(new MetaType[] { getMetaClass(String.class) }, typeParam.getUpperBounds());
  }

  @Test
  public void testMethodReturnTypeWithLowerBoundedWildcardParameter() {
    final MetaClass c = getMetaClass(ClassWithGenericMethods.class);
    final MetaMethod method = c.getMethod("methodReturningLowerBoundedWildcardCollection", new Class[] {});

    // TODO (ERRAI-459) decide whether it's correct to have the type param present or not
    // then adjust this assertion to strict equality rather than startsWith()
    assertTrue(method.getReturnType().getFullyQualifiedNameWithTypeParms().startsWith("java.util.Collection"));

    final MetaType genericReturnType = method.getGenericReturnType();
    assertNotNull(genericReturnType);
    assertTrue("Got unexpected return type type " + genericReturnType.getClass(),
            genericReturnType instanceof MetaParameterizedType);
    final MetaParameterizedType mpReturnType = (MetaParameterizedType) genericReturnType;
    assertEquals(1, mpReturnType.getTypeParameters().length);

    // Sole type parameter should be <? extends String>
    assertTrue(mpReturnType.getTypeParameters()[0] instanceof MetaWildcardType);
    final MetaWildcardType typeParam = (MetaWildcardType) mpReturnType.getTypeParameters()[0];
    assertArrayEquals(new MetaType[] { getMetaClass(String.class) }, typeParam.getLowerBounds());
    assertArrayEquals(new MetaType[] { getMetaClass(Object.class)}, typeParam.getUpperBounds());
  }

  @Test
  public void testGetMethods() {
    final MetaClass c = getMetaClass(Child.class);
    final MetaMethod[] methods = c.getMethods();

    assertNotNull(methods);

    final List<String> methodSignatures = new ArrayList<>();
    for(final MetaMethod m : methods) {
      methodSignatures.add(GenUtil.getMethodString(m));
    }

    final List<String> expectedMethods = new ArrayList<>();
    expectedMethods.add("protectedMethod([])");
    expectedMethods.add("interfaceMethodOverriddenMultipleTimes([])");
    expectedMethods.add("packagePrivateMethod([])");
    expectedMethods.add("finalize([])");
    expectedMethods.add("equals([java.lang.Object])");
    expectedMethods.add("toString([])");
    expectedMethods.add("notify([])");
    expectedMethods.add("wait([])");
    expectedMethods.add("clone([])");
    expectedMethods.add("notifyAll([])");
    expectedMethods.add("getClass([])");
    expectedMethods.add("wait([long])");
    expectedMethods.add("hashCode([])");
    expectedMethods.add("wait([long, int])");

    Collections.sort(expectedMethods);
    Collections.sort(methodSignatures);


    assertEquals(expectedMethods.toString(), methodSignatures.toString());
  }

  @Test
  public void testGetFields() {
    final List<String> expectedFields = Lists.newLinkedList();
    expectedFields.add(Child.class.getCanonicalName() + "." + "childPublic");
    expectedFields.add(Parent.class.getCanonicalName() + "." + "parentPublic");

    final ArrayList<String> actualFields = new ArrayList<>();
    for (final MetaField field : getMetaClass(Child.class).getFields()) {
      actualFields.add(field.getDeclaringClass().getCanonicalName() + "." + field.getName());
    }

    Collections.sort(expectedFields);
    Collections.sort(actualFields);

    assertEquals(expectedFields.toString(), actualFields.toString());
  }

  @Test
  public void testGetDeclaredFields() {
    final List<String> expectedFields = Lists.newLinkedList();
    expectedFields.add(Child.class.getCanonicalName() + "." + "childPrivate");
    expectedFields.add(Child.class.getCanonicalName() + "." + "childPackage");
    expectedFields.add(Child.class.getCanonicalName() + "." + "childProtected");
    expectedFields.add(Child.class.getCanonicalName() + "." + "childPublic");

    final ArrayList<String> actualFields = new ArrayList<>();
    for (final MetaField field : getMetaClass(Child.class).getDeclaredFields()) {
      actualFields.add(field.getDeclaringClass().getCanonicalName() + "." + field.getName());
    }

    Collections.sort(expectedFields);
    Collections.sort(actualFields);

    assertEquals(expectedFields.toString(), actualFields.toString());
  }

  @Test
  public void testGetConstructors() throws Exception {
    final MetaClass mc = getMetaClass(HasManyConstructors.class);
    final MetaConstructor[] ctors = mc.getConstructors();
    assertEquals("Expected only a single visible constructor to be returned.", 1, ctors.length);
  }

  @Test
  public void testGetConstructorOnlyFindsPublicConstructor() throws Exception {
    final MetaClass mc = getMetaClass(HasManyConstructors.class);
    assertNotNull(mc.getConstructor(new Class[0]));
    assertNull(mc.getConstructor(new Class[] {int.class}));
    assertNull(mc.getConstructor(new Class[] {String.class}));
    assertNull(mc.getConstructor(new Class[] {double.class}));
  }

  @Test
  public void testGetDeclaredConstructors() throws Exception {
    final MetaClass mc = getMetaClass(HasManyConstructors.class);
    final MetaConstructor[] ctors = mc.getDeclaredConstructors();
    assertEquals("Not all constructors were returned.", 4, ctors.length);
  }

  @Test
  public void testGetDeclaredConstructorFindsAllConstructors() throws Exception {
    final MetaClass mc = getMetaClass(HasManyConstructors.class);
    assertNotNull(mc.getDeclaredConstructor(new Class[0]));
    assertNotNull(mc.getDeclaredConstructor(new Class[] {int.class}));
    assertNotNull(mc.getDeclaredConstructor(new Class[] {String.class}));
    assertNotNull(mc.getDeclaredConstructor(new Class[] {double.class}));
  }

  @Test
  public void testHashCodeErrorWithGenericHavingArrayUpperBound() throws Exception {
    final MetaClass mc = getMetaClass(ClassWithArrayGenerics.class).getField("field").getType();
    // Precondition
    assertNotNull("Failed to find field with type under test.", mc);
    assertNotNull("Type should be parameterized", mc.getParameterizedType());
    final MetaClass superClassWithProblematicBound = mc.getSuperClass();
    try {
      superClassWithProblematicBound.hashCode();
    } catch (final Throwable t) {
      throw new AssertionError("An error occurred.", t);
    }
  }

}
