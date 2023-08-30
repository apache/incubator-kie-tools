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


package org.jboss.errai.ioc.rebind.ioc.bootstrapper;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.rebind.ioc.graph.api.CustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.codegen.builder.impl.ClassBuilder.define;
import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;

/**
 * Generates {@link Factory} subclasses by dispatching to the appropriate
 * {@link FactoryBodyGenerator} and writing the output.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class FactoryGenerator extends IncrementalGenerator {

  private static final Logger log = LoggerFactory.getLogger(FactoryGenerator.class);

  private static final String GENERATED_PACKAGE = "org.jboss.errai.ioc.client";
  private static DependencyGraph graph;
  private static InjectionContext injectionContext;
  private static Map<String, String> generatedSourceByFactoryTypeName = new HashMap<String, String>();
  private static Map<String, Injectable> injectablesByFactoryTypeName = new HashMap<String, Injectable>();

  private static long totalTime;

  public static void resetTotalTime() {
    totalTime = 0;
  }

  public static void setDependencyGraph(final DependencyGraph graph) {
    log.debug("Dependency graph set.");
    FactoryGenerator.graph = graph;
  }

  public static DependencyGraph getDependencyGraph() {
    return graph;
  }

  public static void setInjectionContext(final InjectionContext injectionContext) {
    log.debug("Injection context set.");
    FactoryGenerator.injectionContext = injectionContext;
  }

  public static String getLocalVariableName(final MetaParameter param) {
    final MetaClassMember member = param.getDeclaringMember();

    return member.getName() + "_" + param.getName() + "_" + param.getIndex();
  }

  private static DependencyGraph assertGraphSet() {
    if (graph == null) {
      throw new RuntimeException("Dependency graph must be generated and set before " + FactoryGenerator.class.getSimpleName() + " runs.");
    }

    return graph;
  }
  private static InjectionContext assertInjectionContextSet() {
    if (injectionContext == null) {
      throw new RuntimeException("Injection context must be set before " + FactoryGenerator.class.getSimpleName() + " runs.");
    }

    return injectionContext;
  }

  @Override
  public RebindResult generateIncrementally(final TreeLogger logger, final GeneratorContext generatorContext, final String typeName)
          throws UnableToCompleteException {
    final long start = System.currentTimeMillis();
    final DependencyGraph graph = assertGraphSet();
    final InjectionContext injectionContext = assertInjectionContextSet();
    final Injectable injectable = graph.getConcreteInjectable(typeName.substring(typeName.lastIndexOf('.')+1));
    final InjectableType factoryType = injectable.getInjectableType();

    final ClassStructureBuilder<?> factoryBuilder = define(getFactorySubTypeName(typeName),
            parameterizedAs(Factory.class, typeParametersOf(injectable.getInjectedType()))).publicScope().body();
    final FactoryBodyGenerator generator = selectBodyGenerator(factoryType, typeName, injectable);

    final String factorySimpleClassName = getFactorySubTypeSimpleName(typeName);
    final PrintWriter pw = generatorContext.tryCreate(logger, GENERATED_PACKAGE, factorySimpleClassName);

    final RebindResult retVal;
    if (pw != null) {
      final String factorySource;
      if (isCacheUsable(typeName, injectable)) {
        log.debug("Reusing cached factory for " + typeName);
        factorySource = generatedSourceByFactoryTypeName.get(typeName);
      } else {
        log.debug("Generating factory for " + typeName);
        generator.generate(factoryBuilder, injectable, graph, injectionContext, logger, generatorContext);
        factorySource = factoryBuilder.toJavaString();
        generatedSourceByFactoryTypeName.put(typeName, factorySource);
        injectablesByFactoryTypeName.put(typeName, injectable);

        writeToDotErraiFolder(factorySimpleClassName, factorySource);
      }

      pw.write(factorySource);
      generatorContext.commit(logger, pw);

      retVal = new RebindResult(RebindMode.USE_ALL_NEW, factoryBuilder.getClassDefinition().getFullyQualifiedName());
    } else {
      log.debug("Reusing factory for " + typeName);
      retVal = new RebindResult(RebindMode.USE_EXISTING, getFactorySubTypeName(typeName));
    }

    final long ellapsed = System.currentTimeMillis() - start;
    totalTime += ellapsed;
    log.debug("Factory for {} completed in {}ms. Total factory generation time: {}ms", typeName, ellapsed, totalTime);

    return retVal;
  }

  private void writeToDotErraiFolder(final String factorySimpleClassName, final String factorySource) {
    RebindUtils.writeStringToJavaSourceFileInErraiCacheDir(GENERATED_PACKAGE, factorySimpleClassName, factorySource);
  }

  private boolean isCacheUsable(final String typeName, final Injectable givenInjectable) {
    if (RebindUtils.NO_CACHE) {
      return false;
    }

    final Injectable cachedInjectable = injectablesByFactoryTypeName.get(typeName);

    if (cachedInjectable != null) {
      final boolean sameContent = cachedInjectable.hashContent() == givenInjectable.hashContent();
      if (log.isTraceEnabled() && !sameContent) {
        log.trace("Different hashContent for cached " + typeName);
        traceConstituentHashContents(cachedInjectable, "cached " + typeName);
        traceConstituentHashContents(givenInjectable, "new " + typeName);
      }

      return sameContent;
    } else {
      log.trace("No cached injectable was found for {}", typeName);
      return false;
    }
  }

  private static void traceConstituentHashContents(final Injectable injectable, final String name) {
    log.trace("Begin trace of hashContent for {}", name);
    log.trace("Combined content: {}", injectable.hashContent());
    log.trace("HashContent for injectable type: {}", injectable.getInjectedType().hashContent());
    for (final Dependency dep : injectable.getDependencies()) {
      log.trace("HashContent for {} dep of type {}: {}", dep.getDependencyType().toString(),
              dep.getInjectable().getInjectedType(), dep.getInjectable().getInjectedType().hashContent());
    }
    log.trace("End trace of hashContent for {}", name);
  }

  private FactoryBodyGenerator selectBodyGenerator(final InjectableType factoryType, final String typeName, final Injectable injectable) {
    final FactoryBodyGenerator generator;
    switch (factoryType) {
    case Type:
      generator = new TypeFactoryBodyGenerator();
      break;
    case Provider:
      generator = new ProviderFactoryBodyGenerator();
      break;
    case JsType:
      generator = new JsTypeFactoryBodyGenerator();
      break;
    case Producer:
      generator = new ProducerFactoryBodyGenerator();
      break;
    case ContextualProvider:
      generator = new ContextualFactoryBodyGenerator();
      break;
    case ExtensionProvided:
      if (!(injectable instanceof CustomFactoryInjectable)) {
        throw new RuntimeException(String.format("The injectable, %s, for %s is extension provided but is not a %s",
                injectable.toString(), typeName, CustomFactoryInjectable.class.getSimpleName()));
      }

      generator = ((CustomFactoryInjectable) injectable).getGenerator();
      break;
    default:
      throw new RuntimeException(factoryType + " not yet implemented!");
    }

    return generator;
  }

  public static String getFactorySubTypeName(final String typeName) {
    return GENERATED_PACKAGE + "." + getFactorySubTypeSimpleName(typeName);
  }

  public static String getFactorySubTypeSimpleName(final String typeName) {
    final int simpleNameStart = Math.max(typeName.lastIndexOf('.'), typeName.lastIndexOf('$')) + 1;
    return typeName.substring(simpleNameStart);
  }

  @Override
  public long getVersionId() {
    return 1;
  }

}
