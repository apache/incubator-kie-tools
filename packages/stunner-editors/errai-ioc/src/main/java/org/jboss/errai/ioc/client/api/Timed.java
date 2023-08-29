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


package org.jboss.errai.ioc.client.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.errai.common.client.util.TimeUnit;

/**
 * Specifies a timed method invocation of a method on a managed bean. Timed methods are tied to the lifecycle of
 * the bean which they are declared on. Therefore, repeating timers cannot be stopped unless the host bean is
 * destroyed.
 *
 * @author Mike Brock
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timed {
  /**
   * The type of timer to run. Either repeating or absolute.
   *
   * @return the {@link TimerType}
   */
  TimerType type();

  /**
   * The time unit of the specified interval.
   *
   * @return the time unit
   */
  TimeUnit timeUnit();

  /**
   * The interval of the timer.
   *
   * @return the interval
   */
  int interval();
}
