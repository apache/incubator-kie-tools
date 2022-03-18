/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;

/**
 * This class is designed to create a randomized delay in the callback to simulate network latency.
 *
 * @author Mike Brock
 */
public class FakeGWT {
  public static Throwable trace;
  private static final Logger logger = LoggerFactory.getLogger(FakeGWT.class);

  public static void runAsync(final Class<?> fragmentName, final RunAsyncCallback callback) {
    // no use for the fragment name here.
    runAsync(callback);
  }
  
  public static void runAsync(final RunAsyncCallback callback) {
    final int delay = Random.nextInt(50) + 1;

    final Throwable _trace = new Throwable();
    new Timer() {
      @Override
      public void run() {
        trace = _trace;
        callback.onSuccess();
      }
    }.schedule(delay);

    logger.info("simulating async load with " + delay + "ms delay.");
  }
}


