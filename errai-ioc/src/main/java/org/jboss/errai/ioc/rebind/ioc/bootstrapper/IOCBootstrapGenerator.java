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

package org.jboss.errai.ioc.rebind.ioc.bootstrapper;


import static org.jboss.errai.codegen.util.Stmt.loadVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.BlockBuilderImpl;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.Implementations;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.annotations.IOCProducer;
import org.jboss.errai.common.metadata.MetaDataScanner;
import org.jboss.errai.common.metadata.ScannerSingleton;
import org.jboss.errai.common.server.api.ErraiBootstrapFailure;
import org.jboss.errai.config.rebind.EnvUtil;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.ioc.client.Bootstrapper;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.IOCBootstrapTask;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.api.TaskOrder;
import org.jboss.errai.ioc.client.api.SharedSingleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;
import org.jboss.errai.ioc.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;

/**
 * Generator for the Bootstrapper class generated to wire an application at runtime.
 *
 * @author Mike Brock <cbrock@redhat.com>
 */
public class IOCBootstrapGenerator {
  private final GeneratorContext context;

  private final Set<String> packages;
  private final boolean useReflectionStubs;

  private final List<MetaClass> beforeTasks = new ArrayList<MetaClass>();
  private final List<MetaClass> afterTasks = new ArrayList<MetaClass>();

  public static final String QUALIFYING_METADATA_FACTORY_PROPERTY = "errai.ioc.QualifyingMetaDataFactory";
  public static final String ENABLED_ALTERNATIVES_PROPERTY = "errai.ioc.enabled.alternatives";
  public static final String ALLOWLIST_PROPERTY = "errai.ioc.allowlist";
  public static final String DENYLIST_PROPERTY = "errai.ioc.denylist";
  public static final String EXPERIMENTAL_INFER_DEPENDENT_BY_REACHABILITY
      = "errai.ioc.experimental.infer_dependent_by_reachability";

  private final TreeLogger logger;
  private static final Logger log = LoggerFactory.getLogger(IOCBootstrapGenerator.class);

  private static final Object generatorLock = new Object();

  private static Set<Class<?>> iocExtensions;

  private static List<IOCExtensionConfigurator> extensionConfigurators;

  private static Collection<MetaClass> bootstrapClassCollection;

  @SuppressWarnings("rawtypes")
  private static Map<Class<? extends IOCDecoratorExtension>, Class<? extends Annotation>> decoratorMap;

  public IOCBootstrapGenerator(final GeneratorContext context,
                               final TreeLogger logger,
                               final Set<String> packages,
                               final boolean useReflectionStubs) {
    this.context = context;
    this.logger = logger;
    this.packages = packages;
    this.useReflectionStubs = useReflectionStubs;
  }

  public String generate(final String packageName, final String className) {
    synchronized (generatorLock) {
      EnvUtil.recordEnvironmentState();

      final String gen;

      log.info("generating IOC bootstrapping class...");
      final long st = System.currentTimeMillis();

      log.debug("setting up injection context...");
      final long injectionStart = System.currentTimeMillis();
      final InjectionContext injectionContext = setupContexts(packageName, className);
      log.debug("injection context setup in " + (System.currentTimeMillis() - injectionStart) + "ms");

      gen = generateBootstrappingClassSource(injectionContext);
      log.info("generated IOC bootstrapping class in " + (System.currentTimeMillis() - st) + "ms ");

      return gen;
    }
  }

  private InjectionContext setupContexts(final String packageName,
                                         final String className) {
    final boolean asyncBootstrap;

    final String s = EnvUtil.getEnvironmentConfig().getFrameworkOrSystemProperty("errai.ioc.async_bean_manager");
    asyncBootstrap = s != null && Boolean.parseBoolean(s);

    final ClassStructureBuilder<?> classStructureBuilder =
        Implementations.implement(Bootstrapper.class, packageName, className);

    logger.log(com.google.gwt.core.ext.TreeLogger.Type.DEBUG, "Generating IOC Bootstrapper "
        + packageName + "." + className);

    final BuildMetaClass bootStrapClass = classStructureBuilder.getClassDefinition();
    final Context buildContext = bootStrapClass.getContext();

    buildContext.addInterningCallback(new BootstrapInterningCallback(classStructureBuilder, buildContext));

    final BlockBuilder<?> blockBuilder =
        classStructureBuilder.publicMethod(ContextManager.class, "bootstrapContainer")
            .methodComment("The main IOC bootstrap method.");

    final IOCProcessingContext.Builder iocProcContextBuilder
        = IOCProcessingContext.Builder.create();

    iocProcContextBuilder.blockBuilder(blockBuilder);
    iocProcContextBuilder.generatorContext(context);
    iocProcContextBuilder.context(buildContext);
    iocProcContextBuilder.bootstrapClassInstance(bootStrapClass);
    iocProcContextBuilder.bootstrapBuilder(classStructureBuilder);
    iocProcContextBuilder.logger(logger);
    iocProcContextBuilder.gwtTarget(!useReflectionStubs);

    final InjectionContext.Builder injectionContextBuilder
        = InjectionContext.Builder.create();

    final MetaDataScanner scanner = ScannerSingleton.getOrCreateInstance();
    final Multimap<String, String> props = scanner.getErraiProperties();

    if (props != null) {
      logger.log(TreeLogger.Type.INFO, "Checking ErraiApp.properties for configured types ...");

      final Collection<String> qualifyingMetadataFactoryProperties = props.get(QUALIFYING_METADATA_FACTORY_PROPERTY);

      if (qualifyingMetadataFactoryProperties.size() > 1) {
        throw new RuntimeException("the property '" + QUALIFYING_METADATA_FACTORY_PROPERTY + "' is set in more than one place");
      }

      final Collection<String> alternatives = PropertiesUtil.getPropertyValues(ENABLED_ALTERNATIVES_PROPERTY, "\\s");
      for (final String alternative : alternatives) {
        injectionContextBuilder.enabledAlternative(alternative.trim());
      }

      final Collection<String> allowlistItems = PropertiesUtil.getPropertyValues(ALLOWLIST_PROPERTY, "\\s");
      for (final String item : allowlistItems) {
        injectionContextBuilder.addToAllowlist(item.trim());
      }

      final Collection<String> denylistItems = PropertiesUtil.getPropertyValues(DENYLIST_PROPERTY, "\\s");
      for (final String type : denylistItems) {
        injectionContextBuilder.addToDenylist(type.trim());
      }
    }

    iocProcContextBuilder.packages(packages);

    final IOCProcessingContext processingContext = iocProcContextBuilder.build();

    injectionContextBuilder.processingContext(processingContext);
    injectionContextBuilder.asyncBootstrap(asyncBootstrap);

    final InjectionContext injectionContext = injectionContextBuilder.build();

    defaultConfigureProcessor(injectionContext);

    return injectionContext;
  }

  private String generateBootstrappingClassSource(final InjectionContext injectionContext) {


    log.debug("Processing IOC extensions...");
    long start = System.currentTimeMillis();
    processExtensions(context, injectionContext, beforeTasks, afterTasks);
    log.debug("Extensions processed in {}ms", (System.currentTimeMillis() - start));

    final IOCProcessor processorFactory = new IOCProcessor(injectionContext);
    final IOCProcessingContext processingContext = injectionContext.getProcessingContext();
    final ClassStructureBuilder<?> classBuilder = processingContext.getBootstrapBuilder();
    final BlockBuilder<?> blockBuilder = processingContext.getBlockBuilder();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    final BlockBuilder builder = new BlockBuilderImpl(classBuilder.getClassDefinition().getInstanceInitializer(), null);

    doBeforeRunnables(builder);

    log.debug("Process dependency graph...");
    start = System.currentTimeMillis();
    processorFactory.process(processingContext);
    log.debug("Processed dependency graph in {}ms", System.currentTimeMillis() - start);

    doAfterRunnbales(blockBuilder);

    blockBuilder.append(loadVariable("contextManager").returnValue());
    blockBuilder.finish();

    start = System.currentTimeMillis();
    final String bootstrapperImplString = classBuilder.toJavaString();
    log.debug("Generated BootstrapperImpl String in {}ms", System.currentTimeMillis() - start);

    return bootstrapperImplString;
  }

  private void doAfterRunnbales(final BlockBuilder<?> blockBuilder) {
    long start;
    log.debug("Running after tasks...");
    start = System.currentTimeMillis();
    _doRunnableTasks(afterTasks, blockBuilder);
    log.debug("Tasks run in " + (System.currentTimeMillis() - start) + "ms");
  }

  private void doBeforeRunnables(final BlockBuilder<?> builder) {
    long start;
    log.debug("Running before tasks...");
    start = System.currentTimeMillis();
    _doRunnableTasks(beforeTasks, builder);
    log.debug("Tasks run in " + (System.currentTimeMillis() - start) + "ms");
  }

  private static void _doRunnableTasks(final Collection<MetaClass> classes, final BlockBuilder<?> blockBuilder) {
    for (final MetaClass clazz : classes) {
      if (!clazz.isAssignableTo(Runnable.class)) {
        throw new RuntimeException("annotated @IOCBootstrap task: " + clazz.getName() + " is not of type: "
            + Runnable.class.getName());
      }

      blockBuilder.append(Stmt.nestedCall(Stmt.newObject(clazz)).invoke("run"));
    }
  }

  public static void processExtensions(final GeneratorContext context,
                                       final InjectionContext injectionContext,
                                       final List<MetaClass> beforeTasks,
                                       final List<MetaClass> afterTasks) {

    final MetaDataScanner scanner = ScannerSingleton.getOrCreateInstance();
    maybeLoadExtensionConfigurators(scanner);

    try {
      for (final IOCExtensionConfigurator configurator : extensionConfigurators) {
        configurator.configure(injectionContext.getProcessingContext(), injectionContext);
      }
    } catch (Exception e) {
      throw new ErraiBootstrapFailure("Unable to run IOC Extension Configurator: " + e.getMessage(), e);
    }


    maybeLoadBootstrapClassCollection(context);
    for (final MetaClass clazz : bootstrapClassCollection) {
      final IOCBootstrapTask task = clazz.getAnnotation(IOCBootstrapTask.class);
      if (task.value() == TaskOrder.Before) {
        beforeTasks.add(clazz);
      }
      else {
        afterTasks.add(clazz);
      }
    }

    maybeValidateDecorators(scanner);

    try {
      for (@SuppressWarnings("rawtypes")
      final Entry<Class<? extends IOCDecoratorExtension>, Class<? extends Annotation>> entry : decoratorMap.entrySet()) {
        injectionContext.registerDecorator(
                entry.getKey().getConstructor(new Class[] { Class.class }).newInstance(entry.getValue()));
      }
    } catch (Exception e) {
      throw new ErraiBootstrapFailure("unable to load code decorator: " + e.getMessage(), e);
    }

    for (final IOCExtensionConfigurator extensionConfigurator : extensionConfigurators) {
      extensionConfigurator.afterInitialization(injectionContext.getProcessingContext(), injectionContext);
    }
  }

  @SuppressWarnings("rawtypes")
  private static void maybeValidateDecorators(final MetaDataScanner scanner) {
    if (decoratorMap == null || EnvUtil.isJUnitTest()) {
      decoratorMap = new HashMap<Class<? extends IOCDecoratorExtension>, Class<? extends Annotation>>();
      final Set<Class<?>> decorators = scanner.getTypesAnnotatedWith(CodeDecorator.class);
      try {
        for (final Class<?> clazz : decorators) {
          final Class<? extends IOCDecoratorExtension> decoratorClass = clazz.asSubclass(IOCDecoratorExtension.class);

          Class<? extends Annotation> annoType = null;
          final Type t = decoratorClass.getGenericSuperclass();
          if (!(t instanceof ParameterizedType)) {
            throw new ErraiBootstrapFailure("code decorator must extend IOCDecoratorExtension<@AnnotationType>");
          }

          final ParameterizedType pType = (ParameterizedType) t;
          if (IOCDecoratorExtension.class.equals(pType.getRawType())) {
            if (pType.getActualTypeArguments().length == 0
                    || !Annotation.class.isAssignableFrom((Class<?>) pType.getActualTypeArguments()[0])) {
              throw new ErraiBootstrapFailure("code decorator must extend IOCDecoratorExtension<@AnnotationType>");
            }

            // noinspection unchecked
            annoType = ((Class<?>) pType.getActualTypeArguments()[0]).asSubclass(Annotation.class);
          }

          decoratorMap.put(decoratorClass, annoType);

        }
      }
      catch (Exception e) {
        throw new ErraiBootstrapFailure("unable to load code decorator: " + e.getMessage(), e);
      }
    }
  }

  private static void maybeLoadBootstrapClassCollection(final GeneratorContext context) {
    if (bootstrapClassCollection == null || EnvUtil.isJUnitTest()) {
      bootstrapClassCollection = ClassScanner.getTypesAnnotatedWith(IOCBootstrapTask.class, context);
    }
  }

  private static void maybeLoadExtensionConfigurators(final MetaDataScanner scanner) {
    if (iocExtensions == null || extensionConfigurators == null || EnvUtil.isJUnitTest()) {
      iocExtensions = scanner
              .getTypesAnnotatedWith(org.jboss.errai.ioc.client.api.IOCExtension.class);
      extensionConfigurators = new ArrayList<IOCExtensionConfigurator>();

      try {
        for (final Class<?> clazz : iocExtensions) {
          final Class<? extends IOCExtensionConfigurator> configuratorClass
          = clazz.asSubclass(IOCExtensionConfigurator.class);

          final IOCExtensionConfigurator configurator = configuratorClass.newInstance();
          extensionConfigurators.add(configurator);

        }
      }
      catch (Exception e) {
        throw new ErraiBootstrapFailure("unable to load IOC Extension Configurator: " + e.getMessage(), e);
      }
    }
  }

  /**
   * @param injectionContext
   *     an instance of the injection context
   */
  private static void defaultConfigureProcessor(final InjectionContext injectionContext) {
    injectionContext.mapElementType(WiringElementType.PseudoScopedBean, Singleton.class);
    injectionContext.mapElementType(WiringElementType.NormalScopedBean, ApplicationScoped.class);
    injectionContext.mapElementType(WiringElementType.NormalScopedBean, SharedSingleton.class);
    injectionContext.mapElementType(WiringElementType.PseudoScopedBean, EntryPoint.class);

    injectionContext.mapElementType(WiringElementType.ProducerElement, IOCProducer.class);

    injectionContext.mapElementType(WiringElementType.DependentBean, Dependent.class);

    injectionContext.mapElementType(WiringElementType.Provider, IOCProvider.class);

    injectionContext.mapElementType(WiringElementType.InjectionPoint, Inject.class);
    injectionContext.mapElementType(WiringElementType.InjectionPoint, com.google.inject.Inject.class);

    injectionContext.mapElementType(WiringElementType.AlternativeBean, Alternative.class);
  }

}
