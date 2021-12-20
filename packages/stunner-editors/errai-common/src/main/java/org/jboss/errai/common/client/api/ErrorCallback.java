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

package org.jboss.errai.common.client.api;

/**
 * Callback interface for failed delivery of specific messages for which an error handler has been provided using the
 * {@code MessageBuilder} API.
 * <p>
 * Errors can also be handled globally by subscribing a regular to the bus topic
 * DefaultErrorCallback.CLIENT_ERROR_SUBJECT.
 * 
 * @author Mike Brock
 */
public interface ErrorCallback<T> {

  /**
   * Called when an error occurs on the bus.
   * 
   * @param message
   *          The message or request for which the failure occurred.
   * @param throwable
   *          The exception thrown or null if not available
   * 
   * @return boolean indicating whether or not the default error handling should be performed.
   */
  public boolean error(T message, Throwable throwable);
}
