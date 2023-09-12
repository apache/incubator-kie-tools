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


package org.jboss.errai.config.rebind;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.config.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class of all asynchronous code generators.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class AbstractAsyncGenerator extends Generator implements AsyncCodeGenerator {

  private static final Map<Class<? extends AbstractAsyncGenerator>, String> cacheMap = new ConcurrentHashMap<Class<? extends AbstractAsyncGenerator>, String>();
  private static final Map<Class<? extends AbstractAsyncGenerator>, Set<String>> cacheRelevantClasses =
          new ConcurrentHashMap<Class<? extends AbstractAsyncGenerator>, Set<String>>();

  private static Logger log = LoggerFactory.getLogger(AbstractAsyncGenerator.class);

  @Override
  public Future<String> generateAsync(final TreeLogger logger, final GeneratorContext context) {
    return ThreadUtil.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        final String generatedCode;

        if (isCacheEnabled() && isCacheValid()) {
          log.info("Using cached output from " + AbstractAsyncGenerator.this.getClass().getName());
          generatedCode = getGeneratedCache();
        }
        else {
          log.info("Running generator " + AbstractAsyncGenerator.this.getClass().getName());
          clearCacheRelevantClasses();
          generatedCode = generate(logger, context);
          setGeneratedCache(generatedCode);
        }

        return generatedCode;
      }
    });
  }

  private boolean isCacheEnabled() {
    return !RebindUtils.NO_CACHE;
  }

  /**
   * @return True iff there is a cached output for this generator. Useful for subclasses that must override {@link #isCacheValid()}.
   */
  protected final boolean hasGenerationCache() {
    final boolean hasCache = cacheMap.containsKey(getClass());
    if (log.isDebugEnabled()) {
      log.debug("{} {} a generated cache.", getClass().getSimpleName(), (hasCache ? "has" : "does not have"));
    }
    return hasCache;
  }

  protected final String getGeneratedCache() {
    return cacheMap.get(getClass());
  }

  private final void setGeneratedCache(final String generated) {
    cacheMap.put(getClass(), generated);
  }

  /**
   * @return True iff this generator does not need to be run again this refresh.
   */
  protected boolean isCacheValid() {
    return hasGenerationCache() && !(MetaClassFactory.hasAnyChanges() && hasRelevantChanges());
  }

  private boolean hasRelevantChanges() {
    final String generatorName = this.getClass().getSimpleName();
    final Set<String> relevantClasses = cacheRelevantClasses.get(this.getClass());
    if (relevantClasses == null) {
      log.debug("No classes marked as relevant for {}. Assuming there are relevant changes.", generatorName);
      return true;
    }

    for (final MetaClass clazz : MetaClassFactory.getAllNewOrUpdatedClasses()) {
      final boolean previouslyMarkedRelevant = relevantClasses.contains(clazz.getFullyQualifiedName());
      if (previouslyMarkedRelevant || isRelevantClass(clazz)) {
        log.debug("New or updated class {} is {} cache relevant for {}.", clazz.getFullyQualifiedName(),
                (previouslyMarkedRelevant ? "previously marked as" : "now marked as"), generatorName);
        return true;
      } else {
        log.trace("New or updated class {} is not cache relevant for {}.", clazz.getFullyQualifiedName(), generatorName);
      }
    }

    for (final String deleted : MetaClassFactory.getAllDeletedClasses()) {
      if (relevantClasses.contains(deleted)) {
        log.debug("Deleted class {} was cache relevant for {}.", deleted, generatorName);
        return true;
      } else {
        log.trace("Deleted class {} is not cache relevant for {}.", deleted, generatorName);
      }
    }

    return false;
  }

  /**
   * Checks if the provided class is relevant to this generator. This method is
   * invoked to determine whether or not the introduction of the provided class
   * should trigger a rerun of this generator.
   *
   * @param clazz
   *          a newly introduced clazz since the last refresh.
   *
   * @return true if newly introduced class is relevant to this generator,
   *         otherwise false.
   */
  protected boolean isRelevantClass(final MetaClass clazz) {
    return true;
  }

  /**
   * Called by {@link #generateAsync(TreeLogger, GeneratorContext)} to carry out the actual code
   * generation.
   *
   * @param context
   *          the generator context to use.
   *
   * @return the generated code.
   */
  protected abstract String generate(final TreeLogger logger, final GeneratorContext context);

  /**
   * Starts all asynchronous generators if they haven't been started yet and waits for the
   * completion of the generator responsible for the provided interface type.
   *
   * @param interfaceType
   *          the interface for which an implementation should be generated.
   * @param context
   *          the generation context to use.
   * @param logger
   *          the tree logger to use.
   * @param packageName
   *          the package name of the generated class.
   * @param className
   *          the name of the generated class.
   *
   * @return the fully qualified name of the generated class.
   */
  protected String startAsyncGeneratorsAndWaitFor(
      final Class<?> interfaceType,
      final GeneratorContext context,
      final TreeLogger logger,
      final String packageName,
      final String className) {

    try {
      final PrintWriter printWriter = context.tryCreate(logger, packageName, className);
      if (printWriter != null) {
        final Future<String> future = AsyncGenerationJob.createBuilder()
            .treeLogger(logger)
            .generatorContext(context)
            .interfaceType(interfaceType)
            .runIfStarting(new Runnable() {
              @Override
              public void run() {
                final long start = System.currentTimeMillis();
                MetaClassBridgeUtil.populateMetaClassFactoryFromTypeOracle(context, logger);
                log.debug("MetaClassFactory populated in {}ms", System.currentTimeMillis() - start);
              }
            })
            .build()
            // this causes all asynchronous code generators to run if this is the first one that executes.
            .submit();

        final String gen = future.get();
        printWriter.append(gen);

        RebindUtils.writeStringToJavaSourceFileInErraiCacheDir(packageName, className, gen);

        context.commit(logger, printWriter);
      }
    }
    catch (final Throwable e) {
      e.printStackTrace();
      logger.log(TreeLogger.ERROR, "Error generating " + className, e);
    }

    return packageName + "." + className;
  }

  /**
   * Marks the provided class as cache relevant. This means that the provided
   * class will be considered when checking for changes to determine whether or
   * not the generator needs to rerun after a refresh. If no cache relevant
   * classes are added any change to reloadable classes will cause the generator
   * to rerun.
   *
   * @param clazz
   *          the class to consider when checking for changes.
   */
  protected void addCacheRelevantClass(final MetaClass clazz) {
    Set<String> classes = cacheRelevantClasses.get(this.getClass());
    if (classes == null) {
      classes = new HashSet<String>();
      cacheRelevantClasses.put(this.getClass(), classes);
    }
    classes.add(clazz.getFullyQualifiedName());
  }

  /**
   * Marks the provided classes as cache relevant. This means that the provided
   * classes will be considered when checking for changes to determine whether
   * or not the generator needs to rerun after a refresh. If no cache relevant
   * classes are added any change to reloadable classes will cause the generator
   * to rerun.
   *
   * @param classes
   *          the classes to consider when checking for changes.
   */
  protected void addCacheRelevantClasses(final Collection<MetaClass> classes) {
    for (final MetaClass clazz : classes) {
      addCacheRelevantClass(clazz);
    }
  }

  private void clearCacheRelevantClasses() {
    cacheRelevantClasses.remove(this.getClass());
  }
}
