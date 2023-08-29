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


package org.jboss.errai.ioc.tests.wiring.client;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.ClientBeanManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCEnvironment;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.rebind.ioc.test.harness.IOCSimulatedTestRunner;
import org.jboss.errai.ioc.tests.wiring.client.res.ActivatedBean;
import org.jboss.errai.ioc.tests.wiring.client.res.ActivatedBeanInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.AfterTask;
import org.jboss.errai.ioc.tests.wiring.client.res.AppScopedBeanInvokingSelf;
import org.jboss.errai.ioc.tests.wiring.client.res.AppScopedWithPreDestroy;
import org.jboss.errai.ioc.tests.wiring.client.res.ApplicationScopedBeanInheritingPreDestroy;
import org.jboss.errai.ioc.tests.wiring.client.res.BeanManagerDependentBean;
import org.jboss.errai.ioc.tests.wiring.client.res.BeforeTask;
import org.jboss.errai.ioc.tests.wiring.client.res.ChildWithSetterOverride;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentBeanWithConstructorCycle;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentOnInnerType;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentWithPackageConstr;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentWithPrivateConstr;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentWithProtectedConstr;
import org.jboss.errai.ioc.tests.wiring.client.res.FieldProducedSelf;
import org.jboss.errai.ioc.tests.wiring.client.res.FieldProducedSimpleton;
import org.jboss.errai.ioc.tests.wiring.client.res.HappyInspector;
import org.jboss.errai.ioc.tests.wiring.client.res.IfaceProducer;
import org.jboss.errai.ioc.tests.wiring.client.res.ProxiableInjectableConstr;
import org.jboss.errai.ioc.tests.wiring.client.res.ProxiableInjectableConstrThrowsNPE;
import org.jboss.errai.ioc.tests.wiring.client.res.ProxiableNonPublicPostconstruct;
import org.jboss.errai.ioc.tests.wiring.client.res.ProxiableProtectedConstr;
import org.jboss.errai.ioc.tests.wiring.client.res.PublicInnerClassIface;
import org.jboss.errai.ioc.tests.wiring.client.res.QualForProducedTypeBean;
import org.jboss.errai.ioc.tests.wiring.client.res.QualForTypedBean;
import org.jboss.errai.ioc.tests.wiring.client.res.QualInspector;
import org.jboss.errai.ioc.tests.wiring.client.res.SetterInjectionBean;
import org.jboss.errai.ioc.tests.wiring.client.res.SimpleBean;
import org.jboss.errai.ioc.tests.wiring.client.res.SimpleBean2;
import org.jboss.errai.ioc.tests.wiring.client.res.SimpleSingleton;
import org.jboss.errai.ioc.tests.wiring.client.res.SimpleSingleton2;
import org.jboss.errai.ioc.tests.wiring.client.res.StaticProducerOfSelf;
import org.jboss.errai.ioc.tests.wiring.client.res.StaticProducerOfSelfSimpleton;
import org.jboss.errai.ioc.tests.wiring.client.res.TestBeanActivator;
import org.jboss.errai.ioc.tests.wiring.client.res.TestProviderDependentBean;
import org.jboss.errai.ioc.tests.wiring.client.res.TestResultsSingleton;
import org.jboss.errai.ioc.tests.wiring.client.res.TransverseDepService;
import org.jboss.errai.ioc.tests.wiring.client.res.TypeWithJustAFieldProducer;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedBaseType;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedSuperInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedTargetInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedType;
import org.junit.runner.RunWith;

@RunWith(IOCSimulatedTestRunner.class)
public class BasicIOCTest extends IOCClientTestCase {

  static {
    // Force classloading of SimpleBean so the package is discovered.
    @SuppressWarnings("unused")
    final
    Class<?> cls = SimpleBean.class;
  }

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.wiring.IOCWiringTests";
  }

  public void testBasicInjectionScenarios() {
    final SimpleSingleton simpleSingleton = IOC.getBeanManager().lookupBean(SimpleSingleton.class).getInstance();
    final SimpleSingleton2 simpleSingleton2 = IOC.getBeanManager().lookupBean(SimpleSingleton2.class).getInstance();

    assertNotNull(simpleSingleton);
    assertNotNull(simpleSingleton2);

    final SimpleBean simpleBean = IOC.getBeanManager().lookupBean(SimpleBean.class).getInstance();
    assertNotNull(simpleBean);

    assertEquals(simpleSingleton, simpleBean.getSingletonA());
    assertEquals(simpleSingleton, simpleBean.getSingletonB());
    assertEquals(simpleSingleton, simpleBean.getSingletonC());
    assertEquals(simpleSingleton, simpleBean.getSuperSimpleSingleton());
    assertEquals(simpleSingleton2, simpleBean.getSingleton2());

    final TransverseDepService transverseDepService = IOC.getBeanManager().lookupBean(TransverseDepService.class).getInstance();

    assertNotNull("svcA is null", simpleBean.getSvcA());
    assertNotNull("svcB is null", simpleBean.getSvcB());
    assertTrue("injection of TransverseDepService into svcA returned different instance!",
            simpleBean.getSvcA().getSvc() == transverseDepService);

    assertTrue("injection of TransverseDepService into svcB returned different instance!",
                simpleBean.getSvcB().getSvc() == transverseDepService);

    assertTrue("@PostConstruct method not called", simpleBean.isPostConstructCalled());
  }

  public void testNewInstanceFromSingleton() {
    final SimpleSingleton simpleSingleton = IOC.getBeanManager().lookupBean(SimpleSingleton.class).getInstance();
    final SimpleSingleton2 simpleSingleton2 = IOC.getBeanManager().lookupBean(SimpleSingleton2.class).getInstance();


    assertNotNull(simpleSingleton);
    assertNotNull(simpleSingleton2);

    final SimpleBean simpleBean1 = IOC.getBeanManager().lookupBean(SimpleBean.class).getInstance();
    assertNotNull(simpleBean1);

    final SimpleBean simpleBean2 = IOC.getBeanManager().lookupBean(SimpleBean.class).newInstance();

    assertNotSame("should have gotten new instance", simpleBean1, simpleBean2);

    assertEquals(simpleSingleton, simpleBean2.getSingletonA());
    assertEquals(simpleSingleton, simpleBean2.getSingletonB());
    assertEquals(simpleSingleton, simpleBean2.getSingletonC());
    assertEquals(simpleSingleton, simpleBean2.getSuperSimpleSingleton());
    assertEquals(simpleSingleton2, simpleBean2.getSingleton2());

    final TransverseDepService transverseDepService = IOC.getBeanManager().lookupBean(TransverseDepService.class).getInstance();

    assertNotNull("svcA is null", simpleBean2.getSvcA());
    assertNotNull("svcB is null", simpleBean2.getSvcB());
    assertTrue("injection of TransverseDepService into svcA returned different instance!",
            simpleBean2.getSvcA().getSvc() == transverseDepService);

    assertTrue("injection of TransverseDepService into svcB returned different instance!",
                simpleBean2.getSvcB().getSvc() == transverseDepService);

    assertTrue("@PostConstruct method not called", simpleBean2.isPostConstructCalled());
  }

  public void testSetterMethodInjection() {
    final SetterInjectionBean bean = IOC.getBeanManager().lookupBean(SetterInjectionBean.class)
            .getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getServiceA());
    assertNotNull(bean.getServiceB());
  }

  public void testInjectionFromProvider() {
    final SimpleBean2 simpleBean2 = IOC.getBeanManager().lookupBean(SimpleBean2.class).getInstance();

    assertEquals("FOO", simpleBean2.getMessage());
  }

  public void testInjectionFromProviderContextual() {
    final SimpleBean2 simpleBean2 = IOC.getBeanManager().lookupBean(SimpleBean2.class).getInstance();

    assertEquals("FOO", simpleBean2.getMessage());
    assertEquals("java.lang.String", simpleBean2.getbSvc().get());
  }

  public void testInterfaceResolution() {
    final HappyInspector happyInspector = IOC.getBeanManager().lookupBean(HappyInspector.class).getInstance();
    assertTrue(happyInspector.confirmHappiness());

    assertNotNull(happyInspector.getStringService());
    assertEquals("Hello", happyInspector.getStringService().get());

    assertNotNull(happyInspector.getIntegerService());
    assertEquals(new Integer(111), happyInspector.getIntegerService().get());

    assertNotNull(happyInspector.getLongService());
    assertEquals(new Long(1l), happyInspector.getLongService().get());
  }

  public void testQualifiers() {
    final QualInspector qualInspector = QualInspector.INSTANCE;

    assertTrue(qualInspector.getaQualService().get() instanceof Integer);
    assertTrue(qualInspector.getbQualService().get() instanceof String);
  }

  public void testIOCTasks() {
    assertTrue("BeforeTask did not run", BeforeTask.ran);
    assertTrue("AfterTask did not run", AfterTask.ran);

    final List<Class<?>> results = TestResultsSingleton.getItemsRun();
    assertTrue("BeforeTask did not run before AfterTask!",
            results.indexOf(BeforeTask.class) < results.indexOf(AfterTask.class));

  }

  public void testBeanManagerInjectable() {
    final BeanManagerDependentBean bean = IOC.getBeanManager().lookupBean(BeanManagerDependentBean.class)
            .getInstance();

    final ClientBeanManager beanManager = (GWT.<IOCEnvironment>create(IOCEnvironment.class).isAsync() ? IOC.getAsyncBeanManager() : IOC.getBeanManager());
    assertSame(beanManager, Factory.maybeUnwrapProxy(bean.getBeanManager()));
  }

  public void testProvidedValueLookup() {
    final TestProviderDependentBean dependentBean = IOC.getBeanManager().lookupBean(TestProviderDependentBean.class)
        .getInstance();

    assertNotNull(dependentBean);
    assertNotNull(dependentBean.getTestProvidedIface());
    assertEquals("foo", dependentBean.getTestProvidedIface().getText());
  }

  public void testBeanActivator() {
    final TestBeanActivator activator = IOC.getBeanManager().lookupBean(TestBeanActivator.class).getInstance();
    activator.setActived(true);

    final SyncBeanDef<ActivatedBean> bean = IOC.getBeanManager().lookupBean(ActivatedBean.class);
    assertTrue(bean.isActivated());

    activator.setActived(false);
    assertFalse(bean.isActivated());

    final SyncBeanDef<ActivatedBeanInterface> qualifiedBean = IOC.getBeanManager().lookupBean(ActivatedBeanInterface.class);
    assertFalse(qualifiedBean.isActivated());

    activator.setActived(true);
    assertTrue(qualifiedBean.isActivated());
  }

  public void testBeanActiveByDefault() {
    final SyncBeanDef<BeanManagerDependentBean> bean = IOC.getBeanManager().lookupBean(BeanManagerDependentBean.class);
    assertTrue(bean.isActivated());
  }

  public void testInjectingStaticInnerClass() {
    final DependentOnInnerType instance = IOC.getBeanManager().lookupBean(DependentOnInnerType.class).getInstance();
    assertNotNull(instance.getInner());
  }

  public void testLoadingBeanWithProxiableNonPublicPostConstruct() {
    final ProxiableNonPublicPostconstruct bean = IOC.getBeanManager().lookupBean(ProxiableNonPublicPostconstruct.class).getInstance();
    assertTrue(bean.getValue());
  }

  public void testProxyingWithProtectedConstructor() throws Exception {
    try {
      assertEquals("Package constructor should not have been invoked before instance lookup.", 0, ProxiableProtectedConstr.numPrivateConstrInvocations);
      IOC.getBeanManager().lookupBean(ProxiableProtectedConstr.class).getInstance();
      assertEquals("Private constructor was not used to create proxy.", 1, ProxiableProtectedConstr.numPrivateConstrInvocations);
    } catch (final Throwable t) {
      throw new AssertionError("Could not construct instance of proxiable type with protected constructor.", t);
    }
  }

  public void testProxyingWithOnlyInjectableConstructor() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(ProxiableInjectableConstr.class).getInstance();
    } catch (final Throwable t) {
      throw new AssertionError("Could not construct instance of proxiable type with injectable constructor.", t);
    }
  }

  public void testErrorMessageWhenProxyingFailsWithOnlyInjectableConstructor() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(ProxiableInjectableConstrThrowsNPE.class).getInstance();
      fail("Looking up an instance should have failed when creating proxy.");
    } catch (final Throwable t) {
      assertTrue("The error message did not explain that the problem was with proxying.", t.getMessage().contains("proxy"));
    }
  }

  public void testDependentScopeWithPrivateConstr() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(DependentWithPrivateConstr.class).getInstance();
    } catch (final Throwable t) {
      throw new AssertionError("Could not create instance of bean with private constructor.", t);
    }
  }

  public void testDependentScopeWithPackageConstr() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(DependentWithPackageConstr.class).getInstance();
    } catch (final Throwable t) {
      throw new AssertionError("Could not create instance of bean with package constructor.", t);
    }
  }

  public void testDependentScopeWithProtectedConstr() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(DependentWithProtectedConstr.class).getInstance();
    } catch (final Throwable t) {
      throw new AssertionError("Could not create instance of bean with protected constructor.", t);
    }
  }

  public void testNoFactoryGeneratedForInnerClassOfNonPublicClass() throws Exception {
    final Collection<SyncBeanDef<PublicInnerClassIface>> foundBeans = IOC.getBeanManager().lookupBeans(PublicInnerClassIface.class, QualifierUtil.ANY_ANNOTATION);
    assertEquals(0, foundBeans.size());
  }

  /*
   * This test was broken by this change in GWT:
   * https://github.com/gwtproject/gwt/commit/75382f1202bf3eaa399d60ebdba42bd7522da3bb
   */
  public void ignoreInterfaceStaticProducer() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(IfaceProducer.class).getInstance();
    } catch (final IOCResolutionException ex) {
      throw new AssertionError("Could not produce " + IfaceProducer.class.getSimpleName(), ex);
    }
  }

  public void testAppScopedBeanInvokingSelfInPostConstruct() throws Exception {
    final AppScopedBeanInvokingSelf instance = IOC.getBeanManager().lookupBean(AppScopedBeanInvokingSelf.class).getInstance();
    assertEquals("foo", instance.getValue());
  }

  // Regression test for ERRAI-959
  public void testDependentBeanWithConstructorInjectionCausingCycyleDoesNotBlowUp() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(DependentBeanWithConstructorCycle.class).getInstance();
    } catch (final RuntimeException e) {
      throw new AssertionError("Could not lookup the dependent bean without error: " + e.getMessage(), e);
    }
  }

  // Regression test for ERRAI-994
  public void testOverrideOfInjectSetterMethod() throws Exception {
    final ChildWithSetterOverride bean = IOCUtil.getInstance(ChildWithSetterOverride.class);
    assertEquals("AQual", bean.dep.value);
  }

  // Regression test for ERRAI-1002
  public void testApplicationScopedSubTypeWithInheritedPreDestroy() throws Exception {
    // Just test that we can look it up. Original issue causes compilation failure.
    IOC.getBeanManager().lookupBean(ApplicationScopedBeanInheritingPreDestroy.class).getInstance();
  }

  public void testStaticProducerMethodOfOwnType() throws Exception {
    try {
      IOCUtil.getInstance(StaticProducerOfSelf.class);
    } catch (final Throwable t) {
      throw new AssertionError("Unable to lookup " + StaticProducerOfSelf.class.getSimpleName(), t);
    }
  }

  public void testStaticProducerMethodOfSimpletonOverridesSimpleton() throws Exception {
    try {
      final StaticProducerOfSelfSimpleton bean = IOCUtil.getInstance(StaticProducerOfSelfSimpleton.class);
      assertTrue("Bean was not created by producer method.", bean.produced);
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError("Unable to lookup " + StaticProducerOfSelfSimpleton.class.getSimpleName(), t);
    }
  }

  public void testStaticProducerFieldOfOwnType() throws Exception {
    try {
      IOCUtil.getInstance(FieldProducedSelf.class);
    } catch (final Throwable t) {
      throw new AssertionError("Unable to lookup " + FieldProducedSelf.class.getSimpleName(), t);
    }
  }

  public void testStaticProducerFieldOfSimpletonOverridesSimpleton() throws Exception {
    try {
      final FieldProducedSimpleton bean = IOCUtil.getInstance(FieldProducedSimpleton.class);
      assertSame("Bean was not from producer field.", FieldProducedSimpleton.instance, bean);
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError("Unable to lookup " + FieldProducedSimpleton.class.getSimpleName(), t);
    }
  }

  public void testNonStaticProducerFieldDetectedWhenNoOtherIocAnnotations() throws Exception {
    try {
      // Sanity check that the type with the producer field is a bean
      IOCUtil.getInstance(TypeWithJustAFieldProducer.class);
      // The real test: that the producer field is a bean
      IOCUtil.getInstance(TypeWithJustAFieldProducer.ProducedType.class);
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError(t);
    }
  }

  public void testDestoryingApplicationScopedBean() throws Exception {
    final AppScopedWithPreDestroy proxiedInstance1 = IOCUtil.getInstance(AppScopedWithPreDestroy.class);
    final AppScopedWithPreDestroy instance1 = Factory.maybeUnwrapProxy(proxiedInstance1);

    assertEquals("PostConstruct for first instance was not called.",
            Collections.singletonList(instance1), AppScopedWithPreDestroy.createdInstances);
    assertEquals("@PreDestroy was prematurely called.", Collections.emptyList(), AppScopedWithPreDestroy.destroyedInstances);
    assertTrue("Instance returned from bean manager was not proxied.", proxiedInstance1 instanceof Proxy);

    IOCUtil.destroy(proxiedInstance1);
    assertEquals("Created and destroyed instances were not the same after destroy called on only instance.",
            AppScopedWithPreDestroy.createdInstances, AppScopedWithPreDestroy.destroyedInstances);

    final AppScopedWithPreDestroy proxiedInstance2 = IOCUtil.getInstance(AppScopedWithPreDestroy.class);
    final AppScopedWithPreDestroy instance2 = Factory.maybeUnwrapProxy(proxiedInstance2);

    assertSame("ApplicationScoped beans should reuse the same proxy.", proxiedInstance1, proxiedInstance2);
    assertNotSame("The instances should be different after the first was destroyed.", instance1, instance2);
    assertEquals("PostConstruct not called for second instance.", Arrays.asList(instance1, instance2),
            AppScopedWithPreDestroy.createdInstances);
    assertEquals("Only the PreDestroy for the first instance shoudl have been called.",
            Collections.singletonList(instance1), AppScopedWithPreDestroy.destroyedInstances);

    IOCUtil.destroy(proxiedInstance2);
    assertEquals("Created and destroyed instances were not the same after destroy called on both beans.",
            AppScopedWithPreDestroy.createdInstances, AppScopedWithPreDestroy.destroyedInstances);

    // Force creation of new instance through lazy-loading proxy mechanism
    final AppScopedWithPreDestroy instance3 = Factory.maybeUnwrapProxy(proxiedInstance2);
    assertEquals("New instance was not created by proxy lazy-loading mechanism",
            Arrays.asList(instance1, instance2, instance3), AppScopedWithPreDestroy.createdInstances);
  }

  public void testBeanWithTypedOnlyAvailableAsSpecifiedTypesAndObject() throws Exception {
    final QualForTypedBean qual = new QualForTypedBean() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return QualForTypedBean.class;
      }
    };
    final SyncBeanManager bm = IOC.getBeanManager();

    final Collection<SyncBeanDef<TypedType>> typedTypeBeans = bm.lookupBeans(TypedType.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + typedTypeBeans, 1, typedTypeBeans.size());
    assertEquals(TypedType.class, typedTypeBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedTargetInterface>> targetIfaceBeans = bm.lookupBeans(TypedTargetInterface.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + targetIfaceBeans, 1, targetIfaceBeans.size());
    assertEquals(TypedType.class, targetIfaceBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<Object>> objectBeans = bm.lookupBeans(Object.class, qual);
    assertEquals("Expected exactly one bean with type Object and qualifier QualForTypedBean. Found: " + objectBeans, 1, objectBeans.size());
    assertEquals(TypedType.class, objectBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedBaseType>> baseTypeBeans = bm.lookupBeans(TypedBaseType.class, qual);
    assertTrue("There should be no beans of type TypedBaseType. Found : " + baseTypeBeans, baseTypeBeans.isEmpty());

    final Collection<SyncBeanDef<TypedSuperInterface>> superIfaceBeans = bm.lookupBeans(TypedSuperInterface.class, qual);
    assertTrue("There should be no beans of type TypedSuperInterface. Found : " + superIfaceBeans, superIfaceBeans.isEmpty());
  }

  public void testStaticMethodProducedBeanWithTypedOnlyAvailableAsSpecifiedTypesAndObject() throws Exception {
    final QualForProducedTypeBean qual = new QualForProducedTypeBean() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return QualForProducedTypeBean.class;
      }

      @Override
      public boolean isStatic() {
        return true;
      }

      @Override
      public ProducerType type() {
        return ProducerType.METHOD;
      }
    };

    final SyncBeanManager bm = IOC.getBeanManager();

    final Collection<SyncBeanDef<TypedType>> typedTypeBeans = bm.lookupBeans(TypedType.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + typedTypeBeans, 1, typedTypeBeans.size());
    assertEquals(TypedType.class, typedTypeBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedTargetInterface>> targetIfaceBeans = bm.lookupBeans(TypedTargetInterface.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + targetIfaceBeans, 1, targetIfaceBeans.size());
    assertEquals(TypedType.class, targetIfaceBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<Object>> objectBeans = bm.lookupBeans(Object.class, qual);
    assertEquals("Expected exactly one bean with type Object and qualifier QualForTypedBean. Found: " + objectBeans, 1, objectBeans.size());
    assertEquals(TypedType.class, objectBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedBaseType>> baseTypeBeans = bm.lookupBeans(TypedBaseType.class, qual);
    assertTrue("There should be no beans of type TypedBaseType. Found : " + baseTypeBeans, baseTypeBeans.isEmpty());

    final Collection<SyncBeanDef<TypedSuperInterface>> superIfaceBeans = bm.lookupBeans(TypedSuperInterface.class, qual);
    assertTrue("There should be no beans of type TypedSuperInterface. Found : " + superIfaceBeans, superIfaceBeans.isEmpty());
  }

  public void testStaticFieldProducedBeanWithTypedOnlyAvailableAsSpecifiedTypesAndObject() throws Exception {
    final QualForProducedTypeBean qual = new QualForProducedTypeBean() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return QualForProducedTypeBean.class;
      }

      @Override
      public boolean isStatic() {
        return true;
      }

      @Override
      public ProducerType type() {
        return ProducerType.FIELD;
      }
    };

    final SyncBeanManager bm = IOC.getBeanManager();

    final Collection<SyncBeanDef<TypedType>> typedTypeBeans = bm.lookupBeans(TypedType.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + typedTypeBeans, 1, typedTypeBeans.size());
    assertEquals(TypedType.class, typedTypeBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedTargetInterface>> targetIfaceBeans = bm.lookupBeans(TypedTargetInterface.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + targetIfaceBeans, 1, targetIfaceBeans.size());
    assertEquals(TypedType.class, targetIfaceBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<Object>> objectBeans = bm.lookupBeans(Object.class, qual);
    assertEquals("Expected exactly one bean with type Object and qualifier QualForTypedBean. Found: " + objectBeans, 1, objectBeans.size());
    assertEquals(TypedType.class, objectBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedBaseType>> baseTypeBeans = bm.lookupBeans(TypedBaseType.class, qual);
    assertTrue("There should be no beans of type TypedBaseType. Found : " + baseTypeBeans, baseTypeBeans.isEmpty());

    final Collection<SyncBeanDef<TypedSuperInterface>> superIfaceBeans = bm.lookupBeans(TypedSuperInterface.class, qual);
    assertTrue("There should be no beans of type TypedSuperInterface. Found : " + superIfaceBeans, superIfaceBeans.isEmpty());
  }

  public void testInstanceFieldProducedBeanWithTypedOnlyAvailableAsSpecifiedTypesAndObject() throws Exception {
    final QualForProducedTypeBean qual = new QualForProducedTypeBean() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return QualForProducedTypeBean.class;
      }

      @Override
      public boolean isStatic() {
        return false;
      }

      @Override
      public ProducerType type() {
        return ProducerType.FIELD;
      }
    };

    final SyncBeanManager bm = IOC.getBeanManager();

    final Collection<SyncBeanDef<TypedType>> typedTypeBeans = bm.lookupBeans(TypedType.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + typedTypeBeans, 1, typedTypeBeans.size());
    assertEquals(TypedType.class, typedTypeBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedTargetInterface>> targetIfaceBeans = bm.lookupBeans(TypedTargetInterface.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + targetIfaceBeans, 1, targetIfaceBeans.size());
    assertEquals(TypedType.class, targetIfaceBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<Object>> objectBeans = bm.lookupBeans(Object.class, qual);
    assertEquals("Expected exactly one bean with type Object and qualifier QualForTypedBean. Found: " + objectBeans, 1, objectBeans.size());
    assertEquals(TypedType.class, objectBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedBaseType>> baseTypeBeans = bm.lookupBeans(TypedBaseType.class, qual);
    assertTrue("There should be no beans of type TypedBaseType. Found : " + baseTypeBeans, baseTypeBeans.isEmpty());

    final Collection<SyncBeanDef<TypedSuperInterface>> superIfaceBeans = bm.lookupBeans(TypedSuperInterface.class, qual);
    assertTrue("There should be no beans of type TypedSuperInterface. Found : " + superIfaceBeans, superIfaceBeans.isEmpty());
  }

  public void testInstanceMethodProducedBeanWithTypedOnlyAvailableAsSpecifiedTypesAndObject() throws Exception {
    final QualForProducedTypeBean qual = new QualForProducedTypeBean() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return QualForProducedTypeBean.class;
      }

      @Override
      public boolean isStatic() {
        return false;
      }

      @Override
      public ProducerType type() {
        return ProducerType.METHOD;
      }
    };

    final SyncBeanManager bm = IOC.getBeanManager();

    final Collection<SyncBeanDef<TypedType>> typedTypeBeans = bm.lookupBeans(TypedType.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + typedTypeBeans, 1, typedTypeBeans.size());
    assertEquals(TypedType.class, typedTypeBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedTargetInterface>> targetIfaceBeans = bm.lookupBeans(TypedTargetInterface.class, qual);
    assertEquals("Expected exactly one bean with type TypedType. Found: " + targetIfaceBeans, 1, targetIfaceBeans.size());
    assertEquals(TypedType.class, targetIfaceBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<Object>> objectBeans = bm.lookupBeans(Object.class, qual);
    assertEquals("Expected exactly one bean with type Object and qualifier QualForTypedBean. Found: " + objectBeans, 1, objectBeans.size());
    assertEquals(TypedType.class, objectBeans.iterator().next().getBeanClass());

    final Collection<SyncBeanDef<TypedBaseType>> baseTypeBeans = bm.lookupBeans(TypedBaseType.class, qual);
    assertTrue("There should be no beans of type TypedBaseType. Found : " + baseTypeBeans, baseTypeBeans.isEmpty());

    final Collection<SyncBeanDef<TypedSuperInterface>> superIfaceBeans = bm.lookupBeans(TypedSuperInterface.class, qual);
    assertTrue("There should be no beans of type TypedSuperInterface. Found : " + superIfaceBeans, superIfaceBeans.isEmpty());
  }
}
