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


package org.jboss.errai.ioc.tests.beanmanager.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.ioc.tests.beanmanager.client.res.A;
import org.jboss.errai.ioc.tests.beanmanager.client.res.B;
import org.jboss.errai.ioc.tests.beanmanager.client.res.BeanWithManagedInstance;
import org.jboss.errai.ioc.tests.beanmanager.client.res.C;
import org.jboss.errai.ioc.tests.beanmanager.client.res.DefaultDependentBean;
import org.jboss.errai.ioc.tests.beanmanager.client.res.DestructableClass;
import org.jboss.errai.ioc.tests.beanmanager.client.res.OtherDestructableClass;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_ANNOTATION;
import static org.jboss.errai.ioc.client.container.IOC.getBeanManager;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ManagedInstanceTest extends AbstractErraiIOCTest {

  private static final A a = new A() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return A.class;
    }
  };

  private static final B b = new B() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return B.class;
    }
  };

  private static final C c = new C() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return C.class;
    }
  };

  private BeanWithManagedInstance module;

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.beanmanager.IOCBeanManagerTests";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    module = IOC.getBeanManager().lookupBean(BeanWithManagedInstance.class).getInstance();
  }

  public void testResolvesDefaultWithNoExplicitQualifiers() throws Exception {
    final List<DestructableClass> beans = fromIterator(module.defaultOnly.iterator());
    assertTrue(module.defaultOnly.isAmbiguous());
    assertEquals(2, beans.size());
    for (final DestructableClass instance : beans) {
      assertTrue(getSimpleName(instance) + " is not @Default but was resolved.", instance.isDefault());
    }
  }

  public void testResolvesAllWithAnyQualifier() throws Exception {
    final List<DestructableClass> beans = fromIterator(module.any.iterator());
    assertTrue(module.any.isAmbiguous());
    assertEquals(4, beans.size());
  }

  public void testDoesNotResolveDefaultWithOtherQualifier() throws Exception {
    final List<DestructableClass> beans = fromIterator(module.aOnly.iterator());
    assertTrue(module.aOnly.isAmbiguous());
    assertEquals(2, beans.size());
    for (final DestructableClass instance : beans) {
      assertFalse(getSimpleName(instance) + " is @Default but was resolved.", instance.isDefault());
    }
  }

  public void testDoesNotResolveAnythingForUnsatisfiableCombination() throws Exception {
    final List<DestructableClass> beans = fromIterator(module.unsatisfied.iterator());
    assertTrue(module.unsatisfied.isUnsatisfied());
    assertEquals(0, beans.size());
  }

  public void testDestroyAllDestroysCreatedDependentScopedBeans() throws Exception {
    final List<DestructableClass> beans = fromIterator(module.any.iterator());
    final List<DestructableClass> depBeans = getDependent(beans);
    final List<DestructableClass> normalBeans = getNormalScoped(beans);
    // Preconditions
    assertEquals(2, depBeans.size());
    assertEquals(2, normalBeans.size());
    for (final DestructableClass instance : beans) {
      assertFalse(getSimpleName(instance) + " was destroyed before destroyAll was called.", instance.isDestroyed());
    }

    // Test
    module.any.destroyAll();
    for (final DestructableClass instance : depBeans) {
      assertTrue(getSimpleName(instance) + " was not destroyed after destroyAll was called.", instance.isDestroyed());
    }
    for (final DestructableClass instance : normalBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed after destroyAll was called.", instance.isDestroyed());
    }
  }

  public void testSelectingQualifierResolution() throws Exception {
    final ManagedInstance<DestructableClass> selectedInstance = module.any.select(a);
    final List<DestructableClass> beans = fromIterator(selectedInstance.iterator());
    assertTrue(selectedInstance.isAmbiguous());
    assertEquals(2, beans.size());
    for (final DestructableClass instance : beans) {
      assertFalse(getSimpleName(instance) + " is @Default but was resolved.", instance.isDefault());
    }
  }

  public void testSelectingSubtypeResolution() throws Exception {
    final ManagedInstance<DefaultDependentBean> selectedInstance = module.any.select(DefaultDependentBean.class);
    final List<DefaultDependentBean> beans = fromIterator(selectedInstance.iterator());
    assertFalse(selectedInstance.isAmbiguous());
    assertFalse(selectedInstance.isUnsatisfied());
    assertEquals(1, beans.size());
    assertTrue(beans.iterator().next().isDependent());
    assertTrue(beans.iterator().next().isDefault());
  }

  public void testDestroyAllOnSelectedAnnotationDoesNotAffectInjectedOrOtherSelectedInstances() throws Exception {
    final ManagedInstance<DestructableClass> aInstance = module.any.select(a);
    final ManagedInstance<DestructableClass> defaultInstance = module.any.select(DEFAULT_ANNOTATION);
    final List<DestructableClass> aBeans = fromIterator(aInstance.iterator());
    final List<DestructableClass> defaultBeans = fromIterator(defaultInstance.iterator());
    final List<DestructableClass> anyBeans = fromIterator(module.any.iterator());
    for (final DestructableClass instance : aBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed before destroyAll was called.", instance.isDestroyed());
    }
    for (final DestructableClass instance : defaultBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed before destroyAll was called.", instance.isDestroyed());
    }
    for (final DestructableClass instance : anyBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed before destroyAll was called.", instance.isDestroyed());
    }

    defaultInstance.destroyAll();
    final List<DestructableClass> defaultDepBeans = getDependent(defaultBeans);
    final List<DestructableClass> defaultNormalBeans = getNormalScoped(defaultBeans);
    for (final DestructableClass instance : aBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed after destroyAll was called.", instance.isDestroyed());
    }
    for (final DestructableClass instance : anyBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed after destroyAll was called.", instance.isDestroyed());
    }
    for (final DestructableClass instance : defaultNormalBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed after destroyAll was called.", instance.isDestroyed());
    }
    for (final DestructableClass instance : defaultDepBeans) {
      assertTrue(getSimpleName(instance) + " was not destroyed after destroyAll was called.", instance.isDestroyed());
    }
  }

  public void testDestroyAllOnSelectedSubtypeDoesNotAffectInjectedOrOtherSelectedInstances() throws Exception {
    final ManagedInstance<DefaultDependentBean> defaultDepBeanInstance = module.any.select(DefaultDependentBean.class);
    assertFalse("ManagedInstance<DefaultDependentBean> should not be ambiguous.", defaultDepBeanInstance.isAmbiguous());
    assertFalse("ManagedInstance<DefaultDependentBean> should not be unsatisfied.", defaultDepBeanInstance.isUnsatisfied());
    final DefaultDependentBean subtypeInstance = defaultDepBeanInstance.get();
    assertFalse(getSimpleName(subtypeInstance) + " was destroyed before destroyAll was called.", subtypeInstance.isDestroyed());

    final List<DestructableClass> anyBeans = fromIterator(module.any.iterator());
    assertEquals(4, anyBeans.size());
    for (final DestructableClass instance : anyBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed before destroyAll was called.", instance.isDestroyed());
    }

    defaultDepBeanInstance.destroyAll();
    assertTrue(getSimpleName(subtypeInstance) + " was not destroyed after destroyAll was called.", subtypeInstance.isDestroyed());
    for (final DestructableClass instance : anyBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed after destroyAll was called on subtype instance.", instance.isDestroyed());
    }
  }

  public void testDestroyingBeanWithInjectedManagedInstanceDestroysAllCreatedInstances() throws Exception {
    // Setup
    final List<DestructableClass> anyBeans = fromIterator(module.any.iterator());
    final ManagedInstance<DestructableClass> aInstance = module.any.select(a);
    final List<DestructableClass> aBeans = fromIterator(aInstance.iterator());

    final List<DestructableClass> depBeans = getDependent(anyBeans);
    depBeans.addAll(getDependent(aBeans));

    final List<DestructableClass> normalBeans = getNormalScoped(anyBeans);
    normalBeans.addAll(getNormalScoped(aBeans));

    // Preconditions
    for (final DestructableClass instance : aBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed before module was destroyed.", instance.isDestroyed());
    }
    for (final DestructableClass instance : anyBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed before module was destroyed.", instance.isDestroyed());
    }

    // Test
    getBeanManager().destroyBean(module);
    assertTrue("The bean containing the injected managed instance was not destroyed.", module.isDestroyed());
    for (final DestructableClass instance : normalBeans) {
      assertFalse(getSimpleName(instance) + " was destroyed after module was destroyed.", instance.isDestroyed());
    }
    for (final DestructableClass instance : depBeans) {
      assertTrue(getSimpleName(instance) + " was not destroyed after module was destroyed.", instance.isDestroyed());
    }
  }

  public void testDestroyingProducedBeanOfApplicationScopedProducerUsingParamInjectedManagedInstance() throws Exception {
    final OtherDestructableClass producedBean = getBeanManager().lookupBean(OtherDestructableClass.class, a).getInstance();
    assertFalse(producedBean.isDestroyed());
    getBeanManager().destroyBean(producedBean);
    assertTrue(producedBean.isDestroyed());
  }

  public void testDestroyingProducedBeanOfDepdendentScopedProducerUsingParamInjectedManagedInstance() throws Exception {
    final OtherDestructableClass producedBean = getBeanManager().lookupBean(OtherDestructableClass.class, b).getInstance();
    assertFalse(producedBean.isDestroyed());
    getBeanManager().destroyBean(producedBean);
    assertTrue(producedBean.isDestroyed());
  }

  public void testDestroyingProducedBeanOfDependentScopedProducerUsingFieldInjectedManagedInstance() throws Exception {
    final OtherDestructableClass producedBean = getBeanManager().lookupBean(OtherDestructableClass.class, c).getInstance();
    assertFalse(producedBean.isDestroyed());
    getBeanManager().destroyBean(producedBean);
    assertTrue(producedBean.isDestroyed());
  }

  private static String getSimpleName(final DestructableClass instance) {
    return Factory.maybeUnwrapProxy(instance).getClass().getSimpleName();
  }

  private static <T> List<T> fromIterator(final Iterator<T> iter) {
    final List<T> list = new ArrayList<>();
    while (iter.hasNext()) {
      list.add(iter.next());
    }

    return list;
  }

  private static List<DestructableClass> getDependent(final List<DestructableClass> all) {
    final List<DestructableClass> list = new ArrayList<>();
    for (final DestructableClass instance : all) {
      if (instance.isDependent()) {
        list.add(instance);
      }
    }

    return list;
  }

  private static List<DestructableClass> getNormalScoped(final List<DestructableClass> all) {
    final List<DestructableClass> list = new ArrayList<>();
    for (final DestructableClass instance : all) {
      if (!instance.isDependent()) {
        list.add(instance);
      }
    }

    return list;
  }

}
