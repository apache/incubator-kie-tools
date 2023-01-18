package org.jboss.errai.ioc.unit.test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.HashMultimap;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.meta.MetaClassCache;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.common.client.api.annotations.IOCProducer;
import org.jboss.errai.ioc.client.Bootstrapper;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessor;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.QualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultQualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedBaseType;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedProducer;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedSuperInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedTargetInterface;
import org.jboss.errai.ioc.tests.wiring.client.res.TypedType;
import org.jboss.errai.ioc.unit.res.BeanWithAlternativeDependency;
import org.jboss.errai.ioc.unit.res.ClassWithBadTypedAnnotation;
import org.jboss.errai.ioc.unit.res.DepCycleA;
import org.jboss.errai.ioc.unit.res.DepCycleB;
import org.jboss.errai.ioc.unit.res.DependencyIface;
import org.jboss.errai.ioc.unit.res.DisabledAlternative;
import org.jboss.errai.ioc.unit.res.DisabledAlternativeContextualProvider;
import org.jboss.errai.ioc.unit.res.DisabledAlternativeProducerField;
import org.jboss.errai.ioc.unit.res.DisabledAlternativeProducerMethod;
import org.jboss.errai.ioc.unit.res.DisabledAlternativeProvider;
import org.jboss.errai.ioc.unit.res.InjectsBeanByWrongTypes;
import org.jboss.errai.ioc.unit.res.InjectsInstanceFieldProducedBeanByWrongTypes;
import org.jboss.errai.ioc.unit.res.InjectsInstanceMethodProducedBeanByWrongTypes;
import org.jboss.errai.ioc.unit.res.InjectsStaticFieldProducedBeanByWrongTypes;
import org.jboss.errai.ioc.unit.res.InjectsStaticMethodProducedBeanByWrongTypes;
import org.jboss.errai.ioc.unit.res.JSTypeWithPrivateConstructor;
import org.jboss.errai.ioc.unit.res.ParameterizedIface;
import org.jboss.errai.ioc.unit.res.PseudoCycleA;
import org.jboss.errai.ioc.unit.res.PseudoCycleB;
import org.jboss.errai.ioc.unit.res.TypeParameterControlModule;
import org.jboss.errai.ioc.unit.res.TypeParameterTestModule;
import org.jboss.errai.ioc.unit.res.UsesJSTypeWithPrivateConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.AlternativeBean;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.DependentBean;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.InjectionPoint;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.NormalScopedBean;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.ProducerElement;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.Provider;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.PseudoScopedBean;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests failing dependency graphs for proper error messages.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class IOCProcessorErrorTest {

  private IOCProcessor processor;

  @Mock
  private InjectionContext injContext;

  @Mock
  private IOCProcessingContext procContext;

  @Mock
  private MetaClassCache cache;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setup() {
    MetaClassFactory.getMetaClassCache().clear();
    FactoryGenerator.setDependencyGraph(null);

    final QualifierFactory qualFactory = new DefaultQualifierFactory();
    when(injContext.getQualifierFactory()).thenReturn(qualFactory);
    when(injContext.getInjectableProviders()).thenReturn(HashMultimap.create());
    when(injContext.getExactTypeInjectableProviders()).thenReturn(HashMultimap.create());
    when(injContext.getAnnotationsForElementType(DependentBean)).thenReturn(Arrays.asList(Dependent.class));
    when(injContext.getAnnotationsForElementType(NormalScopedBean)).thenReturn(Arrays.asList(ApplicationScoped.class));
    when(injContext.getAnnotationsForElementType(PseudoScopedBean)).thenReturn(Arrays.asList(Singleton.class, Dependent.class));
    when(injContext.getAnnotationsForElementType(AlternativeBean)).thenReturn(Arrays.asList(Alternative.class));
    when(injContext.getAnnotationsForElementType(InjectionPoint)).thenReturn(Arrays.asList(Inject.class));
    when(injContext.getAnnotationsForElementType(ProducerElement)).thenReturn(Arrays.asList(IOCProducer.class));
    when(injContext.getAnnotationsForElementType(Provider)).thenReturn(Arrays.asList(IOCProvider.class));
    when(injContext.isAllowlisted(any())).thenReturn(true);
    when(injContext.isDenylisted(any())).thenReturn(false);

    final ClassStructureBuilder<?> classBuilder = ClassBuilder
            .define("org.jboss.errai.ioc.FakeBootstrapperImpl")
            .publicScope()
            .implementsInterface(Bootstrapper.class)
            .body();
    final BlockBuilder blockBuilder = classBuilder.publicMethod(ContextManager.class, "bootstrap").body();

    when(procContext.getBlockBuilder()).thenReturn(blockBuilder);
    when(procContext.getBootstrapBuilder()).thenReturn(classBuilder);
    when(procContext.getBootstrapClass()).thenReturn(classBuilder.getClassDefinition());

    processor = new IOCProcessor(injContext);
  }

  @Test
  public void hintWhenUnsatisfiedTypeHasMissingAlternative() throws Exception {
    addToMetaClassCache(
            Object.class,
            DependencyIface.class,
            BeanWithAlternativeDependency.class,
            DisabledAlternative.class);

    final String injSiteTypeName = DependencyIface.class.getName();
    final String typeWithDepName = BeanWithAlternativeDependency.class.getName();
    final String disabledTypeName = DisabledAlternative.class.getName();

    assertDisabledTypeReported(injSiteTypeName, typeWithDepName, disabledTypeName);
  }

  @Test
  public void hintWhenUnsatisfiedTypeHasMissingAlternativeProducerMethod() throws Exception {
    addToMetaClassCache(
            Object.class,
            DependencyIface.class,
            BeanWithAlternativeDependency.class,
            DisabledAlternativeProducerMethod.class);

    final String injSiteTypeName = DependencyIface.class.getName();
    final String typeWithDepName = BeanWithAlternativeDependency.class.getName();
    final String disabledTypeName = DisabledAlternativeProducerMethod.class.getName();

    assertDisabledTypeReported(injSiteTypeName, typeWithDepName, disabledTypeName);
  }

  @Test
  public void hintWhenUnsatisfiedTypeHasMissingAlternativeProducerField() throws Exception {
    addToMetaClassCache(
            Object.class,
            DependencyIface.class,
            BeanWithAlternativeDependency.class,
            DisabledAlternativeProducerField.class);

    final String injSiteTypeName = DependencyIface.class.getName();
    final String typeWithDepName = BeanWithAlternativeDependency.class.getName();
    final String disabledTypeName = DisabledAlternativeProducerField.class.getName();

    assertDisabledTypeReported(injSiteTypeName, typeWithDepName, disabledTypeName);
  }

  @Test
  public void hintWhenUnsatisfiedTypeHasMissingAlternativeProvider() throws Exception {
    addToMetaClassCache(
            Object.class,
            DependencyIface.class,
            BeanWithAlternativeDependency.class,
            DisabledAlternativeProvider.class);

    final String injSiteTypeName = DependencyIface.class.getName();
    final String typeWithDepName = BeanWithAlternativeDependency.class.getName();
    final String disabledTypeName = DisabledAlternativeProvider.class.getName();

    assertDisabledTypeReported(injSiteTypeName, typeWithDepName, disabledTypeName);
  }

  @Test
  public void hintWhenUnsatisfiedTypeHasMissingAlternativeContextualProvider() throws Exception {
    addToMetaClassCache(
            Object.class,
            DependencyIface.class,
            BeanWithAlternativeDependency.class,
            DisabledAlternativeContextualProvider.class);

    final String injSiteTypeName = DependencyIface.class.getName();
    final String typeWithDepName = BeanWithAlternativeDependency.class.getName();
    final String disabledTypeName = DisabledAlternativeContextualProvider.class.getName();

    assertDisabledTypeReported(injSiteTypeName, typeWithDepName, disabledTypeName);
  }

  @Test
  public void doNotMakeDisabledAlternativesAvailableForLookup() throws Exception {
    addToMetaClassCache(
            Object.class,
            DependencyIface.class,
            DisabledAlternative.class);

    processor.process(procContext);
    final DependencyGraph graph = FactoryGenerator.getDependencyGraph();
    assertNotNull("The dependency graph was not set.", graph);
    assertEquals("The dependency graph should not have any injectables.", 0, graph.getNumberOfInjectables());
  }

  @Test
  public void dependentCycleCausesError() throws Exception {
    addToMetaClassCache(
            Object.class,
            DepCycleA.class,
            DepCycleB.class);

    try {
      processor.process(procContext);
      fail("Did not produce error for @Depenent scope cycle.");
    } catch (final RuntimeException e) {
      final String message = e.getMessage();
      assertTrue(
              "Message did not reference types in dependent scoped cycle.\n\tMessage: " + message,
              message.contains(DepCycleA.class.getSimpleName()) && message.contains(DepCycleB.class.getSimpleName()));
    }
  }

  @Test
  public void pseudoScopeCycleCausesError() throws Exception {
    addToMetaClassCache(
            Object.class,
            PseudoCycleA.class,
            PseudoCycleB.class);

    try {
      processor.process(procContext);
      fail("Did not produce error for pseudo scope cycle.");
    } catch (final RuntimeException e) {
      final String message = e.getMessage();
      assertTrue(
              "Message did not reference types in pseudo scoped cycle.\n\tMessage: " + message,
              message.contains(PseudoCycleA.class.getSimpleName()) && message.contains(PseudoCycleB.class.getSimpleName()));
    }
  }

  @Test
  public void typedAnnotationOnBeanPreventsResolutionViaSuperType() throws Exception {
    addToMetaClassCache(
            Object.class,
            TypedType.class,
            TypedBaseType.class,
            TypedSuperInterface.class,
            TypedTargetInterface.class,
            InjectsBeanByWrongTypes.class);

    try {
      processor.process(procContext);
      fail("Did not produce error processing context with unsatisfied dependencies.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      final String message = t.getMessage();
      assertTrue("Message did not reference unsatisfied dependency for " + TypedSuperInterface.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedSuperInterface.class.getName()));
      assertTrue("Message did not reference unsatisfied dependency for " + TypedBaseType.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedBaseType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedType.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedTargetInterface.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedTargetInterface.class.getName()));
    }
  }

  @Test
  public void typedAnnotationOnStaticProducerMethodPreventsResolutionViaSuperType() throws Exception {
    addToMetaClassCache(
            Object.class,
            TypedType.class,
            TypedBaseType.class,
            TypedSuperInterface.class,
            TypedTargetInterface.class,
            TypedProducer.class,
            InjectsStaticMethodProducedBeanByWrongTypes.class);

    try {
      processor.process(procContext);
      fail("Did not produce error processing context with unsatisfied dependencies.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      final String message = t.getMessage();
      assertTrue("Message did not reference unsatisfied dependency for " + TypedSuperInterface.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedSuperInterface.class.getName()));
      assertTrue("Message did not reference unsatisfied dependency for " + TypedBaseType.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedBaseType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedType.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedTargetInterface.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedTargetInterface.class.getName()));
    }
  }

  @Test
  public void typedAnnotationOnStaticProducerFieldPreventsResolutionViaSuperType() throws Exception {
    addToMetaClassCache(
            Object.class,
            TypedType.class,
            TypedBaseType.class,
            TypedSuperInterface.class,
            TypedTargetInterface.class,
            TypedProducer.class,
            InjectsStaticFieldProducedBeanByWrongTypes.class);

    try {
      processor.process(procContext);
      fail("Did not produce error processing context with unsatisfied dependencies.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      final String message = t.getMessage();
      assertTrue("Message did not reference unsatisfied dependency for " + TypedSuperInterface.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedSuperInterface.class.getName()));
      assertTrue("Message did not reference unsatisfied dependency for " + TypedBaseType.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedBaseType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedType.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedTargetInterface.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedTargetInterface.class.getName()));
    }
  }

  @Test
  public void typedAnnotationOnInstanceProducerFieldPreventsResolutionViaSuperType() throws Exception {
    addToMetaClassCache(
            Object.class,
            TypedType.class,
            TypedBaseType.class,
            TypedSuperInterface.class,
            TypedTargetInterface.class,
            TypedProducer.class,
            InjectsInstanceFieldProducedBeanByWrongTypes.class);

    try {
      processor.process(procContext);
      fail("Did not produce error processing context with unsatisfied dependencies.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      final String message = t.getMessage();
      assertTrue("Message did not reference unsatisfied dependency for " + TypedSuperInterface.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedSuperInterface.class.getName()));
      assertTrue("Message did not reference unsatisfied dependency for " + TypedBaseType.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedBaseType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedType.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedTargetInterface.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedTargetInterface.class.getName()));
    }
  }

  @Test
  public void typedAnnotationOnInstanceProducerMethodPreventsResolutionViaSuperType() throws Exception {
    addToMetaClassCache(
            Object.class,
            TypedType.class,
            TypedBaseType.class,
            TypedSuperInterface.class,
            TypedTargetInterface.class,
            TypedProducer.class,
            InjectsInstanceMethodProducedBeanByWrongTypes.class);

    try {
      processor.process(procContext);
      fail("Did not produce error processing context with unsatisfied dependencies.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      final String message = t.getMessage();
      assertTrue("Message did not reference unsatisfied dependency for " + TypedSuperInterface.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedSuperInterface.class.getName()));
      assertTrue("Message did not reference unsatisfied dependency for " + TypedBaseType.class.getName()
              + ".\n\tMessage: " + message, message.contains(TypedBaseType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedType.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedType.class.getName()));
      assertFalse("Message should not reference satisfied dependency " + TypedTargetInterface.class.getName() + "\n\tMessage: "
              + message, message.contains(TypedTargetInterface.class.getName()));
    }
  }

  @Test
  public void errorWhenTypedAnnotationContainsNonAssignableTypes() throws Exception {
    addToMetaClassCache(
            Object.class,
            List.class,
            ClassWithBadTypedAnnotation.class
            );

    try {
      processor.process(procContext);
      fail("Did not produce error processing @Typed annotation with unassignable values.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final RuntimeException ex) {
      assertTrue("Error does not mention the type with the invalid @Typed declaration.",
              ex.getMessage().contains(ClassWithBadTypedAnnotation.class.getName()));
      assertTrue("Error does not mention the unassignable type.", ex.getMessage().contains("java.util.List"));
    }
  }

  @Test
  public void injectionSiteWithRawTypeDoesNotCauseInfiniteLoopOrBadResolution() throws Exception {
    long elapsed;
    final long start = System.currentTimeMillis();
    try {
      addToMetaClassCache(
              Object.class,
              ParameterizedIface.class,
              TypeParameterControlModule.class);
      processor.process(procContext);
      fail("Control passed, but should fail from unsatisfied dependency.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final RuntimeException ex) {
      if (!ex.getMessage().contains("Unsatisfied")) {
        throw new AssertionError("Error was not from an unsatisfied dependency.", ex);
      }
      if (!ex.getMessage().contains("ParameterizedIface<java.lang.Integer>")) {
        throw new AssertionError("Did not report the type of the unsatisfied dependency.", ex);
      }
    }
    elapsed = System.currentTimeMillis() - start;

    setup();
    addToMetaClassCache(
            Object.class,
            ParameterizedIface.class,
            TypeParameterTestModule.class);
    final ExecutorService execService = Executors.newFixedThreadPool(1);
    final Future<Optional<Throwable>> testFuture = execService.submit(() -> {
      try {
        processor.process(procContext);
      } catch (final Throwable t) {
        return Optional.of(t);
      }
      return Optional.empty();
    });

    try {
      final Optional<Throwable> res = testFuture.get(elapsed * 10, TimeUnit.MILLISECONDS);
      assertTrue("Resolution should have failed from an unsatisfied dependency.", res.isPresent());
      final Throwable t = res.get();
      if (!t.getMessage().contains("Unsatisfied")) {
        throw new AssertionError("Error was not from an unsatisfied dependency.", t);
      }
      if (!t.getMessage().contains("ParameterizedIface<java.lang.Integer>")) {
        throw new AssertionError("Did not report the type of the unsatisfied dependency.", t);
      }
    } catch (final TimeoutException ex) {
      testFuture.cancel(true);
      throw new AssertionError(
              "Dependency resolution took over 10 times the duration of the control. Most likely there is an infinite loop.",
              ex);
    }
    finally {
      execService.shutdown();
    }
  }

  @Test
  public void cannotSatisfyInjectionSiteOfNativeJSTypeWithPrivateConstructor() throws Exception {
    addToMetaClassCache(
            Object.class,
            JSTypeWithPrivateConstructor.class,
            UsesJSTypeWithPrivateConstructor.class
            );
    try {
      processor.process(procContext);
      fail("Did not produce an error for native JS type with private constructor.");
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      assertTrue("Error message did not mention unsatisfied dependency.",
              t.getMessage().contains(JSTypeWithPrivateConstructor.class.getSimpleName()));
    }
  }

  private void assertDisabledTypeReported(final String injSiteTypeName, final String typeWithDepName, final String disabledTypeName)
          throws AssertionError {
    try {
      processor.process(procContext);
      fail("Calling process should have caused an error from an unsatisfied dependency.");
    } catch (final NullPointerException npe) {
      throw npe;
    } catch (final RuntimeException ex) {
      // rethrow exception if preconditions not met
      try {
        assertNotNull("Message of " + ex.getClass().getSimpleName() + " should not have been null.", ex.getMessage());
        assertTrue("IOC error did not mention unsatisfied type: " + ex.getMessage(), ex.getMessage().contains(injSiteTypeName));
        assertTrue("IOC error did not mention the type with the unsatisfied injection site: " + ex.getMessage(),
                ex.getMessage().contains(typeWithDepName));
        assertTrue("IOC error contains two unsatisfied dependencies. Should only containe one.",
                ex.getMessage().indexOf("Unsatisfied") == ex.getMessage().lastIndexOf("Unsatisfied"));
      } catch (final AssertionError ae) {
        throw new AssertionError(ae.getMessage(), ex);
      }

      assertTrue("IOC error did not mention the disabled alternative that satisfies the injection site: " + ex.getMessage(),
              ex.getMessage().contains(disabledTypeName));
    }
  }

  private void addToMetaClassCache(final Class<?>... metaClasses) {
    Arrays.stream(metaClasses)
          .map(type -> JavaReflectionClass.newInstance(type))
          .forEach(mc -> MetaClassFactory.getMetaClassCache().pushCache(mc));
  }

}
