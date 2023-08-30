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


package org.jboss.errai.ioc.rebind.ioc.test.harness;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.google.gwt.junit.JUnitShell;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.common.client.api.tasks.AsyncTask;
import org.jboss.errai.common.client.api.tasks.TaskManager;
import org.jboss.errai.common.client.api.tasks.TaskManagerFactory;
import org.jboss.errai.common.client.api.tasks.TaskManagerProvider;
import org.jboss.errai.common.client.util.TimeUnit;
import org.jboss.errai.ioc.client.Bootstrapper;
import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.QualifierEqualityFactory;
import org.jboss.errai.ioc.client.QualifierEqualityFactoryProvider;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class IOCSimulatedTestRunner extends ParentRunner<Runner> {
  public static boolean SIMULATED = Boolean.getBoolean("errai.ioc.debug.simulated_client");

  List<Runner> runners = new ArrayList<>();
  private Object instance;

  public IOCSimulatedTestRunner(final Class<? extends TestCase> toRun) throws Throwable {
    super(toRun);

    for (final Method method : toRun.getDeclaredMethods()) {
      if (method.getName().startsWith("test") && method.getParameterTypes().length == 0) {
        runners.add(new Runner() {
          @Override
          public Description getDescription() {
            return Description.createTestDescription(getTestClass().getJavaClass(), method.getName());
          }

          @Override
          public void run(final RunNotifier notifier) {
            final IOCClientTestCase iocClientTestCase = (IOCClientTestCase) getInstance();

            final Description description = getDescription();

            notifier.fireTestStarted(description);

            final TestResult result = new TestResult();

            try {
              if (SIMULATED) {
                System.setProperty("errai.simulatedClient", "true");
                try {
                  iocClientTestCase.gwtSetUp();
                  method.invoke(getInstance());
                }
                finally {
                  System.setProperty("errai.simulatedClient", "false");
                }
              }
              else {
                iocClientTestCase.setName(method.getName());
                JUnitShell.runTest(iocClientTestCase, result);
              }
            }
            catch (final GenerationException e) {
              notifier.fireTestFailure(new Failure(description, e));
            }
            catch (final InvocationTargetException e) {
              notifier.fireTestFailure(new Failure(description, e.getTargetException()));
              return;
            }
            catch (final Throwable e) {
              notifier.fireTestFailure(new Failure(description, e));
              return;
            }

            notifier.fireTestRunFinished(new Result());

            if (!result.wasSuccessful()) {
              Failure failure = null;

              if (result.failures().hasMoreElements()) {
                failure = new Failure(description, result.failures().nextElement().thrownException());
              }
              else if (result.errors().hasMoreElements()) {
                failure = new Failure(description, result.errors().nextElement().thrownException());
              }

              notifier.fireTestFailure(failure);
            }
            else {
              notifier.fireTestFinished(description);
            }
          }
        });
      }
    }
  }

  public Object getInstance() {
    if (instance == null) {
      try {
        instance = getTestClass().getJavaClass().newInstance();
      }
      catch (final InstantiationException e) {
        throw new RuntimeException("Could not intantiate test class: " + getTestClass().getJavaClass().getName(), e);
      }
      catch (final IllegalAccessException e) {
        throw new RuntimeException("Could not intantiate test class: " + getTestClass().getJavaClass().getName(), e);
      }
      catch (final Exception e) {
        throw new RuntimeException("could not bootstrap", e);
      }
    }
    return instance;
  }

  @Override
  protected List<Runner> getChildren() {
    return runners;
  }

  @Override
  protected Description describeChild(final Runner child) {
    return child.getDescription();
  }

  @Override
  protected void runChild(final Runner child, final RunNotifier notifier) {
    child.run(notifier);
  }

  @Override
  public void run(final RunNotifier notifier) {
    final IOCClientTestCase iocClientTestCase = (IOCClientTestCase) getInstance();

    if (SIMULATED) {
      QualifierUtil.initFromFactoryProvider(new QualifierEqualityFactoryProvider() {
        @Override
        public QualifierEqualityFactory provide() {
          return new QualifierEqualityFactory() {
            @Override
            public boolean isEqual(final Annotation a1, final Annotation a2) {
              return a1.equals(a2);
            }

            @Override
            public int hashCodeOf(final Annotation a1) {
              return a1.hashCode();
            }
          };
        }
      });

      TaskManagerFactory.setTaskManagerProvider(new TaskManagerProvider() {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        @Override
        public TaskManager get() {
          return new TaskManager() {
            @Override
            public void execute(final Runnable task) {
              service.execute(task);
            }

            @Override
            public AsyncTask scheduleRepeating(final TimeUnit unit, final int interval, final Runnable task) {
              final ScheduledFuture<?> future =
                      service.scheduleAtFixedRate(task, unit.toMillis(interval), 0,
                              java.util.concurrent.TimeUnit.MILLISECONDS);


              return new AsyncTask() {
                @Override
                public void cancel(final boolean interrupt) {
                  future.cancel(true);
                }

                @Override
                public void setExitHandler(final Runnable runnable) {
                }

                @Override
                public boolean isCancelled() {
                  return future.isCancelled();
                }

                @Override
                public boolean isFinished() {
                  return future.isDone();
                }
              };
            }

            @Override
            public AsyncTask schedule(final TimeUnit unit, final int interval, final Runnable task) {
              final ScheduledFuture<?> future =
                      service.schedule(task, unit.toMillis(interval), java.util.concurrent.TimeUnit.MILLISECONDS);

              return new AsyncTask() {
                @Override
                public void cancel(final boolean interrupt) {
                  future.cancel(true);
                }

                @Override
                public void setExitHandler(final Runnable runnable) {
                }

                @Override
                public boolean isCancelled() {
                  return future.isCancelled();
                }

                @Override
                public boolean isFinished() {
                  return future.isDone();
                }
              };
            }

            @Override
            public void requestStop() {
            }
          };
        }
      });

      if (instance instanceof IOCClientTestCase) {
        iocClientTestCase.setInitializer(new IOCClientTestCase.ContainerBootstrapper() {
          @Override
          public void bootstrap() {
            try {
              final String rootPackage = iocClientTestCase.getModulePackage();
              final Set<String> packages = new HashSet<>();
              for (final Package p : Package.getPackages()) {
                final String packageName = p.getName();
                if (packageName.startsWith(rootPackage)) {
                  packages.add(packageName);
                }
              }

              packages.add("org.jboss.errai.ioc.client.api.builtin");

              final MockIOCGenerator mockIOCGenerator = new MockIOCGenerator(packages);

              final Class<? extends Bootstrapper> cls = mockIOCGenerator.generate();
              final Bootstrapper bs = cls.newInstance();

              final long tm = System.currentTimeMillis();
              bs.bootstrapContainer();

              System.out.println("bootstrapped simulated container in " + (System.currentTimeMillis() - tm) + "ms");
            }
            catch (final GenerationException e) {
              throw e;
            }
            catch (final Exception e) {
              throw new RuntimeException("failed to run in emulated mode", e);
            }
          }
        });

        iocClientTestCase.setForcePureJava(true);
      }

      super.run(notifier);
    }
    else {
      super.run(notifier);
    }
  }
}
