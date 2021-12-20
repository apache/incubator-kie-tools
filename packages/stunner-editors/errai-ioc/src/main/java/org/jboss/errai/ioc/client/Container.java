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

package org.jboss.errai.ioc.client;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.container.BeanManagerSetup;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.ErraiUncaughtExceptionHandler;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

public class Container implements EntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(Container.class);

  @Override
  public void onModuleLoad() {
    bootstrapContainer();
  }

  private void setUncaughtExceptionHandler() {
    final UncaughtExceptionHandler replacedHandler = GWT.getUncaughtExceptionHandler();
    GWT.setUncaughtExceptionHandler(new ErraiUncaughtExceptionHandler(replacedHandler));
  }

  public void bootstrapContainer() {
    setUncaughtExceptionHandler();
    logger.info("Starting to bootstrap IOC container...");
    final long bootstrapStart = System.currentTimeMillis();
    try {
      init = false;

      logger.debug("Initializing {}...", QualifierEqualityFactory.class.getSimpleName());
      long start = System.currentTimeMillis();
      QualifierUtil.initFromFactoryProvider(() -> GWT.create(QualifierEqualityFactory.class));
      logger.debug("{} initialized in {}ms", QualifierEqualityFactory.class.getSimpleName(), System.currentTimeMillis() - start);

      final BeanManagerSetup beanManager;
      if (GWT.<IOCEnvironment>create(IOCEnvironment.class).isAsync()) {
        logger.info("Bean manager initialized in async mode.");
        beanManager = (BeanManagerSetup) IOC.getAsyncBeanManager();
      } else {
        beanManager = (BeanManagerSetup) IOC.getBeanManager();
      }

      logger.debug("Creating new {} instance...", Bootstrapper.class.getSimpleName());
      start = System.currentTimeMillis();
      final Bootstrapper bootstrapper = GWT.create(Bootstrapper.class);
      logger.debug("Created {} instance in {}ms", Bootstrapper.class.getSimpleName(), System.currentTimeMillis() - start);

      logger.debug("Creating new {} instance...", ContextManager.class.getSimpleName());
      start = System.currentTimeMillis();
      final ContextManager contextManager = bootstrapper.bootstrapContainer();
      logger.debug("Created {} instance in {}ms", ContextManager.class.getSimpleName(), System.currentTimeMillis() - start);

      logger.debug("Initializing bean manager...");
      start = System.currentTimeMillis();
      beanManager.setContextManager(contextManager);
      logger.debug("Bean manager initialized in {}ms", System.currentTimeMillis() - start);

      logger.debug("Running post initialization runnables...");
      start = System.currentTimeMillis();
      init = true;
      for (final Runnable run : afterInit) {
        run.run();
      }
      afterInit.clear();
      logger.debug("All post initialization runnables finished in {}ms", System.currentTimeMillis() - start);

      logger.info("IOC bootstrapper successfully initialized in {}ms", System.currentTimeMillis() - bootstrapStart);
    }
    catch (final RuntimeException ex) {
      logger.error("Critical error in IOC container bootstrap.", ex);

      throw ex;
    }
  }

  private static final List<Runnable> afterInit = new ArrayList<>();
  private static boolean init = false;

  /**
   * Runs the specified {@link Runnable} only after the bean manager has fully initialized. It is generally not
   * necessary to use this method from within beans themselves. But if you are generated out-of-container calls
   * into the bean manager (such as for testing), it may be necessary to use this method to ensure that the beans
   * you wish to lookup have been loaded.
   * <p/>
   * Use of this method is really only necessary when using the bean manager in asynchronous mode as wiring of the
   * container synchronously does not yield during bootstrapping operations.
   * <p/>
   * If the bean manager is already initialized when you call this method, the <tt>Runnable</tt> is invoked immediately.
   *
   * @param runnable
   *     the {@link Runnable} to execute after bean manager initialization.
   */
  public static void runAfterInit(final Runnable runnable) {
    if (init) {
      runnable.run();
    } else {
      afterInit.add(runnable);
    }
  }

  /**
   * Short-alias method for {@link #runAfterInit(Runnable)}.
   *
   * @param runnable
   */
  public static void $(final Runnable runnable) {
    runAfterInit(runnable);
  }

  public static void reset() {
    init = false;
    afterInit.clear();
  }

}
