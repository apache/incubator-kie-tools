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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.inject.Named;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Timer;
import org.jboss.errai.ioc.client.JsArray;
import org.jboss.errai.ioc.client.WindowInjectionContext;
import org.jboss.errai.ioc.client.WindowInjectionContextStorage;
import org.jboss.errai.ioc.client.container.DynamicAnnotation;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.container.JsTypeProvider;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.ioc.tests.wiring.client.res.AlternativeImpl;
import org.jboss.errai.ioc.tests.wiring.client.res.ConcreteWindowScopedJsType;
import org.jboss.errai.ioc.tests.wiring.client.res.ConsumesProducedJsType;
import org.jboss.errai.ioc.tests.wiring.client.res.DuplicateInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.ExternalJsTypeIface;
import org.jboss.errai.ioc.tests.wiring.client.res.ExternalJsTypeImpl;
import org.jboss.errai.ioc.tests.wiring.client.res.ExternalTestModule;
import org.jboss.errai.ioc.tests.wiring.client.res.InternallySatisfiedImpl;
import org.jboss.errai.ioc.tests.wiring.client.res.InternallySatisfiedJsTypeIface;
import org.jboss.errai.ioc.tests.wiring.client.res.InternallyUnsatisfiedJsTypeIface;
import org.jboss.errai.ioc.tests.wiring.client.res.JsSubTypeWithDuplicateInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.JsSuperTypeWithDuplicateInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.JsTypeConsumer;
import org.jboss.errai.ioc.tests.wiring.client.res.JsTypeDependentBean;
import org.jboss.errai.ioc.tests.wiring.client.res.JsTypeDependentInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.JsTypeNamedBean;
import org.jboss.errai.ioc.tests.wiring.client.res.JsTypeSingletonBean;
import org.jboss.errai.ioc.tests.wiring.client.res.JsTypeSingletonInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.JsTypeWithQualifiers;
import org.jboss.errai.ioc.tests.wiring.client.res.MultipleImplementationsJsType;
import org.jboss.errai.ioc.tests.wiring.client.res.NativeConcreteJsType;
import org.jboss.errai.ioc.tests.wiring.client.res.NativeConcreteJsTypeWithConstructorDependency;
import org.jboss.errai.ioc.tests.wiring.client.res.NativeConcreteJsTypeWithFieldDependency;
import org.jboss.errai.ioc.tests.wiring.client.res.NativeTypeTestModule;
import org.jboss.errai.ioc.tests.wiring.client.res.ProducedJsType;
import org.jboss.errai.ioc.tests.wiring.client.res.QualWithMultiMembers;
import org.jboss.errai.ioc.tests.wiring.client.res.UnimplementedType;

public class JsTypeInjectionTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.wiring.IOCWiringTests";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    WindowInjectionContextStorage.reset();
    final WindowInjectionContext windowInjContext = WindowInjectionContextStorage.createOrGet();
    windowInjContext.addBeanProvider(MultipleImplementationsJsType.class.getName(), new JsTypeProvider<Object>() {
      @Override
      public Object getInstance() {
        return new AlternativeImpl(1);
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public String getFactoryName() {
        return null;
      }

      @Override
      public JsArray<String> getQualifiers() {
        return new JsArray<String>(new String[0]);
      }
    });
    windowInjContext.addBeanProvider(MultipleImplementationsJsType.class.getName(), new JsTypeProvider<Object>() {
      @Override
      public Object getInstance() {
        return new AlternativeImpl(2);
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public String getFactoryName() {
        return null;
      }

      @Override
      public JsArray<String> getQualifiers() {
        return new JsArray<String>(new String[0]);
      }
    });
    windowInjContext.addBeanProvider(InternallyUnsatisfiedJsTypeIface.class.getName(),
            new JsTypeProvider<InternallyUnsatisfiedJsTypeIface>() {

      @Override
      public InternallyUnsatisfiedJsTypeIface getInstance() {
        return new InternallyUnsatisfiedJsTypeIface() {
          @Override
          public String message() {
            return "external";
          }
        };
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public String getFactoryName() {
        return null;
      }

      @Override
      public JsArray<String> getQualifiers() {
        return new JsArray<String>(new String[0]);
      }
    });
    windowInjContext.addBeanProvider(ExternalJsTypeImpl.class.getName(),
            new JsTypeProvider<ExternalJsTypeIface>() {

              @Override
              public ExternalJsTypeIface getInstance() {
                return new ExternalJsTypeIface() {

                  @Override
                  public String message() {
                    return "external";
                  }
                };
              }

              @Override
              public String getName() {
                return null;
              }

              @Override
              public String getFactoryName() {
                return null;
              }

              @Override
              public JsArray<String> getQualifiers() {
                return new JsArray<String>(new String[0]);
              }
    });
    windowInjContext.addBeanProvider(ConcreteWindowScopedJsType.class.getName(),
            new JsTypeProvider<ConcreteWindowScopedJsType>() {

      @Override
      public ConcreteWindowScopedJsType getInstance() {
        return new ConcreteWindowScopedJsType() {
          @Override
          public String message() {
            return "external";
          }
        };
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public String getFactoryName() {
        return null;
      }

      @Override
      public JsArray<String> getQualifiers() {
        return new JsArray<String>(new String[0]);
      }
    });
    windowInjContext.addSuperTypeAlias(ExternalJsTypeIface.class.getName(), ExternalJsTypeImpl.class.getName());
    super.gwtSetUp();
  }

  public void testSingletonJsTypeInWindowContext() {
    final WindowInjectionContext wndContext = WindowInjectionContextStorage.createOrGet();

    final Object bean1 = wndContext.getBean(JsTypeSingletonBean.class.getName());
    assertNotNull("@JsType bean was not registered in window context", bean1);

    final Object bean2 = wndContext.getBean(JsTypeSingletonInterface.class.getName());
    assertNotNull("@JsType bean was not registered using its interface", bean2);

    assertSame(bean1, bean2);
  }

  public void testDependentJsTypeInWindowContext() {
    final WindowInjectionContext wndContext = WindowInjectionContextStorage.createOrGet();

    final Object bean1 = wndContext.getBean(JsTypeDependentBean.class.getName());
    assertNotNull("@JsType bean was not registered in window context", bean1);

    final Object bean2 = wndContext.getBean(JsTypeDependentInterface.class.getName());
    assertNotNull("@JsType bean was not registered using its interface", bean2);

    assertNotSame(bean1, bean2);
  }

  @SuppressWarnings("rawtypes")
  public void testNamedJsTypeInWindowContext() {
    final WindowInjectionContext wndContext = WindowInjectionContextStorage.createOrGet();

    final Object bean1 = wndContext.getBean(JsTypeNamedBean.class.getName());
    assertNotNull("@JsType bean was not registered in window context", bean1);

    final Object bean2 = wndContext.getBean("olaf");
    assertNotNull("@JsType bean was not registered using its interface", bean2);
    assertSame(bean1, bean2);

    final Collection<SyncBeanDef> beans = new ArrayList<>(IOC.getBeanManager().lookupBeans("olaf"));
    beans.addAll(IOC.getBeanManager().lookupBeans(JsTypeNamedBean.class.getName()));
    for (final SyncBeanDef bean : beans) {
      assertEquals("olaf", bean.getName());
    }
  }

  @SuppressWarnings("rawtypes")
  public void testNoDuplicateJsTypeThroughBeanManager() {
    final WindowInjectionContext wndContext = WindowInjectionContextStorage.createOrGet();

    final Object bean1 = wndContext.getBean(JsTypeNamedBean.class.getName());
    assertNotNull("@JsType bean was not registered in window context", bean1);

    final Collection<SyncBeanDef> beans = IOC.getBeanManager().lookupBeans(JsTypeNamedBean.class.getName());
    assertEquals(1, beans.size());
    assertEquals("olaf", beans.iterator().next().getName());
    assertSame(bean1, beans.iterator().next().getInstance());
  }

  public void testConsumingOfUnimplementedJsType() throws Exception {
    injectScriptThenRun(() -> {
      final UnimplementedType ref = new UnimplementedType() {

        @Override
        public void overloaded(final Object obj) {
        }

        @Override
        protected void overloaded() {
        }
      };

      final WindowInjectionContext wndContext = WindowInjectionContextStorage.createOrGet();
      wndContext.addBeanProvider("org.jboss.errai.ioc.tests.wiring.client.res.UnimplementedType", new JsTypeProvider<Object>(){

        @Override
        public Object getInstance() {
          return ref;
        }

        @Override
        public String getName() {
          return null;
        }

        @Override
        public String getFactoryName() {
          return null;
        }

        @Override
        public JsArray<String> getQualifiers() {
          return new JsArray<String>(new String[0]);
        }
      });

      final SyncBeanDef<JsTypeConsumer> consumer = IOC.getBeanManager().lookupBean(JsTypeConsumer.class);

      assertSame(ref, Factory.maybeUnwrapProxy(consumer.getInstance().getIface()));
    });
  }

  public void testNativeJsTypesNotInWindowContext() throws Exception {
    injectScriptThenRun(() -> {
      final WindowInjectionContext context = WindowInjectionContextStorage.createOrGet();
      try {
        context.getBean("org.jboss.errai.ioc.tests.wiring.client.res.NativeConcreteJsType");
        fail("There should not be a provider in the WindowInjectionContext for NativeConcreteJsType.");
      } catch (final IOCResolutionException ex) {
      }
    });
  }

  public void testNoArgInstantiableNativeJsTypeIsInjectable() throws Exception {
    injectScriptThenRun(() -> {
      try {
        final NativeTypeTestModule module = IOC.getBeanManager().lookupBean(NativeTypeTestModule.class).getInstance();
        final NativeConcreteJsType instance = module.nativeConcreteJsType;
        assertEquals("Not the expected implementation (in native.js).", "I am a native type!", instance.message());
      } catch (final IOCResolutionException ex) {
        fail("Precondition failed: Problem looking up test module.");
      }
    });
  }

  public void testInstantiableNativeJsTypeWithConstructorDependencyIsInjectable() throws Exception {
    injectScriptThenRun(() -> {
      try {
        final NativeTypeTestModule module = IOC.getBeanManager().lookupBean(NativeTypeTestModule.class).getInstance();
        final NativeConcreteJsTypeWithConstructorDependency instance = module.nativeWithConstructorDep;
        assertNotNull(instance.get());
        assertEquals("Not the expected implementation (in native.js).", "I am a native type!", instance.get().message());
      } catch (final IOCResolutionException ex) {
        fail("Precondition failed: Problem looking up test module.");
      }
    });
  }

  public void testInstantiableNativeJsTypeWithFieldDependencyIsInjectable() throws Exception {
    injectScriptThenRun(() -> {
      try {
        final NativeTypeTestModule module = IOC.getBeanManager().lookupBean(NativeTypeTestModule.class).getInstance();
        final NativeConcreteJsTypeWithFieldDependency instance = module.nativeWithFieldDep;
        assertNotNull(instance.get());
        assertEquals("Not the expected implementation (in native.js).", "I am a native type!", instance.get().message());
      } catch (final IOCResolutionException ex) {
        fail("Precondition failed: Problem looking up test module.");
      }
    });
  }

  public void testProducerMethodNativeOfJsType() throws Exception {
    injectScriptThenRun(() -> {
      try {
        final NativeTypeTestModule module = IOC.getBeanManager().lookupBean(NativeTypeTestModule.class).getInstance();
        assertNotNull(module.producedNativeIface);
        assertEquals("Not the expected implementation (in native.js).", "please", module.producedNativeIface.getMagicWord());
      } catch (final IOCResolutionException ex) {
        fail("Precondition failed: Problem looking up test module.");
      }
    });
  }

  public void testProducerMethodOfJsType() throws Exception {
    final ConsumesProducedJsType consumer = IOC.getBeanManager().lookupBean(ConsumesProducedJsType.class).getInstance();
    assertNotNull(consumer.instance);
    assertTrue(consumer.instance instanceof ProducedJsType);
    assertEquals(1, IOC.getBeanManager().lookupBeans(ProducedJsType.class).size());
  }

  public void testMultipleJsTypeImplementations() throws Exception {
    Collection<SyncBeanDef<MultipleImplementationsJsType>> beans = IOC.getBeanManager().lookupBeans(MultipleImplementationsJsType.class);
    assertEquals(2, beans.size());

    final Set<Integer> values = new HashSet<>();
    for (final SyncBeanDef<MultipleImplementationsJsType> bean : beans) {
      final MultipleImplementationsJsType instance = bean.getInstance();
      assertTrue(instance instanceof AlternativeImpl);
      values.add(instance.value());
    }

    assertEquals(new HashSet<>(Arrays.asList(1, 2)), values);

    WindowInjectionContextStorage.createOrGet().addBeanProvider(MultipleImplementationsJsType.class.getName(), new JsTypeProvider<Object>() {
      @Override
      public Object getInstance() {
        return new AlternativeImpl(3);
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public String getFactoryName() {
        return null;
      }

      @Override
      public JsArray<String> getQualifiers() {
        return new JsArray<String>(new String[0]);
      }
    });
    values.clear();
    beans = IOC.getBeanManager().lookupBeans(MultipleImplementationsJsType.class);
    for (final SyncBeanDef<MultipleImplementationsJsType> bean : beans) {
      values.add(bean.getInstance().value());
    }
    assertEquals(new HashSet<>(Arrays.asList(1, 2, 3)), values);
  }

  public void testNoDuplicatesWithSuperTypeAliases() throws Exception {
    final JsArray<JsTypeProvider<?>> providers = WindowInjectionContextStorage.createOrGet()
            .getProviders(DuplicateInterface.class.getName());

    assertEquals(2, providers.length());
    assertEquals(
            new HashSet<>(Arrays.asList(
                    JsSubTypeWithDuplicateInterface.class.getName(),
                    JsSuperTypeWithDuplicateInterface.class.getName())),
            new HashSet<>(Arrays.asList(
                    providers.get(0).getInstance().getClass().getName(),
                    providers.get(1).getInstance().getClass().getName())));
  }

  public void testLocalBeanSatisfiesJsTypeInterfaceForDefaultInjectionSite() throws Exception {
    final ExternalTestModule module = IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance();
    assertNotNull(module.defaultSatisfiedIface);
    assertTrue(module.defaultSatisfiedIface instanceof InternallySatisfiedImpl);
    assertSame(module.defaultSatisfiedIface, IOC.getBeanManager().lookupBean(InternallySatisfiedJsTypeIface.class).getInstance());
  }

  public void testLocalBeanSatisfiesJsTypeInterfaceForExternalInjectionSite() throws Exception {
    final ExternalTestModule module = IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance();
    assertNotNull(module.externalSatisfiedIface);
    assertTrue(module.externalSatisfiedIface instanceof InternallySatisfiedImpl);
    assertSame(module.externalSatisfiedIface,
            IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance().externalSatisfiedIface);
  }

  public void testWindowContextBeanSatisfiesJsTypeInterfaceForDefaultInjectionSite() throws Exception {
    final ExternalTestModule module = IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance();
    assertNotNull(module.defaultUnsatisfiedIface);
    assertEquals("external", module.defaultUnsatisfiedIface.message());
  }

  public void testWindowContextBeanSatisfiesJsTypeInterfaceForExternalInjectionSite() throws Exception {
    final ExternalTestModule module = IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance();
    assertNotNull(module.defaultUnsatisfiedIface);
    assertEquals("external", module.defaultUnsatisfiedIface.message());
  }

  public void testImplNotPublishedUnderExternalJsTypeIfaceWhenAlreadyInWindowContext() throws Exception {
    assertEquals(1, WindowInjectionContextStorage.createOrGet().getProviders(ExternalJsTypeIface.class.getName()).length());
  }

  public void testLocalBeanSatisfiesExternalJsTypeInterfaceForDefaultInjectionSite() throws Exception {
    final ExternalTestModule module = IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance();
    assertNotNull(module.defaultExternalIface);
    assertEquals(ExternalJsTypeImpl.class.getSimpleName(), module.defaultExternalIface.message());
    assertTrue(module.defaultExternalIface instanceof ExternalJsTypeImpl);
  }

  public void testWindowContextBeanSatisfiesExternalJsTypeInterfaceForExternalInjectionSite() throws Exception {
    final ExternalTestModule module = IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance();
    assertNotNull(module.externalExternalIface);
    assertEquals("external", module.externalExternalIface.message());
    assertFalse(module.externalExternalIface instanceof ExternalJsTypeImpl);
  }

  public void testWindowContextBeanSatisfiesConcreteWindowScopedJsTypeForDefaultInjectionSite() throws Exception {
    final ExternalTestModule module = IOC.getBeanManager().lookupBean(ExternalTestModule.class).getInstance();
    assertNotNull(module.defaultConcreteWindowScopedJsType);
    assertEquals("external", module.defaultConcreteWindowScopedJsType.message());
  }

  public void testQualifiersOnJsType() throws Exception {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    final Collection<SyncBeanDef<JsTypeWithQualifiers>> beans = (Collection) ((SyncBeanManagerImpl) IOC.getBeanManager())
            .lookupBeans(JsTypeWithQualifiers.class.getName(), true);
    SyncBeanDef<JsTypeWithQualifiers> beanDef = null;
    for (final SyncBeanDef<JsTypeWithQualifiers> bd : beans) {
      if (bd.isDynamic()) {
        beanDef = bd;
      }
    }
    assertNotNull("No bean JS bean def found", beanDef);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    final Set<DynamicAnnotation> quals = (Set) beanDef.getQualifiers();
    assertEquals(3, quals.size());

    final Set<String> notYetFound = new HashSet<>(Arrays.asList(Named.class.getName(),
            QualWithMultiMembers.class.getName(), Any.class.getName()));
    for (final DynamicAnnotation qual : quals) {
      final Map<String, String> members = qual.getMembers();
      if (Any.class.getName().equals(qual.getName())) {
        assertEquals(0, members.size());
        notYetFound.remove(Any.class.getName());
      }
      else if (Named.class.getName().equals(qual.getName())) {
        assertEquals(1, members.size());
        assertEquals("Moogah", members.get("value"));
        notYetFound.remove(Named.class.getName());
      }
      else if (QualWithMultiMembers.class.getName().equals(qual.getName())) {
        assertEquals(3, members.size());
        assertEquals("1", members.get("num"));
        assertEquals("foo", members.get("text"));
        assertEquals(Arrays.toString(new String[] {JsTypeWithQualifiers.class.getName()}), members.get("clazzes"));
        notYetFound.remove(QualWithMultiMembers.class.getName());
      }
    }

    assertEquals(0, notYetFound.size());
  }

  public void testSingletonJsTypeIsNotProxied() throws Exception {
    final NativeTypeTestModule module = IOC.getBeanManager().lookupBean(NativeTypeTestModule.class).getInstance();
    assertEquals("Sanity check for correct bean failed.", "please", module.singletonJsType.magicWord());
    assertFalse("Singleton JS bean should not be proxied.", module.singletonJsType instanceof Proxy);
  }

  private void injectScriptThenRun(final Runnable test) {
    final String scriptUrl = getScriptUrl();
    final Timer timeoutFail = new Timer() {

      @Override
      public void run() {
        fail("Timed out waiting to load " + scriptUrl);
      }
    };

    timeoutFail.schedule(9000);
    delayTestFinish(10000);
    ScriptInjector.fromUrl(scriptUrl)
                  .setWindow(ScriptInjector.TOP_WINDOW)
                  .setCallback(new Callback<Void, Exception>() {

                    @Override
                    public void onFailure(final Exception reason) {
                      timeoutFail.cancel();
                      fail("Could not load " + scriptUrl);
                    }

                    @Override
                    public void onSuccess(final Void result) {
                      try {
                        test.run();
                        finishTest();
                      } finally {
                        timeoutFail.cancel();
                      }
                    }
                  }).inject();
  }

  private String getScriptUrl() {
    final String scriptUrl = GWT.getModuleBaseForStaticFiles() + "native.js";
    return scriptUrl;
  }
}
