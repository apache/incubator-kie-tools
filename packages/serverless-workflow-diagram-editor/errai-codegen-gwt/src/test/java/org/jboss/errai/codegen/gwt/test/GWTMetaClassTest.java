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


package org.jboss.errai.codegen.gwt.test;

import java.io.File;

import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.impl.gwt.GWTClass;
import org.jboss.errai.codegen.test.meta.AbstractMetaClassTest;
import org.jboss.errai.codegen.test.model.PrimitiveFieldContainer;

/**
 * The GWT implementation of the overall MetaClass test. Inherits all the tests
 * from AbstractMetaClassTest and runs them against GWTClass. Don't remove this
 * test! It actually does something!
 *
 * @author Mike Brock
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class GWTMetaClassTest extends AbstractMetaClassTest {

  private static final TypeOracle mockacle;
  static {
    final MockacleFactory f = new MockacleFactory(new File("../errai-codegen/src/test/java"));
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.Child");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.Grandparent");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.GrandparentInterface");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.GrandparentSuperInterface");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.IsolatedInterface");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.Parent");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.ParentInterface");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.ParentSuperInterface1");
    f.addTestClass("org.jboss.errai.codegen.test.model.tree.ParentSuperInterface2");
    f.addTestClass("org.jboss.errai.codegen.test.model.TestInterface");
    f.addTestClass("org.jboss.errai.codegen.test.model.ObjectWithNested");
    f.addTestClass("org.jboss.errai.codegen.test.model.ClassWithGenericCollections");
    f.addTestClass("org.jboss.errai.codegen.test.model.ParameterizedClass");
    f.addTestClass("org.jboss.errai.codegen.test.model.ClassWithGenericMethods");
    f.addTestClass("org.jboss.errai.codegen.test.model.HasManyConstructors");
    f.addTestClass("org.jboss.errai.codegen.test.model.GenericSuperClass");
    f.addTestClass("org.jboss.errai.codegen.test.model.GenericArraySubclass");
    f.addTestClass("org.jboss.errai.codegen.test.model.ClassWithArrayGenerics");
    f.addTestClass(PrimitiveFieldContainer.class.getName());

    mockacle = f.generateMockacle();
  }

  @Override
  protected MetaClass getMetaClassImpl(Class<?> javaClass) {

    int dims = 0;
    while (javaClass.isArray()) {
      javaClass = javaClass.getComponentType();
      dims++;
    }

    MetaClass metaClass;
    if (javaClass.isPrimitive()) {
      // This is a hack for getting a JType for a primitive
      // (I couldn't find any Source implementation that does it directly)
      final MetaClass container = GWTClass.newInstance(mockacle, PrimitiveFieldContainer.class.getName());
      metaClass = container.getDeclaredField(javaClass.getName() + "Field").getType();
    }
    else {
      metaClass = GWTClass.newInstance(mockacle, javaClass.getName());
    }

    if (metaClass == null) {
      throw new RuntimeException("Oops, the mock TypeOracle doesn't know about " + javaClass);
    }

    if (dims > 0) {
      metaClass = metaClass.asArrayOf(dims);
    }

    return metaClass;
  }

  @Override
  protected Class<? extends MetaClass> getTypeOfMetaClassBeingTested() {
    return GWTClass.class;
  }

  // NOTE: all of the test methods are inherited from AbstractMetaClassTest
}
