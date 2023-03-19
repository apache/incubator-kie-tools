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

/**
 * A handle representing an asynchronous task that has been submitted to the message bus.
 */
public interface AsyncTask {

  /**
   * Prevents this task from being scheduled again, optionally interrupting the
   * task if it is currently running.
   *
   * @param interrupt
   *          if true, and this task is presently being executed,
   *          {@link Thread#interrupt()} will be called on the thread currently
   *          executing this task.
   */
  public void cancel(boolean interrupt);

  /**
   * Sets the task that should be run after all executions of this task have
   * completed. The supplied runnable is guaranteed to be invoked exactly once,
   * even if this task has already completed or it has been cancelled.
   *
   * @param runnable
   *          the logic to execute when this task has completed all of its
   *          executions, either because it completed normally, by throwing an
   *          exception, or because it was cancelled by a call to
   *          {@link #cancel(boolean)}.
   */
  public void setExitHandler(Runnable runnable);

  /**
   * Returns true if {@link #cancel(boolean)} has been called on this task,
   * whether it was called from user code or from within the framework because
   * the task threw an exception.
   *
   * @return true if this task has been cancelled; false otherwise.
   */
  public boolean isCancelled();

  /**
   * Returns true if this task is no longer scheduled to run, either because it executed
   * (successfully or with an error) or {@link #cancel(boolean)} was called.
   *
   * @return true if this task is no longer scheduled to run.
   */
  public boolean isFinished();
}
