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

package org.jboss.errai.common.client.api.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.tasks.AsyncTask;
import org.jboss.errai.common.client.api.tasks.TaskManagerFactory;
import org.jboss.errai.common.client.util.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;

/**
 * <p>
 * The <tt>InitVotes</tt> class provides the central algorithm around which disparate services within the Errai
 * Framework can elect to prevent initialization and be notified when initialization occurs. This is required internally
 * to ensure that services such as RPC proxies have been properly bound prior to any remote calls being made. This API
 * also makes it possible for user-defined services and extensions to Errai to participate in the startup contract.
 *
 * <p>
 * Initialization fails if there are any services still waiting after the timeout duration has elapsed. By default the
 * timeout is 90 seconds for Development Mode and 45 seconds for production mode, but it can be adjusted by setting the
 * Javascript variable <code>erraiInitTimeout</code> in the GWT Host Page.
 *
 * @author Mike Brock
 */
public final class InitVotes {
  private InitVotes() {}

  private static final List<Runnable> preInitCallbacks = new ArrayList<>();
  private static final Map<String, List<Runnable>> dependencyCallbacks = new HashMap<>();
  private static final List<Runnable> initCallbacks = new ArrayList<>();
  private static final List<InitFailureListener> initFailureListeners = new ArrayList<>();

  private static boolean armed = false;
  private static boolean init = false;
  private static final Set<String> waitForSet = new HashSet<>();

  // a list of both strings and runnable references that are marked done.
  private static final Set<Object> completedSet = new HashSet<>();

  private static int timeoutMillis = !GWT.isProdMode() ? 90000 : 45000;

  private static volatile AsyncTask initTimeout;
  private static volatile AsyncTask initDelay;

  private static boolean _initWait = false;

  private static final Object lock = new Object();

  private static final Logger logger = LoggerFactory.getLogger(InitVotes.class);

  /**
   * Resets the state, clearing all current waiting votes and disarming the startup process. Calling
   * <tt>reset()</tt> does not however clear out any initialization callbacks registered with
   * {@link #registerPersistentInitCallback(Runnable)}.
   */
  public static void reset() {
    synchronized (lock) {
      logger.info("init polling system reset ...");

      idempotentStopFailTimer();
      idempotentStopDelayTimer();
      _clearOneTimeRunnables(preInitCallbacks);
      _clearOneTimeRunnables(initCallbacks);
      for (final Map.Entry<String, List<Runnable>> entry : dependencyCallbacks.entrySet()) {
        _clearOneTimeRunnables(entry.getValue());
      }
      waitForSet.clear();
      completedSet.clear();
      armed = false;
      init = false;
    }
  }

  private static native int getConfiguredTimeoutOrElse(final int fallback) /*-{
    var configuredValue = $wnd.erraiInitTimeout;
    return (configuredValue == undefined || configuredValue <= 0) ?
              fallback :
              configuredValue;
  }-*/;

  /**
   * Specifies the number of milliseconds that will be permitted to transpire until dependencies are
   * assumed to have failed to satisfy, and thus an error is rendered to the browser console.
   *
   * @param millis
   *          milliseconds.
   */
  public static void setTimeoutMillis(final int millis) {
    timeoutMillis = millis;
  };

  /**
   * Declares a startup dependency on the specified class. By doing so, initialization of the
   * framework services will be blocked until a {@link #voteFor(Class)} is called with the same
   * <tt>Class</tt> reference passed to this method.
   * <p/>
   * If no dependencies have previously been declared, then the first caller to invoke this method
   * arms and begins the startup process. This starts the timer window (see
   * {@link #setTimeoutMillis(int)}) for which all components being waited on are expected to report
   * back that they're ready.
   *
   * @param clazz
   *          a class reference.
   *
   * @see #voteFor(Class)
   */
  public static void waitFor(final Class<?> clazz) {
    waitFor(clazz.getName());
  }

  private static void waitFor(final String topic) {
    synchronized (lock) {
      if (completedSet.contains(topic)) {
        // throw new RuntimeException("cannot declare a wait on '" + topic +
        // "' as it is already marked completed!");
        return;
      }

      if (waitForSet.contains(topic))
        return;

      logger.info("wait for: " + topic);

      if (!armed && waitForSet.isEmpty()) {
        beginInit();
      }

      waitForSet.add(topic);
    }
  }

  public static boolean isInitialized() {
    return init;
  }

  /**
   * Votes for initialization and removes a lock on the initialization of framework services. If the
   * initialization process has been armed and this vote releases the final dependency, the
   * initialization process will be triggered, calling all the registered initialization callbacks.
   * See: {@link #registerPersistentInitCallback(Runnable)}
   *
   * @param clazz
   *          a class reference
   */
  public static void voteFor(final Class<?> clazz) {
    voteFor(clazz.getName());
  }

  private static void voteFor(final String topic) {
    synchronized (lock) {
      if (waitForSet.remove(topic)) {
        logger.info("vote for: " + topic);

        completedSet.add(topic);
      }

      _runAllRunnables(dependencyCallbacks.get(topic));

      if (!waitForSet.isEmpty())
        logger.info("  still waiting for -> " + waitForSet);

      if (armed && waitForSet.isEmpty()) {
        scheduleFinish();
      }
    }
  }

  private static void scheduleFinish() {
    if (_initWait)
      return;

    logger.debug("Scheduling finish.");
    _initWait = true;

    _scheduleFinish(new Runnable() {
      @Override
      public void run() {
        logger.debug("Running finish timer...");
        if (armed && waitForSet.isEmpty()) {
          idempotentStopFailTimer();
          finishInit();
          _initWait = false;
        }
        else {
          logger.debug("Rescheduling finish.");
          _scheduleFinish(this);
        }
      }
    });
  }

  private static void _scheduleFinish(final Runnable runnable) {
    initDelay = TaskManagerFactory.get().schedule(TimeUnit.MILLISECONDS, 250, runnable);
  }

  public static void registerPersistentDependencyCallback(final Class clazz, final Runnable runnable) {
    _registerDependencyCallback(clazz.getName(), runnable);
  }

  public static void registerOneTimeDependencyCallback(final Class clazz, final Runnable runnable) {
    registerPersistentDependencyCallback(clazz, new OneTimeRunnable(runnable));
  }

  private static void _registerDependencyCallback(final String topic, final Runnable runnable) {
    synchronized (lock) {
      List<Runnable> callbacks = dependencyCallbacks.get(topic);
      if (callbacks == null) {
        dependencyCallbacks.put(topic, callbacks = new ArrayList<>());
      }
      if (!callbacks.contains(runnable)) {
        callbacks.add(runnable);
      }

      if (completedSet.contains(topic) && !completedSet.contains(runnable)) {
        runnable.run();
      }
    }
  }

  public static void registerPersistentPreInitCallback(final Runnable runnable) {
    synchronized (lock) {
      if (!preInitCallbacks.contains(runnable)) {
        preInitCallbacks.add(runnable);

        if (armed) {
          runnable.run();
        }
      }
    }
  }

  public static void registerOneTimePreInitCallback(final Runnable runnable) {
    registerPersistentPreInitCallback(new OneTimeRunnable(runnable));
  }

  /**
   * Registers a callback task to be executed once initialization occurs. Callbacks registered with
   * this method will be persistent <em>across</em> multiple initializations, and will not be
   * cleared out even if {@link #reset()} is called. If this is not desirable, see:
   * {@link #registerOneTimeInitCallback};
   * <p>
   * As of Errai 3.0, the callback list is de-duped based on instance to simplify initialization
   * code in modules. You can now safely re-add a Runnable in initialization code as long as it is
   * always guaranteed to be the same instance.*
   *
   * @param runnable
   *          a callback to execute
   */
  public static void registerPersistentInitCallback(final Runnable runnable) {
    synchronized (lock) {
      if (!initCallbacks.contains(runnable)) {
        initCallbacks.add(runnable);
      }
      if (init) {
        _runAllRunnables(Arrays.asList(runnable), initCallbacks);
      }
    }
  }

  /**
   * Registers a one-time callback task to be executed once initialization occurs. Unlike callbacks
   * registered with {@link #registerPersistentInitCallback(Runnable)} Callback(Runnable)},
   * callbacks registered with this method will only be executed once and will never be used again
   * if framework services are re-initialized.
   *
   * @param runnable
   *          a callback to execute
   */
  public static void registerOneTimeInitCallback(final Runnable runnable) {
    registerPersistentInitCallback(new OneTimeRunnable(runnable));
  }

  /**
   * Registers an {@link InitFailureListener} to monitor for initialization failures of the
   * framework or its components.
   *
   * @param failureListener
   *          the instance of the {@link InitFailureListener} to be registered.
   */
  public static void registerInitFailureListener(final InitFailureListener failureListener) {
    initFailureListeners.add(failureListener);
  }

  public static void startInitPolling() {
    if (armed) {
      logger.warn("did not start polling. already armed.");
      return;
    }
    logger.info("Starting init polling.");
    timeoutMillis = getConfiguredTimeoutOrElse(timeoutMillis);
    beginInit();
  }

  private static void beginInit() {
    synchronized (lock) {
      if (armed) {
        throw new RuntimeException("attempt to arm voting process more than once.");
      }

      armed = true;
      _initWait = false;
      idempotentStopFailTimer();

      initTimeout = TaskManagerFactory.get().schedule(TimeUnit.MILLISECONDS, timeoutMillis, new Runnable() {
        @Override
        public void run() {
          synchronized (lock) {
            if (waitForSet.isEmpty() || !armed)
              return;

            idempotentStopDelayTimer();

            final Set<String> failedTopics = Collections.unmodifiableSet(new HashSet<>(waitForSet));
            _fireFailedInit(failedTopics);

            logger.error("components failed to initialize");
            for (final String comp : waitForSet) {
              logger.error("   [failed] -> " + comp);
            }
          }
        }
      });

      _runAllRunnables(preInitCallbacks);
    }
  }

  private static void _fireFailedInit(final Set<String> failedTopics) {
    for (final InitFailureListener initFailureListener : initFailureListeners) {
      initFailureListener.onInitFailure(failedTopics);
    }
  }

  private static void finishInit() {
    synchronized (lock) {
      logger.debug("Finishing initialization...");
      armed = false;
      init = true;
      idempotentStopFailTimer();

      _runAllRunnables(initCallbacks);
    }
  }

  private static void idempotentStopFailTimer() {
    synchronized (lock) {
      if (initTimeout != null && !initTimeout.isFinished()) {
        initTimeout.cancel(true);
      }
    }
  }

  private static void idempotentStopDelayTimer() {
    synchronized (lock) {
      if (initDelay != null && !initDelay.isFinished()) {
        initDelay.cancel(true);
      }
    }
  }

  private static void _runAllRunnables(final List<Runnable> runnables) {
    if (runnables == null || runnables.isEmpty())
      return;
    _runAllRunnables(new ArrayList<>(runnables), runnables);

  }

  private static void _runAllRunnables(final List<Runnable> curRunnables, final List<Runnable> allRunnables) {
    for (final Runnable runnable : curRunnables) {
      if (completedSet.contains(runnable)) {
        continue;
      }
      completedSet.add(runnable);

      if (runnable instanceof OneTimeRunnable) {
        allRunnables.remove(runnable);
      }
      final int expectedSize = allRunnables.size();
      runnable.run();
      if (expectedSize < allRunnables.size()) {
        // this runnable added more runnables that need to be executed
        final List<Runnable> moreRunnables = new ArrayList<>(allRunnables).subList(expectedSize, allRunnables.size());
        _runAllRunnables(moreRunnables, allRunnables);
      }
    }
  }

  private static void _clearOneTimeRunnables(final List<Runnable> runnables) {
    final Iterator<Runnable> runnableIterable = runnables.iterator();
    while (runnableIterable.hasNext()) {
      final Runnable runnable = runnableIterable.next();
      if (runnable instanceof OneTimeRunnable) {
        runnableIterable.remove();
      }
    }
  }

  private static class OneTimeRunnable implements Runnable {
    private final Runnable delegate;

    private OneTimeRunnable(final Runnable delegate) {
      this.delegate = delegate;
    }

    @Override
    public void run() {
      delegate.run();
    }
  }
}
