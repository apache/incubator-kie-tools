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

package org.jboss.errai.common.client.api.tasks;


import org.jboss.errai.common.client.util.TimeUnit;

/**
 * A deferred, delayed, and periodic scheduling facility that has
 * implementations which behave the same on the client or the server.
 * <p>
 * Note that on the client, timer behaviour is somewhat dependant on browser
 * behaviour. For example, the HTML 4 specification mandates that delays or
 * intervals less than 10ms are silently increased to 10ms; HTML 5 lowers this
 * minimum to 4ms. In addition, browsers such as Firefox and Chrome enforce a
 * minimum timeout to 1000ms for pages in inactive tabs.
 *
 * @author Mike Brock
 * @author Jonathan Fuerth <jfuerth@redhat.com>>
 * @author Christian Sadilek <csadilek@redhat.com>>
 */
public interface TaskManager {

  /**
   * Schedules the given task for immediate execution, either on the calling
   * thread or on the first available worker thread.
   *
   * @param task
   *          The task to execute.
   * @throws IllegalStateException if {@link #requestStop()} has been called on this TaskManager.
   */
  public void execute(Runnable task);

  /**
   * Schedules the given task for repeated execution at the given rate.
   * <p>
   * Efforts are made to ensure repeating tasks begin execution at fixed time
   * intervals, rather than having a fixed delay between the end of execution
   * and the beginning of the next. For example, when a task that takes about 2
   * seconds to run is scheduled for repeating execution every 10 seconds, it
   * will begin execution every 10 seconds. There will be about 8 seconds
   * between the end of one execution and the beginning of the next. However, if
   * a task that takes 20 seconds is scheduled to run every 10 seconds, it will
   * not be re-executed while it is still running. In this case (where a task
   * takes longer to execute than the specified interval), the task will be
   * rescheduled for immediate execution upon completion.
   *
   * @param unit
   *          Specifies the units that {@code interval} is interpreted in.
   * @param interval
   *          Amount of time to wait before starting each successive execution.
   * @param task
   *          The task to execute repeatedly.
   * @return A handle on the repeating task that allows it to be canceled.
   * @throws IllegalStateException if {@link #requestStop()} has been called on this TaskManager.
   */
  public AsyncTask scheduleRepeating(TimeUnit unit, int interval, Runnable task);

  /**
   * Schedules the given task for execution at a later time.
   *
   * @param unit
   *          Specifies the units that {@code interval} is interpreted in.
   * @param interval
   *          Amount of time to wait before starting each successive execution.
   * @param task
   *          The task to execute repeatedly.
   * @return A handle on the repeating task that allows it to be canceled.
   * @throws IllegalStateException if {@link #requestStop()} has been called on this TaskManager.
   */
  public AsyncTask schedule(TimeUnit unit, int interval, Runnable task);

  /**
   * Prevents this task manager from beginning execution of all pending tasks.
   * Does not terminate tasks that are currently executing.
   * <p>
   * Once this method has been called, any further method calls on this
   * TaskManager will result in an IllegalStateException.
   */
  public void requestStop();
}
