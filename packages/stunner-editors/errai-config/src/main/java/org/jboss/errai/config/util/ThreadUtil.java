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


package org.jboss.errai.config.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Mike Brock
 */
public class ThreadUtil {
  private static final ExecutorService executorService
      = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));

  public static void execute(final Runnable runnable) {
    executorService.execute(runnable);
  }

  public static <T> Future<T> submit(final Callable<T> callable) {
    return executorService.submit(callable);
  }

  public static Future<?> submit(final Runnable runnable) {
    return executorService.submit(runnable);
  }

  public static void stopExecutor() {
    executorService.shutdown();
  }

  public static class SynchronousCallableFuture<V> implements Future<V> {
    private final Callable<V> runnable;

    public SynchronousCallableFuture(final Callable<V> runnable) {
      this.runnable = runnable;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
      return false;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
      try {
        return runnable.call();
      }
      catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }

    @Override
    public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      try {
        return runnable.call();
      }
      catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }


  }

  public static class SynchronousRunnableeFuture<V> implements Future<V> {
    private final Runnable runnable;

    public SynchronousRunnableeFuture(final Runnable runnable) {
      this.runnable = runnable;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
      return false;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
      try {
        System.out.println("**RUN SYNC**");
        runnable.run();
        return null;
      }
      catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }

    @Override
    public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      try {
        runnable.run();
        return null;
      }
      catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }
  }
}
