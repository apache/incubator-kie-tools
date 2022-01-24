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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Timer;

/**
 * The client-side implementation of {@link TaskManager}.
 */
public class ClientTaskManager implements TaskManager {
  @Override
  public void execute(final Runnable task) {
    GWT.runAsync(new RunAsyncCallback() {
      @Override
      public void onFailure(final Throwable reason) {
        GWT.log("failed async execution", reason);
      }

      @Override
      public void onSuccess() {
        task.run();
      }
    });
  }

  @Override
  public AsyncTask scheduleRepeating(final TimeUnit unit, final int interval, final Runnable userTask) {
    final TaskManagerTimer timer = new TaskManagerTimer(userTask);
    timer.scheduleRepeating((int) unit.toMillis(interval));
    return timer.asyncTask;
  }

  @Override
  public AsyncTask schedule(final TimeUnit unit, final int interval, final Runnable userTask) {
    final TaskManagerTimer timer = new TaskManagerTimer(userTask);
    timer.schedule((int) unit.toMillis(interval));
    return timer.asyncTask;
  }

  /**
   * An AsyncTask implementation that is meant to be created and used by the
   * {@link TaskManagerTimer}.
   *
   * @author Jonathan Fuerth <jfuerth@gmail.com>
   */
  private static class ClientAsyncTask implements Runnable, AsyncTask {
    private final Runnable task;
    private final TaskManagerTimer timer;
    private Runnable exitHandler;

    /**
     * True if and only if the task will never run again.
     */
    private boolean finished;
    private boolean isCancelled;

    /**
     * Don't call this. Use {@link #create(Runnable, org.jboss.errai.common.client.api.tasks.ClientTaskManager.TaskManagerTimer)}.
     *
     * @param task
     * @param timer
     */
    private ClientAsyncTask(final Runnable task, final TaskManagerTimer timer) {
      this.task = task;
      this.timer = timer;
    }

    @Override
    public void run() {
      try {
        task.run();
      } catch (Throwable t) {
        GWT.log("Async Task Execution Failed. Future executions (if any) are cancelled.", t);
        timer.cancel();
      }
    }

    /**
     * Creates a new async task for the client, injecting the reference into
     * {@code task} if it is an instance of {@link HasAsyncTaskRef}.
     *
     * @param task The code to execute. Not null.
     * @param timer The timer that will execute class. Not null.
     * @return A new AsyncTask that relates to {@code task}.
     */
    public static ClientAsyncTask create(final Runnable task, final TaskManagerTimer timer) {
      final ClientAsyncTask t = new ClientAsyncTask(task, timer);
      if (task instanceof HasAsyncTaskRef) {
        ((HasAsyncTaskRef) task).setAsyncTask(t);
      }
      return t;
    }

    @Override
    public void cancel(final boolean interrupt) {
      timer.cancel();
      finishUp();
    }

    @Override
    public void setExitHandler(final Runnable runnable) {
      if (exitHandler != null) {
        throw new IllegalStateException("Exit handler is already set to " + exitHandler);
      }
      this.exitHandler = runnable;
      if (isFinished()) {
        exitHandler.run();
      }
    }

    @Override
    public boolean isCancelled() {
      return timer.isCancelled();
    }

    @Override
    public boolean isFinished() {
      return finished;
    }

    public void finishUp() {
      if (finished) {
        throw new IllegalStateException("Already finished");
      }
      finished = true;
      if (exitHandler != null) {
        exitHandler.run();
      }
    }
  }

  private enum SchedulingMode { ONE_TIME, REPEATING }

  /**
   * A GWT Timer implementation that has a public flag indicating if
   * {@link #cancel()})} has been called.
   * <p>
   * This Timer class only allows a single call to either {@link #schedule(int)}
   * or {@link #scheduleRepeating(int)}. Further attempts to schedule the call
   * will result in {@link IllegalStateException}.
   *
   * @author Jonathan Fuerth <jfuerth@gmail.com>
   */
  private final class TaskManagerTimer extends Timer {

    private SchedulingMode mode;
    private final ClientAsyncTask asyncTask;

    TaskManagerTimer(final Runnable userTask) {
      asyncTask = ClientAsyncTask.create(userTask, this);
    }

    @Override
    public void schedule(final int delayMillis) {
      if (mode != null) {
        throw new IllegalStateException("This timer has already been scheduled.");
      }
      mode = SchedulingMode.ONE_TIME;
      super.schedule(delayMillis);
    }

    @Override
    public void scheduleRepeating(final int periodMillis) {
      if (mode != null) {
        throw new IllegalStateException("This timer has already been scheduled.");
      }
      mode = SchedulingMode.REPEATING;
      super.scheduleRepeating(periodMillis);
    }

    @Override
    public void run() {
      asyncTask.run();
      if (mode == SchedulingMode.ONE_TIME) {
        asyncTask.finishUp();
      }
    }

    @Override
    public void cancel() {
      super.cancel();
      asyncTask.isCancelled = true;
    }

    public boolean isCancelled() {
      return asyncTask.isCancelled;
    }
  }

  @Override
  public void requestStop() {
  }
}
