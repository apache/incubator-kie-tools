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
 * Errai schedulers that accept a Runnable and produce an {@link AsyncTask} will
 * inject the AsyncTask instance into the runnable if it implements this
 * interface.
 *
 * @see org.jboss.errai.common.client.api.tasks.ClientTaskManager
 */
public interface HasAsyncTaskRef extends Runnable {

  /**
   * Called by Errai scheduler services when they receive this Runnable and
   * before its run() method is called.
   *
   * @param task
   *          The companion AsyncTask instance that has been created for this
   *          runnable
   */
  public void setAsyncTask(AsyncTask task);

  /**
   * Returns the AsyncTask instance most recently set in
   * {@link #setAsyncTask(AsyncTask)}, or null if {@code setAsyncTask()} has not
   * been called yet.
   */
  public AsyncTask getAsyncTask();
}
